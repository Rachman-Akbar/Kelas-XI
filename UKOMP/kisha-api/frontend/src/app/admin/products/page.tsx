import { AdminLayout } from '@/components/layouts/AdminLayout';
import { PlaceholderPanel } from '@/components/ui/PlaceholderPanel';

export default function AdminProductsPage() {
  return (
    <AdminLayout>
      <PlaceholderPanel
        title="Product moderation scaffold"
        description="Daftar produk ini akan berisi detail barang, foto, harga, ongkir, dan status moderasi untuk review admin desa."
        backHref="/admin"
        backLabel="Back to admin dashboard"
      />
    </AdminLayout>
  );
}
