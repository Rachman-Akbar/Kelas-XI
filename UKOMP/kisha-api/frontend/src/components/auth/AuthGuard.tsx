'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/hooks/useAuth';
import type { Role } from '@/types/auth';

export function AuthGuard({ children, roles }: { children: React.ReactNode; roles?: Role[] }) {
  const router = useRouter();
  const { user, loading } = useAuth();

  useEffect(() => {
    if (loading) {
      return;
    }

    if (!user) {
      router.replace('/auth/login');
      return;
    }

    if (roles && roles.length > 0 && !roles.includes(user.role)) {
      router.replace('/');
    }
  }, [loading, roles, router, user]);

  if (loading || !user || (roles && roles.length > 0 && !roles.includes(user.role))) {
    return (
      <div className="rounded-[1.5rem] border border-white/10 bg-white/5 p-6 text-slate-300">
        Checking authentication...
      </div>
    );
  }

  return <>{children}</>;
}