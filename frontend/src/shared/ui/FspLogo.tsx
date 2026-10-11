import { cn } from '@/shared/lib/cn'

interface FspLogoProps {
  className?: string
  width?: number
  alt?: string
  /** Радиус скругления. По умолчанию — rounded-xl (12px). */
  rounded?: 'none' | 'sm' | 'md' | 'lg' | 'xl' | '2xl' | 'full'
}

const ROUNDED_CLASSES = {
  none: 'rounded-none',
  sm: 'rounded-sm',
  md: 'rounded-md',
  lg: 'rounded-lg',
  xl: 'rounded-xl',
  '2xl': 'rounded-2xl',
  full: 'rounded-full',
} as const

export function FspLogo({
  className,
  width = 320,
  alt = 'ФСП — Федерация спортивного программирования',
  rounded = 'xl',
}: FspLogoProps) {
  return (
    <img
      src="/logo-fsp.png"
      alt={alt}
      width={width}
      className={cn(
        'h-auto select-none',
        ROUNDED_CLASSES[rounded],
        className
      )}
      draggable={false}
    />
  )
}