import { cn } from '@/shared/lib/cn'

interface ProgressBarProps {
  value: number       // 0..100
  className?: string
}

export function ProgressBar({ value, className }: ProgressBarProps) {
  const v = Math.max(0, Math.min(100, value))
  return (
    <div className={cn('h-2 w-full overflow-hidden rounded-full bg-white/10', className)}>
      <div
        className="h-full rounded-full bg-gradient-to-r from-fsp-pink to-fsp-lilac transition-all"
        style={{ width: `${v}%` }}
      />
    </div>
  )
}