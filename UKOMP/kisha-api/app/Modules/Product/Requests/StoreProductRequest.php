<?php

namespace App\Modules\Product\Requests;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class StoreProductRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'name' => ['required', 'string', 'max:180'],
            'category_id' => ['required', 'integer', 'exists:product_categories,id'],
            'description' => ['required', 'string', 'max:5000'],
            'price' => ['required', 'numeric', 'min:0'],
            'shipping_cost' => ['nullable', 'numeric', 'min:0'],
            'stock' => ['nullable', 'integer', 'min:0'],
            'unit' => ['nullable', 'string', 'max:50'],
            'status' => ['nullable', Rule::in(['draft', 'active', 'inactive'])],
            'village_id' => ['nullable', 'integer', 'exists:villages,id'],
            'image' => ['nullable', 'image', 'max:4096'],
        ];
    }
}
