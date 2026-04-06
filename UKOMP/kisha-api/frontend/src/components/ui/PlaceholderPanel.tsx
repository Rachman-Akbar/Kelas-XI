import Link from 'next/link';
import type { ReactNode } from 'react';

export function PlaceholderPanel({
  title,
  description,
  backHref = '/',
  backLabel = 'Back to home',
}: {
  title: string;
  description: string;
  backHref?: string;
  backLabel?: string;
}) {
  return (
    <div className="rounded-[2rem] border border-white/70 bg-white/90 p-8 shadow-[0_30px_80px_-30px_rgba(15,23,42,0.18)] backdrop-blur">
      <p className="text-sm font-semibold uppercase tracking-[0.3em] text-emerald-600">Scaffold</p>
      <h1 className="mt-3 text-3xl font-semibold tracking-tight text-slate-950">{title}</h1>
      <p className="mt-4 max-w-2xl text-base leading-7 text-slate-600">{description}</p>
      <div className="mt-8">
        <Link href={backHref} className="inline-flex items-center rounded-full bg-slate-950 px-5 py-3 text-sm font-medium text-white transition hover:bg-slate-800">
          {backLabel}
        </Link>
      </div>
    </div>
  );
}
