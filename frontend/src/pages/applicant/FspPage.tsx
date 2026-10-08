import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Award, Link2, Unlink } from 'lucide-react'
import { applicantApi } from '@/api/applicant'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Input } from '@/shared/ui/Input'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'

export default function FspPage() {
  const queryClient = useQueryClient()
  const [fspId, setFspId] = useState('')
  const [error, setError] = useState<string | null>(null)

  const { data: profile, isLoading } = useQuery({
    queryKey: ['applicant', 'profile'],
    queryFn: applicantApi.getProfile,
  })

  const { data: achievements = [] } = useQuery({
    queryKey: ['applicant', 'fsp', 'achievements'],
    queryFn: applicantApi.listFspAchievements,
    enabled: !!profile?.fspId,
  })

  const linkMutation = useMutation({
    mutationFn: () => applicantApi.linkFsp(fspId),
    onSuccess: () => {
      setError(null)
      setFspId('')
      queryClient.invalidateQueries({ queryKey: ['applicant', 'profile'] })
      queryClient.invalidateQueries({ queryKey: ['applicant', 'fsp'] })
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось привязать'),
  })

  const unlinkMutation = useMutation({
    mutationFn: () => applicantApi.unlinkFsp(),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'profile'] })
      queryClient.invalidateQueries({ queryKey: ['applicant', 'fsp'] })
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось отвязать'),
  })

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  const linked = !!profile?.fspId

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold">ФСП ID</h1>
        <p className="mt-1 text-sm text-white/60">
          Привяжите ID участника Федерации спортивного программирования — достижения
          на соревнованиях будут подтверждать ваши навыки.
        </p>
      </div>

      {error && (
        <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {error}
        </div>
      )}

      <Card className="p-6">
        {linked ? (
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div>
              <div className="text-sm text-white/60">Привязанный ФСП ID</div>
              <div className="mt-1 text-lg font-mono font-semibold">
                {profile?.fspId}
              </div>
              {profile?.fspLinkedAt && (
                <div className="mt-1 text-xs text-white/40">
                  Привязан {new Date(profile.fspLinkedAt).toLocaleDateString('ru-RU')}
                </div>
              )}
            </div>
            <Button
              variant="danger"
              onClick={() => unlinkMutation.mutate()}
              loading={unlinkMutation.isPending}
            >
              <Unlink className="h-4 w-4" />
              Отвязать
            </Button>
          </div>
        ) : (
          <div className="space-y-3">
            <div className="text-sm text-white/60">
              У вас ещё нет привязанного ФСП ID.
            </div>
            <div className="flex gap-3">
              <Input
                label="ФСП ID"
                placeholder="например, FSP-12345"
                value={fspId}
                onChange={(e) => setFspId(e.target.value)}
              />
              <Button
                onClick={() => linkMutation.mutate()}
                loading={linkMutation.isPending}
                disabled={!fspId}
                className="self-end"
              >
                <Link2 className="h-4 w-4" />
                Привязать
              </Button>
            </div>
          </div>
        )}
      </Card>

      {linked && (
        <div>
          <h2 className="mb-3 text-lg font-bold">Достижения</h2>
          {achievements.length === 0 ? (
            <EmptyState
              icon={<Award className="h-8 w-8" />}
              title="Пока нет достижений"
              description="Как только вы поучаствуете в соревнованиях ФСП, они появятся здесь."
            />
          ) : (
            <div className="space-y-3">
              {achievements.map((a) => (
                <Card key={a.id} className="flex flex-wrap items-center justify-between gap-3 p-4">
                  <div className="min-w-0 flex-1">
                    <div className="font-semibold">{a.eventName}</div>
                    <div className="mt-1 text-sm text-white/60">
                      {a.category ?? 'Без категории'}
                      {a.eventDate && ` · ${new Date(a.eventDate).toLocaleDateString('ru-RU')}`}
                    </div>
                  </div>
                  <div className="flex shrink-0 gap-2">
                    {a.place && <Badge variant="pink">{a.place} место</Badge>}
                    {a.verified && <Badge variant="success">Подтверждено</Badge>}
                  </div>
                </Card>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  )
}