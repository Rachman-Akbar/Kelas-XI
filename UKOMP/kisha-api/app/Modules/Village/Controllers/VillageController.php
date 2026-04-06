<?php

namespace App\Modules\Village\Controllers;

use App\Http\Controllers\Controller;
use App\Modules\Village\Models\Village;
use App\Modules\Village\Requests\StoreVillageRequest;
use App\Modules\Village\Requests\UpdateVillageRequest;
use App\Modules\Village\Resources\VillageResource;
use App\Modules\Village\Services\VillageService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class VillageController extends Controller
{
    public function __construct(private readonly VillageService $villageService) {}

    public function index(Request $request): JsonResponse
    {
        return response()->json([
            'data' => VillageResource::collection($this->villageService->paginate($request->only(['is_active', 'per_page']))),
        ]);
    }

    public function store(StoreVillageRequest $request): JsonResponse
    {
        $village = $this->villageService->create($request->validated());

        return response()->json([
            'message' => 'Village created successfully.',
            'data' => new VillageResource($village),
        ], Response::HTTP_CREATED);
    }

    public function show(Village $village): JsonResponse
    {
        return response()->json([
            'data' => new VillageResource($village->loadCount(['users', 'products'])),
        ]);
    }

    public function update(UpdateVillageRequest $request, Village $village): JsonResponse
    {
        $village = $this->villageService->update($village, $request->validated());

        return response()->json([
            'message' => 'Village updated successfully.',
            'data' => new VillageResource($village),
        ]);
    }

    public function destroy(Village $village): JsonResponse
    {
        $village->delete();

        return response()->json([
            'message' => 'Village deleted successfully.',
        ]);
    }
}
