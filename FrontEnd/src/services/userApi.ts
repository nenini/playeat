import { apiRequest } from './api'

export interface UserMeResponse {
  userId: number
  email: string
  nickname: string
  profileImageUrl?: string
  profileImagePath?: string
  selectedCoachId?: number
  status?: string
  onboardingCompleted?: boolean
  createdAt?: string
}

export interface HealthProfileResponse {
  healthProfileId?: number
  heightCm?: number
  weightKg?: number
  targetWeightKg?: number
  birthDate?: string
  gender?: string
  healthGoal?: string
  activityLevel?: string
  dietStyles?: string[]
  restrictedFoods?: string[]
  allergies?: string[]
  targetCalories?: number
  targetProteinG?: number
  targetCarbsG?: number
  targetFatG?: number
  targetSodiumMg?: number
  targetFiberG?: number
  nutritionStandardVersion?: string
  nutritionTargetCalculatedAt?: string
  updatedAt?: string
}

export interface HealthProfileRequest {
  heightCm?: number | null
  weightKg?: number | null
  targetWeightKg?: number | null
  birthDate?: string | null
  gender?: string | null
  healthGoal?: string | null
  activityLevel?: string | null
  dietStyles?: string[]
  restrictedFoods?: string[]
  allergies?: string[]
}

export const userApi = {
  getMe() {
    return apiRequest<UserMeResponse>('/v1/users/me')
  },

  updateMe(payload: { nickname: string }) {
    return apiRequest('/v1/users/me', {
      method: 'PATCH',
      body: payload
    })
  },

  deactivate(payload: { password: string }) {
    return apiRequest('/v1/users/me', {
      method: 'DELETE',
      body: payload
    })
  },

  getHealthProfile() {
    return apiRequest<HealthProfileResponse>('/v1/users/me/health-profile')
  },

  updateHealthProfile(payload: HealthProfileRequest) {
    return apiRequest<HealthProfileResponse>('/v1/users/me/health-profile', {
      method: 'PATCH',
      body: payload
    })
  },

  completeOnboarding(payload: Record<string, string | string[]>) {
    return apiRequest('/v1/users/me/onboarding', {
      method: 'POST',
      body: {
        selectedCoachId: 1,
        healthProfile: mapOnboardingPayload(payload)
      }
    })
  },

  changePassword(payload: { currentPassword: string, newPassword: string, newPasswordConfirm: string }) {
    return apiRequest('/v1/users/me/password', {
      method: 'PATCH',
      body: payload
    })
  },

  uploadProfileImage(file: File) {
    const formData = new FormData()
    formData.append('image', file)
    return apiRequest('/v1/users/me/profile-image', {
      method: 'PUT',
      body: formData
    })
  },

  deleteProfileImage() {
    return apiRequest('/v1/users/me/profile-image', { method: 'DELETE' })
  }
}

function mapOnboardingPayload(payload: Record<string, string | string[]>): HealthProfileRequest {
  return {
    heightCm: numberOrNull(payload.height),
    weightKg: numberOrNull(payload.currentWeight),
    targetWeightKg: numberOrNull(payload.targetWeight),
    birthDate: stringOrNull(payload.birthday),
    gender: mapGender(payload.gender),
    healthGoal: mapHealthGoal(payload.mainGoal),
    activityLevel: mapActivityLevel(payload.activityLevel),
    dietStyles: arrayValue(payload.dietStyle),
    restrictedFoods: arrayValue(payload.restrictedFoods),
    allergies: arrayValue(payload.allergies)
  }
}

function numberOrNull(value: unknown) {
  const numberValue = Number(Array.isArray(value) ? value[0] : value)
  return Number.isFinite(numberValue) && numberValue > 0 ? numberValue : null
}

function stringOrNull(value: unknown) {
  const stringValue = String(Array.isArray(value) ? value[0] : value || '').trim()
  return stringValue || null
}

function arrayValue(value: unknown) {
  if (Array.isArray(value)) return value.map(String).filter(Boolean)
  const single = stringOrNull(value)
  return single ? [single] : []
}

function mapGender(value: unknown): string | null {
  const map: Record<string, string> = { '여성': 'FEMALE', '남성': 'MALE' }
  const raw = stringOrNull(value)
  return (raw && map[raw]) || null
}

function mapHealthGoal(value: unknown): string | null {
  const map: Record<string, string> = { '감량': 'LOSE_WEIGHT', '증량': 'GAIN_WEIGHT', '유지': 'MAINTAIN' }
  const raw = stringOrNull(value)
  return (raw && map[raw]) || null
}

function mapActivityLevel(value: unknown): string | null {
  const map: Record<string, string> = {
    '거의 앉아 있어요': 'SEDENTARY',
    '가벼운 활동이 있어요': 'LIGHT',
    '많이 걷거나 움직여요': 'MODERATE',
    '육체 활동이 많아요': 'ACTIVE'
  }
  const raw = stringOrNull(value)
  return (raw && map[raw]) || null
}
