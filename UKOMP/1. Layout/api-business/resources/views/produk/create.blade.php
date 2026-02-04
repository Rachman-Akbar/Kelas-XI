@extends('layouts.master')

@section('title', 'Tambah Produk')

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
                        <a href="{{ route('produk.index') }}" class="text-white/60 hover:text-[#13eca4] transition-colors">Produk</a>
                    </li>
                    <li>
                        <span class="mx-2 text-white/40">/</span>
                        <span class="text-white/80">Tambah</span>
                    </li>
                </ol>
            </nav>
            <h2 class="text-2xl font-bold text-white flex items-center gap-2">
                <span class="material-symbols-outlined text-[#13eca4]">add_shopping_cart</span>
                Tambah Produk
            </h2>
        </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2">
            <div class="bg-[#1a2d27] rounded-xl border border-[#13eca4]/30 overflow-hidden">
                <div class="px-6 py-4 border-b border-[#13eca4]/30">
                    <h5 class="text-white font-semibold">Form Tambah Produk</h5>
                </div>
                <div class="p-6">
                    @if($errors->any())
                        <div class="bg-red-500/20 border border-red-500/50 text-red-400 rounded-lg p-4 mb-6">
                            <ul class="space-y-1 text-sm">
                                @foreach($errors->all() as $error)
                                    <li>{{ $error }}</li>
                                @endforeach
                            </ul>
                        </div>
                    @endif

                    <form action="{{ route('produk.store') }}" method="POST" enctype="multipart/form-data" class="space-y-5">
                        @csrf
                        
                        <div>
                            <label for="nama" class="block text-sm font-medium text-white/80 mb-2">Nama Produk <span class="text-red-400">*</span></label>
                            <input type="text" 
                                   class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30 @error('nama') border-red-500 @enderror" 
                                   id="nama" 
                                   name="nama" 
                                   value="{{ old('nama') }}"
                                   placeholder="Contoh: Kue Brownies Coklat" 
                                   required>
                            @error('nama')
                                <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                            @enderror
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                                <label for="sku" class="block text-sm font-medium text-white/80 mb-2">SKU / Kode Produk</label>
                                <input type="text" 
                                       class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30 @error('sku') border-red-500 @enderror" 
                                       id="sku" 
                                       name="sku" 
                                       value="{{ old('sku') }}"
                                       placeholder="Kosongkan untuk auto-generate">
                                <p class="text-white/50 text-xs mt-1">Biarkan kosong untuk generate otomatis</p>
                                @error('sku')
                                    <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                                @enderror
                            </div>
                            
                            <div>
                                <label for="kategori" class="block text-sm font-medium text-white/80 mb-2">Kategori <span class="text-red-400">*</span></label>
                                <select class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors @error('kategori') border-red-500 @enderror" 
                                        id="kategori" 
                                        name="kategori" 
                                        required>
                                    <option value="">Pilih Kategori</option>
                                    @foreach($categories as $category)
                                        <option value="{{ $category }}" {{ old('kategori') == $category ? 'selected' : '' }}>
                                            {{ ucfirst($category) }}
                                        </option>
                                    @endforeach
                                </select>
                                @error('kategori')
                                    <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                                <label for="harga_jual" class="block text-sm font-medium text-white/80 mb-2">Harga Jual <span class="text-red-400">*</span></label>
                                <div class="relative">
                                    <span class="absolute left-4 top-1/2 -translate-y-1/2 text-white/60">Rp</span>
                                    <input type="number" 
                                           class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg pl-12 pr-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30 @error('harga_jual') border-red-500 @enderror" 
                                           id="harga_jual" 
                                           name="harga_jual" 
                                           value="{{ old('harga_jual') }}"
                                           placeholder="0" 
                                           min="0" 
                                           required>
                                </div>
                                @error('harga_jual')
                                    <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                                @enderror
                            </div>
                            
                            <div>
                                <label for="hpp" class="block text-sm font-medium text-white/80 mb-2">HPP (Harga Pokok Penjualan)</label>
                                <div class="relative">
                                    <span class="absolute left-4 top-1/2 -translate-y-1/2 text-white/60">Rp</span>
                                    <input type="number" 
                                           class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg pl-12 pr-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30 @error('hpp') border-red-500 @enderror" 
                                           id="hpp" 
                                           name="hpp" 
                                           value="{{ old('hpp', 0) }}"
                                           placeholder="0" 
                                           min="0">
                                </div>
                                <p class="text-white/50 text-xs mt-1">Dihitung otomatis dari komposisi bahan</p>
                                @error('hpp')
                                    <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-3 gap-5">
                            <div>
                                <label for="stok" class="block text-sm font-medium text-white/80 mb-2">Stok Awal</label>
                                <input type="number" 
                                       class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30 @error('stok') border-red-500 @enderror" 
                                       id="stok" 
                                       name="stok" 
                                       value="{{ old('stok', 0) }}"
                                       placeholder="0" 
                                       min="0">
                                @error('stok')
                                    <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                                @enderror
                            </div>
                            
                            <div>
                                <label for="stok_minimum" class="block text-sm font-medium text-white/80 mb-2">Stok Minimum</label>
                                <input type="number" 
                                       class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30 @error('stok_minimum') border-red-500 @enderror" 
                                       id="stok_minimum" 
                                       name="stok_minimum" 
                                       value="{{ old('stok_minimum', 10) }}"
                                       placeholder="10" 
                                       min="0">
                                @error('stok_minimum')
                                    <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                                @enderror
                            </div>
                            
                            <div>
                                <label for="satuan" class="block text-sm font-medium text-white/80 mb-2">Satuan</label>
                                <input type="text" 
                                       class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30 @error('satuan') border-red-500 @enderror" 
                                       id="satuan" 
                                       name="satuan" 
                                       value="{{ old('satuan', 'pcs') }}"
                                       placeholder="pcs">
                                @error('satuan')
                                    <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                                @enderror
                            </div>
                        </div>
                        
                        <div>
                            <label for="status" class="block text-sm font-medium text-white/80 mb-2">Status <span class="text-red-400">*</span></label>
                            <select class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors @error('status') border-red-500 @enderror" 
                                    id="status" 
                                    name="status" 
                                    required>
                                <option value="aktif" {{ old('status') == 'aktif' ? 'selected' : 'selected' }}>Aktif</option>
                                <option value="nonaktif" {{ old('status') == 'nonaktif' ? 'selected' : '' }}>Nonaktif</option>
                            </select>
                            @error('status')
                                <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                            @enderror
                        </div>
                        
                        <div>
                            <label for="deskripsi" class="block text-sm font-medium text-white/80 mb-2">Deskripsi Produk</label>
                            <textarea class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors placeholder:text-white/30 @error('deskripsi') border-red-500 @enderror" 
                                      id="deskripsi" 
                                      name="deskripsi" 
                                      rows="3" 
                                      placeholder="Deskripsi singkat tentang produk">{{ old('deskripsi') }}</textarea>
                            @error('deskripsi')
                                <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                            @enderror
                        </div>
                        
                        <div class="flex gap-3 pt-4">
                            <button type="submit" class="bg-[#13eca4] hover:bg-[#11d197] text-[#10221c] font-semibold px-6 py-2.5 rounded-lg transition-colors flex items-center gap-2">
                                <span class="material-symbols-outlined text-xl">save</span>
                                Simpan
                            </button>
                            <a href="{{ route('produk.index') }}" class="bg-white/10 hover:bg-white/20 text-white font-semibold px-6 py-2.5 rounded-lg transition-colors flex items-center gap-2">
                                <span class="material-symbols-outlined text-xl">arrow_back</span>
                                Kembali
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
        <div class="space-y-4">
            <div class="bg-gradient-to-br from-[#13eca4]/20 to-[#13eca4]/5 rounded-xl border border-[#13eca4]/30 p-6">
                <h6 class="text-white font-semibold mb-4 flex items-center gap-2">
                    <span class="material-symbols-outlined text-[#13eca4]">info</span>
                    Panduan
                </h6>
                <ul class="space-y-3 text-white/70 text-sm">
                    <li class="flex items-start gap-2">
                        <span class="material-symbols-outlined text-[#13eca4] text-sm mt-0.5">check_circle</span>
                        <span>Isi data produk dengan lengkap</span>
                    </li>
                    <li class="flex items-start gap-2">
                        <span class="material-symbols-outlined text-[#13eca4] text-sm mt-0.5">check_circle</span>
                        <span>SKU akan di-generate otomatis jika kosong</span>
                    </li>
                    <li class="flex items-start gap-2">
                        <span class="material-symbols-outlined text-[#13eca4] text-sm mt-0.5">check_circle</span>
                        <span>HPP dapat dihitung dari komposisi produk</span>
                    </li>
                    <li class="flex items-start gap-2">
                        <span class="material-symbols-outlined text-[#13eca4] text-sm mt-0.5">check_circle</span>
                        <span>Set status aktif untuk produk yang dijual</span>
                    </li>
                    <li class="flex items-start gap-2">
                        <span class="material-symbols-outlined text-[#13eca4] text-sm mt-0.5">check_circle</span>
                        <span>Stok minimum untuk alert stok rendah</span>
                    </li>
                </ul>
            </div>
            
            <div class="bg-gradient-to-br from-blue-500/20 to-blue-500/5 rounded-xl border border-blue-500/30 p-6">
                <div class="flex items-start gap-3">
                    <span class="material-symbols-outlined text-blue-400 text-2xl">calculate</span>
                    <div>
                        <p class="text-blue-400 font-semibold mb-2">Kalkulasi Margin</p>
                        <div class="text-white/70 text-sm space-y-2">
                            <div>
                                <p class="font-medium text-white/80">Rumus Margin:</p>
                                <p>Margin = ((Harga Jual - HPP) / Harga Jual) × 100%</p>
                            </div>
                            <div class="border-t border-blue-500/20 pt-2">
                                <p class="font-medium text-white/80">Rumus Markup:</p>
                                <p>Markup = ((Harga Jual - HPP) / HPP) × 100%</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="bg-gradient-to-br from-yellow-500/20 to-yellow-500/5 rounded-xl border border-yellow-500/30 p-6">
                <div class="flex items-start gap-3">
                    <span class="material-symbols-outlined text-yellow-400 text-2xl">lightbulb</span>
                    <div>
                        <p class="text-yellow-400 font-semibold mb-1">Langkah Selanjutnya</p>
                        <p class="text-white/70 text-sm">
                            Setelah menambah produk, jangan lupa untuk mengatur <strong class="text-white">Komposisi Produk</strong> agar HPP dapat dihitung otomatis.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
