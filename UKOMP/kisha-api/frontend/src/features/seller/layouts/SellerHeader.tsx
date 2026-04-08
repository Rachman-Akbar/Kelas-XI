'use client';

import { useAuth } from '../../../contexts/AuthContext';

export function SellerHeader() {
  const { user, logout } = useAuth();

  async function handleLogout() {
    await logout();
    window.location.href = '/auth/login';
  }

  return (
    <header className="sticky top-0 z-20 flex h-16 items-center justify-between border-b border-slate-200 bg-white px-6 shadow-sm">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Seller Dashboard</h1>
        <p className="text-xs text-slate-500">Manage your store and products</p>
      </div>
      <div className="flex items-center gap-4">
        <button className="relative rounded-lg p-2 text-slate-500 transition hover:bg-slate-100">
          <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" /></svg>
          <span className="absolute right-1.5 top-1.5 h-2 w-2 rounded-full bg-rose-500 ring-2 ring-white" />
        </button>
        <div className="flex items-center gap-3 border-l border-slate-200 pl-4">
          <div className="flex h-9 w-9 items-center justify-center rounded-full bg-emerald-100 text-sm font-semibold text-emerald-700">
            {user?.name?.charAt(0).toUpperCase() ?? 'S'}
          </div>
          <div className="hidden sm:block">
            <p className="text-sm font-medium text-slate-900">{user?.name ?? 'Seller'}</p>
            <p className="text-xs text-slate-500">{user?.email ?? 'seller@store.com'}</p>
          </div>
        </div>
        <button onClick={handleLogout} className="rounded-lg px-3 py-1.5 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-rose-600">
          Logout
        </button>
      </div>
    </header>
  );
}
