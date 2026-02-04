@extends('layouts.master')

@section('title', 'Breakdown Komposisi Produk')

@section('content')
<div class="page-header">
    <div class="row align-items-center">
        <div class="col">
            <h1><i class="fas fa-chart-pie me-2"></i>Breakdown Komposisi Produk</h1>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="/dashboard">Dashboard</a></li>
                    <li class="breadcrumb-item"><a href="/komposisi-produk">Komposisi Produk</a></li>
                    <li class="breadcrumb-item active">Kue Brownies Coklat</li>
                </ol>
            </nav>
        </div>
        <div class="col-auto">
            <div class="btn-group">
                <button class="btn btn-success">
                    <i class="fas fa-file-excel me-2"></i>Export
                </button>
                <button class="btn btn-primary">
                    <i class="fas fa-print me-2"></i>Print
                </button>
                <a href="/komposisi-produk" class="btn btn-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Kembali
                </a>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <!-- Product Info -->
    <div class="col-lg-4 mb-4">
        <div class="card shadow">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0"><i class="fas fa-cookie-bite me-2"></i>Informasi Produk</h5>
            </div>
            <div class="card-body text-center">
                <div class="bg-light p-4 rounded mb-3">
                    <i class="fas fa-cookie-bite fa-5x text-primary"></i>
                </div>
                <h4>Kue Brownies Coklat</h4>
                <p class="text-muted">SKU: PRD-001</p>
                
                <table class="table table-sm table-borderless mt-3">
                    <tr>
                        <td class="text-start"><strong>Kategori:</strong></td>
                        <td class="text-end">Kue Basah</td>
                    </tr>
                    <tr>
                        <td class="text-start"><strong>Jumlah Produksi:</strong></td>
                        <td class="text-end">1 unit</td>
                    </tr>
                    <tr>
                        <td class="text-start"><strong>Berat:</strong></td>
                        <td class="text-end">500 gram</td>
                    </tr>
                </table>
            </div>
        </div>

        <!-- Cost Summary -->
        <div class="card shadow mt-3">
            <div class="card-header bg-warning text-dark">
                <h5 class="mb-0"><i class="fas fa-money-bill-wave me-2"></i>Ringkasan Biaya</h5>
            </div>
            <div class="card-body">
                <div class="mb-3">
                    <div class="border-start border-warning border-4 ps-3 mb-3">
                        <small class="text-muted">Total HPP</small>
                        <h3 class="mb-0 text-warning">Rp 32.350</h3>
                    </div>
                    <div class="border-start border-primary border-4 ps-3 mb-3">
                        <small class="text-muted">Harga Jual</small>
                        <h3 class="mb-0 text-primary">Rp 50.000</h3>
                    </div>
                    <div class="border-start border-success border-4 ps-3">
                        <small class="text-muted">Margin Profit</small>
                        <h3 class="mb-0 text-success">35.3%</h3>
                    </div>
                </div>

                <div class="progress mb-2" style="height: 30px;">
                    <div class="progress-bar bg-warning" style="width: 64.7%">
                        HPP: 64.7%
                    </div>
                    <div class="progress-bar bg-success" style="width: 35.3%">
                        Profit: 35.3%
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Breakdown Detail -->
    <div class="col-lg-8 mb-4">
        <!-- Bahan Baku Table -->
        <div class="card shadow mb-4">
            <div class="card-header bg-success text-white">
                <h5 class="mb-0"><i class="fas fa-list-alt me-2"></i>Detail Komposisi Bahan Baku</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th width="5%">#</th>
                                <th>Nama Bahan</th>
                                <th width="15%">Qty</th>
                                <th width="15%">Harga/Satuan</th>
                                <th width="18%">Subtotal</th>
                                <th width="12%">% HPP</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>1</td>
                                <td>
                                    <i class="fas fa-box text-primary me-2"></i>
                                    <strong>Tepung Terigu</strong>
                                </td>
                                <td>0.3 kg</td>
                                <td>Rp 12.000/kg</td>
                                <td>Rp 3.600</td>
                                <td>
                                    <div class="progress" style="height: 20px;">
                                        <div class="progress-bar bg-info" style="width: 11.1%">11.1%</div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>2</td>
                                <td>
                                    <i class="fas fa-box text-primary me-2"></i>
                                    <strong>Gula Pasir</strong>
                                </td>
                                <td>0.2 kg</td>
                                <td>Rp 15.000/kg</td>
                                <td>Rp 3.000</td>
                                <td>
                                    <div class="progress" style="height: 20px;">
                                        <div class="progress-bar bg-info" style="width: 9.3%">9.3%</div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>3</td>
                                <td>
                                    <i class="fas fa-box text-primary me-2"></i>
                                    <strong>Telur Ayam</strong>
                                </td>
                                <td>5 butir</td>
                                <td>Rp 2.500/butir</td>
                                <td>Rp 12.500</td>
                                <td>
                                    <div class="progress" style="height: 20px;">
                                        <div class="progress-bar bg-warning" style="width: 38.6%">38.6%</div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>4</td>
                                <td>
                                    <i class="fas fa-box text-primary me-2"></i>
                                    <strong>Mentega</strong>
                                </td>
                                <td>0.15 kg</td>
                                <td>Rp 45.000/kg</td>
                                <td>Rp 6.750</td>
                                <td>
                                    <div class="progress" style="height: 20px;">
                                        <div class="progress-bar bg-primary" style="width: 20.9%">20.9%</div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>5</td>
                                <td>
                                    <i class="fas fa-box text-primary me-2"></i>
                                    <strong>Coklat Bubuk</strong>
                                </td>
                                <td>0.1 kg</td>
                                <td>Rp 65.000/kg</td>
                                <td>Rp 6.500</td>
                                <td>
                                    <div class="progress" style="height: 20px;">
                                        <div class="progress-bar bg-primary" style="width: 20.1%">20.1%</div>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                        <tfoot class="table-light">
                            <tr>
                                <th colspan="4">TOTAL HPP</th>
                                <th>Rp 32.350</th>
                                <th>100%</th>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </div>

        <!-- Chart Komposisi -->
        <div class="row">
            <div class="col-md-6 mb-4">
                <div class="card shadow">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0"><i class="fas fa-chart-pie me-2"></i>Distribusi Biaya Bahan</h5>
                    </div>
                    <div class="card-body">
                        <canvas id="costPieChart"></canvas>
                    </div>
                </div>
            </div>

            <div class="col-md-6 mb-4">
                <div class="card shadow">
                    <div class="card-header bg-dark text-white">
                        <h5 class="mb-0"><i class="fas fa-balance-scale me-2"></i>Perbandingan Berat Bahan</h5>
                    </div>
                    <div class="card-body">
                        <canvas id="weightBarChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recommendations -->
        <div class="card shadow">
            <div class="card-header bg-warning text-dark">
                <h5 class="mb-0"><i class="fas fa-lightbulb me-2"></i>Rekomendasi & Catatan</h5>
            </div>
            <div class="card-body">
                <div class="alert alert-info">
                    <h6><i class="fas fa-info-circle me-2"></i>Bahan dengan Biaya Tertinggi</h6>
                    <p class="mb-0">
                        <strong>Telur Ayam</strong> menyumbang 38.6% dari total HPP. 
                        Pertimbangkan untuk mencari supplier dengan harga lebih kompetitif.
                    </p>
                </div>

                <div class="alert alert-success">
                    <h6><i class="fas fa-check-circle me-2"></i>Efisiensi Produksi</h6>
                    <p class="mb-0">
                        Komposisi saat ini menghasilkan margin 35.3%, sudah di atas target minimal 30%. 
                        Produk ini profitable untuk diproduksi.
                    </p>
                </div>

                <div class="alert alert-warning">
                    <h6><i class="fas fa-exclamation-triangle me-2"></i>Saran Optimasi</h6>
                    <ul class="mb-0">
                        <li>Jika harga telur naik 10%, margin akan turun menjadi 31.5%</li>
                        <li>Pertimbangkan pembelian bahan baku dalam jumlah besar untuk mendapat diskon</li>
                        <li>Monitor harga mentega dan coklat bubuk yang juga cukup signifikan (20%+ masing-masing)</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
// Pie Chart - Cost Distribution
var ctx1 = document.getElementById('costPieChart').getContext('2d');
var costPieChart = new Chart(ctx1, {
    type: 'doughnut',
    data: {
        labels: ['Telur (38.6%)', 'Mentega (20.9%)', 'Coklat (20.1%)', 'Tepung (11.1%)', 'Gula (9.3%)'],
        datasets: [{
            data: [12500, 6750, 6500, 3600, 3000],
            backgroundColor: ['#f6c23e', '#4e73df', '#36b9cc', '#1cc88a', '#858796']
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                position: 'bottom'
            }
        }
    }
});

// Bar Chart - Weight Comparison
var ctx2 = document.getElementById('weightBarChart').getContext('2d');
var weightBarChart = new Chart(ctx2, {
    type: 'bar',
    data: {
        labels: ['Tepung', 'Gula', 'Telur', 'Mentega', 'Coklat'],
        datasets: [{
            label: 'Berat/Qty',
            data: [0.3, 0.2, 5, 0.15, 0.1],
            backgroundColor: '#1cc88a'
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                display: false
            }
        },
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});
</script>
@endsection
