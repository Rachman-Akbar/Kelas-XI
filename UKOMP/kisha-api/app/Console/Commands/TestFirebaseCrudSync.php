<?php

namespace App\Console\Commands;

use App\Models\User;
use App\Modules\Product\Models\Product;
use App\Modules\Product\Models\ProductCategory;
use App\Modules\Village\Models\Village;
use Google\Auth\Credentials\ServiceAccountCredentials;
use GuzzleHttp\Client;
use GuzzleHttp\Exception\ClientException;
use Illuminate\Console\Command;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;

class TestFirebaseCrudSync extends Command
{
    protected $signature = 'firebase:test-crud-sync {--keep : Keep the tested product instead of deleting it} {--seed=0 : Additional products to seed and keep after CRUD test}';
    protected $description = 'Test MySQL <-> Firestore CRUD synchronization for Product data';

    public function handle(): int
    {
        config(['firebase.enabled' => true]);

        $projectId = trim((string) config('firebase.project_id', ''), '"');
        $credentialsPath = $this->credentialsPath();
        $collection = (string) config('firebase.firestore.products_collection', 'products');

        if ($projectId === '') {
            $this->error('FIREBASE_PROJECT_ID is missing.');
            return self::FAILURE;
        }

        if (! $credentialsPath || ! is_file($credentialsPath)) {
            $this->error('Firebase credentials file not found at: ' . ($credentialsPath ?? 'null'));
            return self::FAILURE;
        }

        $client = new Client([
            'base_uri' => 'https://firestore.googleapis.com/v1/',
            'timeout' => 20,
        ]);

        $token = $this->accessToken($credentialsPath);
        if (! $token) {
            $this->error('Failed to generate Firebase access token.');
            return self::FAILURE;
        }

        $this->info('Preparing relational data in MySQL...');

        $village = Village::query()->firstOrCreate(
            ['name' => 'Sync Test Village'],
            [
                'district' => 'Test District',
                'city' => 'Test City',
                'province' => 'Test Province',
                'is_active' => true,
            ]
        );

        $category = ProductCategory::query()->firstOrCreate(
            ['name' => 'Sync Test Category', 'category_type' => 'goods'],
            ['is_active' => true]
        );

        $seller = User::query()->firstOrCreate(
            ['email' => 'sync-tester@example.com'],
            [
                'name' => 'Sync Tester',
                'password' => Hash::make('sync-test-password'),
                'role' => 'seller',
                'village_id' => $village->id,
                'is_active' => true,
            ]
        );

        if ((int) $seller->village_id !== (int) $village->id) {
            $seller->village_id = $village->id;
            $seller->role = 'seller';
            $seller->is_active = true;
            $seller->save();
        }

        $this->info('Step 1/3: CREATE product in MySQL...');

        $suffix = Str::lower(Str::random(6));
        $product = Product::query()->create([
            'seller_id' => $seller->id,
            'village_id' => $village->id,
            'category_id' => $category->id,
            'name' => 'Sync Test Product ' . $suffix,
            'slug' => 'sync-test-product-' . $suffix,
            'description' => 'Created by firebase:test-crud-sync',
            'price' => 12500,
            'shipping_cost' => 2500,
            'stock' => 11,
            'unit' => 'pcs',
            'status' => 'active',
        ]);

        $createdDoc = $this->getFirestoreDocument($client, $token, $projectId, $collection, (string) $product->id);
        if (! $createdDoc) {
            $this->error('CREATE sync failed: Firestore document not found after MySQL insert.');
            return self::FAILURE;
        }

        $this->line('CREATE sync OK: product_id=' . $product->id);

        $this->info('Step 2/3: UPDATE product in MySQL...');

        $product->name = $product->name . ' Updated';
        $product->price = 19999;
        $product->stock = 7;
        $product->save();

        $updatedDoc = $this->getFirestoreDocument($client, $token, $projectId, $collection, (string) $product->id);
        if (! $updatedDoc) {
            $this->error('UPDATE sync failed: Firestore document missing after MySQL update.');
            return self::FAILURE;
        }

        $nameFromFirestore = $this->extractStringField($updatedDoc, 'name');
        if ($nameFromFirestore !== $product->name) {
            $this->error('UPDATE sync failed: Firestore name does not match MySQL.');
            return self::FAILURE;
        }

        $this->line('UPDATE sync OK: name=' . $nameFromFirestore);

        if ($this->option('keep')) {
            $this->warn('DELETE step skipped because --keep is enabled.');
        } else {
            $this->info('Step 3/3: DELETE product in MySQL...');

            $productId = (string) $product->id;
            $product->delete();

            $deletedDoc = $this->getFirestoreDocument($client, $token, $projectId, $collection, $productId);
            if ($deletedDoc) {
                $this->error('DELETE sync failed: Firestore document still exists after MySQL delete.');
                return self::FAILURE;
            }

            $this->line('DELETE sync OK: product_id=' . $productId);
        }

        $seedCount = max(0, (int) $this->option('seed'));
        if ($seedCount > 0) {
            $this->info("Seeding {$seedCount} additional products...");

            for ($index = 1; $index <= $seedCount; $index++) {
                $seedSuffix = Str::lower(Str::random(6));
                $seedProduct = Product::query()->create([
                    'seller_id' => $seller->id,
                    'village_id' => $village->id,
                    'category_id' => $category->id,
                    'name' => 'Seed Product ' . $index . ' ' . $seedSuffix,
                    'slug' => 'seed-product-' . $index . '-' . $seedSuffix,
                    'description' => 'Seeded by firebase:test-crud-sync',
                    'price' => 10000 + ($index * 1000),
                    'shipping_cost' => 2500,
                    'stock' => 10 + $index,
                    'unit' => 'pcs',
                    'status' => 'active',
                ]);

                $syncedDoc = $this->getFirestoreDocument($client, $token, $projectId, $collection, (string) $seedProduct->id);
                if (! $syncedDoc) {
                    $this->warn('Seed sync warning: Firestore doc not found for product_id=' . $seedProduct->id);
                }
            }

            $this->line('SEED OK: created ' . $seedCount . ' additional product records.');
        }

        $this->newLine();
        $this->info('SUCCESS: CRUD sync MySQL <-> Firestore is working.');

        return self::SUCCESS;
    }

    private function accessToken(string $credentialsPath): ?string
    {
        try {
            $credentials = new ServiceAccountCredentials(
                ['https://www.googleapis.com/auth/datastore'],
                $credentialsPath
            );
            $token = $credentials->fetchAuthToken();

            return $token['access_token'] ?? null;
        } catch (\Throwable) {
            return null;
        }
    }

    private function credentialsPath(): ?string
    {
        $configured = trim((string) config('firebase.credentials', ''), '"');
        if ($configured === '') {
            return null;
        }

        if (preg_match('/^[A-Za-z]:\\\\|^\\\\\\\\|^\//', $configured)) {
            return $configured;
        }

        return base_path($configured);
    }

    private function getFirestoreDocument(Client $client, string $token, string $projectId, string $collection, string $id): ?array
    {
        try {
            $response = $client->request('GET', sprintf(
                'projects/%s/databases/(default)/documents/%s/%s',
                $projectId,
                $collection,
                $id
            ), [
                'headers' => [
                    'Authorization' => 'Bearer ' . $token,
                ],
            ]);

            $json = json_decode((string) $response->getBody(), true);

            return is_array($json) ? $json : null;
        } catch (ClientException $exception) {
            if ($exception->getCode() === 404) {
                return null;
            }

            return null;
        }
    }

    private function extractStringField(array $document, string $field): ?string
    {
        $value = $document['fields'][$field] ?? null;

        if (! is_array($value)) {
            return null;
        }

        if (isset($value['stringValue'])) {
            return (string) $value['stringValue'];
        }

        if (isset($value['integerValue'])) {
            return (string) $value['integerValue'];
        }

        if (isset($value['doubleValue'])) {
            return (string) $value['doubleValue'];
        }

        return null;
    }
}
