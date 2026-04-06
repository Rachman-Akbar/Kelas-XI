import type { Village } from './auth';

export type ProductStatus = 'draft' | 'active' | 'inactive' | 'rejected';
export type ProductCategoryType = 'goods' | 'service';

export interface ProductCategory {
  id: number;
  name: string;
  category_type: ProductCategoryType;
  is_active: boolean;
}

export interface ProductImage {
  id: number;
  image_path: string;
  is_primary: boolean;
}

export interface Product {
  id: number;
  name: string;
  slug: string;
  description: string;
  price: string | number;
  shipping_cost: string | number;
  stock: number;
  unit?: string | null;
  status: ProductStatus;
  rejection_reason?: string | null;
  main_image_path?: string | null;
  village?: Pick<Village, 'id' | 'name'> | null;
  category?: ProductCategory | null;
  images?: ProductImage[];
  created_at?: string;
}

export interface ProductPayload {
  name: string;
  category_id: number;
  description: string;
  price: number;
  shipping_cost?: number;
  stock?: number;
  unit?: string;
  status?: Exclude<ProductStatus, 'rejected'>;
  village_id?: number;
}

export interface ProductFilters {
  search?: string;
  category_id?: number | string;
  per_page?: number | string;
}
