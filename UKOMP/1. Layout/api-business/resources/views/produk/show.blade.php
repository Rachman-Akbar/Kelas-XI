@extends('layouts.master')

@section('title', 'Detail Produk')

@section('content')
<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="mb-1">Detail Produk</h3>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item"><a href="{{ route('dashboard') }}">Dashboard</a></li>
                    <li class="breadcrumb-item"><a href="{{ route('produk.index') }}">Produk</a></li>
                    <li class="breadcrumb-item active">{{ $product->nama }}</li>
                </ol>
            </nav>
        </div>
        <div>
            <div class="btn-group">
                <a href="{{ route('produk.edit', $product->id) }}" class="btn btn-warning">
                    <i class="fas fa-edit me-1"></i> Edit
                </a>
                <button type="button" class="btn btn-danger" onclick="deleteProduct({{ $product->id }})">
                    <i class="fas fa-trash me-1"></i> Hapus
                </button>
                <a href="{{ route('produk.index') }}" class="btn btn-secondary">
                    <i class="fas fa-arrow-left me-1"></i> Kembali
                </a>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- Info Produk -->
        <div class="col-lg-4">
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Informasi Produk</h5>
                </div>
                <div class="card-body">
                    <table class="table table-sm mb-0">
                        <tr>
                            <th style="width: 40%;">SKU</th>
                            <td><code>{{ $product->sku }}</code></td>
                        </tr>
                        <tr>
                            <th>Nama Produk</th>
                            <td><strong>{{ $product->nama }}</strong></td>
                        </tr>
                        <tr>
                            <th>Kategori</th>
                            <td><span class="badge bg-info">{{ ucfirst($product->kategori) }}</span></td>
                        </tr>
                        <tr>
                            <th>Status</th>
                            <td>
                                @if($product->status == 'aktif')
                                    <span class="badge bg-success">Aktif</span>
                                @else
                                    <span class="badge bg-secondary">Nonaktif</span>
                                @endif
                            </td>
                        </tr>
                        <tr>
                            <th>Satuan</th>
                            <td>{{ $product->satuan }}</td>
                        </tr>
                        <tr>
                            <th>Deskripsi</th>
                            <td>{{ $product->deskripsi ?? '-' }}</td>
                        </tr>
                    </table>
                </div>
            </div>

            <div class="card border-0 shadow-sm mb-4">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-money-bill-wave me-2"></i>Harga & HPP</h5>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <small class="text-muted d-block">Harga Jual</small>
                        <h4 class="mb-0 text-success">Rp {{ number_format($product->harga_jual, 0, ',', '.') }}</h4>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">HPP (Harga Pokok Penjualan)</small>
                        <h5 class="mb-0">Rp {{ number_format($product->hpp, 0, ',', '.') }}</h5>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Laba Per Unit</small>
                        <h5 class="mb-0">Rp {{ number_format($product->profit_per_unit, 0, ',', '.') }}</h5>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Margin Keuntungan</small>
                        <h4 class="mb-0">
                            @if($product->profit_margin > 0)
                                <span class="text-success">{{ number_format($product->profit_margin, 1) }}%</span>
                            @else
                                <span class="text-danger">{{ number_format($product->profit_margin, 1) }}%</span>
                            @endif
                        </h4>
                    </div>
                    <div>
                        <small class="text-muted d-block">Markup Harga</small>
                        <h5 class="mb-0">{{ number_format($product->markup_percentage, 1) }}%</h5>
                    </div>
                </div>
            </div>

            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-boxes me-2"></i>Stok</h5>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <small class="text-muted d-block">Stok Saat Ini</small>
                        <h3 class="mb-0">{{ $product->stok }} {{ $product->satuan }}</h3>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Stok Minimum</small>
                        <h5 class="mb-0">{{ $product->stok_minimum }} {{ $product->satuan }}</h5>
                    </div>
                    <div>
                        <small class="text-muted d-block">Status Stok</small>
                        <h5 class="mb-0">
                            <span class="badge {{ $product->stock_status_color }}">
                                {{ ucfirst($product->stock_status) }}
                            </span>
                        </h5>
                    </div>
                </div>
            </div>
        </div>

        <!-- Komposisi & Stats -->
        <div class="col-lg-8">
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-header bg-white d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="fas fa-list-alt me-2"></i>Komposisi Bahan</h5>
                    <button type="button" class="btn btn-sm btn-info" onclick="recalculateHPP()">
                        <i class="fas fa-calculator me-1"></i> Hitung Ulang HPP
                    </button>
                </div>
                <div class="card-body">
                    @if($compositions->count() > 0)
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th style="width: 5%;">No</th>
                                        <th style="width: 35%;">Nama Bahan</th>
                                        <th style="width: 15%;" class="text-end">Jumlah</th>
                                        <th style="width: 15%;" class="text-end">Harga Satuan</th>
                                        <th style="width: 15%;" class="text-end">Total Biaya</th>
                                        <th style="width: 15%;" class="text-center">Status Bahan</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    @foreach($compositions as $index => $comp)
                                        <tr>
                                            <td>{{ $index + 1 }}</td>
                                            <td>
                                                <strong>{{ $comp->material->nama }}</strong>
                                                <br><small class="text-muted">{{ $comp->material->kategori }}</small>
                                            </td>
                                            <td class="text-end">
                                                {{ $comp->jumlah }} {{ $comp->material->satuan }}
                                            </td>
                                            <td class="text-end">
                                                Rp {{ number_format($comp->material->harga_per_satuan, 0, ',', '.') }}
                                            </td>
                                            <td class="text-end">
                                                <strong>Rp {{ number_format($comp->biaya, 0, ',', '.') }}</strong>
                                            </td>
                                            <td class="text-center">
                                                <span class="badge {{ $comp->material->stock_status_color }}">
                                                    {{ $comp->material->stok_saat_ini }} {{ $comp->material->satuan }}
                                                </span>
                                            </td>
                                        </tr>
                                    @endforeach
                                </tbody>
                                <tfoot class="table-light">
                                    <tr>
                                        <td colspan="4" class="text-end"><strong>Total HPP</strong></td>
                                        <td class="text-end">
                                            <h5 class="mb-0">Rp {{ number_format($product->hpp, 0, ',', '.') }}</h5>
                                        </td>
                                        <td></td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                        <a href="{{ route('komposisi-produk.index') }}?produk_id={{ $product->id }}" class="btn btn-sm btn-outline-primary">
                            <i class="fas fa-edit me-1"></i> Edit Komposisi
                        </a>
                    @else
                        <div class="text-center py-4">
                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                            <p class="text-muted mb-3">Belum ada komposisi bahan untuk produk ini</p>
                            <a href="{{ route('komposisi-produk.index') }}?produk_id={{ $product->id }}" class="btn btn-primary">
                                <i class="fas fa-plus me-1"></i> Tambah Komposisi
                            </a>
                        </div>
                    @endif
                </div>
            </div>

            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-chart-line me-2"></i>Trend Penjualan (6 Bulan Terakhir)</h5>
                </div>
                <div class="card-body">
                    <canvas id="salesChart" height="80"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>

@push('scripts')
<script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
<script>
// Sales Chart
const salesCtx = document.getElementById('salesChart').getContext('2d');
const salesChart = new Chart(salesCtx, {
    type: 'line',
    data: {
        labels: {!! json_encode($salesTrend['labels']) !!},
        datasets: [{
            label: 'Jumlah Terjual',
            data: {!! json_encode($salesTrend['data']) !!},
            borderColor: 'rgb(75, 192, 192)',
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            tension: 0.4,
            fill: true
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                display: true,
                position: 'top'
            },
            tooltip: {
                callbacks: {
                    label: function(context) {
                        return context.dataset.label + ': ' + context.parsed.y + ' unit';
                    }
                }
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    stepSize: 10
                }
            }
        }
    }
});

function recalculateHPP() {
    if (!confirm('Hitung ulang HPP dari komposisi bahan?\n\nHPP lama akan diganti dengan perhitungan baru.')) {
        return;
    }
    
    fetch("{{ route('produk.calculate-hpp', $product->id) }}", {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': '{{ csrf_token() }}',
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(`HPP berhasil dihitung ulang!\n\nHPP: Rp ${data.hpp.toLocaleString('id-ID')}\nMargin: ${data.margin}%\nMarkup: ${data.markup}%\nLaba/Unit: Rp ${data.profit_per_unit.toLocaleString('id-ID')}`);
            window.location.reload();
        } else {
            alert('Error: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Terjadi kesalahan saat menghitung HPP');
    });
}

function deleteProduct(id) {
    if (confirm('Yakin ingin menghapus produk ini?\n\nProduk yang memiliki transaksi penjualan tidak dapat dihapus.')) {
        fetch(`/produk/${id}`, {
            method: 'DELETE',
            headers: {
                'X-CSRF-TOKEN': '{{ csrf_token() }}',
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                window.location.href = '{{ route("produk.index") }}';
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Terjadi kesalahan saat menghapus produk');
        });
    }
}
</script>
@endpush
@endsection
