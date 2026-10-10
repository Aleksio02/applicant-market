import type { UUID } from './api'

export interface SpecializationItem {
  id: UUID
  code: string
  name: string
  description?: string | null
}

export interface GradeItem {
  id: UUID
  code: string
  name: string
  level: number
  description?: string | null
}

export interface SkillItem {
  id: UUID
  code: string
  name: string
  category: string
}