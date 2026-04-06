import { PublicLayout } from '@/components/layouts/PublicLayout';
import { AuthForm } from '@/components/features/auth/AuthForm';
import { Suspense } from 'react';

export default function RegisterPage() {
  return (
    <PublicLayout>
      <div className="py-8">
        <Suspense fallback={<div className="mx-auto max-w-6xl rounded-[2rem] bg-white p-8 text-slate-600 shadow-sm">Loading register form...</div>}>
          <AuthForm mode="register" />
        </Suspense>
      </div>
    </PublicLayout>
  );
}
