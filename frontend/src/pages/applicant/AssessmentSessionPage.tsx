import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ArrowLeft, CheckCircle2, Clock, Send } from 'lucide-react'
import { assessmentApi } from '@/api/assessment'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Input } from '@/shared/ui/Input'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { ProgressBar } from '@/shared/ui/ProgressBar'
import { parseMcq } from '@/shared/lib/mcqParser'
import type { AssessmentItem } from '@/shared/types/assessment'
import { cn } from '@/shared/lib/cn'

export default function AssessmentSessionPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [error, setError] = useState<string | null>(null)

  const { data: session, isLoading } = useQuery({
    queryKey: ['applicant', 'assessment', 'session', id],
    queryFn: () => assessmentApi.getSession(id!),
    enabled: !!id,
  })

  const submitMutation = useMutation({
    mutationFn: ({
      itemId,
      answer,
    }: {
      itemId: string
      answer: Record<string, unknown>
    }) => assessmentApi.submitAnswer(id!, { itemId, answer }),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({
        queryKey: ['applicant', 'assessment', 'session', id],
      })
    },
    onError: (err) =>
      setError(
        err instanceof ApiError ? err.message : 'Не удалось отправить ответ'
      ),
  })

  const completeMutation = useMutation({
    mutationFn: () => assessmentApi.completeSession(id!),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'assessment'] })
      navigate(`/applicant/assessment/result/${id}`, { replace: true })
    },
    onError: (err) =>
      setError(
        err instanceof ApiError ? err.message : 'Не удалось завершить тест'
      ),
  })

  const items = session?.items ?? []
  const nextUnanswered = useMemo(() => items.find((it) => !it.answered), [items])
  const answeredCount = items.filter((it) => it.answered).length
  const allAnswered = items.length > 0 && answeredCount === items.length

  useEffect(() => {
    if (!session) return
    if (session.status === 'COMPLETED' || session.status === 'FAILED') {
      navigate(`/applicant/assessment/result/${session.id}`, { replace: true })
    }
  }, [session, navigate])

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  if (!session) {
    return <div className="text-white/60">Сессия не найдена</div>
  }

  if (session.status === 'EXPIRED' || session.status === 'CANCELLED') {
    return (
      <Card className="mx-auto max-w-2xl p-6 text-center">
        <div className="text-lg font-semibold">Сессия больше не активна</div>
        <p className="mt-2 text-sm text-white/60">
          Статус: {session.status}. Начните новую с /applicant/assessment.
        </p>
        <Button className="mt-4" onClick={() => navigate('/applicant/assessment')}>
          К тестам
        </Button>
      </Card>
    )
  }

  return (
    <div className="mx-auto max-w-3xl">
      <button
        type="button"
        onClick={() => navigate('/applicant/assessment')}
        className="mb-4 inline-flex items-center gap-1.5 text-sm text-white/60 hover:text-white"
      >
        <ArrowLeft className="h-4 w-4" />
        К списку тестов
      </button>

      <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <div className="text-2xl font-bold">
            {answeredCount} / {items.length}
          </div>
          <div className="text-sm text-white/60">заданий отвечено</div>
        </div>
        <ExpiresCounter expiresAt={session.expiresAt} />
      </div>

      <ProgressBar
        value={items.length > 0 ? (answeredCount / items.length) * 100 : 0}
      />

      {error && (
        <div className="mt-4 rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {error}
        </div>
      )}

      <div className="mt-6">
        {nextUnanswered ? (
          <ItemCard
            key={nextUnanswered.id}
            item={nextUnanswered}
            onSubmit={(answer) =>
              submitMutation.mutate({ itemId: nextUnanswered.id, answer })
            }
            isSubmitting={submitMutation.isPending}
          />
        ) : allAnswered ? (
          <Card className="p-6 text-center">
            <CheckCircle2 className="mx-auto h-10 w-10 text-emerald-400" />
            <div className="mt-3 text-lg font-semibold">
              Все задания отвечены
            </div>
            <p className="mt-1 text-sm text-white/60">
              Нажмите «Завершить», чтобы получить результат.
            </p>
            <Button
              className="mt-4"
              onClick={() => completeMutation.mutate()}
              loading={completeMutation.isPending}
            >
              Завершить тест
            </Button>
          </Card>
        ) : (
          <Card className="p-6 text-center text-white/60">
            Все задания закрыты. Ожидание...
          </Card>
        )}
      </div>
    </div>
  )
}

function ItemCard({
  item,
  onSubmit,
  isSubmitting,
}: {
  item: AssessmentItem
  onSubmit: (answer: Record<string, unknown>) => void
  isSubmitting: boolean
}) {
  return (
    <Card className="p-6">
      <div className="mb-3 flex flex-wrap items-center gap-2">
        <Badge variant="lilac">Задание {item.position}</Badge>
        <Badge variant="muted">{item.topic}</Badge>
        <Badge variant="muted">Сложность {item.difficulty}/5</Badge>
        <Badge variant="pink">{item.points} балл(ов)</Badge>
      </div>

      {item.type === 'MCQ' && (
        <McqItem item={item} onSubmit={onSubmit} isSubmitting={isSubmitting} />
      )}
      {item.type === 'OUTPUT_PREDICT' && (
        <ValueItem
          item={item}
          label="Что выведет код?"
          placeholder="Например, 42"
          onSubmit={onSubmit}
          isSubmitting={isSubmitting}
        />
      )}
      {item.type === 'BUG_FIND' && (
        <ValueItem
          item={item}
          label="В какой строке ошибка? Введите номер строки."
          placeholder="Например, 3"
          onSubmit={onSubmit}
          isSubmitting={isSubmitting}
        />
      )}
    </Card>
  )
}

function McqItem({
  item,
  onSubmit,
  isSubmitting,
}: {
  item: AssessmentItem
  onSubmit: (answer: Record<string, unknown>) => void
  isSubmitting: boolean
}) {
  const { question, options } = parseMcq(item.body)
  const [selected, setSelected] = useState<string>('')

  return (
    <>
      <pre className="mb-4 whitespace-pre-wrap rounded-xl bg-black/30 p-4 text-sm text-white/90">
        {question}
      </pre>

      <div className="space-y-2">
        {options.map((opt) => (
          <button
            key={opt.id}
            type="button"
            onClick={() => setSelected(opt.id)}
            className={cn(
              'flex w-full items-start gap-3 rounded-xl border px-4 py-3 text-left transition-colors',
              selected === opt.id
                ? 'border-fsp-pink bg-fsp-pink/10'
                : 'border-white/15 bg-white/5 hover:bg-white/10'
            )}
          >
            <span className="shrink-0 font-bold">{opt.id})</span>
            <span className="text-sm">{opt.value}</span>
          </button>
        ))}
      </div>

      <div className="mt-4 flex justify-end">
        <Button
          disabled={!selected}
          onClick={() => onSubmit({ selected })}
          loading={isSubmitting}
        >
          <Send className="h-4 w-4" />
          Ответить
        </Button>
      </div>
    </>
  )
}

function ValueItem({
  item,
  label,
  placeholder,
  onSubmit,
  isSubmitting,
}: {
  item: AssessmentItem
  label: string
  placeholder: string
  onSubmit: (answer: Record<string, unknown>) => void
  isSubmitting: boolean
}) {
  const [value, setValue] = useState('')

  return (
    <>
      <pre className="mb-4 whitespace-pre-wrap rounded-xl bg-black/30 p-4 text-sm text-white/90">
        {item.body}
      </pre>

      <Input
        label={label}
        placeholder={placeholder}
        value={value}
        onChange={(e) => setValue(e.target.value)}
      />

      <div className="mt-4 flex justify-end">
        <Button
          disabled={!value}
          onClick={() => onSubmit({ value })}
          loading={isSubmitting}
        >
          <Send className="h-4 w-4" />
          Ответить
        </Button>
      </div>
    </>
  )
}

function ExpiresCounter({ expiresAt }: { expiresAt: string }) {
  const [now, setNow] = useState(Date.now())

  useEffect(() => {
    const t = setInterval(() => setNow(Date.now()), 1000)
    return () => clearInterval(t)
  }, [])

  const left = Math.max(
    0,
    Math.floor((new Date(expiresAt).getTime() - now) / 1000)
  )
  const minutes = Math.floor(left / 60)
  const seconds = left % 60

  return (
    <div className="inline-flex items-center gap-1.5 rounded-xl border border-white/15 bg-white/5 px-3 py-1.5 text-sm text-white/70">
      <Clock className="h-4 w-4" />
      {String(minutes).padStart(2, '0')}:{String(seconds).padStart(2, '0')}
    </div>
  )
}