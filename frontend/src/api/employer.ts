import { api, ApiError } from './client'
import type {
  Company,
  CreateCompanyRequest,
  UpdateCompanyRequest,
  HiringNeed,
  CreateHiringNeedRequest,
  UpdateHiringNeedRequest,
  GetHiringNeedListParams,
} from '@/shared/types/employer'

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
      console.warn('[employerApi] 5xx, fallback:', err.message)
      return fallback
    }
    throw err
  }
}

export const employerApi = {
  // ===== Company =====
  getMyCompany: () =>
    api.get<Company>('/employer/company/me').then((r) => r.data),

  createCompany: (payload: CreateCompanyRequest) =>
    api.post<Company>('/employer/company', payload).then((r) => r.data),

  updateCompany: (payload: UpdateCompanyRequest) =>
    api.patch<Company>('/employer/company/me', payload).then((r) => r.data),

  // ===== Hiring Needs =====
  listHiringNeeds: (params: GetHiringNeedListParams = {}) =>
    safe(
      () =>
        api
          .get('/employer/hiring-need', { params })
          .then((r) => asArray<HiringNeed>(r.data)),
      []
    ),

  createHiringNeed: (payload: CreateHiringNeedRequest) =>
    api.post<HiringNeed>('/employer/hiring-need', payload).then((r) => r.data),

  getHiringNeed: (id: string) =>
    api.get<HiringNeed>(`/employer/hiring-need/${id}`).then((r) => r.data),

  updateHiringNeed: (id: string, payload: UpdateHiringNeedRequest) =>
    api
      .patch<HiringNeed>(`/employer/hiring-need/${id}`, payload)
      .then((r) => r.data),

  deactivateHiringNeed: (id: string) =>
    api
      .patch<HiringNeed>(`/employer/hiring-need/${id}/deactivate`)
      .then((r) => r.data),
}