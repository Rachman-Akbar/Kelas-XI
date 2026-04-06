import { UserLayout } from '@/components/layouts/UserLayout';
import { ProductDetailView } from '@/components/features/products/ProductDetailView';

export default async function ProductDetailPage({ params }: { params: Promise<{ productId: string }> }) {
  const { productId } = await params;

  return (
    <UserLayout>
      <div className="py-4">
        <ProductDetailView productId={Number(productId)} />
      </div>
    </UserLayout>
  );
}
