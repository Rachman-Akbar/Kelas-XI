@extends('layouts.master')

@section('title', 'Detail Produksi #' . $production->id)

@section('content')
<div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
            <a href="{{ route('produksi.index') }}" class="flex items-center justify-center w-10 h-10 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 rounded-lg transition-colors">
                <span class="material-symbols-outlined text-slate-600 dark:text-slate-300">arrow_back</span>
            </a>
            <div>
                <h1 class="text-2xl font-bold dark:text-white flex items-center gap-3">
                    <span class="material-symbols-outlined text-primary text-3xl">assignment_turned_in</span>
                    Detail Produksi #{{ $production->id }}
                </h1>
                <p class="text-slate-600 dark:text-text-muted mt-1">{{ date('d F Y', strtotime($production->tanggal_produksi)) }}</p>
            </div>
        </div>
        <div class="flex items-center gap-3">
            <a href="{{ route('produksi.edit', $production->id) }}" class="flex items-center gap-2 bg-yellow-500 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-yellow-600 transition-colors">
                <span class="material-symbols-outlined text-lg">edit</span>
                Edit
            </a>
            <button type="button" onclick="deleteProduction()" class="flex items-center gap-2 bg-red-500 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-red-600 transition-colors">
                <span class="material-symbols-outlined text-lg">delete</span>
                Hapus
            </button>
        </div>
    </div>

    @if(session('success'))
    <div class="p-4 bg-green-100 dark:bg-green-500/20 border border-green-200 dark:border-green-700 rounded-lg">
        <div class="flex items-center">
            <span class="material-symbols-outlined text-green-600 dark:text-green-400 mr-2">check_circle</span>
            <span class="text-green-800 dark:text-green-200">{{ session('success') }}</span>
        </div>
    </div>
    @endif

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Production Info -->
        <div class="lg:col-span-2 space-y-6">
            <!-- Product Information -->
            <div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
                <div class="bg-primary text-white p-6 rounded-t-xl">
                    <h5 class="text-lg font-bold flex items-center gap-2">
                        <span class="material-symbols-outlined">inventory_2</span>
                        Produk yang Diproduksi
                    </h5>
                </div>
                <div class="p-6">
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label class="block text-sm text-slate-500 dark:text-slate-400 mb-1">Nama Produk</label>
                            <div class="text-lg font-semibold text-slate-900 dark:text-white">{{ $production->product->nama }}</div>
                            </div>
                        </div>
                        <div>
                            <label class="block text-sm text-slate-500 dark:text-slate-400 mb-1">SKU</label>
                            <div class="text-base text-slate-900 dark:text-white">{{ $production->product->sku }}</div>
                        </div>
                        <div>
                            <label class="block text-sm text-slate-500 dark:text-slate-400 mb-1">Jumlah Produksi</label>
                            <div class="text-2xl font-bold text-primary">{{ number_format($production->quantity) }} unit</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Material Details -->
            <div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
                <div class="bg-slate-50 dark:bg-slate-800 p-6 rounded-t-xl border-b border-slate-200 dark:border-border-dark">
                    <h5 class="text-lg font-bold text-slate-900 dark:text-white flex items-center gap-2">
                        <span class="material-symbols-outlined">list_alt</span>
                        Bahan Baku yang Digunakan
                    </h5>
                </div>
                <div class="p-0">
                    <div class="overflow-x-auto">
                        <table class="w-full">
                            <thead>
                                <tr class="border-b border-slate-200 dark:border-border-dark bg-slate-50 dark:bg-slate-800">
                                    <th class="py-3 px-4 text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">No</th>
                                    <th class="py-3 px-4 text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">Bahan Baku</th>
                                    <th class="py-3 px-4 text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">Jumlah Digunakan</th>
                                    <th class="py-3 px-4 text-right text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">Harga Satuan</th>
                                    <th class="py-3 px-4 text-right text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                                @foreach($production->details as $index => $detail)
                                <tr class="border-b border-slate-200 dark:border-border-dark hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors">
                                    <td class="py-3 px-4 text-slate-900 dark:text-white">{{ $index + 1 }}</td>
                                    <td class="py-3 px-4">
                                        <div class="font-semibold text-slate-900 dark:text-white">{{ $detail->material->nama }}</div>
                                        <p class="text-sm text-slate-600 dark:text-slate-400">{{ $detail->material->kategori }}</p>
                                    </td>
                                    <td class="py-3 px-4">
                                        <span class="inline-flex px-2 py-1 text-xs font-medium rounded-lg bg-cyan-100 text-cyan-800 dark:bg-cyan-500/20 dark:text-cyan-400">{{ number_format($detail->quantity_used, 2) }} {{ $detail->material->satuan }}</span>
                                    </td>
                                    <td class="py-3 px-4 text-right text-slate-900 dark:text-white">Rp {{ number_format($detail->harga_satuan, 0, ',', '.') }}</td>
                                    <td class="py-3 px-4 text-right">
                                        <span class="font-semibold text-slate-900 dark:text-white">Rp {{ number_format($detail->subtotal, 0, ',', '.') }}</span>
                                    </td>
                                </tr>
                                @endforeach
                            </tbody>
                            <tfoot>
                                <tr class="bg-slate-50 dark:bg-slate-800 border-t border-slate-200 dark:border-border-dark">
                                    <td colspan="4" class="py-4 px-4 text-right font-semibold text-slate-900 dark:text-white">Total Biaya Bahan:</td>
                                    <td class="py-4 px-4 text-right font-bold text-primary">Rp {{ number_format($production->total_biaya_bahan, 0, ',', '.') }}</td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Notes -->
            @if($production->catatan)
            <div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
                <div class="bg-slate-50 dark:bg-slate-800 p-6 rounded-t-xl border-b border-slate-200 dark:border-border-dark">
                    <h5 class="text-lg font-bold text-slate-900 dark:text-white flex items-center gap-2">
                        <span class="material-symbols-outlined">sticky_note_2</span>
                        Catatan
                    </h5>
                </div>
                <div class="p-6">
                    <p class="text-slate-900 dark:text-white">{{ $production->catatan }}</p>
                </div>
            </div>
            @endif
        </div>

        <!-- Summary & Info -->
        <div class="col-md-4">
            <!-- Cost Summary -->
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-header bg-success text-white py-3">
                    <h5 class="mb-0"><i class="fas fa-calculator me-2"></i>Ringkasan Biaya</h5>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-between mb-3 pb-3 border-bottom">
                        <span class="text-muted">Total Biaya Bahan:</span>
                        <strong>Rp {{ number_format($production->total_biaya_bahan, 0, ',', '.') }}</strong>
                    </div>
                    <div class="d-flex justify-content-between mb-3 pb-3 border-bottom">
                        <span class="text-muted">Biaya Tambahan:</span>
                        <strong class="{{ $production->biaya_tambahan > 0 ? 'text-warning' : '' }}">
                            Rp {{ number_format($production->biaya_tambahan, 0, ',', '.') }}
                        </strong>
                    </div>
                    <div class="d-flex justify-content-between mb-3">
                        <span class="fw-semibold">Total HPP:</span>
                        <strong class="fs-5 text-success">Rp {{ number_format($production->total_hpp, 0, ',', '.') }}</strong>
                    </div>
                    <hr>
                    <div class="bg-light p-3 rounded">
                        <div class="text-center">
                            <small class="text-muted d-block mb-1">HPP per Unit</small>
                            <div class="fs-4 fw-bold text-primary">
                                Rp {{ number_format($production->total_hpp / $production->quantity, 0, ',', '.') }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Production Info -->
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-header bg-light py-3">
                    <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Informasi Produksi</h5>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label class="text-muted small">Tanggal Produksi</label>
                        <div class="fw-semibold">
                            <i class="fas fa-calendar text-primary me-2"></i>
                            {{ date('d F Y', strtotime($production->tanggal_produksi)) }}
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="text-muted small">Operator</label>
                        <div class="fw-semibold">
                            <i class="fas fa-user text-success me-2"></i>
                            {{ $production->user->name }}
                        </div>
                    </div>
                    <div class="mb-0">
                        <label class="text-muted small">Dicatat Pada</label>
                        <div class="fw-semibold">
                            <i class="fas fa-clock text-info me-2"></i>
                            {{ $production->created_at->format('d M Y H:i') }}
                        </div>
                    </div>
                </div>
            </div>

            <!-- Product Current Status -->
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-light py-3">
                    <h5 class="mb-0"><i class="fas fa-warehouse me-2"></i>Stok Produk Saat Ini</h5>
                </div>
                <div class="card-body text-center">
                    <div class="fs-1 fw-bold text-primary mb-2">{{ number_format($production->product->stok) }}</div>
                    <p class="text-muted mb-0">unit tersedia</p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" method="POST" action="{{ route('produksi.destroy', $production->id) }}" style="display: none;">
    @csrf
    @method('DELETE')
</form>

<script>
function deleteProduction() {
    if (confirm('Apakah Anda yakin ingin menghapus produksi ini?\n\nPeringatan:\n- Stok bahan baku akan dikembalikan\n- Stok produk akan dikurangi\n- Data produksi akan hilang permanen')) {
        document.getElementById('deleteForm').submit();
    }
}
</script>
@endsection
