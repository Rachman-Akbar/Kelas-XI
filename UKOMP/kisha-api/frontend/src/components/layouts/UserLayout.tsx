'use client';

import Link from 'next/link';
import { useTransition } from 'react';
import { useRouter } from 'next/navigation';
import type { ReactNode } from 'react';
import { useAuth } from '@/hooks/useAuth';

export function UserLayout({ children }: { children: ReactNode }) {
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
    <div className="min-h-screen bg-slate-50 text-slate-900">
      <header className="border-b border-slate-200 bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4 sm:px-6 lg:px-8">
          <Link href="/" className="text-lg font-semibold tracking-tight">
            Local Market Village
          </Link>
          <div className="flex items-center gap-4">
            <nav className="flex gap-3 text-sm text-slate-600">
              {user?.role === 'seller' && (
                <Link href="/seller" className="hover:text-slate-900">
                  Dashboard
                </Link>
              )}
              {user?.role === 'buyer' && (
                <Link href="/buyer" className="hover:text-slate-900">
                  Dashboard
                </Link>
              )}
              <Link href="/products" className="hover:text-slate-900">
                Products
              </Link>
              <Link href="/orders" className="hover:text-slate-900">
                Orders
              </Link>
            </nav>
            {user && (
              <div className="flex items-center gap-3 border-l border-slate-200 pl-4">
                <span className="text-sm text-slate-500">{user.name}</span>
                <button
                  onClick={handleLogout}
                  disabled={isPending}
                  className="rounded-full border border-slate-300 px-3 py-1.5 text-xs font-medium text-slate-600 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isPending ? 'Logging out...' : 'Logout'}
                </button>
              </div>
            )}
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">{children}</main>
    </div>
  );
}
