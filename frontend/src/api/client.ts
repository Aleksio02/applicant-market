import axios, { AxiosError } from 'axios'
import type { ErrorResponse } from '@/shared/types/api'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
  withCredentials: true, // критично: бэк ставит httpOnly cookie sessionId
  headers: { 'Content-Type': 'application/json' },
})

// Прокидываем читаемое сообщение об ошибке
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ErrorResponse>) => {
    const message =
      error.response?.data?.errorMessage ??
      error.message ??
      'Что-то пошло не так'
    return Promise.reject(new ApiError(message, error.response?.status ?? 0))
  }
)

export class ApiError extends Error {
  status: number
  constructor(message: string, status: number) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}