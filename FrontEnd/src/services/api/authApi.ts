import type { GoogleOAuthLoginRequest, LoginRequest, LoginResponse, SignupRequest, SignupResponse, TokenRefreshResponse } from '../../types/auth'
import { apiRequest } from './client'
import { tokenStorage } from './tokenStorage'

export const authApi = {
  async login(payload: LoginRequest): Promise<LoginResponse> {
    const response = await apiRequest<LoginResponse>('/v1/auth/login', {
      method: 'POST',
      auth: false,
      body: payload
    })
    tokenStorage.setTokens(response.accessToken, response.refreshToken)
    return response
  },

  async loginWithGoogle(payload: GoogleOAuthLoginRequest): Promise<LoginResponse> {
    const response = await apiRequest<LoginResponse>('/v1/auth/oauth/google', {
      method: 'POST',
      auth: false,
      body: payload
    })
    tokenStorage.setTokens(response.accessToken, response.refreshToken)
    return response
  },

  signup(payload: SignupRequest): Promise<SignupResponse> {
    return apiRequest<SignupResponse>('/v1/auth/signup', {
      method: 'POST',
      auth: false,
      body: payload
    })
  },

  async refresh(refreshToken = tokenStorage.getRefreshToken() || ''): Promise<TokenRefreshResponse> {
    const response = await apiRequest<TokenRefreshResponse>('/v1/auth/refresh', {
      method: 'POST',
      auth: false,
      body: { refreshToken }
    })
    tokenStorage.setTokens(response.accessToken, response.refreshToken)
    return response
  },

  async logout(): Promise<void> {
    const refreshToken = tokenStorage.getRefreshToken()

    try {
      if (refreshToken) {
        await apiRequest('/v1/auth/logout', {
          method: 'POST',
          auth: false,
          body: { refreshToken }
        })
      }
    } finally {
      tokenStorage.clear()
    }
  }
}
