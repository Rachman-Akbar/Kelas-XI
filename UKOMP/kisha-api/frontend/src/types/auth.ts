export type Role = 'admin' | 'seller' | 'buyer';

export interface Village {
  id: number;
  name: string;
  district?: string | null;
  city?: string | null;
  province?: string | null;
  is_active: boolean;
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
  is_active: boolean;
  village?: Pick<Village, 'id' | 'name'> | null;
}

export interface AdminUser extends User {
  created_at?: string;
}

export interface AuthResponse {
  message: string;
  data: {
    user: User;
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
  role: Exclude<Role, 'admin'>;
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

export interface AdminDashboard {
  users_total: number;
  buyers_total: number;
  sellers_total: number;
  villages_total: number;
  products_total: number;
  products_pending: number;
  products_active: number;
}
