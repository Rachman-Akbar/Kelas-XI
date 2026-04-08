'use client';

import type { FormEvent } from 'react';
import Link from 'next/link';
import { useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { authService } from '@/services/auth';
import { AuthLayout } from './AuthLayout';
import { useAuthForm } from './useAuthForm';

export function ResetPasswordForm() {
  const searchParams = useSearchParams();
  const { isPending, startTransition, error, success, handleSuccess, handleError, clearMessages } = useAuthForm();
  const [email, setEmail] = useState(searchParams.get('email') ?? '');
  const [token, setToken] = useState(searchParams.get('token') ?? '');
  const [password, setPassword] = useState('');
  const [passwordConfirmation, setPasswordConfirmation] = useState('');

  function onSubmit(event: FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    clearMessages();

    startTransition(async () => {
      try {
        const response = await authService.resetPassword({
          token,
          email,
          password,
          password_confirmation: passwordConfirmation,
        });
        handleSuccess(response.message, '/auth/login');
      } catch (err) {
        handleError(err);
      }
    });
  }

  return (
    <AuthLayout
      title="Reset password"
      subtitle="Set a new password using the reset token from email."
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
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="reset-email">
            Email
          </label>
          <input
            id="reset-email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          />
        </div>

        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="reset-token">
            Reset token
          </label>
          <input
            id="reset-token"
            type="text"
            required
            value={token}
            onChange={(e) => setToken(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          />
        </div>

        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="reset-password">
            New password
          </label>
          <input
            id="reset-password"
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          />
        </div>

        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="reset-password-confirmation">
            Confirm password
          </label>
          <input
            id="reset-password-confirmation"
            type="password"
            required
            value={passwordConfirmation}
            onChange={(e) => setPasswordConfirmation(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          />
        </div>

        <p className="rounded-2xl bg-amber-50 px-4 py-3 text-sm text-amber-800">
          If you opened this page from the reset email, the token should already be filled in the URL or form.
        </p>

        {error && <div className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</div>}
        {success && <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{success}</div>}

        <button
          type="submit"
          disabled={isPending}
          className="w-full rounded-2xl bg-slate-950 px-4 py-3 text-sm font-medium text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-70"
        >
          {isPending ? 'Processing...' : 'Reset password'}
        </button>
      </form>
    </AuthLayout>
  );
}
