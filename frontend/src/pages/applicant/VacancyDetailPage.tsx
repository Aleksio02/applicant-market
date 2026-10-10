import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ArrowLeft, Check, Send } from 'lucide-react'
import { vacancyApi } from '@/api/vacancy'
import { interactionApi } from '@/api/interaction'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Badge } from '@/shared/ui/Badge'
import { Card } from '@/shared/ui/Card'
import { Spinner } from '@/shared/ui/Spinner'
import { Modal } from '@/shared/ui/Modal'
import { Textarea } from '@/shared/ui/Textarea'
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

        <ApplySection vacancyId={vacancy.id} />
      </Card>

      {/* Тестовое задание от работодателя */}
      <ApplicantAssignmentBlock vacancyId={vacancy.id} />
    </div>
  )
}

function ApplySection({ vacancyId }: { vacancyId: string }) {
  const queryClient = useQueryClient()
  const [open, setOpen] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState<string | null>(null)

  // Проверяем, есть ли уже активный отклик на эту вакансию
  const { data: myApplications = [], isLoading } = useQuery({
    queryKey: ['applicant', 'applications', 'mine'],
    queryFn: interactionApi.listMyApplications,
  })

  const existingApplication = myApplications.find(
    (a) =>
      a.vacancyId === vacancyId &&
      (a.status === 'SENT' ||
        a.status === 'VIEWED' ||
        a.status === 'ACCEPTED')
  )
  const applied = !!existingApplication

  const mutation = useMutation({
    mutationFn: () =>
      interactionApi.createApplication(vacancyId, {
        message: message.trim() || undefined,
      }),
    onSuccess: () => {
      setError(null)
      setOpen(false)
      setMessage('')
      queryClient.invalidateQueries({
        queryKey: ['applicant', 'applications'],
      })
    },
    onError: (err) => {
      // 400 = отклик уже есть — обновляем список, кнопка сменится сама
      if (err instanceof ApiError && err.status === 400) {
        setOpen(false)
        setError(null)
        queryClient.invalidateQueries({
          queryKey: ['applicant', 'applications'],
        })
        return
      }
      setError(err instanceof ApiError ? err.message : 'Не удалось откликнуться')
    },
  })

  // Пока грузим список откликов — показываем плейсхолдер
  if (isLoading) {
    return (
      <div className="mt-6 flex justify-end">
        <div className="inline-flex items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-3.5 py-2 text-sm text-white/40">
          Загрузка…
        </div>
      </div>
    )
  }

  return (
    <>
      <div className="mt-6 flex justify-end gap-3">
        {applied ? (
          <div className="inline-flex items-center gap-2 rounded-xl border border-emerald-400/30 bg-emerald-500/10 px-3.5 py-2 text-sm text-emerald-200">
            <Check className="h-4 w-4" />
            {existingApplication?.status === 'ACCEPTED'
              ? 'Отклик принят работодателем'
              : 'Отклик отправлен'}
          </div>
        ) : (
          <Button onClick={() => setOpen(true)}>
            <Send className="h-4 w-4" />
            Откликнуться
          </Button>
        )}
      </div>

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        title="Отклик на вакансию"
      >
        <div className="space-y-4">
          <Textarea
            label="Сообщение (опционально)"
            rows={5}
            placeholder="Почему вам интересна эта роль, что можете добавить"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
          />

          <div className="rounded-xl border border-white/10 bg-white/5 p-3 text-xs text-white/60">
            При отклике работодатель сразу увидит ваши контакты и сможет
            связаться.
          </div>

          {error && (
            <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
              {error}
            </div>
          )}

          <div className="flex justify-end gap-3 pt-2">
            <Button variant="ghost" onClick={() => setOpen(false)}>
              Отмена
            </Button>
            <Button
              onClick={() => mutation.mutate()}
              loading={mutation.isPending}
            >
              Отправить отклик
            </Button>
          </div>
        </div>
      </Modal>
    </>
  )
}