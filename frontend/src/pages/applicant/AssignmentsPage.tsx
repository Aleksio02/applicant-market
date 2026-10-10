import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { FileText } from 'lucide-react'
import { assignmentApi } from '@/api/assignment'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import {
  ATTEMPT_STATUS_LABELS,
  VERDICT_LABELS,
  formatDeadline,
} from '@/shared/lib/assignmentHelpers'
import type { AssignmentAttempt } from '@/shared/types/assignment'

export default function AssignmentsPage() {
  const { data: attempts = [], isLoading } = useQuery({
    queryKey: ['applicant', 'attempts'],
    queryFn: assignmentApi.getMyAttempts,
  })

  return (
    <div className="mx-auto max-w-4xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">Мои попытки заданий</h1>
        <p className="mt-1 text-sm text-white/60">
          Задания, которые вы взяли или уже отправили на оценку.
        </p>
      </div>

      {isLoading ? (
        <div className="flex h-64 items-center justify-center">
          <Spinner className="h-6 w-6" />
        </div>
      ) : attempts.length === 0 ? (
        <EmptyState
          icon={<FileText className="h-8 w-8" />}
          title="Пока нет попыток"
          description="Возьмите тестовое задание на странице подходящей вакансии."
          action={
            <Link to="/applicant/vacancies">
              <Badge variant="pink">К вакансиям</Badge>
            </Link>
          }
        />
      ) : (
        <div className="space-y-2">
          {attempts.map((a) => (
            <AttemptCard key={a.id} attempt={a} />
          ))}
        </div>
      )}
    </div>
  )
}

function AttemptCard({ attempt }: { attempt: AssignmentAttempt }) {
  const statusVariant: Record<
    AssignmentAttempt['status'],
    'success' | 'warning' | 'muted' | 'danger'
  > = {
    STARTED: 'warning',
    SUBMITTED: 'muted',
    EVALUATED: 'success',
    EXPIRED: 'muted',
  }

  return (
    <Card className="p-5">
      <div className="flex flex-wrap items-center gap-2">
        <span className="text-base font-semibold">
          Задание {attempt.assignmentId.slice(0, 8)}…
        </span>
        <Badge variant={statusVariant[attempt.status]}>
          {ATTEMPT_STATUS_LABELS[attempt.status]}
        </Badge>
        {attempt.verdict && (
          <Badge variant={attempt.verdict === 'PASS' ? 'success' : 'danger'}>
            {VERDICT_LABELS[attempt.verdict]}
          </Badge>
        )}
        {attempt.score != null && (
          <Badge variant="lilac">{attempt.score} б.</Badge>
        )}
      </div>

      <div className="mt-2 text-xs text-white/50">
        Начато {new Date(attempt.startedAt).toLocaleString('ru-RU')}
        {attempt.status === 'STARTED' &&
          ` · Осталось ${formatDeadline(attempt.deadlineAt)}`}
        {attempt.submittedAt &&
          ` · Отправлено ${new Date(attempt.submittedAt).toLocaleString('ru-RU')}`}
      </div>

      {attempt.feedback && (
        <div className="mt-3 rounded-xl border border-white/10 bg-white/5 p-3 text-sm text-white/80">
          <b>Отзыв:</b> {attempt.feedback}
        </div>
      )}
    </Card>
  )
}