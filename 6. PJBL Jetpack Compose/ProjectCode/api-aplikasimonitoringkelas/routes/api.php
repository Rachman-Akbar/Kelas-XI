<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\GuruController;
use App\Http\Controllers\SiswaController;
use App\Http\Controllers\KelasController;
use App\Http\Controllers\MataPelajaranController;
use App\Http\Controllers\JadwalController;
use App\Http\Controllers\KehadiranController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\GuruMengajarController;
use App\Http\Controllers\GuruPenggantiController;
use App\Http\Controllers\IzinGuruController;
use App\Http\Controllers\KehadiranGuruController;
use App\Http\Controllers\EnumController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

// Health check endpoint
Route::get('/health', function () {
    return response()->json([
        'status' => 'ok',
        'message' => 'API Server is running',
        'timestamp' => now()->toISOString()
    ]);
});

// Auth Routes (Public - No authentication required)
Route::prefix('auth')->group(function () {
    Route::post('/login', [\App\Http\Controllers\Api\AuthController::class, 'login']);
    Route::post('/register', [AuthController::class, 'register']); // Keep the original register endpoint
});

// Protected Auth Routes (Require authentication)
Route::middleware('auth:sanctum')->group(function () {
    Route::prefix('auth')->group(function () {
        Route::get('/me', [\App\Http\Controllers\Api\AuthController::class, 'me']);
        Route::post('/logout', [\App\Http\Controllers\Api\AuthController::class, 'logout']);
    });
});

// Welcome endpoint
Route::get('/', function () {
    return response()->json([
        'message' => 'Selamat datang di API Aplikasi Monitoring Kelas',
        'version' => '1.0.0',
        'endpoints' => [
            'guru' => '/api/guru',
            'siswa' => '/api/siswa',
            'kelas' => '/api/kelas',
            'mata-pelajaran' => '/api/mata-pelajaran',
            'jadwal' => '/api/jadwal',
            'kehadiran' => '/api/kehadiran',
            'user' => '/api/user',
            'enums' => '/api/enums',
        ]
    ]);
});

// Enum Routes - Get available filter options
Route::prefix('enums')->group(function () {
    Route::get('/', [EnumController::class, 'index']);
    Route::get('/{type}', [EnumController::class, 'show']);
    Route::get('/distinct/{table}/{column}', [EnumController::class, 'getDistinct']);
});

// User Routes
Route::prefix('user')->group(function () {
    Route::get('/', [UserController::class, 'index']);
    Route::get('/{id}', [UserController::class, 'show']);
    Route::post('/', [UserController::class, 'store']);
    Route::put('/{id}', [UserController::class, 'update']);
    Route::delete('/{id}', [UserController::class, 'destroy']);
    Route::get('/role/{role}', [UserController::class, 'byRole']);
});

// Guru Routes
Route::prefix('guru')->group(function () {
    Route::get('/', [GuruController::class, 'index']);
    Route::get('/{id}', [GuruController::class, 'show']);
    Route::post('/', [GuruController::class, 'store']);
    Route::put('/{id}', [GuruController::class, 'update']);
    Route::delete('/{id}', [GuruController::class, 'destroy']);
});

// Siswa Routes
Route::prefix('siswa')->group(function () {
    Route::get('/', [SiswaController::class, 'index']);
    Route::get('/{id}', [SiswaController::class, 'show']);
    Route::post('/', [SiswaController::class, 'store']);
    Route::put('/{id}', [SiswaController::class, 'update']);
    Route::delete('/{id}', [SiswaController::class, 'destroy']);
    Route::get('/kelas/{kelasId}', [SiswaController::class, 'byKelas']);
});

// Kelas Routes
Route::prefix('kelas')->group(function () {
    Route::get('/', [KelasController::class, 'index']);
    Route::get('/{id}', [KelasController::class, 'show']);
    Route::post('/', [KelasController::class, 'store']);
    Route::put('/{id}', [KelasController::class, 'update']);
    Route::delete('/{id}', [KelasController::class, 'destroy']);
});

// Mata Pelajaran Routes
Route::prefix('mata-pelajaran')->group(function () {
    Route::get('/', [MataPelajaranController::class, 'index']);
    Route::get('/{id}', [MataPelajaranController::class, 'show']);
    Route::post('/', [MataPelajaranController::class, 'store']);
    Route::put('/{id}', [MataPelajaranController::class, 'update']);
    Route::delete('/{id}', [MataPelajaranController::class, 'destroy']);
});

// Jadwal Routes
Route::prefix('jadwal')->group(function () {
    Route::get('/', [JadwalController::class, 'index']);
    Route::get('/{id}', [JadwalController::class, 'show']);
    Route::post('/', [JadwalController::class, 'store']);
    Route::put('/{id}', [JadwalController::class, 'update']);
    Route::delete('/{id}', [JadwalController::class, 'destroy']);
    Route::get('/kelas/{kelasId}', [JadwalController::class, 'byKelas']);
    Route::get('/guru/{guruId}', [JadwalController::class, 'byGuru']);
    Route::get('/hari/{hari}', [JadwalController::class, 'byHari']);
    // New endpoints
    Route::get('/kelas/{kelasId}/hari/{hari}', [JadwalController::class, 'getJadwalByKelasHari']);
    Route::get('/detail/kelas/{kelasId}/hari/{hari}', [JadwalController::class, 'getJadwalDetailByKelasHari']);

    // Filter endpoints untuk Kurikulum Activity
    Route::post('/filter/kelas-by-hari', [JadwalController::class, 'filterKelasByHari']);
    Route::post('/filter/guru-by-hari-kelas', [JadwalController::class, 'filterGuruByHariKelas']);
    Route::post('/filter/mapel-by-hari-kelas-guru', [JadwalController::class, 'filterMapelByHariKelasGuru']);
    Route::post('/get-jam-ke', [JadwalController::class, 'getJamKe']);

    // Filter endpoint untuk SiswaActivity - Jadwal Page
    Route::post('/by-hari-kelas', [JadwalController::class, 'filterJadwalByHariKelas']);

    // Flexible filter endpoints (mendukung filter Semua)
    Route::post('/filter-flexible', [JadwalController::class, 'filterJadwalFlexible']);
    Route::post('/filter-flexible-detail', [JadwalController::class, 'filterJadwalFlexibleDetail']);

    // My Schedule endpoint for Guru
    Route::get('/jadwal/my-schedule', [JadwalController::class, 'getMySchedule']);
});

// Kehadiran Routes
Route::prefix('kehadiran')->group(function () {
    Route::get('/', [KehadiranController::class, 'index']);
    Route::get('/{id}', [KehadiranController::class, 'show']);
    Route::post('/', [KehadiranController::class, 'store']);
    Route::put('/{id}', [KehadiranController::class, 'update']);
    Route::delete('/{id}', [KehadiranController::class, 'destroy']);
    Route::get('/siswa/{siswaId}', [KehadiranController::class, 'bySiswa']);
    Route::get('/jadwal/{jadwalId}/tanggal/{tanggal}', [KehadiranController::class, 'byJadwalTanggal']);
    Route::get('/rekap/siswa/{siswaId}', [KehadiranController::class, 'rekapBySiswa']);

    // New endpoints for Siswa attendance tracking
    Route::post('/by-class-date', [KehadiranController::class, 'byClassDate']);
    Route::get('/siswa-by-user-class', [KehadiranController::class, 'getSiswaByUserClass']);
    Route::post('/general', [KehadiranController::class, 'createGeneralKehadiran']);
});

// Compatibility/alias routes for older clients or alternative route names
// (Some Android clients call `/api/kehadiran-guru`; provide full CRUD support)
Route::prefix('kehadiran-guru')->group(function () {
    Route::get('/', [KehadiranGuruController::class, 'index']);
    Route::get('/{id}', [KehadiranGuruController::class, 'show']);
    Route::post('/', [KehadiranGuruController::class, 'store']);
    Route::put('/{id}', [KehadiranGuruController::class, 'update']);
    Route::delete('/{id}', [KehadiranGuruController::class, 'destroy']);
    Route::get('/guru/{guruId}', [KehadiranGuruController::class, 'byGuru']);
    Route::get('/rekap/guru/{guruId}', [KehadiranGuruController::class, 'rekapByGuru']);
});

// Guru Mengajar Routes (Teacher Schedule Assignment)
Route::prefix('guru-mengajar')->group(function () {
    Route::get('/hari/{hari}/kelas/{kelasId}', [GuruMengajarController::class, 'getByHariKelas']);
    Route::get('/tidak-masuk/hari/{hari}/kelas/{kelasId}', [GuruMengajarController::class, 'getGuruTidakMasuk']);
    Route::post('/', [GuruMengajarController::class, 'store']);
    Route::put('/', [GuruMengajarController::class, 'update']);
    Route::put('/{id}', [GuruMengajarController::class, 'updateById']);
    Route::post('/by-hari-kelas', [GuruMengajarController::class, 'getByHariKelasPost']);
    Route::post('/tidak-masuk', [GuruMengajarController::class, 'getGuruTidakMasukPost']);
});

// Guru Pengganti Routes (Substitute Teacher)
Route::prefix('guru-pengganti')->group(function () {
    Route::get('/', [GuruPenggantiController::class, 'index']);
    Route::get('/{id}', [GuruPenggantiController::class, 'show']);
    // Filter substitutes by guru id (either guru_asli_id or guru_pengganti_id)
    Route::get('/filter-by-guru/{guruId}', [GuruPenggantiController::class, 'filterByGuru']);
    Route::post('/', [GuruPenggantiController::class, 'store']);
    Route::put('/{id}', [GuruPenggantiController::class, 'update']);
    Route::delete('/{id}', [GuruPenggantiController::class, 'destroy']);
});

// Izin Guru Routes (Teacher Leave Requests)
Route::prefix('izin-guru')->group(function () {
    Route::get('/', [IzinGuruController::class, 'index']);
    Route::get('/{id}', [IzinGuruController::class, 'show']);
    Route::post('/', [IzinGuruController::class, 'store']);
    Route::put('/{id}', [IzinGuruController::class, 'update']);
    Route::delete('/{id}', [IzinGuruController::class, 'destroy']);
    Route::get('/guru/{guruId}', [IzinGuruController::class, 'getByGuru']);
    Route::post('/filter-by-day', [IzinGuruController::class, 'filterByDay']);
});
