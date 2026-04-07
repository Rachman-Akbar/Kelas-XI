<?php

namespace App\Modules\Product\Observers;

use App\Modules\Product\Models\ProductImage;
use App\Services\Firebase\FirestoreProductSyncService;
use Illuminate\Support\Facades\Log;
use Throwable;

class ProductImageObserver
{
    public function __construct(private readonly FirestoreProductSyncService $syncService) {}

    public function saved(ProductImage $image): void
    {
        if (! $image->product) {
            return;
        }

        try {
            $this->syncService->syncProduct($image->product);
        } catch (Throwable $exception) {
            Log::error('Failed to sync product to Firestore from image save.', [
                'product_id' => $image->product_id,
                'image_id' => $image->id,
                'message' => $exception->getMessage(),
            ]);
        }
    }

    public function deleted(ProductImage $image): void
    {
        if (! $image->product) {
            return;
        }

        try {
            $this->syncService->syncProduct($image->product);
        } catch (Throwable $exception) {
            Log::error('Failed to sync product to Firestore from image delete.', [
                'product_id' => $image->product_id,
                'image_id' => $image->id,
                'message' => $exception->getMessage(),
            ]);
        }
    }
}
