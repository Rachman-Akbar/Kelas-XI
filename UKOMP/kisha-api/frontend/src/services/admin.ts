import api from './api';
import type { AdminDashboard, AdminUser, Village } from '@/types/auth';
import type { Product } from '@/types/product';

export const adminService = {
  async dashboard(): Promise<{ data: AdminDashboard }> {
    const { data } = await api.get<{ data: AdminDashboard }>('/admin/dashboard');
    return data;
  },

  async users(): Promise<{ data: AdminUser[] }> {
    const { data } = await api.get<{ data: AdminUser[] }>('/admin/users');
    return data;
  },

  async moderationQueue(): Promise<{ data: Product[] }> {
    const { data } = await api.get<{ data: Product[] }>('/admin/products/moderation');
    return data;
  },

  async villages(): Promise<{ data: Village[] }> {
    const { data } = await api.get<{ data: Village[] }>('/admin/villages');
    return data;
  },
};
