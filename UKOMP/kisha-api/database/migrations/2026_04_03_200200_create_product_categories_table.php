<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('product_categories', function (Blueprint $table): void {
            $table->id();
            $table->string('name');
            $table->enum('category_type', ['goods', 'service']);
            $table->boolean('is_active')->default(true);
            $table->timestamps();
            $table->unique(['name', 'category_type']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('product_categories');
    }
};
