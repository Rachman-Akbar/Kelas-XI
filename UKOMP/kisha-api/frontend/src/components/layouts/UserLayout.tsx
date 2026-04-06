import Link from 'next/link';
import type { ReactNode } from 'react';

export function UserLayout({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-900">
      <header className="border-b border-slate-200 bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4 sm:px-6 lg:px-8">
          <Link href="/" className="text-lg font-semibold tracking-tight">
            Local Market Village
          </Link>
          <nav className="flex gap-3 text-sm text-slate-600">
            <Link href="/products" className="hover:text-slate-900">
              Products
            </Link>
            <Link href="/orders" className="hover:text-slate-900">
              Orders
            </Link>
          </nav>
        </div>
      </header>
      <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">{children}</main>
    </div>
  );
}
