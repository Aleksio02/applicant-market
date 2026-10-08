import { createContext, useCallback, useEffect, useMemo, useState, type ReactNode } from 'react'
import { authApi } from '@/api/auth'
import { ApiError } from '@/api/client'
import type {
  AuthorizationRequest,
  RegisterRequest,
  RegisterResponse,
  User,
} from '@/shared/types/api'

interface AuthContextValue {
  user: User | null
  isLoading: boolean
  register: (payload: RegisterRequest) => Promise<RegisterResponse>
  login: (payload: AuthorizationRequest) => Promise<User>
  confirmEmail: (userId: string, code: string) => Promise<User>
  logout: () => void
  refresh: () => Promise<void>
}

export const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setLoading] = useState(true)

  const refresh = useCallback(async () => {
    try {
      const payload = await authApi.validateSession()
      setUser(payload.currentUser)
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) setUser(null)
      else setUser(null)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    refresh()
  }, [refresh])

  const login = useCallback(async (payload: AuthorizationRequest) => {
    const res = await authApi.login(payload)
    setUser(res.user)
    return res.user
  }, [])

  const register = useCallback(async (payload: RegisterRequest) => {
    return authApi.register(payload)
  }, [])

  const confirmEmail = useCallback(async (userId: string, code: string) => {
    const res = await authApi.confirmEmail({ userId, code })
    setUser(res.user)
    return res.user
  }, [])

  const logout = useCallback(() => {
    // Кука httpOnly — стереть её из JS нельзя.
    // Если бэк добавит /api/auth/logout — вызовем. Пока просто чистим стейт.
    setUser(null)
  }, [])

  const value = useMemo(
    () => ({ user, isLoading, register, login, confirmEmail, logout, refresh }),
    [user, isLoading, register, login, confirmEmail, logout, refresh]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}