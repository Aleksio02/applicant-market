import { api } from './client'
import type {
  AssessmentHistoryItem,
  AssessmentResult,
  AssessmentSession,
  StartAssessmentRequest,
  SubmitAnswerRequest,
} from '@/shared/types/assessment'

export const assessmentApi = {
  startSession: (payload: StartAssessmentRequest) =>
    api
      .post<AssessmentSession>('/assessment/sessions', payload)
      .then((r) => r.data),

  getSession: (id: string) =>
    api.get<AssessmentSession>(`/assessment/sessions/${id}`).then((r) => r.data),

  submitAnswer: (sessionId: string, payload: SubmitAnswerRequest) =>
    api
      .post<void>(`/assessment/sessions/${sessionId}/answers`, payload)
      .then((r) => r.data),

  completeSession: (sessionId: string) =>
    api
      .post<AssessmentResult>(`/assessment/sessions/${sessionId}/complete`)
      .then((r) => r.data),

  getHistory: () =>
    api
      .get<AssessmentHistoryItem[]>('/assessment/sessions/history')
      .then((r) => r.data),
}