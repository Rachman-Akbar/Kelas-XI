<!-- Header -->
<header class="bg-white/95 dark:bg-sidebar-dark/95 backdrop-blur-md border-b border-slate-200 dark:border-border-dark shadow-sm">
    <div class="flex items-center justify-between px-6 py-4">
        <div class="flex items-center gap-6 flex-1 max-w-2xl">
            <!-- Mobile Menu Button -->
            <button @click="mobileMenuOpen = !mobileMenuOpen" class="md:hidden p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-border-dark transition-colors">
                <span class="material-symbols-outlined text-xl">menu</span>
            </button>
            
            <!-- Breadcrumb -->
            <nav class="flex" aria-label="Breadcrumb">
                <ol class="inline-flex items-center space-x-2">
                    <li class="inline-flex items-center">
                        <a href="/dashboard" class="inline-flex items-center text-sm font-medium text-slate-600 dark:text-text-muted hover:text-primary dark:hover:text-primary transition-colors">
                            <span class="material-symbols-outlined text-lg mr-2">home</span>
                            <span class="hidden sm:inline">Dashboard</span>
                        </a>
                    </li>
                    @yield('breadcrumb')
                </ol>
            </nav>
        </div>
        
        <div class="flex items-center gap-4">
            
            <!-- Export Button -->
            @if(request()->is('laporan*'))
            <button class="flex items-center gap-2 bg-primary text-background-dark px-4 py-2.5 rounded-lg text-sm font-bold transition-all duration-200 hover:scale-[1.02] hover:shadow-lg hover:shadow-primary/25 active:scale-[0.98]" onclick="window.print()">
                <span class="material-symbols-outlined text-lg">file_download</span>
                Export Report
            </button>
            @endif
            
            <!-- Notification -->
            <div class="relative" x-data="{ open: false }">
                <button @click="open = !open" class="relative p-2.5 rounded-lg bg-slate-100 dark:bg-border-dark text-slate-600 dark:text-white hover:bg-slate-200 dark:hover:bg-slate-600 transition-all duration-200">
                    <span class="material-symbols-outlined text-xl">notifications</span>
                    <span class="absolute -top-1 -right-1 size-5 bg-red-500 rounded-full border-2 border-white dark:border-border-dark flex items-center justify-center text-xs font-bold text-white">3</span>
                </button>
                
                <!-- Notification Dropdown -->
                <div x-show="open" @click.away="open = false" x-transition:enter="transition ease-out duration-200" x-transition:enter-start="opacity-0 scale-95" x-transition:enter-end="opacity-100 scale-100" x-transition:leave="transition ease-in duration-150" x-transition:leave-start="opacity-100 scale-100" x-transition:leave-end="opacity-0 scale-95" class="absolute right-0 mt-2 w-96 bg-white dark:bg-background-dark rounded-xl shadow-xl border border-slate-200 dark:border-border-dark z-50">
                    <div class="p-4 border-b border-slate-200 dark:border-border-dark">
                        <div class="flex justify-between items-center">
                            <h3 class="text-base font-semibold dark:text-white">Notifications</h3>
                            <span class="px-2.5 py-1 bg-red-500 text-white text-xs font-bold rounded-full">3 New</span>
                        </div>
                    </div>
                    <div class="max-h-80 overflow-y-auto">
                        <div class="p-4 border-b border-slate-100 dark:border-border-dark hover:bg-slate-50 dark:hover:bg-border-dark/50 cursor-pointer transition-colors">
                            <div class="flex items-start gap-3">
                                <div class="p-2 bg-orange-100 dark:bg-orange-500/20 rounded-lg">
                                    <span class="material-symbols-outlined text-orange-600 dark:text-orange-400 text-lg">warning</span>
                                </div>
                                <div class="flex-1">
                                    <p class="text-sm font-medium dark:text-white">Stok Bahan Baku Menipis</p>
                                    <p class="text-xs text-slate-500 dark:text-text-muted mt-1">Tepung Terigu tersisa 5 kg dari minimal 20 kg</p>
                                    <p class="text-xs text-slate-400 dark:text-text-muted mt-1">2 menit yang lalu</p>
                                </div>
                            </div>
                        </div>
                        <div class="p-4 border-b border-slate-100 dark:border-border-dark hover:bg-slate-50 dark:hover:bg-border-dark/50 cursor-pointer transition-colors">
                            <div class="flex items-start gap-3">
                                <div class="p-2 bg-green-100 dark:bg-green-500/20 rounded-lg">
                                    <span class="material-symbols-outlined text-green-600 dark:text-green-400 text-lg">shopping_cart</span>
                                </div>
                                <div class="flex-1">
                                    <p class="text-sm font-medium dark:text-white">Pesanan Baru Masuk</p>
                                    <p class="text-xs text-slate-500 dark:text-text-muted mt-1">5 pesanan baru menunggu konfirmasi</p>
                                    <p class="text-xs text-slate-400 dark:text-text-muted mt-1">15 menit yang lalu</p>
                                </div>
                            </div>
                        </div>
                        <div class="p-4 hover:bg-slate-50 dark:hover:bg-border-dark/50 cursor-pointer transition-colors">
                            <div class="flex items-start gap-3">
                                <div class="p-2 bg-blue-100 dark:bg-blue-500/20 rounded-lg">
                                    <span class="material-symbols-outlined text-blue-600 dark:text-blue-400 text-lg">trending_up</span>
                                </div>
                                <div class="flex-1">
                                    <p class="text-sm font-medium dark:text-white">Laporan Bulanan Siap</p>
                                    <p class="text-xs text-slate-500 dark:text-text-muted mt-1">Laporan penjualan Februari 2026 telah dibuat</p>
                                    <p class="text-xs text-slate-400 dark:text-text-muted mt-1">1 jam yang lalu</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="p-4 border-t border-slate-200 dark:border-border-dark">
                        <button class="w-full text-sm text-primary hover:text-primary/80 font-medium transition-colors">Lihat Semua Notifikasi</button>
                    </div>
                </div>
            </div>
            
            <!-- User Profile -->
            <div class="relative" x-data="{ open: false }">
                <button @click="open = !open" class="flex items-center gap-3 p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-border-dark transition-all duration-200">
                    <div class="size-9 rounded-full bg-primary/20 flex items-center justify-center ring-2 ring-primary/20">
                        <span class="material-symbols-outlined text-primary text-lg">account_circle</span>
                    </div>
                    <div class="hidden md:block text-left">
                        <p class="text-sm font-semibold dark:text-white">{{ auth()->user()->name ?? 'Admin' }}</p>
                        <p class="text-xs text-slate-500 dark:text-text-muted">Administrator</p>
                    </div>
                    <span class="material-symbols-outlined text-slate-400 dark:text-text-muted text-lg">keyboard_arrow_down</span>
                </button>
                
                <!-- Profile Dropdown -->
                <div x-show="open" @click.away="open = false" x-transition:enter="transition ease-out duration-200" x-transition:enter-start="opacity-0 scale-95" x-transition:enter-end="opacity-100 scale-100" x-transition:leave="transition ease-in duration-150" x-transition:leave-start="opacity-100 scale-100" x-transition:leave-end="opacity-0 scale-95" class="absolute right-0 mt-2 w-56 bg-white dark:bg-background-dark rounded-xl shadow-xl border border-slate-200 dark:border-border-dark z-50">
                    <div class="p-4 border-b border-slate-200 dark:border-border-dark">
                        <p class="text-sm font-semibold dark:text-white">{{ auth()->user()->name ?? 'Admin User' }}</p>
                        <p class="text-xs text-slate-500 dark:text-text-muted">{{ auth()->user()->email ?? 'admin@example.com' }}</p>
                    </div>
                    <div class="p-2">
                        <a href="#" class="flex items-center gap-3 px-3 py-2.5 text-sm dark:text-white hover:bg-slate-100 dark:hover:bg-border-dark rounded-lg transition-colors">
                            <span class="material-symbols-outlined text-lg">person</span>
                            Profile Saya
                        </a>
                        <a href="/pengaturan" class="flex items-center gap-3 px-3 py-2.5 text-sm dark:text-white hover:bg-slate-100 dark:hover:bg-border-dark rounded-lg transition-colors">
                            <span class="material-symbols-outlined text-lg">settings</span>
                            Pengaturan
                        </a>
                        <a href="/activity-logs" class="flex items-center gap-3 px-3 py-2.5 text-sm dark:text-white hover:bg-slate-100 dark:hover:bg-border-dark rounded-lg transition-colors">
                            <span class="material-symbols-outlined text-lg">history</span>
                            Activity Logs
                        </a>
                        <div class="border-t border-slate-200 dark:border-border-dark my-2"></div>
                        <form action="{{ route('logout') }}" method="POST" id="logoutForm">
                            @csrf
                            <a href="#" onclick="event.preventDefault(); document.getElementById('logoutForm').submit();" class="flex items-center gap-3 px-3 py-2.5 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors w-full">
                                <span class="material-symbols-outlined text-lg">logout</span>
                                Logout
                            </a>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>
