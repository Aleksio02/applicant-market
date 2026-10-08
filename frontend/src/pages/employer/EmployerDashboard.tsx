import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Briefcase, Building2, Plus } from 'lucide-react'
import { employerApi } from '@/api/employer'
import { ApiError } from '@/api/client'
import { Card } from '@/shared/ui/Card'
import { Button } from '@/shared/ui/Button'

export default function EmployerDashboard() {
    const { data: company } = useQuery({
        queryKey: ['employer', 'company'],
        queryFn: async () => {
            try {
                return await employerApi.getMyCompany()
            } catch (err) {
                if (err instanceof ApiError && err.status === 404) return null
                throw err
            }
        },
    })

    const { data: needs = [] } = useQuery({
        queryKey: ['employer', 'hiring-needs', { active: true }],
        queryFn: () => employerApi.listHiringNeeds({ active: true }),
        enabled: !!company,
    })

    return (
        <div className="mx-auto max-w-4xl">
            <h1 className="text-2xl font-bold">Кабинет работодателя</h1>
            <p className="mt-1 text-sm text-white/60">
                Управляйте профилем компании и описаниями потребностей.
            </p>

            <div className="mt-8 grid gap-4 sm:grid-cols-2">
                <Card className="flex flex-col gap-3 p-5">
                    <div className="flex items-center gap-2 text-fsp-lilac">
                        <Building2 className="h-5 w-5" />
                        <span className="text-sm font-medium uppercase tracking-wider">
                            Компания
                        </span>
                    </div>
                    <div className="text-lg font-semibold">
                        {company ? company.name : 'Не заполнено'}
                    </div>
                    <p className="text-sm text-white/60">
                        {company
                            ? 'Профиль компании заполнен.'
                            : 'Заполните профиль, чтобы размещать потребности.'}
                    </p>
                    <div className="mt-auto pt-2">
                        <Link to="/employer/company">
                            <Button variant={company ? 'secondary' : 'primary'}>
                                {company ? 'Редактировать' : 'Заполнить профиль'}
                            </Button>
                        </Link>
                    </div>
                </Card>

                <Card className="flex flex-col gap-3 p-5">
                    <div className="flex items-center gap-2 text-fsp-pink">
                        <Briefcase className="h-5 w-5" />
                        <span className="text-sm font-medium uppercase tracking-wider">
                            Потребности
                        </span>
                    </div>
                    <div className="text-3xl font-bold">{needs.length}</div>
                    <p className="text-sm text-white/60">Активных описаний потребностей</p>
                    <div className="mt-auto pt-2">
                        <Link to="/employer/company">
                            <Button variant={company ? 'secondary' : 'primary'}>
                                {company ? 'Редактировать' : 'Заполнить профиль'}
                            </Button>
                        </Link>
                    </div>
                </Card>
            </div>

            {!company && (
                <div className="mt-6 rounded-xl border border-fsp-pink/30 bg-fsp-pink/10 p-4 text-sm text-fsp-pink">
                    Сначала заполните профиль компании — без него вы не сможете создавать описания потребностей.
                </div>
            )}
        </div>
    )
}