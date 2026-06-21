import { apiRequest } from './api'

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
  fiber?: number
}

export interface FoodSearchPage {
  foods: FoodSearchItem[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface FrequentFoodListResponse {
  foods: Array<{
    foodId: number
    name: string
  }>
}

export const foodApi = {
  search(keyword = '', page = 0, size = 20) {
    const params = new URLSearchParams({ page: String(page), size: String(size) })
    if (keyword.trim()) params.set('keyword', keyword.trim())
    return apiRequest<FoodSearchPage>(`/v1/foods?${params}`, { auth: false })
  },

  getDetail(foodId: string | number) {
    return apiRequest<FoodSearchItem>(`/v1/foods/${foodId}`)
  },

  frequent() {
    return apiRequest<FrequentFoodListResponse>('/v1/foods/frequent')
  }
}
