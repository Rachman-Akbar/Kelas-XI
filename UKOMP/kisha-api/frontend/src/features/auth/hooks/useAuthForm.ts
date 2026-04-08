'use client';

import { useRouter } from 'next/navigation';
import { useState, useTransition } from 'react';

export function useAuthForm() {
  const router = useRouter();
  const [isPending, startTransition] = useTransition();
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  function getRedirectPath(role: string): string {
    switch (role) {
      case 'admin': return '/admin';
      case 'seller': return '/seller';
      case 'buyer': return '/buyer';
      default: return '/auth/login';
    }
  }

  function handleSuccess(message: string, redirectPath?: string): void {
    setSuccess(message);
    setError(null);
    if (redirectPath) {
      startTransition(() => { router.push(redirectPath); });
    }
  }

  function handleError(submitError: unknown): void {
    if (submitError && typeof submitError === 'object' && 'response' in submitError) {
      const response = submitError as { response?: { data?: { message?: string; errors?: Record<string, string[]> } } };
      const firstError = response.response?.data?.errors ? Object.values(response.response.data.errors)[0]?.[0] : null;
      setError(firstError ?? response.response?.data?.message ?? 'Request failed.');
      return;
    }
    setError(submitError instanceof Error ? submitError.message : 'Request failed.');
  }

  function clearMessages(): void {
    setError(null);
    setSuccess(null);
  }

  return { isPending, startTransition, error, setError, success, setSuccess, getRedirectPath, handleSuccess, handleError, clearMessages };
}
