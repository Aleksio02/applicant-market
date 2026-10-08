import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useState } from 'react'
import { confirmEmailSchema, type ConfirmEmailForm as Values } from './schemas'
import { useAuth } from './useAuth'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'
import { ApiError } from '@/api/client'

export function ConfirmEmailForm() {
  const [params] = useSearchParams()
  const userId = params.get('userId') ?? ''
  const { confirmEmail } = useAuth()
  const navigate = useNavigate()
  const [serverError, setServerError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<Values>({
    resolver: zodResolver(confirmEmailSchema),
    defaultValues: { code: '' },
  })

  const onSubmit = handleSubmit(async ({ code }) => {
    setServerError(null)
    if (!userId) {
      setServerError('Не найден идентификатор пользователя')
      return
    }
    try {
      const user = await confirmEmail(userId, code)
      navigate(user.role === 'APPLICANT' ? '/applicant' : '/employer', { replace: true })
    } catch (err) {
      setServerError(err instanceof ApiError ? err.message : 'Неверный код')
    }
  })

  return (
    <form onSubmit={onSubmit} className="space-y-4">
      <p className="text-sm text-white/70">
        Мы отправили 6-значный код на вашу почту. Введите его ниже.
        <br />
        <span className="text-white/40">
          (В dev-режиме код печатается в консоль бэкенда)
        </span>
      </p>
      <Input
        label="Код подтверждения"
        placeholder="123456"
        inputMode="numeric"
        maxLength={6}
        error={errors.code?.message}
        {...register('code')}
      />

      {serverError && (
        <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {serverError}
        </div>
      )}

      <Button type="submit" size="lg" loading={isSubmitting} className="w-full">
        Подтвердить
      </Button>
    </form>
  )
}