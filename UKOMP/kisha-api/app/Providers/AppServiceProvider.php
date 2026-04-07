<?php

namespace App\Providers;

use App\Modules\Product\Models\Product;
use App\Modules\Product\Models\ProductImage;
use App\Modules\Product\Observers\ProductImageObserver;
use App\Modules\Product\Observers\ProductObserver;
use Illuminate\Auth\Notifications\ResetPassword;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        //
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        Product::observe(ProductObserver::class);
        ProductImage::observe(ProductImageObserver::class);

        ResetPassword::createUrlUsing(function ($notifiable, string $token): string {
            $frontendUrl = rtrim(config('app.frontend_url', env('FRONTEND_URL', 'http://localhost:3000')), '/');
            $email = urlencode($notifiable->getEmailForPasswordReset());

            return "{$frontendUrl}/reset-password?token={$token}&email={$email}";
        });
    }
}
