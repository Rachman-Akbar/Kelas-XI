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
        // Change the column to VARCHAR first to add the new enum value
        Schema::table('guru_pengganties', function (Blueprint $table) {
            $table->string('status_penggantian')->change();
        });

        // Update all 'dijadwalkan' values to 'pending' to keep the same meaning
        \DB::statement("UPDATE guru_pengganties SET status_penggantian = 'pending' WHERE status_penggantian = 'dijadwalkan'");

        // Now convert back to enum with all the values including 'pending' and set default to 'pending'
        \DB::statement("ALTER TABLE guru_pengganties MODIFY COLUMN status_penggantian ENUM('pending', 'dijadwalkan', 'selesai', 'dibatalkan') DEFAULT 'pending'");
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Change the column to VARCHAR first to modify enum values
        Schema::table('guru_pengganties', function (Blueprint $table) {
            $table->string('status_penggantian')->change();
        });

        // Convert 'pending' values back to 'dijadwalkan' for consistency during rollback
        \DB::statement("UPDATE guru_pengganties SET status_penggantian = 'dijadwalkan' WHERE status_penggantian = 'pending'");

        // Now convert back to enum without 'pending'
        \DB::statement("ALTER TABLE guru_pengganties MODIFY COLUMN status_penggantian ENUM('dijadwalkan', 'selesai', 'dibatalkan') DEFAULT 'dijadwalkan'");
    }
};
