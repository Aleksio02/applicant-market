import { createBrowserRouter, Navigate } from 'react-router-dom'
import Login from '@/pages/Login'
import Register from '@/pages/Register'
import ConfirmEmail from '@/pages/ConfirmEmail'
import ApplicantDashboard from '@/pages/ApplicantDashboard'
import EmployerDashboard from '@/pages/employer/EmployerDashboard'
import CompanyPage from '@/pages/employer/CompanyPage'
import HiringNeedsListPage from '@/pages/employer/HiringNeedsListPage'
import HiringNeedFormPage from '@/pages/employer/HiringNeedFormPage'
import { EmployerLayout } from '@/shared/layout/EmployerLayout'
import { ProtectedRoute } from './ProtectedRoute'

export const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/login" replace /> },
  { path: '/login', element: <Login /> },
  { path: '/register', element: <Register /> },
  { path: '/confirm-email', element: <ConfirmEmail /> },

  {
    element: <ProtectedRoute role="APPLICANT" />,
    children: [{ path: '/applicant', element: <ApplicantDashboard /> }],
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