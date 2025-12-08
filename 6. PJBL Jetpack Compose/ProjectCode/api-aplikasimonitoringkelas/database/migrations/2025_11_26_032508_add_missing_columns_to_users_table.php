<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('users', function (Blueprint $table) {
            // Add missing columns that are defined in User model's fillable array
            if (!Schema::hasColumn('users', 'guru_id')) {
                $table->unsignedBigInteger('guru_id')->nullable();
            }

            if (!Schema::hasColumn('users', 'kelas_id')) {
                $table->unsignedBigInteger('kelas_id')->nullable();
            }

            if (!Schema::hasColumn('users', 'foto')) {
                $table->string('foto')->nullable();
            }
        });

        // Add foreign key constraints separately to avoid potential reference issues
        Schema::table('users', function (Blueprint $table) {
            if (!Schema::hasColumn('users', 'guru_id')) {
                $table->foreign('guru_id')->references('id')->on('gurus')->onDelete('set null');
            }

            if (!Schema::hasColumn('users', 'kelas_id')) {
                $table->foreign('kelas_id')->references('id')->on('kelas')->onDelete('set null');
            }
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->dropForeign(['guru_id']);
            $table->dropForeign(['kelas_id']);
            $table->dropColumn(['guru_id', 'kelas_id', 'foto']);
        });
    }
};
