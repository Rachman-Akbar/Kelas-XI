import { AdminLayout } from '@/components/layouts/AdminLayout';
import { PlaceholderPanel } from '@/components/ui/PlaceholderPanel';

export default function AdminVillagesPage() {
  return (
    <AdminLayout>
      <PlaceholderPanel
        title="Village management scaffold"
        description="CRUD desa akan dipakai untuk membatasi akses user, produk, dan transaksi hanya di lingkungan desa yang sama."
        backHref="/admin"
        backLabel="Back to admin dashboard"
      />
    </AdminLayout>
  );
}
