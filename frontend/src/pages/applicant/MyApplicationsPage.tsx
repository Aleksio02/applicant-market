import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { FileText, Undo2 } from 'lucide-react'
import { interactionApi } from '@/api/interaction'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import {
  INTERACTION_STATUS_LABELS,
  formatSalaryRange,
  isTerminalStatus,
} from '@/shared/lib/interactionHelpers'
import type {
  Interaction,
  InteractionStatus,
} from '@/shared/types/interaction'

export default function MyApplicationsPage() {
  const queryClient = useQueryClient()
  const [error, setError] = useState<string | null>(null)

  const { data: applications = [], isLoading } = useQuery({
    queryKey: ['applicant', 'applications', 'mine'],
    queryFn: interactionApi.listMyApplications,
  })

  const withdrawMutation = useMutation({
    mutationFn: (id: string) => interactionApi.withdrawApplication(id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({
        queryKey: ['applicant', 'applications'],
      })
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось отозвать'),
  })

  return (
    <div className="mx-auto max-w-4xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">Мои отклики</h1>
        <p className="mt-1 text-sm text-white/60">
          Отклики, которые вы отправили на вакансии работодателей.
        </p>
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
      ) : applications.length === 0 ? (
        <EmptyState
          icon={<FileText className="h-8 w-8" />}
          title="Пока нет откликов"
          description="Откликайтесь на интересные вакансии — это дополнительный способ показать себя."
          action={
            <Link to="/applicant/vacancies">
              <Button>К вакансиям</Button>
            </Link>
          }
        />
      ) : (
        <div className="space-y-3">
          {applications.map((app) => (
            <ApplicationCard
              key={app.id}
              application={app}
              onWithdraw={() => withdrawMutation.mutate(app.id)}
              isWithdrawing={withdrawMutation.isPending}
            />
          ))}
        </div>
      )}
    </div>
  )
}

function ApplicationCard({
  application,
  onWithdraw,
  isWithdrawing,
}: {
  application: Interaction
  onWithdraw: () => void
  isWithdrawing: boolean
}) {
  const terminal = isTerminalStatus(application.status)

  return (
    <Card className="p-5">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <Link
              to={
                application.vacancyId
                  ? `/applicant/vacancies/${application.vacancyId}`
                  : '#'
              }
              className="text-lg font-semibold hover:text-fsp-pink"
            >
              {application.title ?? 'Вакансия'}
            </Link>
            <StatusBadge status={application.status} />
          </div>

          <div className="mt-2 text-base font-bold text-fsp-pink">
            {formatSalaryRange(application.salaryFrom, application.salaryTo)}
          </div>

          {application.message && (
            <p className="mt-3 whitespace-pre-line text-sm text-white/80">
              {application.message}
            </p>
          )}

          <div className="mt-3 text-xs text-white/40">
            Отправлено{' '}
            {new Date(application.createdAt).toLocaleString('ru-RU')}
            {application.respondedAt &&
              ` · Ответ ${new Date(application.respondedAt).toLocaleString('ru-RU')}`}
          </div>
        </div>

        {!terminal && (
          <Button
            variant="ghost"
            size="sm"
            onClick={onWithdraw}
            loading={isWithdrawing}
          >
            <Undo2 className="h-4 w-4" />
            Отозвать
          </Button>
        )}
      </div>

      {application.status === 'ACCEPTED' && (
        <div className="mt-3 rounded-xl border border-emerald-400/30 bg-emerald-500/10 px-3.5 py-2.5 text-sm text-emerald-200">
          Работодатель принял отклик — ждите связи.
        </div>
      )}
    </Card>
  )
}

function StatusBadge({ status }: { status: InteractionStatus }) {
  const variant = (() => {
    switch (status) {
      case 'SENT':
        return 'pink'
      case 'VIEWED':
        return 'lilac'
      case 'ACCEPTED':
        return 'success'
      case 'REJECTED':
        return 'danger'
      case 'WITHDRAWN':
        return 'muted'
    }
  })() as 'pink' | 'lilac' | 'success' | 'danger' | 'muted'

  return <Badge variant={variant}>{INTERACTION_STATUS_LABELS[status]}</Badge>
}