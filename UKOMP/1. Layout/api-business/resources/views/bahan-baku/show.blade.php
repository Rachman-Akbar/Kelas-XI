@extends('layouts.master')

@section('title', 'Detail Bahan Baku')

@section('content')
<div class="page-header">
    <div class="row align-items-center">
        <div class="col">
            <h1><i class="fas fa-box me-2"></i>Detail Bahan Baku</h1>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="/dashboard">Dashboard</a></li>
                    <li class="breadcrumb-item"><a href="/bahan-baku">Bahan Baku</a></li>
                    <li class="breadcrumb-item active">Detail</li>
                </ol>
            </nav>
        </div>
        <div class="col-auto">
            <div class="btn-group">
                <a href="/bahan-baku/1/edit" class="btn btn-warning">
                    <i class="fas fa-edit me-2"></i>Edit
                </a>
                <button class="btn btn-danger" onclick="confirm('Hapus bahan baku ini?')">
                    <i class="fas fa-trash me-2"></i>Hapus
                </button>
                <a href="/bahan-baku" class="btn btn-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Kembali
                </a>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-lg-4 mb-4">
        <div class="card shadow">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Informasi Bahan</h5>
            </div>
            <div class="card-body">
                <div class="text-center mb-4">
                    <div class="bg-light p-4 rounded">
                        <i class="fas fa-box fa-5x text-primary"></i>
                    </div>
                </div>
                
                <table class="table table-borderless">
                    <tr>
                        <th width="40%">Kode Bahan</th>
                        <td><strong>BB-001</strong></td>
                    </tr>
                    <tr>
                        <th>Nama Bahan</th>
                        <td><strong>Tepung Terigu</strong></td>
                    </tr>
                    <tr>
                        <th>Kategori</th>
                        <td><span class="badge bg-info">Bahan Utama</span></td>
                    </tr>
                    <tr>
                        <th>Satuan</th>
                        <td>Kilogram (kg)</td>
                    </tr>
                    <tr>
                        <th>Harga/Satuan</th>
                        <td><strong class="text-primary">Rp 12.000</strong></td>
                    </tr>
                    <tr>
                        <th>Supplier</th>
                        <td>PT Tepung Jaya</td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <div class="col-lg-8 mb-4">
        <!-- Informasi Stok -->
        <div class="card shadow mb-4">
            <div class="card-header bg-warning text-dark">
                <h5 class="mb-0"><i class="fas fa-boxes me-2"></i>Informasi Stok</h5>
            </div>
            <div class="card-body">
                <div class="row mb-3">
                    <div class="col-md-4">
                        <div class="border-start border-danger border-4 ps-3">
                            <small class="text-muted">Stok Saat Ini</small>
                            <h3 class="mb-0 text-danger">5 kg</h3>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="border-start border-warning border-4 ps-3">
                            <small class="text-muted">Stok Minimum</small>
                            <h3 class="mb-0">50 kg</h3>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="border-start border-success border-4 ps-3">
                            <small class="text-muted">Nilai Stok</small>
                            <h3 class="mb-0">Rp 60.000</h3>
                        </div>
                    </div>
                </div>
                
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    <strong>Peringatan!</strong> Stok di bawah minimum (10%). Segera lakukan pemesanan.
                </div>
                
                <div class="progress" style="height: 25px;">
                    <div class="progress-bar bg-danger" style="width: 10%">10%</div>
                </div>
                <small class="text-muted">Persentase stok terhadap minimum</small>
            </div>
        </div>

        <!-- Riwayat Transaksi -->
        <div class="card shadow">
            <div class="card-header bg-info text-white">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="fas fa-history me-2"></i>Riwayat Transaksi (10 Terakhir)</h5>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-light btn-sm">
                            <i class="fas fa-filter"></i> Filter
                        </button>
                        <button class="btn btn-light btn-sm">
                            <i class="fas fa-download"></i> Export
                        </button>
                    </div>
                </div>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover table-sm">
                        <thead class="table-light">
                            <tr>
                                <th>Tanggal</th>
                                <th>Jenis</th>
                                <th>Referensi</th>
                                <th>Qty</th>
                                <th>Harga</th>
                                <th>Stok Akhir</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>21 Jan 2026</td>
                                <td><span class="badge bg-danger">Keluar</span></td>
                                <td>PROD-00125</td>
                                <td>-15 kg</td>
                                <td>Rp 180.000</td>
                                <td>5 kg</td>
                            </tr>
                            <tr>
                                <td>20 Jan 2026</td>
                                <td><span class="badge bg-danger">Keluar</span></td>
                                <td>PROD-00124</td>
                                <td>-10 kg</td>
                                <td>Rp 120.000</td>
                                <td>20 kg</td>
                            </tr>
                            <tr>
                                <td>18 Jan 2026</td>
                                <td><span class="badge bg-success">Masuk</span></td>
                                <td>PO-00521</td>
                                <td>+30 kg</td>
                                <td>Rp 360.000</td>
                                <td>30 kg</td>
                            </tr>
                            <tr>
                                <td>17 Jan 2026</td>
                                <td><span class="badge bg-danger">Keluar</span></td>
                                <td>PROD-00120</td>
                                <td>-25 kg</td>
                                <td>Rp 300.000</td>
                                <td>0 kg</td>
                            </tr>
                            <tr>
                                <td>15 Jan 2026</td>
                                <td><span class="badge bg-success">Masuk</span></td>
                                <td>PO-00515</td>
                                <td>+25 kg</td>
                                <td>Rp 300.000</td>
                                <td>25 kg</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="text-center">
                    <a href="#" class="btn btn-sm btn-outline-primary">Lihat Semua Riwayat</a>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Statistik Penggunaan -->
<div class="row">
    <div class="col-md-6 mb-4">
        <div class="card shadow">
            <div class="card-header bg-success text-white">
                <h5 class="mb-0"><i class="fas fa-chart-line me-2"></i>Grafik Penggunaan Bulanan</h5>
            </div>
            <div class="card-body">
                <canvas id="usageChart" height="100"></canvas>
            </div>
        </div>
    </div>
    
    <div class="col-md-6 mb-4">
        <div class="card shadow">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0"><i class="fas fa-shopping-bag me-2"></i>Produk yang Menggunakan</h5>
            </div>
            <div class="card-body">
                <ul class="list-group list-group-flush">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Roti Tawar
                        <span class="badge bg-primary rounded-pill">0.5 kg/unit</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Kue Brownies
                        <span class="badge bg-primary rounded-pill">0.3 kg/unit</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Kue Lapis
                        <span class="badge bg-primary rounded-pill">0.4 kg/unit</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Donat
                        <span class="badge bg-primary rounded-pill">0.25 kg/unit</span>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
var ctx = document.getElementById('usageChart').getContext('2d');
var usageChart = new Chart(ctx, {
    type: 'line',
    data: {
        labels: ['Nov', 'Dec', 'Jan (s.d. 23)'],
        datasets: [{
            label: 'Penggunaan (kg)',
            data: [120, 135, 90],
            borderColor: '#1cc88a',
            backgroundColor: 'rgba(28, 200, 138, 0.1)',
            tension: 0.4,
            fill: true
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                display: true
            }
        }
    }
});
</script>
@endsection
