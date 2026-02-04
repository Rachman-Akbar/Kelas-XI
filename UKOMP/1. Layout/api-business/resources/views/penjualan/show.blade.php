@extends('layouts.master')

@section('title', 'Invoice #' . $sale->order_number)

@section('content')
<div class="container">
    <div class="mb-4">
        <div class="d-flex justify-content-between align-items-center">
            <a href="{{ route('penjualan.index') }}" class="btn btn-outline-secondary"><i class="fas fa-arrow-left me-2"></i>Kembali</a>
            <div class="d-flex gap-2">
                <button onclick="window.print()" class="btn btn-primary"><i class="fas fa-print me-2"></i>Cetak</button>
                @if($sale->payment_status !== 'paid')
                <a href="{{ route('penjualan.edit', $sale->id) }}" class="btn btn-warning"><i class="fas fa-edit me-2"></i>Edit</a>
                @endif
            </div>
        </div>
    </div>

    @if(session('success'))
    <div class="alert alert-success alert-dismissible fade show">
        <i class="fas fa-check-circle me-2"></i>{{ session('success') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    @endif

    <!-- Invoice -->
    <div class="card border-0 shadow-lg">
        <div class="card-body p-5">
            <!-- Header -->
            <div class="row mb-5">
                <div class="col-md-6">
                    <h1 class="fw-bold mb-3">INVOICE</h1>
                    <h4 class="text-primary fw-bold">{{ $sale->order_number }}</h4>
                    <p class="text-muted mb-0">Tanggal: {{ $sale->order_date->format('d F Y H:i') }}</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <h3 class="fw-bold text-primary">SI-UMKM</h3>
                    <p class="mb-0">Sistem Informasi UMKM</p>
                    <p class="mb-0 text-muted">Kasir: {{ $sale->user->name }}</p>
                </div>
            </div>

            <!-- Customer Info -->
            <div class="row mb-4">
                <div class="col-md-6">
                    <h6 class="text-muted mb-2">CUSTOMER</h6>
                    @if($sale->customer)
                        <p class="mb-0 fw-bold">{{ $sale->customer->nama }}</p>
                        <p class="mb-0">{{ $sale->customer->no_telepon }}</p>
                        <p class="mb-0 small text-muted">{{ $sale->customer->alamat }}</p>
                    @else
                        <p class="mb-0 text-muted">Walk-in Customer</p>
                    @endif
                </div>
                <div class="col-md-6 text-md-end">
                    <h6 class="text-muted mb-2">STATUS</h6>
                    @if($sale->payment_status === 'paid')
                        <span class="badge bg-success fs-6">LUNAS</span>
                    @elseif($sale->payment_status === 'partial')
                        <span class="badge bg-warning fs-6">DIBAYAR SEBAGIAN</span>
                    @else
                        <span class="badge bg-danger fs-6">BELUM DIBAYAR</span>
                    @endif
                    <p class="mt-2 mb-0">Metode: <span class="fw-bold">{{ strtoupper($sale->payment_method) }}</span></p>
                </div>
            </div>

            <!-- Items Table -->
            <div class="table-responsive mb-4">
                <table class="table table-bordered">
                    <thead class="bg-light">
                        <tr>
                            <th>No</th>
                            <th>Produk</th>
                            <th class="text-center">Jumlah</th>
                            <th class="text-end">Harga</th>
                            <th class="text-end">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($sale->details as $index => $detail)
                        <tr>
                            <td>{{ $index + 1 }}</td>
                            <td>
                                <div class="fw-semibold">{{ $detail->product->nama }}</div>
                                <small class="text-muted">{{ $detail->product->sku }}</small>
                            </td>
                            <td class="text-center">{{ number_format($detail->quantity) }}</td>
                            <td class="text-end">Rp {{ number_format($detail->unit_price, 0, ',', '.') }}</td>
                            <td class="text-end fw-semibold">Rp {{ number_format($detail->subtotal, 0, ',', '.') }}</td>
                        </tr>
                        @endforeach
                    </tbody>
                    <tfoot>
                        <tr class="bg-light">
                            <td colspan="4" class="text-end fw-bold">Subtotal:</td>
                            <td class="text-end fw-bold">Rp {{ number_format($sale->subtotal, 0, ',', '.') }}</td>
                        </tr>
                        @if($sale->discount > 0)
                        <tr>
                            <td colspan="4" class="text-end">Diskon:</td>
                            <td class="text-end text-danger">- Rp {{ number_format($sale->discount, 0, ',', '.') }}</td>
                        </tr>
                        @endif
                        <tr class="bg-primary text-white">
                            <td colspan="4" class="text-end fw-bold fs-5">TOTAL:</td>
                            <td class="text-end fw-bold fs-5">Rp {{ number_format($sale->total, 0, ',', '.') }}</td>
                        </tr>
                        <tr>
                            <td colspan="4" class="text-end">Dibayar:</td>
                            <td class="text-end fw-semibold text-success">Rp {{ number_format($sale->paid_amount, 0, ',', '.') }}</td>
                        </tr>
                        @if($sale->total - $sale->paid_amount > 0)
                        <tr class="bg-warning">
                            <td colspan="4" class="text-end fw-bold">Sisa Tagihan:</td>
                            <td class="text-end fw-bold">Rp {{ number_format($sale->total - $sale->paid_amount, 0, ',', '.') }}</td>
                        </tr>
                        @endif
                    </tfoot>
                </table>
            </div>

            <!-- Notes -->
            @if($sale->notes)
            <div class="alert alert-info">
                <strong>Catatan:</strong> {{ $sale->notes }}
            </div>
            @endif

            <!-- Footer -->
            <div class="text-center mt-5 pt-4 border-top">
                <p class="text-muted mb-0">Terima kasih atas pembelian Anda!</p>
                <p class="text-muted small">Invoice ini dibuat otomatis oleh sistem SI-UMKM</p>
            </div>
        </div>
    </div>
</div>

<style>
@media print {
    .btn, .alert { display: none !important; }
    body { background: white !important; }
}
</style>
@endsection
