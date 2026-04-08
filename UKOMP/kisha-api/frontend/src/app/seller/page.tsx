import { AuthGuard } from '@/components/auth/AuthGuard';
import { SellerLayout } from '@/features/seller';
import { Card } from '@/shared/components/ui';

const stats = [
  { label: 'Total Products', value: '0', badge: 'Belum ada produk' as const },
  { label: 'Active Orders', value: '0', badge: 'Tidak ada pesanan' as const },
  { label: 'Total Revenue', value: 'Rp 0', badge: 'Belum ada pendapatan' as const },
  { label: 'Customers', value: '0', badge: 'Belum ada customer' as const },
];

export default function SellerDashboardPage() {
  return (
    <AuthGuard roles={['seller']}>
      <SellerLayout>
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-slate-900">Dashboard Overview</h1>
          <p className="mt-1 text-sm text-slate-500">Welcome back! Here&apos;s your store summary.</p>
        </div>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {stats.map((stat) => (
            <Card key={stat.label}>
              <p className="text-sm text-slate-500">{stat.label}</p>
              <p className="mt-2 text-3xl font-bold text-slate-900">{stat.value}</p>
              <span className="mt-1 inline-block rounded-full bg-emerald-50 px-2 py-0.5 text-xs text-emerald-700">{stat.badge}</span>
            </Card>
          ))}
        </div>
      </SellerLayout>
    </AuthGuard>
  );
}
