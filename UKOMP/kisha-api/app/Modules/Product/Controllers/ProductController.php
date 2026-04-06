<?php

namespace App\Modules\Product\Controllers;

use App\Http\Controllers\Controller;
use App\Modules\Product\Models\Product;
use App\Modules\Product\Requests\StoreProductRequest;
use App\Modules\Product\Requests\UpdateProductRequest;
use App\Modules\Product\Requests\UploadProductImageRequest;
use App\Modules\Product\Resources\ProductResource;
use App\Modules\Product\Services\ProductService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class ProductController extends Controller
{
    public function __construct(private readonly ProductService $productService) {}

    public function index(Request $request): JsonResponse
    {
        return response()->json([
            'data' => ProductResource::collection(
                $this->productService->paginateForUser($request->user(), $request->only(['search', 'category_id', 'per_page']))
            ),
        ]);
    }

    public function show(Request $request, Product $product): JsonResponse
    {
        return response()->json([
            'data' => new ProductResource($this->productService->getVisibleProduct($request->user(), $product)),
        ]);
    }

    public function store(StoreProductRequest $request): JsonResponse
    {
        $product = $this->productService->create(
            $request->user(),
            $request->validated(),
            $request->file('image')
        );

        return response()->json([
            'message' => 'Product created successfully.',
            'data' => new ProductResource($product),
        ], Response::HTTP_CREATED);
    }

    public function update(UpdateProductRequest $request, Product $product): JsonResponse
    {
        $product = $this->productService->update(
            $request->user(),
            $product,
            $request->validated(),
            $request->file('image')
        );

        return response()->json([
            'message' => 'Product updated successfully.',
            'data' => new ProductResource($product),
        ]);
    }

    public function destroy(Request $request, Product $product): JsonResponse
    {
        $this->productService->delete($request->user(), $product);

        return response()->json([
            'message' => 'Product deleted successfully.',
        ]);
    }

    public function uploadImage(UploadProductImageRequest $request, Product $product): JsonResponse
    {
        $this->productService->addImage(
            $product,
            $request->file('image'),
            (bool) $request->boolean('is_primary')
        );

        return response()->json([
            'message' => 'Product image uploaded successfully.',
            'data' => new ProductResource($product->load(['seller', 'village', 'category', 'images'])),
        ], Response::HTTP_CREATED);
    }
}
