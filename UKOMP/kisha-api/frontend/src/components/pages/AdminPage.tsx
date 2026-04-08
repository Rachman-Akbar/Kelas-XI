import { AuthGuard } from '@/components/auth/AuthGuard';
import { AdminLayout } from '@/components/layouts/AdminLayout';
import { PlaceholderPanel } from '@/components/ui/PlaceholderPanel';

interface AdminPageProps {
  title: string;
  description: string;
}

/**
 * Shared wrapper for all admin sub-pages.
 * Automatically wraps with AuthGuard + AdminLayout + PlaceholderPanel.
 */
export function AdminPage({ title, description }: AdminPageProps) {
  return (
    <AuthGuard roles={['admin']}>
      <AdminLayout>
        <PlaceholderPanel
          title={title}
          description={description}
          backHref="/admin"
          backLabel="Back to admin dashboard"
        />
      </AdminLayout>
    </AuthGuard>
  );
}
