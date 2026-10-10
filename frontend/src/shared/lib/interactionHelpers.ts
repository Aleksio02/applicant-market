import type { InteractionStatus } from '@/shared/types/interaction'

export const INTERACTION_STATUS_LABELS: Record<InteractionStatus, string> = {
  SENT: 'Отправлено',
  VIEWED: 'Просмотрено',
  ACCEPTED: 'Принято',
  REJECTED: 'Отклонено',
  WITHDRAWN: 'Отозвано',
}

export function isTerminalStatus(status: InteractionStatus): boolean {
  return status !== 'SENT' && status !== 'VIEWED'
}

export function formatSalaryRange(from: number, to: number): string {
  return `${from.toLocaleString('ru-RU')} — ${to.toLocaleString('ru-RU')} ₽`
}