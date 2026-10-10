import { Link, useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { ArrowLeft, Send } from 'lucide-react'
import { vacancyApi } from '@/api/vacancy'
import { Button } from '@/shared/ui/Button'
import { Badge } from '@/shared/ui/Badge'
import { Card } from '@/shared/ui/Card'
import { Spinner } from '@/shared/ui/Spinner'
import { ApplicantAssignmentBlock } from '@/features/assignment/ApplicantAssignmentBlock'
import {
  WORK_FORMAT_LABELS,
  findGrade,
  findSpecialization,
} from '@/shared/constants/catalog'

export default function VacancyDetailPage() {
  const { id } = useParams<{ id: string }>()

  const { data: vacancy, isLoading } = useQuery({
    queryKey: ['applicant', 'vacancy', id],
    queryFn: () => vacancyApi.getById(id!),
    enabled: !!id,
  })

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  if (!vacancy) {
    return <div className="text-white/60">Вакансия не найдена</div>
  }

  const spec = vacancy.specialization ?? findSpecialization(vacancy.specializationId)
  const grade = vacancy.grade ?? findGrade(vacancy.gradeId)

  return (
    <div className="mx-auto max-w-3xl">
      <Link
        to="/applicant/vacancies"
        className="mb-4 inline-flex items-center gap-1.5 text-sm text-white/60 hover:text-white"
      >
        <ArrowLeft className="h-4 w-4" />
        К списку вакансий
      </Link>

      <Card className="p-6">
        <h1 className="text-2xl font-bold">{vacancy.title}</h1>

        <div className="mt-3 flex flex-wrap gap-1.5">
          {spec && <Badge variant="lilac">{spec.name}</Badge>}
          {grade && <Badge variant="pink">{grade.name}</Badge>}
          <Badge>{WORK_FORMAT_LABELS[vacancy.format] ?? vacancy.format}</Badge>
          {vacancy.location && <Badge variant="muted">{vacancy.location}</Badge>}
        </div>

        <div className="mt-4 text-2xl font-bold text-fsp-pink">
          {vacancy.salaryFrom.toLocaleString('ru-RU')} —{' '}
          {vacancy.salaryTo.toLocaleString('ru-RU')} ₽
        </div>

        <div className="mt-6 whitespace-pre-line text-sm text-white/80">
          {vacancy.description}
        </div>

        <div className="mt-6 flex justify-end gap-3">
          <Button
            disabled
            title="Отклики появятся после готовности модуля interaction"
          >
            <Send className="h-4 w-4" />
            Откликнуться (скоро)
          </Button>
        </div>
      </Card>

      {/* Тестовое задание от работодателя */}
      <ApplicantAssignmentBlock vacancyId={vacancy.id} />
    </div>
  )
}