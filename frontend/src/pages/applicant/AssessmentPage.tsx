import { useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Award, History, PlayCircle, Sparkles } from 'lucide-react'
import { assessmentApi } from '@/api/assessment'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Select } from '@/shared/ui/Select'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import {
  findGrade,
  findSkill,
  useGrades,
  useSkills,
} from '@/shared/hooks/useCatalog'
import type { AssessmentHistoryItem } from '@/shared/types/assessment'

export default function AssessmentPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [skillId, setSkillId] = useState('')
  const [gradeId, setGradeId] = useState('')
  const [error, setError] = useState<string | null>(null)

  const { data: skills = [] } = useSkills()
  const { data: grades = [] } = useGrades()

  const { data: history = [], isLoading } = useQuery({
    queryKey: ['applicant', 'assessment', 'history'],
    queryFn: assessmentApi.getHistory,
  })

  const activeSession = useMemo(
    () =>
      history.find(
        (h) => h.status === 'IN_PROGRESS' || h.status === 'SURVEY'
      ),
    [history]
  )

  const startMutation = useMutation({
    mutationFn: () =>
      assessmentApi.startSession({ skillId, claimedGradeId: gradeId }),
    onSuccess: (session) => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'assessment'] })
      navigate(`/applicant/assessment/session/${session.id}`)
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось начать тест'),
  })

  const submit = () => {
    if (!skillId) {
      setError('Выберите навык')
      return
    }
    if (!gradeId) {
      setError('Выберите грейд')
      return
    }
    startMutation.mutate()
  }

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Тестирование на грейд</h1>
        <p className="mt-1 text-sm text-white/60">
          Пройдите тест, чтобы подтвердить свой уровень. По результату система
          присвоит вам грейд — он виден работодателям.
        </p>
      </div>

      {activeSession && (
        <Card className="border-fsp-pink/40 bg-fsp-pink/5 p-5">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div>
              <div className="text-sm font-medium text-fsp-pink">
                У вас есть незавершённый тест
              </div>
              <div className="mt-1 text-lg font-semibold">
                {findSkill(skills, activeSession.skillId)?.name ?? '—'} ·{' '}
                {findGrade(grades, activeSession.claimedGradeId)?.name ?? '—'}
              </div>
            </div>
            <Button
              onClick={() =>
                navigate(
                  `/applicant/assessment/session/${activeSession.sessionId}`
                )
              }
            >
              <PlayCircle className="h-4 w-4" />
              Продолжить
            </Button>
          </div>
        </Card>
      )}

      {!activeSession && (
        <Card className="p-6">
          <div className="mb-4 flex items-center gap-2 text-fsp-pink">
            <Sparkles className="h-5 w-5" />
            <span className="text-sm font-medium uppercase tracking-wider">
              Новый тест
            </span>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <Select
              label="Навык *"
              placeholder="Выберите..."
              value={skillId}
              onChange={(e) => setSkillId(e.target.value)}
              options={skills.map((s) => ({
                value: s.id,
                label: `${s.name} (${s.category})`,
              }))}
            />
            <Select
              label="Заявляемый грейд *"
              placeholder="Выберите..."
              value={gradeId}
              onChange={(e) => setGradeId(e.target.value)}
              options={grades.map((g) => ({
                value: g.id,
                label: g.name,
              }))}
            />
          </div>

          <div className="mt-4 rounded-xl border border-white/10 bg-white/5 p-3 text-xs text-white/60">
            <b>Как это работает:</b> система подберёт задания под ваш заявленный
            грейд. Если справитесь уверенно — грейд повысится на один уровень. Если
            не наберёте порог — тест будет считаться проваленным, грейд останется
            прежним. Повторная попытка — через некоторое время.
          </div>

          {error && (
            <div className="mt-4 rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
              {error}
            </div>
          )}

          <div className="mt-4 flex justify-end">
            <Button onClick={submit} loading={startMutation.isPending}>
              <PlayCircle className="h-4 w-4" />
              Начать тест
            </Button>
          </div>
        </Card>
      )}

      <div>
        <div className="mb-3 flex items-center gap-2">
          <History className="h-5 w-5 text-fsp-lilac" />
          <h2 className="text-lg font-bold">История тестирований</h2>
        </div>

        {isLoading ? (
          <div className="flex h-32 items-center justify-center">
            <Spinner className="h-5 w-5" />
          </div>
        ) : history.length === 0 ? (
          <EmptyState
            icon={<Award className="h-8 w-8" />}
            title="Пока нет попыток"
            description="Пройдите первый тест, чтобы получить грейд."
          />
        ) : (
          <div className="space-y-2">
            {history.map((h) => (
              <HistoryRow key={h.sessionId} item={h} />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

function HistoryRow({ item }: { item: AssessmentHistoryItem }) {
  const { data: skills = [] } = useSkills()
  const { data: grades = [] } = useGrades()

  const skill = findSkill(skills, item.skillId)
  const claimed = findGrade(grades, item.claimedGradeId)
  const result = findGrade(grades, item.resultGradeId)

  return (
    <Card className="flex flex-wrap items-center justify-between gap-3 p-4">
      <div className="min-w-0 flex-1">
        <div className="flex flex-wrap items-center gap-2">
          <span className="text-base font-semibold">{skill?.name ?? '—'}</span>
          <StatusBadge status={item.status} />
        </div>
        <div className="mt-1 text-sm text-white/60">
          Заявлено: {claimed?.name ?? '—'}
          {result && item.status === 'COMPLETED' && (
            <>
              {' → '}
              <span className="font-semibold text-fsp-pink">
                {result.name}
              </span>
            </>
          )}
        </div>
        <div className="mt-1 text-xs text-white/40">
          {new Date(item.startedAt).toLocaleString('ru-RU')}
          {item.score != null && ` · ${Math.round(item.score * 100)}%`}
        </div>
      </div>
    </Card>
  )
}

function StatusBadge({ status }: { status: string }) {
  switch (status) {
    case 'COMPLETED':
      return <Badge variant="success">Пройден</Badge>
    case 'FAILED':
      return <Badge variant="danger">Провален</Badge>
    case 'IN_PROGRESS':
    case 'SURVEY':
      return <Badge variant="warning">В процессе</Badge>
    case 'EXPIRED':
      return <Badge variant="muted">Истёк</Badge>
    case 'CANCELLED':
      return <Badge variant="muted">Отменён</Badge>
    default:
      return <Badge variant="muted">{status}</Badge>
  }
}