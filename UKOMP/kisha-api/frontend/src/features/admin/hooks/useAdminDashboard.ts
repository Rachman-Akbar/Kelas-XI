'use client';

export function useAdminDashboard() {
  const stats = { users_total: 0, buyers_total: 0, sellers_total: 0, villages_total: 0, products_total: 0, products_pending: 0, products_active: 0 };
  const loading = false;
  return { stats, loading };
}
