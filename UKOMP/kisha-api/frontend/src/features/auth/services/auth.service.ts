import { api } from '../../../shared/lib/api';

export interface AuthResponse {
  message: string;
  data: {
    user: { id: number; name: string; email: string; role: 'admin' | 'seller' | 'buyer'; is_active: boolean; village?: { id: number; name: string } | null };
    token: string;
  };
}

export interface LoginPayload {
  email: string;
  password: string;
  device_name?: string;
}

export interface RegisterPayload {
  name: string;
  email: string;
  password: string;
  password_confirmation: string;
  role: 'seller' | 'buyer';
  village_id?: number;
  device_name?: string;
}

export interface ForgotPasswordPayload {
  email: string;
}

export interface ResetPasswordPayload {
  token: string;
  email: string;
  password: string;
  password_confirmation: string;
}

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

  async me(): Promise<{ data: { id: number; name: string; email: string; role: string; is_active: boolean; village?: { id: number; name: string } | null } } | null> {
    try {
      const { data } = await api.get<{ data: { id: number; name: string; email: string; role: string; is_active: boolean; village?: { id: number; name: string } | null } }>('/auth/me');
      return data;
    } catch (error) {
      if (typeof error === 'object' && error !== null && 'response' in error && (error as { response?: { status?: number } }).response?.status === 401) {
        return null;
      }
      throw error;
    }
  },

  async logout(): Promise<void> {
    await api.post('/auth/logout');
  },
};
