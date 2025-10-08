<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Siswa extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'nis',
        'nisn',
        'nama',
        'email',
        'no_telp',
        'alamat',
        'jenis_kelamin',
        'tanggal_lahir',
        'foto',
        'kelas_id',
        'nama_orang_tua',
        'no_telp_orang_tua',
        'status',
    ];

    protected $casts = [
        'tanggal_lahir' => 'date',
    ];

    // Relationships
    public function kelas()
    {
        return $this->belongsTo(Kelas::class);
    }

    public function kehadirans()
    {
        return $this->hasMany(Kehadiran::class);
    }
}
