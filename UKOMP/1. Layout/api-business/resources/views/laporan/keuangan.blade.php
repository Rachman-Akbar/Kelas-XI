{{-- Summary Cards --}}
<div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
    <div class="bg-gradient-to-r from-green-500 to-green-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Total Pemasukan</p>
                <h3 class="text-3xl font-bold text-white mt-1">Rp {{ number_format($data['summary']['total_income'], 0, ',', '.') }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                trending_up
            </span>
        </div>
    </div>
    
    <div class="bg-gradient-to-r from-red-500 to-red-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Total Pengeluaran</p>
                <h3 class="text-3xl font-bold text-white mt-1">Rp {{ number_format($data['summary']['total_expense'], 0, ',', '.') }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                trending_down
            </span>
        </div>
    </div>
    
    <div class="bg-gradient-to-r from-[#13eca4] to-[#11d197] text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Laba Bersih</p>
                <h3 class="text-3xl font-bold text-white mt-1">Rp {{ number_format($data['summary']['net_profit'], 0, ',', '.') }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                account_balance_wallet
            </span>
        </div>
    </div>
    
    <div class="bg-gradient-to-r from-blue-500 to-blue-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Arus Kas</p>
                <h3 class="text-3xl font-bold text-white mt-1">Rp {{ number_format($data['summary']['cash_flow'], 0, ',', '.') }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                water_drop
            </span>
        </div>
    </div>
</div>

{{-- Secondary Cards --}}
<div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
    <div class="bg-gradient-to-r from-yellow-500 to-yellow-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Total Hutang</p>
                <h3 class="text-3xl font-bold text-white mt-1">Rp {{ number_format($data['summary']['total_debt'], 0, ',', '.') }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                credit_card_off
            </span>
        </div>
    </div>
    
    <div class="bg-gradient-to-r from-gray-500 to-gray-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Total Piutang</p>
                <h3 class="text-3xl font-bold text-white mt-1">Rp {{ number_format($data['summary']['total_receivable'], 0, ',', '.') }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                receipt
            </span>
        </div>
    </div>
</div>

{{-- Data Table --}}
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark overflow-hidden shadow-sm">
    <div class="px-6 py-4 border-b border-slate-200 dark:border-border-dark">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Detail Transaksi Keuangan</h2>
    </div>
    <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-border-dark">
            <thead class="bg-gray-50 dark:bg-sidebar-dark">
                <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Tanggal</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Jenis</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Kategori</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Deskripsi</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Jumlah</th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Status</th>
                </tr>
            </thead>
            <tbody class="bg-white dark:bg-background-dark divide-y divide-gray-200 dark:divide-border-dark">
                @forelse($data['items'] as $transaction)
                <tr class="hover:bg-gray-50 dark:hover:bg-border-dark/30 transition-colors">
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                        {{ \Carbon\Carbon::parse($transaction->transaction_date)->format('d/m/Y') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        @if($transaction->type == 'income')
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-800/20 dark:text-green-400">
                                <span class="material-symbols-outlined mr-1 text-sm">trending_up</span>
                                Pemasukan
                            </span>
                        @elseif($transaction->type == 'expense')
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800 dark:bg-red-800/20 dark:text-red-400">
                                <span class="material-symbols-outlined mr-1 text-sm">trending_down</span>
                                Pengeluaran
                            </span>
                        @elseif($transaction->type == 'debt')
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800 dark:bg-yellow-800/20 dark:text-yellow-400">
                                <span class="material-symbols-outlined mr-1 text-sm">credit_card_off</span>
                                Hutang
                            </span>
                        @else
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800 dark:bg-gray-800/20 dark:text-gray-400">
                                <span class="material-symbols-outlined mr-1 text-sm">receipt</span>
                                Piutang
                            </span>
                        @endif
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-text-muted">
                        {{ $transaction->category ?? '-' }}
                    </td>
                    <td class="px-6 py-4 text-sm text-gray-500 dark:text-text-muted max-w-xs truncate">
                        {{ $transaction->description ?? '-' }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-right text-gray-900 dark:text-white">
                        Rp {{ number_format($transaction->amount, 0, ',', '.') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                        @if(isset($transaction->status))
                            @if($transaction->status == 'paid')
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-800/20 dark:text-green-400">
                                    <span class="material-symbols-outlined mr-1 text-sm">check_circle</span>
                                    Lunas
                                </span>
                            @elseif($transaction->status == 'unpaid')
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800 dark:bg-red-800/20 dark:text-red-400">
                                    <span class="material-symbols-outlined mr-1 text-sm">cancel</span>
                                    Belum Lunas
                                </span>
                            @else
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800 dark:bg-yellow-800/20 dark:text-yellow-400">
                                    {{ $transaction->status }}
                                </span>
                            @endif
                        @else
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800 dark:bg-gray-800/20 dark:text-gray-400">
                                -
                            </span>
                        @endif
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="6" class="px-6 py-16 text-center">
                        <div class="flex flex-col items-center justify-center">
                            <span class="material-symbols-outlined text-6xl text-gray-300 mb-4">
                                receipt_long
                            </span>
                            <p class="text-gray-500 text-lg font-medium">Tidak ada data transaksi</p>
                            <p class="text-gray-400 text-sm mt-1">Belum ada transaksi keuangan untuk periode ini</p>
                        </div>
                    </td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>
