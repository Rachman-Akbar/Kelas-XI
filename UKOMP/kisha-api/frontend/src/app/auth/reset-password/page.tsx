import { AuthPage } from '@/components/pages';
import { ResetPasswordForm } from '@/features/auth';

export default function ResetPasswordPage() {
  return <AuthPage form={<ResetPasswordForm />} fallbackText="Loading reset-password form..." />;
}
