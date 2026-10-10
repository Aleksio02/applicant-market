import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Award, Clock, FileCheck2 } from 'lucide-react'
import { applicantApi } from '@/api/applicant'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Button } from '@/shared/ui/Button'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import {
  useGrades,
  useSkills,
  findGrade,
  findSkill,
} from '@/shared/hooks/useCatalog'

export default function CategoryPage() {
  const { data: skills = [], isLoading: loadingSkills } = useQuery({
    queryKey: ['applicant', 'skills'],
    queryFn: applicantApi.listSkills,
  })

  const { data: history = [], isLoading: loadingHistory } = useQuery({
    queryKey: ['applicant', 'grade-history'],
    queryFn: applicantApi.listGradeHistory,
  })

  const { data: catalogGrades = [] } = useGrades()
  const { data: catalogSkills = [] } = useSkills()

  if (loadingSkills || loadingHistory) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  const primary = skills.find((s) => s.primary)
  const grade = primary?.verifiedGradeId
    ? findGrade(catalogGrades, primary.verifiedGradeId)
    : null
  const skillItem = primary ? findSkill(catalogSkills, primary.skillId) : null

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Категория</h1>
        <p className="mt-1 text-sm text-white/60">
          Ваша категория определяется основным навыком и подтверждённым грейдом.
        </p>
      </div>

      {/* Current category */}
      <Card className="p-6">
        {primary && grade && skillItem ? (
          <>
            <div className="flex items-center gap-2 text-fsp-pink">
              <Award className="h-5 w-5" />
              <span className="text-sm font-medium uppercase tracking-wider">
                Текущая категория
              </span>
            </div>
            <div className="mt-3 text-2xl font-bold">{skillItem.category}</div>
            <div className="mt-2 flex flex-wrap gap-2">
              <Badge variant="pink">{grade.name}</Badge>
              <Badge variant="lilac">{skillItem.name}</Badge>
            </div>
            {primary.verifiedAt && (
              <p className="mt-3 text-sm text-white/60">
                Грейд подтверждён{' '}
                {new Date(primary.verifiedAt).toLocaleDateString('ru-RU')}
              </p>
            )}
          </>
        ) : (
          <EmptyState
            icon={<Award className="h-8 w-8" />}
            title="Категория не присвоена"
            description="Пройдите тест на грейд, чтобы подтвердить уровень и получить категорию."
          />
        )}

        <div className="mt-6 flex flex-wrap justify-center gap-3">
          <Link to="/applicant/assessment">
            <Button>
              <FileCheck2 className="h-4 w-4" />
              {primary && grade ? 'Пройти тест заново' : 'Пройти тест на грейд'}
            </Button>
          </Link>
        </div>
      </Card>

      {/* Grade history */}
      <div>
        <div className="mb-3 flex items-center gap-2">
          <Clock className="h-5 w-5 text-fsp-lilac" />
          <h2 className="text-lg font-bold">История изменений грейда</h2>
        </div>

        {history.length === 0 ? (
          <EmptyState
            title="Пока нет изменений"
            description="Как только вы пройдёте тестирование, история появится здесь."
          />
        ) : (
          <div className="space-y-2">
            {history.map((h) => {
              const from = h.fromGradeId
                ? findGrade(catalogGrades, h.fromGradeId)
                : null
              const to = findGrade(catalogGrades, h.toGradeId)
              return (
                <Card
                  key={h.id}
                  className="flex flex-wrap items-center justify-between gap-3 p-4"
                >
                  <div className="flex items-center gap-3">
                    <div className="text-sm text-white/60">
                      {from ? from.name : '—'} →{' '}
                      <span className="text-white font-semibold">
                        {to?.name ?? '—'}
                      </span>
                    </div>
                    <Badge variant={h.reason === 'ASSESSMENT' ? 'pink' : 'muted'}>
                      {h.reason}
                    </Badge>
                  </div>
                  <div className="text-xs text-white/40">
                    {new Date(h.changedAt).toLocaleString('ru-RU')}
                  </div>
                </Card>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}