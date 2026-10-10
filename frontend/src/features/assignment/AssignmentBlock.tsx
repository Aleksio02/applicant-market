import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { FileText, Pencil, Plus, Trash2, Users } from 'lucide-react'
import { assignmentApi } from '@/api/assignment'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { Modal } from '@/shared/ui/Modal'
import { Input } from '@/shared/ui/Input'
import { Textarea } from '@/shared/ui/Textarea'
import { Checkbox } from '@/shared/ui/Checkbox'
import { EmptyState } from '@/shared/ui/EmptyState'
import { AttemptsList } from './AttemptsList'
import type {
  CreateAssignmentRequest,
  UpdateAssignmentRequest,
  VacancyAssignment,
} from '@/shared/types/assignment'

export function AssignmentBlock({ vacancyId }: { vacancyId: string }) {
  const queryClient = useQueryClient()
  const [editOpen, setEditOpen] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const { data: assignment, isLoading } = useQuery({
    queryKey: ['employer', 'vacancy', vacancyId, 'assignment'],
    queryFn: async () => {
      try {
        return await assignmentApi.getByVacancy(vacancyId)
      } catch (err) {
        if (err instanceof ApiError && err.status === 404) return null
        throw err
      }
    },
  })

  const deleteMutation = useMutation({
    mutationFn: () => assignmentApi.deleteForVacancy(vacancyId),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({
        queryKey: ['employer', 'vacancy', vacancyId, 'assignment'],
      })
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось удалить'),
  })

  if (isLoading) {
    return (
      <div className="mt-8 flex h-32 items-center justify-center">
        <Spinner className="h-5 w-5" />
      </div>
    )
  }

  return (
    <div className="mt-8">
      <div className="mb-3 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <FileText className="h-5 w-5 text-fsp-lilac" />
          <h2 className="text-lg font-bold">Тестовое задание</h2>
        </div>
        {!assignment && (
          <Button size="sm" onClick={() => setEditOpen(true)}>
            <Plus className="h-4 w-4" />
            Добавить задание
          </Button>
        )}
      </div>

      {error && (
        <div className="mb-3 rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {error}
        </div>
      )}

      {!assignment ? (
        <EmptyState
          description="Кандидаты не смогут отправить решение, пока задание не создано."
          title="Нет тестового задания"
          action={
            <Button onClick={() => setEditOpen(true)}>
              <Plus className="h-4 w-4" />
              Создать задание
            </Button>
          }
        />
      ) : (
        <>
          <Card className="p-5">
            <div className="flex flex-wrap items-start justify-between gap-3">
              <div className="min-w-0 flex-1">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="text-base font-semibold">
                    {assignment.title}
                  </span>
                  <Badge variant={assignment.active ? 'success' : 'muted'}>
                    {assignment.active ? 'Активно' : 'Неактивно'}
                  </Badge>
                  <Badge variant="lilac">
                    {assignment.durationHours} ч. на решение
                  </Badge>
                </div>
                <p className="mt-2 whitespace-pre-line text-sm text-white/70">
                  {assignment.description}
                </p>
              </div>
              <div className="flex shrink-0 gap-2">
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={() => setEditOpen(true)}
                >
                  <Pencil className="h-4 w-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={() => {
                    if (confirm('Удалить тестовое задание?')) {
                      deleteMutation.mutate()
                    }
                  }}
                  loading={deleteMutation.isPending}
                >
                  <Trash2 className="h-4 w-4 text-red-300" />
                </Button>
              </div>
            </div>
          </Card>

          <AttemptsList assignmentId={assignment.id} />
        </>
      )}

      <AssignmentEditModal
        key={editOpen ? 'open' : 'closed'}
        open={editOpen}
        vacancyId={vacancyId}
        existing={assignment ?? null}
        onClose={() => setEditOpen(false)}
      />
    </div>
  )
}

function AssignmentEditModal({
  open,
  vacancyId,
  existing,
  onClose,
}: {
  open: boolean
  vacancyId: string
  existing: VacancyAssignment | null
  onClose: () => void
}) {
  const queryClient = useQueryClient()
  const [title, setTitle] = useState(existing?.title ?? '')
  const [description, setDescription] = useState(existing?.description ?? '')
  const [durationHours, setDurationHours] = useState(
    String(existing?.durationHours ?? 24)
  )
  const [active, setActive] = useState(existing?.active ?? true)
  const [error, setError] = useState<string | null>(null)

  const createMutation = useMutation({
    mutationFn: (payload: CreateAssignmentRequest) =>
      assignmentApi.createForVacancy(vacancyId, payload),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({
        queryKey: ['employer', 'vacancy', vacancyId, 'assignment'],
      })
      onClose()
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось создать'),
  })

  const updateMutation = useMutation({
    mutationFn: (payload: UpdateAssignmentRequest) =>
      assignmentApi.updateForVacancy(vacancyId, payload),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({
        queryKey: ['employer', 'vacancy', vacancyId, 'assignment'],
      })
      onClose()
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось обновить'),
  })

  const submit = () => {
    if (!title.trim()) {
      setError('Укажите название')
      return
    }
    if (!description.trim()) {
      setError('Заполните описание задания')
      return
    }
    const hours = Number(durationHours)
    if (!Number.isFinite(hours) || hours < 1) {
      setError('Срок должен быть минимум 1 час')
      return
    }
    if (existing) {
      updateMutation.mutate({ title, description, durationHours: hours, active })
    } else {
      createMutation.mutate({ title, description, durationHours: hours })
    }
  }

  const isPending = createMutation.isPending || updateMutation.isPending

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={existing ? 'Редактировать задание' : 'Новое тестовое задание'}
    >
      <div className="space-y-4">
        <Input
          label="Название *"
          placeholder="Реализовать LRU-кэш"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

        <Textarea
          label="Описание задания *"
          rows={6}
          placeholder="Что нужно сделать, какие требования, что прислать в ответ"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        <Input
          label="Срок на решение (часы) *"
          type="number"
          min={1}
          value={durationHours}
          onChange={(e) => setDurationHours(e.target.value)}
        />

        {existing && (
          <Checkbox
            name="active"
            checked={active}
            onChange={(e) => setActive(e.target.checked)}
            label="Активно (кандидаты могут брать задание)"
          />
        )}

        {error && (
          <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
            {error}
          </div>
        )}

        <div className="flex justify-end gap-3 pt-2">
          <Button variant="ghost" onClick={onClose}>
            Отмена
          </Button>
          <Button onClick={submit} loading={isPending}>
            {existing ? 'Сохранить' : 'Создать'}
          </Button>
        </div>
      </div>
    </Modal>
  )
}