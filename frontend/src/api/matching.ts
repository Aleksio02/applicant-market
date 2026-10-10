import { api } from './client'
import type {
  CandidateCard,
  CandidateSearchRequest,
  CandidateSearchResponse,
  CategorySummary,
} from '@/shared/types/matching'

export const matchingApi = {
  search: (payload: CandidateSearchRequest) =>
    api
      .post<CandidateSearchResponse>('/matching/search', payload)
      .then((r) => r.data),

  getCandidate: (applicantId: string) =>
    api
      .get<CandidateCard>(`/matching/candidates/${applicantId}`)
      .then((r) => r.data),

  replayQuery: (queryId: string) =>
    api
      .get<CandidateSearchResponse>(`/matching/queries/${queryId}`)
      .then((r) => r.data),

  getCategorySummary: () =>
    api
      .get<CategorySummary[]>('/matching/categories')
      .then((r) => r.data),
}