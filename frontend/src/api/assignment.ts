import { api } from './client'
import type {
  AssignmentAttempt,
  CreateAssignmentRequest,
  EvaluateAttemptRequest,
  SubmitAttemptRequest,
  UpdateAssignmentRequest,
  VacancyAssignment,
} from '@/shared/types/assignment'

export const assignmentApi = {
  // ===== Assignment (employer) =====

  createForVacancy: (vacancyId: string, payload: CreateAssignmentRequest) =>
    api
      .post<VacancyAssignment>(`/assignment/vacancy/${vacancyId}`, payload)
      .then((r) => r.data),

  getByVacancy: (vacancyId: string) =>
    api
      .get<VacancyAssignment>(`/assignment/vacancy/${vacancyId}`)
      .then((r) => r.data),

  updateForVacancy: (vacancyId: string, payload: UpdateAssignmentRequest) =>
    api
      .patch<VacancyAssignment>(`/assignment/vacancy/${vacancyId}`, payload)
      .then((r) => r.data),

  deleteForVacancy: (vacancyId: string) =>
    api.delete(`/assignment/vacancy/${vacancyId}`).then((r) => r.data),

  // ===== Attempts =====

  startAttempt: (assignmentId: string) =>
    api
      .post<AssignmentAttempt>(`/assignment/attempt/${assignmentId}/start`)
      .then((r) => r.data),

  submitAttempt: (attemptId: string, payload: SubmitAttemptRequest) =>
    api
      .patch<AssignmentAttempt>(
        `/assignment/attempt/${attemptId}/submit`,
        payload
      )
      .then((r) => r.data),

  evaluateAttempt: (attemptId: string, payload: EvaluateAttemptRequest) =>
    api
      .patch<AssignmentAttempt>(
        `/assignment/attempt/${attemptId}/evaluate`,
        payload
      )
      .then((r) => r.data),

  getAttempt: (attemptId: string) =>
    api
      .get<AssignmentAttempt>(`/assignment/attempt/${attemptId}`)
      .then((r) => r.data),

  getMyAttempts: () =>
    api
      .get<AssignmentAttempt[]>('/assignment/attempt/mine')
      .then((r) => r.data),

  getAttemptsByAssignment: (assignmentId: string) =>
    api
      .get<AssignmentAttempt[]>(
        `/assignment/attempt/by-assignment/${assignmentId}`
      )
      .then((r) => r.data),
}