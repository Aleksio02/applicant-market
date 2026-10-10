import type { UUID } from './api'

export type AssessmentItemType = 'MCQ' | 'OUTPUT_PREDICT' | 'BUG_FIND'

export type AssessmentSessionStatus =
  | 'SURVEY'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'FAILED'
  | 'EXPIRED'
  | 'CANCELLED'

export interface AssessmentItem {
  id: UUID
  position: number
  type: AssessmentItemType
  topic: string
  difficulty: number
  body: string
  points: number
  answered: boolean
}

export interface AssessmentSession {
  id: UUID
  skillId: UUID
  claimedGradeId: UUID
  status: AssessmentSessionStatus
  resultGradeId?: UUID | null
  score?: number | null
  startedAt: string
  expiresAt: string
  completedAt?: string | null
  items: AssessmentItem[]
}

export interface StartAssessmentRequest {
  skillId: UUID
  claimedGradeId: UUID
}

export interface SubmitAnswerRequest {
  itemId: UUID
  answer: Record<string, unknown>
}

export interface AssessmentResult {
  sessionId: UUID
  status: AssessmentSessionStatus
  score: number
  resultGradeId?: UUID | null
}

export interface AssessmentHistoryItem {
  sessionId: UUID
  skillId: UUID
  claimedGradeId: UUID
  resultGradeId?: UUID | null
  status: AssessmentSessionStatus
  score?: number | null
  startedAt: string
  completedAt?: string | null
}