import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import {
  Award,
  BookOpen,
  Briefcase,
  FileCheck2,
  GraduationCap,
  LayoutDashboard,
  Lock,
  LogOut,
  Search,
  Sparkles,
  User,
} from 'lucide-react'
import { useAuth } from '@/features/auth/useAuth'
import { cn } from '@/shared/lib/cn'

const NAV = [
  { to: '/applicant',            label: 'Обзор',         icon: LayoutDashboard, end: true },
  { to: '/applicant/profile',    label: 'Профиль',       icon: User },
  { to: '/applicant/skills',     label: 'Навыки',        icon: Sparkles },
  { to: '/applicant/assessment', label: 'Тест на грейд', icon: FileCheck2 },
  { to: '/applicant/experience', label: 'Опыт',          icon: Briefcase },
  { to: '/applicant/education',  label: 'Образование',   icon: GraduationCap },
  { to: '/applicant/category',   label: 'Категория',     icon: Award },
  { to: '/applicant/vacancies',  label: 'Вакансии',      icon: Search },
  { to: '/applicant/fsp',        label: 'ФСП',           icon: BookOpen },
  { to: '/applicant/privacy',    label: 'Приватность',   icon: Lock },
]

export function ApplicantLayout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="flex min-h-screen bg-fsp-violet text-white">
      <aside className="flex w-64 shrink-0 flex-col border-r border-white/10 bg-black/20 p-4">
        <div className="mb-8 flex items-center gap-2 px-2">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-fsp-pink font-black">
            Ф
          </div>
          <span className="font-bold">Applicant Market</span>
        </div>

        <nav className="flex-1 space-y-1">
          {NAV.map(({ to, label, icon: Icon, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                cn(
                  'flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-colors',
                  isActive
                    ? 'bg-fsp-pink/15 text-white'
                    : 'text-white/60 hover:bg-white/5 hover:text-white'
                )
              }
            >
              <Icon className="h-4 w-4" />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="border-t border-white/10 pt-4">
          <div className="mb-3 px-2 text-xs">
            <div className="truncate font-semibold text-white">{user?.username}</div>
            <div className="truncate text-white/40">{user?.email}</div>
          </div>
          <button
            onClick={handleLogout}
            className="flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium text-white/60 transition-colors hover:bg-white/5 hover:text-white"
          >
            <LogOut className="h-4 w-4" />
            Выйти
          </button>
        </div>
      </aside>

      <main className="flex-1 overflow-auto p-8">
        <Outlet />
      </main>
    </div>
  )
}