@extends('layouts.master')

@section('title', 'Bahan Baku')

@section('breadcrumb')
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-600 dark:text-text-muted">Data Master</span>
</li>
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-900 dark:text-white">Bahan Baku</span>
</li>
@endsection

@section('content')
<!-- Page Header -->
<div class="flex items-center justify-between mb-8">
    <div>
        <h1 class="text-2xl font-bold dark:text-white flex items-center gap-3">
            <span class="material-symbols-outlined text-primary text-3xl">inventory_2</span>
            Bahan Baku
        </h1>
        <p class="text-slate-600 dark:text-text-muted mt-1">Kelola data bahan baku dan monitoring stok</p>
    </div>
    
    <!-- Action Buttons -->
    <div class="flex items-center gap-3">
        <button class="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors">
            <span class="material-symbols-outlined text-lg">file_download</span>
            Export
        </button>
        <button class="flex items-center gap-2 bg-blue-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
            <span class="material-symbols-outlined text-lg">file_upload</span>
            Import
        </button>
        <a href="/bahan-baku/create" class="flex items-center gap-2 bg-primary text-background-dark px-4 py-2.5 rounded-lg text-sm font-bold transition-all duration-200 hover:scale-[1.02] hover:shadow-lg hover:shadow-primary/25">
            <span class="material-symbols-outlined text-lg">add</span>
            Tambah Bahan Baku
        </a>
    </div>
</div>

<!-- Stats Cards -->
<div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-green-100 dark:bg-green-500/20 rounded-lg">
                <span class="material-symbols-outlined text-green-600 dark:text-green-400 text-xl">inventory_2</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Total Bahan Baku</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ $totalMaterials ?? 0 }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-orange-100 dark:bg-orange-500/20 rounded-lg">
                <span class="material-symbols-outlined text-orange-600 dark:text-orange-400 text-xl">warning</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Stok Menipis</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ $lowStock ?? 0 }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-red-100 dark:bg-red-500/20 rounded-lg">
                <span class="material-symbols-outlined text-red-600 dark:text-red-400 text-xl">error</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Stok Habis</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ $outOfStock ?? 0 }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-blue-100 dark:bg-blue-500/20 rounded-lg">
                <span class="material-symbols-outlined text-blue-600 dark:text-blue-400 text-xl">payments</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Total Nilai Stok</p>
        <h3 class="text-2xl font-bold dark:text-white">Rp {{ number_format($totalValue ?? 0, 0, ',', '.') }}</h3>
    </div>
</div>

<!-- Main Content -->
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
    <!-- Header with Search and Filters -->
    <div class="p-6 border-b border-slate-200 dark:border-border-dark">
        <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold dark:text-white">Daftar Bahan Baku</h2>
        </div>
        
        <div class="flex items-center gap-4">
            <!-- Search -->
            <div class="relative flex-1 max-w-md">
                <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 dark:text-text-muted text-xl">search</span>
                <input class="w-full bg-slate-100 dark:bg-border-dark border-none rounded-lg pl-10 pr-4 py-2.5 text-sm focus:ring-2 focus:ring-primary/50 dark:text-white placeholder:text-slate-400 dark:placeholder:text-text-muted" 
                       placeholder="Cari bahan baku..." 
                       type="text"/>
            </div>
            
            <!-- Status Filter -->
            <select class="bg-slate-100 dark:bg-border-dark border-none rounded-lg text-sm font-medium dark:text-white py-2.5 pl-4 pr-10 focus:ring-2 focus:ring-primary/50">
                <option value="">Semua Status</option>
                <option value="aman">Stok Aman</option>
                <option value="rendah">Stok Rendah</option>
                <option value="kritis">Stok Kritis</option>
            </select>
            
            <!-- Category Filter -->
            <select class="bg-slate-100 dark:bg-border-dark border-none rounded-lg text-sm font-medium dark:text-white py-2.5 pl-4 pr-10 focus:ring-2 focus:ring-primary/50">
                <option value="">Semua Kategori</option>
                <option value="tepung">Tepung</option>
                <option value="gula">Gula & Pemanis</option>
                <option value="minyak">Minyak & Lemak</option>
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
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Kode Bahan</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Nama Bahan</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Kategori</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Satuan</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Stok Saat Ini</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Min. Stok</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Harga/Unit</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Status</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-600 dark:text-text-muted uppercase tracking-wider">Aksi</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-200 dark:divide-border-dark">
                @forelse($materials ?? [] as $index => $material)
                <tr class="hover:bg-slate-50 dark:hover:bg-border-dark/30 transition-colors">
                    <td class="px-6 py-4">
                        <input type="checkbox" class="text-primary bg-slate-100 dark:bg-background-dark border-slate-300 dark:border-border-dark rounded focus:ring-primary focus:ring-2 row-checkbox" value="{{ $material->id ?? $index }}">
                    </td>
                    <td class="px-6 py-4 text-sm font-medium dark:text-white">{{ $material->kode ?? 'BB-' . str_pad($index + 1, 3, '0', STR_PAD_LEFT) }}</td>
                    <td class="px-6 py-4">
                        <div class="flex items-center gap-3">
                            <div class="w-10 h-10 bg-slate-100 dark:bg-border-dark rounded-lg flex items-center justify-center">
                                @if($material->gambar_url ?? false)
                                    <img src="{{ $material->gambar_url }}" alt="{{ $material->nama }}" class="w-full h-full object-cover rounded-lg">
                                @else
                                    <span class="material-symbols-outlined text-slate-600 dark:text-text-muted text-lg">inventory_2</span>
                                @endif
                            </div>
                            <div>
                                <p class="text-sm font-medium dark:text-white">{{ $material->nama ?? 'Tepung Terigu' }}</p>
                                <p class="text-xs text-slate-500 dark:text-text-muted">{{ $material->deskripsi ?? 'Tepung protein tinggi' }}</p>
                            </div>
                        </div>
                    </td>
                    <td class="px-6 py-4">
                        <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-700 dark:bg-blue-500/20 dark:text-blue-300">
                            {{ $material->kategori ?? 'Tepung' }}
                        </span>
                    </td>
                    <td class="px-6 py-4 text-sm text-slate-600 dark:text-text-muted font-medium">{{ $material->satuan ?? 'Kg' }}</td>
                    <td class="px-6 py-4">
                        <div class="text-sm font-semibold dark:text-white">{{ number_format($material->stok ?? 250, 0, ',', '.') }} {{ $material->satuan ?? 'Kg' }}</div>
                        @php
                            $stok = $material->stok ?? 250;
                            $minStok = $material->min_stok ?? 100;
                            $persentase = $minStok > 0 ? ($stok / $minStok) * 100 : 100;
                            $warna = $persentase >= 100 ? 'bg-green-500' : ($persentase >= 50 ? 'bg-yellow-500' : 'bg-red-500');
                        @endphp
                        <div class="w-16 bg-slate-200 dark:bg-border-dark rounded-full h-1.5 mt-1">
                            <div class="{{ $warna }} h-1.5 rounded-full transition-all" style="width: {{ min($persentase, 100) }}%"></div>
                        </div>
                    </td>
                    <td class="px-6 py-4 text-sm text-slate-600 dark:text-text-muted">{{ number_format($material->min_stok ?? 100, 0, ',', '.') }} {{ $material->satuan ?? 'Kg' }}</td>
                    <td class="px-6 py-4 text-sm font-medium dark:text-white">Rp {{ number_format($material->harga_per_satuan ?? 12500, 0, ',', '.') }}</td>
                    <td class="px-6 py-4">
                        @php
                            $stockStatus = $material->stock_status ?? 'aman';
                            $statusColor = $stockStatus == 'aman' ? 'green' : ($stockStatus == 'rendah' ? 'yellow' : 'red');
                        @endphp
                        <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-{{ $statusColor }}-100 text-{{ $statusColor }}-700 dark:bg-{{ $statusColor }}-500/20 dark:text-{{ $statusColor }}-300">
                            {{ ucfirst($stockStatus) }}
                        </span>
                    </td>
                    <td class="px-6 py-4">
                        <div class="flex items-center gap-2">
                            <button class="p-2 text-blue-600 hover:bg-blue-50 dark:hover:bg-blue-500/20 rounded-lg transition-colors" title="Edit">
                                <span class="material-symbols-outlined text-lg">edit</span>
                            </button>
                            <button class="p-2 text-green-600 hover:bg-green-50 dark:hover:bg-green-500/20 rounded-lg transition-colors" title="Detail">
                                <span class="material-symbols-outlined text-lg">visibility</span>
                            </button>
                            <button class="p-2 text-red-600 hover:bg-red-50 dark:hover:bg-red-500/20 rounded-lg transition-colors" title="Hapus">
                                <span class="material-symbols-outlined text-lg">delete</span>
                            </button>
                        </div>
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="10" class="px-6 py-12 text-center">
                        <div class="flex flex-col items-center gap-3 text-slate-500 dark:text-text-muted">
                            <span class="material-symbols-outlined text-4xl opacity-50">inventory_2</span>
                            <p class="font-medium">Belum ada data bahan baku</p>
                            <p class="text-sm">Klik tombol "Tambah Bahan Baku" untuk menambah data pertama</p>
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
                    @if(isset($materials) && method_exists($materials, 'total'))
                        Menampilkan {{ $materials->firstItem() }} - {{ $materials->lastItem() }} dari {{ $materials->total() }} bahan baku
                    @else
                        Menampilkan data bahan baku
                    @endif
                </p>
            </div>
            @if(isset($materials) && method_exists($materials, 'links') && $materials->hasPages())
                {{ $materials->appends(request()->query())->links() }}
            @else
                <div class="flex items-center gap-2">
                    <button class="px-3 py-1.5 text-sm text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark rounded-md transition-colors">
                        <span class="material-symbols-outlined text-lg">chevron_left</span>
                    </button>
                    <button class="px-3 py-1.5 text-sm bg-primary text-background-dark rounded-md font-medium">1</button>
                    <button class="px-3 py-1.5 text-sm text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark rounded-md transition-colors">
                        <span class="material-symbols-outlined text-lg">chevron_right</span>
                    </button>
                </div>
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
    const searchInput = document.querySelector('input[placeholder="Cari bahan baku..."]');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            // Add search logic here
            console.log('Searching for:', e.target.value);
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
    
    // Row checkbox change
    rowCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const checkedBoxes = document.querySelectorAll('.row-checkbox:checked');
            selectAllCheckbox.checked = checkedBoxes.length === rowCheckboxes.length;
        });
    });
});
</script>
@endpush

