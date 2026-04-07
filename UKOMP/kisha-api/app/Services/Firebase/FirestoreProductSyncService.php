<?php

namespace App\Services\Firebase;

use App\Modules\Product\Models\Product;
use Google\Auth\Credentials\ServiceAccountCredentials;
use GuzzleHttp\Client;
use Illuminate\Support\Facades\Log;
use Throwable;

class FirestoreProductSyncService
{
    private ?Client $http = null;

    public function syncProduct(Product $product): void
    {
        if (! $this->isEnabled()) {
            return;
        }

        $product->loadMissing(['seller', 'village', 'category', 'images']);

        try {
            $token = $this->accessToken();

            if (! $token) {
                return;
            }

            $this->http()->request('PATCH', $this->documentUrl((string) $product->id), [
                'headers' => [
                    'Authorization' => 'Bearer ' . $token,
                    'Content-Type' => 'application/json',
                ],
                'json' => [
                    'fields' => $this->toFirestoreMap($this->payloadFor($product)),
                ],
            ]);
        } catch (Throwable $exception) {
            Log::error('Failed to sync product to Firestore.', [
                'product_id' => $product->id,
                'message' => $exception->getMessage(),
            ]);
        }
    }

    public function deleteProduct(int|string $productId): void
    {
        if (! $this->isEnabled()) {
            return;
        }

        try {
            $token = $this->accessToken();

            if (! $token) {
                return;
            }

            $this->http()->request('DELETE', $this->documentUrl((string) $productId), [
                'headers' => [
                    'Authorization' => 'Bearer ' . $token,
                ],
            ]);
        } catch (Throwable $exception) {
            Log::error('Failed to delete product from Firestore.', [
                'product_id' => $productId,
                'message' => $exception->getMessage(),
            ]);
        }
    }

    public function testConnection(): array
    {
        if (! $this->isEnabled()) {
            return ['ok' => false, 'message' => 'Firebase is disabled.'];
        }

        try {
            $token = $this->accessToken();

            if (! $token) {
                return ['ok' => false, 'message' => 'Unable to generate access token.'];
            }

            $response = $this->http()->request('GET', $this->collectionUrl(), [
                'headers' => [
                    'Authorization' => 'Bearer ' . $token,
                ],
                'query' => [
                    'pageSize' => 1,
                ],
            ]);

            if ($response->getStatusCode() >= 200 && $response->getStatusCode() < 300) {
                return ['ok' => true, 'message' => 'Firestore REST connection successful.'];
            }

            return ['ok' => false, 'message' => 'Unexpected status code: ' . $response->getStatusCode()];
        } catch (Throwable $exception) {
            return ['ok' => false, 'message' => $exception->getMessage()];
        }
    }

    private function accessToken(): ?string
    {
        $credentialsPath = $this->credentialsPath();

        if (! $credentialsPath || ! is_file($credentialsPath)) {
            Log::error('Firebase credentials file not found.', ['path' => $credentialsPath]);
            return null;
        }

        try {
            $credentials = new ServiceAccountCredentials(
                ['https://www.googleapis.com/auth/datastore'],
                $credentialsPath
            );

            $token = $credentials->fetchAuthToken();

            return $token['access_token'] ?? null;
        } catch (Throwable $exception) {
            Log::error('Failed to fetch Firebase access token.', [
                'message' => $exception->getMessage(),
            ]);

            return null;
        }
    }

    private function http(): Client
    {
        if ($this->http) {
            return $this->http;
        }

        $this->http = new Client([
            'base_uri' => 'https://firestore.googleapis.com/v1/',
            'timeout' => 15,
        ]);

        return $this->http;
    }

    private function collectionUrl(): string
    {
        return sprintf(
            'projects/%s/databases/(default)/documents/%s',
            $this->projectId(),
            $this->productsCollection()
        );
    }

    private function documentUrl(string $productId): string
    {
        return sprintf(
            'projects/%s/databases/(default)/documents/%s/%s',
            $this->projectId(),
            $this->productsCollection(),
            $productId
        );
    }

    private function projectId(): string
    {
        $configured = (string) config('firebase.project_id', '');
        if ($configured !== '') {
            return trim($configured, '"');
        }

        $credentialsPath = $this->credentialsPath();
        if (! $credentialsPath || ! is_file($credentialsPath)) {
            return '';
        }

        $json = json_decode((string) file_get_contents($credentialsPath), true);

        return (string) ($json['project_id'] ?? '');
    }

    private function credentialsPath(): ?string
    {
        $configured = (string) config('firebase.credentials', '');

        if ($configured === '') {
            return null;
        }

        $path = trim($configured, '"');
        if (preg_match('/^[A-Za-z]:\\\\|^\\\\\\\\|^\//', $path)) {
            return $path;
        }

        return base_path($path);
    }

    private function toFirestoreMap(array $data): array
    {
        $fields = [];

        foreach ($data as $key => $value) {
            $fields[(string) $key] = $this->toFirestoreValue($value);
        }

        return $fields;
    }

    private function toFirestoreValue(mixed $value): array
    {
        if (is_null($value)) {
            return ['nullValue' => null];
        }

        if (is_bool($value)) {
            return ['booleanValue' => $value];
        }

        if (is_int($value)) {
            return ['integerValue' => (string) $value];
        }

        if (is_float($value)) {
            return ['doubleValue' => $value];
        }

        if (is_array($value)) {
            if (array_is_list($value)) {
                return [
                    'arrayValue' => [
                        'values' => array_map(fn ($item) => $this->toFirestoreValue($item), $value),
                    ],
                ];
            }

            return [
                'mapValue' => [
                    'fields' => $this->toFirestoreMap($value),
                ],
            ];
        }

        return ['stringValue' => (string) $value];
    }

    private function payloadFor(Product $product): array
    {
        $images = $product->images->map(fn ($image): array => [
            'id' => $image->id,
            'image_path' => $image->image_path,
            'image_url' => $this->publicUrl($image->image_path),
            'is_primary' => (bool) $image->is_primary,
        ])->values()->all();

        return [
            'id' => $product->id,
            'name' => $product->name,
            'slug' => $product->slug,
            'description' => $product->description,
            'price' => (string) $product->price,
            'shipping_cost' => (string) $product->shipping_cost,
            'stock' => (int) $product->stock,
            'unit' => $product->unit,
            'status' => $product->status,
            'rejection_reason' => $product->rejection_reason,
            'main_image_path' => $product->main_image_path,
            'main_image_url' => $this->publicUrl($product->main_image_path),
            'seller' => $product->seller ? [
                'id' => $product->seller->id,
                'name' => $product->seller->name,
                'email' => $product->seller->email,
            ] : null,
            'village' => $product->village ? [
                'id' => $product->village->id,
                'name' => $product->village->name,
            ] : null,
            'category' => $product->category ? [
                'id' => $product->category->id,
                'name' => $product->category->name,
                'category_type' => $product->category->category_type,
            ] : null,
            'images' => $images,
            'created_at' => optional($product->created_at)->toIso8601String(),
            'updated_at' => optional($product->updated_at)->toIso8601String(),
            'synced_at' => now()->toIso8601String(),
            'source' => 'mysql',
        ];
    }

    private function publicUrl(?string $path): ?string
    {
        if (! $path) {
            return null;
        }

        return asset('storage/' . ltrim($path, '/'));
    }

    private function productsCollection(): string
    {
        return (string) config('firebase.firestore.products_collection', 'products');
    }

    private function isEnabled(): bool
    {
        return (bool) config('firebase.enabled', false);
    }
}
