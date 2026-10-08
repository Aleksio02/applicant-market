import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ArrowLeft } from 'lucide-react'
import { employerApi } from '@/api/employer'
import { ApiError } from '@/api/client'
import { Button } from '@/shared/ui/Button'
import { Input } from '@/shared/ui/Input'
import { Select } from '@/shared/ui/Select'
import { Textarea } from '@/shared/ui/Textarea'
import { Card } from '@/shared/ui/Card'
import { Spinner } from '@/shared/ui/Spinner'
import {
    GRADES,
    SPECIALIZATIONS,
    WORK_FORMAT_LABELS,
} from '@/shared/constants/catalog'
import type { WorkFormat } from '@/shared/types/employer'

const schema = z
    .object({
        title: z.string().min(1, 'Укажите название').max(255),
        description: z.string().optional(),
        specializationId: z.string().uuid('Выберите специализацию'),
        gradeId: z.string().uuid('Выберите грейд'),
        salaryFrom: z.coerce.number().int().positive('Должно быть больше 0'),
        salaryTo: z.coerce.number().int().positive('Должно быть больше 0'),
        format: z.enum(['OFFICE', 'REMOTE', 'HYBRID']),
        location: z.string().optional(),
    })
    .refine((d) => d.salaryFrom <= d.salaryTo, {
        path: ['salaryTo'],
        message: 'Нижняя граница не может быть выше верхней',
    })

type FormValues = z.infer<typeof schema>

export default function HiringNeedFormPage() {
    const { id } = useParams<{ id: string }>()
    const isEdit = !!id
    const navigate = useNavigate()
    const queryClient = useQueryClient()
    const [serverError, setServerError] = useState<string | null>(null)

    const { data: existing, isLoading } = useQuery({
        queryKey: ['employer', 'hiring-need', id],
        queryFn: () => employerApi.getHiringNeed(id!),
        enabled: isEdit,
    })

    const form = useForm<FormValues>({
        resolver: zodResolver(schema),
        defaultValues: {
            title: '',
            description: '',
            specializationId: '',
            gradeId: '',
            salaryFrom: 100000,
            salaryTo: 200000,
            format: 'REMOTE',
            location: '',
        },
    })

    useEffect(() => {
        if (existing) {
            form.reset({
                title: existing.title ?? '',
                description: existing.description ?? '',
                specializationId: existing.specializationId,
                gradeId: existing.gradeId,
                salaryFrom: existing.salaryFrom ?? 0,
                salaryTo: existing.salaryTo ?? 0,
                format: existing.format,
                location: existing.location ?? '',
            })
        }
    }, [existing, form])

    const mutation = useMutation({
        mutationFn: (values: FormValues) => {
            const payload = {
                ...values,
                description: values.description || undefined,
                location: values.location || undefined,
            }
            return isEdit
                ? employerApi.updateHiringNeed(id!, payload)
                : employerApi.createHiringNeed(payload)
        },
        onSuccess: () => {
            setServerError(null)
            queryClient.invalidateQueries({ queryKey: ['employer', 'hiring-needs'] })
            navigate('/employer/hiring-needs')
        },
        onError: (err) =>
            setServerError(err instanceof ApiError ? err.message : 'Не удалось сохранить'),
    })

    const onSubmit = form.handleSubmit((v) => mutation.mutate(v))

    if (isEdit && isLoading) {
        return (
            <div className="flex h-64 items-center justify-center">
                <Spinner className="h-6 w-6" />
            </div>
        )
    }

    return (
        <div className="mx-auto max-w-3xl">
            <button
                type="button"
                onClick={() => navigate('/employer/hiring-needs')}
                className="mb-4 inline-flex items-center gap-1.5 text-sm text-white/60 transition-colors hover:text-white"
            >
                <ArrowLeft className="h-4 w-4" />
                К списку потребностей
            </button>

            <h1 className="text-2xl font-bold">
                {isEdit ? 'Редактировать потребность' : 'Новая потребность'}
            </h1>
            <p className="mt-1 text-sm text-white/60">
                Опишите, какой специалист нужен. На основе этих данных система подберёт кандидатов.
            </p>

            <Card className="mt-6 p-6">
                <form onSubmit={onSubmit} className="space-y-4">
                    <Input
                        label="Название *"
                        placeholder="Backend-разработчик в команду платежей"
                        error={form.formState.errors.title?.message}
                        {...form.register('title')}
                    />

                    <Textarea
                        label="Описание"
                        placeholder="Задачи, стек, культура команды, что важно"
                        {...form.register('description')}
                    />

                    <div className="grid gap-4 sm:grid-cols-2">
                        <Controller
                            control={form.control}
                            name="specializationId"
                            render={({ field }) => (
                                <Select
                                    label="Специализация *"
                                    placeholder="Выберите..."
                                    value={field.value}
                                    onChange={field.onChange}
                                    error={form.formState.errors.specializationId?.message}
                                    options={SPECIALIZATIONS.map((s) => ({
                                        value: s.id,
                                        label: s.name,
                                    }))}
                                />
                            )}
                        />

                        <Controller
                            control={form.control}
                            name="gradeId"
                            render={({ field }) => (
                                <Select
                                    label="Грейд *"
                                    placeholder="Выберите..."
                                    value={field.value}
                                    onChange={field.onChange}
                                    error={form.formState.errors.gradeId?.message}
                                    options={GRADES.map((g) => ({
                                        value: g.id,
                                        label: `${g.name} (${g.level})`,
                                    }))}
                                />
                            )}
                        />
                    </div>

                    <div className="grid gap-4 sm:grid-cols-2">
                        <Input
                            label="Зарплата от, ₽ *"
                            type="number"
                            min={0}
                            step={1000}
                            error={form.formState.errors.salaryFrom?.message}
                            {...form.register('salaryFrom')}
                        />
                        <Input
                            label="Зарплата до, ₽ *"
                            type="number"
                            min={0}
                            step={1000}
                            error={form.formState.errors.salaryTo?.message}
                            {...form.register('salaryTo')}
                        />
                    </div>

                    <div className="grid gap-4 sm:grid-cols-2">
                        <Controller
                            control={form.control}
                            name="format"
                            render={({ field }) => (
                                <Select
                                    label="Формат работы *"
                                    value={field.value}
                                    onChange={field.onChange}
                                    error={form.formState.errors.format?.message}
                                    options={(Object.keys(WORK_FORMAT_LABELS) as WorkFormat[]).map(
                                        (f) => ({ value: f, label: WORK_FORMAT_LABELS[f] })
                                    )}
                                />
                            )}
                        />
                        <Input
                            label="Локация"
                            placeholder="Москва, Санкт-Петербург..."
                            {...form.register('location')}
                        />
                    </div>

                    {serverError && (
                        <div className="rounded-xl border border-red-400/30 bg-red-500/10 px-3.5 py-2.5 text-sm text-red-200">
                            {serverError}
                        </div>
                    )}

                    <div className="flex justify-end gap-3 pt-2">
                        <Button
                            type="button"
                            variant="ghost"
                            onClick={() => navigate('/employer/hiring-needs')}
                        >
                            Отмена
                        </Button>
                        <Button type="submit" loading={mutation.isPending}>
                            {isEdit ? 'Сохранить изменения' : 'Создать потребность'}
                        </Button>
                    </div>
                </form>
            </Card>
        </div>
    )
}