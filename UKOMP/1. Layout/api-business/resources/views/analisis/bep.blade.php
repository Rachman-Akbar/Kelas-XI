<!-- KPI Cards -->
<div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
        <div class="bg-[#1a2d27] p-6 rounded-xl border border-[#13eca4]/30 shadow-sm">
            <p class="text-white/60 text-xs font-bold uppercase tracking-wider mb-2">Total Biaya Tetap</p>
            <div class="flex items-baseline gap-2">
                <h3 class="text-3xl font-black text-white">Rp {{ number_format($data['summary']['total_fixed_cost'] ?? 0, 0, ',', '.') }}</h3>
            </div>
            <div class="mt-4 flex items-center gap-1 text-yellow-400 text-xs font-medium">
                <span class="material-symbols-outlined text-sm">account_balance</span>
                Biaya operasional
            </div>
        </div>

        <div class="bg-[#1a2d27] p-6 rounded-xl border border-[#13eca4]/30 shadow-sm">
            <p class="text-white/60 text-xs font-bold uppercase tracking-wider mb-2">Total Produk</p>
            <div class="flex items-baseline gap-2">
                <h3 class="text-3xl font-black text-white">{{ $data['summary']['total_products'] }}</h3>
                <span class="text-white/40 text-sm">Items</span>
            </div>
            <div class="mt-4 flex items-center gap-1 text-[#13eca4] text-xs font-medium">
                <span class="material-symbols-outlined text-sm">inventory_2</span>
                Produk aktif
            </div>
        </div>

        <div class="bg-[#1a2d27] p-6 rounded-xl border border-[#13eca4]/30 shadow-sm">
            <p class="text-white/60 text-xs font-bold uppercase tracking-wider mb-2">Produk Profit</p>
            <div class="flex items-baseline gap-2">
                <h3 class="text-3xl font-black text-white">{{ $data['summary']['profitable_products'] }}</h3>
                <span class="text-white/40 text-sm">/ {{ $data['summary']['total_products'] }}</span>
            </div>
            <div class="mt-4 flex items-center gap-1 text-green-400 text-xs font-medium">
                <span class="material-symbols-outlined text-sm">trending_up</span>
                Above break-even
            </div>
        </div>

        <div class="bg-[#1a2d27] p-6 rounded-xl border border-[#13eca4]/30 shadow-sm">
            <p class="text-white/60 text-xs font-bold uppercase tracking-wider mb-2">Total Pendapatan</p>
            <div class="flex items-baseline gap-2">
                <h3 class="text-3xl font-black text-[#13eca4]">Rp {{ number_format($data['summary']['total_revenue'] ?? 0, 0, ',', '.') }}</h3>
            </div>
            <div class="mt-4 flex items-center gap-1 text-[#13eca4] text-xs font-medium">
                <span class="material-symbols-outlined text-sm">payments</span>
                Revenue stream
            </div>
        </div>
    </div>

    <!-- BEP Visualization Chart -->
    <div class="bg-[#1a2d27] rounded-xl border border-[#13eca4]/30 overflow-hidden mb-8">
        <div class="px-6 py-4 border-b border-[#13eca4]/30 flex justify-between items-center">
            <h3 class="text-sm font-bold text-white">Break-Even Point Visualization</h3>
            <div class="flex gap-2">
                <button onclick="switchBEPChart('bar')" class="px-3 py-1 text-xs rounded-lg bg-[#13eca4]/20 text-[#13eca4] hover:bg-[#13eca4]/30 transition-colors">Bar</button>
                <button onclick="switchBEPChart('line')" class="px-3 py-1 text-xs rounded-lg bg-white/10 text-white/60 hover:bg-white/20 transition-colors">Line</button>
            </div>
        </div>
        <div class="p-6">
            <canvas id="bepChart" height="300"></canvas>
        </div>
    </div>

    <!-- Main Content Grid -->
    <div class="grid grid-cols-1 xl:grid-cols-12 gap-8">
        <!-- Left Column: Product List -->
        <div class="xl:col-span-8">
            <div class="bg-[#1a2d27] rounded-xl border border-[#13eca4]/30 overflow-hidden shadow-sm">
                <div class="px-6 py-4 border-b border-[#13eca4]/30 flex justify-between items-center">
                    <h3 class="text-sm font-bold text-white">Detail Analisis per Produk</h3>
                    <button class="text-[#13eca4] text-xs font-bold hover:underline">View All Products</button>
                </div>
                <div class="overflow-x-auto">
                    <table class="w-full text-left text-sm">
                        <thead class="bg-[#10221c] text-white/60 font-medium">
                            <tr>
                                <th class="px-6 py-3">Produk</th>
                                <th class="px-6 py-3 text-right">HPP</th>
                                <th class="px-6 py-3 text-right">Harga Jual</th>
                                <th class="px-6 py-3 text-right">BEP (Unit)</th>
                                <th class="px-6 py-3 text-right">BEP (Rp)</th>
                                <th class="px-6 py-3 text-right">Sales Aktual</th>
                                <th class="px-6 py-3 text-center">Status</th>
                                <th class="px-6 py-3 text-center">Detail</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-[#13eca4]/10">
                            @forelse($data['items'] as $index => $item)
                            <tr class="hover:bg-[#13eca4]/5 transition-colors">
                                <td class="px-6 py-3">
                                    <div class="font-medium text-white">{{ $item['product']->nama }}</div>
                                    <div class="text-white/40 text-xs">{{ $item['product']->kategori }}</div>
                                </td>
                                <td class="px-6 py-3 text-right text-white">Rp {{ number_format($item['hpp'], 0, ',', '.') }}</td>
                                <td class="px-6 py-3 text-right text-white font-semibold">Rp {{ number_format($item['selling_price'], 0, ',', '.') }}</td>
                                <td class="px-6 py-3 text-right text-white">{{ number_format($item['bep_units'], 0) }}</td>
                                <td class="px-6 py-3 text-right text-white">Rp {{ number_format($item['bep_rupiah'], 0, ',', '.') }}</td>
                                <td class="px-6 py-3 text-right">
                                    <div class="text-white">{{ number_format($item['actual_sales_units'], 0) }} unit</div>
                                    <div class="text-white/40 text-xs">Rp {{ number_format($item['actual_sales_rupiah'], 0, ',', '.') }}</div>
                                </td>
                                <td class="px-6 py-3 text-center">
                                    @if($item['status'] === 'Profit')
                                        <span class="px-2 py-0.5 rounded-full bg-green-500/20 text-green-400 text-[10px] font-bold border border-green-500/30">PROFIT</span>
                                    @elseif($item['status'] === 'Loss')
                                        <span class="px-2 py-0.5 rounded-full bg-red-500/20 text-red-400 text-[10px] font-bold border border-red-500/30">LOSS</span>
                                    @else
                                        <span class="px-2 py-0.5 rounded-full bg-gray-500/20 text-gray-400 text-[10px] font-bold border border-gray-500/30">NO SALES</span>
                                    @endif
                                </td>
                                <td class="px-6 py-3 text-center">
                                    <button onclick="toggleBEPBreakdown('bep-{{ $index }}')" class="px-3 py-1.5 rounded-lg bg-[#13eca4]/20 text-[#13eca4] hover:bg-[#13eca4]/30 transition-colors text-xs font-bold flex items-center gap-1 mx-auto">
                                        <span class="material-symbols-outlined text-sm">expand_more</span>
                                        Breakdown
                                    </button>
                                </td>
                            </tr>
                            <!-- Breakdown Row -->
                            <tr id="bep-{{ $index }}" class="breakdown-row hidden">
                                <td colspan="8" class="px-6 py-4 bg-[#10221c]">
                                    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                                        <!-- BEP Analysis -->
                                        <div>
                                            <h4 class="text-white font-bold mb-4 flex items-center gap-2">
                                                <span class="material-symbols-outlined text-[#13eca4]">analytics</span>
                                                Analisis Break-Even Point
                                            </h4>
                                            <div class="space-y-3">
                                                <div class="bg-[#1a2d27] rounded-lg p-4 border border-[#13eca4]/20">
                                                    <p class="text-white/60 text-xs mb-3">Contribution Margin Analysis</p>
                                                    <div class="space-y-2">
                                                        <div class="flex justify-between items-center">
                                                            <span class="text-white text-sm">Harga Jual</span>
                                                            <span class="text-white font-bold">Rp {{ number_format($item['selling_price'], 0, ',', '.') }}</span>
                                                        </div>
                                                        <div class="flex justify-between items-center">
                                                            <span class="text-white text-sm">HPP per Unit</span>
                                                            <span class="text-white font-bold">Rp {{ number_format($item['hpp'], 0, ',', '.') }}</span>
                                                        </div>
                                                        <div class="flex justify-between items-center pt-2 border-t border-[#13eca4]/20">
                                                            <span class="text-white font-medium">Contribution Margin</span>
                                                            <span class="text-[#13eca4] font-bold text-lg">Rp {{ number_format($item['contribution_margin'], 0, ',', '.') }}</span>
                                                        </div>
                                                        <div class="flex justify-between items-center">
                                                            <span class="text-white/60 text-xs">Margin Ratio</span>
                                                            <span class="text-[#13eca4] font-bold">{{ number_format($item['contribution_margin_ratio'], 1) }}%</span>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="bg-gradient-to-br from-yellow-500/20 to-yellow-500/5 rounded-lg p-4 border border-yellow-500/30">
                                                    <p class="text-white/60 text-xs mb-3">Break-Even Point</p>
                                                    <div class="grid grid-cols-2 gap-3">
                                                        <div>
                                                            <p class="text-white/60 text-xs mb-1">BEP Units</p>
                                                            <p class="text-white font-bold text-xl">{{ number_format($item['bep_units'], 0) }}</p>
                                                            <p class="text-white/40 text-xs">unit</p>
                                                        </div>
                                                        <div>
                                                            <p class="text-white/60 text-xs mb-1">BEP Rupiah</p>
                                                            <p class="text-white font-bold text-sm">Rp {{ number_format($item['bep_rupiah'], 0, ',', '.') }}</p>
                                                        </div>
                                                    </div>
                                                </div>

                                                @if($item['margin_safety'] > 0)
                                                <div class="bg-green-500/10 rounded-lg p-4 border border-green-500/20">
                                                    <div class="flex items-center gap-2 mb-2">
                                                        <span class="material-symbols-outlined text-green-400 text-sm">shield</span>
                                                        <p class="text-white/60 text-xs">Margin of Safety</p>
                                                    </div>
                                                    <p class="text-green-400 font-bold text-2xl">{{ number_format($item['margin_safety'], 1) }}%</p>
                                                    <p class="text-white/60 text-xs mt-1">
                                                        @if($item['margin_safety'] > 50)
                                                            Sangat Aman - Buffer tinggi terhadap kerugian
                                                        @elseif($item['margin_safety'] > 25)
                                                            Aman - Buffer memadai
                                                        @else
                                                            Perhatian - Mendekati BEP
                                                        @endif
                                                    </p>
                                                </div>
                                                @endif
                                            </div>
                                        </div>

                                        <!-- Sales Performance -->
                                        <div>
                                            <h4 class="text-white font-bold mb-4 flex items-center gap-2">
                                                <span class="material-symbols-outlined text-[#13eca4]">trending_up</span>
                                                Sales Performance
                                            </h4>
                                            <div class="space-y-3">
                                                <div class="bg-[#1a2d27] rounded-lg p-4 border border-[#13eca4]/20">
                                                    <p class="text-white/60 text-xs mb-3">Actual Sales vs BEP</p>
                                                    <div class="space-y-3">
                                                        <div>
                                                            <div class="flex justify-between items-center mb-1">
                                                                <span class="text-white text-sm">Actual Sales</span>
                                                                <span class="text-[#13eca4] font-bold">{{ number_format($item['actual_sales_units'], 0) }} unit</span>
                                                            </div>
                                                            <div class="w-full bg-white/10 rounded-full h-2 overflow-hidden">
                                                                @php
                                                                    $salesPercentage = $item['bep_units'] > 0 ? min(($item['actual_sales_units'] / $item['bep_units']) * 100, 200) : 0;
                                                                @endphp
                                                                <div class="bg-[#13eca4] h-full rounded-full" style="width: {{ $salesPercentage }}%"></div>
                                                            </div>
                                                            <p class="text-white/40 text-xs mt-1">{{ number_format($salesPercentage, 0) }}% dari BEP</p>
                                                        </div>

                                                        <div>
                                                            <div class="flex justify-between items-center mb-1">
                                                                <span class="text-white text-sm">Target BEP</span>
                                                                <span class="text-yellow-400 font-bold">{{ number_format($item['bep_units'], 0) }} unit</span>
                                                            </div>
                                                        </div>

                                                        @if($item['status'] === 'Profit')
                                                        <div class="pt-2 border-t border-[#13eca4]/20">
                                                            <p class="text-white/60 text-xs mb-1">Profit Earned</p>
                                                            <p class="text-green-400 font-bold text-lg">Rp {{ number_format($item['actual_sales_rupiah'] - $item['bep_rupiah'], 0, ',', '.') }}</p>
                                                        </div>
                                                        @elseif($item['status'] === 'Loss')
                                                        <div class="pt-2 border-t border-red-500/20">
                                                            <p class="text-white/60 text-xs mb-1">Units Needed to Break-Even</p>
                                                            <p class="text-red-400 font-bold text-lg">{{ number_format($item['bep_units'] - $item['actual_sales_units'], 0) }} unit</p>
                                                        </div>
                                                        @endif
                                                    </div>
                                                </div>

                                                <div class="bg-[#1a2d27] rounded-lg p-4 border border-blue-500/20">
                                                    <p class="text-white/60 text-xs mb-3">Revenue Analysis</p>
                                                    <div class="space-y-2">
                                                        <div class="flex justify-between items-center">
                                                            <span class="text-white text-sm">Total Revenue</span>
                                                            <span class="text-blue-400 font-bold">Rp {{ number_format($item['actual_sales_rupiah'], 0, ',', '.') }}</span>
                                                        </div>
                                                        <div class="flex justify-between items-center">
                                                            <span class="text-white text-sm">BEP Revenue</span>
                                                            <span class="text-yellow-400 font-bold">Rp {{ number_format($item['bep_rupiah'], 0, ',', '.') }}</span>
                                                        </div>
                                                        <div class="flex justify-between items-center pt-2 border-t border-blue-500/20">
                                                            <span class="text-white font-medium">Gap</span>
                                                            <span class="{{ $item['status'] === 'Profit' ? 'text-green-400' : 'text-red-400' }} font-bold">
                                                                {{ $item['status'] === 'Profit' ? '+' : '-' }}Rp {{ number_format(abs($item['actual_sales_rupiah'] - $item['bep_rupiah']), 0, ',', '.') }}
                                                            </span>
                                                        </div>
                                                    </div>
                                                </div>

                                                @if($item['status'] === 'No Sales')
                                                <div class="bg-gray-500/10 rounded-lg p-4 border border-gray-500/20">
                                                    <div class="flex items-center gap-2 mb-2">
                                                        <span class="material-symbols-outlined text-gray-400 text-sm">info</span>
                                                        <p class="text-white font-medium text-sm">Belum Ada Penjualan</p>
                                                    </div>
                                                    <p class="text-white/60 text-xs">Mulai promosikan produk ini untuk mencapai target BEP {{ number_format($item['bep_units'], 0) }} unit</p>
                                                </div>
                                                @endif
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            @empty
                            <tr>
                                <td colspan="8" class="text-center py-12 text-white/40">
                                    <span class="material-symbols-outlined text-5xl mb-3 block opacity-20">inventory_2</span>
                                    Tidak ada data produk
                                </td>
                            </tr>
                            @endforelse
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Right Column: Summary & Insights -->
        <div class="xl:col-span-4 flex flex-col gap-6">
            <!-- Quick Stats -->
            <div class="bg-[#1a2d27] p-6 rounded-xl border border-[#13eca4]/30">
                <h2 class="text-white text-lg font-bold mb-6 flex items-center gap-2">
                    <span class="material-symbols-outlined text-[#13eca4]">analytics</span> Quick Insights
                </h2>
                <div class="space-y-4">
                    <div class="flex justify-between items-center pb-3 border-b border-[#13eca4]/10">
                        <span class="text-white/60 text-sm">Avg BEP per Product</span>
                        <span class="text-white font-bold">
                            @if($data['summary']['total_products'] > 0)
                                {{ number_format(collect($data['items'])->avg('bep_units'), 0) }} units
                            @else
                                0 units
                            @endif
                        </span>
                    </div>
                    <div class="flex justify-between items-center pb-3 border-b border-[#13eca4]/10">
                        <span class="text-white/60 text-sm">Contribution Margin</span>
                        <span class="text-[#13eca4] font-bold">
                            @if($data['summary']['total_products'] > 0)
                                {{ number_format(collect($data['items'])->avg('contribution_margin_ratio'), 1) }}%
                            @else
                                0%
                            @endif
                        </span>
                    </div>
                    <div class="flex justify-between items-center">
                        <span class="text-white/60 text-sm">Profit Products Ratio</span>
                        <span class="text-green-400 font-bold">
                            @if($data['summary']['total_products'] > 0)
                                {{ number_format(($data['summary']['profitable_products'] / $data['summary']['total_products']) * 100, 1) }}%
                            @else
                                0%
                            @endif
                        </span>
                    </div>
                </div>
            </div>

            <!-- Recommendations -->
            <div class="bg-[#13eca4]/5 p-6 rounded-xl border border-[#13eca4]/20">
                <div class="flex justify-between items-center mb-4">
                    <h3 class="text-sm font-bold text-white">Recommendations</h3>
                    <span class="px-2 py-1 bg-[#13eca4]/20 text-[#13eca4] rounded text-xs font-bold">Auto-Generated</span>
                </div>
                <div class="space-y-3">
                    @php
                        $lossProducts = collect($data['items'])->where('status', 'Loss')->count();
                        $noSalesProducts = collect($data['items'])->where('status', 'No Sales')->count();
                    @endphp
                    
                    @if($lossProducts > 0)
                    <div class="flex gap-3 p-3 bg-red-500/10 border border-red-500/20 rounded-lg">
                        <span class="material-symbols-outlined text-red-400 text-sm mt-0.5">warning</span>
                        <div class="flex-1 text-xs">
                            <p class="text-white font-medium">{{ $lossProducts }} produk belum mencapai BEP</p>
                            <p class="text-white/60 mt-1">Pertimbangkan untuk meningkatkan harga atau mengurangi biaya</p>
                        </div>
                    </div>
                    @endif

                    @if($noSalesProducts > 0)
                    <div class="flex gap-3 p-3 bg-yellow-500/10 border border-yellow-500/20 rounded-lg">
                        <span class="material-symbols-outlined text-yellow-400 text-sm mt-0.5">info</span>
                        <div class="flex-1 text-xs">
                            <p class="text-white font-medium">{{ $noSalesProducts }} produk belum ada penjualan</p>
                            <p class="text-white/60 mt-1">Fokuskan marketing pada produk-produk ini</p>
                        </div>
                    </div>
                    @endif

                    @if($data['summary']['profitable_products'] > 0)
                    <div class="flex gap-3 p-3 bg-green-500/10 border border-green-500/20 rounded-lg">
                        <span class="material-symbols-outlined text-green-400 text-sm mt-0.5">verified</span>
                        <div class="flex-1 text-xs">
                            <p class="text-white font-medium">{{ $data['summary']['profitable_products'] }} produk sudah profitable</p>
                            <p class="text-white/60 mt-1">Tingkatkan produksi untuk memaksimalkan profit</p>
                        </div>
                    </div>
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>

@push('scripts')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
let bepChart;
const bepData = {
    labels: {!! json_encode(collect($data['items'])->pluck('product.nama')->toArray()) !!},
    bepUnits: {!! json_encode(collect($data['items'])->pluck('bep_units')->toArray()) !!},
    actualSales: {!! json_encode(collect($data['items'])->pluck('actual_sales_units')->toArray()) !!},
    contributionMargin: {!! json_encode(collect($data['items'])->pluck('contribution_margin_ratio')->toArray()) !!}
};

function createBEPChart(type = 'bar') {
    const ctx = document.getElementById('bepChart');
    if (!ctx) return;

    if (bepChart) bepChart.destroy();

    bepChart = new Chart(ctx, {
        type: type,
        data: {
            labels: bepData.labels,
            datasets: [
                {
                    label: 'BEP Units',
                    data: bepData.bepUnits,
                    backgroundColor: 'rgba(255, 193, 7, 0.2)',
                    borderColor: '#ffc107',
                    borderWidth: 2,
                    tension: 0.4
                },
                {
                    label: 'Actual Sales',
                    data: bepData.actualSales,
                    backgroundColor: 'rgba(19, 236, 164, 0.2)',
                    borderColor: '#13eca4',
                    borderWidth: 2,
                    tension: 0.4
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        color: '#fff',
                        padding: 15,
                        font: {
                            size: 12,
                            weight: 'bold'
                        }
                    }
                },
                tooltip: {
                    backgroundColor: '#1a2d27',
                    titleColor: '#13eca4',
                    bodyColor: '#fff',
                    borderColor: '#13eca4',
                    borderWidth: 1,
                    padding: 12,
                    callbacks: {
                        label: function(context) {
                            return context.dataset.label + ': ' + context.parsed.y.toFixed(0) + ' units';
                        },
                        afterLabel: function(context) {
                            const status = bepData.actualSales[context.dataIndex] >= bepData.bepUnits[context.dataIndex] ? 'PROFIT' : 'LOSS';
                            return 'Status: ' + status;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(19, 236, 164, 0.1)'
                    },
                    ticks: {
                        color: '#fff',
                        callback: function(value) {
                            return value.toLocaleString() + ' units';
                        }
                    }
                },
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        color: '#fff',
                        maxRotation: 45,
                        minRotation: 45
                    }
                }
            }
        }
    });
}

function switchBEPChart(type) {
    createBEPChart(type);
    // Update button styles
    document.querySelectorAll('[onclick^="switchBEPChart"]').forEach(btn => {
        btn.className = 'px-3 py-1 text-xs rounded-lg bg-white/10 text-white/60 hover:bg-white/20 transition-colors';
    });
    event.target.className = 'px-3 py-1 text-xs rounded-lg bg-[#13eca4]/20 text-[#13eca4] hover:bg-[#13eca4]/30 transition-colors';
}

document.addEventListener('DOMContentLoaded', function() {
    createBEPChart('bar');
});

function toggleBEPBreakdown(rowId) {
    const row = document.getElementById(rowId);
    const button = event.currentTarget;
    const icon = button.querySelector('.material-symbols-outlined');
    
    if (row.classList.contains('hidden')) {
        row.classList.remove('hidden');
        icon.style.transform = 'rotate(180deg)';
    } else {
        row.classList.add('hidden');
        icon.style.transform = 'rotate(0deg)';
    }
}
</script>
@endpush
