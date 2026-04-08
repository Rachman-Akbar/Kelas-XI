'use client';

import type { FormEvent } from 'react';
import Link from 'next/link';
import { useState } from 'react';
import { authService } from '@/services/auth';
import { useAuth } from '@/hooks/useAuth';
import { AuthLayout } from './AuthLayout';
import { useAuthForm } from './useAuthForm';

export function LoginForm() {
  const { setSession } = useAuth();
  const { isPending, startTransition, error, success, handleSuccess, getRedirectPath, handleError, clearMessages } = useAuthForm();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  function onSubmit(event: FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    clearMessages();

    startTransition(async () => {
      try {
        const response = await authService.login({ email, password, device_name: 'web' });
        await setSession(response.data.token);
        handleSuccess('Login successful.', getRedirectPath(response.data.user.role));
      } catch (err) {
        handleError(err);
      }
    });
  }

  return (
    <AuthLayout
      title="Login"
      subtitle="Sign in to continue as seller or buyer."
      sidebarContent={
        <>
          <div>
            <p className="text-sm font-medium text-white">Need an account?</p>
            <p className="mt-1 text-sm text-slate-300">Register as buyer or seller, tied to one village only.</p>
            <Link className="mt-3 inline-flex rounded-full bg-white px-4 py-2 text-sm font-medium text-slate-950 transition hover:bg-slate-200" href="/auth/register">
              Create account
            </Link>
          </div>
          <div className="border-t border-white/10 pt-4">
            <p className="text-sm font-medium text-white">Forgot password?</p>
            <p className="mt-1 text-sm text-slate-300">Use the email-based reset flow and continue from reset page.</p>
            <Link className="mt-3 inline-flex rounded-full border border-white/15 px-4 py-2 text-sm font-medium text-white transition hover:bg-white/10" href="/auth/forgot-password">
              Reset flow
            </Link>
          </div>
        </>
      }
    >
      <form className="space-y-5" onSubmit={onSubmit}>
        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="login-email">
            Email
          </label>
          <input
            id="login-email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          />
        </div>

        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="login-password">
            Password
          </label>
          <input
            id="login-password"
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          />
        </div>

        <div className="flex items-center justify-between text-sm">
          <Link href="/auth/forgot-password" className="font-medium text-emerald-700 hover:text-emerald-800">
            Forgot password?
          </Link>
          <Link href="/auth/register" className="font-medium text-slate-600 hover:text-slate-950">
            Create account
          </Link>
        </div>

        {error && <div className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</div>}
        {success && <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{success}</div>}

        <button
          type="submit"
          disabled={isPending}
          className="w-full rounded-2xl bg-slate-950 px-4 py-3 text-sm font-medium text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-70"
        >
          {isPending ? 'Processing...' : 'Login'}
        </button>
      </form>
    </AuthLayout>
  );
}
