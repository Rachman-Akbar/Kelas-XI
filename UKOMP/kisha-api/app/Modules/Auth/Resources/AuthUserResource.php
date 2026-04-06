<?php

namespace App\Modules\Auth\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class AuthUserResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'name' => $this->name,
            'email' => $this->email,
            'role' => $this->role,
            'is_active' => (bool) $this->is_active,
            'village' => $this->whenLoaded('village', fn(): array => [
                'id' => $this->village->id,
                'name' => $this->village->name,
            ]),
            'created_at' => $this->created_at,
        ];
    }
}
