import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Briefcase, Plus, Power, PowerOff } from 'lucide-react'
import { employerApi } from '@/api/employer'
import { ApiError } from '@/api/client'
import { Card } from '@/shared/ui/Card'
import { Button } from '@/shared/ui/Button'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { cn } from '@/shared/lib/cn'
import { findGrade, findSpecialization, WORK_FORMAT_LABELS } from '@/shared/constants/catalog'
import type { HiringNeed } from '@/shared/types/employer'

type Filter = 'all' | 'active' | 'inactive'

const FILTERS: { value: Filter; label: string }[] = [
  { value: 'all', label: 'Все' },
  { value: 'active', label: 'Активные' },
  { value: 'inactive', label: 'Неактивные' },
]

export default function HiringNeedsListPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [filter, setFilter] = useState<Filter>('all')
  const [serverError, setServerError] = useState<string | null>(null)

  const { data: needs = [], isLoading } = useQuery({
    queryKey: ['employer', 'hiring-needs', filter],
    queryFn: () =>
      employerApi.listHiringNeeds({
        active: filter === 'all' ? undefined : filter === 'active',
      }),
  })

  const toggleMutation = useMutation({
    mutationFn: (id: string) => employerApi.deactivateHiringNeed(id),
    onSuccess: () => {
      setServerError(null)
      queryClient.invalidateQueries({ queryKey: ['employer', 'hiring-needs'] })
    },
    onError: (err) =>
      setServerError(err instanceof ApiError ? err.message : 'Не удалось изменить статус'),
  })

  return (
    <div className="mx-auto max-w-5xl">
      {/* Header */}
      <div className="mb-6 flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">Потребности</h1>
          <p className="mt-1 text-sm text-white/60">
            Опишите, какие специалисты нужны вашей команде.
          </p>
        </div>
        <Link to="/employer/hiring-needs/new">
          <Button>
            <Plus className="h-4 w-4" />
            Создать потребность
          </Button>
        </Link>
      </div>

      {/* Filters */}
      <div className="mb-4 flex gap-2">
        {FILTERS.map((f) => (
          <button
            key={f.value}
            type="button"
            onClick={() => setFilter(f.value)}
            className={cn(
              'rounded-xl border px-3.5 py-1.5 text-sm font-medium transition-colors',
              filter === f.value
                ? 'border-fsp-pink bg-fsp-pink/15 text-white'
                : 'border-white/15 bg-white/5 text-white/60 hover:bg-white/10 hover:text-white'
            )}
          >
            {f.label}
          </button>
        ))}
      </div>

      {serverError && (
        <div className="mb-4 rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {serverError}
        </div>
      )}

      {/* List */}
      {isLoading ? (
        <div className="flex h-64 items-center justify-center">
          <Spinner className="h-6 w-6" />
        </div>
      ) : needs.length === 0 ? (
        <Card className="flex flex-col items-center gap-3 p-10 text-center">
          <Briefcase className="h-8 w-8 text-white/40" />
          <div className="text-lg font-semibold">Пока нет потребностей</div>
          <p className="max-w-sm text-sm text-white/60">
            Создайте первую потребность — система начнёт подбирать кандидатов под неё.
          </p>
          <Link to="/employer/hiring-needs/new" className="mt-2">
            <Button>
              <Plus className="h-4 w-4" />
              Создать
            </Button>
          </Link>
        </Card>
      ) : (
        <div className="space-y-3">
          {needs.map((need) => (
            <HiringNeedRow
              key={need.id}
              need={need}
              onToggle={() => toggleMutation.mutate(need.id)}
              onOpen={() => navigate(`/employer/hiring-needs/${need.id}`)}
              isToggling={toggleMutation.isPending}
            />
          ))}
        </div>
      )}
    </div>
  )
}

function HiringNeedRow({
  need,
  onToggle,
  onOpen,
  isToggling,
}: {
  need: HiringNeed
  onToggle: () => void
  onOpen: () => void
  isToggling: boolean
}) {
  const spec =
    need.specialization ?? findSpecialization(need.specializationId)
  const grade = need.grade ?? findGrade(need.gradeId)

  return (
    <Card className="p-5 transition-colors hover:border-white/20">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <button
              type="button"
              onClick={onOpen}
              className="truncate text-lg font-semibold text-left hover:text-fsp-pink"
            >
              {need.title}
            </button>
            <Badge variant={need.active ? 'success' : 'muted'}>
              {need.active ? 'Активна' : 'Неактивна'}
            </Badge>
          </div>

          {need.description && (
            <p className="mt-1.5 line-clamp-2 text-sm text-white/60">
              {need.description}
            </p>
          )}

          <div className="mt-3 flex flex-wrap gap-1.5">
            {spec && <Badge variant="lilac">{spec.name}</Badge>}
            {grade && <Badge variant="pink">{grade.name}</Badge>}
            <Badge>{WORK_FORMAT_LABELS[need.format] ?? need.format}</Badge>
            {need.location && <Badge variant="muted">{need.location}</Badge>}
          </div>

          {(need.salaryFrom || need.salaryTo) && (
            <div className="mt-3 text-sm text-white/70">
              {need.salaryFrom?.toLocaleString('ru-RU')}
              {' — '}
              {need.salaryTo?.toLocaleString('ru-RU')} ₽
            </div>
          )}
        </div>

        <div className="flex shrink-0 gap-2">
          <Button variant="ghost" size="sm" onClick={onOpen}>
            Открыть
          </Button>
          {need.active && (
            <Button
              variant="ghost"
              size="sm"
              onClick={onToggle}
              loading={isToggling}
              title="Деактивировать"
            >
              <PowerOff className="h-4 w-4" />
            </Button>
          )}
          {!need.active && (
            <Button
              variant="ghost"
              size="sm"
              onClick={onToggle}
              loading={isToggling}
              title="Активировать"
            >
              <Power className="h-4 w-4" />
            </Button>
          )}
        </div>
      </div>
    </Card>
  )
}