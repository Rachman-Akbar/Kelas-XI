import type { ReactNode } from 'react';

interface AuthLayoutProps {
  title: string;
  subtitle: string;
  sidebarTitle?: string;
  sidebarSubtitle?: string;
  sidebarContent: ReactNode;
  children: ReactNode;
}

export function AuthLayout({
  title,
  subtitle,
  sidebarContent,
  children,
}: AuthLayoutProps) {
  return (
    <div className="mx-auto grid max-w-6xl gap-8 lg:grid-cols-[0.95fr_1.05fr] lg:items-start">
      <section className="rounded-[2rem] bg-slate-950 p-8 text-white shadow-[0_30px_80px_-30px_rgba(15,23,42,0.55)]">
        <p className="text-sm uppercase tracking-[0.3em] text-emerald-300">Village marketplace</p>
        <h1 className="mt-4 text-4xl font-semibold tracking-tight">{title}</h1>
        <p className="mt-4 max-w-lg text-sm leading-7 text-slate-300">{subtitle}</p>
        <div className="mt-8 space-y-4 rounded-3xl border border-white/10 bg-white/5 p-5">
          {sidebarContent}
        </div>
      </section>
      <section className="rounded-[2rem] border border-slate-200 bg-white p-8 shadow-[0_20px_60px_-30px_rgba(15,23,42,0.2)]">
        {children}
      </section>
    </div>
  );
}
