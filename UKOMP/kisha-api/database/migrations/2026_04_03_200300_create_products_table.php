<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('products', function (Blueprint $table): void {
            $table->id();
            $table->foreignId('seller_id')->constrained('users')->cascadeOnDelete();
            $table->foreignId('village_id')->constrained('villages')->cascadeOnDelete();
            $table->foreignId('category_id')->constrained('product_categories')->restrictOnDelete();
            $table->string('name');
            $table->string('slug')->unique();
            $table->text('description');
            $table->decimal('price', 14, 2);
            $table->decimal('shipping_cost', 14, 2)->default(0);
            $table->integer('stock')->default(0);
            $table->string('unit')->nullable();
            $table->string('main_image_path')->nullable();
            $table->enum('status', ['draft', 'active', 'inactive', 'rejected'])->default('draft');
            $table->string('rejection_reason')->nullable();
            $table->timestamps();
            $table->index(['village_id', 'status']);
            $table->index(['seller_id']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('products');
    }
};
