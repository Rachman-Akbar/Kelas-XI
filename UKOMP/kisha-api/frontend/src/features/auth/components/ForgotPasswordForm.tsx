'use client';

import type { FormEvent } from 'react';
import Link from 'next/link';
import { useState } from 'react';
import { authService } from '../services/auth.service';
import { AuthLayout } from './AuthLayout';
import { useAuthForm } from '../hooks/useAuthForm';

export function ForgotPasswordForm() {
  const { isPending, startTransition, error, success, handleSuccess, handleError, clearMessages } = useAuthForm();
  const [email, setEmail] = useState('');

  function onSubmit(event: FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    clearMessages();

    startTransition(async () => {
      try {
        const response = await authService.forgotPassword({ email });
        handleSuccess(response.message || 'Reset link sent. Please check your email.');
      } catch (err) {
        handleError(err);
      }
    });
  }

  return (
    <AuthLayout
      title="Forgot password"
      subtitle="We will send password reset instructions to your email."
      sidebarContent={
        <div>
          <p className="text-sm font-medium text-white">Remember your password?</p>
          <p className="mt-1 text-sm text-slate-300">Sign in to continue as seller or buyer.</p>
          <Link className="mt-3 inline-flex rounded-full bg-white px-4 py-2 text-sm font-medium text-slate-950 transition hover:bg-slate-200" href="/auth/login">
            Go to login
          </Link>
        </div>
      }
    >
      <form className="space-y-5" onSubmit={onSubmit}>
        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="forgot-email">
            Email
          </label>
          <input
            id="forgot-email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          />
        </div>

        {error && <div className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</div>}
        {success && <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{success}</div>}

        <button
          type="submit"
          disabled={isPending}
          className="w-full rounded-2xl bg-slate-950 px-4 py-3 text-sm font-medium text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-70"
        >
          {isPending ? 'Processing...' : 'Send reset link'}
        </button>
      </form>
    </AuthLayout>
  );
}
