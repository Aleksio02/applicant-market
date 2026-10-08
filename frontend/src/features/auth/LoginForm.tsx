import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { loginSchema, type LoginForm as LoginFormValues } from './schemas'
import { useAuth } from './useAuth'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'
import { ApiError } from '@/api/client'

export function LoginForm() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [serverError, setServerError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { login: '', password: '' },
  })

  const onSubmit = handleSubmit(async (values) => {
    setServerError(null)
    try {
      const user = await login(values)
      navigate(user.role === 'APPLICANT' ? '/applicant' : '/employer', { replace: true })
    } catch (err) {
      setServerError(err instanceof ApiError ? err.message : 'Ошибка входа')
    }
  })

  return (
    <form onSubmit={onSubmit} className="space-y-4">
      <Input
        label="Логин или email"
        placeholder="you@example.com"
        autoComplete="username"
        error={errors.login?.message}
        {...register('login')}
      />
      <Input
        label="Пароль"
        type="password"
        placeholder="••••••••"
        autoComplete="current-password"
        error={errors.password?.message}
        {...register('password')}
      />

      {serverError && (
        <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {serverError}
        </div>
      )}

      <Button type="submit" size="lg" loading={isSubmitting} className="w-full">
        Войти
      </Button>

      <p className="text-center text-sm text-white/60">
        Нет аккаунта?{' '}
        <Link to="/register" className="font-semibold text-fsp-pink hover:text-fsp-rose">
          Зарегистрироваться
        </Link>
      </p>
    </form>
  )
}