'use client';

import type { FormEvent } from 'react';
import Link from 'next/link';
import { useState } from 'react';
import { authService } from '../services/auth.service';
import { useAuth } from '../../../contexts/AuthContext';
import { AuthLayout } from './AuthLayout';
import { useAuthForm } from '../hooks/useAuthForm';
import { useVillages } from '../hooks/useVillages';

type Role = 'admin' | 'seller' | 'buyer';

const roleOptions: Array<{ value: Exclude<Role, 'admin'>; label: string; description: string }> = [
  { value: 'buyer', label: 'Buyer', description: 'Beli produk dari desa yang sama' },
  { value: 'seller', label: 'Seller', description: 'Jual produk atau jasa lokal' },
];

export function RegisterForm() {
  const { setSession } = useAuth();
  const { isPending, startTransition, error, success, handleSuccess, getRedirectPath, handleError, clearMessages } = useAuthForm();
  const { villages, loading: loadingVillages } = useVillages();
  const [role, setRole] = useState<Exclude<Role, 'admin'>>('buyer');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirmation, setPasswordConfirmation] = useState('');
  const [villageId, setVillageId] = useState('');

  function onSubmit(event: FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    clearMessages();

    startTransition(async () => {
      try {
        if (password !== passwordConfirmation) {
          throw new Error('Password confirmation does not match.');
        }

        const parsedVillageId = villageId ? Number(villageId) : undefined;

        if (!parsedVillageId || Number.isNaN(parsedVillageId)) {
          throw new Error('Please select a valid village.');
        }

        const response = await authService.register({
          name,
          email,
          password,
          password_confirmation: passwordConfirmation,
          role,
          village_id: parsedVillageId,
          device_name: 'web',
        });

        await setSession(response.data.token);
        handleSuccess('Registration successful.', getRedirectPath(response.data.user.role));
      } catch (err) {
        handleError(err);
      }
    });
  }

  return (
    <AuthLayout
      title="Create account"
      subtitle="Create seller or buyer account for the same village."
      sidebarContent={
        <div>
          <p className="text-sm font-medium text-white">Already have an account?</p>
          <p className="mt-1 text-sm text-slate-300">Sign in to continue as seller or buyer.</p>
          <Link className="mt-3 inline-flex rounded-full bg-white px-4 py-2 text-sm font-medium text-slate-950 transition hover:bg-slate-200" href="/auth/login">
            Go to login
          </Link>
        </div>
      }
    >
      <form className="space-y-5" onSubmit={onSubmit}>
        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="register-name">
            Full name
          </label>
          <input
            id="register-name"
            type="text"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          />
        </div>

        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="register-role">
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
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="register-village">
            Village
          </label>
          <select
            id="register-village"
            required
            value={villageId}
            onChange={(e) => setVillageId(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          >
            <option value="">Select village</option>
            {loadingVillages && <option>Loading villages...</option>}
            {villages.map((village) => (
              <option key={village.id} value={village.id}>
                {village.name}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="register-email">
            Email
          </label>
          <input
            id="register-email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          />
        </div>

        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="register-password">
            Password
          </label>
          <input
            id="register-password"
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full rounded-2xl border border-slate-300 px-4 py-3 outline-none transition focus:border-emerald-500"
          />
        </div>

        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700" htmlFor="register-password-confirmation">
            Confirm password
          </label>
          <input
            id="register-password-confirmation"
            type="password"
            required
            value={passwordConfirmation}
            onChange={(e) => setPasswordConfirmation(e.target.value)}
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
          {isPending ? 'Processing...' : 'Create account'}
        </button>
      </form>
    </AuthLayout>
  );
}
