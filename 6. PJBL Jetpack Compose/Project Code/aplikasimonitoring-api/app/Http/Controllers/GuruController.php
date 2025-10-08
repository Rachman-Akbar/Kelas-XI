<?php

namespace App\Http\Controllers;

use App\Models\Guru;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class GuruController extends Controller
{
    /**
     * Display a listing of gurus
     */
    public function index(Request $request)
    {
        try {
            $query = Guru::query();

            // Filter by status
            if ($request->has('status')) {
                $query->where('status', $request->status);
            }

            // Search
            if ($request->has('search')) {
                $search = $request->search;
                $query->where(function ($q) use ($search) {
                    $q->where('nama', 'like', "%{$search}%")
                        ->orWhere('nip', 'like', "%{$search}%")
                        ->orWhere('email', 'like', "%{$search}%");
                });
            }

            $gurus = $query->with(['jadwals', 'kelasWali'])->paginate($request->get('per_page', 15));

            return response()->json([
                'success' => true,
                'message' => 'Data guru berhasil diambil',
                'data' => $gurus
            ], 200);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    /**
     * Store a newly created guru
     */
    public function store(Request $request)
    {
        try {
            $validator = Validator::make($request->all(), [
                'nip' => 'required|string|unique:gurus,nip',
                'nama' => 'required|string|max:255',
                'email' => 'required|email|unique:gurus,email',
                'no_telp' => 'nullable|string|max:20',
                'alamat' => 'nullable|string',
                'jenis_kelamin' => 'required|in:L,P',
                'tanggal_lahir' => 'nullable|date',
                'foto' => 'nullable|string',
                'status' => 'nullable|in:aktif,non-aktif',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $guru = Guru::create($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Guru berhasil ditambahkan',
                'data' => $guru
            ], 201);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    /**
     * Display the specified guru
     */
    public function show($id)
    {
        try {
            $guru = Guru::with(['jadwals.mataPelajaran', 'jadwals.kelas', 'kelasWali'])->find($id);

            if (!$guru) {
                return response()->json([
                    'success' => false,
                    'message' => 'Guru tidak ditemukan',
                    'data' => null
                ], 404);
            }

            return response()->json([
                'success' => true,
                'message' => 'Data guru berhasil diambil',
                'data' => $guru
            ], 200);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    /**
     * Update the specified guru
     */
    public function update(Request $request, $id)
    {
        try {
            $guru = Guru::find($id);

            if (!$guru) {
                return response()->json([
                    'success' => false,
                    'message' => 'Guru tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $validator = Validator::make($request->all(), [
                'nip' => 'sometimes|string|unique:gurus,nip,' . $id,
                'nama' => 'sometimes|string|max:255',
                'email' => 'sometimes|email|unique:gurus,email,' . $id,
                'no_telp' => 'nullable|string|max:20',
                'alamat' => 'nullable|string',
                'jenis_kelamin' => 'sometimes|in:L,P',
                'tanggal_lahir' => 'nullable|date',
                'foto' => 'nullable|string',
                'status' => 'nullable|in:aktif,non-aktif',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $guru->update($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Guru berhasil diupdate',
                'data' => $guru
            ], 200);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    /**
     * Remove the specified guru
     */
    public function destroy($id)
    {
        try {
            $guru = Guru::find($id);

            if (!$guru) {
                return response()->json([
                    'success' => false,
                    'message' => 'Guru tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $guru->delete();

            return response()->json([
                'success' => true,
                'message' => 'Guru berhasil dihapus',
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
