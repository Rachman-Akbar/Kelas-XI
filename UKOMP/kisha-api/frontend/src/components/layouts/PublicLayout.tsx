import Link from 'next/link';
import type { ReactNode } from 'react';

export function PublicLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top_left,_rgba(34,197,94,0.16),_transparent_28%),radial-gradient(circle_at_bottom_right,_rgba(14,165,233,0.14),_transparent_24%),linear-gradient(180deg,_#f8fafc_0%,_#eef2ff_100%)] text-slate-900">
      <header className="sticky top-0 z-20 border-b border-white/50 bg-white/70 backdrop-blur-xl">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4 sm:px-6 lg:px-8">
          <Link href="/" className="flex items-center gap-3 text-base font-semibold tracking-tight">
            <span className="inline-flex h-10 w-10 items-center justify-center rounded-2xl bg-emerald-600 text-white shadow-lg shadow-emerald-600/25">LM</span>
            Local Market Village
          </Link>
          <nav className="flex items-center gap-3 text-sm font-medium text-slate-600">
            <Link href="/auth/login" className="rounded-full px-4 py-2 transition hover:bg-slate-900 hover:text-white">
              Login
            </Link>
            <Link href="/admin" className="rounded-full bg-slate-900 px-4 py-2 text-white transition hover:bg-slate-700">
              Admin
            </Link>
          </nav>
        </div>
      </header>
      <main className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">{children}</main>
    </div>
  );
}
