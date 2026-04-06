import api from './api';
import type { Product, ProductCategory, ProductFilters, ProductPayload } from '@/types/product';

export const productService = {
  async list(filters: ProductFilters = {}): Promise<{ data: Product[] }> {
    const { data } = await api.get<{ data: Product[] }>('/products', { params: filters });
    return data;
  },

  async detail(productId: number): Promise<{ data: Product }> {
    const { data } = await api.get<{ data: Product }>(`/products/${productId}`);
    return data;
  },

  async categories(): Promise<{ data: ProductCategory[] }> {
    const { data } = await api.get<{ data: ProductCategory[] }>('/categories');
    return data;
  },

  async create(payload: ProductPayload): Promise<{ data: Product }> {
    const { data } = await api.post<{ data: Product }>('/products', payload);
    return data;
  },
};
