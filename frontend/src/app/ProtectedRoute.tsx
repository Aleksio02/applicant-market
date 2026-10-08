import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '@/features/auth/useAuth'
import type { Role } from '@/shared/types/api'
import { Spinner } from '@/shared/ui/Spinner'

interface Props {
  role?: Role
}

export function ProtectedRoute({ role }: Props) {
  const { user, isLoading } = useAuth()
  const location = useLocation()

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-fsp-gradient">
        <Spinner className="h-8 w-8" />
      </div>
    )
  }

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  if (role && user.role !== role) {
    return <Navigate to={user.role === 'APPLICANT' ? '/applicant' : '/employer'} replace />
  }

  return <Outlet />
}