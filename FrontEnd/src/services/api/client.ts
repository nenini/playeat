import type { ApiFieldError, ApiResponse } from '../../types/api'
import { tokenStorage } from './tokenStorage'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/+$/, '')

export class ApiError extends Error {
  status: number
  code?: string
  errors?: ApiFieldError[]

  constructor(message: string, status: number, code?: string, errors?: ApiFieldError[]) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
    this.errors = errors
  }
}

export interface RequestOptions extends Omit<RequestInit, 'body'> {
  body?: unknown
  auth?: boolean
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers = new Headers(options.headers)
  const hasBody = options.body !== undefined
  const isFormData = options.body instanceof FormData

  if (hasBody && !isFormData) {
    headers.set('Content-Type', 'application/json')
  }

  if (options.auth !== false) {
    const token = tokenStorage.getAccessToken()
    if (token) headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE_URL}/${path.replace(/^\/+/, '')}`, {
    ...options,
    headers,
    body: hasBody
      ? isFormData
        ? options.body as BodyInit
        : JSON.stringify(options.body)
      : undefined
  })

  const payload = await parseResponseBody(response)

  if (!response.ok) {
    if (response.status === 401) tokenStorage.clear()
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

async function parseResponseBody(response: Response) {
  const text = await response.text()
  if (!text) return null

  const contentType = response.headers.get('content-type') || ''
  if (!contentType.includes('application/json')) return text

  try {
    return JSON.parse(text)
  } catch {
    return null
  }
}
