import { AuthGuard } from '@/components/auth/AuthGuard';
import { SellerLayout } from '@/features/seller';
import { Card } from '@/shared/components/ui';

export default function SellerPromotionsPage() {
  return (<AuthGuard roles={['seller']}><SellerLayout><Card title="Promotions" description="Create and manage discounts."><div className="rounded-lg border border-dashed border-slate-300 p-8 text-center text-sm text-slate-500">No promotions yet.</div></Card></SellerLayout></AuthGuard>);
}
