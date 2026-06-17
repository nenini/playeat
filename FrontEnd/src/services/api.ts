const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '')

export interface ApiResponse<T> {
  success: boolean
  data: T
  message: string
  code?: string
  errors?: Array<{ field: string, reason: string }>
}

export class ApiError extends Error {
  status: number
  code?: string
  errors?: Array<{ field: string, reason: string }>

  constructor(message: string, status: number, code?: string, errors?: Array<{ field: string, reason: string }>) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
    this.errors = errors
  }
}

export const tokenStorage = {
  getAccessToken() {
    return localStorage.getItem('nyamnyam.accessToken')
  },
  getRefreshToken() {
    return localStorage.getItem('nyamnyam.refreshToken')
  },
  setTokens(accessToken: string, refreshToken: string) {
    localStorage.setItem('nyamnyam.accessToken', accessToken)
    localStorage.setItem('nyamnyam.refreshToken', refreshToken)
  },
  clear() {
    localStorage.removeItem('nyamnyam.accessToken')
    localStorage.removeItem('nyamnyam.refreshToken')
  }
}

interface RequestOptions extends Omit<RequestInit, 'body'> {
  body?: unknown
  auth?: boolean
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers = new Headers(options.headers)
  const hasBody = options.body !== undefined

  if (hasBody && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json')
  }

  if (options.auth !== false) {
    const token = tokenStorage.getAccessToken()
    if (token) headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
    body: hasBody
      ? options.body instanceof FormData
        ? options.body
        : JSON.stringify(options.body)
      : undefined
  })

  const contentType = response.headers.get('content-type') || ''
  const payload = contentType.includes('application/json') ? await response.json() : null

  if (!response.ok) {
    throw new ApiError(payload?.message || response.statusText, response.status, payload?.code, payload?.errors)
  }

  if (payload && typeof payload === 'object' && 'success' in payload) {
    const envelope = payload as ApiResponse<T>
    if (!envelope.success) {
      throw new ApiError(envelope.message, response.status, envelope.code, envelope.errors)
    }
    return envelope.data
  }

  return payload as T
}
