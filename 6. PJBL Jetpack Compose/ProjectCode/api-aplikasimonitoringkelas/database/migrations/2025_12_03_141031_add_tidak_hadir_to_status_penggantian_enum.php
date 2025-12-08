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
        // Modify the enum to include 'tidak_hadir'
        \DB::statement("ALTER TABLE guru_pengganties MODIFY COLUMN status_penggantian ENUM('pending', 'dijadwalkan', 'selesai', 'dibatalkan', 'tidak_hadir')");
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Revert to original enum without 'tidak_hadir'
        \DB::statement("ALTER TABLE guru_pengganties MODIFY COLUMN status_penggantian ENUM('pending', 'dijadwalkan', 'selesai', 'dibatalkan')");
    }
};
