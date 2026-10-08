import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { applicantApi } from '@/api/applicant'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Checkbox } from '@/shared/ui/Checkbox'
import { Spinner } from '@/shared/ui/Spinner'
import { useState } from 'react'
import type { PrivacySettings } from '@/shared/types/applicant'

const FIELDS: { name: keyof PrivacySettings; label: string; hint: string }[] = [
  {
    name: 'visibleInSearch',
    label: 'Показывать профиль в поиске',
    hint: 'Работодатели смогут находить вас в банке кандидатов.',
  },
  {
    name: 'allowInvitations',
    label: 'Разрешить приглашения',
    hint: 'Работодатели смогут отправлять вам приглашения с зарплатой.',
  },
  {
    name: 'showContactsAfterAccept',
    label: 'Раскрывать контакты после принятия приглашения',
    hint: 'Ваши телефон и email увидит только тот работодатель, чьё приглашение вы приняли.',
  },
  {
    name: 'showFspAchievements',
    label: 'Показывать достижения ФСП',
    hint: 'Результаты соревнований будут видны работодателям как подтверждение навыков.',
  },
]

export default function PrivacyPage() {
  const queryClient = useQueryClient()
  const [serverError, setServerError] = useState<string | null>(null)
  const [saved, setSaved] = useState(false)

  const { data: privacy, isLoading } = useQuery({
    queryKey: ['applicant', 'privacy'],
    queryFn: applicantApi.getPrivacy,
  })

  const form = useForm<PrivacySettings>({
    defaultValues: {
      visibleInSearch: true,
      allowInvitations: true,
      showContactsAfterAccept: true,
      showFspAchievements: true,
    },
  })

  useEffect(() => {
    if (privacy) form.reset(privacy)
  }, [privacy, form])

  const mutation = useMutation({
    mutationFn: (values: PrivacySettings) => applicantApi.updatePrivacy(values),
    onSuccess: () => {
      setServerError(null)
      setSaved(true)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'privacy'] })
      queryClient.invalidateQueries({ queryKey: ['applicant', 'completeness'] })
      setTimeout(() => setSaved(false), 2500)
    },
    onError: (err) =>
      setServerError(err instanceof ApiError ? err.message : 'Не удалось сохранить'),
  })

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-3xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">Приватность</h1>
        <p className="mt-1 text-sm text-white/60">
          Управляйте тем, что видят работодатели.
        </p>
      </div>

      <Card className="p-6">
        <form
          onSubmit={form.handleSubmit((v) => mutation.mutate(v))}
          className="space-y-5"
        >
          {FIELDS.map((f) => (
            <div key={f.name} className="space-y-1">
              <Checkbox
                name={f.name}
                checked={form.watch(f.name)}
                onChange={(e) =>
                  form.setValue(f.name, e.target.checked, { shouldDirty: true })
                }
                label={f.label}
              />
              <p className="ml-8 text-xs text-white/40">{f.hint}</p>
            </div>
          ))}

          {serverError && (
            <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
              {serverError}
            </div>
          )}
          {saved && (
            <div className="rounded-xl border border-emerald-400/30 bg-emerald-500/10 px-3.5 py-2.5 text-sm text-emerald-200">
              Настройки сохранены
            </div>
          )}

          <div className="flex justify-end pt-2">
            <Button type="submit" loading={mutation.isPending}>
              Сохранить
            </Button>
          </div>
        </form>
      </Card>
    </div>
  )
}