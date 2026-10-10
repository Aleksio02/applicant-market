import { api } from './client'
import type {
  GradeItem,
  SkillItem,
  SpecializationItem,
} from '@/shared/types/catalog'

export const catalogApi = {
  listSpecializations: () =>
    api.get<SpecializationItem[]>('/catalog/specializations').then((r) => r.data),

  listGrades: () =>
    api.get<GradeItem[]>('/catalog/grades').then((r) => r.data),

  listSkills: () =>
    api.get<SkillItem[]>('/catalog/skills').then((r) => r.data),
}