import { AdminLayout } from '@/components/layouts/AdminLayout';
import { PlaceholderPanel } from '@/components/ui/PlaceholderPanel';

export default function AdminUsersPage() {
  return (
    <AdminLayout>
      <PlaceholderPanel
        title="User management scaffold"
        description="Halaman ini nanti menampilkan registrasi pengguna, status aktif/nonaktif, role assignment, dan village assignment dari API Laravel."
        backHref="/admin"
        backLabel="Back to admin dashboard"
      />
    </AdminLayout>
  );
}
