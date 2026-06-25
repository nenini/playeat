export interface ApiFieldError {
  field?: string
  message?: string
  rejectedValue?: unknown
}

export interface ApiResponse<T> {
  success: boolean
  data: T
  message: string
  code?: string
  errors?: ApiFieldError[]
}
