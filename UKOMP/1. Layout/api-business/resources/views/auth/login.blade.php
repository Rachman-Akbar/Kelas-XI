<!DOCTYPE html>
<html class="dark" lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Login - SI-UMKM Admin Panel</title>
    
    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    
    <!-- Google Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    
    <!-- Inter Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    
    <script>
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    colors: {
                        "primary": "#13eca4",
                        "background-light": "#f6f8f7", 
                        "background-dark": "#10221c",
                        "background": "#ffffff",
                        "sidebar-dark": "#11221c",
                        "border": "#e2e8f0",
                        "border-dark": "#23483c",
                        "text-muted": "#92c9b7"
                    },
                    fontFamily: {
                        "display": ["Inter", "sans-serif"]
                    }
                }
            }
        }
    </script>
    
    <style>
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
    </style>
</head>

<body class="min-h-screen bg-gradient-to-br from-primary to-green-700 dark:from-green-800 dark:to-green-900 flex items-center justify-center font-display">
    <div class="bg-white dark:bg-background-dark rounded-2xl shadow-2xl overflow-hidden max-w-4xl w-full mx-4">
        <div class="grid grid-cols-1 lg:grid-cols-2">
            <!-- Left Panel -->
            <div class="bg-gradient-to-br from-primary to-green-600 text-white p-12 flex flex-col justify-center">
                <h1 class="text-3xl lg:text-4xl font-bold mb-6">
                    Selamat Datang di<br>
                    <span class="text-green-200">SI-UMKM</span>
                </h1>
                <p class="text-lg opacity-90 leading-relaxed">
                    Sistem informasi manajemen UMKM yang membantu Anda mengelola bisnis dengan lebih efektif dan efisien.
                </p>
                <div class="mt-8 space-y-4">
                    <div class="flex items-center gap-3">
                        <span class="material-symbols-outlined bg-white/20 p-2 rounded-lg">analytics</span>
                        <span>Dashboard & Laporan Lengkap</span>
                    </div>
                    <div class="flex items-center gap-3">
                        <span class="material-symbols-outlined bg-white/20 p-2 rounded-lg">inventory_2</span>
                        <span>Manajemen Produk & Stok</span>
                    </div>
                    <div class="flex items-center gap-3">
                        <span class="material-symbols-outlined bg-white/20 p-2 rounded-lg">account_balance_wallet</span>
                        <span>Kelola Keuangan & Transaksi</span>
                    </div>
                </div>
            </div>

            <!-- Right Panel - Login Form -->
            <div class="p-12">
                <div class="max-w-sm mx-auto">
                    <h2 class="text-2xl font-bold text-slate-900 dark:text-white mb-2">Masuk ke Dashboard</h2>
                    <p class="text-slate-600 dark:text-text-muted mb-8">Silakan masukkan kredensial Anda</p>

                    @if ($errors->any())
                        <div class="mb-6 p-4 bg-red-100 dark:bg-red-500/20 border border-red-200 dark:border-red-700 rounded-lg">
                            <div class="flex items-start">
                                <span class="material-symbols-outlined text-red-600 dark:text-red-400 mr-2">error</span>
                                <div>
                                    <strong class="text-red-800 dark:text-red-200">Error!</strong>
                                    <ul class="mt-2 text-red-700 dark:text-red-300 space-y-1">
                                        @foreach ($errors->all() as $error)
                                            <li>• {{ $error }}</li>
                                        @endforeach
                                    </ul>
                                </div>
                            </div>
                        </div>
                    @endif

                    @if (session('success'))
                        <div class="mb-6 p-4 bg-green-100 dark:bg-green-500/20 border border-green-200 dark:border-green-700 rounded-lg">
                            <div class="flex items-center">
                                <span class="material-symbols-outlined text-green-600 dark:text-green-400 mr-2">check_circle</span>
                                <span class="text-green-800 dark:text-green-200">{{ session('success') }}</span>
                            </div>
                        </div>
                    @endif

                    @if (session('error'))
                        <div class="mb-6 p-4 bg-red-100 dark:bg-red-500/20 border border-red-200 dark:border-red-700 rounded-lg">
                            <div class="flex items-center">
                                <span class="material-symbols-outlined text-red-600 dark:text-red-400 mr-2">error</span>
                                <span class="text-red-800 dark:text-red-200">{{ session('error') }}</span>
                            </div>
                        </div>
                    @endif
                    
                    <form action="{{ route('login.post') }}" method="POST" class="space-y-6">
                        @csrf
                        
                        <div>
                            <label for="email" class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">Email</label>
                            <div class="relative">
                                <span class="absolute left-3 top-1/2 transform -translate-y-1/2">
                                    <span class="material-symbols-outlined text-slate-400 text-lg">mail</span>
                                </span>
                                <input type="email" 
                                       class="w-full pl-10 pr-4 py-3 border border-slate-300 dark:border-border-dark rounded-lg bg-white dark:bg-background-dark text-slate-900 dark:text-white placeholder-slate-400 focus:border-primary focus:ring-1 focus:ring-primary @error('email') border-red-500 @enderror" 
                                       id="email" name="email" placeholder="admin@ukomp.com" 
                                       value="{{ old('email') }}" required autofocus>
                            </div>
                            @error('email')
                                <p class="text-red-500 text-sm mt-1">{{ $message }}</p>
                            @enderror
                        </div>
                        
                        <div>
                            <label for="password" class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">Password</label>
                            <div class="relative">
                                <span class="absolute left-3 top-1/2 transform -translate-y-1/2">
                                    <span class="material-symbols-outlined text-slate-400 text-lg">lock</span>
                                </span>
                                <input type="password" 
                                       class="w-full pl-10 pr-4 py-3 border border-slate-300 dark:border-border-dark rounded-lg bg-white dark:bg-background-dark text-slate-900 dark:text-white placeholder-slate-400 focus:border-primary focus:ring-1 focus:ring-primary @error('password') border-red-500 @enderror" 
                                       id="password" name="password" placeholder="••••••••" required>
                            </div>
                            @error('password')
                                <p class="text-red-500 text-sm mt-1">{{ $message }}</p>
                            @enderror
                        </div>
                        
                        <div class="flex items-center">
                            <input type="checkbox" class="w-4 h-4 text-primary bg-slate-100 border-slate-300 rounded focus:ring-primary focus:ring-2" id="remember" name="remember">
                            <label class="ml-2 text-sm text-slate-600 dark:text-slate-300" for="remember">
                                Ingat Saya
                            </label>
                        </div>
                        
                        <button type="submit" class="w-full flex items-center justify-center gap-2 bg-primary text-background-dark py-3 px-4 rounded-lg text-sm font-bold transition-all duration-200 hover:scale-[1.02] hover:shadow-lg hover:shadow-primary/25">
                            <span class="material-symbols-outlined">login</span>
                            Login
                        </button>
                        
                        <div class="text-center mt-6 p-4 bg-slate-50 dark:bg-slate-800 rounded-lg">
                            <div class="text-sm text-slate-600 dark:text-slate-400 mb-2 flex items-center justify-center gap-1">
                                <span class="material-symbols-outlined text-sm">info</span>
                                Default credentials:
                            </div>
                            <div class="text-sm text-slate-800 dark:text-slate-200">
                                <span class="font-medium">Email:</span> admin@ukomp.com<br>
                                <span class="font-medium">Password:</span> password
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
