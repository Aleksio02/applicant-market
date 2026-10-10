import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Check, Inbox, X } from 'lucide-react'
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

export default function InvitationsPage() {
  const queryClient = useQueryClient()
  const [error, setError] = useState<string | null>(null)

  const { data: invitations = [], isLoading } = useQuery({
    queryKey: ['applicant', 'invitations', 'incoming'],
    queryFn: interactionApi.listIncomingInvitations,
  })

  const acceptMutation = useMutation({
    mutationFn: (id: string) => interactionApi.acceptInvitation(id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({
        queryKey: ['applicant', 'invitations'],
      })
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось принять'),
  })

  const rejectMutation = useMutation({
    mutationFn: (id: string) => interactionApi.rejectInvitation(id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({
        queryKey: ['applicant', 'invitations'],
      })
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось отклонить'),
  })

  return (
    <div className="mx-auto max-w-4xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">Входящие приглашения</h1>
        <p className="mt-1 text-sm text-white/60">
          Работодатели сами находят вас и отправляют предложения с уровнем
          зарплаты.
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
      ) : invitations.length === 0 ? (
        <EmptyState
          icon={<Inbox className="h-8 w-8" />}
          title="Пока нет приглашений"
          description="Сделайте профиль видимым и подтвердите грейд — работодатели начнут вас находить."
        />
      ) : (
        <div className="space-y-3">
          {invitations.map((inv) => (
            <InvitationCard
              key={inv.id}
              invitation={inv}
              onAccept={() => acceptMutation.mutate(inv.id)}
              onReject={() => rejectMutation.mutate(inv.id)}
              isAccepting={acceptMutation.isPending}
              isRejecting={rejectMutation.isPending}
            />
          ))}
        </div>
      )}
    </div>
  )
}

function InvitationCard({
  invitation,
  onAccept,
  onReject,
  isAccepting,
  isRejecting,
}: {
  invitation: Interaction
  onAccept: () => void
  onReject: () => void
  isAccepting: boolean
  isRejecting: boolean
}) {
  const terminal = isTerminalStatus(invitation.status)

  return (
    <Card className="p-5">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <span className="text-lg font-semibold">
              {invitation.title ?? 'Приглашение от работодателя'}
            </span>
            <StatusBadge status={invitation.status} />
          </div>

          <div className="mt-2 text-base font-bold text-fsp-pink">
            {formatSalaryRange(invitation.salaryFrom, invitation.salaryTo)}
          </div>

          {invitation.message && (
            <p className="mt-3 whitespace-pre-line text-sm text-white/80">
              {invitation.message}
            </p>
          )}

          <div className="mt-3 text-xs text-white/40">
            Отправлено{' '}
            {new Date(invitation.sentAt).toLocaleString('ru-RU')}
            {invitation.respondedAt &&
              ` · Ответ ${new Date(invitation.respondedAt).toLocaleString('ru-RU')}`}
          </div>
        </div>

        {!terminal && (
          <div className="flex shrink-0 gap-2">
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
          </div>
        )}
      </div>

      {invitation.status === 'ACCEPTED' && (
        <div className="mt-3 rounded-xl border border-emerald-400/30 bg-emerald-500/10 px-3.5 py-2.5 text-sm text-emerald-200">
          Вы приняли приглашение. Работодатель видит ваши контакты.
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