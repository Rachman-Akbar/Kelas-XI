<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        // Update existing records with 'dibatalkan' status to 'tidak_hadir'
        DB::statement("UPDATE guru_pengganties SET status_penggantian = 'tidak_hadir' WHERE status_penggantian = 'dibatalkan'");

        // Modify the enum to replace 'dibatalkan' with 'tidak_hadir'
        DB::statement("ALTER TABLE guru_pengganties MODIFY COLUMN status_penggantian ENUM('pending', 'dijadwalkan', 'selesai', 'tidak_hadir')");
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Update existing records with 'tidak_hadir' status back to 'dibatalkan'
        DB::statement("UPDATE guru_pengganties SET status_penggantian = 'dibatalkan' WHERE status_penggantian = 'tidak_hadir'");

        // Revert to original enum without 'tidak_hadir'
        DB::statement("ALTER TABLE guru_pengganties MODIFY COLUMN status_penggantian ENUM('pending', 'dijadwalkan', 'selesai', 'dibatalkan')");
    }
};
