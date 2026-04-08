import { AuthPage } from '@/components/pages';
import { RegisterForm } from '@/features/auth';

export default function RegisterPage() {
  return <AuthPage form={<RegisterForm />} fallbackText="Loading register form..." />;
}
