export interface AuthUser {
  userId: number
  email: string
  nickname: string
  onboardingCompleted: boolean
}

export interface LoginRequest {
  email: string
  password: string
}

export interface GoogleOAuthLoginRequest {
  code: string
  redirectUri: string
}

export interface SignupRequest {
  email: string
  password: string
  nickname: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: AuthUser
}

export interface SignupResponse {
  userId: number
  email: string
  nickname: string
  onboardingCompleted: boolean
  createdAt: string
}

export interface TokenRefreshResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}
