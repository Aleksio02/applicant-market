import { useAuth } from '@/features/auth/useAuth'
import { Button } from '@/shared/ui/Button'

export default function EmployerDashboard() {
  const { user, logout } = useAuth()
  return (
    <div className="min-h-screen bg-fsp-gradient p-8 text-white">
      <div className="mx-auto max-w-3xl">
        <h1 className="text-3xl font-bold">Кабинет работодателя</h1>
        <p className="mt-2 text-white/70">
          Привет, {user?.username} ({user?.email})
        </p>
        <div className="mt-8">
          <Button variant="secondary" onClick={logout}>
            Выйти
          </Button>
        </div>
        <p className="mt-8 text-sm text-white/40">
          Здесь будет модуль employer (AM-009): профиль компании, поиск кандидатов, приглашения.
        </p>
      </div>
    </div>
  )
}