'use client';

import type { FormEvent } from 'react';
import Link from 'next/link';
import { useEffect, useMemo, useState, useTransition } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { authService } from '@/services/auth';
import { villageService } from '@/services/villages';
import { useAuth } from '@/hooks/useAuth';
import type { Role, Village } from '@/types/auth';

type AuthMode = 'login' | 'register' | 'forgot' | 'reset';

const roleOptions: Array<{ value: Exclude<Role, 'admin'>; label: string; description: string }> = [
  { value: 'buyer', label: 'Buyer', description: 'Beli produk dari desa yang sama' },
  { value: 'seller', label: 'Seller', description: 'Jual produk atau jasa lokal' },
];

export function AuthForm({ mode }: { mode: AuthMode }) {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { setSession } = useAuth();
  const [isPending, startTransition] = useTransition();
  const [villages, setVillages] = useState<Village[]>([]);
  const [loadingVillages, setLoadingVillages] = useState(mode === 'register');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [role, setRole] = useState<Exclude<Role, 'admin'>>('buyer');
  const [form, setForm] = useState({
    name: '',
    email: searchParams.get('email') ?? '',
    password: '',
    password_confirmation: '',
    village_id: '',
    token: searchParams.get('token') ?? '',
  });

  useEffect(() => {
    if (mode !== 'register') {
      return;
    }

    let active = true;

    async function loadVillages(): Promise<void> {
      try {
        setLoadingVillages(true);
        const response = await villageService.list();

        if (active) {
          setVillages(response.data.filter((item) => item.is_active));
        }
      } catch (loadError) {
        if (active) {
          setError(loadError instanceof Error ? loadError.message : 'Failed to load villages.');
        }
      } finally {
        if (active) {
          setLoadingVillages(false);
        }
      }
    }

    void loadVillages();

    return () => {
      active = false;
    };
  }, [mode]);

  const title = useMemo(() => {
    switch (mode) {
      case 'register':
        return 'Create account';
      case 'forgot':
        return 'Forgot password';
      case 'reset':
        return 'Reset password';
      default:
        return 'Login';
    }
  }, [mode]);

  const subtitle = useMemo(() => {
    switch (mode) {
      case 'register':
        return 'Create seller or buyer account for the same village.';
      case 'forgot':
        return 'We will send password reset instructions to your email.';
      case 'reset':
        return 'Set a new password using the reset token from email.';
      default:
        return 'Sign in to continue as seller or buyer.';
    }
  }, [mode]);

  function updateField(name: keyof typeof form, value: string): void {
    setForm((current) => ({ ...current, [name]: value }));
  }

  function handleSuccess(message: string, redirectPath?: string): void {
    setSuccess(message);
    setError(null);

    if (redirectPath) {
      startTransition(() => {
        router.push(redirectPath);
      });
    }
  }

  async function submitRegister(): Promise<void> {
    const response = await authService.register({
      name: form.name,
      email: form.email,
      password: form.password,
      password_confirmation: form.password_confirmation,
      role,
      village_id: Number(form.village_id),
      device_name: 'web',
    });

    await setSession(response.data.token);
    handleSuccess('Registration successful.', response.data.user.role === 'admin' ? '/admin' : '/');
  }

  async function submitLogin(): Promise<void> {
    const response = await authService.login({
      email: form.email,
      password: form.password,
      device_name: 'web',
    });

    await setSession(response.data.token);
    handleSuccess('Login successful.', response.data.user.role === 'admin' ? '/admin' : '/');
  }

  async function submitForgot(): Promise<void> {
    const response = await authService.forgotPassword({ email: form.email });
    handleSuccess(response.message || 'Reset link sent. Please check your email.');
  }

  async function submitReset(): Promise<void> {
    const response = await authService.resetPassword({
      token: form.token,
      email: form.email,
      password: form.password,
      password_confirmation: form.password_confirmation,
    });

    handleSuccess(response.message, '/login');
  }

  function onSubmit(event: FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    setError(null);
    setSuccess(null);

    startTransition(() => {
      Promise.resolve()
        .then(async () => {
          if (mode === 'login') {
            await submitLogin();
            return;
          }

          if (mode === 'register') {
            await submitRegister();
            return;
          }

          if (mode === 'forgot') {
            await submitForgot();
            return;
          }

          await submitReset();
        })
        .catch((submitError) => {
          if (submitError && typeof submitError === 'object' && 'response' in submitError) {
            const response = submitError as { response?: { data?: { message?: string; errors?: Record<string, string[]> } } };
            const firstError = response.response?.data?.errors ? Object.values(response.response.data.errors)[0]?.[0] : null;
            setError(firstError ?? response.response?.data?.message ?? 'Request failed.');
            return;
          }

          setError(submitError instanceof Error ? submitError.message : 'Request failed.');
        });
    });
  }

  return (
    <div className="mx-auto grid max-w-6xl gap-8 lg:grid-cols-[0.95fr_1.05fr] lg:items-start">
      <section className="rounded-[2rem] bg-slate-950 p-8 text-white shadow-[0_30px_80px_-30px_rgba(15,23,42,0.55)]">
        <p className="text-sm uppercase tracking-[0.3em] text-emerald-300">Village marketplace</p>
        <h1 className="mt-4 text-4xl font-semibold tracking-tight">{title}</h1>
        <p className="mt-4 max-w-lg text-sm leading-7 text-slate-300">{subtitle}</p>

        <div className="mt-8 space-y-4 rounded-3xl border border-white/10 bg-white/5 p-5">
          <div>
            <p className="text-sm font-medium text-white">Need an account?</p>
            <p className="mt-1 text-sm text-slate-300">Register as buyer or seller, tied to one village only.</p>
            <Link className="mt-3 inline-flex rounded-full bg-white px-4 py-2 text-sm font-medium text-slate-950 transition hover:bg-slate-200" href={mode === 'register' ? '/login' : '/register'}>
              {mode === 'register' ? 'Go to login' : 'Create account'}
            </Link>
          </div>
          <div className="border-t border-white/10 pt-4">
            <p className="text-sm font-medium text-white">Forgot password?</p>
            <p className="mt-1 text-sm text-slate-300">Use the email-based reset flow and continue from reset page.</p>
            <Link className="mt-3 inline-flex rounded-full border border-white/15 px-4 py-2 text-sm font-medium text-white transition hover:bg-white/10" href="/forgot-password">
              Reset flow
            </Link>
          </div>
        </div>
      </section>

      <section className="rounded-[2rem] border border-slate-200 bg-white p-8 shadow-[0_20px_60px_-30px_rgba(15,23,42,0.2)]">
        <form className="space-y-5" onSubmit={onSubmit}>
          {mode === 'register' ? (
            <>
              <div>
                <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="name">
                  Full name
                </label>
                <input id="name" type="text" required value={form.name} onChange={(event) => updateField('name', event.target.value)} className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none ring-0 transition focus:border-emerald-500" />
              </div>
              <div>
                <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="role">
                  Account type
                </label>
                <div className="grid gap-3 sm:grid-cols-2">
                  {roleOptions.map((option) => (
                    <label key={option.value} className={`cursor-pointer rounded-2xl border p-4 transition ${role === option.value ? 'border-emerald-500 bg-emerald-50' : 'border-slate-200 bg-white'}`}>
                      <div className="flex items-start gap-3">
                        <input type="radio" name="role" value={option.value} checked={role === option.value} onChange={() => setRole(option.value)} className="mt-1 h-4 w-4 accent-emerald-600" />
                        <div>
                          <p className="font-medium text-slate-900">{option.label}</p>
                          <p className="mt-1 text-sm text-slate-500">{option.description}</p>
                        </div>
                      </div>
                    </label>
                  ))}
                </div>
              </div>
              <div>
                <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="village_id">
                  Village
                </label>
                <select id="village_id" required value={form.village_id} onChange={(event) => updateField('village_id', event.target.value)} className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500">
                  <option value="">Select village</option>
                  {loadingVillages ? <option>Loading villages...</option> : null}
                  {villages.map((village) => (
                    <option key={village.id} value={village.id}>
                      {village.name}
                    </option>
                  ))}
                </select>
              </div>
            </>
          ) : null}

          <div>
            <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="email">
              Email
            </label>
            <input id="email" type="email" required value={form.email} onChange={(event) => updateField('email', event.target.value)} className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500" />
          </div>

          {mode !== 'forgot' ? (
            <div>
              <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor={mode === 'reset' ? 'token' : 'password'}>
                {mode === 'reset' ? 'Reset token' : 'Password'}
              </label>
              <input id={mode === 'reset' ? 'token' : 'password'} type={mode === 'reset' ? 'text' : 'password'} required value={mode === 'reset' ? form.token : form.password} onChange={(event) => updateField(mode === 'reset' ? 'token' : 'password', event.target.value)} className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500" />
            </div>
          ) : null}

          {mode === 'register' || mode === 'reset' ? (
            <div>
              <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="password_confirmation">
                Confirm password
              </label>
              <input id="password_confirmation" type="password" required value={form.password_confirmation} onChange={(event) => updateField('password_confirmation', event.target.value)} className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500" />
            </div>
          ) : null}

          {mode === 'reset' ? (
            <p className="rounded-2xl bg-amber-50 px-4 py-3 text-sm text-amber-800">
              If you opened this page from the reset email, the token should already be filled in the URL or form.
            </p>
          ) : null}

          {mode === 'login' ? (
            <div className="flex items-center justify-between text-sm">
              <Link href="/forgot-password" className="font-medium text-emerald-700 hover:text-emerald-800">
                Forgot password?
              </Link>
              <Link href="/register" className="font-medium text-slate-600 hover:text-slate-950">
                Create account
              </Link>
            </div>
          ) : null}

          {error ? <div className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</div> : null}
          {success ? <div className="rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{success}</div> : null}

          <button type="submit" disabled={isPending} className="w-full rounded-2xl bg-slate-950 px-4 py-3 text-sm font-medium text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-70">
            {isPending ? 'Processing...' : title}
          </button>
        </form>
      </section>
    </div>
  );
}
