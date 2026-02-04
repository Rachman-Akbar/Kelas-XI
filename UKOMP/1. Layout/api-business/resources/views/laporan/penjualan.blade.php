{{-- Summary Cards --}}
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark flex flex-col gap-2 shadow-sm">
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium">Total Penjualan</p>
        <div class="flex items-end justify-between">
            <h3 class="text-3xl font-bold dark:text-white">Rp {{ number_format($data['summary']['total_revenue'], 0, ',', '.') }}</h3>
            <span class="text-primary text-sm font-bold flex items-center bg-primary/10 px-2 py-0.5 rounded">+12.5%</span>
        </div>
    </div>
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark flex flex-col gap-2 shadow-sm">
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium">Total Order</p>
        <div class="flex items-end justify-between">
            <h3 class="text-3xl font-bold dark:text-white">{{ number_format($data['summary']['total_orders']) }}</h3>
            <span class="text-primary text-sm font-bold flex items-center bg-primary/10 px-2 py-0.5 rounded">+8.1%</span>
        </div>
    </div>
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark flex flex-col gap-2 shadow-sm">
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium">Rata-rata Order</p>
        <div class="flex items-end justify-between">
            <h3 class="text-3xl font-bold dark:text-white">Rp {{ number_format($data['summary']['avg_order_value'], 0, ',', '.') }}</h3>
            <span class="text-primary text-sm font-bold flex items-center bg-primary/10 px-2 py-0.5 rounded">+5.2%</span>
        </div>
    </div>
    <div class="bg-white dark:bg-background-dark p-6 rounded-xl border border-slate-200 dark:border-border-dark flex flex-col gap-2 shadow-sm">
        <p class="text-slate-500 dark:text-text-muted text-sm font-medium">Total Kuantitas</p>
        <div class="flex items-end justify-between">
            <h3 class="text-3xl font-bold dark:text-white">{{ number_format($data['summary']['total_quantity']) }}</h3>
            <span class="text-red-400 text-sm font-bold flex items-center bg-red-400/10 px-2 py-0.5 rounded">-2%</span>
        </div>
    </div>
</div>

{{-- Data Table --}}
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark overflow-hidden shadow-sm">
    <div class="px-6 py-4 border-b border-slate-200 dark:border-border-dark flex items-center justify-between">
        <h2 class="text-base font-bold dark:text-white">Detail Transaksi Penjualan</h2>
        <button class="text-xs font-bold text-primary hover:underline">View All</button>
    </div>
    <div class="overflow-x-auto">
        <table class="w-full text-left">
            <thead>
                <tr class="bg-slate-50 dark:bg-sidebar-dark">
                    <th class="px-6 py-3 text-[11px] font-bold text-slate-500 dark:text-text-muted uppercase">No. Order</th>
                    <th class="px-6 py-3 text-[11px] font-bold text-slate-500 dark:text-text-muted uppercase">Tanggal</th>
                    <th class="px-6 py-3 text-[11px] font-bold text-slate-500 dark:text-text-muted uppercase">Pelanggan</th>
                    <th class="px-6 py-3 text-[11px] font-bold text-slate-500 dark:text-text-muted uppercase">Item</th>
                    <th class="px-6 py-3 text-[11px] font-bold text-slate-500 dark:text-text-muted uppercase">Qty</th>
                    <th class="px-6 py-3 text-[11px] font-bold text-slate-500 dark:text-text-muted uppercase text-right">Total</th>
                    <th class="px-6 py-3 text-[11px] font-bold text-slate-500 dark:text-text-muted uppercase">Status</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-100 dark:divide-border-dark">
                @forelse($data['items'] as $order)
                <tr class="hover:bg-slate-50 dark:hover:bg-border-dark/30 transition-colors">
                    <td class="px-6 py-4 text-xs font-bold dark:text-white">{{ $order->order_number }}</td>
                    <td class="px-6 py-4 text-xs dark:text-white">{{ $order->order_date->format('d/m/Y') }}</td>
                    <td class="px-6 py-4 text-xs dark:text-text-muted">{{ $order->customer->nama ?? '-' }}</td>
                    <td class="px-6 py-4 text-xs dark:text-text-muted">{{ $order->details->count() }}</td>
                    <td class="px-6 py-4 text-xs dark:text-text-muted">{{ $order->details->sum('quantity') }}</td>
                    <td class="px-6 py-4 text-xs font-bold text-right dark:text-white">Rp {{ number_format($order->total, 0, ',', '.') }}</td>
                    <td class="px-6 py-4">
                        @if($order->payment_status == 'paid')
                            <span class="px-2 py-1 rounded-full bg-primary/10 text-primary text-[10px] font-bold">Lunas</span>
                        @elseif($order->payment_status == 'partial')
                            <span class="px-2 py-1 rounded-full bg-orange-500/10 text-orange-500 text-[10px] font-bold">Sebagian</span>
                        @else
                            <span class="px-2 py-1 rounded-full bg-red-500/10 text-red-500 text-[10px] font-bold">Belum Bayar</span>
                        @endif
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="7" class="px-6 py-8 text-center text-slate-500 dark:text-text-muted">
                        <div class="flex flex-col items-center gap-2">
                            <span class="material-symbols-outlined text-4xl opacity-50">receipt_long</span>
                            <span class="text-sm">Tidak ada data penjualan</span>
                        </div>
                    </td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>
