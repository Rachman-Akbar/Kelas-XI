'use client';

import { useEffect, useState } from 'react';
import { villageService } from '@/services/villages';
import type { Village } from '@/types/auth';

/**
 * Hook to load active villages for registration form.
 */
export function useVillages() {
  const [villages, setVillages] = useState<Village[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;

    async function loadVillages(): Promise<void> {
      try {
        setLoading(true);
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
          setLoading(false);
        }
      }
    }

    void loadVillages();

    return () => {
      active = false;
    };
  }, []);

  return { villages, loading, error };
}
