import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Briefcase, Pencil, Plus, Trash2 } from 'lucide-react'
import { applicantApi } from '@/api/applicant'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import { Modal } from '@/shared/ui/Modal'
import { Input } from '@/shared/ui/Input'
import { Textarea } from '@/shared/ui/Textarea'
import { Checkbox } from '@/shared/ui/Checkbox'
import type { AddExperienceRequest, ExperienceItem } from '@/shared/types/applicant'

export default function ExperiencePage() {
  const queryClient = useQueryClient()
  const [editing, setEditing] = useState<ExperienceItem | 'new' | null>(null)
  const [error, setError] = useState<string | null>(null)

  const { data: items = [], isLoading } = useQuery({
    queryKey: ['applicant', 'experiences'],
    queryFn: applicantApi.listExperiences,
  })

  const deleteMutation = useMutation({
    mutationFn: (id: string) => applicantApi.deleteExperience(id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'experiences'] })
      queryClient.invalidateQueries({ queryKey: ['applicant', 'completeness'] })
    },
    onError: (err) => setError(err instanceof ApiError ? err.message : 'Ошибка'),
  })

  return (
    <div className="mx-auto max-w-4xl">
      <div className="mb-6 flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">Опыт работы</h1>
          <p className="mt-1 text-sm text-white/60">
            Опишите места работы — это повышает доверие работодателей.
          </p>
        </div>
        <Button onClick={() => setEditing('new')}>
          <Plus className="h-4 w-4" />
          Добавить
        </Button>
      </div>

      {error && (
        <div className="mb-4 rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {error}
        </div>
      )}

      {isLoading ? (
        <div className="flex h-64 items-center justify-center">
          <Spinner className="h-6 w-6" />
        </div>
      ) : items.length === 0 ? (
        <EmptyState
          icon={<Briefcase className="h-8 w-8" />}
          title="Пока нет опыта"
          description="Добавьте первое место работы."
          action={
            <Button onClick={() => setEditing('new')}>
              <Plus className="h-4 w-4" />
              Добавить
            </Button>
          }
        />
      ) : (
        <div className="space-y-3">
          {items.map((item) => (
            <Card key={item.id} className="flex flex-wrap items-start justify-between gap-3 p-5">
              <div className="min-w-0 flex-1">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="text-lg font-semibold">{item.position}</span>
                  {item.current && <Badge variant="success">Сейчас</Badge>}
                </div>
                <div className="mt-1 text-sm text-white/70">{item.company}</div>
                <div className="mt-1 text-xs text-white/50">
                  {formatDate(item.startDate)} — {item.current ? 'по н.в.' : formatDate(item.endDate) || '—'}
                </div>
                {item.description && (
                  <p className="mt-2 whitespace-pre-line text-sm text-white/70">
                    {item.description}
                  </p>
                )}
              </div>
              <div className="flex shrink-0 gap-2">
                <Button variant="ghost" size="icon" onClick={() => setEditing(item)}>
                  <Pencil className="h-4 w-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={() => deleteMutation.mutate(item.id)}
                  loading={deleteMutation.isPending}
                >
                  <Trash2 className="h-4 w-4 text-red-300" />
                </Button>
              </div>
            </Card>
          ))}
        </div>
      )}

      <ExperienceModal
        editing={editing}
        onClose={() => setEditing(null)}
      />
    </div>
  )
}

function ExperienceModal({
  editing,
  onClose,
}: {
  editing: ExperienceItem | 'new' | null
  onClose: () => void
}) {
  const queryClient = useQueryClient()
  const isNew = editing === 'new'
  const item = editing && editing !== 'new' ? editing : null
  const open = editing !== null

  const [form, setForm] = useState<AddExperienceRequest>(() => initialForm(item))
  const [error, setError] = useState<string | null>(null)

  // сбрасываем форму при открытии
  useState(() => {
    setForm(initialForm(item))
  })

  const mutation = useMutation({
    mutationFn: (payload: AddExperienceRequest) =>
      isNew
        ? applicantApi.addExperience(payload)
        : applicantApi.updateExperience(item!.id, payload),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'experiences'] })
      queryClient.invalidateQueries({ queryKey: ['applicant', 'completeness'] })
      onClose()
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось сохранить'),
  })

  const submit = () => {
    if (!form.company || !form.position || !form.startDate) {
      setError('Заполните компанию, должность и дату начала')
      return
    }
    mutation.mutate({
      ...form,
      endDate: form.current ? null : form.endDate || null,
    })
  }

  return (
    <Modal open={open} onClose={onClose} title={isNew ? 'Новое место работы' : 'Редактировать'}>
      <div className="space-y-4">
        <div className="grid gap-4 sm:grid-cols-2">
          <Input
            label="Компания *"
            placeholder="ООО Ромашка"
            value={form.company ?? ''}
            onChange={(e) => setForm({ ...form, company: e.target.value })}
          />
          <Input
            label="Должность *"
            placeholder="Backend-разработчик"
            value={form.position ?? ''}
            onChange={(e) => setForm({ ...form, position: e.target.value })}
          />
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <Input
            label="Дата начала *"
            type="date"
            value={form.startDate ?? ''}
            onChange={(e) => setForm({ ...form, startDate: e.target.value })}
          />
          <Input
            label="Дата окончания"
            type="date"
            disabled={form.current}
            value={form.endDate ?? ''}
            onChange={(e) => setForm({ ...form, endDate: e.target.value })}
          />
        </div>

        <Checkbox
          name="current"
          checked={!!form.current}
          onChange={() => setForm({ ...form, current: !form.current, endDate: '' })}
          label="Работаю здесь по настоящее время"
        />

        <Textarea
          label="Описание"
          placeholder="Задачи, стек, что сделали"
          value={form.description ?? ''}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
        />

        {error && (
          <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
            {error}
          </div>
        )}

        <div className="flex justify-end gap-3 pt-2">
          <Button variant="ghost" onClick={onClose}>Отмена</Button>
          <Button onClick={submit} loading={mutation.isPending}>Сохранить</Button>
        </div>
      </div>
    </Modal>
  )
}

function initialForm(item: ExperienceItem | null): AddExperienceRequest {
  return {
    company: item?.company ?? '',
    position: item?.position ?? '',
    startDate: item?.startDate ?? '',
    endDate: item?.endDate ?? '',
    current: item?.current ?? false,
    description: item?.description ?? '',
  }
}

function formatDate(d?: string | null) {
  if (!d) return ''
  return new Date(d).toLocaleDateString('ru-RU', { year: 'numeric', month: 'short' })
}