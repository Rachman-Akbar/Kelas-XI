import { UserLayout } from '@/components/layouts/UserLayout';
import { PlaceholderPanel } from '@/components/ui/PlaceholderPanel';

export default function OrdersPage() {
  return (
    <UserLayout>
      <div className="py-6">
        <PlaceholderPanel
          title="Orders scaffold"
          description="Rute order akan dipakai untuk checkout sederhana, riwayat pesanan, dan status order setelah product flow stabil."
          backHref="/"
          backLabel="Back to marketplace"
        />
      </div>
    </UserLayout>
  );
}
