import type { MatchReason } from '@/shared/types/matching'

/** Иконка/цвет по коду причины */
export function reasonTone(code: string): 'success' | 'pink' | 'lilac' | 'muted' | 'danger' {
  switch (code) {
    case 'GRADE_MATCH':
      return 'success'
    case 'SKILL_MATCH':
      return 'pink'
    case 'SKILL_MATCH_PARTIAL':
      return 'lilac'
    case 'MISSING_SKILLS':
      return 'danger'
    case 'FSP_ACHIEVEMENTS':
      return 'lilac'
    case 'ASSESSMENT_SCORE':
      return 'pink'
    case 'FRESHNESS':
      return 'muted'
    default:
      return 'muted'
  }
}

export function formatMatchPercent(value: number): string {
  return `${Math.round(value * 100)}%`
}

export function formatAssessmentScore(score?: number | null): string {
  if (score == null) return '—'
  return `${Math.round(score * 100)}%`
}