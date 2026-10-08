import { AuthLayout } from '@/shared/layout/AuthLayout'
import { ConfirmEmailForm } from '@/features/auth/ConfirmEmailForm'

export default function ConfirmEmail() {
  return (
    <AuthLayout title="Подтверждение почты" subtitle="Остался один шаг">
      <ConfirmEmailForm />
    </AuthLayout>
  )
}