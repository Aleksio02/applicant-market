import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { FileText, Plus, Power, XCircle } from 'lucide-react'
import { vacancyApi } from '@/api/vacancy'
import { ApiError } from '@/api/client'
import { Card } from '@/shared/ui/Card'
import { Button } from '@/shared/ui/Button'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import { cn } from '@/shared/lib/cn'
import { findGrade, findSpecialization, WORK_FORMAT_LABELS } from '@/shared/constants/catalog'
import type { Vacancy, VacancyStatus } from '@/shared/types/vacancy'

type Filter = 'all' | VacancyStatus

const FILTERS: { value: Filter; label: string }[] = [
  { value: 'all',       label: 'Все' },
  { value: 'DRAFT',     label: 'Черновики' },
  { value: 'PUBLISHED', label: 'Опубликованные' },
  { value: 'CLOSED',    label: 'Закрытые' },
]

export default function VacanciesListPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [filter, setFilter] = useState<Filter>('all')
  const [error, setError] = useState<string | null>(null)

  const { data: vacancies = [], isLoading } = useQuery({
    queryKey: ['employer', 'vacancies', filter],
    queryFn: () =>
      vacancyApi.listMine({
        status: filter === 'all' ? undefined : filter,
      }),
  })

  const publishMutation = useMutation({
    mutationFn: (id: string) => vacancyApi.publish(id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['employer', 'vacancies'] })
    },
    onError: (err) => setError(err instanceof ApiError ? err.message : 'Ошибка'),
  })

  const closeMutation = useMutation({
    mutationFn: (id: string) => vacancyApi.close(id),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['employer', 'vacancies'] })
    },
    onError: (err) => setError(err instanceof ApiError ? err.message : 'Ошибка'),
  })

  return (
    <div className="mx-auto max-w-5xl">
      <div className="mb-6 flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">Вакансии</h1>
          <p className="mt-1 text-sm text-white/60">
            Публикуйте вакансии — соискатели смогут откликнуться.
          </p>
        </div>
        <Link to="/employer/vacancies/new">
          <Button>
            <Plus className="h-4 w-4" />
            Создать вакансию
          </Button>
        </Link>
      </div>

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

      {error && (
        <div className="mb-4 rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {error}
        </div>
      )}

      {isLoading ? (
        <div className="flex h-64 items-center justify-center">
          <Spinner className="h-6 w-6" />
        </div>
      ) : vacancies.length === 0 ? (
        <EmptyState
          icon={<FileText className="h-8 w-8" />}
          title="Пока нет вакансий"
          description="Создайте первую вакансию — она станет доступна соискателям после публикации."
          action={
            <Link to="/employer/vacancies/new">
              <Button>
                <Plus className="h-4 w-4" />
                Создать
              </Button>
            </Link>
          }
        />
      ) : (
        <div className="space-y-3">
          {vacancies.map((v) => (
            <VacancyRow
              key={v.id}
              vacancy={v}
              onOpen={() => navigate(`/employer/vacancies/${v.id}`)}
              onPublish={() => publishMutation.mutate(v.id)}
              onClose={() => closeMutation.mutate(v.id)}
              isPublishing={publishMutation.isPending}
              isClosing={closeMutation.isPending}
            />
          ))}
        </div>
      )}
    </div>
  )
}

function VacancyRow({
  vacancy,
  onOpen,
  onPublish,
  onClose,
  isPublishing,
  isClosing,
}: {
  vacancy: Vacancy
  onOpen: () => void
  onPublish: () => void
  onClose: () => void
  isPublishing: boolean
  isClosing: boolean
}) {
  const spec = vacancy.specialization ?? findSpecialization(vacancy.specializationId)
  const grade = vacancy.grade ?? findGrade(vacancy.gradeId)

  return (
    <Card className="p-5 transition-colors hover:border-white/20">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <button
              type="button"
              onClick={onOpen}
              className="truncate text-left text-lg font-semibold hover:text-fsp-pink"
            >
              {vacancy.title}
            </button>
            <StatusBadge status={vacancy.status} />
          </div>

          <p className="mt-1.5 line-clamp-2 text-sm text-white/60">
            {vacancy.description}
          </p>

          <div className="mt-3 flex flex-wrap gap-1.5">
            {spec && <Badge variant="lilac">{spec.name}</Badge>}
            {grade && <Badge variant="pink">{grade.name}</Badge>}
            <Badge>{WORK_FORMAT_LABELS[vacancy.format] ?? vacancy.format}</Badge>
            {vacancy.location && <Badge variant="muted">{vacancy.location}</Badge>}
          </div>

          <div className="mt-3 text-sm text-white/70">
            {vacancy.salaryFrom.toLocaleString('ru-RU')}
            {' — '}
            {vacancy.salaryTo.toLocaleString('ru-RU')} ₽
          </div>
        </div>

        <div className="flex shrink-0 gap-2">
          <Button variant="ghost" size="sm" onClick={onOpen}>
            Открыть
          </Button>
          {vacancy.status === 'DRAFT' && (
            <Button size="sm" onClick={onPublish} loading={isPublishing}>
              <Power className="h-4 w-4" />
              Опубликовать
            </Button>
          )}
          {vacancy.status === 'PUBLISHED' && (
            <Button variant="ghost" size="sm" onClick={onClose} loading={isClosing}>
              <XCircle className="h-4 w-4" />
              Закрыть
            </Button>
          )}
        </div>
      </div>
    </Card>
  )
}

function StatusBadge({ status }: { status: VacancyStatus }) {
  switch (status) {
    case 'DRAFT':
      return <Badge variant="warning">Черновик</Badge>
    case 'PUBLISHED':
      return <Badge variant="success">Опубликована</Badge>
    case 'CLOSED':
      return <Badge variant="muted">Закрыта</Badge>
  }
}