'use client';

import { useEffect, useState } from 'react';
import { api } from '../../../shared/lib/api';

export interface Village {
  id: number;
  name: string;
  district?: string | null;
  city?: string | null;
  province?: string | null;
  is_active: boolean;
}

export function useVillages() {
  const [villages, setVillages] = useState<Village[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    async function loadVillages(): Promise<void> {
      try {
        setLoading(true);
        const { data } = await api.get<{ data: Village[] }>('/villages');
        if (active) setVillages(data.data.filter((item) => item.is_active));
      } catch (loadError) {
        if (active) setError(loadError instanceof Error ? loadError.message : 'Failed to load villages.');
      } finally {
        if (active) setLoading(false);
      }
    }
    void loadVillages();
    return () => { active = false; };
  }, []);

  return { villages, loading, error };
}
