<?php

namespace App\Modules\Village\Services;

use App\Modules\Village\Models\Village;
use Illuminate\Contracts\Pagination\LengthAwarePaginator;

class VillageService
{
    public function paginate(array $filters = []): LengthAwarePaginator
    {
        return Village::query()
            ->when(isset($filters['is_active']), fn($query) => $query->where('is_active', (bool) $filters['is_active']))
            ->latest()
            ->paginate($filters['per_page'] ?? 15);
    }

    public function create(array $payload): Village
    {
        return Village::query()->create($payload);
    }

    public function update(Village $village, array $payload): Village
    {
        $village->fill($payload)->save();

        return $village;
    }
}
