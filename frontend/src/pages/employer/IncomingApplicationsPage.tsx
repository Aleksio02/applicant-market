import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Check, Inbox, User, X } from 'lucide-react'
import { interactionApi } from '@/api/interaction'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import { Modal } from '@/shared/ui/Modal'
import {
  INTERACTION_STATUS_LABELS,
  formatSalaryRange,
  isTerminalStatus,
} from '@/shared/lib/interactionHelpers'
import type {
  ContactInfo,
  Interaction,
  InteractionStatus,
} from '@/shared/types/interaction'

export default function IncomingApplicationsPage() {
  const queryClient = useQueryClient()
  const [error, setError] = useState<string | null>(null)
  const [contacts, setContacts] = useState<{
    interaction: Interaction
    info: ContactInfo
  } | null>(null)

  const { data: applications = [], isLoading } = useQuery({
    queryKey: ['employer', 'applications', 'incoming'],
    queryFn: interactionApi.listIncomingApplications,
  })

  const acceptMutation = useMutation({
    mutationFn: (id: string) => interactionApi.acceptApplication(id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['employer', 'applications'] })
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось принять'),
  })

  const rejectMutation = useMutation({
    mutationFn: (id: string) => interactionApi.rejectApplication(id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['employer', 'applications'] })
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось отклонить'),
  })

  const contactsMutation = useMutation({
    mutationFn: (id: string) => interactionApi.getContacts(id),
    onSuccess: (info, id) => {
      const interaction = applications.find((a) => a.id === id)
      if (interaction) setContacts({ interaction, info })
    },
  })

  return (
    <div className="mx-auto max-w-4xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">Входящие отклики</h1>
        <p className="mt-1 text-sm text-white/60">
          Кандидаты, которые сами откликнулись на ваши вакансии.
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
          icon={<Inbox className="h-8 w-8" />}
          title="Пока нет откликов"
          description="Как только кандидаты откликнутся, они появятся здесь."
        />
      ) : (
        <div className="space-y-3">
          {applications.map((app) => (
            <IncomingCard
              key={app.id}
              application={app}
              onAccept={() => acceptMutation.mutate(app.id)}
              onReject={() => rejectMutation.mutate(app.id)}
              onShowContacts={() => contactsMutation.mutate(app.id)}
              isAccepting={acceptMutation.isPending}
              isRejecting={rejectMutation.isPending}
            />
          ))}
        </div>
      )}

      <Modal
        open={!!contacts}
        onClose={() => setContacts(null)}
        title="Контакты кандидата"
      >
        {contacts && (
          <div className="space-y-3">
            <div className="rounded-xl border border-white/10 bg-white/5 p-4">
              <div className="text-xs uppercase tracking-wider text-white/50">
                Имя
              </div>
              <div className="mt-1 text-base font-semibold">
                {contacts.info.displayName || '—'}
              </div>
            </div>
            <div className="rounded-xl border border-white/10 bg-white/5 p-4">
              <div className="text-xs uppercase tracking-wider text-white/50">
                Email
              </div>
              <div className="mt-1 text-base font-mono">
                {contacts.info.email}
              </div>
            </div>
            <div className="flex justify-end">
              <Button variant="ghost" onClick={() => setContacts(null)}>
                Закрыть
              </Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  )
}

function IncomingCard({
  application,
  onAccept,
  onReject,
  onShowContacts,
  isAccepting,
  isRejecting,
}: {
  application: Interaction
  onAccept: () => void
  onReject: () => void
  onShowContacts: () => void
  isAccepting: boolean
  isRejecting: boolean
}) {
  const terminal = isTerminalStatus(application.status)

  return (
    <Card className="p-5">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <span className="text-lg font-semibold">
              {application.title ?? 'Отклик'}
            </span>
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
          </div>
        </div>

        <div className="flex shrink-0 flex-wrap gap-2">
          <Button
            size="sm"
            variant="secondary"
            onClick={onShowContacts}
          >
            <User className="h-4 w-4" />
            Контакты
          </Button>
          {!terminal && (
            <>
              <Button
                variant="ghost"
                size="sm"
                onClick={onReject}
                loading={isRejecting}
              >
                <X className="h-4 w-4" />
                Отклонить
              </Button>
              <Button size="sm" onClick={onAccept} loading={isAccepting}>
                <Check className="h-4 w-4" />
                Принять
              </Button>
            </>
          )}
        </div>
      </div>
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