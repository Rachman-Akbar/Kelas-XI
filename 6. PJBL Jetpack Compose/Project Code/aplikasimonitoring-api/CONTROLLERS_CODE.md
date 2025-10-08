# Complete Controllers Code

Copy and paste the following code to complete your controllers.

## 1. MataPelajaranController.php

Replace the content of `app/Http/Controllers/MataPelajaranController.php` with the code in `MataPelajaranController_Complete.php`

## 2. KelasController.php

```php
<?php

namespace App\Http\Controllers;

use App\Models\Kelas;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class KelasController extends Controller
{
    public function index(Request $request)
    {
        try {
            $query = Kelas::query();

            if ($request->has('tingkat')) {
                $query->where('tingkat', $request->tingkat);
            }

            if ($request->has('jurusan')) {
                $query->where('jurusan', $request->jurusan);
            }

            if ($request->has('status')) {
                $query->where('status', $request->status);
            }

            if ($request->has('search')) {
                $search = $request->search;
                $query->where('nama', 'like', "%{$search}%");
            }

            $kelas = $query->with(['waliKelas', 'siswas'])->paginate($request->get('per_page', 15));

            return response()->json([
                'success' => true,
                'message' => 'Data kelas berhasil diambil',
                'data' => $kelas
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function store(Request $request)
    {
        try {
            $validator = Validator::make($request->all(), [
                'nama' => 'required|string|max:255',
                'tingkat' => 'required|integer|min:10|max:12',
                'jurusan' => 'required|string|max:255',
                'wali_kelas_id' => 'nullable|exists:gurus,id',
                'kapasitas' => 'nullable|integer|min:0',
                'jumlah_siswa' => 'nullable|integer|min:0',
                'ruangan' => 'nullable|string',
                'status' => 'nullable|in:aktif,non-aktif',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $kelas = Kelas::create($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Kelas berhasil ditambahkan',
                'data' => $kelas->load('waliKelas')
            ], 201);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function show($id)
    {
        try {
            $kelas = Kelas::with(['waliKelas', 'siswas', 'jadwals.mataPelajaran', 'jadwals.guru'])->find($id);

            if (!$kelas) {
                return response()->json([
                    'success' => false,
                    'message' => 'Kelas tidak ditemukan',
                    'data' => null
                ], 404);
            }

            return response()->json([
                'success' => true,
                'message' => 'Data kelas berhasil diambil',
                'data' => $kelas
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function update(Request $request, $id)
    {
        try {
            $kelas = Kelas::find($id);

            if (!$kelas) {
                return response()->json([
                    'success' => false,
                    'message' => 'Kelas tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $validator = Validator::make($request->all(), [
                'nama' => 'sometimes|string|max:255',
                'tingkat' => 'sometimes|integer|min:10|max:12',
                'jurusan' => 'sometimes|string|max:255',
                'wali_kelas_id' => 'nullable|exists:gurus,id',
                'kapasitas' => 'nullable|integer|min:0',
                'jumlah_siswa' => 'nullable|integer|min:0',
                'ruangan' => 'nullable|string',
                'status' => 'nullable|in:aktif,non-aktif',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $kelas->update($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Kelas berhasil diupdate',
                'data' => $kelas->load('waliKelas')
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function destroy($id)
    {
        try {
            $kelas = Kelas::find($id);

            if (!$kelas) {
                return response()->json([
                    'success' => false,
                    'message' => 'Kelas tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $kelas->delete();

            return response()->json([
                'success' => true,
                'message' => 'Kelas berhasil dihapus',
                'data' => null
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }
}
```

## 3. JadwalController.php

```php
<?php

namespace App\Http\Controllers;

use App\Models\Jadwal;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class JadwalController extends Controller
{
    public function index(Request $request)
    {
        try {
            $query = Jadwal::query();

            if ($request->has('kelas_id')) {
                $query->where('kelas_id', $request->kelas_id);
            }

            if ($request->has('guru_id')) {
                $query->where('guru_id', $request->guru_id);
            }

            if ($request->has('hari')) {
                $query->where('hari', $request->hari);
            }

            if ($request->has('tahun_ajaran')) {
                $query->where('tahun_ajaran', $request->tahun_ajaran);
            }

            if ($request->has('semester')) {
                $query->where('semester', $request->semester);
            }

            $jadwals = $query->with(['kelas', 'mataPelajaran', 'guru'])->paginate($request->get('per_page', 15));

            return response()->json([
                'success' => true,
                'message' => 'Data jadwal berhasil diambil',
                'data' => $jadwals
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function store(Request $request)
    {
        try {
            $validator = Validator::make($request->all(), [
                'kelas_id' => 'required|exists:kelas,id',
                'mata_pelajaran_id' => 'required|exists:mata_pelajarans,id',
                'guru_id' => 'required|exists:gurus,id',
                'hari' => 'required|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
                'jam_mulai' => 'required|date_format:H:i',
                'jam_selesai' => 'required|date_format:H:i|after:jam_mulai',
                'ruangan' => 'nullable|string',
                'semester' => 'required|string',
                'tahun_ajaran' => 'required|string',
                'keterangan' => 'nullable|string',
                'status' => 'nullable|in:aktif,non-aktif',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $jadwal = Jadwal::create($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Jadwal berhasil ditambahkan',
                'data' => $jadwal->load(['kelas', 'mataPelajaran', 'guru'])
            ], 201);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function show($id)
    {
        try {
            $jadwal = Jadwal::with(['kelas', 'mataPelajaran', 'guru', 'kehadirans'])->find($id);

            if (!$jadwal) {
                return response()->json([
                    'success' => false,
                    'message' => 'Jadwal tidak ditemukan',
                    'data' => null
                ], 404);
            }

            return response()->json([
                'success' => true,
                'message' => 'Data jadwal berhasil diambil',
                'data' => $jadwal
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function update(Request $request, $id)
    {
        try {
            $jadwal = Jadwal::find($id);

            if (!$jadwal) {
                return response()->json([
                    'success' => false,
                    'message' => 'Jadwal tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $validator = Validator::make($request->all(), [
                'kelas_id' => 'sometimes|exists:kelas,id',
                'mata_pelajaran_id' => 'sometimes|exists:mata_pelajarans,id',
                'guru_id' => 'sometimes|exists:gurus,id',
                'hari' => 'sometimes|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
                'jam_mulai' => 'sometimes|date_format:H:i',
                'jam_selesai' => 'sometimes|date_format:H:i|after:jam_mulai',
                'ruangan' => 'nullable|string',
                'semester' => 'sometimes|string',
                'tahun_ajaran' => 'sometimes|string',
                'keterangan' => 'nullable|string',
                'status' => 'nullable|in:aktif,non-aktif',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $jadwal->update($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Jadwal berhasil diupdate',
                'data' => $jadwal->load(['kelas', 'mataPelajaran', 'guru'])
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function destroy($id)
    {
        try {
            $jadwal = Jadwal::find($id);

            if (!$jadwal) {
                return response()->json([
                    'success' => false,
                    'message' => 'Jadwal tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $jadwal->delete();

            return response()->json([
                'success' => true,
                'message' => 'Jadwal berhasil dihapus',
                'data' => null
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }
}
```

## 4. KehadiranController.php

```php
<?php

namespace App\Http\Controllers;

use App\Models\Kehadiran;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class KehadiranController extends Controller
{
    public function index(Request $request)
    {
        try {
            $query = Kehadiran::query();

            if ($request->has('jadwal_id')) {
                $query->where('jadwal_id', $request->jadwal_id);
            }

            if ($request->has('siswa_id')) {
                $query->where('siswa_id', $request->siswa_id);
            }

            if ($request->has('tanggal')) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            if ($request->has('status')) {
                $query->where('status', $request->status);
            }

            $kehadirans = $query->with(['jadwal.kelas', 'jadwal.mataPelajaran', 'siswa'])
                               ->paginate($request->get('per_page', 15));

            return response()->json([
                'success' => true,
                'message' => 'Data kehadiran berhasil diambil',
                'data' => $kehadirans
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function store(Request $request)
    {
        try {
            $validator = Validator::make($request->all(), [
                'jadwal_id' => 'required|exists:jadwals,id',
                'siswa_id' => 'required|exists:siswas,id',
                'tanggal' => 'required|date',
                'status' => 'required|in:Hadir,Sakit,Izin,Alpa',
                'keterangan' => 'nullable|string',
                'waktu_absen' => 'nullable|date_format:Y-m-d H:i:s',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $kehadiran = Kehadiran::create($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Kehadiran berhasil dicatat',
                'data' => $kehadiran->load(['jadwal', 'siswa'])
            ], 201);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function show($id)
    {
        try {
            $kehadiran = Kehadiran::with(['jadwal.kelas', 'jadwal.mataPelajaran', 'jadwal.guru', 'siswa'])->find($id);

            if (!$kehadiran) {
                return response()->json([
                    'success' => false,
                    'message' => 'Kehadiran tidak ditemukan',
                    'data' => null
                ], 404);
            }

            return response()->json([
                'success' => true,
                'message' => 'Data kehadiran berhasil diambil',
                'data' => $kehadiran
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function update(Request $request, $id)
    {
        try {
            $kehadiran = Kehadiran::find($id);

            if (!$kehadiran) {
                return response()->json([
                    'success' => false,
                    'message' => 'Kehadiran tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $validator = Validator::make($request->all(), [
                'jadwal_id' => 'sometimes|exists:jadwals,id',
                'siswa_id' => 'sometimes|exists:siswas,id',
                'tanggal' => 'sometimes|date',
                'status' => 'sometimes|in:Hadir,Sakit,Izin,Alpa',
                'keterangan' => 'nullable|string',
                'waktu_absen' => 'nullable|date_format:Y-m-d H:i:s',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $kehadiran->update($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Kehadiran berhasil diupdate',
                'data' => $kehadiran->load(['jadwal', 'siswa'])
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function destroy($id)
    {
        try {
            $kehadiran = Kehadiran::find($id);

            if (!$kehadiran) {
                return response()->json([
                    'success' => false,
                    'message' => 'Kehadiran tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $kehadiran->delete();

            return response()->json([
                'success' => true,
                'message' => 'Kehadiran berhasil dihapus',
                'data' => null
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }
}
```

## Next: Update routes/api.php

Add these routes after the existing routes in `routes/api.php`:

```php
// Guru endpoints
Route::apiResource('gurus', GuruController::class);

// Siswa endpoints
Route::apiResource('siswas', SiswaController::class);

// MataPelajaran endpoints
Route::apiResource('mata-pelajarans', MataPelajaranController::class);

// Kelas endpoints
Route::apiResource('kelas', KelasController::class);

// Jadwal endpoints
Route::apiResource('jadwals', JadwalController::class);

// Kehadiran endpoints
Route::apiResource('kehadirans', KehadiranController::class);
```
