import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { applicantApi } from '@/api/applicant'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'
import { Textarea } from '@/shared/ui/Textarea'
import { Card } from '@/shared/ui/Card'
import { Spinner } from '@/shared/ui/Spinner'

const schema = z.object({
  firstName: z.string().min(1, 'Укажите имя').max(100),
  lastName: z.string().min(1, 'Укажите фамилию').max(100),
  middleName: z.string().max(100).optional(),
  phone: z.string().max(32).optional(),
  city: z.string().max(120).optional(),
  country: z.string().max(120).optional(),
  about: z.string().optional(),
  experienceYears: z.coerce.number().int().min(0).max(80).optional().nullable(),
})

type FormValues = z.infer<typeof schema>

export default function ProfilePage() {
  const queryClient = useQueryClient()
  const [serverError, setServerError] = useState<string | null>(null)
  const [saved, setSaved] = useState(false)

  const { data: profile, isLoading } = useQuery({
    queryKey: ['applicant', 'profile'],
    queryFn: applicantApi.getProfile,
  })

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      firstName: '',
      lastName: '',
      middleName: '',
      phone: '',
      city: '',
      country: '',
      about: '',
      experienceYears: undefined,
    },
  })

  useEffect(() => {
    if (profile) {
      form.reset({
        firstName: profile.firstName ?? '',
        lastName: profile.lastName ?? '',
        middleName: profile.middleName ?? '',
        phone: profile.phone ?? '',
        city: profile.city ?? '',
        country: profile.country ?? '',
        about: profile.about ?? '',
        experienceYears: profile.experienceYears ?? undefined,
      })
    }
  }, [profile, form])

  const mutation = useMutation({
    mutationFn: (values: FormValues) => {
      const payload = Object.fromEntries(
        Object.entries(values).filter(([, v]) => v !== '' && v != null && v !== undefined)
      )
      return applicantApi.updateProfile(payload)
    },
    onSuccess: () => {
      setServerError(null)
      setSaved(true)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'profile'] })
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
        <h1 className="text-2xl font-bold">Профиль</h1>
        <p className="mt-1 text-sm text-white/60">
          Расскажите о себе — эта информация видна работодателям, если профиль не скрыт.
        </p>
      </div>

      <Card className="p-6">
        <form
          onSubmit={form.handleSubmit((v) => mutation.mutate(v))}
          className="space-y-4"
        >
          <div className="grid gap-4 sm:grid-cols-2">
            <Input
              label="Имя *"
              placeholder="Иван"
              error={form.formState.errors.firstName?.message}
              {...form.register('firstName')}
            />
            <Input
              label="Фамилия *"
              placeholder="Иванов"
              error={form.formState.errors.lastName?.message}
              {...form.register('lastName')}
            />
          </div>

          <Input label="Отчество" placeholder="Иванович" {...form.register('middleName')} />

          <div className="grid gap-4 sm:grid-cols-2">
            <Input
              label="Телефон"
              placeholder="+7 (999) 123-45-67"
              {...form.register('phone')}
            />
            <Input
              label="Опыт работы (лет)"
              type="number"
              min={0}
              max={80}
              placeholder="3"
              error={form.formState.errors.experienceYears?.message}
              {...form.register('experienceYears')}
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <Input label="Город" placeholder="Москва" {...form.register('city')} />
            <Input label="Страна" placeholder="Россия" {...form.register('country')} />
          </div>

          <Textarea
            label="О себе"
            placeholder="Коротко о вашем опыте, интересах, чему учитесь"
            rows={5}
            {...form.register('about')}
          />

          {serverError && (
            <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
              {serverError}
            </div>
          )}
          {saved && (
            <div className="rounded-xl border border-emerald-400/30 bg-emerald-500/10 px-3.5 py-2.5 text-sm text-emerald-200">
              Профиль сохранён
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