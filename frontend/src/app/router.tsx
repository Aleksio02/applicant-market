import { createBrowserRouter, Navigate } from 'react-router-dom'
import Login from '@/pages/Login'
import Register from '@/pages/Register'
import ConfirmEmail from '@/pages/ConfirmEmail'
import ApplicantDashboard from '@/pages/ApplicantDashboard'
import EmployerDashboard from '@/pages/EmployerDashboard'
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
    children: [{ path: '/employer', element: <EmployerDashboard /> }],
  },

  { path: '*', element: <Navigate to="/login" replace /> },
])