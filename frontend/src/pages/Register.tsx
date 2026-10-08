import { AuthLayout } from '@/shared/layout/AuthLayout'
import { RegisterForm } from '@/features/auth/RegisterForm'

export default function Register() {
  return (
    <AuthLayout title="Регистрация" subtitle="Создайте аккаунт в Applicant Market">
      <RegisterForm />
    </AuthLayout>
  )
}