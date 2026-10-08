import { forwardRef, type InputHTMLAttributes, type ReactNode } from 'react'
import { cn } from '@/shared/lib/cn'

interface CheckboxProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type'> {
  label: ReactNode
  error?: string
}

export const Checkbox = forwardRef<HTMLInputElement, CheckboxProps>(function Checkbox(
  { label, error, className, id, ...rest },
  ref
) {
  const cbId = id ?? rest.name
  return (
    <div className="w-full">
      <label htmlFor={cbId} className="flex cursor-pointer items-start gap-3">
        <span className="relative mt-0.5 inline-flex h-5 w-5 shrink-0">
          <input
            ref={ref}
            id={cbId}
            type="checkbox"
            className={cn(
              'peer h-5 w-5 shrink-0 cursor-pointer appearance-none rounded-md',
              'border border-white/25 bg-white/5 transition-colors',
              'checked:border-fsp-pink checked:bg-fsp-pink',
              'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-fsp-pink/70',
              className
            )}
            {...rest}
          />
          {/* Галочка — рисуем SVG как отдельный слой, показываем через peer-checked */}
          <svg
            viewBox="0 0 16 16"
            fill="none"
            stroke="white"
            strokeWidth="2.5"
            strokeLinecap="round"
            strokeLinejoin="round"
            className="pointer-events-none absolute left-0 top-0 h-5 w-5 scale-50 opacity-0 transition-transform peer-checked:scale-100 peer-checked:opacity-100"
          >
            <path d="M3.5 8.5l3 3 6-7" />
          </svg>
        </span>
        <span className="text-sm leading-snug text-white/75">{label}</span>
      </label>
      {error && <p className="mt-1.5 text-xs text-red-300">{error}</p>}
    </div>
  )
})