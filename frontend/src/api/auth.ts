import { api } from './client'
import type {
  AuthResponse,
  AuthorizationRequest,
  ConfirmEmailRequest,
  RegisterRequest,
  RegisterResponse,
  SessionPayload,
} from '@/shared/types/api'

export const authApi = {
  register: (payload: RegisterRequest) =>
    api.post<RegisterResponse>('/auth/register', payload).then((r) => r.data),

  confirmEmail: (payload: ConfirmEmailRequest) =>
    api.post<AuthResponse>('/auth/confirm-email', payload).then((r) => r.data),

  login: (payload: AuthorizationRequest) =>
    api.post<AuthResponse>('/auth/login', payload).then((r) => r.data),

  validateSession: () =>
    api.get<SessionPayload>('/auth/validateSession').then((r) => r.data),
}