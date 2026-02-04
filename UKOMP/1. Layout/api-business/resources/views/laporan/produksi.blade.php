{{-- Summary Cards --}}
<div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
    <div class="bg-gradient-to-r from-[#13eca4] to-[#11d197] text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Total Produksi</p>
                <h3 class="text-3xl font-bold text-white mt-1">{{ number_format($data['summary']['total_produced']) }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                precision_manufacturing
            </span>
        </div>
    </div>
    
    <div class="bg-gradient-to-r from-green-500 to-green-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Total Biaya</p>
                <h3 class="text-3xl font-bold text-white mt-1">Rp {{ number_format($data['summary']['total_cost'], 0, ',', '.') }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                payments
            </span>
        </div>
    </div>
    
    <div class="bg-gradient-to-r from-blue-500 to-blue-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Rata-rata Biaya/Unit</p>
                <h3 class="text-3xl font-bold text-white mt-1">Rp {{ number_format($data['summary']['avg_cost_per_unit'], 0, ',', '.') }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                calculate
            </span>
        </div>
    </div>
    
    <div class="bg-gradient-to-r from-yellow-500 to-yellow-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Total Batch</p>
                <h3 class="text-3xl font-bold text-white mt-1">{{ number_format($data['summary']['total_batches']) }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                batch_prediction
            </span>
        </div>
    </div>
</div>

{{-- Data Table --}}
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark overflow-hidden shadow-sm">
    <div class="px-6 py-4 border-b border-slate-200 dark:border-border-dark">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Detail Produksi</h2>
    </div>
    <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-border-dark">
            <thead class="bg-gray-50 dark:bg-sidebar-dark">
                <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Tanggal</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Produk</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Batch</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Qty</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Biaya Total</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Biaya/Unit</th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Status</th>
                </tr>
            </thead>
            <tbody class="bg-white dark:bg-background-dark divide-y divide-gray-200 dark:divide-border-dark">
                @forelse($data['items'] as $production)
                <tr class="hover:bg-gray-50 dark:hover:bg-border-dark/30 transition-colors">
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                        {{ \Carbon\Carbon::parse($production->tanggal_produksi)->format('d/m/Y') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm font-medium text-gray-900 dark:text-white">{{ $production->product->nama ?? '-' }}</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800 dark:bg-gray-800/20 dark:text-gray-400">
                            #{{ $production->id }}
                        </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-900 dark:text-white font-semibold">
                        {{ number_format($production->quantity) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-right text-gray-900 dark:text-white">
                        Rp {{ number_format($production->total_hpp, 0, ',', '.') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-right text-gray-700 dark:text-text-muted">
                        Rp {{ number_format($production->quantity > 0 ? $production->total_hpp / $production->quantity : 0, 0, ',', '.') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-800/20 dark:text-green-400">
                            <span class="material-symbols-outlined mr-1 text-sm">check_circle</span>
                            Selesai
                        </span>
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="7" class="px-6 py-16 text-center">
                        <div class="flex flex-col items-center justify-center">
                            <span class="material-symbols-outlined text-6xl text-gray-300 mb-4">
                                manufacturing
                            </span>
                            <p class="text-gray-500 text-lg font-medium">Tidak ada data produksi</p>
                            <p class="text-gray-400 text-sm mt-1">Belum ada kegiatan produksi untuk periode ini</p>
                        </div>
                    </td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>
