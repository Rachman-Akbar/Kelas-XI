import api from './api';
import type {
  AuthResponse,
  ForgotPasswordPayload,
  LoginPayload,
  RegisterPayload,
  ResetPasswordPayload,
  User,
} from '@/types/auth';

const TOKEN_KEY = 'auth_token';

export const authService = {

  // ================= LOGIN =================
  async login(payload: LoginPayload): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>(
      '/auth/login',
      payload
    );

    if (typeof window !== 'undefined') {
      localStorage.setItem(TOKEN_KEY, data.data.token);
    }

    return data;
  },

  // ================= REGISTER =================
  async register(payload: RegisterPayload): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>(
      '/auth/register',
      payload
    );

    if (typeof window !== 'undefined') {
      localStorage.setItem(TOKEN_KEY, data.data.token);
    }

    return data;
  },

  // ================= FORGOT PASSWORD =================
  async forgotPassword(
    payload: ForgotPasswordPayload
  ): Promise<{ message: string }> {

    const { data } = await api.post(
      '/auth/forgot-password',
      payload
    );

    return data;
  },

  // ================= RESET PASSWORD =================
  async resetPassword(
    payload: ResetPasswordPayload
  ): Promise<{ message: string }> {

    const { data } = await api.post(
      '/auth/reset-password',
      payload
    );

    return data;
  },

  // ================= CURRENT USER =================
  async me(): Promise<User | null> {
    try {
      const { data } = await api.get<{ data: User }>('/auth/me');
      return data.data;
    } catch (error) {
      if (
        typeof error === 'object' &&
        error !== null &&
        'response' in error &&
        (error as { response?: { status?: number } }).response?.status === 401
      ) {
        return null;
      }
      throw error;
    }
  },

  // ================= LOGOUT =================
  async logout(): Promise<void> {
    await api.post('/auth/logout');

    if (typeof window !== 'undefined') {
      localStorage.removeItem(TOKEN_KEY);
    }
  },
};