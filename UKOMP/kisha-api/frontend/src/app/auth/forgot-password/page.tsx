import { AuthPage } from '@/components/pages';
import { ForgotPasswordForm } from '@/features/auth';

export default function ForgotPasswordPage() {
  return <AuthPage form={<ForgotPasswordForm />} fallbackText="Loading forgot-password form..." />;
}
