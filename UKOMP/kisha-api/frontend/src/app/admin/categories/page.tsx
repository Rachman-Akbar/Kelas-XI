import { AdminLayout } from '@/components/layouts/AdminLayout';
import { PlaceholderPanel } from '@/components/ui/PlaceholderPanel';

export default function AdminCategoriesPage() {
  return (
    <AdminLayout>
      <PlaceholderPanel
        title="Category management scaffold"
        description="Kategori goods dan service akan diatur di sini agar etalase produk tetap rapi dan reusable di seluruh desa."
        backHref="/admin"
        backLabel="Back to admin dashboard"
      />
    </AdminLayout>
  );
}
