import { AuthGuard } from '@/components/auth/AuthGuard';
import { SellerLayout } from '@/features/seller';
import { Card } from '@/shared/components/ui';

export default function SellerSettingsPage() {
  return (
    <AuthGuard roles={['seller']}>
      <SellerLayout>
        <Card title="Store Settings" description="Configure your store.">
          <div className="space-y-4">
            <div className="rounded-lg border border-slate-200 p-4"><h3 className="text-sm font-medium text-slate-900">Store Profile</h3><p className="mt-1 text-xs text-slate-500">Update name, description, and contact info.</p></div>
            <div className="rounded-lg border border-slate-200 p-4"><h3 className="text-sm font-medium text-slate-900">Shipping</h3><p className="mt-1 text-xs text-slate-500">Configure rates and delivery options.</p></div>
            <div className="rounded-lg border border-slate-200 p-4"><h3 className="text-sm font-medium text-slate-900">Notifications</h3><p className="mt-1 text-xs text-slate-500">Manage notification preferences.</p></div>
          </div>
        </Card>
      </SellerLayout>
    </AuthGuard>
  );
}
