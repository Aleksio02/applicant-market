import { forwardRef, type SelectHTMLAttributes } from 'react'
import { cn } from '@/shared/lib/cn'

interface Option {
  value: string
  label: string
}

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  error?: string
  options: Option[]
  placeholder?: string
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(function Select(
  { label, error, options, placeholder, className, id, ...rest },
  ref
) {
  const selectId = id ?? rest.name
  return (
    <div className="w-full">
      {label && (
        <label
          htmlFor={selectId}
          className="mb-1.5 block text-sm font-medium text-white/80"
        >
          {label}
        </label>
      )}
      <select
        ref={ref}
        id={selectId}
        className={cn(
          'h-11 w-full cursor-pointer rounded-xl border border-white/15 bg-white/5 px-3.5 text-sm text-white',
          'transition-colors focus:border-fsp-pink/80 focus:bg-white/10',
          '[&>option]:bg-fsp-violet [&>option]:text-white',
          error && 'border-red-400/70 focus:border-red-400',
          className
        )}
        {...rest}
      >
        {placeholder && (
          <option value="" disabled>
            {placeholder}
          </option>
        )}
        {options.map((o) => (
          <option key={o.value} value={o.value}>
            {o.label}
          </option>
        ))}
      </select>
      {error && <p className="mt-1.5 text-xs text-red-300">{error}</p>}
    </div>
  )
})