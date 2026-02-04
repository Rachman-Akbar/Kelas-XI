@extends('layouts.master')

@section('title', 'Komposisi Produk & Analisis HPP')

@section('breadcrumb')
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-600 dark:text-text-muted">Operasional</span>
</li>
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-900 dark:text-white">Komposisi Produk & HPP</span>
</li>
@endsection

@section('content')
<!-- Page Header -->
<div class="flex items-center justify-between mb-8">
    <div>
        <h1 class="text-2xl font-bold dark:text-white flex items-center gap-3">
            <span class="material-symbols-outlined text-primary text-3xl">inventory</span>
            Komposisi Produk & Analisis COGS
        </h1>
        <p class="text-slate-600 dark:text-text-muted mt-1">Kelola komposisi bahan baku dan analisis HPP produk</p>
        @if(isset($stats['products_need_review']) && $stats['products_need_review'] > 0)
            <span class="inline-flex items-center gap-1.5 px-3 py-1 mt-2 bg-red-500/10 border border-red-500/20 rounded-full text-xs font-bold text-red-500 uppercase tracking-wider">
                <span class="material-symbols-outlined text-sm">error</span>
                {{ $stats['products_need_review'] }} Produk Perlu Review
            </span>
        @endif
    </div>
    
    <!-- Action Buttons -->
    <div class="flex items-center gap-3">
        <a href="{{ route('komposisi-produk.bulk-calculate') ?? '#' }}" class="flex items-center gap-2 bg-blue-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
            <span class="material-symbols-outlined text-lg">calculate</span>
            Bulk Calculate
        </a>
        <a href="{{ route('komposisi-produk.export') ?? '#' }}" class="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors">
            <span class="material-symbols-outlined text-lg">file_download</span>
            Export
        </a>
        <a href="{{ route('komposisi-produk.create') }}" class="flex items-center gap-2 bg-primary text-background-dark px-4 py-2.5 rounded-lg text-sm font-bold transition-all duration-200 hover:scale-[1.02] hover:shadow-lg hover:shadow-primary/25">
            <span class="material-symbols-outlined text-lg">add</span>
            Buat Resep
        </a>
    </div>
</div>

<!-- Advanced Stats Cards (PRESERVED) -->
<div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
    <!-- Total Catalog Value -->
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-start justify-between mb-4">
            <div>
                <p class="text-xs font-bold uppercase tracking-widest text-slate-400 mb-1">Total Nilai Katalog</p>
                <h3 class="text-2xl font-black text-slate-900 dark:text-white">
                    Rp {{ number_format(($stats['total_catalog_value'] ?? 0), 0, ',', '.') }}
                </h3>
            </div>
            <div class="p-2 bg-blue-100 dark:bg-blue-500/20 rounded-lg">
                <span class="material-symbols-outlined text-blue-600 dark:text-blue-400 text-xl">inventory</span>
            </div>
        </div>
        <div class="flex items-center gap-1 text-xs">
            @php
                $valueChange = $stats['catalog_value_change'] ?? 2.4;
            @endphp
            <span class="material-symbols-outlined text-xs {{ $valueChange >= 0 ? 'text-green-500' : 'text-red-500' }}">
                {{ $valueChange >= 0 ? 'trending_up' : 'trending_down' }}
            </span>
            <span class="font-bold {{ $valueChange >= 0 ? 'text-green-500' : 'text-red-500' }}">
                {{ $valueChange >= 0 ? '+' : '' }}{{ $valueChange }}%
            </span>
            <span class="text-slate-400">vs bulan lalu</span>
        </div>
    </div>

    <!-- Avg Food Cost Percentage -->
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-start justify-between mb-4">
            <div>
                <p class="text-xs font-bold uppercase tracking-widest text-slate-400 mb-1">Rata-rata Food Cost %</p>
                <h3 class="text-2xl font-black text-slate-900 dark:text-white">
                    {{ number_format(($stats['avg_food_cost_percentage'] ?? 28.4), 1) }}%
                </h3>
            </div>
            <div class="p-2 bg-emerald-100 dark:bg-emerald-500/20 rounded-lg">
                <span class="material-symbols-outlined text-emerald-600 dark:text-emerald-400 text-xl">pie_chart</span>
            </div>
        </div>
        <div class="flex items-center gap-1 text-xs">
            @php
                $costChange = $stats['food_cost_change'] ?? -1.2;
            @endphp
            <span class="material-symbols-outlined text-xs {{ $costChange <= 0 ? 'text-green-500' : 'text-red-500' }}">
                {{ $costChange <= 0 ? 'trending_down' : 'trending_up' }}
            </span>
            <span class="font-bold {{ $costChange <= 0 ? 'text-green-500' : 'text-red-500' }}">
                {{ $costChange }}%
            </span>
            <span class="text-slate-400">optimisasi biaya</span>
        </div>
    </div>

    <!-- Material Shortages -->
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-start justify-between mb-4">
            <div>
                <p class="text-xs font-bold uppercase tracking-widest text-slate-400 mb-1">Kekurangan Bahan</p>
                <h3 class="text-2xl font-black {{ ($stats['material_shortages'] ?? 0) > 0 ? 'text-red-500' : 'text-slate-900 dark:text-white' }}">
                    {{ $stats['material_shortages'] ?? 0 }} Item
                </h3>
            </div>
            <div class="p-2 {{ ($stats['material_shortages'] ?? 0) > 0 ? 'bg-red-100 dark:bg-red-500/20' : 'bg-yellow-100 dark:bg-yellow-500/20' }} rounded-lg">
                <span class="material-symbols-outlined {{ ($stats['material_shortages'] ?? 0) > 0 ? 'text-red-600 dark:text-red-400' : 'text-yellow-600 dark:text-yellow-400' }} text-xl">inventory_2</span>
            </div>
        </div>
        <div class="flex items-center gap-1 text-xs">
            @if(($stats['material_shortages'] ?? 0) > 0)
                <span class="material-symbols-outlined text-xs text-red-500">warning</span>
                <span class="font-bold text-red-500">Perlu Restock</span>
            @else
                <span class="material-symbols-outlined text-xs text-green-500">check_circle</span>
                <span class="font-bold text-green-500">Stok Aman</span>
            @endif
        </div>
    </div>

    <!-- Gross Profit Margin -->
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-start justify-between mb-4">
            <div>
                <p class="text-xs font-bold uppercase tracking-widest text-slate-400 mb-1">Gross Profit Margin</p>
                <h3 class="text-2xl font-black text-slate-900 dark:text-white">
                    {{ number_format(($stats['gross_profit_margin'] ?? 64.5), 1) }}%
                </h3>
            </div>
            <div class="p-2 bg-primary/10 rounded-lg">
                <span class="material-symbols-outlined text-primary text-xl">trending_up</span>
            </div>
        </div>
        <div class="flex items-center gap-1 text-xs">
            <div class="size-2 rounded-full bg-primary animate-pulse"></div>
            <span class="font-bold text-primary">Stabil</span>
            <span class="text-slate-400">performa baik</span>
        </div>
    </div>
</div>

<!-- Main Content -->
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
    <!-- Header with Search and Filters -->
    <div class="p-6 border-b border-slate-200 dark:border-border-dark">
        <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold dark:text-white">Daftar Komposisi Produk</h2>
        </div>
        
        <div class="flex items-center gap-4">
            <!-- Search -->
            <div class="relative flex-1 max-w-md">
                <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 dark:text-text-muted text-xl">search</span>
                <input class="w-full bg-slate-100 dark:bg-border-dark border-none rounded-lg pl-10 pr-4 py-2.5 text-sm focus:ring-2 focus:ring-primary/50 dark:text-white placeholder:text-slate-400 dark:placeholder:text-text-muted" 
                       placeholder="Cari produk atau bahan..." 
                       type="text"
                       id="globalSearch"/>
            </div>
            
            <!-- Category Filter -->
            <select class="bg-slate-100 dark:bg-border-dark border-none rounded-lg text-sm font-medium dark:text-white py-2.5 pl-4 pr-10 focus:ring-2 focus:ring-primary/50">
                <option value="">Semua Kategori</option>
                @foreach($categories ?? [] as $category)
                    <option value="{{ $category }}">{{ $category }}</option>
                @endforeach
            </select>
            
            <!-- Status Filter -->
            <select class="bg-slate-100 dark:bg-border-dark border-none rounded-lg text-sm font-medium dark:text-white py-2.5 pl-4 pr-10 focus:ring-2 focus:ring-primary/50">
                <option value="">Semua Status</option>
                <option value="sehat">Sehat</option>
                <option value="rendah">Stok Rendah</option>
                <option value="belum">Belum Lengkap</option>
            </select>
        </div>
    </div>

    <!-- Flash Messages -->
    @if(session('success'))
        <div class="mx-6 mt-6 p-4 bg-green-100 dark:bg-green-500/20 border border-green-200 dark:border-green-700 rounded-lg">
            <div class="flex items-center">
                <span class="material-symbols-outlined text-green-600 dark:text-green-400 mr-2">check_circle</span>
                <span class="text-green-800 dark:text-green-200">{{ session('success') }}</span>
            </div>
        </div>
    @endif

    @if(session('error'))
        <div class="mx-6 mt-6 p-4 bg-red-100 dark:bg-red-500/20 border border-red-200 dark:border-red-700 rounded-lg">
            <div class="flex items-center">
                <span class="material-symbols-outlined text-red-600 dark:text-red-400 mr-2">error</span>
                <span class="text-red-800 dark:text-red-200">{{ session('error') }}</span>
            </div>
        </div>
    @endif
    
    <!-- Table with Expandable Rows (PRESERVED) -->
    <div class="overflow-x-auto">
        <table class="w-full">
            <thead class="bg-slate-50 dark:bg-border-dark">
                <tr>
                    <th class="px-6 py-4 text-center text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider" style="width: 5%;"></th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider" style="width: 25%;">Detail Produk</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider" style="width: 12%;">SKU</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider" style="width: 15%;">Kategori</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider" style="width: 15%;">HPP Saat Ini</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider" style="width: 13%;">Status</th>
                    <th class="px-6 py-4 text-right text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider" style="width: 15%;">Aksi</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-200 dark:divide-border-dark">
                @forelse($compositions->groupBy('product_id') ?? [] as $productId => $productCompositions)
                    @php
                        $product = $productCompositions->first()->product;
                        $hasComposition = true;
                        $totalCost = $productCompositions->sum('biaya');
                        $hasLowStock = $productCompositions->filter(function($comp) {
                            return $comp->material->stok_saat_ini < ($comp->material->stok_minimum ?? 10);
                        })->count() > 0;
                        
                        // Status determination
                        if (!$hasComposition) {
                            $statusColor = 'amber';
                            $statusText = 'Resep Belum Lengkap';
                            $statusIcon = 'error';
                        } elseif ($hasLowStock) {
                            $statusColor = 'red';
                            $statusText = 'Bahan Tidak Tersedia';
                            $statusIcon = 'warning';
                        } else {
                            $statusColor = 'primary';
                            $statusText = 'Sehat';
                            $statusIcon = 'check_circle';
                        }
                    @endphp
                    
                    <!-- Main Product Row -->
                    <tr class="hover:bg-slate-50 dark:hover:bg-border-dark/30 transition-colors group" data-product-id="{{ $product->id }}">
                        <td class="px-6 py-4 text-center">
                            @if($hasComposition)
                                <button class="text-slate-400 hover:text-primary transition-colors expand-btn" onclick="toggleRow({{ $product->id }})">
                                    <span class="material-symbols-outlined chevron-icon">chevron_right</span>
                                </button>
                            @endif
                        </td>
                        <td class="px-6 py-4">
                            <div class="flex flex-col">
                                <span class="text-sm font-bold dark:text-white">{{ $product->nama }}</span>
                                @if($product->deskripsi)
                                    <span class="text-xs text-slate-500 dark:text-text-muted">{{ Str::limit($product->deskripsi, 40) }}</span>
                                @endif
                            </div>
                        </td>
                        <td class="px-6 py-4 text-sm font-mono text-slate-500 dark:text-text-muted">{{ $product->sku ?? '-' }}</td>
                        <td class="px-6 py-4">
                            <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-700 dark:bg-blue-500/20 dark:text-blue-300">
                                {{ $product->kategori ?? 'Uncategorized' }}
                            </span>
                        </td>
                        <td class="px-6 py-4 text-sm font-bold dark:text-white">
                            Rp {{ number_format($product->hpp ?? $totalCost, 0, ',', '.') }}
                        </td>
                        <td class="px-6 py-4">
                            <div class="flex items-center gap-2">
                                <div class="size-2 rounded-full bg-{{ $statusColor }}-500 {{ $statusText === 'Sehat' ? 'animate-pulse' : '' }}"></div>
                                <span class="text-xs font-medium text-{{ $statusColor }}-500">{{ $statusText }}</span>
                            </div>
                        </td>
                        <td class="px-6 py-4 text-right">
                            <a href="{{ route('komposisi-produk.create') }}?product_id={{ $product->id }}" class="text-sm font-bold text-primary hover:underline">
                                {{ $hasComposition ? 'Edit Resep' : 'Buat Resep' }}
                            </a>
                        </td>
                    </tr>
                    
                    <!-- Expandable Breakdown Row -->
                    @if($hasComposition)
                    <tr class="breakdown-row hidden bg-slate-50/50 dark:bg-border-dark/20" id="breakdown-{{ $product->id }}">
                        <td class="px-10 py-6" colspan="7">
                            <div class="bg-white dark:bg-background-dark rounded-lg border border-slate-200 dark:border-border-dark overflow-hidden shadow-inner">
                                <div class="px-4 py-3 bg-slate-50 dark:bg-border-dark flex justify-between items-center border-b border-slate-200 dark:border-border-dark">
                                    <span class="text-xs font-bold uppercase tracking-widest text-slate-400">Breakdown Resep</span>
                                    <div class="flex gap-4 items-center">
                                        <span class="text-xs text-slate-500 dark:text-text-muted">Yield: 1 Unit</span>
                                        <a href="{{ route('komposisi-produk.create') }}?product_id={{ $product->id }}" class="text-xs font-bold text-primary flex items-center gap-1">
                                            <span class="material-symbols-outlined text-sm">add</span> Tambah Bahan
                                        </a>
                                    </div>
                                </div>
                                <table class="w-full text-sm">
                                    <thead>
                                        <tr class="text-slate-400 text-xs uppercase font-semibold">
                                            <th class="px-4 py-2 text-left">Bahan</th>
                                            <th class="px-4 py-2 text-left">Qty per Unit</th>
                                            <th class="px-4 py-2 text-left">Harga Satuan</th>
                                            <th class="px-4 py-2 text-left">Stok</th>
                                            <th class="px-4 py-2 text-right">Total Biaya</th>
                                            <th class="px-4 py-2 text-center">Aksi</th>
                                        </tr>
                                    </thead>
                                    <tbody class="divide-y divide-slate-100 dark:divide-border-dark">
                                        @foreach($productCompositions as $comp)
                                        <tr>
                                            <td class="px-4 py-3 dark:text-white font-medium">{{ $comp->material->nama }}</td>
                                            <td class="px-4 py-3 dark:text-white">{{ $comp->jumlah }} {{ $comp->material->satuan }}</td>
                                            <td class="px-4 py-3 text-slate-500 dark:text-text-muted">Rp {{ number_format($comp->material->harga_per_satuan, 0, ',', '.') }}/{{ $comp->material->satuan }}</td>
                                            <td class="px-4 py-3">
                                                @php
                                                    $stockLevel = $comp->material->stok_saat_ini;
                                                    $stockMin = $comp->material->stok_minimum ?? 10;
                                                    if($stockLevel == 0) {
                                                        $stockClass = 'text-red-500';
                                                    } elseif($stockLevel < $stockMin) {
                                                        $stockClass = 'text-amber-500';
                                                    } else {
                                                        $stockClass = 'text-green-500';
                                                    }
                                                @endphp
                                                <span class="font-medium {{ $stockClass }}">{{ $stockLevel }} {{ $comp->material->satuan }}</span>
                                            </td>
                                            <td class="px-4 py-3 text-right font-bold dark:text-white">Rp {{ number_format($comp->biaya, 0, ',', '.') }}</td>
                                            <td class="px-4 py-3 text-center">
                                                <form action="{{ route('komposisi-produk.destroy', $comp->id) }}" method="POST" class="inline" onsubmit="return confirm('Yakin hapus bahan ini dari resep?')">
                                                    @csrf
                                                    @method('DELETE')
                                                    <button type="submit" class="text-red-500 hover:text-red-700">
                                                        <span class="material-symbols-outlined text-sm">delete</span>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                        @endforeach
                                    </tbody>
                                    <tfoot>
                                        <tr class="bg-primary/5">
                                            <td class="px-4 py-3 text-right font-bold uppercase text-xs tracking-widest text-primary" colspan="4">HPP Terhitung (COGS)</td>
                                            <td class="px-4 py-3 text-right font-black text-primary text-lg">Rp {{ number_format($totalCost, 0, ',', '.') }}</td>
                                            <td></td>
                                        </tr>
                                    </tfoot>
                                </table>
                            </div>
                        </td>
                    </tr>
                    @endif
                @empty
                    <tr>
                        <td colspan="7" class="px-6 py-12 text-center">
                            <div class="flex flex-col items-center gap-3 text-slate-500 dark:text-text-muted">
                                <span class="material-symbols-outlined text-4xl opacity-50">inventory</span>
                                <p class="font-medium">Belum ada produk dengan komposisi</p>
                                <p class="text-sm">Klik tombol "Buat Resep" untuk menambah komposisi pertama</p>
                            </div>
                        </td>
                    </tr>
                @endforelse
            </tbody>
        </table>
    </div>
    
    <!-- Pagination -->
    <div class="p-6 border-t border-slate-200 dark:border-border-dark">
        <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <div class="flex items-center gap-4">
                <div class="flex items-center gap-2">
                    <label class="text-sm text-slate-600 dark:text-text-muted">Tampilkan</label>
                    <select id="perPageSelect" class="bg-slate-100 dark:bg-border-dark border-none rounded-lg text-sm font-medium dark:text-white py-1.5 px-3 focus:ring-2 focus:ring-primary/50">
                        <option value="5" {{ request('per_page') == '5' ? 'selected' : '' }}>5</option>
                        <option value="10" {{ request('per_page') == '10' || !request('per_page') ? 'selected' : '' }}>10</option>
                        <option value="25" {{ request('per_page') == '25' ? 'selected' : '' }}>25</option>
                        <option value="50" {{ request('per_page') == '50' ? 'selected' : '' }}>50</option>
                        <option value="100" {{ request('per_page') == '100' ? 'selected' : '' }}>100</option>
                    </select>
                    <span class="text-sm text-slate-600 dark:text-text-muted">data</span>
                </div>
                <p class="text-sm text-slate-600 dark:text-text-muted">
                    @if(isset($compositions) && method_exists($compositions, 'total'))
                        Menampilkan {{ $compositions->firstItem() }} - {{ $compositions->lastItem() }} dari {{ $compositions->total() }} komposisi
                    @else
                        Menampilkan data komposisi produk
                    @endif
                </p>
            </div>
            @if(isset($compositions) && method_exists($compositions, 'links') && $compositions->hasPages())
                {{ $compositions->appends(request()->query())->links() }}
            @endif
        </div>
    </div>
</div>

<!-- System Alerts -->
@if(isset($stats['outdated_prices']) && $stats['outdated_prices'] > 0)
<div class="mt-8 p-4 rounded-xl bg-red-500/10 border border-red-500/20 flex items-start gap-4">
    <div class="size-10 rounded-full bg-red-500 flex items-center justify-center text-white shrink-0">
        <span class="material-symbols-outlined">error</span>
    </div>
    <div>
        <h4 class="text-sm font-bold text-red-500 uppercase tracking-wider mb-1">Peringatan Sistem</h4>
        <p class="text-sm text-slate-600 dark:text-red-200/80 leading-relaxed">
            Beberapa resep menggunakan harga bahan yang sudah kadaluarsa. HPP yang dihitung mungkin tidak akurat.
            <a href="{{ route('komposisi-produk.bulk-calculate') }}" class="font-bold underline ml-1 hover:text-red-400">Hitung ulang semua sekarang?</a>
        </p>
    </div>
</div>
@endif

@endsection

@push('scripts')
<script>
document.addEventListener('DOMContentLoaded', function() {
    // Per page selector
    const perPageSelect = document.getElementById('perPageSelect');
    if (perPageSelect) {
        perPageSelect.addEventListener('change', function() {
            const value = this.value;
            const currentUrl = window.location.href;
            let newUrl;
            
            // Cek apakah sudah ada parameter per_page
            if (currentUrl.includes('per_page=')) {
                // Replace existing per_page parameter
                newUrl = currentUrl.replace(/per_page=\d+/, 'per_page=' + value);
            } else if (currentUrl.includes('?')) {
                // Ada query string tapi belum ada per_page
                newUrl = currentUrl + '&per_page=' + value;
            } else {
                // Belum ada query string sama sekali
                newUrl = currentUrl + '?per_page=' + value;
            }
            
            // Hapus parameter page jika ada
            if (newUrl.includes('&page=')) {
                newUrl = newUrl.replace(/&page=\d+/, '');
            } else if (newUrl.includes('?page=')) {
                newUrl = newUrl.replace(/\?page=\d+&/, '?').replace(/\?page=\d+/, '');
            }
            
            console.log('Original URL:', currentUrl);
            console.log('New URL:', newUrl);
            window.location.href = newUrl;
        });
    }
});

function toggleRow(productId) {
    const breakdownRow = document.getElementById('breakdown-' + productId);
    const chevron = event.currentTarget.querySelector('.chevron-icon');
    
    if (breakdownRow.classList.contains('hidden')) {
        breakdownRow.classList.remove('hidden');
        chevron.textContent = 'expand_more';
    } else {
        breakdownRow.classList.add('hidden');
        chevron.textContent = 'chevron_right';
    }
}

// Global search functionality
document.getElementById('globalSearch')?.addEventListener('input', function(e) {
    const searchTerm = e.target.value.toLowerCase();
    const rows = document.querySelectorAll('tbody tr:not(.breakdown-row)');
    
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(searchTerm) ? '' : 'none';
    });
});
</script>
@endpush
