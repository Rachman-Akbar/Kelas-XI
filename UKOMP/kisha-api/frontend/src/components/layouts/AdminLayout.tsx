'use client';

import Link from 'next/link';
import { useTransition } from 'react';
import { useRouter } from 'next/navigation';
import type { ReactNode } from 'react';
import { useAuth } from '@/hooks/useAuth';

const navigation = [
  { label: 'Dashboard', href: '/admin' },
  { label: 'Users', href: '/admin/users' },
  { label: 'Products', href: '/admin/products' },
  { label: 'Villages', href: '/admin/villages' },
  { label: 'Categories', href: '/admin/categories' },
];

export function AdminLayout({ children }: { children: ReactNode }) {
  const router = useRouter();
  const { user, logout } = useAuth();
  const [isPending, startTransition] = useTransition();

  function handleLogout(): void {
    startTransition(async () => {
      await logout();
      router.replace('/auth/login');
    });
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <div className="grid min-h-screen lg:grid-cols-[280px_1fr]">
        <aside className="flex flex-col border-r border-white/10 bg-slate-900/70 px-5 py-6 backdrop-blur">
          <div className="mb-8 flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-emerald-500 text-sm font-bold text-white">AD</div>
            <div>
              <p className="text-sm text-slate-400">Village Marketplace</p>
              <h1 className="text-lg font-semibold">Admin Panel</h1>
            </div>
          </div>
          <nav className="space-y-1">
            {navigation.map((item) => (
              <Link key={item.href} href={item.href} className="flex rounded-2xl px-4 py-3 text-sm font-medium text-slate-300 transition hover:bg-white/10 hover:text-white">
                {item.label}
              </Link>
            ))}
          </nav>
          <div className="mt-auto pt-6">
            {user && (
              <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                <p className="text-sm font-medium text-white">{user.name}</p>
                <p className="mt-0.5 text-xs text-slate-400">{user.email}</p>
                <button
                  onClick={handleLogout}
                  disabled={isPending}
                  className="mt-3 w-full rounded-full border border-white/15 px-3 py-2 text-xs font-medium text-slate-300 transition hover:bg-white/10 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isPending ? 'Logging out...' : 'Logout'}
                </button>
              </div>
            )}
          </div>
        </aside>
        <div className="flex min-w-0 flex-col">
          <header className="border-b border-white/10 bg-slate-950/80 px-6 py-5 backdrop-blur">
            <div>
              <p className="text-sm text-slate-400">Restricted to admin village operations</p>
              <h2 className="text-2xl font-semibold tracking-tight">Management Console</h2>
            </div>
          </header>
          <main className="flex-1 px-6 py-8">{children}</main>
        </div>
      </div>
    </div>
  );
}
