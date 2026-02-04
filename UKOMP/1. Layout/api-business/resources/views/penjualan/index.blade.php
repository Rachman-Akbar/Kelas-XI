@extends('layouts.master')

@section('title', 'Penjualan')

@section('breadcrumb')
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-600 dark:text-text-muted">Transaksi & Operasional</span>
</li>
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-900 dark:text-white">Penjualan</span>
</li>
@endsection

@section('content')
<!-- Page Header -->
<div class="flex items-center justify-between mb-8">
    <div>
        <h1 class="text-2xl font-bold dark:text-white flex items-center gap-3">
            <span class="material-symbols-outlined text-primary text-3xl">point_of_sale</span>
            Penjualan
        </h1>
        <p class="text-slate-600 dark:text-text-muted mt-1">Kelola transaksi penjualan dan invoice</p>
    </div>
    
    <a href="{{ route('penjualan.create') ?? '/penjualan/create' }}" class="flex items-center gap-2 bg-primary text-background-dark px-6 py-3 rounded-lg text-lg font-bold transition-all duration-200 hover:scale-[1.02] hover:shadow-lg hover:shadow-primary/25">
        <span class="material-symbols-outlined text-xl">add_circle</span>
        Penjualan Baru
    </a>
</div>

<!-- Statistics Cards -->
<div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-blue-100 dark:bg-blue-500/20 rounded-lg">
                <span class="material-symbols-outlined text-blue-600 dark:text-blue-400 text-xl">receipt</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Total Penjualan</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ number_format($stats['total_sales'] ?? 0) }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-green-100 dark:bg-green-500/20 rounded-lg">
                <span class="material-symbols-outlined text-green-600 dark:text-green-400 text-xl">account_balance_wallet</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Total Pendapatan</p>
        <h3 class="text-2xl font-bold dark:text-white">Rp {{ number_format($stats['total_revenue'] ?? 0, 0, ',', '.') }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-yellow-100 dark:bg-yellow-500/20 rounded-lg">
                <span class="material-symbols-outlined text-yellow-600 dark:text-yellow-400 text-xl">trending_up</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Penjualan Hari Ini</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ number_format($stats['today_sales'] ?? 0) }}</h3>
    </div>
    
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
        <div class="flex items-center justify-between mb-3">
            <div class="p-2 bg-cyan-100 dark:bg-cyan-500/20 rounded-lg">
                <span class="material-symbols-outlined text-cyan-600 dark:text-cyan-400 text-xl">inventory</span>
            </div>
        </div>
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium mb-1">Produk Terjual</p>
        <h3 class="text-2xl font-bold dark:text-white">{{ number_format($stats['total_products_sold'] ?? 0) }}</h3>
    </div>
</div>

<!-- Filters -->
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm mb-6 p-6">
    <form method="GET" action="{{ route('penjualan.index') }}" class="grid grid-cols-1 md:grid-cols-5 gap-4">
        <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-text-muted mb-2">Customer</label>
            <select name="customer_id" class="w-full px-3 py-2 border border-gray-300 dark:border-border-dark rounded-lg focus:ring-2 focus:ring-[#13eca4] focus:border-transparent transition-colors bg-white dark:bg-sidebar-dark dark:text-white">
                <option value="">Semua Customer</option>
                @foreach($customers as $customer)
                    <option value="{{ $customer->id }}" {{ request('customer_id') == $customer->id ? 'selected' : '' }}>
                        {{ $customer->nama }}
                    </option>
                @endforeach
            </select>
        </div>
        <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-text-muted mb-2">Status Bayar</label>
            <select name="payment_status" class="w-full px-3 py-2 border border-gray-300 dark:border-border-dark rounded-lg focus:ring-2 focus:ring-[#13eca4] focus:border-transparent transition-colors bg-white dark:bg-sidebar-dark dark:text-white">
                <option value="">Semua Status</option>
                <option value="paid" {{ request('payment_status') == 'paid' ? 'selected' : '' }}>Lunas</option>
                <option value="partial" {{ request('payment_status') == 'partial' ? 'selected' : '' }}>Sebagian</option>
                <option value="unpaid" {{ request('payment_status') == 'unpaid' ? 'selected' : '' }}>Belum Bayar</option>
            </select>
        </div>
        <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-text-muted mb-2">Tanggal Dari</label>
            <input type="date" name="tanggal_dari" 
                   class="w-full px-3 py-2 border border-gray-300 dark:border-border-dark rounded-lg focus:ring-2 focus:ring-[#13eca4] focus:border-transparent transition-colors bg-white dark:bg-sidebar-dark dark:text-white" 
                   value="{{ request('tanggal_dari') }}">
        </div>
        <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-text-muted mb-2">Tanggal Sampai</label>
            <input type="date" name="tanggal_sampai" 
                   class="w-full px-3 py-2 border border-gray-300 dark:border-border-dark rounded-lg focus:ring-2 focus:ring-[#13eca4] focus:border-transparent transition-colors bg-white dark:bg-sidebar-dark dark:text-white" 
                   value="{{ request('tanggal_sampai') }}">
        </div>
        <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-text-muted mb-2">Cari</label>
            <div class="flex gap-2">
                <div class="relative flex-1">
                    <span class="material-symbols-outlined absolute left-3 top-2.5 text-gray-400 text-lg">
                        search
                    </span>
                    <input type="text" name="search" 
                           class="pl-10 w-full px-3 py-2 border border-gray-300 dark:border-border-dark rounded-lg focus:ring-2 focus:ring-[#13eca4] focus:border-transparent transition-colors bg-white dark:bg-sidebar-dark dark:text-white" 
                           placeholder="No. Invoice, Customer..." 
                           value="{{ request('search') }}">
                </div>
                <button type="submit" class="px-4 py-2 bg-[#13eca4] hover:bg-[#11d197] text-white rounded-lg font-medium transition-colors duration-200 flex items-center gap-2">
                    <span class="material-symbols-outlined text-sm">
                        filter_list
                    </span>
                </button>
                <a href="{{ route('penjualan.index') }}" class="px-4 py-2 bg-gray-500 hover:bg-gray-600 text-white rounded-lg font-medium transition-colors duration-200 flex items-center gap-2">
                    <span class="material-symbols-outlined text-sm">
                        refresh
                    </span>
                </a>
            </div>
        </div>
    </form>
</div>

<!-- Table -->
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm">
    <div class="border-b border-gray-200 dark:border-border-dark p-6 flex justify-between items-center">
        <h5 class="text-lg font-semibold text-gray-900 dark:text-white">Daftar Penjualan</h5>
        <a href="{{ route('penjualan.export', request()->query()) }}" class="inline-flex items-center px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg font-medium transition-colors duration-200">
            <span class="material-symbols-outlined mr-2 text-sm">
                download
            </span>
            Export Excel
        </a>
    </div>
    
    @if($sales->count() > 0)
    <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-border-dark">
            <thead class="bg-gray-50 dark:bg-sidebar-dark">
                <tr>
                    <th class="px-6 py-3 text-left">
                        <input type="checkbox" class="w-4 h-4 text-[#13eca4] border-gray-300 rounded focus:ring-[#13eca4]" id="selectAll">
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">No</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">No. Invoice</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Tanggal</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Customer</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Total</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Dibayar</th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Status Bayar</th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Metode</th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider w-16">
                        <div class="relative" x-data="{ open: false }">
                            <button @click="open = !open" class="p-2 text-gray-400 hover:text-gray-600 rounded-lg">
                                <span class="material-symbols-outlined text-lg">more_vert</span>
                            </button>
                            <div x-show="open" @click.away="open = false" x-transition
                                 class="absolute right-0 mt-2 w-56 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
                                <div class="p-2">
                                    <div class="px-3 py-2 text-xs font-semibold text-gray-500 uppercase border-b border-gray-200 mb-1">Aksi Terpilih</div>
                                    <button class="w-full text-left px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md flex items-center gap-2" onclick="handleAction('bulk-delete'); return false;">
                                        <span class="material-symbols-outlined text-lg text-red-500">delete</span>Hapus Terpilih
                                    </button>
                                    <button class="w-full text-left px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md flex items-center gap-2" onclick="handleAction('bulk-export'); return false;">
                                        <span class="material-symbols-outlined text-lg text-green-500">download</span>Export Terpilih
                                    </button>
                                    <hr class="my-2 border-gray-200">
                                    <div class="px-3 py-2 text-xs font-semibold text-gray-500 uppercase border-b border-gray-200 mb-1">Aksi Individu</div>
                                    <button class="w-full text-left px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md flex items-center gap-2" onclick="handleAction('detail-selected'); return false;">
                                        <span class="material-symbols-outlined text-lg text-blue-500">description</span>Lihat Invoice
                                    </button>
                                    <button class="w-full text-left px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md flex items-center gap-2" onclick="handleAction('edit-selected'); return false;">
                                        <span class="material-symbols-outlined text-lg text-orange-500">edit</span>Edit
                                    </button>
                                    <button class="w-full text-left px-3 py-2 text-sm text-red-600 hover:bg-red-50 rounded-md flex items-center gap-2" onclick="handleAction('delete-selected'); return false;">
                                        <span class="material-symbols-outlined text-lg">delete</span>Hapus
                                    </button>
                                </div>
                            </div>
                        </div>
                    </th>
                </tr>
            </thead>
            <tbody class="bg-white dark:bg-background-dark divide-y divide-gray-200 dark:divide-border-dark">
                @foreach($sales as $index => $sale)
                <tr class="hover:bg-gray-50 dark:hover:bg-border-dark/30 transition-colors">
                    <td class="px-6 py-4 whitespace-nowrap">
                        <input type="checkbox" class="w-4 h-4 text-[#13eca4] border-gray-300 rounded focus:ring-[#13eca4] row-checkbox" value="{{ $sale->id }}">
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white font-medium">
                        {{ $sales->firstItem() + $index }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm font-semibold text-[#13eca4]">{{ $sale->order_number }}</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-text-muted">
                        {{ $sale->order_date->format('d M Y H:i') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        @if($sale->customer)
                            <div class="text-sm font-medium text-gray-900 dark:text-white">{{ $sale->customer->nama }}</div>
                            <div class="text-xs text-gray-500 dark:text-text-muted">{{ $sale->customer->no_telepon }}</div>
                        @else
                            <span class="text-sm text-gray-500 dark:text-text-muted italic">Walk-in Customer</span>
                        @endif
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-right">
                        <div class="text-sm font-semibold text-gray-900 dark:text-white">Rp {{ number_format($sale->total, 0, ',', '.') }}</div>
                        @if($sale->discount > 0)
                            <div class="text-xs text-gray-500 dark:text-text-muted">Diskon: Rp {{ number_format($sale->discount, 0, ',', '.') }}</div>
                        @endif
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-right text-green-600 dark:text-green-400">
                        Rp {{ number_format($sale->paid_amount, 0, ',', '.') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                        @if($sale->payment_status === 'paid')
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-800/20 dark:text-green-400">Lunas</span>
                        @elseif($sale->payment_status === 'partial')
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800 dark:bg-yellow-800/20 dark:text-yellow-400">Sebagian</span>
                        @else
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800 dark:bg-red-800/20 dark:text-red-400">Belum Bayar</span>
                        @endif
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800 dark:bg-gray-800/20 dark:text-gray-400">{{ strtoupper($sale->payment_method) }}</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                        <form id="delete-form-{{ $sale->id }}" action="#" method="POST" class="hidden">
                            @csrf
                            @method('DELETE')
                        </form>
                    </td>
                </tr>
                @endforeach
            </tbody>
        </table>
    </div>

    @if($sales->hasPages())
    <!-- Pagination -->
    <div class="px-6 py-4 border-t border-gray-200 dark:border-border-dark">
        {{ $sales->links() }}
    </div>
    @endif
</div>
@else
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark shadow-sm p-16 text-center">
    <span class="material-symbols-outlined text-6xl text-gray-300 mb-4">
        receipt_long
    </span>
    <p class="text-gray-500 text-lg font-medium mb-2">Belum ada data penjualan</p>
    <p class="text-gray-400 text-sm mb-4">Mulai dengan menambahkan penjualan pertama Anda</p>
    <a href="{{ route('penjualan.create') }}" class="inline-flex items-center px-4 py-2 bg-[#13eca4] hover:bg-[#11d197] text-white rounded-lg font-medium transition-colors duration-200">
        <span class="material-symbols-outlined mr-2 text-sm">add</span>
        Tambah Penjualan
    </a>
</div>
@endif

<!-- Delete Form -->
<form id="deleteForm" method="POST" style="display: none;">
    @csrf
    @method('DELETE')
</form>

@push('scripts')
<script>
// Generic action handler
function handleAction(action, id = null) {
    const selected = Array.from(document.querySelectorAll('.row-checkbox:checked')).map(cb => cb.value);
    
    switch(action) {
        case 'bulk-delete':
            if (selected.length === 0) {
                alert('Pilih minimal 1 penjualan untuk dihapus');
                return;
            }
            if (confirm(`Yakin ingin menghapus ${selected.length} penjualan terpilih?`)) {
                console.log('Bulk delete:', selected);
                // TODO: Implement bulk delete API
            }
            break;
            
        case 'bulk-export':
            if (selected.length === 0) {
                alert('Pilih minimal 1 penjualan untuk diexport');
                return;
            }
            console.log('Bulk export:', selected);
            // TODO: Implement bulk export
            break;
            
        case 'detail-selected':
            if (selected.length === 0) {
                alert('Pilih 1 penjualan untuk melihat invoice');
                return;
            }
            if (selected.length > 1) {
                alert('Pilih hanya 1 penjualan untuk melihat invoice');
                return;
            }
            window.location.href = `/penjualan/${selected[0]}`;
            break;
            
        case 'edit-selected':
            if (selected.length === 0) {
                alert('Pilih 1 penjualan untuk diedit');
                return;
            }
            if (selected.length > 1) {
                alert('Pilih hanya 1 penjualan untuk diedit');
                return;
            }
            window.location.href = `/penjualan/${selected[0]}/edit`;
            break;
            
        case 'delete-selected':
            if (selected.length === 0) {
                alert('Pilih minimal 1 penjualan untuk dihapus');
                return;
            }
            if (selected.length === 1) {
                const form = document.getElementById(`delete-form-${selected[0]}`);
                if (form && confirm('Yakin ingin menghapus penjualan ini?\n\nStok produk akan dikembalikan.')) {
                    form.submit();
                }
            } else {
                if (confirm(`Yakin ingin menghapus ${selected.length} penjualan terpilih?`)) {
                    console.log('Delete multiple:', selected);
                    // TODO: Implement bulk delete API
                }
            }
            break;
    }
}

// Checkbox select all
document.getElementById('selectAll')?.addEventListener('change', function() {
    const checkboxes = document.querySelectorAll('.row-checkbox');
    checkboxes.forEach(cb => cb.checked = this.checked);
});

function deleteSale(id) {
    if (confirm('Apakah Anda yakin ingin menghapus penjualan ini?\n\nStok produk akan dikembalikan.')) {
        const form = document.getElementById('deleteForm');
        form.action = `/penjualan/${id}`;
        form.submit();
    }
}
</script>
@endpush
@endsection
