import { useState } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { interactionApi } from '@/api/interaction'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'
import { Textarea } from '@/shared/ui/Textarea'
import { Modal } from '@/shared/ui/Modal'

interface Props {
  open: boolean
  candidateId: string
  defaultTitle?: string
  onClose: () => void
}

export function InviteCandidateModal({
  open,
  candidateId,
  defaultTitle,
  onClose,
}: Props) {
  const queryClient = useQueryClient()
  const [title, setTitle] = useState(defaultTitle ?? '')
  const [message, setMessage] = useState('')
  const [salaryFrom, setSalaryFrom] = useState('200000')
  const [salaryTo, setSalaryTo] = useState('300000')
  const [error, setError] = useState<string | null>(null)

  const mutation = useMutation({
    mutationFn: () =>
      interactionApi.createInvitation({
        candidateId,
        title: title.trim() || undefined,
        message: message.trim() || undefined,
        salaryFrom: Number(salaryFrom),
        salaryTo: Number(salaryTo),
      }),
    onSuccess: () => {
      setError(null)
      setTitle('')
      setMessage('')
      queryClient.invalidateQueries({ queryKey: ['employer', 'invitations'] })
      onClose()
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось пригласить'),
  })

  const submit = () => {
    if (!salaryFrom || !salaryTo) {
      setError('Укажите зарплатную вилку')
      return
    }
    const from = Number(salaryFrom)
    const to = Number(salaryTo)
    if (!Number.isFinite(from) || !Number.isFinite(to) || from <= 0 || to <= 0) {
      setError('Зарплата должна быть положительным числом')
      return
    }
    if (from > to) {
      setError('Верхняя граница должна быть не меньше нижней')
      return
    }
    mutation.mutate()
  }

  return (
    <Modal open={open} onClose={onClose} title="Пригласить кандидата">
      <div className="space-y-4">
        <Input
          label="Заголовок"
          placeholder="Senior Backend в финтех"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

        <Textarea
          label="Сообщение"
          rows={5}
          placeholder="Есть интересная роль, хотим познакомиться"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
        />

        <div className="grid gap-4 sm:grid-cols-2">
          <Input
            label="Зарплата от, ₽ *"
            type="number"
            min={0}
            value={salaryFrom}
            onChange={(e) => setSalaryFrom(e.target.value)}
          />
          <Input
            label="Зарплата до, ₽ *"
            type="number"
            min={0}
            value={salaryTo}
            onChange={(e) => setSalaryTo(e.target.value)}
          />
        </div>

        <div className="rounded-xl border border-white/10 bg-white/5 p-3 text-xs text-white/60">
          Кандидат увидит приглашение с указанной зарплатой и сможет принять
          или отклонить. Ваши контакты и контакты кандидата раскроются после
          того, как он примет приглашение.
        </div>

        {error && (
          <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
            {error}
          </div>
        )}

        <div className="flex justify-end gap-3 pt-2">
          <Button variant="ghost" onClick={onClose}>
            Отмена
          </Button>
          <Button onClick={submit} loading={mutation.isPending}>
            Отправить приглашение
          </Button>
        </div>
      </div>
    </Modal>
  )
}