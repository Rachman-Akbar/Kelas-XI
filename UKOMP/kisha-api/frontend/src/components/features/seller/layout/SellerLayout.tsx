'use client';

import type { ReactNode } from 'react';
import { useState } from 'react';
import Link from 'next/link';
import { SellerHeader } from './SellerHeader';
import { SellerFooter } from './SellerFooter';
import { SellerSidebar } from './SellerSidebar';

export function SellerLayout({ children }: { children: ReactNode }) {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <div className="min-h-screen bg-slate-100">
      <div className="flex min-h-screen">
        <aside className={`fixed inset-y-0 left-0 z-30 flex flex-col border-r border-slate-200 bg-white transition-all duration-300 ${collapsed ? 'w-16' : 'w-64'}`}>
          <div className={`flex h-16 items-center border-b border-slate-200 px-4 ${collapsed ? 'justify-center' : 'justify-between'}`}>
            {!collapsed && (
              <Link href="/seller" className="flex items-center gap-2 text-lg font-bold text-slate-900">
                <span className="inline-flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-600 text-sm font-bold text-white">SM</span>
                Seller Panel
              </Link>
            )}
            <button onClick={() => setCollapsed(!collapsed)} className="rounded-lg p-2 text-slate-500 transition hover:bg-slate-100">
              <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={collapsed ? 'M13 5l7 7-7 7M5 5l7 7-7 7' : 'M11 19l-7-7 7-7m8 14l-7-7 7-7'} /></svg>
            </button>
          </div>
          <SellerSidebar collapsed={collapsed} />
        </aside>
        <div className={`flex flex-1 flex-col transition-all duration-300 ${collapsed ? 'ml-16' : 'ml-64'}`}>
          <SellerHeader />
          <main className="flex-1 p-6">{children}</main>
          <SellerFooter />
        </div>
      </div>
    </div>
  );
}
