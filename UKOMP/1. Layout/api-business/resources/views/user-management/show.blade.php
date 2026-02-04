@extends('layouts.master')

@section('title', 'Detail User')

@section('breadcrumb')
<li class="breadcrumb-item"><a href="/user-management">User Management</a></li>
<li class="breadcrumb-item active" aria-current="page">Detail User</li>
@endsection

@section('content')
<div class="page-header">
    <div class="row align-items-center">
        <div class="col">
            <h1><i class="fas fa-user me-2"></i>Detail User</h1>
        </div>
        <div class="col-auto">
            <div class="btn-group">
                <a href="/user-management/1/edit" class="flex items-center gap-2 bg-primary text-background-dark px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-primary/90 transition-colors">
                    <span class="material-symbols-outlined text-lg">edit</span>
                    Edit User
                </a>
                <a href="/user-management" class="btn btn-light">
                    <i class="fas fa-arrow-left me-2"></i>Kembali
                </a>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-4 mb-4">
        <div class="card text-center">
            <div class="card-body">
                <div class="avatar bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 120px; height: 120px; font-size: 3rem;">
                    <strong>AD</strong>
                </div>
                <h4 class="mb-1">Admin Utama</h4>
                <p class="text-muted mb-2">@admin</p>
                <span class="badge bg-danger mb-3">Admin</span>
                <div class="d-flex justify-content-center gap-2">
                    <span class="badge bg-success">
                        <i class="fas fa-circle me-1" style="font-size: 8px;"></i>Aktif
                    </span>
                </div>
            </div>
        </div>

        <div class="card mt-3">
            <div class="card-header">
                <h6 class="mb-0"><i class="fas fa-info-circle me-2"></i>Informasi Kontak</h6>
            </div>
            <div class="card-body">
                <div class="mb-3">
                    <small class="text-muted d-block">Email</small>
                    <strong><i class="fas fa-envelope me-2 text-primary"></i>admin@business.com</strong>
                </div>
                <div class="mb-3">
                    <small class="text-muted d-block">No. Telepon</small>
                    <strong><i class="fas fa-phone me-2 text-success"></i>081234567890</strong>
                </div>
                <div>
                    <small class="text-muted d-block">Alamat</small>
                    <strong><i class="fas fa-map-marker-alt me-2 text-danger"></i>Jl. Contoh No. 123, Jakarta</strong>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-8">
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-user-shield me-2"></i>Informasi Akun</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <small class="text-muted d-block mb-1">Username</small>
                        <h6 class="mb-0">admin</h6>
                    </div>
                    <div class="col-md-6 mb-3">
                        <small class="text-muted d-block mb-1">Role</small>
                        <h6 class="mb-0"><span class="badge bg-danger">Admin</span></h6>
                    </div>
                    <div class="col-md-6 mb-3">
                        <small class="text-muted d-block mb-1">Status</small>
                        <h6 class="mb-0"><span class="badge bg-success">Aktif</span></h6>
                    </div>
                    <div class="col-md-6 mb-3">
                        <small class="text-muted d-block mb-1">Terakhir Login</small>
                        <h6 class="mb-0">23/01/2024 10:30</h6>
                    </div>
                    <div class="col-md-6 mb-3">
                        <small class="text-muted d-block mb-1">Akun Dibuat</small>
                        <h6 class="mb-0">15/01/2024 10:30</h6>
                    </div>
                    <div class="col-md-6 mb-3">
                        <small class="text-muted d-block mb-1">Terakhir Update</small>
                        <h6 class="mb-0">20/01/2024 14:15</h6>
                    </div>
                </div>
            </div>
        </div>

        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-key me-2"></i>Hak Akses & Permission</h5>
            </div>
            <div class="card-body">
                <h6 class="text-danger mb-3"><i class="fas fa-shield-alt me-2"></i>Admin - Full Access</h6>
                <div class="row">
                    <div class="col-md-6">
                        <h6 class="small fw-bold">Modul Akses:</h6>
                        <ul class="small">
                            <li><i class="fas fa-check text-success me-1"></i>Dashboard</li>
                            <li><i class="fas fa-check text-success me-1"></i>Master Data (Bahan Baku, Produk)</li>
                            <li><i class="fas fa-check text-success me-1"></i>Produksi</li>
                            <li><i class="fas fa-check text-success me-1"></i>Penjualan</li>
                            <li><i class="fas fa-check text-success me-1"></i>Keuangan</li>
                            <li><i class="fas fa-check text-success me-1"></i>Laporan & Analisis</li>
                            <li><i class="fas fa-check text-success me-1"></i>User Management</li>
                            <li><i class="fas fa-check text-success me-1"></i>Pengaturan Sistem</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h6 class="small fw-bold">Permission:</h6>
                        <ul class="small">
                            <li><i class="fas fa-check text-success me-1"></i>Create (Tambah Data)</li>
                            <li><i class="fas fa-check text-success me-1"></i>Read (Lihat Data)</li>
                            <li><i class="fas fa-check text-success me-1"></i>Update (Edit Data)</li>
                            <li><i class="fas fa-check text-success me-1"></i>Delete (Hapus Data)</li>
                            <li><i class="fas fa-check text-success me-1"></i>Export Data</li>
                            <li><i class="fas fa-check text-success me-1"></i>Import Data</li>
                            <li><i class="fas fa-check text-success me-1"></i>Approval Transaksi</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-history me-2"></i>Log Aktivitas Terakhir</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-sm table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>Waktu</th>
                                <th>Aktivitas</th>
                                <th>Modul</th>
                                <th>Detail</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>23/01/2024 10:30</td>
                                <td><span class="badge bg-success">Login</span></td>
                                <td>Authentication</td>
                                <td>Login dari IP: 192.168.1.100</td>
                            </tr>
                            <tr>
                                <td>23/01/2024 09:15</td>
                                <td><span class="badge bg-primary">Edit</span></td>
                                <td>Bahan Baku</td>
                                <td>Update stok Tepung Terigu</td>
                            </tr>
                            <tr>
                                <td>22/01/2024 16:20</td>
                                <td><span class="badge bg-info">Create</span></td>
                                <td>Produk</td>
                                <td>Tambah produk: Kue Coklat Premium</td>
                            </tr>
                            <tr>
                                <td>22/01/2024 14:30</td>
                                <td><span class="badge bg-warning">View</span></td>
                                <td>Laporan</td>
                                <td>Export laporan penjualan</td>
                            </tr>
                            <tr>
                                <td>21/01/2024 11:45</td>
                                <td><span class="badge bg-danger">Delete</span></td>
                                <td>Bahan Baku</td>
                                <td>Hapus bahan baku expired</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="text-center mt-3">
                    <button class="btn btn-sm btn-outline-primary">
                        <i class="fas fa-history me-2"></i>Lihat Semua Aktivitas
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

@endsection
