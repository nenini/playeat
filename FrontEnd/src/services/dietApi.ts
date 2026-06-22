import { apiRequest } from './api'
import { registerApiFoods, type MealKindId, type MealLog, type Stage } from './mock/nyamnyamMock'

export type BackendMealType = 'BREAKFAST' | 'LUNCH' | 'SNACK' | 'DINNER'

export interface DietItemRequest {
  foodId: number
  inputAmount: number
  inputUnit: BackendInputUnit
}

export type BackendInputUnit = 'g' | 'ml' | 'piece'

export interface DietSaveRequest {
  mealType: BackendMealType
  eatenAt: string
  memo?: string
  items: DietItemRequest[]
}

export interface DietDetailResponse {
  dietId: number
  mealType: BackendMealType
  eatenAt: string
  memo?: string
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
    totalFiber?: number
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
      fiber?: number
    }>
  }>
  dailySummary?: {
    totalCalories?: number
    targetCalories?: number
    calorieRate?: number
    totalProtein?: number
    targetProtein?: number
    proteinRate?: number
    totalCarbs?: number
    targetCarbs?: number
    carbsRate?: number
    totalFat?: number
    targetFat?: number
    fatRate?: number
    totalFiber?: number
    targetFiber?: number
    fiberRate?: number
  }
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

export const dietApi = {
  getDay(date: string) {
    return apiRequest<DietDayResponse>(`/v1/diets?date=${encodeURIComponent(date)}`)
  },

  getDetail(dietId: string | number) {
    return apiRequest(`/v1/diets/${dietId}`)
  },

  create(payload: { foodId: string | number, mealKind: MealKindId, qty: number, inputUnit?: string, date: string }) {
    return apiRequest<DietDetailResponse>('/v1/diets', {
      method: 'POST',
      body: toDietSaveRequest(payload)
    })
  },

  createMeal(payload: DietSaveRequest) {
    return apiRequest<DietDetailResponse>('/v1/diets', {
      method: 'POST',
      body: payload
    })
  },

  update(dietId: string | number, payload: DietSaveRequest) {
    return apiRequest<DietDetailResponse>(`/v1/diets/${dietId}`, {
      method: 'PATCH',
      body: payload
    })
  },

  remove(dietId: string | number) {
    return apiRequest(`/v1/diets/${dietId}`, { method: 'DELETE' })
  }
}

export function logsFromDietDay(day: DietDayResponse): MealLog[] {
  return day.meals.flatMap((meal) => {
    const mealKind = backendToMealKind[meal.mealType]
    registerApiFoods((meal.items || []).map((item) => ({
      id: String(item.foodId),
      name: item.foodName,
      emoji: '🍽️',
      src: item.brand || 'DB',
      per: `${Number(item.inputAmount || 1)}${item.inputUnit || 'g'}`,
      kcal: Number(item.calories || 0),
      p: Number(item.protein || 0),
      c: Number(item.carbs || 0),
      f: Number(item.fat || 0),
      unit: item.inputUnit || 'g',
      presets: item.inputUnit === 'g' ? [50, 100, 150] : [1, 2, 3]
    })))
    return (meal.items || []).map((item) => ({
      id: String(item.dietItemId),
      dietId: meal.dietId ? String(meal.dietId) : undefined,
      foodId: String(item.foodId),
      mealKind,
      qty: Number(item.inputAmount || 1),
      inputUnit: item.inputUnit
    }))
  })
}

export function stageFromBackend(stage?: string): Stage {
  const value = String(stage || '').toLowerCase()
  if (value.includes('adult')) return 'adult'
  if (value.includes('egg')) return 'egg'
  return 'chick'
}

export function toBackendInputUnit(inputUnit?: string): BackendInputUnit {
  const normalized = String(inputUnit || '').trim().toLowerCase()
  if (normalized === 'g' || normalized === 'gram') return 'g'
  if (normalized === 'ml') return 'ml'
  return 'piece'
}

export function mealKindToBackendMealType(mealKind: MealKindId): BackendMealType {
  return mealKindToBackend[mealKind]
}

function toDietSaveRequest(payload: { foodId: string | number, mealKind: MealKindId, qty: number, inputUnit?: string, date: string }): DietSaveRequest {
  return {
    mealType: mealKindToBackend[payload.mealKind],
    eatenAt: `${payload.date}T12:00:00`,
    memo: '',
    items: [{
      foodId: Number(payload.foodId),
      inputAmount: payload.qty,
      inputUnit: toBackendInputUnit(payload.inputUnit)
    }]
  }
}
