@extends('layouts.master')

@section('title', 'Tambah User')

@section('breadcrumb')
<li class="breadcrumb-item"><a href="/user-management">User Management</a></li>
<li class="breadcrumb-item active" aria-current="page">Tambah User</li>
@endsection

@section('content')
<div class="space-y-6">
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
            <nav class="flex mb-2" aria-label="Breadcrumb">
                <ol class="inline-flex items-center space-x-1 md:space-x-3">
                    <li class="inline-flex items-center">
                        <a href="{{ route('dashboard') }}" class="text-white/60 hover:text-[#13eca4] transition-colors">Dashboard</a>
                    </li>
                    <li>
                        <span class="mx-2 text-white/40">/</span>
                        <a href="/user-management" class="text-white/60 hover:text-[#13eca4] transition-colors">User Management</a>
                    </li>
                    <li>
                        <span class="mx-2 text-white/40">/</span>
                        <span class="text-white/80">Tambah User</span>
                    </li>
                </ol>
            </nav>
            <h2 class="text-2xl font-bold text-white flex items-center gap-2">
                <span class="material-symbols-outlined text-[#13eca4]">person_add</span>
                Tambah User
            </h2>
        </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2">
            <div class="bg-[#1a2d27] rounded-xl border border-[#13eca4]/30 overflow-hidden">
                <div class="px-6 py-4 border-b border-[#13eca4]/30">
                    <h5 class="text-white font-semibold">Form Tambah User</h5>
                </div>
                <div class="p-6">
                    <form action="/user-management" method="POST" enctype="multipart/form-data" class="space-y-5">
                        <div>
                            <label for="foto" class="block text-sm font-medium text-white/80 mb-2">Foto Profil</label>
                            <input type="file" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:bg-[#13eca4]/20 file:text-[#13eca4] hover:file:bg-[#13eca4]/30 focus:outline-none focus:border-[#13eca4] transition-colors" id="foto" name="foto" accept="image/*">
                            <p class="text-white/40 text-xs mt-1">Format: JPG, PNG. Maksimal 2MB</p>
                        </div>

                        <div>
                            <label for="nama" class="block text-sm font-medium text-white/80 mb-2">Nama Lengkap <span class="text-red-400">*</span></label>
                            <input type="text" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors" id="nama" name="nama" placeholder="Contoh: John Doe" required>
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                                <label for="email" class="block text-sm font-medium text-white/80 mb-2">Email <span class="text-red-400">*</span></label>
                                <input type="email" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors" id="email" name="email" placeholder="user@example.com" required>
                            </div>
                            
                            <div>
                                <label for="no_telepon" class="block text-sm font-medium text-white/80 mb-2">No. Telepon <span class="text-red-400">*</span></label>
                                <input type="tel" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors" id="no_telepon" name="no_telepon" placeholder="081234567890" required>
                            </div>
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                                <label for="username" class="block text-sm font-medium text-white/80 mb-2">Username <span class="text-red-400">*</span></label>
                                <input type="text" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors" id="username" name="username" placeholder="username" required>
                                <p class="text-white/40 text-xs mt-1">Username untuk login (minimal 5 karakter)</p>
                            </div>
                            
                            <div>
                                <label for="role" class="block text-sm font-medium text-white/80 mb-2">Role <span class="text-red-400">*</span></label>
                                <select class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors" id="role" name="role" required>
                                    <option value="">Pilih Role</option>
                                    <option value="admin">Admin</option>
                                    <option value="manager">Manager</option>
                                    <option value="staff">Staff</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                                <label for="password" class="block text-sm font-medium text-white/80 mb-2">Password <span class="text-red-400">*</span></label>
                                <div class="relative">
                                    <input type="password" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 pr-12 focus:outline-none focus:border-[#13eca4] transition-colors" id="password" name="password" placeholder="Minimal 8 karakter" required>
                                    <button class="absolute right-3 top-1/2 -translate-y-1/2 text-white/40 hover:text-white/80" type="button" onclick="togglePassword('password')">
                                        <span class="material-symbols-outlined">visibility</span>
                                    </button>
                                </div>
                            </div>
                            
                            <div>
                                <label for="password_confirmation" class="block text-sm font-medium text-white/80 mb-2">Konfirmasi Password <span class="text-red-400">*</span></label>
                                <div class="relative">
                                    <input type="password" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 pr-12 focus:outline-none focus:border-[#13eca4] transition-colors" id="password_confirmation" name="password_confirmation" placeholder="Ulangi password" required>
                                    <button class="absolute right-3 top-1/2 -translate-y-1/2 text-white/40 hover:text-white/80" type="button" onclick="togglePassword('password_confirmation')">
                                        <span class="material-symbols-outlined">visibility</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                        
                        <div>
                            <label for="alamat" class="block text-sm font-medium text-white/80 mb-2">Alamat</label>
                            <textarea class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors" id="alamat" name="alamat" rows="3" placeholder="Alamat lengkap (opsional)"></textarea>
                        </div>
                        
                        <div>
                            <label for="status" class="block text-sm font-medium text-white/80 mb-2">Status <span class="text-red-400">*</span></label>
                            <select class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors" id="status" name="status" required>
                                <option value="aktif" selected>Aktif</option>
                                <option value="non-aktif">Non-Aktif</option>
                            </select>
                        </div>
                        
                        <div>
                            <label class="flex items-center gap-2 cursor-pointer">
                                <input class="w-4 h-4 text-[#13eca4] bg-[#10221c] border-[#13eca4]/30 rounded focus:ring-[#13eca4] focus:ring-2" type="checkbox" id="kirim_email" name="kirim_email" checked>
                                <span class="text-sm text-white/80">Kirim email notifikasi ke user dengan informasi login</span>
                            </label>
                        </div>
                        
                        <div class="flex gap-3 pt-2">
                            <button type="submit" class="flex items-center gap-2 px-6 py-2.5 bg-[#13eca4] text-[#10221c] font-semibold rounded-lg hover:bg-[#11d694] transition-colors">
                                <span class="material-symbols-outlined text-xl">save</span>
                                Simpan
                            </button>
                            <button type="reset" class="flex items-center gap-2 px-6 py-2.5 bg-white/10 text-white rounded-lg hover:bg-white/20 transition-colors">
                                <span class="material-symbols-outlined text-xl">refresh</span>
                                Reset
                            </button>
                            <a href="/user-management" class="flex items-center gap-2 px-6 py-2.5 bg-white/10 text-white rounded-lg hover:bg-white/20 transition-colors">
                                <span class="material-symbols-outlined text-xl">close</span>
                                Batal
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
        <div class="space-y-6">
            <div class="bg-gradient-to-br from-purple-500/20 to-purple-500/5 border border-purple-500/30 rounded-xl p-6">
                <h6 class="text-white font-semibold mb-4 flex items-center gap-2">
                    <span class="material-symbols-outlined text-purple-400">info</span>
                    Informasi Role
                </h6>
                <div class="space-y-4">
                    <div>
                        <h6 class="text-red-400 flex items-center gap-2 mb-2">
                            <span class="material-symbols-outlined text-lg">admin_panel_settings</span>
                            Admin
                        </h6>
                        <ul class="text-sm text-white/60 space-y-1 ml-6">
                            <li>• Akses penuh ke semua fitur</li>
                            <li>• Manajemen user dan pengaturan</li>
                            <li>• Laporan & analisis lengkap</li>
                        </ul>
                    </div>
                    
                    <div>
                        <h6 class="text-yellow-400 flex items-center gap-2 mb-2">
                            <span class="material-symbols-outlined text-lg">manage_accounts</span>
                            Manager
                        </h6>
                        <ul class="text-sm text-white/60 space-y-1 ml-6">
                            <li>• Akses produksi & penjualan</li>
                            <li>• Manajemen bahan baku</li>
                            <li>• Approval transaksi</li>
                        </ul>
                    </div>
                    
                    <div>
                        <h6 class="text-blue-400 flex items-center gap-2 mb-2">
                            <span class="material-symbols-outlined text-lg">badge</span>
                            Staff
                        </h6>
                        <ul class="text-sm text-white/60 space-y-1 ml-6">
                            <li>• Input transaksi dasar</li>
                            <li>• Lihat data terbatas</li>
                            <li>• Update stok</li>
                        </ul>
                    </div>
                </div>
            </div>
            
            <div class="bg-gradient-to-br from-green-500/20 to-green-500/5 border border-green-500/30 rounded-xl p-6">
                <h6 class="text-white font-semibold mb-3 flex items-center gap-2">
                    <span class="material-symbols-outlined text-green-400">shield</span>
                    Keamanan Password
                </h6>
                <p class="text-sm text-white/80 mb-2">Password harus memenuhi:</p>
                <ul class="text-sm text-white/60 space-y-1 ml-4">
                    <li>• Minimal 8 karakter</li>
                    <li>• Kombinasi huruf & angka</li>
                    <li>• Disarankan gunakan karakter khusus</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<script>
function togglePassword(id) {
    const input = document.getElementById(id);
    const icon = event.currentTarget.querySelector('.material-symbols-outlined');
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.textContent = 'visibility_off';
    } else {
        input.type = 'password';
        icon.textContent = 'visibility';
    }
}
</script>

@endsection
