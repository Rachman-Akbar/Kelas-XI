<?php

namespace App\Modules\Product\Services;

use App\Modules\Product\Models\ProductCategory;
use Illuminate\Contracts\Pagination\LengthAwarePaginator;

class ProductCategoryService
{
    public function paginate(array $filters = []): LengthAwarePaginator
    {
        return ProductCategory::query()
            ->when($filters['category_type'] ?? null, fn($query, $type) => $query->where('category_type', $type))
            ->when(isset($filters['is_active']), fn($query) => $query->where('is_active', (bool) $filters['is_active']))
            ->latest()
            ->paginate($filters['per_page'] ?? 15);
    }

    public function create(array $payload): ProductCategory
    {
        return ProductCategory::query()->create($payload);
    }

    public function update(ProductCategory $category, array $payload): ProductCategory
    {
        $category->fill($payload)->save();

        return $category;
    }
}
