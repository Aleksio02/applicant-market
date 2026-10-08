import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Plus, Star, Trash2 } from 'lucide-react'
import { applicantApi } from '@/api/applicant'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Card } from '@/shared/ui/Card'
import { Badge } from '@/shared/ui/Badge'
import { Spinner } from '@/shared/ui/Spinner'
import { EmptyState } from '@/shared/ui/EmptyState'
import { Modal } from '@/shared/ui/Modal'
import { Input } from '@/shared/ui/Input'
import { Select } from '@/shared/ui/Select'
import { findGrade } from '@/shared/constants/catalog'
import { SKILLS } from '@/shared/constants/skills'
import type { AddSkillRequest, ApplicantSkill } from '@/shared/types/applicant'

export default function SkillsPage() {
  const queryClient = useQueryClient()
  const [addOpen, setAddOpen] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const { data: skills = [], isLoading } = useQuery({
    queryKey: ['applicant', 'skills'],
    queryFn: applicantApi.listSkills,
  })

  const removeMutation = useMutation({
    mutationFn: (skillId: string) => applicantApi.removeSkill(skillId),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'skills'] })
      queryClient.invalidateQueries({ queryKey: ['applicant', 'completeness'] })
    },
    onError: (err) => setError(err instanceof ApiError ? err.message : 'Ошибка'),
  })

  const primaryMutation = useMutation({
    mutationFn: (skillId: string) => applicantApi.setPrimarySkill(skillId),
    onSuccess: () => {
      setError(null)
      queryClient.invalidateQueries({ queryKey: ['applicant', 'skills'] })
      queryClient.invalidateQueries({ queryKey: ['applicant', 'completeness'] })
    },
    onError: (err) => setError(err instanceof ApiError ? err.message : 'Ошибка'),
  })

  const usedSkillIds = new Set(skills.map((s) => s.skillId))
  const availableSkills = SKILLS.filter((s) => !usedSkillIds.has(s.id))

  return (
    <div className="mx-auto max-w-4xl">
      <div className="mb-6 flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">Навыки</h1>
          <p className="mt-1 text-sm text-white/60">
            Добавьте навыки и выберите основной — из него формируется ваша категория.
          </p>
        </div>
        <Button onClick={() => setAddOpen(true)} disabled={availableSkills.length === 0}>
          <Plus className="h-4 w-4" />
          Добавить навык
        </Button>
      </div>

      {error && (
        <div className="mb-4 rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
          {error}
        </div>
      )}

      {isLoading ? (
        <div className="flex h-64 items-center justify-center">
          <Spinner className="h-6 w-6" />
        </div>
      ) : skills.length === 0 ? (
        <EmptyState
          icon={<Star className="h-8 w-8" />}
          title="Пока нет навыков"
          description="Добавьте первый навык — например, Java или React — и укажите уровень владения."
          action={
            <Button onClick={() => setAddOpen(true)}>
              <Plus className="h-4 w-4" />
              Добавить навык
            </Button>
          }
        />
      ) : (
        <div className="space-y-3">
          {skills.map((skill) => (
            <SkillRow
              key={skill.id}
              skill={skill}
              onRemove={() => removeMutation.mutate(skill.skillId)}
              onMakePrimary={() => primaryMutation.mutate(skill.skillId)}
              isRemoving={removeMutation.isPending}
              isPromoting={primaryMutation.isPending}
            />
          ))}
        </div>
      )}

      <AddSkillModal
        open={addOpen}
        onClose={() => setAddOpen(false)}
        availableSkills={availableSkills}
      />
    </div>
  )
}

function SkillRow({
  skill,
  onRemove,
  onMakePrimary,
  isRemoving,
  isPromoting,
}: {
  skill: ApplicantSkill
  onRemove: () => void
  onMakePrimary: () => void
  isRemoving: boolean
  isPromoting: boolean
}) {
  const grade = skill.verifiedGradeId ? findGrade(skill.verifiedGradeId) : null

  return (
    <Card className="flex flex-wrap items-center justify-between gap-3 p-4">
      <div className="min-w-0 flex-1">
        <div className="flex flex-wrap items-center gap-2">
          <span className="text-lg font-semibold">
            {skill.skillName ?? skill.skillCode ?? skill.skillId}
          </span>
          {skill.primary && <Badge variant="pink">Основной</Badge>}
          {grade && <Badge variant="lilac">{grade.name}</Badge>}
          {skill.selfAssessedLevel != null && (
            <Badge variant="muted">Самооценка: {skill.selfAssessedLevel}/5</Badge>
          )}
        </div>
        {skill.yearsExperience != null && (
          <p className="mt-1 text-sm text-white/60">
            Опыт: {skill.yearsExperience} лет
          </p>
        )}
        {skill.verifiedAt && (
          <p className="text-xs text-white/40">
            Грейд подтверждён {new Date(skill.verifiedAt).toLocaleDateString('ru-RU')}
          </p>
        )}
      </div>

      <div className="flex shrink-0 gap-2">
        {!skill.primary && (
          <Button
            variant="ghost"
            size="sm"
            onClick={onMakePrimary}
            loading={isPromoting}
            title="Сделать основным"
          >
            <Star className="h-4 w-4" />
            Сделать основным
          </Button>
        )}
        {!skill.primary && (
          <Button
            variant="ghost"
            size="icon"
            onClick={onRemove}
            loading={isRemoving}
            title="Удалить"
          >
            <Trash2 className="h-4 w-4 text-red-300" />
          </Button>
        )}
      </div>
    </Card>
  )
}

function AddSkillModal({
  open,
  onClose,
  availableSkills,
}: {
  open: boolean
  onClose: () => void
  availableSkills: typeof SKILLS
}) {
  const queryClient = useQueryClient()
  const [skillId, setSkillId] = useState('')
  const [level, setLevel] = useState('3')
  const [years, setYears] = useState('')
  const [error, setError] = useState<string | null>(null)

  const mutation = useMutation({
    mutationFn: (payload: AddSkillRequest) => applicantApi.addSkill(payload),
    onSuccess: () => {
      setError(null)
      setSkillId('')
      setLevel('3')
      setYears('')
      queryClient.invalidateQueries({ queryKey: ['applicant', 'skills'] })
      queryClient.invalidateQueries({ queryKey: ['applicant', 'completeness'] })
      onClose()
    },
    onError: (err) =>
      setError(err instanceof ApiError ? err.message : 'Не удалось добавить'),
  })

  const submit = () => {
    if (!skillId) {
      setError('Выберите навык')
      return
    }
    mutation.mutate({
      skillId,
      selfAssessedLevel: level ? Number(level) : undefined,
      yearsExperience: years ? Number(years) : undefined,
    })
  }

  return (
    <Modal open={open} onClose={onClose} title="Добавить навык">
      <div className="space-y-4">
        <Select
          label="Навык *"
          placeholder="Выберите..."
          value={skillId}
          onChange={(e) => setSkillId(e.target.value)}
          options={availableSkills.map((s) => ({
            value: s.id,
            label: `${s.name} (${s.category})`,
          }))}
        />
        <div className="grid gap-4 sm:grid-cols-2">
          <Select
            label="Самооценка (1–5)"
            value={level}
            onChange={(e) => setLevel(e.target.value)}
            options={[1, 2, 3, 4, 5].map((n) => ({ value: String(n), label: String(n) }))}
          />
          <Input
            label="Опыт (лет)"
            type="number"
            step="0.5"
            min={0}
            placeholder="2"
            value={years}
            onChange={(e) => setYears(e.target.value)}
          />
        </div>

        {error && (
          <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
            {error}
          </div>
        )}

        <div className="flex justify-end gap-3 pt-2">
          <Button variant="ghost" onClick={onClose}>Отмена</Button>
          <Button onClick={submit} loading={mutation.isPending}>Добавить</Button>
        </div>
      </div>
    </Modal>
  )
}