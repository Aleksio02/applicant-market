import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { registerSchema, type RegisterForm as RegisterFormValues } from './schemas'
import { useAuth } from './useAuth'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'
import { Checkbox } from '@/shared/ui/Checkbox'
import { ApiError } from '@/api/client'
import { cn } from '@/shared/lib/cn'

const CONSENTS = [
  { value: 'DATA_PROCESSING', label: 'Согласен на обработку персональных данных', required: true },
  { value: 'PROFILE_PUBLICATION', label: 'Разрешаю публикацию профиля для работодателей', required: false },
  { value: 'CONTACT_REVEAL', label: 'Разрешаю раскрытие контактов после принятия приглашения', required: false },
] as const

export function RegisterForm() {
  const { register: registerUser } = useAuth()
  const navigate = useNavigate()
  const [serverError, setServerError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      login: '',
      email: '',
      password: '',
      confirmPassword: '',
      role: 'APPLICANT',
      acceptedConsents: ['DATA_PROCESSING'],
    },
  })

  const role = watch('role')
  const consents = watch('acceptedConsents') ?? []

  const toggleConsent = (value: (typeof CONSENTS)[number]['value']) => {
    const next = consents.includes(value)
      ? consents.filter((v) => v !== value)
      : [...consents, value]
    setValue('acceptedConsents', next, { shouldValidate: true })
  }

  const onSubmit = handleSubmit(async (values) => {
    setServerError(null)
    try {
      const res = await registerUser({
        login: values.login,
        email: values.email,
        password: values.password,
        role: values.role,
        acceptedConsents: values.acceptedConsents,
      })
      // Бэк: если включено подтверждение email — ждём код.
      // Если выключено — сразу логинимся.
      if (/confirm|code/i.test(res.message)) {
        navigate(`/confirm-email?userId=${res.userId}`, { replace: true })
      } else {
        navigate('/login', { replace: true })
      }
    } catch (err) {
      setServerError(err instanceof ApiError ? err.message : 'Ошибка регистрации')
    }
  })

  return (
    <form onSubmit={onSubmit} className="space-y-4">
      {/* Роль */}
      <div className="grid grid-cols-2 gap-3">
        {(['APPLICANT', 'EMPLOYER'] as const).map((r) => (
          <button
            key={r}
            type="button"
            onClick={() => setValue('role', r, { shouldValidate: true })}
            className={cn(
              'rounded-xl border px-4 py-3 text-sm font-semibold transition-colors',
              role === r
                ? 'border-fsp-pink bg-fsp-pink/15 text-white'
                : 'border-white/15 bg-white/5 text-white/70 hover:bg-white/10'
            )}
          >
            {r === 'APPLICANT' ? 'Соискатель' : 'Работодатель'}
          </button>
        ))}
      </div>
      {errors.role && <p className="text-xs text-red-300">{errors.role.message}</p>}

      <Input
        label="Логин"
        placeholder="например, ivan_dev"
        autoComplete="username"
        error={errors.login?.message}
        {...register('login')}
      />
      <Input
        label="Email"
        type="email"
        placeholder="you@example.com"
        autoComplete="email"
        error={errors.email?.message}
        {...register('email')}
      />
      <Input
        label="Пароль"
        type="password"
        placeholder="Минимум 8 символов"
        autoComplete="new-password"
        error={errors.password?.message}
        {...register('password')}
      />
      <Input
        label="Повторите пароль"
        type="password"
        placeholder="Ещё раз"
        autoComplete="new-password"
        error={errors.confirmPassword?.message}
        {...register('confirmPassword')}
      />

      <div className="space-y-3 pt-1">
        {CONSENTS.map((c) => (
          <Checkbox
            key={c.value}
            name={c.value}
            label={
              <>
                {c.label}
                {c.required && <span className="text-fsp-pink"> *</span>}
              </>
            }
            checked={consents.includes(c.value)}
            onChange={() => toggleConsent(c.value)}
          />
        ))}
        {errors.acceptedConsents && (
          <p className="text-xs text-red-300">{errors.acceptedConsents.message as string}</p>
        )}
      </div>

      {serverError && (
        <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {serverError}
        </div>
      )}

      <Button type="submit" size="lg" loading={isSubmitting} className="w-full">
        Зарегистрироваться
      </Button>

      <p className="text-center text-sm text-white/60">
        Уже есть аккаунт?{' '}
        <Link to="/login" className="font-semibold text-fsp-pink hover:text-fsp-rose">
          Войти
        </Link>
      </p>
    </form>
  )
}