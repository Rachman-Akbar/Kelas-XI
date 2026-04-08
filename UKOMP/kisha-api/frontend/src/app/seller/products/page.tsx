import { AuthGuard } from '@/components/auth/AuthGuard';
import { SellerLayout } from '@/features/seller';
import { Card } from '@/shared/components/ui';

export default function SellerProductsPage() {
  return (
    <AuthGuard roles={['seller']}>
      <SellerLayout>
        <Card title="Products / Services" description="Manage your products and services here.">
          <div className="rounded-lg border border-dashed border-slate-300 p-8 text-center text-sm text-slate-500">No products yet. Add your first product to get started.</div>
        </Card>
      </SellerLayout>
    </AuthGuard>
  );
}
