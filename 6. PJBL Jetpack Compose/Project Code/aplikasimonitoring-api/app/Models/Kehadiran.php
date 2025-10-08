<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Kehadiran extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'jadwal_id',
        'siswa_id',
        'tanggal',
        'status',
        'keterangan',
        'waktu_absen',
    ];

    protected $casts = [
        'tanggal' => 'date',
        'waktu_absen' => 'datetime',
    ];

    // Relationships
    public function jadwal()
    {
        return $this->belongsTo(Jadwal::class);
    }

    public function siswa()
    {
        return $this->belongsTo(Siswa::class);
    }
}
