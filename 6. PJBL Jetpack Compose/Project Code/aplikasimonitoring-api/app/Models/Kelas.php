<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Kelas extends Model
{
    use HasFactory, SoftDeletes;

    protected $table = 'kelas'; // prevent Laravel from pluralizing to 'kelases'

    protected $fillable = [
        'nama',
        'tingkat',
        'jurusan',
        'wali_kelas_id',
        'kapasitas',
        'jumlah_siswa',
        'ruangan',
        'status',
    ];

    // Relationships
    public function waliKelas()
    {
        return $this->belongsTo(Guru::class, 'wali_kelas_id');
    }

    public function siswas()
    {
        return $this->hasMany(Siswa::class);
    }

    public function jadwals()
    {
        return $this->hasMany(Jadwal::class);
    }
}
