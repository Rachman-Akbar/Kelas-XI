<?php

namespace App\Modules\Product\Services;

use App\Models\User;
use App\Modules\Product\Models\Product;
use App\Modules\Product\Models\ProductImage;
use Illuminate\Contracts\Pagination\LengthAwarePaginator;
use Illuminate\Http\UploadedFile;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;

class ProductService
{
    public function paginateForUser(User $user, array $filters = []): LengthAwarePaginator
    {
        $query = Product::query()->with(['seller', 'village', 'category', 'images']);

        if ($user->role === 'admin') {
            return $query->latest()->paginate($filters['per_page'] ?? 15);
        }

        if (! $user->village_id) {
            return $query->whereRaw('1 = 0')->paginate($filters['per_page'] ?? 15);
        }

        $query->where('village_id', $user->village_id);

        if ($user->role !== 'seller') {
            $query->where('status', 'active');
        }

        if ($keyword = $filters['search'] ?? null) {
            $query->where(function ($subQuery) use ($keyword): void {
                $subQuery->where('name', 'like', "%{$keyword}%")
                    ->orWhere('description', 'like', "%{$keyword}%");
            });
        }

        if ($categoryId = $filters['category_id'] ?? null) {
            $query->where('category_id', $categoryId);
        }

        return $query->latest()->paginate($filters['per_page'] ?? 15);
    }

    public function getVisibleProduct(User $user, Product $product): Product
    {
        if ($user->role !== 'admin') {
            $sameVillage = $user->village_id && $product->village_id === $user->village_id;
            $ownedByUser = $product->seller_id === $user->id;

            if (! $sameVillage || ($user->role === 'buyer' && $product->status !== 'active')) {
                throw ValidationException::withMessages([
                    'product' => ['Product is not available for your village.'],
                ]);
            }

            if ($user->role === 'seller' && ! $sameVillage && ! $ownedByUser) {
                throw ValidationException::withMessages([
                    'product' => ['Product is not accessible.'],
                ]);
            }
        }

        return $product->load(['seller', 'village', 'category', 'images']);
    }

    public function create(User $user, array $payload, ?UploadedFile $image = null): Product
    {
        $villageId = $payload['village_id'] ?? $user->village_id;

        if (! $villageId) {
            throw ValidationException::withMessages([
                'village_id' => ['Village is required for this product.'],
            ]);
        }

        if ($user->role !== 'admin' && (int) $villageId !== (int) $user->village_id) {
            throw ValidationException::withMessages([
                'village_id' => ['Product must belong to your own village.'],
            ]);
        }

        $slug = $this->generateUniqueSlug($payload['name']);

        $product = Product::query()->create([
            'seller_id' => $user->id,
            'village_id' => $villageId,
            'category_id' => $payload['category_id'],
            'name' => $payload['name'],
            'slug' => $slug,
            'description' => $payload['description'],
            'price' => $payload['price'],
            'shipping_cost' => $payload['shipping_cost'] ?? 0,
            'stock' => $payload['stock'] ?? 0,
            'unit' => $payload['unit'] ?? null,
            'main_image_path' => null,
            'status' => $payload['status'] ?? 'draft',
            'rejection_reason' => null,
        ]);

        if ($image) {
            $this->storeImage($product, $image, true);
        }

        return $product->load(['seller', 'village', 'category', 'images']);
    }

    public function update(User $user, Product $product, array $payload, ?UploadedFile $image = null): Product
    {
        $this->ensureOwnership($user, $product);

        if (array_key_exists('name', $payload) && $payload['name']) {
            $product->slug = $this->generateUniqueSlug($payload['name'], $product->id);
        }

        $product->fill(array_filter([
            'category_id' => $payload['category_id'] ?? null,
            'name' => $payload['name'] ?? null,
            'description' => $payload['description'] ?? null,
            'price' => $payload['price'] ?? null,
            'shipping_cost' => $payload['shipping_cost'] ?? null,
            'stock' => $payload['stock'] ?? null,
            'unit' => $payload['unit'] ?? null,
            'status' => $payload['status'] ?? null,
        ], static fn($value) => ! is_null($value)));

        $product->save();

        if ($image) {
            $this->storeImage($product, $image, false);
        }

        return $product->load(['seller', 'village', 'category', 'images']);
    }

    public function delete(User $user, Product $product): void
    {
        $this->ensureOwnership($user, $product);
        $product->delete();
    }

    public function moderate(Product $product, array $payload): Product
    {
        $product->fill([
            'status' => $payload['status'],
            'rejection_reason' => $payload['rejection_reason'] ?? null,
        ])->save();

        return $product->load(['seller', 'village', 'category', 'images']);
    }

    public function addImage(Product $product, UploadedFile $image, bool $isPrimary = false): ProductImage
    {
        return $this->storeImage($product, $image, $isPrimary);
    }

    protected function ensureOwnership(User $user, Product $product): void
    {
        if ($user->role === 'admin') {
            return;
        }

        if ($product->seller_id !== $user->id) {
            throw ValidationException::withMessages([
                'product' => ['You are not allowed to manage this product.'],
            ]);
        }
    }

    protected function storeImage(Product $product, UploadedFile $image, bool $isPrimary): ProductImage
    {
        $path = $image->store('products', 'public');

        if ($isPrimary) {
            $product->images()->update(['is_primary' => false]);
            $product->main_image_path = $path;
            $product->save();
        }

        return $product->images()->create([
            'image_path' => $path,
            'is_primary' => $isPrimary,
        ]);
    }

    protected function generateUniqueSlug(string $name, ?int $ignoreId = null): string
    {
        $baseSlug = Str::slug($name);
        $slug = $baseSlug;
        $suffix = 1;

        while (Product::query()
            ->when($ignoreId, fn($query) => $query->whereKeyNot($ignoreId))
            ->where('slug', $slug)
            ->exists()
        ) {
            $slug = $baseSlug . '-' . $suffix;
            $suffix++;
        }

        return $slug;
    }
}
