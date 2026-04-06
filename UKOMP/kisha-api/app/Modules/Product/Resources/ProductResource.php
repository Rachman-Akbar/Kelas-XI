<?php

namespace App\Modules\Product\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class ProductResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'name' => $this->name,
            'slug' => $this->slug,
            'description' => $this->description,
            'price' => $this->price,
            'shipping_cost' => $this->shipping_cost,
            'stock' => $this->stock,
            'unit' => $this->unit,
            'status' => $this->status,
            'rejection_reason' => $this->rejection_reason,
            'main_image_path' => $this->main_image_path,
            'seller' => $this->whenLoaded('seller', fn(): array => [
                'id' => $this->seller->id,
                'name' => $this->seller->name,
            ]),
            'village' => $this->whenLoaded('village', fn(): array => [
                'id' => $this->village->id,
                'name' => $this->village->name,
            ]),
            'category' => $this->whenLoaded('category', fn(): array => [
                'id' => $this->category->id,
                'name' => $this->category->name,
                'category_type' => $this->category->category_type,
            ]),
            'images' => $this->whenLoaded('images', fn() => ProductImageResource::collection($this->images)),
            'created_at' => $this->created_at,
        ];
    }
}
