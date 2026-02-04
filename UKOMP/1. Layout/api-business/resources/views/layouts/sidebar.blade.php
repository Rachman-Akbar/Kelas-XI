<!-- Side Navigation -->
<aside class="w-full h-full border-r border-slate-200 dark:border-border-dark bg-white dark:bg-sidebar-dark flex flex-col overflow-y-auto">
    <!-- Logo & Header -->
    <div class="p-6 flex items-center gap-3 border-b border-slate-200 dark:border-border-dark">
        <div class="bg-primary/20 p-2.5 rounded-lg">
            <span class="material-symbols-outlined text-primary text-xl">storefront</span>
        </div>
        <div>
            <h1 class="text-lg font-bold leading-none dark:text-white">SI-UMKM</h1>
            <p class="text-xs text-slate-500 dark:text-text-muted mt-0.5">Management Portal</p>
        </div>
    </div>
    
    <!-- Navigation -->
    <nav class="flex-1 px-4 py-4 space-y-1">
        <!-- Dashboard -->
        <a href="/dashboard" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('dashboard') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">dashboard</span>
            <span class="text-sm">Dashboard</span>
        </a>
        
        <!-- Divider & Section -->
        <div class="pt-4 pb-2">
            <div class="border-t border-slate-200 dark:border-border-dark mb-3"></div>
            <p class="text-[11px] font-semibold text-slate-400 dark:text-text-muted/70 px-3 uppercase tracking-wider">Data Master</p>
        </div>
        
        <!-- Bahan Baku -->
        <a href="/bahan-baku" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('bahan-baku*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">inventory_2</span>
            <span class="text-sm">Bahan Baku</span>
        </a>
        
        <!-- Produk -->
        <a href="/produk" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('produk*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">shopping_bag</span>
            <span class="text-sm">Produk</span>
        </a>
        
        <!-- Komposisi Produk -->
        <a href="/komposisi-produk" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('komposisi-produk*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">list_alt</span>
            <span class="text-sm">Komposisi Produk</span>
        </a>
        
        <!-- Pelanggan -->
        <a href="/pelanggan" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('pelanggan*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">people</span>
            <span class="text-sm">Pelanggan</span>
        </a>
        
        <!-- Divider & Section -->
        <div class="pt-4 pb-2">
            <div class="border-t border-slate-200 dark:border-border-dark mb-3"></div>
            <p class="text-[11px] font-semibold text-slate-400 dark:text-text-muted/70 px-3 uppercase tracking-wider">Transaksi & Operasional</p>
        </div>
        
        <!-- Transaksi -->
        <a href="{{ route('transaksi.index', ['tab' => 'pemasukan']) }}" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('transaksi*') || request()->is('keuangan*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">payments</span>
            <span class="text-sm">Transaksi</span>
        </a>
        
        <!-- Produksi -->
        <a href="/produksi" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('produksi*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">precision_manufacturing</span>
            <span class="text-sm">Produksi</span>
        </a>
        
        <!-- Pesanan -->
        <a href="/pesanan" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('pesanan*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">shopping_cart</span>
            <span class="text-sm">Pesanan</span>
        </a>
        
        <!-- Penjualan -->
        <a href="/penjualan" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('penjualan*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">point_of_sale</span>
            <span class="text-sm">Penjualan</span>
        </a>
        
        <!-- Divider & Section -->
        <div class="pt-4 pb-2">
            <div class="border-t border-slate-200 dark:border-border-dark mb-3"></div>
            <p class="text-[11px] font-semibold text-slate-400 dark:text-text-muted/70 px-3 uppercase tracking-wider">Analisis & Laporan</p>
        </div>
        
        <!-- Analisis -->
        <a href="{{ route('analisis.index', ['tab' => 'hpp']) }}" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('analisis*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">trending_up</span>
            <span class="text-sm">Analisis Bisnis</span>
        </a>
        
        <!-- Laporan -->
        <a href="{{ route('laporan.index', ['tab' => 'penjualan']) }}" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('laporan*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">description</span>
            <span class="text-sm">Laporan</span>
        </a>
        
        <!-- Divider & Section -->
        <div class="pt-4 pb-2">
            <div class="border-t border-slate-200 dark:border-border-dark mb-3"></div>
            <p class="text-[11px] font-semibold text-slate-400 dark:text-text-muted/70 px-3 uppercase tracking-wider">Pengaturan</p>
        </div>
        
        <!-- User Management -->
        <a href="/user-management" class="flex items-center gap-3 px-3 py-3 rounded-lg font-medium transition-all duration-200
           {{ request()->is('user-management*') ? 'bg-primary text-background-dark shadow-lg shadow-primary/25' : 'text-slate-600 dark:text-text-muted hover:bg-slate-100 dark:hover:bg-border-dark hover:text-slate-900 dark:hover:text-white' }}">
            <span class="material-symbols-outlined text-xl">admin_panel_settings</span>
            <span class="text-sm">User Management</span>
        </a>
    </nav>
    
    <!-- User Profile Footer -->
    <div class="p-4 border-t border-slate-200 dark:border-border-dark bg-slate-50 dark:bg-background-dark/50">
        <div class="flex items-center gap-3 p-3 rounded-lg bg-white dark:bg-border-dark">
            <div class="size-10 rounded-full bg-primary/20 flex items-center justify-center ring-2 ring-primary/20">
                <span class="material-symbols-outlined text-primary text-xl">account_circle</span>
            </div>
            <div class="flex flex-col overflow-hidden">
                <p class="text-sm font-semibold truncate dark:text-white">Admin User</p>
                <p class="text-xs text-slate-500 dark:text-text-muted truncate">Administrator</p>
            </div>
            <div class="ml-auto">
                <span class="material-symbols-outlined text-slate-400 dark:text-text-muted text-lg">more_vert</span>
            </div>
        </div>
    </div>
</aside>
