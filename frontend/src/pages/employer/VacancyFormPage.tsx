import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ArrowLeft, Power, XCircle, Plus, Trash2 } from 'lucide-react'
import { vacancyApi } from '@/api/vacancy'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'
import { Select } from '@/shared/ui/Select'
import { Textarea } from '@/shared/ui/Textarea'
import { Checkbox } from '@/shared/ui/Checkbox'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { Modal } from '@/shared/ui/Modal'
import {
  GRADES,
  SPECIALIZATIONS,
  WORK_FORMAT_LABELS,
} from '@/shared/constants/catalog'
import { SKILLS } from '@/shared/constants/skills'
import type { VacancyRequirement, WorkFormat } from '@/shared/types/vacancy'

const schema = z
  .object({
    title: z.string().min(1, 'Укажите название').max(255),
    description: z.string().min(1, 'Заполните описание'),
    specializationId: z.string().min(1, 'Выберите специализацию'),
    gradeId: z.string().min(1, 'Выберите грейд'),
    salaryFrom: z.coerce.number().int().positive('Должно быть больше 0'),
    salaryTo: z.coerce.number().int().positive('Должно быть больше 0'),
    format: z.enum(['OFFICE', 'REMOTE', 'HYBRID']),
    location: z.string().optional(),
  })
  .refine((d) => d.salaryFrom <= d.salaryTo, {
    path: ['salaryTo'],
    message: 'Нижняя граница не может быть выше верхней',
  })

type FormValues = z.infer<typeof schema>

export default function VacancyFormPage() {
  const { id } = useParams<{ id: string }>()
  const isEdit = !!id
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [serverError, setServerError] = useState<string | null>(null)

  const { data: existing, isLoading } = useQuery({
    queryKey: ['employer', 'vacancy', id],
    queryFn: () => vacancyApi.getById(id!),
    enabled: isEdit,
  })

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      title: '',
      description: '',
      specializationId: '',
      gradeId: '',
      salaryFrom: 100000,
      salaryTo: 200000,
      format: 'REMOTE',
      location: '',
    },
  })

  useEffect(() => {
    if (existing) {
      form.reset({
        title: existing.title,
        description: existing.description,
        specializationId: existing.specializationId,
        gradeId: existing.gradeId,
        salaryFrom: existing.salaryFrom,
        salaryTo: existing.salaryTo,
        format: existing.format,
        location: existing.location ?? '',
      })
    }
  }, [existing, form])

  const saveMutation = useMutation({
    mutationFn: (values: FormValues) => {
      const payload = {
        ...values,
        location: values.location || undefined,
      }
      return isEdit
        ? vacancyApi.update(id!, payload)
        : vacancyApi.create(payload)
    },
    onSuccess: (v) => {
      setServerError(null)
      queryClient.invalidateQueries({ queryKey: ['employer', 'vacancies'] })
      if (!isEdit) navigate(`/employer/vacancies/${v.id}`, { replace: true })
    },
    onError: (err) =>
      setServerError(err instanceof ApiError ? err.message : 'Не удалось сохранить'),
  })

  const publishMutation = useMutation({
    mutationFn: () => vacancyApi.publish(id!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['employer', 'vacancy', id] })
      queryClient.invalidateQueries({ queryKey: ['employer', 'vacancies'] })
    },
  })

  const closeMutation = useMutation({
    mutationFn: () => vacancyApi.close(id!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['employer', 'vacancy', id] })
      queryClient.invalidateQueries({ queryKey: ['employer', 'vacancies'] })
    },
  })

  const onSubmit = form.handleSubmit((v) => saveMutation.mutate(v))

  if (isEdit && isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-3xl">
      <button
        type="button"
        onClick={() => navigate('/employer/vacancies')}
        className="mb-4 inline-flex items-center gap-1.5 text-sm text-white/60 hover:text-white"
      >
        <ArrowLeft className="h-4 w-4" />
        К списку вакансий
      </button>

      <div className="mb-6 flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">
            {isEdit ? 'Редактировать вакансию' : 'Новая вакансия'}
          </h1>
          {existing && (
            <div className="mt-1 flex items-center gap-2">
              <Badge
                variant={
                  existing.status === 'PUBLISHED'
                    ? 'success'
                    : existing.status === 'DRAFT'
                      ? 'warning'
                      : 'muted'
                }
              >
                {existing.status === 'DRAFT'
                  ? 'Черновик'
                  : existing.status === 'PUBLISHED'
                    ? 'Опубликована'
                    : 'Закрыта'}
              </Badge>
            </div>
          )}
        </div>

        {isEdit && existing && (
          <div className="flex gap-2">
            {existing.status === 'DRAFT' && (
              <Button
                onClick={() => publishMutation.mutate()}
                loading={publishMutation.isPending}
              >
                <Power className="h-4 w-4" />
                Опубликовать
              </Button>
            )}
            {existing.status === 'PUBLISHED' && (
              <Button
                variant="ghost"
                onClick={() => closeMutation.mutate()}
                loading={closeMutation.isPending}
              >
                <XCircle className="h-4 w-4" />
                Закрыть
              </Button>
            )}
          </div>
        )}
      </div>

      <Card className="p-6">
        <form onSubmit={onSubmit} className="space-y-4">
          <Input
            label="Название *"
            placeholder="Backend-разработчик в команду платежей"
            error={form.formState.errors.title?.message}
            {...form.register('title')}
          />

          <Textarea
            label="Описание *"
            placeholder="Задачи, стек, условия"
            rows={6}
            error={form.formState.errors.description?.message}
            {...form.register('description')}
          />

          <div className="grid gap-4 sm:grid-cols-2">
            <Controller
              control={form.control}
              name="specializationId"
              render={({ field }) => (
                <Select
                  label="Специализация *"
                  placeholder="Выберите..."
                  value={field.value}
                  onChange={field.onChange}
                  error={form.formState.errors.specializationId?.message}
                  options={SPECIALIZATIONS.map((s) => ({ value: s.id, label: s.name }))}
                />
              )}
            />
            <Controller
              control={form.control}
              name="gradeId"
              render={({ field }) => (
                <Select
                  label="Грейд *"
                  placeholder="Выберите..."
                  value={field.value}
                  onChange={field.onChange}
                  error={form.formState.errors.gradeId?.message}
                  options={GRADES.map((g) => ({
                    value: g.id,
                    label: `${g.name} (${g.level})`,
                  }))}
                />
              )}
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <Input
              label="Зарплата от, ₽ *"
              type="number"
              min={0}
              step={1000}
              error={form.formState.errors.salaryFrom?.message}
              {...form.register('salaryFrom')}
            />
            <Input
              label="Зарплата до, ₽ *"
              type="number"
              min={0}
              step={1000}
              error={form.formState.errors.salaryTo?.message}
              {...form.register('salaryTo')}
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <Controller
              control={form.control}
              name="format"
              render={({ field }) => (
                <Select
                  label="Формат работы *"
                  value={field.value}
                  onChange={field.onChange}
                  error={form.formState.errors.format?.message}
                  options={(Object.keys(WORK_FORMAT_LABELS) as WorkFormat[]).map((f) => ({
                    value: f,
                    label: WORK_FORMAT_LABELS[f],
                  }))}
                />
              )}
            />
            <Input label="Локация" placeholder="Москва" {...form.register('location')} />
          </div>

          {serverError && (
            <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
              {serverError}
            </div>
          )}

          <div className="flex justify-end gap-3 pt-2">
            <Button
              type="button"
              variant="ghost"
              onClick={() => navigate('/employer/vacancies')}
            >
              Отмена
            </Button>
            <Button type="submit" loading={saveMutation.isPending}>
              {isEdit ? 'Сохранить' : 'Создать'}
            </Button>
          </div>
        </form>
      </Card>

      {isEdit && id && <RequirementsSection vacancyId={id} />}
    </div>
  )
}

// ===== Requirements =====

function RequirementsSection({ vacancyId }: { vacancyId: string }) {
  const queryClient = useQueryClient()
  const [addOpen, setAddOpen] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const { data: items = [], isLoading } = useQuery({
    queryKey: ['employer', 'vacancy', vacancyId, 'requirements'],
    queryFn: () => vacancyApi.listRequirements(vacancyId),
  })

  const removeMutation = useMutation({
    mutationFn: (reqId: string) => vacancyApi.deleteRequirement(vacancyId, reqId),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({
        queryKey: ['employer', 'vacancy', vacancyId, 'requirements'],
      })
    },
    onError: (err) => setError(err instanceof ApiError ? err.message : 'Ошибка'),
  })

  return (
    <div className="mt-8">
      <div className="mb-3 flex items-center justify-between">
        <h2 className="text-lg font-bold">Требования (навыки)</h2>
        <Button size="sm" onClick={() => setAddOpen(true)}>
          <Plus className="h-4 w-4" />
          Добавить требование
        </Button>
      </div>

      {error && (
        <div className="mb-3 rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {error}
        </div>
      )}

      {isLoading ? (
        <div className="flex h-32 items-center justify-center">
          <Spinner className="h-5 w-5" />
        </div>
      ) : items.length === 0 ? (
        <Card className="p-6 text-center text-sm text-white/60">
          Пока нет требований. Добавьте навыки, которые важны для этой роли.
        </Card>
      ) : (
        <div className="space-y-2">
          {items.map((r) => (
            <RequirementRow
              key={r.id}
              requirement={r}
              onRemove={() => removeMutation.mutate(r.id)}
              isRemoving={removeMutation.isPending}
            />
          ))}
        </div>
      )}

      <AddRequirementModal
        key={addOpen ? 'open' : 'closed'}
        open={addOpen}
        vacancyId={vacancyId}
        onClose={() => setAddOpen(false)}
        usedSkillIds={items.map((r) => r.skillId)}
      />
    </div>
  )
}

function RequirementRow({
  requirement,
  onRemove,
  isRemoving,
}: {
  requirement: VacancyRequirement
  onRemove: () => void
  isRemoving: boolean
}) {
  const skill = requirement.skill ?? SKILLS.find((s) => s.id === requirement.skillId)

  return (
    <Card className="flex flex-wrap items-center justify-between gap-3 p-4">
      <div className="flex flex-wrap items-center gap-2">
        <span className="text-base font-semibold">
          {skill?.name ?? requirement.skillId}
        </span>
        <Badge variant="lilac">Уровень {requirement.level}/5</Badge>
        {requirement.mandatory && <Badge variant="pink">Обязательный</Badge>}
      </div>
      <Button variant="ghost" size="icon" onClick={onRemove} loading={isRemoving}>
        <Trash2 className="h-4 w-4 text-red-300" />
      </Button>
    </Card>
  )
}

function AddRequirementModal({
  open,
  vacancyId,
  onClose,
  usedSkillIds,
}: {
  open: boolean
  vacancyId: string
  onClose: () => void
  usedSkillIds: string[]
}) {
  const queryClient = useQueryClient()
  const [skillId, setSkillId] = useState('')
  const [level, setLevel] = useState('3')
  const [mandatory, setMandatory] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const available = SKILLS.filter((s) => !usedSkillIds.includes(s.id))

  const mutation = useMutation({
    mutationFn: () =>
      vacancyApi.addRequirement(vacancyId, {
        skillId,
        level: Number(level),
        mandatory,
      }),
    onSuccess: () => {
      setError(null)
      setSkillId('')
      setLevel('3')
      setMandatory(false)
      queryClient.invalidateQueries({
        queryKey: ['employer', 'vacancy', vacancyId, 'requirements'],
      })
      onClose()
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось добавить'),
  })

  const submit = () => {
    if (!skillId) {
      setError('Выберите навык')
      return
    }
    mutation.mutate()
  }

  return (
    <Modal open={open} onClose={onClose} title="Новое требование">
      <div className="space-y-4">
        <Select
          label="Навык *"
          placeholder="Выберите..."
          value={skillId}
          onChange={(e) => setSkillId(e.target.value)}
          options={available.map((s) => ({
            value: s.id,
            label: `${s.name} (${s.category})`,
          }))}
        />

        <Select
          label="Уровень (1–5)"
          value={level}
          onChange={(e) => setLevel(e.target.value)}
          options={[1, 2, 3, 4, 5].map((n) => ({ value: String(n), label: String(n) }))}
        />

        <Checkbox
          name="mandatory"
          checked={mandatory}
          onChange={(e) => setMandatory(e.target.checked)}
          label="Обязательный навык"
        />

        {error && (
          <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
            {error}
          </div>
        )}

        <div className="flex justify-end gap-3 pt-2">
          <Button variant="ghost" onClick={onClose}>
            Отмена
          </Button>
          <Button onClick={submit} loading={mutation.isPending}>
            Добавить
          </Button>
        </div>
      </div>
    </Modal>
  )
}