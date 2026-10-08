import { api } from './client'
import type {
  ApplicantProfile,
  ApplicantSkill,
  AddEducationRequest,
  AddExperienceRequest,
  AddSkillRequest,
  EducationItem,
  ExperienceItem,
  FspAchievement,
  GradeHistoryItem,
  PrivacySettings,
  ProfileCompleteness,
  UpdateEducationRequest,
  UpdateExperienceRequest,
  UpdatePrivacyRequest,
  UpdateProfileRequest,
  UpdateSkillRequest,
} from '@/shared/types/applicant'

export const applicantApi = {
  // ===== Profile =====
  getProfile: () =>
    api.get<ApplicantProfile>('/applicant/profile').then((r) => r.data),

  updateProfile: (payload: UpdateProfileRequest) =>
    api.put<ApplicantProfile>('/applicant/profile', payload).then((r) => r.data),

  activateProfile: () =>
    api.post<ApplicantProfile>('/applicant/profile/activate').then((r) => r.data),

  hideProfile: () =>
    api.post<ApplicantProfile>('/applicant/profile/hide').then((r) => r.data),

  getCompleteness: () =>
    api.get<ProfileCompleteness>('/applicant/profile/completeness').then((r) => r.data),

  // ===== Experience =====
  listExperiences: () =>
    api.get<ExperienceItem[]>('/applicant/experiences').then((r) => r.data),

  addExperience: (payload: AddExperienceRequest) =>
    api.post<ExperienceItem>('/applicant/experiences', payload).then((r) => r.data),

  updateExperience: (id: string, payload: UpdateExperienceRequest) =>
    api.put<ExperienceItem>(`/applicant/experiences/${id}`, payload).then((r) => r.data),

  deleteExperience: (id: string) =>
    api.delete(`/applicant/experiences/${id}`).then((r) => r.data),

  // ===== Education =====
  listEducations: () =>
    api.get<EducationItem[]>('/applicant/educations').then((r) => r.data),

  addEducation: (payload: AddEducationRequest) =>
    api.post<EducationItem>('/applicant/educations', payload).then((r) => r.data),

  updateEducation: (id: string, payload: UpdateEducationRequest) =>
    api.put<EducationItem>(`/applicant/educations/${id}`, payload).then((r) => r.data),

  deleteEducation: (id: string) =>
    api.delete(`/applicant/educations/${id}`).then((r) => r.data),

  // ===== Skills =====
  listSkills: () =>
    api.get<ApplicantSkill[]>('/applicant/skills').then((r) => r.data),

  addSkill: (payload: AddSkillRequest) =>
    api.post<ApplicantSkill>('/applicant/skills', payload).then((r) => r.data),

  updateSkill: (skillId: string, payload: UpdateSkillRequest) =>
    api.put<ApplicantSkill>(`/applicant/skills/${skillId}`, payload).then((r) => r.data),

  removeSkill: (skillId: string) =>
    api.delete(`/applicant/skills/${skillId}`).then((r) => r.data),

  setPrimarySkill: (skillId: string) =>
    api.put<ApplicantSkill>(`/applicant/skills/${skillId}/primary`).then((r) => r.data),

  // ===== Privacy =====
  getPrivacy: () =>
    api.get<PrivacySettings>('/applicant/privacy').then((r) => r.data),

  updatePrivacy: (payload: UpdatePrivacyRequest) =>
    api.put<PrivacySettings>('/applicant/privacy', payload).then((r) => r.data),

  // ===== FSP =====
  linkFsp: (fspId: string) =>
    api.post('/applicant/fsp/link', { fspId }).then((r) => r.data),

  unlinkFsp: () =>
    api.delete('/applicant/fsp/link').then((r) => r.data),

  listFspAchievements: () =>
    api.get<FspAchievement[]>('/applicant/fsp/achievements').then((r) => r.data),

  // ===== Grade History =====
  listGradeHistory: () =>
    api.get<GradeHistoryItem[]>('/applicant/grade-history').then((r) => r.data),
}