@extends('layouts.master')

@section('title', 'Produk')

@section('breadcrumb')
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-600 dark:text-text-muted">Data Master</span>
</li>
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-900 dark:text-white">Produk</span>
</li>
@endsection

@section('content')
<!-- Page Header -->
<div class="flex items-center justify-between mb-8">
    <div>
        <h1 class="text-2xl font-bold dark:text-white flex items-center gap-3">
            <span class="material-symbols-outlined text-primary text-3xl">shopping_bag</span>
            Produk
        </h1>
        <p class="text-slate-600 dark:text-text-muted mt-1">Kelola produk jadi dengan HPP dan monitoring stok</p>
    </div>
    
    <!-- Action Buttons -->
    <div class="flex items-center gap-3">
        <a href="{{ route('produk.export') ?? '#' }}" class="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors">
            <span class="material-symbols-outlined text-lg">file_download</span>
            Export
        </a>
        <button class="flex items-center gap-2 bg-blue-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
            <span class="material-symbols-outlined text-lg">file_upload</span>
            Import
        </button>
        <a href="{{ route('produk.create') }}" class="flex items-center gap-2 bg-primary text-background-dark px-4 py-2.5 rounded-lg text-sm font-bold transition-all duration-200 hover:scale-[1.02] hover:shadow-lg hover:shadow-primary/25">
            <span class="material-symbols-outlined text-lg">add</span>
            Tambah Produk
        </a>
    </div>
</div>

<!-- Stats Cards -->
<div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-blue-100 dark:bg-blue-500/20 rounded-lg">
                <span class="material-symbols-outlined text-blue-600 dark:text-blue-400 text-xl">shopping_bag</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Total Produk</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ number_format($stats['total'] ?? 0) }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-orange-100 dark:bg-orange-500/20 rounded-lg">
                <span class="material-symbols-outlined text-orange-600 dark:text-orange-400 text-xl">warning</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Stok Rendah</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ number_format($stats['low_stock'] ?? 0) }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-red-100 dark:bg-red-500/20 rounded-lg">
                <span class="material-symbols-outlined text-red-600 dark:text-red-400 text-xl">cancel</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Stok Habis</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ number_format($stats['out_of_stock'] ?? 0) }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-emerald-100 dark:bg-emerald-500/20 rounded-lg">
                <span class="material-symbols-outlined text-emerald-600 dark:text-emerald-400 text-xl">payments</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Total Nilai Stok</p>
        <h3 class="text-2xl font-bold dark:text-white">Rp {{ number_format($stats['total_value'] ?? 0, 0, ',', '.') }}</h3>
    </div>
</div>

<!-- Main Content -->
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
    <!-- Header with Search and Filters -->
    <div class="p-6 border-b border-slate-200 dark:border-border-dark">
        <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold dark:text-white">Daftar Produk</h2>
        </div>
        
        <div class="flex items-center gap-4">
            <!-- Search -->
            <div class="relative flex-1 max-w-md">
                <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 dark:text-text-muted text-xl">search</span>
                <input class="w-full bg-slate-100 dark:bg-border-dark border-none rounded-lg pl-10 pr-4 py-2.5 text-sm focus:ring-2 focus:ring-primary/50 dark:text-white placeholder:text-slate-400 dark:placeholder:text-text-muted" 
                       placeholder="Cari produk..." 
                       type="text"
                       id="productSearch"/>
            </div>
            
            <!-- Category Filter -->
            <select class="bg-slate-100 dark:bg-border-dark border-none rounded-lg text-sm font-medium dark:text-white py-2.5 pl-4 pr-10 focus:ring-2 focus:ring-primary/50">
                <option value="">Semua Kategori</option>
                @foreach($categories ?? [] as $category)
                    <option value="{{ $category }}">{{ $category }}</option>
                @endforeach
            </select>
            
            <!-- Stock Filter -->
            <select class="bg-slate-100 dark:bg-border-dark border-none rounded-lg text-sm font-medium dark:text-white py-2.5 pl-4 pr-10 focus:ring-2 focus:ring-primary/50">
                <option value="">Semua Stok</option>
                <option value="rendah">Stok Rendah</option>
                <option value="habis">Stok Habis</option>
                <option value="aman">Stok Aman</option>
            </select>
        </div>
    </div>
    
    <!-- Table -->
    <div class="overflow-x-auto">
        <table class="w-full">
            <thead class="bg-slate-50 dark:bg-border-dark">
                <tr>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">
                        <input type="checkbox" class="text-primary bg-slate-100 dark:bg-background-dark border-slate-300 dark:border-border-dark rounded focus:ring-primary focus:ring-2" id="selectAll">
                    </th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">SKU</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Nama Produk</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Kategori</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">HPP</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Harga Jual</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Stok</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Status</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Aksi</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-200 dark:divide-border-dark">
                @forelse($products ?? [] as $product)
                <tr class="hover:bg-slate-50 dark:hover:bg-border-dark/30 transition-colors">
                    <td class="px-6 py-4">
                        <input type="checkbox" class="text-primary bg-slate-100 dark:bg-background-dark border-slate-300 dark:border-border-dark rounded focus:ring-primary focus:ring-2 row-checkbox" value="{{ $product->id }}">
                    </td>
                    <td class="px-6 py-4 text-sm font-medium dark:text-white">{{ $product->sku ?? '-' }}</td>
                    <td class="px-6 py-4">
                        <div class="flex items-center gap-3">
                            @if($product->gambar_url ?? false)
                                <img src="{{ $product->gambar_url }}" alt="{{ $product->nama }}" class="w-10 h-10 rounded-lg object-cover">
                            @else
                                <div class="w-10 h-10 bg-slate-100 dark:bg-border-dark rounded-lg flex items-center justify-center">
                                    <span class="material-symbols-outlined text-slate-600 dark:text-text-muted text-lg">shopping_bag</span>
                                </div>
                            @endif
                            <div>
                                <p class="text-sm font-medium dark:text-white">{{ $product->nama }}</p>
                                <p class="text-xs text-slate-500 dark:text-text-muted">{{ Str::limit($product->deskripsi ?? '', 40) }}</p>
                            </div>
                        </div>
                    </td>
                    <td class="px-6 py-4">
                        <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-700 dark:bg-blue-500/20 dark:text-blue-300">
                            {{ $product->kategori ?? 'Uncategorized' }}
                        </span>
                    </td>
                    <td class="px-6 py-4 text-sm font-medium dark:text-white">Rp {{ number_format($product->hpp ?? 0, 0, ',', '.') }}</td>
                    <td class="px-6 py-4 text-sm font-medium dark:text-white">Rp {{ number_format($product->harga_jual ?? 0, 0, ',', '.') }}</td>
                    <td class="px-6 py-4">
                        <div class="text-sm font-semibold dark:text-white">{{ number_format($product->stok_tersedia ?? 0, 0, ',', '.') }} {{ $product->satuan ?? 'pcs' }}</div>
                        @php
                            $stok = $product->stok_tersedia ?? 0;
                            $minStok = $product->stok_minimum ?? 10;
                            $persentase = $minStok > 0 ? ($stok / $minStok) * 100 : 100;
                            $warna = $persentase >= 100 ? 'bg-green-500' : ($persentase >= 50 ? 'bg-yellow-500' : 'bg-red-500');
                        @endphp
                        <div class="w-16 bg-slate-200 dark:bg-border-dark rounded-full h-1.5 mt-1">
                            <div class="{{ $warna }} h-1.5 rounded-full transition-all" style="width: {{ min($persentase, 100) }}%"></div>
                        </div>
                    </td>
                    <td class="px-6 py-4">
                        @php
                            $isActive = $product->is_active ?? true;
                        @endphp
                        <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium {{ $isActive ? 'bg-green-100 text-green-700 dark:bg-green-500/20 dark:text-green-300' : 'bg-red-100 text-red-700 dark:bg-red-500/20 dark:text-red-300' }}">
                            {{ $isActive ? 'Aktif' : 'Non-aktif' }}
                        </span>
                    </td>
                    <td class="px-6 py-4">
                        <div class="flex items-center gap-2">
                            <a href="{{ route('produk.show', $product->id) }}" class="p-2 text-green-600 hover:bg-green-50 dark:hover:bg-green-500/20 rounded-lg transition-colors" title="Detail">
                                <span class="material-symbols-outlined text-lg">visibility</span>
                            </a>
                            <a href="{{ route('produk.edit', $product->id) }}" class="p-2 text-blue-600 hover:bg-blue-50 dark:hover:bg-blue-500/20 rounded-lg transition-colors" title="Edit">
                                <span class="material-symbols-outlined text-lg">edit</span>
                            </a>
                            <button class="p-2 text-red-600 hover:bg-red-50 dark:hover:bg-red-500/20 rounded-lg transition-colors" title="Hapus">
                                <span class="material-symbols-outlined text-lg">delete</span>
                            </button>
                        </div>
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="9" class="px-6 py-12 text-center">
                        <div class="flex flex-col items-center gap-3 text-slate-500 dark:text-text-muted">
                            <span class="material-symbols-outlined text-4xl opacity-50">shopping_bag</span>
                            <p class="font-medium">Belum ada data produk</p>
                            <p class="text-sm">Klik tombol "Tambah Produk" untuk menambah data pertama</p>
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
                    @if(isset($products) && method_exists($products, 'total'))
                        Menampilkan {{ $products->firstItem() }} - {{ $products->lastItem() }} dari {{ $products->total() }} produk
                    @else
                        Menampilkan data produk
                    @endif
                </p>
            </div>
            @if(isset($products) && method_exists($products, 'links') && $products->hasPages())
                {{ $products->appends(request()->query())->links() }}
            @endif
        </div>
    </div>
</div>

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

    // Search functionality
    const searchInput = document.querySelector('#productSearch');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();
            const rows = document.querySelectorAll('tbody tr:not(:last-child)');
            
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(searchTerm) ? '' : 'none';
            });
        });
    }

    // Select all checkbox
    const selectAllCheckbox = document.querySelector('#selectAll');
    const rowCheckboxes = document.querySelectorAll('.row-checkbox');
    
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function() {
            rowCheckboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
        });
    }
});
</script>
@endpush
