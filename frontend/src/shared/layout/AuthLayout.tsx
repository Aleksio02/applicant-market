import type { ReactNode } from 'react'
import { Card } from '@/shared/ui/Card'
import { FspLogo } from '@/shared/ui/FspLogo'

interface Props {
  title: string
  subtitle?: string
  children: ReactNode
}

export function AuthLayout({ title, subtitle, children }: Props) {
  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-fsp-gradient p-4">
      <div className="pointer-events-none absolute -top-32 -left-32 h-96 w-96 rounded-full bg-fsp-pink/20 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-32 -right-32 h-96 w-96 rounded-full bg-fsp-lilac/20 blur-3xl" />

      <div className="relative w-full max-w-md">
        {/* Логотип ФСП */}
        <div className="mb-6 flex w-full justify-center px-2">
         <FspLogo width={360} rounded="2xl" />
        </div>

        {/* Заголовок страницы */}
        <div className="mb-6 text-center">
          <h1 className="text-2xl font-bold text-white">{title}</h1>
          {subtitle && <p className="mt-1 text-sm text-white/60">{subtitle}</p>}
        </div>

        {/* Форма */}
        <Card className="p-6 sm:p-8">{children}</Card>

        <p className="mt-6 text-center text-xs text-white/40">
          Applicant Market · 2026
        </p>
      </div>
    </div>
  )
}