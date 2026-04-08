import { AuthGuard } from '@/components/auth/AuthGuard';
import { SellerLayout } from '@/features/seller';
import { Card } from '@/shared/components/ui';

export default function SellerOrdersPage() {
  return (
    <AuthGuard roles={['seller']}>
      <SellerLayout>
        <Card title="Orders" description="View and manage all incoming orders.">
          <div className="rounded-lg border border-dashed border-slate-300 p-8 text-center text-sm text-slate-500">No orders yet.</div>
        </Card>
      </SellerLayout>
    </AuthGuard>
  );
}
