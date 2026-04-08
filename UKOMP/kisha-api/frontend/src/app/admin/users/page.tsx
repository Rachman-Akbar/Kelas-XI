import { AuthGuard } from '@/components/auth/AuthGuard';
import { AdminPage } from '@/features/admin';

export default function AdminUsersPage() {
  return <AdminPage title="User management scaffold" description="Halaman ini nanti menampilkan registrasi pengguna, status aktif/nonaktif, role assignment, dan village assignment dari API Laravel." />;
}
