<?php

namespace App\Http\Controllers;

use App\Models\MataPelajaran;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class MataPelajaranController extends Controller
{
    public function index(Request $request)
    {
        try {
            $query = MataPelajaran::query();

            if ($request->has('kategori')) {
                $query->where('kategori', $request->kategori);
            }

            if ($request->has('status')) {
                $query->where('status', $request->status);
            }

            if ($request->has('search')) {
                $search = $request->search;
                $query->where(function ($q) use ($search) {
                    $q->where('nama', 'like', "%{$search}%")
                        ->orWhere('kode', 'like', "%{$search}%");
                });
            }

            $mataPelajarans = $query->with('jadwals')->paginate($request->get('per_page', 15));

            return response()->json([
                'success' => true,
                'message' => 'Data mata pelajaran berhasil diambil',
                'data' => $mataPelajarans
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
                'kode' => 'required|string|unique:mata_pelajarans,kode',
                'nama' => 'required|string|max:255',
                'deskripsi' => 'nullable|string',
                'sks' => 'nullable|integer|min:0',
                'kategori' => 'nullable|in:wajib,pilihan',
                'status' => 'nullable|in:aktif,non-aktif',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $mataPelajaran = MataPelajaran::create($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Mata pelajaran berhasil ditambahkan',
                'data' => $mataPelajaran
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
            $mataPelajaran = MataPelajaran::with(['jadwals.guru', 'jadwals.kelas'])->find($id);

            if (!$mataPelajaran) {
                return response()->json([
                    'success' => false,
                    'message' => 'Mata pelajaran tidak ditemukan',
                    'data' => null
                ], 404);
            }

            return response()->json([
                'success' => true,
                'message' => 'Data mata pelajaran berhasil diambil',
                'data' => $mataPelajaran
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
            $mataPelajaran = MataPelajaran::find($id);

            if (!$mataPelajaran) {
                return response()->json([
                    'success' => false,
                    'message' => 'Mata pelajaran tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $validator = Validator::make($request->all(), [
                'kode' => 'sometimes|string|unique:mata_pelajarans,kode,' . $id,
                'nama' => 'sometimes|string|max:255',
                'deskripsi' => 'nullable|string',
                'sks' => 'nullable|integer|min:0',
                'kategori' => 'nullable|in:wajib,pilihan',
                'status' => 'nullable|in:aktif,non-aktif',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $mataPelajaran->update($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Mata pelajaran berhasil diupdate',
                'data' => $mataPelajaran
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
            $mataPelajaran = MataPelajaran::find($id);

            if (!$mataPelajaran) {
                return response()->json([
                    'success' => false,
                    'message' => 'Mata pelajaran tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $mataPelajaran->delete();

            return response()->json([
                'success' => true,
                'message' => 'Mata pelajaran berhasil dihapus',
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
