import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { GraduationCap, Pencil, Plus, Trash2 } from 'lucide-react'
import { applicantApi } from '@/api/applicant'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import { Modal } from '@/shared/ui/Modal'
import { Input } from '@/shared/ui/Input'
import type { AddEducationRequest, EducationItem } from '@/shared/types/applicant'

export default function EducationPage() {
  const queryClient = useQueryClient()
  const [editing, setEditing] = useState<EducationItem | 'new' | null>(null)
  const [error, setError] = useState<string | null>(null)

  const { data: items = [], isLoading } = useQuery({
    queryKey: ['applicant', 'educations'],
    queryFn: applicantApi.listEducations,
  })

  const deleteMutation = useMutation({
    mutationFn: (id: string) => applicantApi.deleteEducation(id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'educations'] })
      queryClient.invalidateQueries({ queryKey: ['applicant', 'completeness'] })
    },
    onError: (err) => setError(err instanceof ApiError ? err.message : 'Ошибка'),
  })

  return (
    <div className="mx-auto max-w-4xl">
      <div className="mb-6 flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">Образование</h1>
          <p className="mt-1 text-sm text-white/60">ВУЗы, курсы, программы.</p>
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
          icon={<GraduationCap className="h-8 w-8" />}
          title="Пока нет записей об образовании"
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
                <div className="text-lg font-semibold">{item.institution}</div>
                {(item.degree || item.field) && (
                  <div className="mt-1 text-sm text-white/70">
                    {[item.degree, item.field].filter(Boolean).join(' · ')}
                  </div>
                )}
                {(item.startYear || item.endYear) && (
                  <div className="mt-1 text-xs text-white/50">
                    {item.startYear ?? '—'} — {item.endYear ?? 'н.в.'}
                  </div>
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

      <EducationModal
        key={editing === 'new' ? 'new' : editing?.id ?? 'closed'}
        editing={editing}
        onClose={() => setEditing(null)}
      />
    </div>
  )
}

function EducationModal({
  editing,
  onClose,
}: {
  editing: EducationItem | 'new' | null
  onClose: () => void
}) {
  const queryClient = useQueryClient()
  const isNew = editing === 'new'
  const item = editing && editing !== 'new' ? editing : null
  const open = editing !== null

  const [form, setForm] = useState<AddEducationRequest>(() => initialForm(item))
  const [error, setError] = useState<string | null>(null)

  const mutation = useMutation({
    mutationFn: (payload: AddEducationRequest) =>
      isNew
        ? applicantApi.addEducation(payload)
        : applicantApi.updateEducation(item!.id, payload),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'educations'] })
      queryClient.invalidateQueries({ queryKey: ['applicant', 'completeness'] })
      onClose()
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось сохранить'),
  })

  const submit = () => {
    if (!form.institution) {
      setError('Укажите учебное заведение')
      return
    }
    mutation.mutate(form)
  }

  return (
    <Modal open={open} onClose={onClose} title={isNew ? 'Новая запись' : 'Редактировать'}>
      <div className="space-y-4">
        <Input
          label="Учебное заведение *"
          value={form.institution ?? ''}
          onChange={(e) => setForm({ ...form, institution: e.target.value })}
        />
        <div className="grid gap-4 sm:grid-cols-2">
          <Input
            label="Степень"
            placeholder="Бакалавр"
            value={form.degree ?? ''}
            onChange={(e) => setForm({ ...form, degree: e.target.value })}
          />
          <Input
            label="Специальность"
            placeholder="Прикладная информатика"
            value={form.field ?? ''}
            onChange={(e) => setForm({ ...form, field: e.target.value })}
          />
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <Input
            label="Год начала"
            type="number"
            min={1950}
            max={2100}
            value={form.startYear ?? ''}
            onChange={(e) => setForm({ ...form, startYear: e.target.value ? Number(e.target.value) : undefined })}
          />
          <Input
            label="Год окончания"
            type="number"
            min={1950}
            max={2100}
            value={form.endYear ?? ''}
            onChange={(e) => setForm({ ...form, endYear: e.target.value ? Number(e.target.value) : undefined })}
          />
        </div>

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

function initialForm(item: EducationItem | null): AddEducationRequest {
  return {
    institution: item?.institution ?? '',
    degree: item?.degree ?? '',
    field: item?.field ?? '',
    startYear: item?.startYear ?? undefined,
    endYear: item?.endYear ?? undefined,
  }
}