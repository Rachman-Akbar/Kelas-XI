import Link from 'next/link';
import { PublicLayout } from '@/components/layouts/PublicLayout';

const highlights = [
  'Visible only within one village',
  'Seller and buyer live in the same local area',
  'Admin moderates products and manages village data',
];

const stats = [
  { label: 'Village-first restriction', value: '100%' },
  { label: 'Core MVP focus', value: '3' },
  { label: 'Reusable feature modules', value: '5+' },
];

export default function Home() {
  return (
    <PublicLayout>
      <section className="grid gap-8 lg:grid-cols-[1.1fr_0.9fr] lg:items-center">
        <div>
          <span className="inline-flex rounded-full border border-emerald-200 bg-emerald-50 px-4 py-2 text-sm font-medium text-emerald-700">
            Local village marketplace
          </span>
          <h1 className="mt-6 max-w-3xl text-5xl font-semibold tracking-tight text-slate-950 sm:text-6xl">
            Marketplace lokal untuk jual beli antar warga dalam satu desa.
          </h1>
          <p className="mt-6 max-w-2xl text-lg leading-8 text-slate-600">
            Platform ini membatasi produk, transaksi, dan pengguna hanya di area desa yang sama agar ekonomi lokal tetap berputar di komunitas sendiri.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link href="/auth/login" className="rounded-full bg-slate-950 px-6 py-3 text-sm font-medium text-white transition hover:bg-slate-800">
              Masuk ke sistem
            </Link>
            <Link href="/admin" className="rounded-full border border-slate-300 bg-white px-6 py-3 text-sm font-medium text-slate-700 transition hover:border-slate-400 hover:text-slate-950">
              Buka dashboard admin
            </Link>
          </div>
        </div>

        <div className="rounded-[2rem] border border-white/70 bg-white/80 p-6 shadow-[0_30px_80px_-30px_rgba(15,23,42,0.18)] backdrop-blur">
          <div className="rounded-[1.5rem] bg-slate-950 p-6 text-white">
            <p className="text-sm text-slate-400">Marketplace rules</p>
            <h2 className="mt-2 text-2xl font-semibold">Village-only visibility</h2>
            <div className="mt-6 space-y-3 text-sm text-slate-300">
              {highlights.map((item) => (
                <div key={item} className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3">
                  {item}
                </div>
              ))}
            </div>
          </div>
          <div className="mt-6 grid gap-4 sm:grid-cols-3">
            {stats.map((stat) => (
              <div key={stat.label} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <p className="text-2xl font-semibold text-slate-950">{stat.value}</p>
                <p className="mt-1 text-xs uppercase tracking-[0.18em] text-slate-500">{stat.label}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </PublicLayout>
  );
}
