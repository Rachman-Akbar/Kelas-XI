'use client';

export function useBuyerDashboard() {
  const stats = { orders_active: 0, order_history: 0, favorites: 0 };
  const actions = [
    'Jelajahi produk dari seller di desa Anda',
    'Buat pesanan dan lacak status pengiriman',
    'Lihat riwayat transaksi dan produk favorit',
  ];
  const loading = false;
  return { stats, actions, loading };
}
