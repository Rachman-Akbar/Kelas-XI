<?php

use Illuminate\Support\Facades\Route;
use App\Modules\Admin\Controllers\AdminController;
use App\Modules\Auth\Controllers\AuthController;
use App\Modules\Product\Controllers\ProductCategoryController;
use App\Modules\Product\Controllers\ProductController;
use App\Modules\Village\Controllers\VillageController;

Route::prefix('v1')->group(function (): void {
    Route::get('/villages', [VillageController::class, 'index']);

    Route::prefix('auth')->group(function (): void {
        Route::post('/register', [AuthController::class, 'register']);
        Route::post('/login', [AuthController::class, 'login']);
        Route::post('/forgot-password', [AuthController::class, 'forgotPassword']);
        Route::post('/reset-password', [AuthController::class, 'resetPassword']);

        Route::middleware('auth:sanctum')->group(function (): void {
            Route::post('/logout', [AuthController::class, 'logout']);
            Route::get('/me', [AuthController::class, 'me']);
        });
    });

    Route::middleware('auth:sanctum')->group(function (): void {
        Route::get('/categories', [ProductCategoryController::class, 'index']);

        Route::get('/products', [ProductController::class, 'index']);
        Route::get('/products/{product}', [ProductController::class, 'show']);

        Route::middleware('role:seller,admin')->group(function (): void {
            Route::post('/products', [ProductController::class, 'store']);
            Route::put('/products/{product}', [ProductController::class, 'update']);
            Route::delete('/products/{product}', [ProductController::class, 'destroy']);
            Route::post('/products/{product}/images', [ProductController::class, 'uploadImage']);
        });

        Route::prefix('admin')->middleware('role:admin')->group(function (): void {
            Route::get('/dashboard', [AdminController::class, 'dashboard']);
            Route::get('/users', [AdminController::class, 'users']);
            Route::patch('/users/{user}/status', [AdminController::class, 'updateUserStatus']);

            Route::get('/villages', [AdminController::class, 'villages']);
            Route::get('/villages/{village}', [AdminController::class, 'villageDetail']);
            Route::post('/villages', [VillageController::class, 'store']);
            Route::put('/villages/{village}', [VillageController::class, 'update']);
            Route::delete('/villages/{village}', [VillageController::class, 'destroy']);

            Route::get('/categories', [ProductCategoryController::class, 'index']);
            Route::post('/categories', [ProductCategoryController::class, 'store']);
            Route::put('/categories/{category}', [ProductCategoryController::class, 'update']);

            Route::get('/products/moderation', [AdminController::class, 'moderationQueue']);
            Route::patch('/products/{product}/moderation', [AdminController::class, 'moderateProduct']);
        });

        Route::post('/orders/checkout', fn() => response()->json(['message' => 'TODO: checkout implementation next.']));
        Route::get('/orders', fn() => response()->json(['message' => 'TODO: orders implementation next.']));
        Route::get('/orders/{id}', fn(int $id) => response()->json(['message' => 'TODO: order detail', 'id' => $id]));
        Route::patch('/orders/{id}/status', fn(int $id) => response()->json(['message' => 'TODO: update order status', 'id' => $id]));
    });
});
