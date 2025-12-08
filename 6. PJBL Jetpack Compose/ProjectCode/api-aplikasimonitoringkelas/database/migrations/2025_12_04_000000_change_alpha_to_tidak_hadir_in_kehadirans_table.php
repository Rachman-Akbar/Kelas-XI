<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        // First, modify the enum column to include both 'alpha' and 'tidak_hadir'
        DB::statement("ALTER TABLE kehadirans CHANGE COLUMN status status ENUM('hadir', 'alpha', 'tidak_hadir', 'sakit', 'izin') NOT NULL DEFAULT 'hadir'");

        // Then update existing 'alpha' records to 'tidak_hadir'
        DB::statement("UPDATE kehadirans SET status = 'tidak_hadir' WHERE status = 'alpha'");

        // Finally, modify the enum column to remove 'alpha' and keep 'tidak_hadir'
        DB::statement("ALTER TABLE kehadirans CHANGE COLUMN status status ENUM('hadir', 'tidak_hadir', 'sakit', 'izin') NOT NULL DEFAULT 'hadir'");
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // First, modify the enum column to include both 'tidak_hadir' and 'alpha' for the transition
        DB::statement("ALTER TABLE kehadirans CHANGE COLUMN status status ENUM('hadir', 'tidak_hadir', 'alpha', 'sakit', 'izin') NOT NULL DEFAULT 'hadir'");

        // Then update existing 'tidak_hadir' records back to 'alpha'
        DB::statement("UPDATE kehadirans SET status = 'alpha' WHERE status = 'tidak_hadir'");

        // Finally, revert the enum column to remove 'tidak_hadir' and keep 'alpha'
        DB::statement("ALTER TABLE kehadirans CHANGE COLUMN status status ENUM('hadir', 'alpha', 'sakit', 'izin') NOT NULL DEFAULT 'hadir'");
    }
};