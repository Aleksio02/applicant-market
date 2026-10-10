import { useQuery } from '@tanstack/react-query'
import { catalogApi } from '@/api/catalog'
import type {
  GradeItem,
  SkillItem,
  SpecializationItem,
} from '@/shared/types/catalog'

const ONE_HOUR = 60 * 60 * 1000

export function useSpecializations() {
  return useQuery<SpecializationItem[]>({
    queryKey: ['catalog', 'specializations'],
    queryFn: catalogApi.listSpecializations,
    staleTime: ONE_HOUR,
  })
}

export function useGrades() {
  return useQuery<GradeItem[]>({
    queryKey: ['catalog', 'grades'],
    queryFn: catalogApi.listGrades,
    staleTime: ONE_HOUR,
  })
}

export function useSkills() {
  return useQuery<SkillItem[]>({
    queryKey: ['catalog', 'skills'],
    queryFn: catalogApi.listSkills,
    staleTime: ONE_HOUR,
  })
}

export function findSpecialization(
  list: SpecializationItem[],
  id?: string | null
) {
  if (!id) return null
  return list.find((s) => s.id === id) ?? null
}

export function findGrade(list: GradeItem[], id?: string | null) {
  if (!id) return null
  return list.find((g) => g.id === id) ?? null
}

export function findSkill(list: SkillItem[], id?: string | null) {
  if (!id) return null
  return list.find((s) => s.id === id) ?? null
}