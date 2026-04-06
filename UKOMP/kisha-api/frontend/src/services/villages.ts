import api from './api';
import type { Village } from '@/types/auth';

export const villageService = {
  async list(): Promise<{ data: Village[] }> {
    const { data } = await api.get<{ data: Village[] }>('/villages');
    return data;
  },
};
