import type { ReactNode } from 'react';
import { Suspense } from 'react';
import { PublicLayout } from '@/components/layouts/PublicLayout';

interface AuthPageProps {
  form: ReactNode;
  fallbackText: string;
}

/**
 * Shared wrapper for all auth pages (login, register, forgot, reset).
 * Wraps the form in PublicLayout + Suspense with consistent styling.
 */
export function AuthPage({ form, fallbackText }: AuthPageProps) {
  return (
    <PublicLayout>
      <div className="py-8">
        <Suspense fallback={<div className="mx-auto max-w-6xl rounded-[2rem] bg-white p-8 text-slate-600 shadow-sm">{fallbackText}</div>}>
          {form}
        </Suspense>
      </div>
    </PublicLayout>
  );
}
