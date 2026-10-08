import type { ReactNode } from 'react'
import { Card } from './Card'

interface EmptyStateProps {
  icon?: ReactNode
  title: string
  description?: string
  action?: ReactNode
}

export function EmptyState({ icon, title, description, action }: EmptyStateProps) {
  return (
    <Card className="flex flex-col items-center gap-3 p-10 text-center">
      {icon && <div className="text-white/40">{icon}</div>}
      <div className="text-lg font-semibold">{title}</div>
      {description && <p className="max-w-sm text-sm text-white/60">{description}</p>}
      {action && <div className="mt-2">{action}</div>}
    </Card>
  )
}