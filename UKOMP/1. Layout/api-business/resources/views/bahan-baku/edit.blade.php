@extends('layouts.master')

@section('title', 'Edit Bahan Baku')

@section('content')
<div class="page-header">
    <div class="row align-items-center">
        <div class="col">
            <h1><i class="fas fa-box me-2"></i>Edit Bahan Baku</h1>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="/dashboard">Dashboard</a></li>
                    <li class="breadcrumb-item"><a href="/bahan-baku">Bahan Baku</a></li>
                    <li class="breadcrumb-item active">Edit</li>
                </ol>
            </nav>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Form Edit Bahan Baku</h5>
            </div>
            <div class="card-body">
                <form action="/bahan-baku/1" method="POST">
                    <div class="mb-3">
                        <label for="nama" class="form-label">Nama Bahan Baku <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="nama" name="nama" value="Tepung Terigu" required>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="satuan" class="form-label">Satuan <span class="text-danger">*</span></label>
                            <select class="form-select" id="satuan" name="satuan" required>
                                <option value="">Pilih Satuan</option>
                                <option value="kg" selected>Kilogram (kg)</option>
                                <option value="gram">Gram (g)</option>
                                <option value="liter">Liter (l)</option>
                                <option value="ml">Mililiter (ml)</option>
                                <option value="pcs">Pieces (pcs)</option>
                                <option value="butir">Butir</option>
                                <option value="pack">Pack</option>
                            </select>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="harga" class="form-label">Harga per Satuan <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text">Rp</span>
                                <input type="number" class="form-control" id="harga" name="harga" value="12000" required>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="stok" class="form-label">Stok Saat Ini <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="stok" name="stok" value="5" required>
                            <small class="text-muted">Stok terakhir: 5 kg</small>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="min_stok" class="form-label">Minimum Stok (Alert)</label>
                            <input type="number" class="form-control" id="min_stok" name="min_stok" value="10" placeholder="0">
                            <small class="text-muted">Sistem akan memberi peringatan jika stok di bawah nilai ini</small>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="supplier" class="form-label">Supplier</label>
                        <input type="text" class="form-control" id="supplier" name="supplier" value="PT Tepung Jaya">
                    </div>
                    
                    <div class="mb-3">
                        <label for="keterangan" class="form-label">Keterangan</label>
                        <textarea class="form-control" id="keterangan" name="keterangan" rows="3">Tepung terigu premium untuk produksi kue</textarea>
                    </div>
                    
                    <div class="d-flex justify-content-between">
                        <a href="/bahan-baku" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Kembali
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-2"></i>Update
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-history me-2"></i>Riwayat Stok</h5>
            </div>
            <div class="card-body">
                <div class="list-group list-group-flush">
                    <div class="list-group-item px-0">
                        <div class="d-flex justify-content-between">
                            <small class="fw-bold">Restock</small>
                            <small class="text-muted">20/01/2026</small>
                        </div>
                        <small class="text-success">+50 kg</small>
                    </div>
                    <div class="list-group-item px-0">
                        <div class="d-flex justify-content-between">
                            <small class="fw-bold">Produksi #123</small>
                            <small class="text-muted">18/01/2026</small>
                        </div>
                        <small class="text-danger">-25 kg</small>
                    </div>
                    <div class="list-group-item px-0">
                        <div class="d-flex justify-content-between">
                            <small class="fw-bold">Produksi #122</small>
                            <small class="text-muted">15/01/2026</small>
                        </div>
                        <small class="text-danger">-20 kg</small>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="card mt-3 border-danger">
            <div class="card-header bg-danger text-white">
                <h6 class="mb-0"><i class="fas fa-exclamation-triangle me-2"></i>Peringatan</h6>
            </div>
            <div class="card-body">
                <p class="small mb-0 text-danger">Stok saat ini (5 kg) di bawah minimum stok (10 kg). Segera lakukan restock!</p>
            </div>
        </div>
    </div>
</div>
@endsection
