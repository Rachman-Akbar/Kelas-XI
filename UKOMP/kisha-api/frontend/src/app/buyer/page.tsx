'use client';

import { AuthGuard } from '@/components/auth/AuthGuard';
import { UserLayout } from '@/components/layouts/UserLayout';
import { useBuyerDashboard } from '@/features/buyer';
import { StatCard, NumberedList } from '@/components/pages';

function BuyerDashboard() {
  const { stats, actions } = useBuyerDashboard();

  return (
    <UserLayout>
      <div className="mx-auto max-w-6xl">
        <div className="mb-8">
          <h1 className="text-3xl font-semibold tracking-tight text-slate-900">Buyer Dashboard</h1>
          <p className="mt-2 text-sm text-slate-500">Temukan produk lokal dari desa Anda.</p>
        </div>
        <section className="grid gap-4 md:grid-cols-3">
          <StatCard label="Pesanan Aktif" value={String(stats.orders_active)} badgeText="Belum ada pesanan" />
          <StatCard label="Riwayat Belanja" value={String(stats.order_history)} badgeText="Belum ada riwayat" badgeVariant="slate" />
          <StatCard label="Produk Favorit" value={String(stats.favorites)} badgeText="Belum ada favorit" badgeVariant="rose" />
        </section>
        <NumberedList sectionLabel="Mulai belanja" title="Produk lokal desa Anda" items={actions} />
      </div>
    </UserLayout>
  );
}

export default function BuyerDashboardPage() {
  return (
    <AuthGuard roles={['buyer']}>
      <BuyerDashboard />
    </AuthGuard>
  );
}
