import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import {
  Briefcase,
  Building2,
  FileText,
  Inbox,
  LayoutDashboard,
  LogOut,
  Mail,
  Search,
} from 'lucide-react'
import { useAuth } from '@/features/auth/useAuth'
import { cn } from '@/shared/lib/cn'
import { FspLogo } from '@/shared/ui/FspLogo'

const NAV = [
  { to: '/employer',              label: 'Обзор',       icon: LayoutDashboard, end: true },
  { to: '/employer/company',      label: 'Компания',    icon: Building2 },
  { to: '/employer/hiring-needs', label: 'Потребности', icon: Briefcase },
  { to: '/employer/vacancies',    label: 'Вакансии',    icon: FileText },
  { to: '/employer/candidates',   label: 'Кандидаты',   icon: Search },
  { to: '/employer/invitations',  label: 'Приглашения', icon: Mail },
  { to: '/employer/responses',    label: 'Отклики',     icon: Inbox },
]

export function EmployerLayout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="flex min-h-screen bg-fsp-violet text-white">
      <aside className="flex w-64 shrink-0 flex-col border-r border-white/10 bg-black/20 p-4">
        {/* Шапка сайдбара — логотип ФСП */}
        <div className="mb-8 flex flex-col items-center gap-2 px-2">
          <FspLogo width={200} />
          <span className="text-[10px] uppercase tracking-[0.2em] text-white/40">
            Applicant Market
          </span>
        </div>

        <nav className="flex-1 space-y-1 overflow-y-auto">
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
            <div className="truncate font-semibold text-white">
              {user?.username}
            </div>
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