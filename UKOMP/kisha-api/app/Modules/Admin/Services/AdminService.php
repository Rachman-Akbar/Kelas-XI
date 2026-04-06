<?php

namespace App\Modules\Admin\Services;

use App\Models\User;
use App\Modules\Product\Models\Product;
use App\Modules\Village\Models\Village;
use Illuminate\Contracts\Pagination\LengthAwarePaginator;

class AdminService
{
    public function dashboard(): array
    {
        return [
            'users_total' => User::query()->count(),
            'buyers_total' => User::query()->where('role', 'buyer')->count(),
            'sellers_total' => User::query()->where('role', 'seller')->count(),
            'villages_total' => Village::query()->count(),
            'products_total' => Product::query()->count(),
            'products_pending' => Product::query()->where('status', 'draft')->count(),
            'products_active' => Product::query()->where('status', 'active')->count(),
        ];
    }

    public function users(array $filters = []): LengthAwarePaginator
    {
        return User::query()
            ->with('village')
            ->when($filters['role'] ?? null, fn($query, $role) => $query->where('role', $role))
            ->when(isset($filters['is_active']), fn($query) => $query->where('is_active', (bool) $filters['is_active']))
            ->latest()
            ->paginate($filters['per_page'] ?? 15);
    }

    public function updateUser(User $user, array $payload): User
    {
        $user->fill($payload)->save();

        return $user->load('village');
    }

    public function moderationQueue(array $filters = []): LengthAwarePaginator
    {
        return Product::query()
            ->with(['seller', 'village', 'category', 'images'])
            ->when($filters['status'] ?? null, fn($query, $status) => $query->where('status', $status))
            ->latest()
            ->paginate($filters['per_page'] ?? 15);
    }
}
