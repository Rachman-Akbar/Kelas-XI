<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Guru extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'nip',
        'nama',
        'email',
        'no_telp',
        'alamat',
        'jenis_kelamin',
        'tanggal_lahir',
        'foto',
        'status',
    ];

    protected $casts = [
        'tanggal_lahir' => 'date',
    ];

    // Relationships
    public function jadwals()
    {
        return $this->hasMany(Jadwal::class);
    }

    public function kelasWali()
    {
        return $this->hasMany(Kelas::class, 'wali_kelas_id');
    }
}
