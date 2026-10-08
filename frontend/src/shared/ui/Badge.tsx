import type { ReactNode } from 'react'
import { cn } from '@/shared/lib/cn'

type Variant = 'default' | 'pink' | 'lilac' | 'success' | 'muted' | 'danger' | 'warning'

interface BadgeProps {
  children: ReactNode
  variant?: Variant
  className?: string
}

const variants: Record<Variant, string> = {
  default: 'bg-white/10 text-white/90 border-white/15',
  pink:    'bg-fsp-pink/15 text-fsp-pink border-fsp-pink/30',
  lilac:   'bg-fsp-lilac/20 text-white border-fsp-lilac/40',
  success: 'bg-emerald-500/15 text-emerald-300 border-emerald-500/30',
  muted:   'bg-white/5 text-white/50 border-white/10',
  danger:  'bg-red-500/15 text-red-300 border-red-500/30',
  warning: 'bg-amber-500/15 text-amber-300 border-amber-500/30',
}

export function Badge({ children, variant = 'default', className }: BadgeProps) {
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1 rounded-full border px-2.5 py-0.5 text-xs font-medium',
        variants[variant],
        className
      )}
    >
      {children}
    </span>
  )
}