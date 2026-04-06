<?php

namespace App\Modules\Admin\Requests;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class UpdateUserStatusRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'is_active' => ['required', 'boolean'],
            'role' => ['sometimes', Rule::in(['admin', 'seller', 'buyer'])],
            'village_id' => ['sometimes', 'nullable', 'integer', 'exists:villages,id'],
        ];
    }
}
