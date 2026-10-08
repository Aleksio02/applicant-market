import { AuthLayout } from '@/shared/layout/AuthLayout'
import { LoginForm } from '@/features/auth/LoginForm'

export default function Login() {
  return (
    <AuthLayout title="Вход" subtitle="Войдите в аккаунт ФСП">
      <LoginForm />
    </AuthLayout>
  )
}