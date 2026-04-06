import { AuthGuard } from '@/components/auth/AuthGuard';
import { AdminLayout } from '@/components/layouts/AdminLayout';

const summary = [
  { label: 'Users', value: '1,284', note: 'buyer, seller, admin' },
  { label: 'Villages', value: '12', note: 'active local zones' },
  { label: 'Products', value: '326', note: 'awaiting or active' },
  { label: 'Moderation', value: '18', note: 'products pending review' },
];

const tasks = [
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
          {summary.map((item) => (
            <article key={item.label} className="rounded-[1.5rem] border border-white/10 bg-white/5 p-5 shadow-[0_20px_60px_-24px_rgba(0,0,0,0.6)]">
              <p className="text-sm text-slate-400">{item.label}</p>
              <div className="mt-3 flex items-end justify-between gap-4">
                <h3 className="text-3xl font-semibold text-white">{item.value}</h3>
                <span className="rounded-full bg-emerald-500/15 px-3 py-1 text-xs font-medium text-emerald-300">{item.note}</span>
              </div>
            </article>
          ))}
        </section>

        <section className="grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
          <div className="rounded-[1.75rem] border border-white/10 bg-white/5 p-6">
            <p className="text-sm uppercase tracking-[0.25em] text-slate-400">Three admin priorities</p>
            <h2 className="mt-2 text-2xl font-semibold tracking-tight text-white">Core scaffolding for MVP governance</h2>
            <div className="mt-6 space-y-4">
              {tasks.map((task, index) => (
                <div key={task} className="flex gap-4 rounded-2xl border border-white/10 bg-slate-900/70 p-4">
                  <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-emerald-500 text-sm font-semibold text-white">0{index + 1}</div>
                  <p className="text-sm leading-6 text-slate-300">{task}</p>
                </div>
              ))}
            </div>
          </div>

          <div className="rounded-[1.75rem] border border-emerald-500/20 bg-emerald-500/10 p-6">
            <p className="text-sm uppercase tracking-[0.25em] text-emerald-300">Next implementation slice</p>
            <h2 className="mt-2 text-2xl font-semibold tracking-tight text-white">Wire API-driven admin forms</h2>
            <p className="mt-4 text-sm leading-7 text-emerald-50/80">
              Setelah backend stabil, dashboard ini akan dihubungkan ke data Laravel untuk user management, village CRUD, category CRUD, dan moderation queue.
            </p>
          </div>
        </section>
        </div>
      </AdminLayout>
    </AuthGuard>
  );
}
