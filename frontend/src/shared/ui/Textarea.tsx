import { forwardRef, type TextareaHTMLAttributes } from 'react'
import { cn } from '@/shared/lib/cn'

interface TextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string
  error?: string
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(function Textarea(
  { label, error, className, id, ...rest },
  ref
) {
  const areaId = id ?? rest.name
  return (
    <div className="w-full">
      {label && (
        <label
          htmlFor={areaId}
          className="mb-1.5 block text-sm font-medium text-white/80"
        >
          {label}
        </label>
      )}
      <textarea
        ref={ref}
        id={areaId}
        rows={4}
        className={cn(
          'w-full resize-y rounded-xl border border-white/15 bg-white/5 px-3.5 py-2.5 text-sm text-white',
          'placeholder:text-white/40 transition-colors',
          'focus:border-fsp-pink/80 focus:bg-white/10',
          error && 'border-red-400/70 focus:border-red-400',
          className
        )}
        {...rest}
      />
      {error && <p className="mt-1.5 text-xs text-red-300">{error}</p>}
    </div>
  )
})