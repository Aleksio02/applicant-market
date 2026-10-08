import type { ReactNode } from 'react'
import { Card } from '@/shared/ui/Card'

interface Props {
  title: string
  subtitle?: string
  children: ReactNode
}

export function AuthLayout({ title, subtitle, children }: Props) {
  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-fsp-gradient p-4">
      {/* Декоративные пятна */}
      <div className="pointer-events-none absolute -top-32 -left-32 h-96 w-96 rounded-full bg-fsp-pink/20 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-32 -right-32 h-96 w-96 rounded-full bg-fsp-lilac/20 blur-3xl" />

      <div className="relative w-full max-w-md">
        <div className="mb-8 flex flex-col items-center text-center">
          <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-white/10 backdrop-blur">
            {/* TODO: заменить на логотип ФСП из брендбука */}
            <span className="text-2xl font-black text-fsp-pink">ФСП</span>
          </div>
          <h1 className="text-2xl font-bold text-white">{title}</h1>
          {subtitle && <p className="mt-1 text-sm text-white/60">{subtitle}</p>}
        </div>

        <Card className="p-6 sm:p-8">{children}</Card>

        <p className="mt-6 text-center text-xs text-white/40">
          Applicant Market · ФСП · 2026
        </p>
      </div>
    </div>
  )
}