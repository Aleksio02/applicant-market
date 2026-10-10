import type {
  AssignmentAttemptStatus,
  EvaluationVerdict,
} from '@/shared/types/assignment'

export const ATTEMPT_STATUS_LABELS: Record<AssignmentAttemptStatus, string> = {
  STARTED: 'В работе',
  SUBMITTED: 'Отправлено',
  EVALUATED: 'Оценено',
  EXPIRED: 'Истекло',
}

export const VERDICT_LABELS: Record<EvaluationVerdict, string> = {
  PASS: 'Принято',
  FAIL: 'Отклонено',
}

export function formatDeadline(deadlineAt: string): string {
  const deadline = new Date(deadlineAt)
  const now = new Date()
  const diffMs = deadline.getTime() - now.getTime()

  if (diffMs <= 0) return 'срок истёк'

  const hours = Math.floor(diffMs / (1000 * 60 * 60))
  const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60))

  if (hours >= 24) {
    const days = Math.floor(hours / 24)
    return `${days} д. ${hours % 24} ч.`
  }
  if (hours > 0) return `${hours} ч. ${minutes} мин.`
  return `${minutes} мин.`
}

export function isAttemptExpired(deadlineAt: string): boolean {
  return new Date(deadlineAt).getTime() <= Date.now()
}