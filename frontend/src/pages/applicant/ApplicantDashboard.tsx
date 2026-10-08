import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Award, Sparkles, User, Lock, ArrowRight } from 'lucide-react'
import { applicantApi } from '@/api/applicant'
import { ApiError } from '@/api/client'
import { Card } from '@/shared/ui/Card'
import { Button } from '@/shared/ui/Button'
import { Badge } from '@/shared/ui/Badge'
import { ProgressBar } from '@/shared/ui/ProgressBar'
import { Spinner } from '@/shared/ui/Spinner'
import { findGrade, findSpecialization, SPECIALIZATIONS, GRADES } from '@/shared/constants/catalog'

const MISSING_LABELS: Record<string, string> = {
  fullName: 'Имя и фамилия',
  about: 'О себе',
  phone: 'Телефон',
  experienceYears: 'Опыт работы',
  experience: 'Место работы',
  education: 'Образование',
  skills: 'Навыки',
  primaryVerifiedSkill: 'Подтверждённый primary-навык',
}

export default function ApplicantDashboard() {
  const { data: profile, isLoading: loadingProfile } = useQuery({
    queryKey: ['applicant', 'profile'],
    queryFn: applicantApi.getProfile,
  })

  const { data: completeness, isLoading: loadingCompleteness } = useQuery({
    queryKey: ['applicant', 'completeness'],
    queryFn: applicantApi.getCompleteness,
    enabled: !!profile,
  })

  const { data: skills = [] } = useQuery({
    queryKey: ['applicant', 'skills'],
    queryFn: applicantApi.listSkills,
    enabled: !!profile,
  })

  const { data: privacy } = useQuery({
    queryKey: ['applicant', 'privacy'],
    queryFn: applicantApi.getPrivacy,
    enabled: !!profile,
  })

  if (loadingProfile || loadingCompleteness) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  const primary = skills.find((s) => s.primary)
  const grade = primary?.verifiedGradeId ? findGrade(primary.verifiedGradeId) : null
  const spec = primary?.skillCategory
    ? SPECIALIZATIONS.find((s) => s.code === primary.skillCategory) ?? null
    : null

  const displayName =
    [profile?.lastName, profile?.firstName].filter(Boolean).join(' ') || 'Соискатель'

  return (
    <div className="mx-auto max-w-5xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold">{displayName}</h1>
        <p className="mt-1 text-sm text-white/60">
          Управляйте профилем, навыками и приватностью.
        </p>
      </div>

      {/* Completeness */}
      <Card className="p-6">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div className="flex-1">
            <div className="mb-2 flex items-center justify-between">
              <div className="text-sm font-medium text-white/80">
                Заполненность профиля
              </div>
              <div className="text-sm font-bold text-fsp-pink">
                {completeness?.percent ?? 0}%
              </div>
            </div>
            <ProgressBar value={completeness?.percent ?? 0} />
            {completeness && completeness.missing.length > 0 && (
              <div className="mt-3 flex flex-wrap gap-1.5">
                {completeness.missing.map((m) => (
                  <Badge key={m} variant="muted">
                    {MISSING_LABELS[m] ?? m}
                  </Badge>
                ))}
              </div>
            )}
            {completeness && completeness.missing.length === 0 && (
              <p className="mt-3 text-sm text-emerald-300">
                Профиль полностью заполнен. Отлично!
              </p>
            )}
          </div>
        </div>
      </Card>

      {/* Grid: Category + Privacy */}
      <div className="grid gap-4 sm:grid-cols-2">
        {/* Category */}
        <Card className="flex flex-col gap-3 p-5">
          <div className="flex items-center gap-2 text-fsp-pink">
            <Award className="h-5 w-5" />
            <span className="text-sm font-medium uppercase tracking-wider">
              Категория
            </span>
          </div>

          {primary && grade && spec ? (
            <>
              <div className="text-lg font-semibold">{spec.name}</div>
              <div className="flex gap-1.5">
                <Badge variant="pink">{grade.name}</Badge>
                <Badge variant="lilac">{primary.skillName ?? primary.skillCode}</Badge>
              </div>
              <p className="text-sm text-white/60">
                Навык подтверждён {primary.verifiedAt
                  ? new Date(primary.verifiedAt).toLocaleDateString('ru-RU')
                  : '—'}
              </p>
            </>
          ) : (
            <>
              <div className="text-lg font-semibold text-white/60">
                Категория не присвоена
              </div>
              <p className="text-sm text-white/60">
                Добавьте навыки и сделайте один из них основным, чтобы получить категорию.
              </p>
            </>
          )}

          <div className="mt-auto pt-2">
            <Link to="/applicant/skills">
              <Button variant={primary && grade ? 'secondary' : 'primary'}>
                {primary && grade ? 'Мои навыки' : 'Добавить навыки'}
                <ArrowRight className="h-4 w-4" />
              </Button>
            </Link>
          </div>
        </Card>

        {/* Privacy */}
        <Card className="flex flex-col gap-3 p-5">
          <div className="flex items-center gap-2 text-fsp-lilac">
            <Lock className="h-5 w-5" />
            <span className="text-sm font-medium uppercase tracking-wider">
              Приватность
            </span>
          </div>

          <div className="text-lg font-semibold">
            {privacy?.visibleInSearch ? 'Профиль виден' : 'Профиль скрыт'}
          </div>

          {privacy && (
            <div className="flex flex-wrap gap-1.5">
              {privacy.allowInvitations && <Badge variant="success">Приглашения</Badge>}
              {privacy.showFspAchievements && <Badge variant="lilac">ФСП видно</Badge>}
              {privacy.showContactsAfterAccept && <Badge>Контакты после accept</Badge>}
            </div>
          )}

          <div className="mt-auto pt-2">
            <Link to="/applicant/privacy">
              <Button variant="secondary">
                Настроить
                <ArrowRight className="h-4 w-4" />
              </Button>
            </Link>
          </div>
        </Card>
      </div>

      {/* Quick actions */}
      <div className="grid gap-4 sm:grid-cols-3">
        <QuickLink to="/applicant/profile" icon={<User className="h-4 w-4" />} label="Профиль" />
        <QuickLink to="/applicant/skills" icon={<Sparkles className="h-4 w-4" />} label="Навыки" />
        <QuickLink to="/applicant/category" icon={<Award className="h-4 w-4" />} label="Категория" />
      </div>
    </div>
  )
}

function QuickLink({ to, icon, label }: { to: string; icon: React.ReactNode; label: string }) {
  return (
    <Link to={to}>
      <Card className="flex items-center gap-3 p-4 transition-colors hover:border-white/20 hover:bg-white/[0.06]">
        <span className="text-fsp-pink">{icon}</span>
        <span className="font-medium">{label}</span>
        <ArrowRight className="ml-auto h-4 w-4 text-white/40" />
      </Card>
    </Link>
  )
}