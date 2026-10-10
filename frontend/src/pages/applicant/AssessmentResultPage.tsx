import { useNavigate, useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { ArrowLeft, Award, RefreshCw, XCircle } from 'lucide-react'
import { assessmentApi } from '@/api/assessment'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { findGrade, useGrades } from '@/shared/hooks/useCatalog'

export default function AssessmentResultPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { data: grades = [] } = useGrades()

  const { data: session, isLoading } = useQuery({
    queryKey: ['applicant', 'assessment', 'session', id],
    queryFn: () => assessmentApi.getSession(id!),
    enabled: !!id,
  })

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  if (!session) {
    return <div className="text-white/60">Результат не найден</div>
  }

  const completed = session.status === 'COMPLETED'
  const failed = session.status === 'FAILED'
  const score = session.score != null ? Math.round(session.score * 100) : 0
  const resultGrade = findGrade(grades, session.resultGradeId)
  const claimedGrade = findGrade(grades, session.claimedGradeId)

  return (
    <div className="mx-auto max-w-2xl">
      <button
        type="button"
        onClick={() => navigate('/applicant/assessment')}
        className="mb-4 inline-flex items-center gap-1.5 text-sm text-white/60 hover:text-white"
      >
        <ArrowLeft className="h-4 w-4" />
        К тестам
      </button>

      <Card className="p-8 text-center">
        {completed && (
          <>
            <Award className="mx-auto h-14 w-14 text-fsp-pink" />
            <div className="mt-4 text-2xl font-bold">Тест пройден</div>
            <div className="mt-2 text-4xl font-black text-fsp-pink">
              {score}%
            </div>
            <div className="mt-4">
              <div className="text-sm text-white/60">Присвоенный грейд</div>
              <div className="mt-1 text-xl font-semibold">
                {resultGrade?.name ?? '—'}
              </div>
            </div>
            {resultGrade &&
              claimedGrade &&
              resultGrade.id !== claimedGrade.id && (
                <div className="mt-3">
                  <Badge variant="success">Повышение грейда!</Badge>
                </div>
              )}
          </>
        )}

        {failed && (
          <>
            <XCircle className="mx-auto h-14 w-14 text-red-400" />
            <div className="mt-4 text-2xl font-bold">Тест не пройден</div>
            <div className="mt-2 text-4xl font-black text-white/60">
              {score}%
            </div>
            <p className="mt-4 text-sm text-white/60">
              Пороговое значение для прохождения — 50%. Грейд остался прежним.
            </p>
          </>
        )}

        {!completed && !failed && (
          <div className="text-lg font-semibold">
            Тест ещё не завершён. Статус: {session.status}
          </div>
        )}

        <div className="mt-8 flex flex-wrap justify-center gap-3">
          <Button
            variant="secondary"
            onClick={() => navigate('/applicant/category')}
          >
            Моя категория
          </Button>
          <Button onClick={() => navigate('/applicant/assessment')}>
            <RefreshCw className="h-4 w-4" />
            К тестам
          </Button>
        </div>
      </Card>
    </div>
  )
}