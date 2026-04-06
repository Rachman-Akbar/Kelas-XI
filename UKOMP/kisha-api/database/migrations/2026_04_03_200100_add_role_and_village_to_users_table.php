<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('users', function (Blueprint $table): void {
            $table->enum('role', ['admin', 'seller', 'buyer'])->default('buyer')->after('email');
            $table->foreignId('village_id')->nullable()->after('role')->constrained('villages')->nullOnDelete();
            $table->boolean('is_active')->default(true)->after('remember_token');
        });
    }

    public function down(): void
    {
        Schema::table('users', function (Blueprint $table): void {
            $table->dropConstrainedForeignId('village_id');
            $table->dropColumn(['role', 'is_active']);
        });
    }
};
