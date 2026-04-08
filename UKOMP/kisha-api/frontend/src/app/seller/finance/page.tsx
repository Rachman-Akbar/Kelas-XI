import { AuthGuard } from '@/components/auth/AuthGuard';
import { SellerLayout } from '@/features/seller';
import { Card } from '@/shared/components/ui';

export default function SellerFinancePage() {
  return (<AuthGuard roles={['seller']}><SellerLayout><Card title="Finance / Wallet" description="View balance and transactions."><div className="rounded-lg border border-dashed border-slate-300 p-8 text-center text-sm text-slate-500">No financial data yet.</div></Card></SellerLayout></AuthGuard>);
}
