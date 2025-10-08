<?php

namespace Database\Seeders;

use App\Models\User;
use App\Models\Guru;
use App\Models\Kelas;
use App\Models\MataPelajaran;
use App\Models\Siswa;
use App\Models\Jadwal;
use App\Models\Kehadiran;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use Carbon\Carbon;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // ========== USERS ==========
        // Create Admin User
        User::create([
            'name' => 'Admin',
            'email' => 'admin@sekolah.com',
            'password' => Hash::make('admin123'),
            'role' => 'admin'
        ]);

        // Create Kepala Sekolah
        User::create([
            'name' => 'Kepala Sekolah',
            'email' => 'kepsek@sekolah.com',
            'password' => Hash::make('kepsek123'),
            'role' => 'kepala_sekolah'
        ]);

        // Create Kurikulum
        User::create([
            'name' => 'Staff Kurikulum',
            'email' => 'kurikulum@sekolah.com',
            'password' => Hash::make('kurikulum123'),
            'role' => 'kurikulum'
        ]);

        // ========== GURU ==========
        $guru1 = Guru::create([
            'nip' => '198501012010011001',
            'nama' => 'Budi Santoso, S.Pd',
            'email' => 'budi.guru@sekolah.com',
            'no_telp' => '081234567890',
            'alamat' => 'Jl. Pendidikan No. 123, Jakarta',
            'jenis_kelamin' => 'L',
            'tanggal_lahir' => '1985-01-01',
            'status' => 'aktif'
        ]);

        $guru2 = Guru::create([
            'nip' => '198602022010012002',
            'nama' => 'Siti Aminah, S.Pd',
            'email' => 'siti.guru@sekolah.com',
            'no_telp' => '081234567891',
            'alamat' => 'Jl. Guru No. 45, Jakarta',
            'jenis_kelamin' => 'P',
            'tanggal_lahir' => '1986-02-02',
            'status' => 'aktif'
        ]);

        $guru3 = Guru::create([
            'nip' => '198703032010013003',
            'nama' => 'Ahmad Yani, S.Pd',
            'email' => 'ahmad.guru@sekolah.com',
            'no_telp' => '081234567892',
            'alamat' => 'Jl. Pengajar No. 67, Jakarta',
            'jenis_kelamin' => 'L',
            'tanggal_lahir' => '1987-03-03',
            'status' => 'aktif'
        ]);

        $guru4 = Guru::create([
            'nip' => '198804042010014004',
            'nama' => 'Dewi Kusuma, S.Pd',
            'email' => 'dewi.guru@sekolah.com',
            'no_telp' => '081234567893',
            'alamat' => 'Jl. Pendidik No. 89, Jakarta',
            'jenis_kelamin' => 'P',
            'tanggal_lahir' => '1988-04-04',
            'status' => 'aktif'
        ]);

        $guru5 = Guru::create([
            'nip' => '198905052010015005',
            'nama' => 'Rudi Hartono, S.Pd',
            'email' => 'rudi.guru@sekolah.com',
            'no_telp' => '081234567894',
            'alamat' => 'Jl. Edukasi No. 101, Jakarta',
            'jenis_kelamin' => 'L',
            'tanggal_lahir' => '1989-05-05',
            'status' => 'aktif'
        ]);

        // ========== MATA PELAJARAN ==========
        $mapel1 = MataPelajaran::create([
            'kode' => 'MTK',
            'nama' => 'Matematika',
            'deskripsi' => 'Mata pelajaran matematika untuk tingkat SMA',
            'sks' => 4,
            'kategori' => 'wajib',
            'status' => 'aktif'
        ]);

        $mapel2 = MataPelajaran::create([
            'kode' => 'FIS',
            'nama' => 'Fisika',
            'deskripsi' => 'Mata pelajaran fisika untuk tingkat SMA',
            'sks' => 4,
            'kategori' => 'wajib',
            'status' => 'aktif'
        ]);

        $mapel3 = MataPelajaran::create([
            'kode' => 'KIM',
            'nama' => 'Kimia',
            'deskripsi' => 'Mata pelajaran kimia untuk tingkat SMA',
            'sks' => 4,
            'kategori' => 'wajib',
            'status' => 'aktif'
        ]);

        $mapel4 = MataPelajaran::create([
            'kode' => 'BIO',
            'nama' => 'Biologi',
            'deskripsi' => 'Mata pelajaran biologi untuk tingkat SMA',
            'sks' => 4,
            'kategori' => 'wajib',
            'status' => 'aktif'
        ]);

        $mapel5 = MataPelajaran::create([
            'kode' => 'BIN',
            'nama' => 'Bahasa Indonesia',
            'deskripsi' => 'Mata pelajaran bahasa Indonesia untuk tingkat SMA',
            'sks' => 4,
            'kategori' => 'wajib',
            'status' => 'aktif'
        ]);

        $mapel6 = MataPelajaran::create([
            'kode' => 'BING',
            'nama' => 'Bahasa Inggris',
            'deskripsi' => 'Mata pelajaran bahasa Inggris untuk tingkat SMA',
            'sks' => 3,
            'kategori' => 'wajib',
            'status' => 'aktif'
        ]);

        // ========== KELAS ==========
        $kelas1 = Kelas::create([
            'nama' => 'X IPA 1',
            'tingkat' => 10,
            'jurusan' => 'IPA',
            'wali_kelas_id' => $guru1->id,
            'kapasitas' => 36,
            'jumlah_siswa' => 0,
            'ruangan' => 'R101',
            'status' => 'aktif'
        ]);

        $kelas2 = Kelas::create([
            'nama' => 'X IPA 2',
            'tingkat' => 10,
            'jurusan' => 'IPA',
            'wali_kelas_id' => $guru2->id,
            'kapasitas' => 36,
            'jumlah_siswa' => 0,
            'ruangan' => 'R102',
            'status' => 'aktif'
        ]);

        $kelas3 = Kelas::create([
            'nama' => 'XI IPA 1',
            'tingkat' => 11,
            'jurusan' => 'IPA',
            'wali_kelas_id' => $guru3->id,
            'kapasitas' => 36,
            'jumlah_siswa' => 0,
            'ruangan' => 'R201',
            'status' => 'aktif'
        ]);

        $kelas4 = Kelas::create([
            'nama' => 'XII IPA 1',
            'tingkat' => 12,
            'jurusan' => 'IPA',
            'wali_kelas_id' => $guru4->id,
            'kapasitas' => 36,
            'jumlah_siswa' => 0,
            'ruangan' => 'R301',
            'status' => 'aktif'
        ]);

        // ========== SISWA ==========
        // Siswa Kelas X IPA 1
        $siswa1 = Siswa::create([
            'nis' => '2024001',
            'nisn' => '0012345601',
            'nama' => 'Andi Wijaya',
            'email' => 'andi.siswa@sekolah.com',
            'no_telp' => '081298765001',
            'alamat' => 'Jl. Siswa No. 1, Jakarta',
            'jenis_kelamin' => 'L',
            'tanggal_lahir' => '2008-05-15',
            'kelas_id' => $kelas1->id,
            'nama_orang_tua' => 'Bapak Wijaya',
            'no_telp_orang_tua' => '081234560001',
            'status' => 'aktif'
        ]);

        $siswa2 = Siswa::create([
            'nis' => '2024002',
            'nisn' => '0012345602',
            'nama' => 'Siti Nurhaliza',
            'email' => 'siti.siswa@sekolah.com',
            'no_telp' => '081298765002',
            'alamat' => 'Jl. Siswa No. 2, Jakarta',
            'jenis_kelamin' => 'P',
            'tanggal_lahir' => '2008-06-20',
            'kelas_id' => $kelas1->id,
            'nama_orang_tua' => 'Bapak Nur',
            'no_telp_orang_tua' => '081234560002',
            'status' => 'aktif'
        ]);

        $siswa3 = Siswa::create([
            'nis' => '2024003',
            'nisn' => '0012345603',
            'nama' => 'Budi Pratama',
            'email' => 'budi.siswa@sekolah.com',
            'no_telp' => '081298765003',
            'alamat' => 'Jl. Siswa No. 3, Jakarta',
            'jenis_kelamin' => 'L',
            'tanggal_lahir' => '2008-07-25',
            'kelas_id' => $kelas1->id,
            'nama_orang_tua' => 'Bapak Pratama',
            'no_telp_orang_tua' => '081234560003',
            'status' => 'aktif'
        ]);

        // Siswa Kelas X IPA 2
        $siswa4 = Siswa::create([
            'nis' => '2024004',
            'nisn' => '0012345604',
            'nama' => 'Dewi Lestari',
            'email' => 'dewi.siswa@sekolah.com',
            'no_telp' => '081298765004',
            'alamat' => 'Jl. Siswa No. 4, Jakarta',
            'jenis_kelamin' => 'P',
            'tanggal_lahir' => '2008-08-30',
            'kelas_id' => $kelas2->id,
            'nama_orang_tua' => 'Bapak Lestari',
            'no_telp_orang_tua' => '081234560004',
            'status' => 'aktif'
        ]);

        $siswa5 = Siswa::create([
            'nis' => '2024005',
            'nisn' => '0012345605',
            'nama' => 'Rudi Hermawan',
            'email' => 'rudi.siswa@sekolah.com',
            'no_telp' => '081298765005',
            'alamat' => 'Jl. Siswa No. 5, Jakarta',
            'jenis_kelamin' => 'L',
            'tanggal_lahir' => '2008-09-10',
            'kelas_id' => $kelas2->id,
            'nama_orang_tua' => 'Bapak Hermawan',
            'no_telp_orang_tua' => '081234560005',
            'status' => 'aktif'
        ]);

        // Update jumlah siswa di kelas
        $kelas1->update(['jumlah_siswa' => 3]);
        $kelas2->update(['jumlah_siswa' => 2]);

        // ========== JADWAL ==========
        // Jadwal untuk Kelas X IPA 1 - Senin
        $jadwal1 = Jadwal::create([
            'kelas_id' => $kelas1->id,
            'mata_pelajaran_id' => $mapel1->id,
            'guru_id' => $guru1->id,
            'hari' => 'Senin',
            'jam_mulai' => '07:00',
            'jam_selesai' => '08:30',
            'ruangan' => 'R101',
            'semester' => 'Ganjil',
            'tahun_ajaran' => '2024/2025',
            'status' => 'aktif'
        ]);

        $jadwal2 = Jadwal::create([
            'kelas_id' => $kelas1->id,
            'mata_pelajaran_id' => $mapel2->id,
            'guru_id' => $guru2->id,
            'hari' => 'Senin',
            'jam_mulai' => '08:30',
            'jam_selesai' => '10:00',
            'ruangan' => 'R101',
            'semester' => 'Ganjil',
            'tahun_ajaran' => '2024/2025',
            'status' => 'aktif'
        ]);

        // Jadwal untuk Kelas X IPA 1 - Selasa
        $jadwal3 = Jadwal::create([
            'kelas_id' => $kelas1->id,
            'mata_pelajaran_id' => $mapel3->id,
            'guru_id' => $guru3->id,
            'hari' => 'Selasa',
            'jam_mulai' => '07:00',
            'jam_selesai' => '08:30',
            'ruangan' => 'R101',
            'semester' => 'Ganjil',
            'tahun_ajaran' => '2024/2025',
            'status' => 'aktif'
        ]);

        $jadwal4 = Jadwal::create([
            'kelas_id' => $kelas1->id,
            'mata_pelajaran_id' => $mapel4->id,
            'guru_id' => $guru4->id,
            'hari' => 'Selasa',
            'jam_mulai' => '08:30',
            'jam_selesai' => '10:00',
            'ruangan' => 'R101',
            'semester' => 'Ganjil',
            'tahun_ajaran' => '2024/2025',
            'status' => 'aktif'
        ]);

        // Jadwal untuk Kelas X IPA 2
        $jadwal5 = Jadwal::create([
            'kelas_id' => $kelas2->id,
            'mata_pelajaran_id' => $mapel1->id,
            'guru_id' => $guru1->id,
            'hari' => 'Rabu',
            'jam_mulai' => '07:00',
            'jam_selesai' => '08:30',
            'ruangan' => 'R102',
            'semester' => 'Ganjil',
            'tahun_ajaran' => '2024/2025',
            'status' => 'aktif'
        ]);

        // ========== KEHADIRAN ==========
        // Kehadiran untuk jadwal 1 (Matematika - Senin) - Tanggal hari ini
        $today = Carbon::today();

        Kehadiran::create([
            'jadwal_id' => $jadwal1->id,
            'siswa_id' => $siswa1->id,
            'tanggal' => $today->format('Y-m-d'),
            'status' => 'Hadir',
            'waktu_absen' => $today->copy()->setTime(7, 5, 0),
            'keterangan' => null
        ]);

        Kehadiran::create([
            'jadwal_id' => $jadwal1->id,
            'siswa_id' => $siswa2->id,
            'tanggal' => $today->format('Y-m-d'),
            'status' => 'Hadir',
            'waktu_absen' => $today->copy()->setTime(7, 10, 0),
            'keterangan' => null
        ]);

        Kehadiran::create([
            'jadwal_id' => $jadwal1->id,
            'siswa_id' => $siswa3->id,
            'tanggal' => $today->format('Y-m-d'),
            'status' => 'Sakit',
            'waktu_absen' => null,
            'keterangan' => 'Sakit demam'
        ]);

        // Kehadiran untuk jadwal 2 (Fisika - Senin)
        Kehadiran::create([
            'jadwal_id' => $jadwal2->id,
            'siswa_id' => $siswa1->id,
            'tanggal' => $today->format('Y-m-d'),
            'status' => 'Hadir',
            'waktu_absen' => $today->copy()->setTime(8, 32, 0),
            'keterangan' => null
        ]);

        Kehadiran::create([
            'jadwal_id' => $jadwal2->id,
            'siswa_id' => $siswa2->id,
            'tanggal' => $today->format('Y-m-d'),
            'status' => 'Hadir',
            'waktu_absen' => $today->copy()->setTime(8, 35, 0),
            'keterangan' => null
        ]);

        Kehadiran::create([
            'jadwal_id' => $jadwal2->id,
            'siswa_id' => $siswa3->id,
            'tanggal' => $today->format('Y-m-d'),
            'status' => 'Sakit',
            'waktu_absen' => null,
            'keterangan' => 'Sakit demam (lanjutan)'
        ]);

        echo "\n✅ Seeding completed successfully!\n\n";
        echo "📊 Data yang berhasil ditambahkan:\n";
        echo "- Users: 3 (admin, kepsek, kurikulum)\n";
        echo "- Guru: 5\n";
        echo "- Mata Pelajaran: 6\n";
        echo "- Kelas: 4\n";
        echo "- Siswa: 5\n";
        echo "- Jadwal: 5\n";
        echo "- Kehadiran: 6\n\n";
        echo "🔑 Login credentials:\n";
        echo "Admin: admin@sekolah.com / admin123\n";
        echo "Kepsek: kepsek@sekolah.com / kepsek123\n";
        echo "Kurikulum: kurikulum@sekolah.com / kurikulum123\n\n";
    }
}
