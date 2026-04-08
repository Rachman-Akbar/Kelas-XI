import { AuthGuard } from '@/components/auth/AuthGuard';
import { AdminLayout, AdminTaskCard } from '@/features/admin';

const adminStats = [
  { label: 'Users', value: '1,284', note: 'buyer, seller, admin' },
  { label: 'Villages', value: '12', note: 'active local zones' },
  { label: 'Products', value: '326', note: 'awaiting or active' },
  { label: 'Moderation', value: '18', note: 'products pending review' },
];

const adminTasks = [
  'Manage user registration and forgot-password flows',
  'Maintain goods and service categories for etalase',
  'Moderate product details, photos, price, shipping, and extra info',
];

export default function AdminDashboardPage() {
  return (
    <AuthGuard roles={['admin']}>
      <AdminLayout>
        <div className="grid gap-6">
          <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            {adminStats.map((stat) => (
              <article key={stat.label} className="rounded-[1.5rem] border border-white/10 bg-white/5 p-5 shadow-[0_20px_60px_-24px_rgba(0,0,0,0.6)]">
                <p className="text-sm text-slate-400">{stat.label}</p>
                <div className="mt-3 flex items-end justify-between gap-4">
                  <h3 className="text-3xl font-semibold text-white">{stat.value}</h3>
                  <span className="rounded-full bg-emerald-500/15 px-3 py-1 text-xs font-medium text-emerald-300">{stat.note}</span>
                </div>
              </article>
            ))}
          </section>
          <section className="grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
            <AdminTaskCard tasks={adminTasks} />
            <div className="rounded-[1.75rem] border border-emerald-500/20 bg-emerald-500/10 p-6">
              <p className="text-sm uppercase tracking-[0.25em] text-emerald-300">Next implementation slice</p>
              <h2 className="mt-2 text-2xl font-semibold tracking-tight text-white">Wire API-driven admin forms</h2>
              <p className="mt-4 text-sm leading-7 text-emerald-50/80">Setelah backend stabil, dashboard ini akan dihubungkan ke data Laravel untuk user management, village CRUD, category CRUD, dan moderation queue.</p>
            </div>
          </section>
        </div>
      </AdminLayout>
    </AuthGuard>
  );
}
