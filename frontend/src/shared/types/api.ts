// ===== Enums (совпадают с Java) =====
export type Role = 'APPLICANT' | 'EMPLOYER'
export type UserStatus = 'PENDING_EMAIL' | 'ACTIVE' | 'BLOCKED'
export type ConsentType = 'DATA_PROCESSING' | 'PROFILE_PUBLICATION' | 'CONTACT_REVEAL'
export type UUID = string
// ===== User =====
export interface User {
  id: string
  username: string
  email: string
  role: Role
  status: UserStatus
}

// ===== Auth requests/responses =====
export interface RegisterRequest {
  login: string
  email: string
  password: string
  role: Role
  acceptedConsents: ConsentType[]
}

export interface RegisterResponse {
  userId: string
  message: string
}

export interface AuthorizationRequest {
  login: string
  password: string
}

export interface AuthResponse {
  token: string
  user: User
}

export interface ConfirmEmailRequest {
  userId: string
  code: string
}

export interface SessionPayload {
  userId: string
  created: string
  expires: string
  currentUser: User
}

// ===== Errors =====
export interface ErrorResponse {
  errorCode: number
  errorMessage: string
}