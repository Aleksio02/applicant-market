import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useMutation, useQuery } from '@tanstack/react-query'
import { Search, Sparkles, User } from 'lucide-react'
import { matchingApi } from '@/api/matching'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Select } from '@/shared/ui/Select'
import { Input } from '@/shared/ui/Input'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import { useGrades, useSkills, useSpecializations } from '@/shared/hooks/useCatalog'
import {
  formatAssessmentScore,
  formatMatchPercent,
  reasonTone,
} from '@/shared/lib/matchingHelpers'
import type {
  CandidateCard,
  CandidateSearchRequest,
  CandidateSearchResponse,
  CandidateSortBy,
} from '@/shared/types/matching'

const SORT_OPTIONS: { value: CandidateSortBy; label: string }[] = [
  { value: 'RANK',            label: 'По релевантности' },
  { value: 'MATCH_LEVEL',     label: 'По совпадению навыков' },
  { value: 'ASSESSMENT_SCORE', label: 'По оценке теста' },
  { value: 'FSP',             label: 'По достижениям ФСП' },
  { value: 'FRESHNESS',       label: 'По свежести' },
]

export default function CandidateSearchPage() {
  const { data: specializations = [] } = useSpecializations()
  const { data: grades = [] } = useGrades()
  const { data: skills = [] } = useSkills()

  const [specializationId, setSpecializationId] = useState('')
  const [gradeId, setGradeId] = useState('')
  const [skillIds, setSkillIds] = useState<string[]>([])
  const [sortBy, setSortBy] = useState<CandidateSortBy>('RANK')
  const [minMatchLevel, setMinMatchLevel] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [result, setResult] = useState<CandidateSearchResponse | null>(null)

  const mutation = useMutation({
    mutationFn: (payload: CandidateSearchRequest) => matchingApi.search(payload),
    onSuccess: (data) => {
      setError(null)
      setResult(data)
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось выполнить поиск'),
  })

  const submit = () => {
    if (!specializationId) {
      setError('Выберите специализацию')
      return
    }
    if (!gradeId) {
      setError('Выберите грейд')
      return
    }
    const min = minMatchLevel ? Number(minMatchLevel) / 100 : undefined
    mutation.mutate({
      specializationId,
      gradeId,
      skillIds: skillIds.length ? skillIds : undefined,
      sortBy,
      minMatchLevel: min,
      page: 0,
      size: 20,
    })
  }

  const toggleSkill = (id: string) => {
    setSkillIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    )
  }

  return (
    <div className="mx-auto max-w-5xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Поиск кандидатов</h1>
        <p className="mt-1 text-sm text-white/60">
          Опишите, кто вам нужен. Система найдёт кандидатов в подходящей категории
          и объяснит, почему каждый попал в выдачу.
        </p>
      </div>

      {/* Форма поиска */}
      <Card className="p-6">
        <div className="grid gap-4 sm:grid-cols-2">
          <Select
            label="Специализация *"
            placeholder="Выберите..."
            value={specializationId}
            onChange={(e) => setSpecializationId(e.target.value)}
            options={specializations.map((s) => ({ value: s.id, label: s.name }))}
          />
          <Select
            label="Грейд *"
            placeholder="Выберите..."
            value={gradeId}
            onChange={(e) => setGradeId(e.target.value)}
            options={grades.map((g) => ({ value: g.id, label: g.name }))}
          />
        </div>

        {/* Навыки — чипсы */}
        <div className="mt-4">
          <div className="mb-1.5 text-sm font-medium text-white/80">
            Ключевые навыки (опционально)
          </div>
          <div className="flex max-h-40 flex-wrap gap-1.5 overflow-y-auto rounded-xl border border-white/10 bg-white/5 p-3">
            {skills.map((s) => {
              const active = skillIds.includes(s.id)
              return (
                <button
                  key={s.id}
                  type="button"
                  onClick={() => toggleSkill(s.id)}
                  className={
                    'rounded-lg border px-2.5 py-1 text-xs font-medium transition-colors ' +
                    (active
                      ? 'border-fsp-pink bg-fsp-pink/15 text-white'
                      : 'border-white/15 bg-white/5 text-white/60 hover:bg-white/10 hover:text-white')
                  }
                >
                  {s.name}
                </button>
              )
            })}
          </div>
          {skillIds.length > 0 && (
            <div className="mt-1 text-xs text-white/50">
              Выбрано: {skillIds.length}
            </div>
          )}
        </div>

        <div className="mt-4 grid gap-4 sm:grid-cols-3">
          <Select
            label="Сортировка"
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as CandidateSortBy)}
            options={SORT_OPTIONS.map((o) => ({ value: o.value, label: o.label }))}
          />
          <Input
            label="Минимум совпадения, %"
            type="number"
            min={0}
            max={100}
            placeholder="0"
            value={minMatchLevel}
            onChange={(e) => setMinMatchLevel(e.target.value)}
          />
          <div className="flex items-end">
            <Button
              className="w-full"
              onClick={submit}
              loading={mutation.isPending}
            >
              <Search className="h-4 w-4" />
              Найти кандидатов
            </Button>
          </div>
        </div>

        {error && (
          <div className="mt-4 rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
            {error}
          </div>
        )}
      </Card>

      {/* Результаты */}
      {result && (
        <div>
          <div className="mb-3 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Sparkles className="h-5 w-5 text-fsp-lilac" />
              <h2 className="text-lg font-bold">
                Найдено кандидатов: {result.total}
              </h2>
            </div>
            {result.suggestion && (
              <div className="text-xs text-white/50">{result.suggestion}</div>
            )}
          </div>

          {result.items.length === 0 ? (
            <EmptyState
              icon={<User className="h-8 w-8" />}
              title="Никого не нашли"
              description="Попробуйте снизить требования или выбрать другую категорию."
            />
          ) : (
            <div className="space-y-3">
              {result.items.map((c) => (
                <CandidateRow key={c.applicantId} candidate={c} />
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  )
}

function CandidateRow({ candidate }: { candidate: CandidateCard }) {
  return (
    <Card className="p-5">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <span className="text-lg font-semibold">
              {candidate.displayName || 'Кандидат'}
            </span>
            {candidate.categoryName && (
              <Badge variant="lilac">{candidate.categoryName}</Badge>
            )}
            <Badge variant="pink">
              Совпадение: {formatMatchPercent(candidate.matchLevel)}
            </Badge>
          </div>

          <div className="mt-2 flex flex-wrap gap-1.5 text-xs text-white/60">
            {candidate.city && <span>{candidate.city}</span>}
            {candidate.experienceYears != null && (
              <span>· Опыт: {candidate.experienceYears} лет</span>
            )}
            {candidate.primaryGradeName && (
              <span>· {candidate.primaryGradeName}</span>
            )}
            {candidate.primarySkillName && (
              <span>· {candidate.primarySkillName}</span>
            )}
          </div>

          {/* Reasons */}
          {candidate.reasons.length > 0 && (
            <div className="mt-3 flex flex-wrap gap-1.5">
              {candidate.reasons.map((r) => (
                <Badge key={r.code + r.text} variant={reasonTone(r.code)}>
                  {r.text}
                </Badge>
              ))}
            </div>
          )}

          <div className="mt-3 flex flex-wrap gap-3 text-xs text-white/50">
            {candidate.assessmentScore != null && (
              <span>
                Оценка теста: {formatAssessmentScore(candidate.assessmentScore)}
              </span>
            )}
            {candidate.fspAchievementCount > 0 && (
              <span>ФСП: {candidate.fspAchievementCount} достижений</span>
            )}
          </div>
        </div>

        <div className="flex shrink-0">
          <Link to={`/employer/candidates/${candidate.applicantId}`}>
            <Button size="sm">Открыть</Button>
          </Link>
        </div>
      </div>
    </Card>
  )
}