import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Users } from 'lucide-react'
import { assignmentApi } from '@/api/assignment'
import { ApiError } from '@/api/client'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Button } from '@/shared/ui/Button'
import { Spinner } from '@/shared/ui/Spinner'
import { Modal } from '@/shared/ui/Modal'
import { Input } from '@/shared/ui/Input'
import { Textarea } from '@/shared/ui/Textarea'
import { EmptyState } from '@/shared/ui/EmptyState'
import {
  ATTEMPT_STATUS_LABELS,
  VERDICT_LABELS,
} from '@/shared/lib/assignmentHelpers'
import type {
  AssignmentAttempt,
  EvaluateAttemptRequest,
  EvaluationVerdict,
} from '@/shared/types/assignment'
import { cn } from '@/shared/lib/cn'

export function AttemptsList({ assignmentId }: { assignmentId: string }) {
  const [evaluating, setEvaluating] = useState<AssignmentAttempt | null>(null)

  const { data: attempts = [], isLoading } = useQuery({
    queryKey: ['employer', 'assignment', assignmentId, 'attempts'],
    queryFn: () => assignmentApi.getAttemptsByAssignment(assignmentId),
  })

  return (
    <div className="mt-6">
      <div className="mb-3 flex items-center gap-2">
        <Users className="h-5 w-5 text-fsp-lilac" />
        <h3 className="text-base font-bold">
          Попытки кандидатов ({attempts.length})
        </h3>
      </div>

      {isLoading ? (
        <div className="flex h-24 items-center justify-center">
          <Spinner className="h-5 w-5" />
        </div>
      ) : attempts.length === 0 ? (
        <Card className="p-5 text-center text-sm text-white/60">
          Пока никто не начал задание.
        </Card>
      ) : (
        <div className="space-y-2">
          {attempts.map((a) => (
            <AttemptRow
              key={a.id}
              attempt={a}
              onEvaluate={() => setEvaluating(a)}
            />
          ))}
        </div>
      )}

      <EvaluateModal
        key={evaluating?.id ?? 'closed'}
        attempt={evaluating}
        onClose={() => setEvaluating(null)}
      />
    </div>
  )
}

function AttemptRow({
  attempt,
  onEvaluate,
}: {
  attempt: AssignmentAttempt
  onEvaluate: () => void
}) {
  return (
    <Card className="flex flex-wrap items-center justify-between gap-3 p-4">
      <div className="min-w-0 flex-1">
        <div className="flex flex-wrap items-center gap-2">
          <span className="text-sm font-semibold">
            Кандидат {attempt.candidateId.slice(0, 8)}…
          </span>
          <StatusBadge status={attempt.status} />
          {attempt.verdict && <VerdictBadge verdict={attempt.verdict} />}
          {attempt.score != null && (
            <Badge variant="lilac">{attempt.score} б.</Badge>
          )}
        </div>
        <div className="mt-1 text-xs text-white/40">
          Начато {new Date(attempt.startedAt).toLocaleString('ru-RU')}
          {attempt.submittedAt &&
            ` · Отправлено ${new Date(attempt.submittedAt).toLocaleString('ru-RU')}`}
        </div>
      </div>
      {attempt.status === 'SUBMITTED' && (
        <Button size="sm" onClick={onEvaluate}>
          Оценить
        </Button>
      )}
    </Card>
  )
}

function StatusBadge({ status }: { status: AssignmentAttempt['status'] }) {
  const map: Record<typeof status, 'success' | 'warning' | 'muted' | 'danger'> = {
    STARTED: 'warning',
    SUBMITTED: 'lilac' as never,
    EVALUATED: 'success',
    EXPIRED: 'muted',
  }
  return (
    <Badge variant={map[status] as never}>
      {ATTEMPT_STATUS_LABELS[status]}
    </Badge>
  )
}

function VerdictBadge({ verdict }: { verdict: EvaluationVerdict }) {
  return (
    <Badge variant={verdict === 'PASS' ? 'success' : 'danger'}>
      {VERDICT_LABELS[verdict]}
    </Badge>
  )
}

function EvaluateModal({
  attempt,
  onClose,
}: {
  attempt: AssignmentAttempt | null
  onClose: () => void
}) {
  const queryClient = useQueryClient()
  const [verdict, setVerdict] = useState<EvaluationVerdict>('PASS')
  const [score, setScore] = useState('')
  const [feedback, setFeedback] = useState('')
  const [error, setError] = useState<string | null>(null)

  const mutation = useMutation({
    mutationFn: (payload: EvaluateAttemptRequest) =>
      assignmentApi.evaluateAttempt(attempt!.id, payload),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({
        queryKey: ['employer', 'assignment', attempt?.assignmentId, 'attempts'],
      })
      onClose()
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось оценить'),
  })

  if (!attempt) return null

  const submit = () => {
    const payload: EvaluateAttemptRequest = { verdict }
    const trimmedScore = score.trim()
    if (trimmedScore) {
      const n = Number(trimmedScore)
      if (!Number.isFinite(n) || n < 0 || n > 100) {
        setError('Оценка должна быть числом от 0 до 100')
        return
      }
      payload.score = n
    }
    if (feedback.trim()) payload.feedback = feedback.trim()
    mutation.mutate(payload)
  }

  return (
    <Modal
      open={!!attempt}
      onClose={onClose}
      title="Оценка решения"
      className="max-w-2xl"
    >
      <div className="space-y-4">
        <div className="rounded-xl border border-white/10 bg-black/30 p-4">
          <div className="mb-1 text-xs uppercase tracking-wider text-white/50">
            Решение кандидата
          </div>
          <pre className="max-h-64 overflow-auto whitespace-pre-wrap text-sm text-white/90">
            {attempt.contentText || '(пусто)'}
          </pre>
        </div>

        <div className="flex gap-2">
          {(['PASS', 'FAIL'] as const).map((v) => (
            <button
              key={v}
              type="button"
              onClick={() => setVerdict(v)}
              className={cn(
                'flex-1 rounded-xl border px-4 py-2.5 text-sm font-medium transition-colors',
                verdict === v
                  ? v === 'PASS'
                    ? 'border-emerald-400/60 bg-emerald-500/15 text-emerald-200'
                    : 'border-red-400/60 bg-red-500/15 text-red-200'
                  : 'border-white/15 bg-white/5 text-white/70 hover:bg-white/10'
              )}
            >
              {v === 'PASS' ? 'Принять (PASS)' : 'Отклонить (FAIL)'}
            </button>
          ))}
        </div>

        <Input
          label="Оценка (0–100, опционально)"
          type="number"
          min={0}
          max={100}
          placeholder="90"
          value={score}
          onChange={(e) => setScore(e.target.value)}
        />

        <Textarea
          label="Комментарий (опционально)"
          rows={3}
          placeholder="Что понравилось, что можно улучшить"
          value={feedback}
          onChange={(e) => setFeedback(e.target.value)}
        />

        <div className="rounded-xl border border-white/10 bg-white/5 p-3 text-xs text-white/60">
          При вердикте <b>PASS</b> система автоматически создаст отклик
          кандидата на вакансию. Кандидат увидит его в разделе «Мои отклики».
        </div>

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
            Сохранить оценку
          </Button>
        </div>
      </div>
    </Modal>
  )
}