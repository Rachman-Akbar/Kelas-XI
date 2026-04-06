'use client';

import Image from 'next/image';
import { useEffect, useState } from 'react';
import { productService } from '@/services/products';
import type { Product } from '@/types/product';

export function ProductDetailView({ productId }: { productId: number }) {
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadProduct(): Promise<void> {
      try {
        setLoading(true);
        const response = await productService.detail(productId);

        if (isMounted) {
          setProduct(response.data);
          setError(null);
        }
      } catch (fetchError) {
        if (isMounted) {
          setError(fetchError instanceof Error ? fetchError.message : 'Failed to load product detail.');
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    void loadProduct();

    return () => {
      isMounted = false;
    };
  }, [productId]);

  if (loading) {
    return (
      <div className="grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
        <div className="h-[420px] animate-pulse rounded-[2rem] bg-slate-200" />
        <div className="space-y-4 rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm">
          <div className="h-6 w-1/2 animate-pulse rounded bg-slate-200" />
          <div className="h-10 w-2/3 animate-pulse rounded bg-slate-200" />
          <div className="h-28 animate-pulse rounded bg-slate-200" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-[2rem] border border-rose-200 bg-rose-50 p-6 text-rose-700">
        {error}
      </div>
    );
  }

  if (!product) {
    return (
      <div className="rounded-[2rem] border border-slate-200 bg-white p-6 text-slate-600">
        Product not found.
      </div>
    );
  }

  const primaryImage = product.images?.find((image) => image.is_primary) ?? product.images?.[0];

  return (
    <div className="grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
      <section className="overflow-hidden rounded-[2rem] border border-slate-200 bg-white shadow-sm">
        <div className="relative aspect-[4/3] bg-slate-100">
          {primaryImage || product.main_image_path ? (
            <Image
              src={`/${primaryImage?.image_path ?? product.main_image_path}`}
              alt={product.name}
              fill
              className="object-cover"
            />
          ) : (
            <div className="flex h-full items-center justify-center text-sm text-slate-500">
              No product image available.
            </div>
          )}
        </div>
        {product.images && product.images.length > 0 ? (
          <div className="flex gap-3 overflow-x-auto border-t border-slate-200 p-4">
            {product.images.map((image) => (
              <div key={image.id} className="relative h-20 w-20 shrink-0 overflow-hidden rounded-2xl border border-slate-200">
                <Image src={`/${image.image_path}`} alt={product.name} fill className="object-cover" />
              </div>
            ))}
          </div>
        ) : null}
      </section>

      <aside className="space-y-5 rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm">
        <div>
          <p className="text-sm uppercase tracking-[0.25em] text-emerald-600">{product.status}</p>
          <h1 className="mt-2 text-3xl font-semibold tracking-tight text-slate-950">{product.name}</h1>
          <p className="mt-2 text-sm text-slate-500">
            {product.category?.name ?? 'No category'} · {product.village?.name ?? 'Unknown village'}
          </p>
        </div>

        <div className="rounded-3xl bg-slate-50 p-5">
          <p className="text-sm text-slate-500">Price</p>
          <p className="mt-1 text-3xl font-semibold text-slate-950">Rp {Number(product.price).toLocaleString('id-ID')}</p>
          <div className="mt-4 grid grid-cols-2 gap-3 text-sm text-slate-600">
            <div className="rounded-2xl bg-white p-4">
              <p className="text-slate-500">Shipping</p>
              <p className="mt-1 font-medium text-slate-950">Rp {Number(product.shipping_cost).toLocaleString('id-ID')}</p>
            </div>
            <div className="rounded-2xl bg-white p-4">
              <p className="text-slate-500">Stock</p>
              <p className="mt-1 font-medium text-slate-950">{product.stock}</p>
            </div>
          </div>
        </div>

        <div>
          <h2 className="text-lg font-semibold text-slate-950">Description</h2>
          <p className="mt-3 whitespace-pre-line text-sm leading-7 text-slate-600">{product.description}</p>
        </div>

        <div className="rounded-3xl border border-dashed border-slate-300 p-4 text-sm text-slate-600">
          <p className="font-medium text-slate-900">Additional info</p>
          <p className="mt-2">Unit: {product.unit ?? '-'}</p>
          <p>Rejection reason: {product.rejection_reason ?? '-'}</p>
        </div>
      </aside>
    </div>
  );
}
