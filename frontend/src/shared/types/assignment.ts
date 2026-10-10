import type { UUID } from './api'

export type AssignmentAttemptStatus =
  | 'STARTED'
  | 'SUBMITTED'
  | 'EVALUATED'
  | 'EXPIRED'

export type EvaluationVerdict = 'PASS' | 'FAIL'

export interface VacancyAssignment {
  id: UUID
  vacancyId: UUID
  title: string
  description: string
  durationHours: number
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface CreateAssignmentRequest {
  title: string
  description: string
  durationHours: number
}

export interface UpdateAssignmentRequest {
  title?: string
  description?: string
  durationHours?: number
  active?: boolean
}

export interface AssignmentAttempt {
  id: UUID
  assignmentId: UUID
  candidateId: UUID
  status: AssignmentAttemptStatus
  startedAt: string
  deadlineAt: string
  submittedAt?: string | null
  contentText?: string | null
  evaluatedAt?: string | null
  score?: number | null
  verdict?: EvaluationVerdict | null
  feedback?: string | null
  createdAt: string
  updatedAt: string
}

export interface SubmitAttemptRequest {
  contentText: string
}

export interface EvaluateAttemptRequest {
  verdict: EvaluationVerdict
  score?: number
  feedback?: string
}