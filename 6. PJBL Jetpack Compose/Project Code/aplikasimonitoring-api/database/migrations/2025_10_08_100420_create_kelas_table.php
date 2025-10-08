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
        Schema::create('kelas', function (Blueprint $table) {
            $table->id();
            $table->string('nama'); // e.g., X RPL, XI RPL, XII RPL
            $table->integer('tingkat'); // 10, 11, 12
            $table->string('jurusan'); // RPL, TKJ, MM, etc.
            $table->foreignId('wali_kelas_id')->nullable()->constrained('gurus')->onDelete('set null');
            $table->integer('kapasitas')->default(36);
            $table->integer('jumlah_siswa')->default(0);
            $table->string('ruangan')->nullable();
            $table->enum('status', ['aktif', 'non-aktif'])->default('aktif');
            $table->timestamps();
            $table->softDeletes();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('kelas');
    }
};
