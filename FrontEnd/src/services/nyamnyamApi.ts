import { apiRequest } from './api'
import { authApi as baseAuthApi } from './api/authApi'
import type { MealKindId, MealLog, Stage } from './mock/nyamnyamMock'

export type BackendMealType = 'BREAKFAST' | 'LUNCH' | 'SNACK' | 'DINNER'

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: {
    userId: number
    email: string
    nickname: string
    onboardingCompleted: boolean
  }
}

export interface SignupResponse {
  userId: number
  email: string
  nickname: string
  onboardingCompleted: boolean
  createdAt: string
}

export interface FoodSearchItem {
  foodId: number
  name: string
  brand?: string
  category?: string
  nutritionBasisAmount?: number
  nutritionBasisUnit?: string
  servingAmount?: number
  servingUnit?: string
  gramPerPiece?: number
  calories?: number
  protein?: number
  carbs?: number
  fat?: number
  sugar?: number
  sodium?: number
}

export interface FoodSearchPage {
  foods: FoodSearchItem[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface DietDayResponse {
  date: string
  meals: Array<{
    mealType: BackendMealType
    label: string
    timeRange: string
    recorded: boolean
    dietId?: number
    eatenAt?: string
    memo?: string
    totalCalories?: number
    totalProtein?: number
    totalCarbs?: number
    totalFat?: number
    items?: Array<{
      dietItemId: number
      foodId: number
      foodName: string
      brand?: string
      inputAmount: number
      inputUnit: string
      calories?: number
      protein?: number
      carbs?: number
      fat?: number
      sodium?: number
    }>
  }>
  dailySummary?: {
    totalCalories?: number
    targetCalories?: number
    totalProtein?: number
    targetProtein?: number
    totalCarbs?: number
    targetCarbs?: number
    totalFat?: number
    targetFat?: number
    vegetableServings?: number
    targetVegetableServings?: number
  }
}

export interface CharacterResponse {
  characterId: number
  name: string
  level: number
  xp: number
  requiredXp: number
  stage: string
  mood: string
  streakDays: number
}

export interface GuildListResponse {
  guilds: Array<{
    guildId: number
    name: string
    description?: string
    inviteCode?: string
    memberCount: number
    maxMembers: number
    guildPoint: number
    ownerNickname?: string
    myJoinStatus?: string
    joinRequestId?: number
    alreadyJoinedAnyGuild?: boolean
  }>
  page?: number
  size?: number
  hasNext?: boolean
}

const mealKindToBackend: Record<MealKindId, BackendMealType> = {
  breakfast: 'BREAKFAST',
  lunch: 'LUNCH',
  snack: 'SNACK',
  dinner: 'DINNER'
}

const backendToMealKind: Record<BackendMealType, MealKindId> = {
  BREAKFAST: 'breakfast',
  LUNCH: 'lunch',
  SNACK: 'snack',
  DINNER: 'dinner'
}

export const authApi = {
  login(email: string, password: string) {
    return baseAuthApi.login({ email, password })
  },
  signup(email: string, password: string, nickname: string) {
    return baseAuthApi.signup({ email, password, nickname })
  },
  refresh: baseAuthApi.refresh,
  logout: baseAuthApi.logout
}

export const foodApi = {
  search(keyword = '', page = 0, size = 20) {
    const params = new URLSearchParams({ page: String(page), size: String(size) })
    if (keyword.trim()) params.set('keyword', keyword.trim())
    return apiRequest<FoodSearchPage>(`/v1/foods?${params}`, { auth: false })
  },
  frequent() {
    return apiRequest<{ foods: Array<{ foodId: number, name: string }> }>('/v1/foods/frequent')
  }
}

export const dietApi = {
  getDay(date: string) {
    return apiRequest<DietDayResponse>(`/v1/diets?date=${encodeURIComponent(date)}`)
  },
  create(payload: { foodId: string | number, mealKind: MealKindId, qty: number, inputUnit?: string, date: string }) {
    return apiRequest('/v1/diets', {
      method: 'POST',
      body: {
        mealType: mealKindToBackend[payload.mealKind],
        eatenAt: `${payload.date}T12:00:00`,
        memo: '',
        items: [{
          foodId: Number(payload.foodId),
          inputAmount: payload.qty,
          inputUnit: payload.inputUnit || 'g'
        }]
      }
    })
  },
  remove(dietId: string | number) {
    return apiRequest(`/v1/diets/${dietId}`, { method: 'DELETE' })
  }
}

export const userApi = {
  me() {
    return apiRequest('/v1/users/me')
  },
  completeOnboarding(payload: Record<string, string | string[]>) {
    return apiRequest('/v1/users/me/onboarding', {
      method: 'POST',
      body: {
        selectedCoachId: 1,
        healthProfile: mapOnboardingPayload(payload)
      }
    })
  }
}

export const characterApi = {
  me() {
    return apiRequest<CharacterResponse>('/v1/characters/me')
  }
}

export const analysisApi = {
  daily(date: string) {
    return apiRequest(`/v1/analysis/daily?date=${encodeURIComponent(date)}`)
  }
}

export const guildApi = {
  list(page = 0, size = 20) {
    return apiRequest<GuildListResponse>(`/v1/guilds?page=${page}&size=${size}`)
  },
  me() {
    return apiRequest('/v1/guilds/me')
  },
  requestJoin(guildId: string | number) {
    return apiRequest('/v1/guilds/join-requests', {
      method: 'POST',
      body: { guildId: Number(guildId) }
    })
  },
  cancelJoinRequest(guildId: string | number, requestId: string | number) {
    return apiRequest(`/v1/guilds/${guildId}/join-requests/${requestId}`, { method: 'DELETE' })
  }
}

export const shopApi = {
  getShop() {
    return apiRequest('/v1/shop')
  }
}

export function logsFromDietDay(day: DietDayResponse): MealLog[] {
  return day.meals.flatMap((meal) => {
    const mealKind = backendToMealKind[meal.mealType]
    return (meal.items || []).map((item) => ({
      id: String(meal.dietId || item.dietItemId),
      foodId: String(item.foodId),
      mealKind,
      qty: Number(item.inputAmount || 1)
    }))
  })
}

export function stageFromBackend(stage?: string): Stage {
  const value = String(stage || '').toLowerCase()
  if (value.includes('adult')) return 'adult'
  if (value.includes('child')) return 'child'
  if (value.includes('baby')) return 'baby'
  if (value.includes('egg')) return 'egg'
  return 'baby'
}

function mapOnboardingPayload(payload: Record<string, string | string[]>) {
  return {
    heightCm: numberOrNull(payload.height),
    weightKg: numberOrNull(payload.currentWeight),
    targetWeightKg: numberOrNull(payload.targetWeight),
    birthDate: stringOrNull(payload.birthday),
    gender: enumValue(payload.gender),
    healthGoal: enumValue(payload.mainGoal),
    activityLevel: enumValue(payload.activityLevel),
    dietStyles: arrayValue(payload.dietStyle),
    restrictedFoods: arrayValue(payload.restrictedFoods),
    allergies: arrayValue(payload.allergies),
    targetCalories: 2000,
    targetProteinG: 90,
    targetCarbsG: 260,
    targetFatG: 65,
    targetSodiumMg: 2300
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

function enumValue(value: unknown) {
  const raw = stringOrNull(value)
  return raw ? raw.replace(/\s+/g, '_').toUpperCase() : null
}
