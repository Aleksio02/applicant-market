import type { HTMLAttributes } from 'react'
import { cn } from '@/shared/lib/cn'

export function Card({ className, ...rest }: HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn(
        'rounded-3xl border border-white/10 bg-white/[0.04] backdrop-blur-xl',
        'shadow-card',
        className
      )}
      {...rest}
    />
  )
}