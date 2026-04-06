<?php

namespace App\Modules\Village\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class VillageResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'name' => $this->name,
            'district' => $this->district,
            'city' => $this->city,
            'province' => $this->province,
            'is_active' => (bool) $this->is_active,
            'users_count' => $this->whenCounted('users'),
            'products_count' => $this->whenCounted('products'),
            'created_at' => $this->created_at,
        ];
    }
}
