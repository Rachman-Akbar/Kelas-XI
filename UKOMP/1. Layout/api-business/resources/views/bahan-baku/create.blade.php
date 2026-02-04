@extends('layouts.master')

@section('title', 'Tambah Bahan Baku')

@section('content')
<div class="space-y-6">
    <!-- Page Header -->
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
            <nav class="flex mb-2" aria-label="Breadcrumb">
                <ol class="inline-flex items-center space-x-1 md:space-x-3">
                    <li class="inline-flex items-center">
                        <a href="/dashboard" class="text-white/60 hover:text-[#13eca4] transition-colors">Dashboard</a>
                    </li>
                    <li>
                        <span class="mx-2 text-white/40">/</span>
                        <a href="/bahan-baku" class="text-white/60 hover:text-[#13eca4] transition-colors">Bahan Baku</a>
                    </li>
                    <li>
                        <span class="mx-2 text-white/40">/</span>
                        <span class="text-white/80">Tambah</span>
                    </li>
                </ol>
            </nav>
            <h2 class="text-2xl font-bold text-white flex items-center gap-2">
                <span class="material-symbols-outlined text-[#13eca4]">inventory</span>
                Tambah Bahan Baku
            </h2>
        </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Form Panel -->
        <div class="lg:col-span-2">
            <div class="bg-[#1a2d27] rounded-xl border border-[#13eca4]/30 overflow-hidden">
                <div class="px-6 py-4 border-b border-[#13eca4]/30">
                    <h5 class="text-white font-semibold">Form Tambah Bahan Baku</h5>
                </div>
                <div class="p-6">
                    <form action="/bahan-baku" method="POST" class="space-y-5">
                        <div>
                            <label for="nama" class="block text-sm font-medium text-white/80 mb-2">
                                Nama Bahan Baku <span class="text-red-400">*</span>
                            </label>
                            <input type="text" 
                                   class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30" 
                                   id="nama" 
                                   name="nama" 
                                   placeholder="Contoh: Tepung Terigu" 
                                   required>
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                                <label for="satuan" class="block text-sm font-medium text-white/80 mb-2">
                                    Satuan <span class="text-red-400">*</span>
                                </label>
                                <select class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors" 
                                        id="satuan" 
                                        name="satuan" 
                                        required>
                                    <option value="">Pilih Satuan</option>
                                    <option value="kg">Kilogram (kg)</option>
                                    <option value="gram">Gram (g)</option>
                                    <option value="liter">Liter (l)</option>
                                    <option value="ml">Mililiter (ml)</option>
                                    <option value="pcs">Pieces (pcs)</option>
                                    <option value="butir">Butir</option>
                                    <option value="pack">Pack</option>
                                </select>
                            </div>
                            
                            <div>
                                <label for="harga" class="block text-sm font-medium text-white/80 mb-2">
                                    Harga per Satuan <span class="text-red-400">*</span>
                                </label>
                                <div class="relative">
                                    <span class="absolute left-4 top-1/2 -translate-y-1/2 text-white/60">Rp</span>
                                    <input type="number" 
                                           class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg pl-12 pr-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30" 
                                           id="harga" 
                                           name="harga" 
                                           placeholder="0" 
                                           required>
                                </div>
                            </div>
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                                <label for="stok" class="block text-sm font-medium text-white/80 mb-2">
                                    Stok Awal <span class="text-red-400">*</span>
                                </label>
                                <input type="number" 
                                       class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30" 
                                       id="stok" 
                                       name="stok" 
                                       placeholder="0" 
                                       required>
                            </div>
                            
                            <div>
                                <label for="min_stok" class="block text-sm font-medium text-white/80 mb-2">
                                    Minimum Stok (Alert)
                                </label>
                                <input type="number" 
                                       class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30" 
                                       id="min_stok" 
                                       name="min_stok" 
                                       placeholder="0">
                                <p class="text-white/50 text-xs mt-1">Sistem akan memberi peringatan jika stok di bawah nilai ini</p>
                            </div>
                        </div>
                        
                        <div>
                            <label for="supplier" class="block text-sm font-medium text-white/80 mb-2">
                                Supplier
                            </label>
                            <input type="text" 
                                   class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30" 
                                   id="supplier" 
                                   name="supplier" 
                                   placeholder="Nama supplier (opsional)">
                        </div>
                        
                        <div>
                            <label for="keterangan" class="block text-sm font-medium text-white/80 mb-2">
                                Keterangan
                            </label>
                            <textarea class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30" 
                                      id="keterangan" 
                                      name="keterangan" 
                                      rows="3" 
                                      placeholder="Keterangan tambahan (opsional)"></textarea>
                        </div>
                        
                        <div class="flex gap-3 pt-4">
                            <button type="submit" class="bg-[#13eca4] hover:bg-[#11d197] text-[#10221c] font-semibold px-6 py-2.5 rounded-lg transition-colors flex items-center gap-2">
                                <span class="material-symbols-outlined text-xl">save</span>
                                Simpan
                            </button>
                            <a href="/bahan-baku" class="bg-white/10 hover:bg-white/20 text-white font-semibold px-6 py-2.5 rounded-lg transition-colors flex items-center gap-2">
                                <span class="material-symbols-outlined text-xl">arrow_back</span>
                                Kembali
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
        <!-- Info Panel -->
        <div class="space-y-4">
            <div class="bg-gradient-to-br from-[#13eca4]/20 to-[#13eca4]/5 rounded-xl border border-[#13eca4]/30 p-6">
                <h6 class="text-white font-semibold mb-4 flex items-center gap-2">
                    <span class="material-symbols-outlined text-[#13eca4]">info</span>
                    Panduan
                </h6>
                <ul class="space-y-3 text-white/70 text-sm">
                    <li class="flex items-start gap-2">
                        <span class="material-symbols-outlined text-[#13eca4] text-sm mt-0.5">check_circle</span>
                        <span>Isi nama bahan baku dengan lengkap</span>
                    </li>
                    <li class="flex items-start gap-2">
                        <span class="material-symbols-outlined text-[#13eca4] text-sm mt-0.5">check_circle</span>
                        <span>Pilih satuan yang sesuai</span>
                    </li>
                    <li class="flex items-start gap-2">
                        <span class="material-symbols-outlined text-[#13eca4] text-sm mt-0.5">check_circle</span>
                        <span>Harga per satuan digunakan untuk kalkulasi biaya</span>
                    </li>
                    <li class="flex items-start gap-2">
                        <span class="material-symbols-outlined text-[#13eca4] text-sm mt-0.5">check_circle</span>
                        <span>Set minimum stok untuk notifikasi otomatis</span>
                    </li>
                    <li class="flex items-start gap-2">
                        <span class="material-symbols-outlined text-red-400 text-sm mt-0.5">star</span>
                        <span>Field bertanda * wajib diisi</span>
                    </li>
                </ul>
            </div>
            
            <div class="bg-gradient-to-br from-yellow-500/20 to-yellow-500/5 rounded-xl border border-yellow-500/30 p-6">
                <div class="flex items-start gap-3">
                    <span class="material-symbols-outlined text-yellow-400 text-2xl">lightbulb</span>
                    <div>
                        <p class="text-yellow-400 font-semibold mb-1">Tips</p>
                        <p class="text-white/70 text-sm">
                            Pastikan satuan yang dipilih konsisten untuk setiap bahan. Ini membantu dalam perhitungan komposisi produk.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
