@extends('layouts.master')

@section('title', 'Produksi Baru')

@section('content')
<div class="space-y-6">
    <div class="flex items-center gap-4">
        <a href="{{ route('produksi.index') }}" class="flex items-center justify-center w-10 h-10 bg-white/10 hover:bg-white/20 rounded-lg transition-colors">
            <span class="material-symbols-outlined text-white">arrow_back</span>
        </a>
        <div>
            <h2 class="text-2xl font-bold text-white flex items-center gap-2">
                <span class="material-symbols-outlined text-[#13eca4]">add_circle</span>
                Produksi Baru
            </h2>
            <p class="text-white/60 text-sm">Catat proses produksi untuk mengurangi stok bahan dan menambah stok produk</p>
        </div>
    </div>

    @if($errors->any())
    <div class="bg-red-500/20 border border-red-500/50 text-red-400 rounded-lg p-4">
        <div class="flex items-start gap-2">
            <span class="material-symbols-outlined mt-0.5">error</span>
            <div class="flex-1">
                <strong>Error!</strong>
                <ul class="mt-2 space-y-1 text-sm">
                    @foreach($errors->all() as $error)
                        <li>{!! $error !!}</li>
                    @endforeach
                </ul>
            </div>
        </div>
    </div>
    @endif

    <form action="{{ route('produksi.store') }}" method="POST">
        @csrf
        
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <div class="lg:col-span-7">
                <div class="bg-[#1a2d27] rounded-xl border border-[#13eca4]/30 overflow-hidden mb-6">
                    <div class="px-6 py-4 border-b border-[#13eca4]/30">
                        <h5 class="text-white font-semibold flex items-center gap-2">
                            <span class="material-symbols-outlined text-[#13eca4]">inventory_2</span>
                            Informasi Produksi
                        </h5>
                    </div>
                    <div class="p-6 space-y-5">
                        <div>
                            <label class="block text-sm font-medium text-white/80 mb-2">Produk <span class="text-red-400">*</span></label>
                            <select name="product_id" id="product_id" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-3 text-lg focus:outline-none focus:border-[#13eca4] transition-colors" required onchange="loadProductInfo()">
                                <option value="">-- Pilih Produk --</option>
                                @foreach($products as $product)
                                    <option value="{{ $product->id }}" 
                                            data-sku="{{ $product->sku }}"
                                            data-hpp="{{ $product->hpp }}"
                                            data-harga="{{ $product->harga_jual }}"
                                            data-stok="{{ $product->stok }}"
                                            data-compositions="{{ json_encode($product->compositions->map(fn($c) => [
                                                'material_id' => $c->material_id,
                                                'material_nama' => $c->material->nama,
                                                'material_satuan' => $c->material->satuan,
                                                'material_stok' => $c->material->stok,
                                                'material_harga' => $c->material->harga_satuan,
                                                'quantity_per_unit' => $c->jumlah,
                                            ])) }}"
                                            {{ old('product_id', $selectedProductId) == $product->id ? 'selected' : '' }}>
                                        {{ $product->nama }} ({{ $product->sku }})
                                    </option>
                                @endforeach
                            </select>
                            @error('product_id')
                                <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                            @enderror
                        </div>

                        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                                <label class="block text-sm font-medium text-white/80 mb-2">Jumlah Produksi <span class="text-red-400">*</span></label>
                                <input type="number" name="quantity" id="quantity" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-3 text-lg focus:outline-none focus:border-[#13eca4] transition-colors" 
                                       min="1" value="{{ old('quantity', 1) }}" required
                                       oninput="calculateMaterialNeeds()">
                                <p class="text-white/40 text-xs mt-1">Unit produk yang akan diproduksi</p>
                                @error('quantity')
                                    <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                                @enderror
                            </div>

                            <div>
                                <label class="block text-sm font-medium text-white/80 mb-2">Tanggal Produksi <span class="text-red-400">*</span></label>
                                <input type="date" name="tanggal_produksi" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-3 text-lg focus:outline-none focus:border-[#13eca4] transition-colors" 
                                       value="{{ old('tanggal_produksi', date('Y-m-d')) }}" required>
                                @error('tanggal_produksi')
                                    <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                                @enderror
                            </div>
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-white/80 mb-2">Biaya Tambahan</label>
                            <div class="relative">
                                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-white/40 text-lg">Rp</span>
                                <input type="number" name="biaya_tambahan" id="biaya_tambahan" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg pl-12 pr-4 py-3 text-lg focus:outline-none focus:border-[#13eca4] transition-colors" 
                                       step="0.01" min="0" value="{{ old('biaya_tambahan', 0) }}"
                                       oninput="calculateTotalCost()">
                            </div>
                            <p class="text-white/40 text-xs mt-1">Biaya operasional, listrik, tenaga kerja, dll.</p>
                            @error('biaya_tambahan')
                                <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                            @enderror
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-white/80 mb-2">Catatan</label>
                            <textarea name="catatan" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors" rows="3" 
                                      placeholder="Catatan tambahan untuk produksi ini...">{{ old('catatan') }}</textarea>
                            @error('catatan')
                                <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                            @enderror
                        </div>
                    </div>
                </div>

                <div class="flex gap-3">
                    <button type="submit" class="flex-1 flex items-center justify-center gap-2 px-6 py-3 bg-[#13eca4] text-[#10221c] font-semibold text-lg rounded-lg hover:bg-[#11d694] transition-colors">
                        <span class="material-symbols-outlined text-xl">save</span>
                        Simpan Produksi
                    </button>
                    <a href="{{ route('produksi.index') }}" class="flex items-center gap-2 px-6 py-3 bg-white/10 text-white rounded-lg hover:bg-white/20 transition-colors">
                        <span class="material-symbols-outlined text-xl">close</span>
                        Batal
                    </a>
                </div>
            </div>

            <div class="lg:col-span-5">
                <div class="bg-[#1a2d27] rounded-xl border border-[#13eca4]/30 overflow-hidden sticky top-5">
                    <div class="px-6 py-4 border-b border-[#13eca4]/30">
                        <h5 class="text-white font-semibold flex items-center gap-2">
                            <span class="material-symbols-outlined text-[#13eca4]">checklist</span>
                            Kebutuhan Bahan Baku
                        </h5>
                    </div>
                    <div class="p-6" id="materialRequirementsContainer">
                        <div class="text-center text-white/40 py-12">
                            <span class="material-symbols-outlined" style="font-size: 3rem; opacity: 0.3;">inventory</span>
                            <p class="mt-3">Pilih produk untuk melihat kebutuhan bahan baku</p>
                        </div>
                    </div>
                    
                    <div class="px-6 py-4 border-t border-[#13eca4]/30 bg-[#10221c]/30" id="costSummary" style="display: none;">
                        <div class="space-y-2">
                            <div class="flex justify-between text-sm">
                                <span class="text-white/60">Total Biaya Bahan:</span>
                                <strong class="text-white" id="totalMaterialCost">Rp 0</strong>
                            </div>
                            <div class="flex justify-between text-sm">
                                <span class="text-white/60">Biaya Tambahan:</span>
                                <strong class="text-white" id="additionalCostDisplay">Rp 0</strong>
                            </div>
                            <hr class="border-[#13eca4]/20">
                            <div class="flex justify-between">
                                <span class="font-semibold text-white">Total HPP:</span>
                                <strong class="text-[#13eca4]" id="totalHPP">Rp 0</strong>
                            </div>
                            <p class="text-white/40 text-xs mt-2">HPP per unit: <span class="text-white" id="hppPerUnit">Rp 0</span></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<script>
let currentCompositions = [];

function loadProductInfo() {
    const select = document.getElementById('product_id');
    const option = select.options[select.selectedIndex];
    
    if (!option.value) {
        document.getElementById('materialRequirementsContainer').innerHTML = `
            <div class="text-center text-white/40 py-12">
                <span class="material-symbols-outlined" style="font-size: 3rem; opacity: 0.3;">inventory</span>
                <p class="mt-3">Pilih produk untuk melihat kebutuhan bahan baku</p>
            </div>
        `;
        document.getElementById('costSummary').style.display = 'none';
        return;
    }
    
    currentCompositions = JSON.parse(option.dataset.compositions || '[]');
    calculateMaterialNeeds();
}

function calculateMaterialNeeds() {
    const quantity = parseInt(document.getElementById('quantity').value) || 1;
    
    if (currentCompositions.length === 0) {
        return;
    }
    
    let html = '<div class="space-y-4">';
    let totalMaterialCost = 0;
    
    currentCompositions.forEach(comp => {
        const neededQty = comp.quantity_per_unit * quantity;
        const cost = neededQty * comp.material_harga;
        totalMaterialCost += cost;
        
        const sufficient = comp.material_stok >= neededQty;
        const statusColor = sufficient ? 'green' : 'red';
        const statusIcon = sufficient ? 'check_circle' : 'error';
        
        html += `
            <div class="bg-[#10221c]/50 rounded-lg p-4 border border-[#13eca4]/20">
                <div class="flex justify-between items-start mb-3">
                    <div class="flex-1">
                        <div class="font-semibold text-white">${comp.material_nama}</div>
                        <p class="text-white/40 text-xs">${comp.quantity_per_unit} ${comp.material_satuan} per unit</p>
                    </div>
                    <span class="material-symbols-outlined text-${statusColor}-400">${statusIcon}</span>
                </div>
                <div class="grid grid-cols-2 gap-3 text-sm">
                    <div>
                        <div class="text-white/40 text-xs">Dibutuhkan:</div>
                        <strong class="text-white">${neededQty.toFixed(2)} ${comp.material_satuan}</strong>
                    </div>
                    <div>
                        <div class="text-white/40 text-xs">Tersedia:</div>
                        <strong class="text-${statusColor}-400">${comp.material_stok} ${comp.material_satuan}</strong>
                    </div>
                    <div class="col-span-2">
                        <div class="text-white/40 text-xs">Biaya:</div>
                        <strong class="text-[#13eca4]">Rp ${cost.toLocaleString('id-ID')}</strong>
                    </div>
                </div>
            </div>
        `;
    });
    
    html += '</div>';
    
    document.getElementById('materialRequirementsContainer').innerHTML = html;
    document.getElementById('costSummary').style.display = 'block';
    document.getElementById('totalMaterialCost').textContent = 'Rp ' + totalMaterialCost.toLocaleString('id-ID');
    
    calculateTotalCost();
}

function calculateTotalCost() {
    const materialCostText = document.getElementById('totalMaterialCost').textContent;
    const materialCost = parseFloat(materialCostText.replace(/[^\d]/g, '')) || 0;
    const additionalCost = parseFloat(document.getElementById('biaya_tambahan').value) || 0;
    const quantity = parseInt(document.getElementById('quantity').value) || 1;
    
    const totalHPP = materialCost + additionalCost;
    const hppPerUnit = totalHPP / quantity;
    
    document.getElementById('additionalCostDisplay').textContent = 'Rp ' + additionalCost.toLocaleString('id-ID');
    document.getElementById('totalHPP').textContent = 'Rp ' + totalHPP.toLocaleString('id-ID');
    document.getElementById('hppPerUnit').textContent = 'Rp ' + hppPerUnit.toLocaleString('id-ID');
}

// Load product info on page load if pre-selected
window.addEventListener('DOMContentLoaded', function() {
    loadProductInfo();
});
</script>
@endsection
