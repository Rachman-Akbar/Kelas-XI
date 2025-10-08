<?php

namespace App\Http\Controllers;

use App\Models\Siswa;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class SiswaController extends Controller
{
    public function index(Request $request)
    {
        try {
            $query = Siswa::query();

            if ($request->has('kelas_id')) {
                $query->where('kelas_id', $request->kelas_id);
            }

            if ($request->has('status')) {
                $query->where('status', $request->status);
            }

            if ($request->has('search')) {
                $search = $request->search;
                $query->where(function ($q) use ($search) {
                    $q->where('nama', 'like', "%{$search}%")
                      ->orWhere('nis', 'like', "%{$search}%")
                      ->orWhere('nisn', 'like', "%{$search}%");
                });
            }

            $siswas = $query->with('kelas')->paginate($request->get('per_page', 15));

            return response()->json([
                'success' => true,
                'message' => 'Data siswa berhasil diambil',
                'data' => $siswas
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
                'nis' => 'required|string|unique:siswas,nis',
                'nisn' => 'nullable|string|unique:siswas,nisn',
                'nama' => 'required|string|max:255',
                'email' => 'nullable|email|unique:siswas,email',
                'no_telp' => 'nullable|string|max:20',
                'alamat' => 'nullable|string',
                'jenis_kelamin' => 'required|in:L,P',
                'tanggal_lahir' => 'nullable|date',
                'foto' => 'nullable|string',
                'kelas_id' => 'nullable|exists:kelas,id',
                'nama_orang_tua' => 'nullable|string',
                'no_telp_orang_tua' => 'nullable|string',
                'status' => 'nullable|in:aktif,lulus,pindah,keluar',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $siswa = Siswa::create($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Siswa berhasil ditambahkan',
                'data' => $siswa->load('kelas')
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
            $siswa = Siswa::with(['kelas', 'kehadirans'])->find($id);

            if (!$siswa) {
                return response()->json([
                    'success' => false,
                    'message' => 'Siswa tidak ditemukan',
                    'data' => null
                ], 404);
            }

            return response()->json([
                'success' => true,
                'message' => 'Data siswa berhasil diambil',
                'data' => $siswa
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
            $siswa = Siswa::find($id);

            if (!$siswa) {
                return response()->json([
                    'success' => false,
                    'message' => 'Siswa tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $validator = Validator::make($request->all(), [
                'nis' => 'sometimes|string|unique:siswas,nis,' . $id,
                'nisn' => 'nullable|string|unique:siswas,nisn,' . $id,
                'nama' => 'sometimes|string|max:255',
                'email' => 'nullable|email|unique:siswas,email,' . $id,
                'no_telp' => 'nullable|string|max:20',
                'alamat' => 'nullable|string',
                'jenis_kelamin' => 'sometimes|in:L,P',
                'tanggal_lahir' => 'nullable|date',
                'foto' => 'nullable|string',
                'kelas_id' => 'nullable|exists:kelas,id',
                'nama_orang_tua' => 'nullable|string',
                'no_telp_orang_tua' => 'nullable|string',
                'status' => 'nullable|in:aktif,lulus,pindah,keluar',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation Error',
                    'errors' => $validator->errors()
                ], 422);
            }

            $siswa->update($request->all());

            return response()->json([
                'success' => true,
                'message' => 'Siswa berhasil diupdate',
                'data' => $siswa->load('kelas')
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
            $siswa = Siswa::find($id);

            if (!$siswa) {
                return response()->json([
                    'success' => false,
                    'message' => 'Siswa tidak ditemukan',
                    'data' => null
                ], 404);
            }

            $siswa->delete();

            return response()->json([
                'success' => true,
                'message' => 'Siswa berhasil dihapus',
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
