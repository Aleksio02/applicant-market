import { forwardRef, type InputHTMLAttributes } from 'react'
import { cn } from '@/shared/lib/cn'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
  hint?: string
}

export const Input = forwardRef<HTMLInputElement, InputProps>(function Input(
  { label, error, hint, className, id, ...rest },
  ref
) {
  const inputId = id ?? rest.name
  return (
    <div className="w-full">
      {label && (
        <label
          htmlFor={inputId}
          className="mb-1.5 block text-sm font-medium text-white/80"
        >
          {label}
        </label>
      )}
      <input
        ref={ref}
        id={inputId}
        className={cn(
          'h-11 w-full rounded-xl border bg-white/5 px-3.5 text-sm text-white',
          'placeholder:text-white/40 transition-colors',
          'border-white/15 focus:border-fsp-pink/80 focus:bg-white/10',
          error && 'border-red-400/70 focus:border-red-400',
          className
        )}
        {...rest}
      />
      {error ? (
        <p className="mt-1.5 text-xs text-red-300">{error}</p>
      ) : hint ? (
        <p className="mt-1.5 text-xs text-white/50">{hint}</p>
      ) : null}
    </div>
  )
})