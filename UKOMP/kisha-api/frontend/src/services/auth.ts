import api from './api';
import type { AuthResponse, ForgotPasswordPayload, LoginPayload, RegisterPayload, ResetPasswordPayload, User } from '@/types/auth';

export const authService = {
  async login(payload: LoginPayload): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/login', payload);
    return data;
  },

  async register(payload: RegisterPayload): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/register', payload);
    return data;
  },

  async forgotPassword(payload: ForgotPasswordPayload): Promise<{ message: string }> {
    const { data } = await api.post<{ message: string }>('/auth/forgot-password', payload);
    return data;
  },

  async resetPassword(payload: ResetPasswordPayload): Promise<{ message: string }> {
    const { data } = await api.post<{ message: string }>('/auth/reset-password', payload);
    return data;
  },

  async me(): Promise<{ data: User }> {
    const { data } = await api.get<{ data: User }>('/auth/me');
    return data;
  },

  async logout(): Promise<{ message: string }> {
    const { data } = await api.post<{ message: string }>('/auth/logout');
    if (typeof window !== 'undefined') {
      window.localStorage.removeItem('auth_token');
    }
    return data;
  },
};
