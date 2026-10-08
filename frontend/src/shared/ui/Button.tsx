import { forwardRef, type ButtonHTMLAttributes } from 'react'
import { cn } from '@/shared/lib/cn'
import { Spinner } from './Spinner'

type Variant = 'primary' | 'secondary' | 'ghost' | 'danger'
type Size = 'sm' | 'md' | 'lg' | 'icon'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant
  size?: Size
  loading?: boolean
  fullWidth?: boolean
}

const variants: Record<Variant, string> = {
  primary:
    'bg-fsp-pink text-white hover:bg-fsp-rose active:bg-fsp-pink shadow-lg shadow-fsp-pink/25',
  secondary:
    'bg-white/10 text-white hover:bg-white/15 border border-white/15 backdrop-blur',
  ghost:
    'bg-transparent text-white/80 hover:text-white hover:bg-white/5',
  danger:
    'bg-red-500/90 text-white hover:bg-red-500 active:bg-red-600',
}

const sizes: Record<Size, string> = {
  sm:   'h-9 px-3 text-sm rounded-lg',
  md:   'h-11 px-5 text-sm rounded-xl',
  lg:   'h-12 px-6 text-base rounded-xl',
  icon: 'h-10 w-10 rounded-xl',
}

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(function Button(
  {
    variant = 'primary',
    size = 'md',
    loading,
    fullWidth,
    className,
    children,
    disabled,
    type = 'button',
    ...rest
  },
  ref
) {
  return (
    <button
      ref={ref}
      type={type}
      disabled={disabled || loading}
      className={cn(
        'inline-flex items-center justify-center gap-2 font-semibold transition-colors',
        'disabled:opacity-50 disabled:cursor-not-allowed',
        variants[variant],
        sizes[size],
        fullWidth && 'w-full',
        className
      )}
      {...rest}
    >
      {loading && <Spinner className="h-4 w-4" />}
      {children}
    </button>
  )
})