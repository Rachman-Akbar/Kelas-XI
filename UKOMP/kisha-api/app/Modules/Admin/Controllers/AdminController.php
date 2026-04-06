<?php

namespace App\Modules\Admin\Controllers;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Modules\Admin\Requests\ModerateProductRequest;
use App\Modules\Admin\Requests\UpdateUserStatusRequest;
use App\Modules\Admin\Resources\AdminUserResource;
use App\Modules\Admin\Services\AdminService;
use App\Modules\Product\Models\Product;
use App\Modules\Product\Resources\ProductResource;
use App\Modules\Product\Services\ProductService;
use App\Modules\Village\Resources\VillageResource;
use App\Modules\Village\Services\VillageService;
use App\Modules\Village\Models\Village;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class AdminController extends Controller
{
    public function __construct(
        private readonly AdminService $adminService,
        private readonly ProductService $productService,
        private readonly VillageService $villageService
    ) {}

    public function dashboard(): JsonResponse
    {
        return response()->json([
            'data' => $this->adminService->dashboard(),
        ]);
    }

    public function users(Request $request): JsonResponse
    {
        return response()->json([
            'data' => AdminUserResource::collection(
                $this->adminService->users($request->only(['role', 'is_active', 'per_page']))
            ),
        ]);
    }

    public function updateUserStatus(UpdateUserStatusRequest $request, User $user): JsonResponse
    {
        $user = $this->adminService->updateUser($user, $request->validated());

        return response()->json([
            'message' => 'User updated successfully.',
            'data' => new AdminUserResource($user),
        ]);
    }

    public function moderationQueue(Request $request): JsonResponse
    {
        return response()->json([
            'data' => ProductResource::collection(
                $this->adminService->moderationQueue($request->only(['status', 'per_page']))
            ),
        ]);
    }

    public function moderateProduct(ModerateProductRequest $request, Product $product): JsonResponse
    {
        $product = $this->productService->moderate($product, $request->validated());

        return response()->json([
            'message' => 'Product moderation updated successfully.',
            'data' => new ProductResource($product),
        ]);
    }

    public function villages(Request $request): JsonResponse
    {
        return response()->json([
            'data' => VillageResource::collection($this->villageService->paginate($request->only(['is_active', 'per_page']))),
        ]);
    }

    public function villageDetail(Village $village): JsonResponse
    {
        return response()->json([
            'data' => new VillageResource($village->loadCount(['users', 'products'])),
        ]);
    }
}
