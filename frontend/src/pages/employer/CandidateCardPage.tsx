import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { ArrowLeft, Mail, MapPin, Sparkles, User } from 'lucide-react'
import { matchingApi } from '@/api/matching'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { InviteCandidateModal } from '@/features/interaction/InviteCandidateModal'
import {
  formatAssessmentScore,
  formatMatchPercent,
  reasonTone,
} from '@/shared/lib/matchingHelpers'

export default function CandidateCardPage() {
  const { applicantId } = useParams<{ applicantId: string }>()
  const navigate = useNavigate()
  const [inviteOpen, setInviteOpen] = useState(false)

  const { data: candidate, isLoading } = useQuery({
    queryKey: ['employer', 'matching', 'candidate', applicantId],
    queryFn: () => matchingApi.getCandidate(applicantId!),
    enabled: !!applicantId,
  })

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  if (!candidate) {
    return <div className="text-white/60">Кандидат не найден или скрыт</div>
  }

  return (
    <div className="mx-auto max-w-3xl">
      <button
        type="button"
        onClick={() => navigate(-1)}
        className="mb-4 inline-flex items-center gap-1.5 text-sm text-white/60 hover:text-white"
      >
        <ArrowLeft className="h-4 w-4" />
        Назад к поиску
      </button>

      <Card className="p-6">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <h1 className="text-2xl font-bold">
              {candidate.displayName || 'Кандидат'}
            </h1>
            <div className="mt-1 flex flex-wrap gap-1.5">
              {candidate.primaryGradeName && (
                <Badge variant="pink">{candidate.primaryGradeName}</Badge>
              )}
              {candidate.primarySkillName && (
                <Badge variant="lilac">{candidate.primarySkillName}</Badge>
              )}
              {candidate.categoryName && (
                <Badge>{candidate.categoryName}</Badge>
              )}
            </div>
          </div>
          <div className="text-right">
            <div className="text-xs uppercase tracking-wider text-white/50">
              Совпадение
            </div>
            <div className="text-2xl font-bold text-fsp-pink">
              {formatMatchPercent(candidate.matchLevel)}
            </div>
          </div>
        </div>

        <div className="mt-4 flex flex-wrap gap-3 text-sm text-white/70">
          {candidate.city && (
            <span className="inline-flex items-center gap-1">
              <MapPin className="h-3.5 w-3.5" />
              {candidate.city}
            </span>
          )}
          {candidate.experienceYears != null && (
            <span>Опыт: {candidate.experienceYears} лет</span>
          )}
          {candidate.assessmentScore != null && (
            <span>
              Оценка теста:{' '}
              {formatAssessmentScore(candidate.assessmentScore)}
            </span>
          )}
          {candidate.fspAchievementCount > 0 && (
            <span>ФСП: {candidate.fspAchievementCount} достижений</span>
          )}
        </div>

        <div className="mt-6 flex justify-end">
          <Button onClick={() => setInviteOpen(true)}>
            <Mail className="h-4 w-4" />
            Пригласить
          </Button>
        </div>
      </Card>

      {/* Объяснение релевантности */}
      {candidate.reasons.length > 0 && (
        <div className="mt-6">
          <div className="mb-3 flex items-center gap-2">
            <Sparkles className="h-5 w-5 text-fsp-lilac" />
            <h2 className="text-lg font-bold">
              Почему кандидат в выдаче
            </h2>
          </div>
          <Card className="p-5">
            <div className="space-y-2">
              {candidate.reasons.map((r) => (
                <div
                  key={r.code + r.text}
                  className="flex items-center justify-between gap-3 rounded-xl border border-white/10 bg-white/5 px-3.5 py-2.5"
                >
                  <div className="flex items-center gap-2">
                    <Badge variant={reasonTone(r.code)}>{r.code}</Badge>
                    <span className="text-sm text-white/80">{r.text}</span>
                  </div>
                  {r.weight !== 0 && (
                    <span className="text-xs text-white/40">
                      {r.weight > 0 ? '+' : ''}
                      {r.weight.toFixed(2)}
                    </span>
                  )}
                </div>
              ))}
            </div>
          </Card>
        </div>
      )}

      {/* Плейсхолдер про контакты */}
      <div className="mt-6 rounded-xl border border-white/10 bg-white/5 p-4 text-xs text-white/60">
        <b className="text-white/80">Контакты скрыты.</b> Имя, email и телефон
        кандидата раскроются после того, как он примет ваше приглашение.
      </div>

      <InviteCandidateModal
        open={inviteOpen}
        candidateId={candidate.candidateId}
        defaultTitle={
          candidate.categoryName && candidate.primarySkillName
            ? `${candidate.primaryGradeName ?? ''} ${candidate.primarySkillName}`.trim()
            : undefined
        }
        onClose={() => setInviteOpen(false)}
      />
    </div>
  )
}