import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { employerApi } from '@/api/employer'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'
import { Textarea } from '@/shared/ui/Textarea'
import { Card } from '@/shared/ui/Card'
import { Spinner } from '@/shared/ui/Spinner'
import { ApiError } from '@/api/client'

const schema = z.object({
  name: z.string().min(1, 'Название обязательно').max(255),
  description: z.string().optional(),
  industry: z.string().optional(),
  website: z.string().url('Некорректный URL').optional().or(z.literal('')),
  contactPersonName: z.string().optional(),
  contactPersonPosition: z.string().optional(),
  contactEmail: z.string().email('Некорректный email').optional().or(z.literal('')),
  contactPhone: z.string().optional(),
  logoUrl: z.string().url('Некорректный URL').optional().or(z.literal('')),
})

type FormValues = z.infer<typeof schema>

export default function CompanyPage() {
  const queryClient = useQueryClient()
  const [serverError, setServerError] = useState<string | null>(null)

  const { data: company, isLoading } = useQuery({
    queryKey: ['employer', 'company'],
    queryFn: async () => {
      try {
        return await employerApi.getMyCompany()
      } catch (err) {
        if (err instanceof ApiError && err.status === 404) return null
        throw err
      }
    },
  })

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: '',
      description: '',
      industry: '',
      website: '',
      contactPersonName: '',
      contactPersonPosition: '',
      contactEmail: '',
      contactPhone: '',
      logoUrl: '',
    },
  })

  useEffect(() => {
    if (company) {
      form.reset({
        name: company.name ?? '',
        description: company.description ?? '',
        industry: company.industry ?? '',
        website: company.website ?? '',
        contactPersonName: company.contactPersonName ?? '',
        contactPersonPosition: company.contactPersonPosition ?? '',
        contactEmail: company.contactEmail ?? '',
        contactPhone: company.contactPhone ?? '',
        logoUrl: company.logoUrl ?? '',
      })
    }
  }, [company, form])

  const mutation = useMutation({
    mutationFn: (values: FormValues) => {
      const payload = Object.fromEntries(
        Object.entries(values).filter(([, v]) => v !== '' && v != null)
      )
      return company
        ? employerApi.updateCompany(payload)
        : employerApi.createCompany(payload as any)
    },
    onSuccess: () => {
      setServerError(null)
      queryClient.invalidateQueries({ queryKey: ['employer', 'company'] })
    },
    onError: (err) => {
      setServerError(err instanceof ApiError ? err.message : 'Не удалось сохранить')
    },
  })

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  const isNew = !company

  return (
    <div className="mx-auto max-w-3xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">
          {isNew ? 'Профиль компании' : company?.name}
        </h1>
        <p className="mt-1 text-sm text-white/60">
          {isNew
            ? 'Заполните профиль компании, чтобы размещать потребности и приглашать кандидатов.'
            : 'Обновите информацию о компании.'}
        </p>
      </div>

      <Card className="p-6">
        <form
          onSubmit={form.handleSubmit((v) => mutation.mutate(v))}
          className="space-y-4"
        >
          <Input
            label="Название компании *"
            placeholder="ООО Ромашка"
            error={form.formState.errors.name?.message}
            {...form.register('name')}
          />

          <Textarea
            label="Описание"
            placeholder="Чем занимается компания, стек, культура"
            error={form.formState.errors.description?.message}
            {...form.register('description')}
          />

          <div className="grid gap-4 sm:grid-cols-2">
            <Input
              label="Индустрия"
              placeholder="IT, финтех, ритейл"
              {...form.register('industry')}
            />
            <Input
              label="Сайт"
              placeholder="https://example.com"
              error={form.formState.errors.website?.message}
              {...form.register('website')}
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <Input
              label="Контактное лицо"
              placeholder="Иван Иванов"
              {...form.register('contactPersonName')}
            />
            <Input
              label="Должность"
              placeholder="CTO"
              {...form.register('contactPersonPosition')}
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <Input
              label="Email для связи"
              placeholder="hr@example.com"
              error={form.formState.errors.contactEmail?.message}
              {...form.register('contactEmail')}
            />
            <Input
              label="Телефон"
              placeholder="+7 (999) 000-00-00"
              {...form.register('contactPhone')}
            />
          </div>

          <Input
            label="Ссылка на логотип"
            placeholder="https://example.com/logo.png"
            error={form.formState.errors.logoUrl?.message}
            {...form.register('logoUrl')}
          />

          {serverError && (
            <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
              {serverError}
            </div>
          )}

          <div className="flex justify-end gap-3 pt-2">
            <Button type="submit" loading={mutation.isPending}>
              {isNew ? 'Создать компанию' : 'Сохранить изменения'}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  )
}