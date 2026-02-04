@extends('layouts.master')

@section('title', 'Edit User')

@section('breadcrumb')
<li class="breadcrumb-item"><a href="/user-management">User Management</a></li>
<li class="breadcrumb-item active" aria-current="page">Edit User</li>
@endsection

@section('content')
<div class="page-header">
    <div class="row align-items-center">
        <div class="col">
            <h1><i class="fas fa-user-edit me-2"></i>Edit User</h1>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Form Edit User</h5>
            </div>
            <div class="card-body">
                <form action="/user-management/1" method="POST" enctype="multipart/form-data">
                    <div class="mb-3">
                        <label class="form-label">Foto Profil Saat Ini</label>
                        <div class="d-flex align-items-center gap-3">
                            <div class="avatar bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 80px; height: 80px; font-size: 2rem;">
                                <strong>AD</strong>
                            </div>
                            <div>
                                <input type="file" class="form-control" id="foto" name="foto" accept="image/*">
                                <small class="text-muted">Format: JPG, PNG. Maksimal 2MB. Kosongkan jika tidak ingin mengubah foto</small>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="nama" class="form-label">Nama Lengkap <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="nama" name="nama" value="Admin Utama" required>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email" class="form-control" id="email" name="email" value="admin@business.com" required>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="no_telepon" class="form-label">No. Telepon <span class="text-danger">*</span></label>
                            <input type="tel" class="form-control" id="no_telepon" name="no_telepon" value="081234567890" required>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="username" class="form-label">Username <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="username" name="username" value="admin" required readonly>
                            <small class="text-muted">Username tidak dapat diubah</small>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="role" class="form-label">Role <span class="text-danger">*</span></label>
                            <select class="form-select" id="role" name="role" required>
                                <option value="">Pilih Role</option>
                                <option value="admin" selected>Admin</option>
                                <option value="manager">Manager</option>
                                <option value="staff">Staff</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        <strong>Ubah Password:</strong> Kosongkan field password jika tidak ingin mengubah password
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="password" class="form-label">Password Baru</label>
                            <div class="input-group">
                                <input type="password" class="form-control" id="password" name="password" placeholder="Minimal 8 karakter (opsional)">
                                <button class="btn btn-outline-secondary" type="button" onclick="togglePassword('password')">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </div>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="password_confirmation" class="form-label">Konfirmasi Password Baru</label>
                            <div class="input-group">
                                <input type="password" class="form-control" id="password_confirmation" name="password_confirmation" placeholder="Ulangi password baru">
                                <button class="btn btn-outline-secondary" type="button" onclick="togglePassword('password_confirmation')">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="alamat" class="form-label">Alamat</label>
                        <textarea class="form-control" id="alamat" name="alamat" rows="3">Jl. Contoh No. 123, Jakarta</textarea>
                    </div>
                    
                    <div class="mb-3">
                        <label for="status" class="form-label">Status <span class="text-danger">*</span></label>
                        <select class="form-select" id="status" name="status" required>
                            <option value="aktif" selected>Aktif</option>
                            <option value="non-aktif">Non-Aktif</option>
                        </select>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label">Informasi Terakhir</label>
                        <div class="row">
                            <div class="col-md-6">
                                <small class="text-muted">
                                    <i class="fas fa-calendar me-1"></i>Dibuat: 15/01/2024 10:30
                                </small>
                            </div>
                            <div class="col-md-6">
                                <small class="text-muted">
                                    <i class="fas fa-clock me-1"></i>Login Terakhir: 23/01/2024 10:30
                                </small>
                            </div>
                        </div>
                    </div>
                    
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-2"></i>Simpan Perubahan
                        </button>
                        <button type="reset" class="btn btn-secondary">
                            <i class="fas fa-redo me-2"></i>Reset
                        </button>
                        <a href="/user-management" class="btn btn-light">
                            <i class="fas fa-times me-2"></i>Batal
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card">
            <div class="card-header">
                <h6 class="mb-0"><i class="fas fa-history me-2"></i>Aktivitas User</h6>
            </div>
            <div class="card-body">
                <ul class="timeline small">
                    <li class="mb-2">
                        <i class="fas fa-sign-in-alt text-success"></i>
                        <strong>Login</strong>
                        <br>23/01/2024 10:30
                    </li>
                    <li class="mb-2">
                        <i class="fas fa-edit text-primary"></i>
                        <strong>Edit Bahan Baku</strong>
                        <br>23/01/2024 09:15
                    </li>
                    <li class="mb-2">
                        <i class="fas fa-plus text-info"></i>
                        <strong>Tambah Produk</strong>
                        <br>22/01/2024 16:20
                    </li>
                    <li class="mb-2">
                        <i class="fas fa-file-alt text-warning"></i>
                        <strong>Lihat Laporan</strong>
                        <br>22/01/2024 14:30
                    </li>
                </ul>
            </div>
        </div>
        
        <div class="card mt-3">
            <div class="card-header bg-danger text-white">
                <h6 class="mb-0"><i class="fas fa-exclamation-triangle me-2"></i>Zona Berbahaya</h6>
            </div>
            <div class="card-body">
                <p class="small mb-2">Aksi berikut tidak dapat dibatalkan:</p>
                <button type="button" class="btn btn-danger btn-sm w-100" onclick="confirm('Yakin ingin menghapus user ini?')">
                    <i class="fas fa-trash me-2"></i>Hapus User
                </button>
            </div>
        </div>
    </div>
</div>

<script>
function togglePassword(id) {
    const input = document.getElementById(id);
    const icon = event.currentTarget.querySelector('i');
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}
</script>

@endsection
