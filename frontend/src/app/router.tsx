import { createBrowserRouter, Navigate } from 'react-router-dom'
import Login from '@/pages/Login'
import Register from '@/pages/Register'
import ConfirmEmail from '@/pages/ConfirmEmail'
import ApplicantDashboard from '@/pages/applicant/ApplicantDashboard'
import ProfilePage from '@/pages/applicant/ProfilePage'
import SkillsPage from '@/pages/applicant/SkillsPage'
import ExperiencePage from '@/pages/applicant/ExperiencePage'
import EducationPage from '@/pages/applicant/EducationPage'
import PrivacyPage from '@/pages/applicant/PrivacyPage'
import FspPage from '@/pages/applicant/FspPage'
import CategoryPage from '@/pages/applicant/CategoryPage'
import EmployerDashboard from '@/pages/employer/EmployerDashboard'
import CompanyPage from '@/pages/employer/CompanyPage'
import HiringNeedsListPage from '@/pages/employer/HiringNeedsListPage'
import HiringNeedFormPage from '@/pages/employer/HiringNeedFormPage'
import { EmployerLayout } from '@/shared/layout/EmployerLayout'
import { ApplicantLayout } from '@/shared/layout/ApplicantLayout'
import { ProtectedRoute } from './ProtectedRoute'

export const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/login" replace /> },
  { path: '/login', element: <Login /> },
  { path: '/register', element: <Register /> },
  { path: '/confirm-email', element: <ConfirmEmail /> },

  {
    element: <ProtectedRoute role="APPLICANT" />,
    children: [
      {
        element: <ApplicantLayout />,
        children: [
          { path: '/applicant', element: <ApplicantDashboard /> },
          { path: '/applicant/profile', element: <ProfilePage /> },
          { path: '/applicant/skills', element: <SkillsPage /> },
          { path: '/applicant/experience', element: <ExperiencePage /> },
          { path: '/applicant/education', element: <EducationPage /> },
          { path: '/applicant/privacy', element: <PrivacyPage /> },
          { path: '/applicant/fsp', element: <FspPage /> },
          { path: '/applicant/category', element: <CategoryPage /> },
        ],
      },
    ],
  },

  {
    element: <ProtectedRoute role="EMPLOYER" />,
    children: [
      {
        element: <EmployerLayout />,
        children: [
          { path: '/employer', element: <EmployerDashboard /> },
          { path: '/employer/company', element: <CompanyPage /> },
          { path: '/employer/hiring-needs', element: <HiringNeedsListPage /> },
          { path: '/employer/hiring-needs/new', element: <HiringNeedFormPage /> },
          { path: '/employer/hiring-needs/:id', element: <HiringNeedFormPage /> },
        ],
      },
    ],
  },

  { path: '*', element: <Navigate to="/login" replace /> },
])