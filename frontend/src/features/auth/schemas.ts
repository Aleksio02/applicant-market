import { z } from 'zod'

export const loginSchema = z.object({
  login: z.string().min(1, 'Введите логин или email'),
  password: z.string().min(1, 'Введите пароль'),
})
export type LoginForm = z.infer<typeof loginSchema>

export const registerSchema = z
  .object({
    login: z.string().min(3, 'Минимум 3 символа').max(32, 'Максимум 32 символа'),
    email: z.string().email('Некорректный email'),
    password: z
      .string()
      .min(8, 'Минимум 8 символов')
      .max(32, 'Максимум 32 символа'),
    confirmPassword: z.string(),
    role: z.enum(['APPLICANT', 'EMPLOYER']),
    acceptedConsents: z.array(z.enum(['DATA_PROCESSING', 'PROFILE_PUBLICATION', 'CONTACT_REVEAL'])),
  })
  .refine((d) => d.password === d.confirmPassword, {
    path: ['confirmPassword'],
    message: 'Пароли не совпадают',
  })
  .refine((d) => d.acceptedConsents.includes('DATA_PROCESSING'), {
    path: ['acceptedConsents'],
    message: 'Без согласия на обработку данных регистрация невозможна',
  })
export type RegisterForm = z.infer<typeof registerSchema>

export const confirmEmailSchema = z.object({
  code: z.string().length(6, 'Код состоит из 6 цифр').regex(/^\d+$/, 'Только цифры'),
})
export type ConfirmEmailForm = z.infer<typeof confirmEmailSchema>