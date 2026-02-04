@extends('layouts.master')

@section('title', 'Edit Produk')

@section('content')
<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="mb-1">Edit Produk</h3>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item"><a href="{{ route('dashboard') }}">Dashboard</a></li>
                    <li class="breadcrumb-item"><a href="{{ route('produk.index') }}">Produk</a></li>
                    <li class="breadcrumb-item active">Edit</li>
                </ol>
            </nav>
        </div>
        <div>
            <button type="button" class="btn btn-info" onclick="recalculateHPP()">
                <i class="fas fa-calculator me-1"></i> Hitung Ulang HPP
            </button>
        </div>
    </div>

    <div class="row">
        <div class="col-md-8">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0">Form Edit Produk</h5>
                </div>
                <div class="card-body">
                    @if($errors->any())
                        <div class="alert alert-danger">
                            <ul class="mb-0">
                                @foreach($errors->all() as $error)
                                    <li>{{ $error }}</li>
                                @endforeach
                            </ul>
                        </div>
                    @endif

                    <form action="{{ route('produk.update', $product->id) }}" method="POST" enctype="multipart/form-data">
                        @csrf
                        @method('PUT')
                        
                        <div class="mb-3">
                            <label for="nama" class="form-label">Nama Produk <span class="text-danger">*</span></label>
                            <input type="text" 
                                   class="form-control @error('nama') is-invalid @enderror" 
                                   id="nama" 
                                   name="nama" 
                                   value="{{ old('nama', $product->nama) }}"
                                   required>
                            @error('nama')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="sku" class="form-label">SKU / Kode Produk</label>
                                <input type="text" 
                                       class="form-control bg-light" 
                                       id="sku" 
                                       name="sku" 
                                       value="{{ $product->sku }}" 
                                       readonly>
                                <small class="text-muted">SKU tidak dapat diubah</small>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="kategori" class="form-label">Kategori <span class="text-danger">*</span></label>
                                <select class="form-select @error('kategori') is-invalid @enderror" 
                                        id="kategori" 
                                        name="kategori" 
                                        required>
                                    <option value="">Pilih Kategori</option>
                                    @foreach($categories as $category)
                                        <option value="{{ $category }}" {{ old('kategori', $product->kategori) == $category ? 'selected' : '' }}>
                                            {{ ucfirst($category) }}
                                        </option>
                                    @endforeach
                                </select>
                                @error('kategori')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="harga_jual" class="form-label">Harga Jual <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text">Rp</span>
                                    <input type="number" 
                                           class="form-control @error('harga_jual') is-invalid @enderror" 
                                           id="harga_jual" 
                                           name="harga_jual" 
                                           value="{{ old('harga_jual', $product->harga_jual) }}"
                                           min="0" 
                                           required>
                                    @error('harga_jual')
                                        <div class="invalid-feedback">{{ $message }}</div>
                                    @enderror
                                </div>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="hpp" class="form-label">HPP (Harga Pokok Penjualan)</label>
                                <div class="input-group">
                                    <span class="input-group-text">Rp</span>
                                    <input type="number" 
                                           class="form-control bg-light @error('hpp') is-invalid @enderror" 
                                           id="hpp" 
                                           name="hpp" 
                                           value="{{ old('hpp', $product->hpp) }}"
                                           min="0" 
                                           readonly>
                                    @error('hpp')
                                        <div class="invalid-feedback">{{ $message }}</div>
                                    @enderror
                                </div>
                                <small class="text-muted">Dihitung dari komposisi bahan</small>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label for="stok" class="form-label">Stok Saat Ini</label>
                                <div class="input-group">
                                    <input type="number" 
                                           class="form-control @error('stok') is-invalid @enderror" 
                                           id="stok" 
                                           name="stok" 
                                           value="{{ old('stok', $product->stok) }}"
                                           min="0">
                                    @error('stok')
                                        <div class="invalid-feedback">{{ $message }}</div>
                                    @enderror
                                </div>
                            </div>
                            
                            <div class="col-md-4 mb-3">
                                <label for="stok_minimum" class="form-label">Stok Minimum</label>
                                <div class="input-group">
                                    <input type="number" 
                                           class="form-control @error('stok_minimum') is-invalid @enderror" 
                                           id="stok_minimum" 
                                           name="stok_minimum" 
                                           value="{{ old('stok_minimum', $product->stok_minimum) }}"
                                           min="0">
                                    @error('stok_minimum')
                                        <div class="invalid-feedback">{{ $message }}</div>
                                    @enderror
                                </div>
                            </div>
                            
                            <div class="col-md-4 mb-3">
                                <label for="satuan" class="form-label">Satuan</label>
                                <input type="text" 
                                       class="form-control @error('satuan') is-invalid @enderror" 
                                       id="satuan" 
                                       name="satuan" 
                                       value="{{ old('satuan', $product->satuan) }}">
                                @error('satuan')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="status" class="form-label">Status <span class="text-danger">*</span></label>
                            <select class="form-select @error('status') is-invalid @enderror" 
                                    id="status" 
                                    name="status" 
                                    required>
                                <option value="aktif" {{ old('status', $product->status) == 'aktif' ? 'selected' : '' }}>Aktif</option>
                                <option value="nonaktif" {{ old('status', $product->status) == 'nonaktif' ? 'selected' : '' }}>Nonaktif</option>
                            </select>
                            @error('status')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="deskripsi" class="form-label">Deskripsi Produk</label>
                            <textarea class="form-control @error('deskripsi') is-invalid @enderror" 
                                      id="deskripsi" 
                                      name="deskripsi" 
                                      rows="3">{{ old('deskripsi', $product->deskripsi) }}</textarea>
                            @error('deskripsi')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="d-flex justify-content-between mt-4">
                            <a href="{{ route('produk.index') }}" class="btn btn-secondary">
                                <i class="fas fa-arrow-left me-1"></i> Kembali
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-1"></i> Update
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
        <div class="col-md-4">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-chart-bar me-2"></i>Informasi Produk</h5>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <small class="text-muted">Margin Keuntungan</small>
                        <h4 class="mb-0">
                            @if($product->profit_margin > 0)
                                <span class="text-success">{{ number_format($product->profit_margin, 1) }}%</span>
                            @else
                                <span class="text-danger">{{ number_format($product->profit_margin, 1) }}%</span>
                            @endif
                        </h4>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted">Markup Harga</small>
                        <h4 class="mb-0">{{ number_format($product->markup_percentage, 1) }}%</h4>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted">Laba Per Unit</small>
                        <h4 class="mb-0">Rp {{ number_format($product->profit_per_unit, 0, ',', '.') }}</h4>
                    </div>
                    <div>
                        <small class="text-muted">Status Stok</small>
                        <h4 class="mb-0">
                            <span class="badge {{ $product->stock_status_color }}">
                                {{ ucfirst($product->stock_status) }}
                            </span>
                        </h4>
                    </div>
                </div>
            </div>
            
            <div class="card border-0 shadow-sm mt-3">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-list-alt me-2"></i>Komposisi Bahan</h5>
                </div>
                <div class="card-body">
                    @if($compositions->count() > 0)
                        <div class="table-responsive">
                            <table class="table table-sm mb-0">
                                <thead>
                                    <tr>
                                        <th>Bahan</th>
                                        <th class="text-end">Qty</th>
                                        <th class="text-end">Biaya</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    @foreach($compositions as $composition)
                                        <tr>
                                            <td>{{ $composition->material->nama }}</td>
                                            <td class="text-end">{{ $composition->jumlah }} {{ $composition->material->satuan }}</td>
                                            <td class="text-end">Rp {{ number_format($composition->biaya, 0, ',', '.') }}</td>
                                        </tr>
                                    @endforeach
                                </tbody>
                                <tfoot>
                                    <tr class="fw-bold">
                                        <td colspan="2">Total HPP</td>
                                        <td class="text-end">Rp {{ number_format($product->hpp, 0, ',', '.') }}</td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                        <a href="{{ route('komposisi-produk.index') }}?produk_id={{ $product->id }}" class="btn btn-sm btn-outline-primary w-100 mt-3">
                            <i class="fas fa-edit me-1"></i> Edit Komposisi
                        </a>
                    @else
                        <div class="text-center py-3">
                            <i class="fas fa-inbox fa-3x text-muted mb-2"></i>
                            <p class="text-muted mb-2">Belum ada komposisi</p>
                            <a href="{{ route('komposisi-produk.index') }}?produk_id={{ $product->id }}" class="btn btn-sm btn-primary">
                                <i class="fas fa-plus me-1"></i> Tambah Komposisi
                            </a>
                        </div>
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>

@push('scripts')
<script>
function recalculateHPP() {
    if (!confirm('Hitung ulang HPP dari komposisi bahan?\n\nHPP lama akan diganti dengan perhitungan baru.')) {
        return;
    }
    
    fetch("{{ route('produk.calculate-hpp', $product->id) }}", {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': '{{ csrf_token() }}',
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            document.getElementById('hpp').value = data.hpp;
            alert(`HPP berhasil dihitung ulang!\n\nHPP: Rp ${data.hpp.toLocaleString('id-ID')}\nMargin: ${data.margin}%\nMarkup: ${data.markup}%\nLaba/Unit: Rp ${data.profit_per_unit.toLocaleString('id-ID')}`);
            window.location.reload();
        } else {
            alert('Error: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Terjadi kesalahan saat menghitung HPP');
    });
}
</script>
@endpush
@endsection
