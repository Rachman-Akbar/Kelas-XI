import { UserLayout } from '@/components/layouts/UserLayout';
import { PlaceholderPanel } from '@/components/ui/PlaceholderPanel';

export default function ProductsPage() {
  return (
    <UserLayout>
      <div className="py-6">
        <PlaceholderPanel
          title="Product listing scaffold"
          description="Halaman daftar produk akan memakai filter village-aware, search, dan category filter sebelum data dihubungkan ke API Laravel."
          backHref="/"
          backLabel="Back to marketplace"
        />
      </div>
    </UserLayout>
  );
}
