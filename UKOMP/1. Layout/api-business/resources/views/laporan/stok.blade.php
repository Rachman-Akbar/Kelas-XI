{{-- Summary Cards --}}
<div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
    <div class="bg-gradient-to-r from-[#13eca4] to-[#11d197] text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Total Bahan Baku</p>
                <h3 class="text-3xl font-bold text-white mt-1">{{ number_format($data['summary']['total_materials']) }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                category
            </span>
        </div>
    </div>
    
    <div class="bg-gradient-to-r from-green-500 to-green-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Total Produk</p>
                <h3 class="text-3xl font-bold text-white mt-1">{{ number_format($data['summary']['total_products']) }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                inventory
            </span>
        </div>
    </div>
    
    <div class="bg-gradient-to-r from-blue-500 to-blue-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Nilai Stok Bahan</p>
                <h3 class="text-3xl font-bold text-white mt-1">Rp {{ number_format($data['summary']['total_materials_value'], 0, ',', '.') }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                shopping_bag
            </span>
        </div>
    </div>
    
    <div class="bg-gradient-to-r from-yellow-500 to-yellow-600 text-white p-6 rounded-xl shadow-lg">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-white/80 text-sm font-medium">Nilai Stok Produk</p>
                <h3 class="text-3xl font-bold text-white mt-1">Rp {{ number_format($data['summary']['total_products_value'], 0, ',', '.') }}</h3>
            </div>
            <span class="material-symbols-outlined text-4xl text-white/30">
                inventory_2
            </span>
        </div>
    </div>
</div>

{{-- Materials Table --}}
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark overflow-hidden shadow-sm mb-8">
    <div class="px-6 py-4 border-b border-slate-200 dark:border-border-dark">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Stok Bahan Baku</h2>
    </div>
    <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-border-dark">
            <thead class="bg-gray-50 dark:bg-sidebar-dark">
                <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Nama Bahan</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Satuan</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Stok</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Stok Min</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Harga/Unit</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Nilai Total</th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Status</th>
                </tr>
            </thead>
            <tbody class="bg-white dark:bg-background-dark divide-y divide-gray-200 dark:divide-border-dark">
                @forelse($data['materials'] as $material)
                <tr class="hover:bg-gray-50 dark:hover:bg-border-dark/30 transition-colors">
                    <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm font-medium text-gray-900 dark:text-white">{{ $material->nama }}</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm text-gray-500 dark:text-text-muted">{{ $material->satuan }}</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-900 dark:text-white font-semibold">
                        {{ number_format($material->stok, 2) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-500 dark:text-text-muted">
                        {{ number_format($material->stok_minimum, 2) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-right text-gray-700 dark:text-text-muted">
                        Rp {{ number_format($material->harga_per_satuan, 0, ',', '.') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-right text-gray-900 dark:text-white">
                        Rp {{ number_format($material->stok * $material->harga_per_satuan, 0, ',', '.') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                        @if($material->stok <= 0)
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800 dark:bg-red-800/20 dark:text-red-400">
                                <span class="material-symbols-outlined mr-1 text-sm">cancel</span>
                                Habis
                            </span>
                        @elseif($material->stok <= $material->stok_minimum)
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800 dark:bg-yellow-800/20 dark:text-yellow-400">
                                <span class="material-symbols-outlined mr-1 text-sm">warning</span>
                                Rendah
                            </span>
                        @else
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-800/20 dark:text-green-400">
                                <span class="material-symbols-outlined mr-1 text-sm">check_circle</span>
                                Aman
                            </span>
                        @endif
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="7" class="px-6 py-16 text-center">
                        <div class="flex flex-col items-center justify-center">
                            <span class="material-symbols-outlined text-6xl text-gray-300 mb-4">
                                production_quantity_limits
                            </span>
                            <p class="text-gray-500 text-lg font-medium">Tidak ada data bahan baku</p>
                        </div>
                    </td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>

{{-- Products Table --}}
<div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark overflow-hidden shadow-sm">
    <div class="px-6 py-4 border-b border-slate-200 dark:border-border-dark">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Stok Produk</h2>
    </div>
    <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-border-dark">
            <thead class="bg-gray-50 dark:bg-sidebar-dark">
                <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Nama Produk</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Stok</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Stok Min</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Harga Jual</th>
                    <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Nilai Total</th>
                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 dark:text-text-muted uppercase tracking-wider">Status</th>
                </tr>
            </thead>
            <tbody class="bg-white dark:bg-background-dark divide-y divide-gray-200 dark:divide-border-dark">
                @forelse($data['products'] as $product)
                <tr class="hover:bg-gray-50 dark:hover:bg-border-dark/30 transition-colors">
                    <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm font-medium text-gray-900 dark:text-white">{{ $product->nama }}</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-900 dark:text-white font-semibold">
                        {{ number_format($product->stok) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-500 dark:text-text-muted">
                        {{ number_format($product->stok_minimum) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-right text-gray-700 dark:text-text-muted">
                        Rp {{ number_format($product->harga_jual, 0, ',', '.') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-right text-gray-900 dark:text-white">
                        Rp {{ number_format($product->stok * $product->harga_jual, 0, ',', '.') }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                        @if($product->stok <= 0)
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800 dark:bg-red-800/20 dark:text-red-400">
                                <span class="material-symbols-outlined mr-1 text-sm">cancel</span>
                                Habis
                            </span>
                        @elseif($product->stok <= $product->stok_minimum)
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800 dark:bg-yellow-800/20 dark:text-yellow-400">
                                <span class="material-symbols-outlined mr-1 text-sm">warning</span>
                                Rendah
                            </span>
                        @else
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-800/20 dark:text-green-400">
                                <span class="material-symbols-outlined mr-1 text-sm">check_circle</span>
                                Aman
                            </span>
                        @endif
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="6" class="px-6 py-16 text-center">
                        <div class="flex flex-col items-center justify-center">
                            <span class="material-symbols-outlined text-6xl text-gray-300 mb-4">
                                inventory_2
                            </span>
                            <p class="text-gray-500 text-lg font-medium">Tidak ada data produk</p>
                        </div>
                    </td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>
