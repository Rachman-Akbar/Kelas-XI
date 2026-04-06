<?php

namespace App\Modules\Product\Controllers;

use App\Http\Controllers\Controller;
use App\Modules\Product\Requests\StoreProductCategoryRequest;
use App\Modules\Product\Requests\UpdateProductCategoryRequest;
use App\Modules\Product\Resources\ProductCategoryResource;
use App\Modules\Product\Services\ProductCategoryService;
use App\Modules\Product\Models\ProductCategory;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class ProductCategoryController extends Controller
{
    public function __construct(private readonly ProductCategoryService $categoryService) {}

    public function index(Request $request): JsonResponse
    {
        return response()->json([
            'data' => ProductCategoryResource::collection(
                $this->categoryService->paginate($request->only(['category_type', 'is_active', 'per_page']))
            ),
        ]);
    }

    public function store(StoreProductCategoryRequest $request): JsonResponse
    {
        $category = $this->categoryService->create($request->validated());

        return response()->json([
            'message' => 'Category created successfully.',
            'data' => new ProductCategoryResource($category),
        ], Response::HTTP_CREATED);
    }

    public function update(UpdateProductCategoryRequest $request, ProductCategory $category): JsonResponse
    {
        $category = $this->categoryService->update($category, $request->validated());

        return response()->json([
            'message' => 'Category updated successfully.',
            'data' => new ProductCategoryResource($category),
        ]);
    }
}
