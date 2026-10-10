import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Mail, Undo2, User } from 'lucide-react'
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

export default function OutgoingInvitationsPage() {
  const queryClient = useQueryClient()
  const [error, setError] = useState<string | null>(null)
  const [contacts, setContacts] = useState<{
    interaction: Interaction
    info: ContactInfo
  } | null>(null)
  const [contactsError, setContactsError] = useState<string | null>(null)

  const { data: invitations = [], isLoading } = useQuery({
    queryKey: ['employer', 'invitations', 'outgoing'],
    queryFn: interactionApi.listOutgoingInvitations,
  })

  const withdrawMutation = useMutation({
    mutationFn: (id: string) => interactionApi.withdrawInvitation(id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['employer', 'invitations'] })
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось отозвать'),
  })

  const contactsMutation = useMutation({
    mutationFn: (id: string) => interactionApi.getContacts(id),
    onSuccess: (info, id) => {
      setContactsError(null)
      const interaction = invitations.find((i) => i.id === id)
      if (interaction) setContacts({ interaction, info })
    },
    onError: (err) =>
      setContactsError(
        err instanceof ApiError ? err.message : 'Не удалось получить контакты'
      ),
  })

  return (
    <div className="mx-auto max-w-4xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">Исходящие приглашения</h1>
        <p className="mt-1 text-sm text-white/60">
          Приглашения, которые вы отправили кандидатам.
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
          icon={<Mail className="h-8 w-8" />}
          title="Пока нет исходящих приглашений"
          description="Найдите кандидатов через matching и отправьте приглашение."
          action={
            <Link to="/employer/candidates">
              <Button>К поиску кандидатов</Button>
            </Link>
          }
        />
      ) : (
        <div className="space-y-3">
          {invitations.map((inv) => (
            <OutgoingCard
              key={inv.id}
              invitation={inv}
              onWithdraw={() => withdrawMutation.mutate(inv.id)}
              isWithdrawing={withdrawMutation.isPending}
              onShowContacts={() => contactsMutation.mutate(inv.id)}
              isFetchingContacts={contactsMutation.isPending}
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

function OutgoingCard({
  invitation,
  onWithdraw,
  isWithdrawing,
  onShowContacts,
  isFetchingContacts,
}: {
  invitation: Interaction
  onWithdraw: () => void
  isWithdrawing: boolean
  onShowContacts: () => void
  isFetchingContacts: boolean
}) {
  const terminal = isTerminalStatus(invitation.status)
  const contactsAvailable = !!invitation.contactsRevealedAt

  return (
    <Card className="p-5">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <span className="text-lg font-semibold">
              {invitation.title ?? 'Приглашение'}
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

        <div className="flex shrink-0 flex-wrap gap-2">
          {contactsAvailable && (
            <Button
              size="sm"
              variant="secondary"
              onClick={onShowContacts}
              loading={isFetchingContacts}
            >
              <User className="h-4 w-4" />
              Контакты
            </Button>
          )}
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