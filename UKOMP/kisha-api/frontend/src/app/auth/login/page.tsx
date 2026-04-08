import { AuthPage } from '@/components/pages';
import { LoginForm } from '@/features/auth';

export default function LoginPage() {
  return <AuthPage form={<LoginForm />} fallbackText="Loading login form..." />;
}
