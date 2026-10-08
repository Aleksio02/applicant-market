import type { UUID } from '@/shared/types/api'

export interface CatalogItem<T extends string = string> {
  id: UUID
  code: T
  name: string
}

export interface GradeItem extends CatalogItem {
  level: number
}

export const SPECIALIZATIONS: CatalogItem[] = [
  { id: '28dcc1d5-60d3-4e43-b9de-04acddbb7f17', code: 'BACKEND',  name: 'Backend-разработка' },
  { id: '6d89b710-7223-42fb-aefa-559a698680e6', code: 'DATA',     name: 'Data' },
  { id: '37b4e2f9-f9cd-4a43-a344-8d97b1897c38', code: 'DEVOPS',   name: 'DevOps' },
  { id: '63ad153b-9e1a-46b8-bb6f-c0be2125cff6', code: 'FRONTEND', name: 'Frontend-разработка' },
  { id: '4afc0a01-75ab-45de-a040-3213dc5c49da', code: 'MOBILE',   name: 'Мобильная разработка' },
  { id: '39c4a9c5-f971-45fb-ab1d-76b3b5b3122c', code: 'QA',       name: 'Тестирование' },
]

export const GRADES: GradeItem[] = [
  { id: '1bced867-7eee-43cd-9090-aa14d5f65162', code: 'JUNIOR', name: 'Junior', level: 1 },
  { id: 'd08eb088-3093-4991-96e8-631ffc07f6d1', code: 'MIDDLE', name: 'Middle', level: 2 },
  { id: 'c6cb8177-8326-444c-aeff-a68b8cad4f0b', code: 'SENIOR', name: 'Senior', level: 3 },
  { id: 'd4126dc6-28e7-4fed-ab8c-4edcbb3e4ea6', code: 'LEAD',   name: 'Lead',   level: 4 },
]

export const WORK_FORMAT_LABELS: Record<string, string> = {
  OFFICE: 'Офис',
  REMOTE: 'Удалённо',
  HYBRID: 'Гибрид',
}

export const findSpecialization = (id?: string | null) =>
  SPECIALIZATIONS.find((s) => s.id === id)

export const findGrade = (id?: string | null) =>
  GRADES.find((g) => g.id === id)