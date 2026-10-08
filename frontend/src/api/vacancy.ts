import { api, ApiError } from './client'
import type {
  Vacancy,
  CreateVacancyRequest,
  UpdateVacancyRequest,
  GetVacancyListParams,
  VacancyRequirement,
  CreateVacancyRequirementRequest,
  UpdateVacancyRequirementRequest,
} from '@/shared/types/vacancy'

// Извлекает массив из ответа: если Spring вернул Page<> — берём .content
function asArray<T>(data: unknown): T[] {
  if (Array.isArray(data)) return data as T[]
  if (data && typeof data === 'object' && 'content' in data) {
    const c = (data as { content: unknown }).content
    if (Array.isArray(c)) return c as T[]
  }
  return []
}

async function safe<T>(fn: () => Promise<T>, fallback: T): Promise<T> {
  try {
    return await fn()
  } catch (err) {
    if (err instanceof ApiError && err.status >= 500) {
      console.warn('[vacancyApi] 5xx, fallback:', err.message)
      return fallback
    }
    throw err
  }
}

export const vacancyApi = {
  create: (payload: CreateVacancyRequest) =>
    api.post<Vacancy>('/vacancy', payload).then((r) => r.data),

  listPublished: (params: GetVacancyListParams = {}) =>
    safe(
      () => api.get('/vacancy', { params }).then((r) => asArray<Vacancy>(r.data)),
      []
    ),

  listMine: (params: GetVacancyListParams = {}) =>
    safe(
      () => api.get('/vacancy/mine', { params }).then((r) => asArray<Vacancy>(r.data)),
      []
    ),

  getById: (id: string) =>
    api.get<Vacancy>(`/vacancy/${id}`).then((r) => r.data),

  update: (id: string, payload: UpdateVacancyRequest) =>
    api.patch<Vacancy>(`/vacancy/${id}`, payload).then((r) => r.data),

  publish: (id: string) =>
    api.patch<Vacancy>(`/vacancy/${id}/publish`).then((r) => r.data),

  close: (id: string) =>
    api.patch<Vacancy>(`/vacancy/${id}/close`).then((r) => r.data),

  listRequirements: (vacancyId: string) =>
    safe(
      () =>
        api
          .get(`/vacancy/requirement/vacancy/${vacancyId}`)
          .then((r) => asArray<VacancyRequirement>(r.data)),
      []
    ),

  addRequirement: (vacancyId: string, payload: CreateVacancyRequirementRequest) =>
    api
      .post<VacancyRequirement>(`/vacancy/requirement/vacancy/${vacancyId}`, payload)
      .then((r) => r.data),

  updateRequirement: (
    vacancyId: string,
    requirementId: string,
    payload: UpdateVacancyRequirementRequest
  ) =>
    api
      .patch<VacancyRequirement>(
        `/vacancy/requirement/${requirementId}/vacancy/${vacancyId}`,
        payload
      )
      .then((r) => r.data),

  deleteRequirement: (vacancyId: string, requirementId: string) =>
    api
      .delete(`/vacancy/requirement/${requirementId}/vacancy/${vacancyId}`)
      .then((r) => r.data),
}