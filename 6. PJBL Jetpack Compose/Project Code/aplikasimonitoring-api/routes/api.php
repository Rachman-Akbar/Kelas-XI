<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\GuruController;
use App\Http\Controllers\SiswaController;
use App\Http\Controllers\MataPelajaranController;
use App\Http\Controllers\KelasController;
use App\Http\Controllers\JadwalController;
use App\Http\Controllers\KehadiranController;

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

// Test Connection
Route::get('/test', function () {
    return response()->json([
        'success' => true,
        'message' => 'API Connected Successfully!',
        'data' => 'Laravel API is running'
    ]);
});

// Authentication Routes
Route::post('/login', [AuthController::class, 'login']);
Route::post('/register', [AuthController::class, 'register']);

// Protected Routes (requires authentication)
Route::middleware('auth:sanctum')->group(function () {
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/user', function (Request $request) {
        return response()->json([
            'success' => true,
            'message' => 'User retrieved successfully',
            'data' => $request->user()
        ]);
    });

    // User Management
    Route::get('/users', [UserController::class, 'index']);
    Route::get('/users/{id}', [UserController::class, 'show']);
    Route::post('/users', [UserController::class, 'store']);
    Route::put('/users/{id}', [UserController::class, 'update']);
    Route::delete('/users/{id}', [UserController::class, 'destroy']);

    // Guru Management
    Route::apiResource('gurus', GuruController::class);

    // Siswa Management
    Route::apiResource('siswas', SiswaController::class);

    // Mata Pelajaran Management
    Route::apiResource('mata-pelajarans', MataPelajaranController::class);

    // Kelas Management
    Route::apiResource('kelas', KelasController::class);

    // Jadwal Management
    Route::apiResource('jadwals', JadwalController::class);

    // Kehadiran Management
    Route::apiResource('kehadirans', KehadiranController::class);
});
