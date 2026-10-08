import { api } from './client'
import type {
  Company,
  CreateCompanyRequest,
  UpdateCompanyRequest,
  HiringNeed,
  CreateHiringNeedRequest,
  UpdateHiringNeedRequest,
  GetHiringNeedListParams,
} from '@/shared/types/employer'

export const employerApi = {
  getMyCompany: () =>
    api.get<Company>('/employer/company/me').then((r) => r.data),

  createCompany: (payload: CreateCompanyRequest) =>
    api.post<Company>('/employer/company', payload).then((r) => r.data),

  updateCompany: (payload: UpdateCompanyRequest) =>
    api.patch<Company>('/employer/company/me', payload).then((r) => r.data),

  listHiringNeeds: (params: GetHiringNeedListParams = {}) =>
    api
      .get<HiringNeed[]>('/employer/hiring-need', { params })
      .then((r) => r.data),

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