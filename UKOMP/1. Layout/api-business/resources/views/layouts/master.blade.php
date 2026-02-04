<!DOCTYPE html>
<html class="dark" lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>@yield('title', 'SI-UMKM') - Admin Panel</title>
    
    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    
    <!-- Google Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    
    <!-- Inter Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    
    <script id="tailwind-config">
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
                    },
                    borderRadius: {
                        "DEFAULT": "0.5rem", 
                        "lg": "0.75rem", 
                        "xl": "1rem", 
                        "2xl": "1.25rem",
                        "full": "9999px"
                    },
                },
            },
        }
    </script>
    
    <!-- Custom CSS -->
    <style>
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
        
        body {
            font-family: 'Inter', sans-serif;
        }
        
        /* Force dark mode for consistent design */
        html {
            color-scheme: dark;
        }
        
        /* Custom layout fixes */
        .sidebar-container {
            width: 16rem; /* 256px / w-64 */
            position: fixed;
            top: 0;
            left: 0;
            height: 100vh;
            z-index: 40;
        }
        
        .main-container {
            margin-left: 16rem; /* Same as sidebar width */
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .header-container {
            position: sticky;
            top: 0;
            z-index: 30;
        }
        
        .content-container {
            flex: 1;
            padding: 2rem;
        }
        
        /* Responsive adjustments */
        @media (max-width: 768px) {
            .sidebar-container {
                transform: translateX(-100%);
                transition: transform 0.3s ease;
            }
            
            .sidebar-container.mobile-open {
                transform: translateX(0);
            }
            
            .main-container {
                margin-left: 0;
            }
            
            /* Mobile backdrop */
            .mobile-backdrop {
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: rgba(0, 0, 0, 0.5);
                z-index: 35;
                display: none;
            }
            
            .mobile-backdrop.active {
                display: block;
            }
        }
        
        /* Smooth transitions */
        * {
            transition: color 0.2s ease, background-color 0.2s ease, border-color 0.2s ease;
        }
    </style>
    
    @yield('styles')
</head>
<body class="bg-background-light dark:bg-background-dark text-slate-900 dark:text-white antialiased" x-data="{ mobileMenuOpen: false }">
    <!-- Mobile backdrop -->
    <div class="mobile-backdrop" :class="{ 'active': mobileMenuOpen }" @click="mobileMenuOpen = false"></div>
    
    <!-- Sidebar -->
    <div class="sidebar-container" :class="{ 'mobile-open': mobileMenuOpen }">
        @include('layouts.sidebar')
    </div>
    
    <!-- Main Content Container -->
    <div class="main-container">
        <!-- Header -->
        <div class="header-container">
            @include('layouts.navbar')
        </div>
        
        <!-- Page Content -->
        <main class="content-container">
            @yield('content')
        </main>
        
        <!-- Footer -->
        @include('layouts.footer')
    </div>
    
    <!-- Alpine.js for interactive components -->
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
    
    @yield('scripts')
    @stack('scripts')
</body>
</html></script>
    
    @yield('scripts')
</body>
</html>
