import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Search } from 'lucide-react'
import { vacancyApi } from '@/api/vacancy'
import { Badge } from '@/shared/ui/Badge'
import { Card } from '@/shared/ui/Card'
import { Select } from '@/shared/ui/Select'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import {
  GRADES,
  SPECIALIZATIONS,
  WORK_FORMAT_LABELS,
  findGrade,
  findSpecialization,
} from '@/shared/constants/catalog'
import type { Vacancy } from '@/shared/types/vacancy'

export default function VacanciesPage() {
  const [specId, setSpecId] = useState('')
  const [gradeId, setGradeId] = useState('')
  const [format, setFormat] = useState('')

  const { data: vacancies = [], isLoading } = useQuery({
    queryKey: ['applicant', 'vacancies', { specId, gradeId, format }],
    queryFn: () =>
      vacancyApi.listPublished({
        specializationId: specId || undefined,
        gradeId: gradeId || undefined,
        format: (format as any) || undefined,
      }),
  })

  return (
    <div className="mx-auto max-w-5xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">Вакансии</h1>
        <p className="mt-1 text-sm text-white/60">
          Опубликованные вакансии от работодателей.
        </p>
      </div>

      <div className="mb-4 grid gap-3 sm:grid-cols-3">
        <Select
          placeholder="Специализация"
          value={specId}
          onChange={(e) => setSpecId(e.target.value)}
          options={SPECIALIZATIONS.map((s) => ({ value: s.id, label: s.name }))}
        />
        <Select
          placeholder="Грейд"
          value={gradeId}
          onChange={(e) => setGradeId(e.target.value)}
          options={GRADES.map((g) => ({ value: g.id, label: g.name }))}
        />
        <Select
          placeholder="Формат"
          value={format}
          onChange={(e) => setFormat(e.target.value)}
          options={Object.keys(WORK_FORMAT_LABELS).map((f) => ({
            value: f,
            label: WORK_FORMAT_LABELS[f],
          }))}
        />
      </div>

      {isLoading ? (
        <div className="flex h-64 items-center justify-center">
          <Spinner className="h-6 w-6" />
        </div>
      ) : vacancies.length === 0 ? (
        <EmptyState
          icon={<Search className="h-8 w-8" />}
          title="Вакансий не найдено"
          description="Попробуйте изменить фильтры или заглянуть позже."
        />
      ) : (
        <div className="space-y-3">
          {vacancies.map((v) => (
            <VacancyCard key={v.id} vacancy={v} />
          ))}
        </div>
      )}
    </div>
  )
}

function VacancyCard({ vacancy }: { vacancy: Vacancy }) {
  const spec = vacancy.specialization ?? findSpecialization(vacancy.specializationId)
  const grade = vacancy.grade ?? findGrade(vacancy.gradeId)

  return (
    <Card className="p-5 transition-colors hover:border-white/20">
      <Link to={`/applicant/vacancies/${vacancy.id}`} className="block">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div className="min-w-0 flex-1">
            <div className="text-lg font-semibold">{vacancy.title}</div>
            <p className="mt-1.5 line-clamp-2 text-sm text-white/60">
              {vacancy.description}
            </p>
            <div className="mt-3 flex flex-wrap gap-1.5">
              {spec && <Badge variant="lilac">{spec.name}</Badge>}
              {grade && <Badge variant="pink">{grade.name}</Badge>}
              <Badge>{WORK_FORMAT_LABELS[vacancy.format] ?? vacancy.format}</Badge>
              {vacancy.location && <Badge variant="muted">{vacancy.location}</Badge>}
            </div>
          </div>
          <div className="shrink-0 text-right">
            <div className="text-sm text-white/60">Зарплата</div>
            <div className="text-lg font-bold text-fsp-pink">
              {vacancy.salaryFrom.toLocaleString('ru-RU')} —{' '}
              {vacancy.salaryTo.toLocaleString('ru-RU')} ₽
            </div>
          </div>
        </div>
      </Link>
    </Card>
  )
}