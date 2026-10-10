import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Clock, FileText, PlayCircle } from 'lucide-react'
import { assignmentApi } from '@/api/assignment'
import { ApiError } from '@/api/client'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Button } from '@/shared/ui/Button'
import { Spinner } from '@/shared/ui/Spinner'
import { Modal } from '@/shared/ui/Modal'
import { Textarea } from '@/shared/ui/Textarea'
import {
  ATTEMPT_STATUS_LABELS,
  VERDICT_LABELS,
  formatDeadline,
} from '@/shared/lib/assignmentHelpers'
import type { AssignmentAttempt } from '@/shared/types/assignment'

export function ApplicantAssignmentBlock({ vacancyId }: { vacancyId: string }) {
  const queryClient = useQueryClient()
  const [submitOpen, setSubmitOpen] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const { data: assignment, isLoading } = useQuery({
    queryKey: ['applicant', 'vacancy', vacancyId, 'assignment'],
    queryFn: async () => {
      try {
        return await assignmentApi.getByVacancy(vacancyId)
      } catch (err) {
        if (err instanceof ApiError && err.status === 404) return null
        throw err
      }
    },
  })

  const { data: myAttempts = [] } = useQuery({
    queryKey: ['applicant', 'attempts'],
    queryFn: assignmentApi.getMyAttempts,
    enabled: !!assignment,
  })

  const attempt = assignment
    ? myAttempts.find((a) => a.assignmentId === assignment.id) ?? null
    : null

  const startMutation = useMutation({
    mutationFn: () => assignmentApi.startAttempt(assignment!.id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'attempts'] })
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось начать'),
  })

  if (isLoading) {
    return (
      <div className="mt-6 flex h-24 items-center justify-center">
        <Spinner className="h-5 w-5" />
      </div>
    )
  }

  if (!assignment || !assignment.active) return null

  return (
    <Card className="mt-6 p-5">
      <div className="mb-3 flex items-center gap-2 text-fsp-pink">
        <FileText className="h-5 w-5" />
        <span className="text-sm font-medium uppercase tracking-wider">
          Тестовое задание
        </span>
      </div>

      <div className="text-lg font-semibold">{assignment.title}</div>
      <p className="mt-2 whitespace-pre-line text-sm text-white/80">
        {assignment.description}
      </p>

      <div className="mt-3 flex flex-wrap gap-2">
        <Badge variant="lilac">
          <Clock className="h-3 w-3" />
          {assignment.durationHours} ч. на решение
        </Badge>
        {attempt && (
          <Badge
            variant={
              attempt.status === 'EVALUATED'
                ? attempt.verdict === 'PASS'
                  ? 'success'
                  : 'danger'
                : attempt.status === 'EXPIRED'
                  ? 'muted'
                  : 'warning'
            }
          >
            {attempt.status === 'EVALUATED' && attempt.verdict
              ? `Оценено: ${VERDICT_LABELS[attempt.verdict]}`
              : ATTEMPT_STATUS_LABELS[attempt.status]}
          </Badge>
        )}
      </div>

      {error && (
        <div className="mt-3 rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {error}
        </div>
      )}

      <div className="mt-4">
        {!attempt && (
          <Button
            onClick={() => startMutation.mutate()}
            loading={startMutation.isPending}
          >
            <PlayCircle className="h-4 w-4" />
            Начать задание
          </Button>
        )}

        {attempt?.status === 'STARTED' && (
          <div className="flex flex-wrap items-center gap-3">
            <div className="text-sm text-white/60">
              Осталось: <b>{formatDeadline(attempt.deadlineAt)}</b>
            </div>
            <Button onClick={() => setSubmitOpen(true)}>Отправить решение</Button>
          </div>
        )}

        {attempt?.status === 'SUBMITTED' && (
          <div className="text-sm text-white/60">
            Решение отправлено. Ждём оценки работодателя.
          </div>
        )}

        {attempt?.status === 'EVALUATED' && (
          <div className="space-y-2">
            <div className="text-sm">
              <b className="text-white">Вердикт:</b>{' '}
              {attempt.verdict ? VERDICT_LABELS[attempt.verdict] : '—'}
              {attempt.score != null && (
                <>
                  {' · '}
                  <b className="text-white">Оценка:</b> {attempt.score}
                </>
              )}
            </div>
            {attempt.feedback && (
              <div className="rounded-xl border border-white/10 bg-white/5 p-3 text-sm text-white/80">
                {attempt.feedback}
              </div>
            )}
          </div>
        )}

        {attempt?.status === 'EXPIRED' && (
          <div className="text-sm text-red-300">Срок решения истёк.</div>
        )}
      </div>

      <SubmitModal
        key={submitOpen ? 'open' : 'closed'}
        open={submitOpen}
        attempt={attempt}
        onClose={() => setSubmitOpen(false)}
      />
    </Card>
  )
}

function SubmitModal({
  open,
  attempt,
  onClose,
}: {
  open: boolean
  attempt: AssignmentAttempt | null
  onClose: () => void
}) {
  const queryClient = useQueryClient()
  const [content, setContent] = useState(attempt?.contentText ?? '')
  const [error, setError] = useState<string | null>(null)

  const mutation = useMutation({
    mutationFn: () =>
      assignmentApi.submitAttempt(attempt!.id, { contentText: content }),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'attempts'] })
      onClose()
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось отправить'),
  })

  const submit = () => {
    if (!content.trim()) {
      setError('Введите решение')
      return
    }
    mutation.mutate()
  }

  return (
    <Modal open={open} onClose={onClose} title="Отправить решение" className="max-w-2xl">
      <div className="space-y-4">
        <Textarea
          label="Решение *"
          rows={12}
          placeholder="Опишите подход, приложите код, ссылки — что угодно, что поможет работодателю оценить"
          value={content}
          onChange={(e) => setContent(e.target.value)}
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
            Отправить
          </Button>
        </div>
      </div>
    </Modal>
  )
}