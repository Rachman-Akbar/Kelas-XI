<?php

namespace App\Modules\Product\Observers;

use App\Modules\Product\Models\Product;
use App\Services\Firebase\FirestoreProductSyncService;
use Illuminate\Support\Facades\Log;
use Throwable;

class ProductObserver
{
    public function __construct(private readonly FirestoreProductSyncService $syncService) {}

    public function saved(Product $product): void
    {
        try {
            $this->syncService->syncProduct($product);
        } catch (Throwable $exception) {
            Log::error('Failed to sync product to Firestore on save.', [
                'product_id' => $product->id,
                'message' => $exception->getMessage(),
            ]);
        }
    }

    public function deleted(Product $product): void
    {
        try {
            $this->syncService->deleteProduct($product->id);
        } catch (Throwable $exception) {
            Log::error('Failed to remove product from Firestore on delete.', [
                'product_id' => $product->id,
                'message' => $exception->getMessage(),
            ]);
        }
    }
}
