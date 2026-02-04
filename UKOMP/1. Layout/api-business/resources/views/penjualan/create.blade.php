@extends('layouts.master')

@section('title', 'Penjualan Baru - POS')

@section('content')
<div class="space-y-6">
    <div class="flex items-center gap-4">
        <a href="{{ route('penjualan.index') }}" class="flex items-center justify-center w-10 h-10 bg-white/10 hover:bg-white/20 rounded-lg transition-colors">
            <span class="material-symbols-outlined text-white">arrow_back</span>
        </a>
        <div>
            <h2 class="text-2xl font-bold text-white flex items-center gap-2">
                <span class="material-symbols-outlined text-[#13eca4]">shopping_cart</span>
                Point of Sale
            </h2>
            <p class="text-white/60 text-sm">Sistem penjualan dengan cart interaktif</p>
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

    <form action="{{ route('penjualan.store') }}" method="POST" id="saleForm">
        @csrf
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <div class="lg:col-span-7">
                <div class="bg-[#1a2d27] rounded-xl border border-[#13eca4]/30 overflow-hidden">
                    <div class="px-6 py-4 border-b border-[#13eca4]/30">
                        <h5 class="text-white font-semibold flex items-center gap-2">
                            <span class="material-symbols-outlined text-[#13eca4]">inventory_2</span>
                            Pilih Produk
                        </h5>
                    </div>
                    <div class="p-6">
                        <div class="grid grid-cols-1 md:grid-cols-3 gap-4" id="productGrid">
                            @foreach($products as $product)
                            <div class="bg-[#10221c] rounded-lg p-4 border border-[#13eca4]/20 cursor-pointer hover:border-[#13eca4] hover:-translate-y-1 transition-all" onclick="addToCart({{ $product->id }}, '{{ $product->nama }}', {{ $product->harga_jual }}, {{ $product->stok }})">
                                <h6 class="font-bold text-white mb-1">{{ $product->nama }}</h6>
                                <p class="text-white/40 text-xs mb-3">{{ $product->sku }}</p>
                                <div class="flex justify-between items-center">
                                    <span class="text-[#13eca4] font-bold">Rp {{ number_format($product->harga_jual, 0, ',', '.') }}</span>
                                    <span class="px-2 py-1 rounded text-xs {{ $product->stok > 10 ? 'bg-green-500/20 text-green-400' : ($product->stok > 0 ? 'bg-yellow-500/20 text-yellow-400' : 'bg-red-500/20 text-red-400') }}">
                                        Stok: {{ $product->stok }}
                                    </span>
                                </div>
                            </div>
                            @endforeach
                        </div>
                    </div>
                </div>
            </div>

            <div class="lg:col-span-5">
                <div class="bg-[#1a2d27] rounded-xl border border-[#13eca4]/30 overflow-hidden sticky top-5">
                    <div class="px-6 py-4 border-b border-[#13eca4]/30">
                        <h5 class="text-white font-semibold flex items-center gap-2">
                            <span class="material-symbols-outlined text-[#13eca4]">shopping_basket</span>
                            Keranjang
                        </h5>
                    </div>
                    <div class="p-6 space-y-4">
                        <div>
                            <label class="block text-sm font-medium text-white/80 mb-2">Customer (Opsional)</label>
                            <select name="customer_id" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-[#13eca4] transition-colors">
                                <option value="">Walk-in Customer</option>
                                @foreach($customers as $customer)
                                    <option value="{{ $customer->id }}">{{ $customer->nama }} ({{ $customer->no_telepon }})</option>
                                @endforeach
                            </select>
                        </div>

                        <div id="cartItems" class="max-h-64 overflow-y-auto">
                            <div class="text-center text-white/40 py-8">
                                <span class="material-symbols-outlined" style="font-size: 2rem; opacity: 0.3;">shopping_basket</span>
                                <p class="mt-2">Keranjang kosong</p>
                            </div>
                        </div>

                        <div class="grid grid-cols-2 gap-3">
                            <div>
                                <label class="block text-xs text-white/60 mb-1">Tanggal</label>
                                <input type="date" name="order_date" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-[#13eca4] transition-colors" value="{{ date('Y-m-d') }}" required>
                            </div>
                            <div>
                                <label class="block text-xs text-white/60 mb-1">Metode Bayar</label>
                                <select name="payment_method" class="w-full bg-[#10221c] border border-[#13eca4]/30 text-white rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-[#13eca4] transition-colors" required>
                                    <option value="cash">Cash</option>
                                    <option value="transfer">Transfer</option>
                                    <option value="debit">Debit Card</option>
                                    <option value="credit">Credit Card</option>
                                    <option value="tempo">Tempo/Hutang</option>
                                </select>
                            </div>
                        </div>

                        <div class="bg-[#10221c]/50 p-4 rounded-lg space-y-2">
                            <div class="flex justify-between text-sm">
                                <span class="text-white/60">Subtotal:</span>
                                <strong class="text-white" id="displaySubtotal">Rp 0</strong>
                            </div>
                            <div class="flex justify-between items-center">
                                <span class="text-white/60 text-sm">Diskon:</span>
                                <input type="number" name="discount" id="discount" class="w-32 bg-[#10221c] border border-[#13eca4]/30 text-white rounded px-3 py-1 text-sm focus:outline-none focus:border-[#13eca4]" value="0" min="0" oninput="calculateTotal()">
                            </div>
                            <hr class="border-[#13eca4]/20">
                            <div class="flex justify-between">
                                <span class="font-bold text-white text-lg">Total:</span>
                                <strong class="text-[#13eca4] text-lg" id="displayTotal">Rp 0</strong>
                            </div>
                            <div class="flex justify-between items-center">
                                <span class="text-white/60 text-sm">Dibayar:</span>
                                <input type="number" name="paid_amount" id="paid_amount" class="w-32 bg-[#10221c] border border-[#13eca4]/30 text-white rounded px-3 py-1 text-sm focus:outline-none focus:border-[#13eca4]" value="0" min="0" required>
                            </div>
                        </div>

                        <input type="hidden" name="order_date" value="{{ date('Y-m-d H:i:s') }}">
                        <button type="submit" class="w-full flex items-center justify-center gap-2 px-6 py-3 bg-[#13eca4] text-[#10221c] font-bold text-lg rounded-lg hover:bg-[#11d694] transition-colors disabled:opacity-50 disabled:cursor-not-allowed" id="checkoutBtn" disabled>
                            <span class="material-symbols-outlined">check</span>
                            Checkout
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<script>
let cart = [];
let productData = @json($products->keyBy('id'));

function addToCart(productId, name, price, stock) {
    const existingItem = cart.find(item => item.product_id === productId);
    
    if (existingItem) {
        if (existingItem.quantity < stock) {
            existingItem.quantity++;
        } else {
            alert('Stok tidak cukup!');
            return;
        }
    } else {
        cart.push({
            product_id: productId,
            name: name,
            quantity: 1,
            unit_price: price,
            stock: stock
        });
    }
    
    renderCart();
}

function removeFromCart(index) {
    cart.splice(index, 1);
    renderCart();
}

function updateQuantity(index, quantity) {
    if (quantity <= 0) {
        removeFromCart(index);
        return;
    }
    
    if (quantity > cart[index].stock) {
        alert('Stok tidak cukup!');
        return;
    }
    
    cart[index].quantity = parseInt(quantity);
    renderCart();
}

function renderCart() {
    const container = document.getElementById('cartItems');
    
    if (cart.length === 0) {
        container.innerHTML = '<div class="text-center text-white/40 py-8"><span class="material-symbols-outlined" style="font-size: 2rem; opacity: 0.3;">shopping_basket</span><p class="mt-2">Keranjang kosong</p></div>';
        document.getElementById('checkoutBtn').disabled = true;
        calculateTotal();
        return;
    }
    
    let html = '';
    cart.forEach((item, index) => {
        html += `
            <div class="bg-[#10221c]/50 rounded-lg p-3 mb-2 border border-[#13eca4]/20">
                <div class="flex justify-between items-start mb-2">
                    <div class="flex-1">
                        <div class="font-semibold text-white text-sm">${item.name}</div>
                        <p class="text-white/40 text-xs">Rp ${item.unit_price.toLocaleString('id-ID')}</p>
                    </div>
                    <button type="button" class="text-red-400 hover:text-red-300" onclick="removeFromCart(${index})">
                        <span class="material-symbols-outlined">close</span>
                    </button>
                </div>
                <div class="flex justify-between items-center">
                    <input type="number" class="w-20 bg-[#10221c] border border-[#13eca4]/30 text-white rounded px-2 py-1 text-sm" value="${item.quantity}" min="1" max="${item.stock}" onchange="updateQuantity(${index}, this.value)">
                    <strong class="text-[#13eca4]">Rp ${(item.quantity * item.unit_price).toLocaleString('id-ID')}</strong>
                </div>
                <input type="hidden" name="products[${index}][product_id]" value="${item.product_id}">
                <input type="hidden" name="products[${index}][quantity]" value="${item.quantity}">
                <input type="hidden" name="products[${index}][unit_price]" value="${item.unit_price}">
            </div>
        `;
    });
    
    container.innerHTML = html;
    document.getElementById('checkoutBtn').disabled = false;
    calculateTotal();
}

function calculateTotal() {
    const subtotal = cart.reduce((sum, item) => sum + (item.quantity * item.unit_price), 0);
    const discount = parseFloat(document.getElementById('discount').value) || 0;
    const total = subtotal - discount;
    
    document.getElementById('displaySubtotal').textContent = 'Rp ' + subtotal.toLocaleString('id-ID');
    document.getElementById('displayTotal').textContent = 'Rp ' + total.toLocaleString('id-ID');
    document.getElementById('paid_amount').value = total;
}
</script>
@endsection
