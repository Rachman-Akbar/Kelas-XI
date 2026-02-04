@extends('layouts.master')

@section('title', 'Produksi')

@section('breadcrumb')
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-600 dark:text-text-muted">Transaksi & Operasional</span>
</li>
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-900 dark:text-white">Produksi</span>
</li>
@endsection

@section('content')
<!-- Page Header -->
<div class="flex items-center justify-between mb-8">
    <div>
        <h1 class="text-2xl font-bold dark:text-white flex items-center gap-3">
            <span class="material-symbols-outlined text-primary text-3xl">precision_manufacturing</span>
            Produksi
        </h1>
        <p class="text-slate-600 dark:text-text-muted mt-1">Kelola proses produksi produk dari bahan baku</p>
    </div>
    
    <a href="{{ route('produksi.create') ?? '/produksi/create' }}" class="flex items-center gap-2 bg-primary text-background-dark px-4 py-2.5 rounded-lg text-sm font-bold transition-all duration-200 hover:scale-[1.02] hover:shadow-lg hover:shadow-primary/25">
        <span class="material-symbols-outlined text-lg">add_circle</span>
        Produksi Baru
    </a>
</div>

<!-- Statistics Cards -->
<div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-blue-100 dark:bg-blue-500/20 rounded-lg">
                <span class="material-symbols-outlined text-blue-600 dark:text-blue-400 text-xl">list_alt</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Total Produksi</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ number_format($stats['total_productions'] ?? 0) }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-green-100 dark:bg-green-500/20 rounded-lg">
                <span class="material-symbols-outlined text-green-600 dark:text-green-400 text-xl">inventory</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Total Unit Diproduksi</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ number_format($stats['total_quantity_produced'] ?? 0) }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-orange-100 dark:bg-orange-500/20 rounded-lg">
                <span class="material-symbols-outlined text-orange-600 dark:text-orange-400 text-xl">payments</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Total Biaya Produksi</p>
        <h3 class="text-2xl font-bold dark:text-white">Rp {{ number_format($stats['total_production_cost'] ?? 0, 0, ',', '.') }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-cyan-100 dark:bg-cyan-500/20 rounded-lg">
                <span class="material-symbols-outlined text-cyan-600 dark:text-cyan-400 text-xl">calendar_month</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Produksi Bulan Ini</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ number_format($stats['productions_this_month'] ?? 0) }}</h3>
    </div>
</div>

    <!-- Filters -->
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm mb-6">
    <div class="p-6">
        <form method="GET" action="{{ route('produksi.index') }}" class="grid grid-cols-1 md:grid-cols-6 gap-4">
            <div class="md:col-span-2">
                <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-2">Produk</label>
                <select name="product_id" class="w-full px-3 py-2 border border-slate-300 dark:border-border-dark rounded-lg bg-white dark:bg-background-dark text-slate-900 dark:text-white focus:border-primary focus:ring-1 focus:ring-primary">
                    <option value="">Semua Produk</option>
                    @foreach($products as $product)
                        <option value="{{ $product->id }}" {{ request('product_id') == $product->id ? 'selected' : '' }}>
                            {{ $product->nama }} ({{ $product->sku }})
                        </option>
                    @endforeach
                </select>
            </div>
            <div>
                <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-2">Tanggal Dari</label>
                <input type="date" name="tanggal_dari" class="w-full px-3 py-2 border border-slate-300 dark:border-border-dark rounded-lg bg-white dark:bg-background-dark text-slate-900 dark:text-white focus:border-primary focus:ring-1 focus:ring-primary" value="{{ request('tanggal_dari') }}">
            </div>
            <div>
                <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-2">Tanggal Sampai</label>
                <input type="date" name="tanggal_sampai" class="w-full px-3 py-2 border border-slate-300 dark:border-border-dark rounded-lg bg-white dark:bg-background-dark text-slate-900 dark:text-white focus:border-primary focus:ring-1 focus:ring-primary" value="{{ request('tanggal_sampai') }}">
            </div>
            <div>
                <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-2">Cari</label>
                <input type="text" name="search" class="w-full px-3 py-2 border border-slate-300 dark:border-border-dark rounded-lg bg-white dark:bg-background-dark text-slate-900 dark:text-white focus:border-primary focus:ring-1 focus:ring-primary" placeholder="Nama produk, SKU, catatan..." value="{{ request('search') }}">
            </div>
            <div class="flex items-end gap-2">
                <button type="submit" class="flex-1 flex items-center justify-center gap-2 bg-primary text-background-dark px-4 py-2 rounded-lg text-sm font-medium hover:bg-primary/90 transition-colors">
                    <span class="material-symbols-outlined text-lg">search</span>
                    Filter
                </button>
                <a href="{{ route('produksi.index') }}" class="flex items-center justify-center px-3 py-2 bg-slate-500 text-white rounded-lg hover:bg-slate-600 transition-colors">
                    <span class="material-symbols-outlined text-lg">refresh</span>
                </a>
            </div>
        </form>
    </div>
</div>

<!-- Tabel Produksi -->
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm overflow-hidden">
    <!-- Table Header -->
    <div class="px-6 py-4 border-b border-slate-200 dark:border-border-dark">
        <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold dark:text-white flex items-center gap-2">
                <span class="material-symbols-outlined text-primary">table_view</span>
                Daftar Produksi
            </h3>
            <a href="{{ route('produksi.export', request()->query()) ?? '#' }}" class="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg text-sm font-medium transition-all duration-200 hover:bg-green-700 hover:scale-[1.02] hover:shadow-md hover:shadow-green-600/25">
                <span class="material-symbols-outlined text-lg">download</span>
                Export Excel
            </a>
        </div>
    </div>
    <!-- Table Content -->
    <div class="overflow-x-auto">
        @if($productions->count() > 0)
        <table class="w-full">
            <thead>
                <tr class="bg-slate-50 dark:bg-background-dark/50 border-b border-slate-200 dark:border-border-dark">
                    <th class="px-4 py-4 text-left">
                        <input type="checkbox" id="selectAll" class="w-4 h-4 rounded border-border dark:border-border-dark text-primary focus:ring-primary/20">
                    </th>
                    <th class="px-4 py-4 text-left text-xs font-medium text-slate-500 dark:text-text-muted uppercase tracking-wider">No</th>
                    <th class="px-4 py-4 text-left text-xs font-medium text-slate-500 dark:text-text-muted uppercase tracking-wider">
                        <div class="flex items-center gap-2">
                            <span class="material-symbols-outlined text-base">event</span>
                            Tanggal
                        </div>
                    </th>
                    <th class="px-4 py-4 text-left text-xs font-medium text-slate-500 dark:text-text-muted uppercase tracking-wider">
                        <div class="flex items-center gap-2">
                            <span class="material-symbols-outlined text-base">inventory_2</span>
                            Produk
                        </div>
                    </th>
                    <th class="px-4 py-4 text-left text-xs font-medium text-slate-500 dark:text-text-muted uppercase tracking-wider">
                        <div class="flex items-center gap-2">
                            <span class="material-symbols-outlined text-base">scale</span>
                            Jumlah
                        </div>
                    </th>
                    <th class="px-4 py-4 text-left text-xs font-medium text-slate-500 dark:text-text-muted uppercase tracking-wider">
                        <div class="flex items-center gap-2">
                            <span class="material-symbols-outlined text-base">payments</span>
                            Biaya Bahan
                        </div>
                    </th>
                    <th class="px-4 py-4 text-left text-xs font-medium text-slate-500 dark:text-text-muted uppercase tracking-wider">
                        <div class="flex items-center gap-2">
                            <span class="material-symbols-outlined text-base">add_circle</span>
                            Biaya Tambahan
                        </div>
                    </th>
                    <th class="px-4 py-4 text-left text-xs font-medium text-slate-500 dark:text-text-muted uppercase tracking-wider">
                        <div class="flex items-center gap-2">
                            <span class="material-symbols-outlined text-base">account_balance</span>
                            Total HPP
                        </div>
                    </th>
                    <th class="px-4 py-4 text-left text-xs font-medium text-slate-500 dark:text-text-muted uppercase tracking-wider">
                        <div class="flex items-center gap-2">
                            <span class="material-symbols-outlined text-base">person</span>
                            Operator
                        </div>
                    </th>
                    <th class="px-4 py-4 text-center text-xs font-medium text-slate-500 dark:text-text-muted uppercase tracking-wider">
                        <div class="flex items-center justify-center gap-2" x-data="{ open: false }">
                            <span class="material-symbols-outlined text-base">settings</span>
                            <div class="relative">
                                <button @click="open = !open" class="p-1 rounded hover:bg-slate-200 dark:hover:bg-slate-600 transition-colors">
                                    <span class="material-symbols-outlined text-lg">more_vert</span>
                                </button>
                                <div x-show="open" @click.outside="open = false" x-transition class="absolute right-0 mt-2 w-48 bg-white dark:bg-background-dark rounded-lg shadow-lg border border-border dark:border-border-dark z-10">
                                    <div class="p-2">
                                        <h6 class="px-2 py-1 text-xs font-semibold text-slate-500 dark:text-text-muted uppercase">Aksi Terpilih</h6>
                                        <button onclick="handleAction('bulk-delete')" class="w-full text-left px-2 py-1 text-sm text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700 rounded flex items-center gap-2">
                                            <span class="material-symbols-outlined text-base text-red-500">delete</span>
                                            Hapus Terpilih
                                        </button>
                                        <button onclick="handleAction('bulk-export')" class="w-full text-left px-2 py-1 text-sm text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700 rounded flex items-center gap-2">
                                            <span class="material-symbols-outlined text-base text-green-500">download</span>
                                            Export Terpilih
                                        </button>
                                        <hr class="my-1 border-border dark:border-border-dark">
                                        <h6 class="px-2 py-1 text-xs font-semibold text-slate-500 dark:text-text-muted uppercase">Aksi Individu</h6>
                                        <button onclick="handleAction('detail-selected')" class="w-full text-left px-2 py-1 text-sm text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700 rounded flex items-center gap-2">
                                            <span class="material-symbols-outlined text-base text-blue-500">visibility</span>
                                            Lihat Detail
                                        </button>
                                        <button onclick="handleAction('edit-selected')" class="w-full text-left px-2 py-1 text-sm text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700 rounded flex items-center gap-2">
                                            <span class="material-symbols-outlined text-base text-yellow-500">edit</span>
                                            Edit
                                        </button>
                                        <button onclick="handleAction('delete-selected')" class="w-full text-left px-2 py-1 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/30 rounded flex items-center gap-2">
                                            <span class="material-symbols-outlined text-base">delete</span>
                                            Hapus
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-200 dark:divide-border-dark">
                @foreach($productions as $index => $production)
                <tr class="hover:bg-slate-50/50 dark:hover:bg-background-dark/50 transition-colors group">
                    <td class="px-4 py-4">
                        <input type="checkbox" class="row-checkbox w-4 h-4 rounded border-border dark:border-border-dark text-primary focus:ring-primary/20" value="{{ $production->id }}">
                    </td>
                    <td class="px-4 py-4 text-sm font-medium text-slate-900 dark:text-white">{{ $productions->firstItem() + $index }}</td>
                    <td class="px-4 py-4">
                        <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400">
                            <span class="material-symbols-outlined text-sm">event</span>
                            {{ date('d M Y', strtotime($production->tanggal_produksi)) }}
                        </span>
                    </td>
                    <td class="px-4 py-4">
                        <div class="flex items-center gap-3">
                            <div class="w-10 h-10 bg-slate-100 dark:bg-background-dark rounded-lg flex items-center justify-center">
                                <span class="material-symbols-outlined text-slate-600 dark:text-text-muted">inventory</span>
                            </div>
                            <div>
                                <div class="text-sm font-medium text-slate-900 dark:text-white">{{ $production->product->nama ?? $production->produk->nama_produk ?? 'Produk Tidak Ditemukan' }}</div>
                                <div class="text-xs text-slate-500 dark:text-text-muted">{{ $production->product->sku ?? $production->produk->kode_produk ?? '-' }}</div>
                            </div>
                        </div>
                    </td>
                    <td class="px-4 py-4">
                        <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium bg-primary/10 text-primary">
                            <span class="material-symbols-outlined text-sm">scale</span>
                            {{ number_format($production->quantity ?? $production->jumlah_produksi) }} unit
                        </span>
                    </td>
                    <td class="px-4 py-4">
                        <span class="text-sm font-semibold text-slate-900 dark:text-white">Rp {{ number_format($production->total_biaya_bahan ?? 0, 0, ',', '.') }}</span>
                    </td>
                    <td class="px-4 py-4">
                        @if(($production->biaya_tambahan ?? 0) > 0)
                            <span class="text-sm font-semibold text-yellow-600 dark:text-yellow-400">Rp {{ number_format($production->biaya_tambahan, 0, ',', '.') }}</span>
                        @else
                            <span class="text-sm text-slate-500 dark:text-text-muted">-</span>
                        @endif
                    </td>
                    <td class="px-4 py-4">
                        <span class="text-sm font-bold text-green-600 dark:text-green-400">Rp {{ number_format($production->total_hpp ?? $production->total_biaya ?? 0, 0, ',', '.') }}</span>
                    </td>
                    <td class="px-4 py-4">
                        <div class="flex items-center gap-2">
                            <div class="w-6 h-6 bg-slate-100 dark:bg-background-dark rounded-full flex items-center justify-center">
                                <span class="material-symbols-outlined text-xs text-slate-600 dark:text-text-muted">person</span>
                            </div>
                            <span class="text-xs text-slate-500 dark:text-text-muted">{{ $production->user->name ?? 'Unknown' }}</span>
                        </div>
                    </td>
                    <td class="px-4 py-4 text-center">
                        <form id="delete-form-{{ $production->id }}" action="#" method="POST" style="display: none;">
                            @csrf
                            @method('DELETE')
                        </form>
                    </td>
                </tr>
                @endforeach
            </tbody>
        </table>

        <!-- Pagination -->
        <div class="px-6 py-4 border-t border-slate-200 dark:border-border-dark">
            {{ $productions->links() }}
        </div>
        @else
        <div class="flex flex-col items-center justify-center py-16">
            <div class="w-20 h-20 bg-slate-100 dark:bg-background-dark rounded-full flex items-center justify-center mb-6">
                <span class="material-symbols-outlined text-slate-400 dark:text-text-muted text-4xl">precision_manufacturing</span>
            </div>
            <h3 class="text-lg font-semibold text-slate-900 dark:text-white mb-2">Belum ada data produksi</h3>
            <p class="text-slate-500 dark:text-text-muted text-center mb-6">Data produksi akan muncul setelah Anda menambah produksi baru</p>
            <a href="{{ route('produksi.create') ?? '/produksi/create' }}" class="flex items-center gap-2 bg-primary text-background-dark px-6 py-3 rounded-lg font-medium transition-all duration-200 hover:scale-[1.02] hover:shadow-lg hover:shadow-primary/25">
                <span class="material-symbols-outlined">add_circle</span>
                Tambah Produksi
            </a>
        </div>
        @endif
    </div>
</div>
</div>

<!-- Delete Form -->
<form id="deleteForm" method="POST" style="display: none;">
    @csrf
    @method('DELETE')
</form>
@endsection

@push('scripts')
<script>
// Generic action handler
function handleAction(action, id = null) {
    const selected = Array.from(document.querySelectorAll('.row-checkbox:checked')).map(cb => cb.value);
    
    switch(action) {
        case 'bulk-delete':
            if (selected.length === 0) {
                Swal.fire({
                    title: 'Peringatan',
                    text: 'Pilih minimal 1 produksi untuk dihapus',
                    icon: 'warning',
                    confirmButtonColor: '#13eca4'
                });
                return;
            }
            Swal.fire({
                title: `Hapus ${selected.length} Produksi?`,
                text: 'Data yang sudah dihapus tidak dapat dikembalikan!',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#dc3545',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Ya, Hapus!',
                cancelButtonText: 'Batal'
            }).then((result) => {
                if (result.isConfirmed) {
                    console.log('Bulk delete:', selected);
                    // TODO: Implement bulk delete API
                }
            });
            break;
            
        case 'bulk-export':
            if (selected.length === 0) {
                Swal.fire({
                    title: 'Peringatan',
                    text: 'Pilih minimal 1 produksi untuk diexport',
                    icon: 'warning',
                    confirmButtonColor: '#13eca4'
                });
                return;
            }
            console.log('Bulk export:', selected);
            // TODO: Implement bulk export
            break;
            
        case 'detail-selected':
            if (selected.length === 0) {
                Swal.fire({
                    title: 'Peringatan',
                    text: 'Pilih 1 produksi untuk melihat detail',
                    icon: 'warning',
                    confirmButtonColor: '#13eca4'
                });
                return;
            }
            if (selected.length > 1) {
                Swal.fire({
                    title: 'Peringatan',
                    text: 'Pilih hanya 1 produksi untuk melihat detail',
                    icon: 'warning',
                    confirmButtonColor: '#13eca4'
                });
                return;
            }
            window.location.href = `/produksi/${selected[0]}`;
            break;
            
        case 'edit-selected':
            if (selected.length === 0) {
                Swal.fire({
                    title: 'Peringatan',
                    text: 'Pilih 1 produksi untuk diedit',
                    icon: 'warning',
                    confirmButtonColor: '#13eca4'
                });
                return;
            }
            if (selected.length > 1) {
                Swal.fire({
                    title: 'Peringatan',
                    text: 'Pilih hanya 1 produksi untuk diedit',
                    icon: 'warning',
                    confirmButtonColor: '#13eca4'
                });
                return;
            }
            window.location.href = `/produksi/${selected[0]}/edit`;
            break;
            
        case 'delete-selected':
            if (selected.length === 0) {
                Swal.fire({
                    title: 'Peringatan',
                    text: 'Pilih minimal 1 produksi untuk dihapus',
                    icon: 'warning',
                    confirmButtonColor: '#13eca4'
                });
                return;
            }
            if (selected.length === 1) {
                const form = document.getElementById(`delete-form-${selected[0]}`);
                if (form) {
                    Swal.fire({
                        title: 'Hapus Produksi?',
                        text: 'Yakin ingin menghapus produksi ini? Stok bahan baku akan dikembalikan dan stok produk akan dikurangi.',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#dc3545',
                        cancelButtonColor: '#6c757d',
                        confirmButtonText: 'Ya, Hapus!',
                        cancelButtonText: 'Batal'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            form.submit();
                        }
                    });
                }
            } else {
                Swal.fire({
                    title: `Hapus ${selected.length} Produksi?`,
                    text: 'Data yang sudah dihapus tidak dapat dikembalikan!',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#dc3545',
                    cancelButtonColor: '#6c757d',
                    confirmButtonText: 'Ya, Hapus!',
                    cancelButtonText: 'Batal'
                }).then((result) => {
                    if (result.isConfirmed) {
                        console.log('Delete multiple:', selected);
                        // TODO: Implement bulk delete API
                    }
                });
            }
            break;
    }
}

// Checkbox select all
document.getElementById('selectAll')?.addEventListener('change', function() {
    const checkboxes = document.querySelectorAll('.row-checkbox');
    checkboxes.forEach(cb => cb.checked = this.checked);
});

function deleteProduction(id) {
    const form = document.getElementById('deleteForm');
    form.action = `/produksi/${id}`;
    
    Swal.fire({
        title: 'Hapus Produksi?',
        text: 'Apakah Anda yakin ingin menghapus produksi ini? Stok bahan baku akan dikembalikan dan stok produk akan dikurangi.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Ya, Hapus!',
        cancelButtonText: 'Batal'
    }).then((result) => {
        if (result.isConfirmed) {
            form.submit();
        }
    });
}
</script>
@endpush
