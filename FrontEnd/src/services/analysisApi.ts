import { apiRequest } from './api'
import type { DietDayResponse } from './dietApi'

export interface NutrientAnalysis {
  code: string
  name: string
  unit: string
  current: number
  target: number
  achievementRate: number
  status: 'LOW' | 'OK' | 'HIGH' | string
}

export interface StandardInsight {
  nutrient: string
  label: string
  unit: string
  current: number
  standard: number
  status: 'LOW' | 'OK' | 'HIGH' | string
}

export interface AiReportResponse {
  reportId: number
  reportType: string
  periodStart: string
  periodEnd: string
  healthScore: number
  summary: string
  strengths?: string[]
  warnings?: string[]
  nextAction?: string
  createdAt?: string
}

export interface CoachFeedbackResponse {
  feedbackId: number
  dietId: number
  coachId: number
  coachName: string
  message: string
  modelName?: string
  createdAt?: string
}

export interface AnalysisDailyResponse {
  date: string
  healthScore: number
  diet?: DietDayResponse
  nutrition?: {
    date: string
    healthScore: number
    nutrients: NutrientAnalysis[]
    standardInsights: StandardInsight[]
  }
  dailyReport?: AiReportResponse | null
  mealFeedbacks?: CoachFeedbackResponse[]
  latestMealFeedback?: CoachFeedbackResponse | null
}

export interface AnalysisWeeklyResponse {
  startDate: string
  endDate: string
  averageHealthScore: number
  scoreDiffFromPreviousWeek: number
  recordRate: number
  recordedDays: number
  totalDays: number
  dailyScores: Array<{
    date: string
    dayOfWeek: string
    healthScore: number
  }>
  weeklyNutritionAverage?: {
    calories?: number
    protein?: number
    carbs?: number
    fat?: number
    sodium?: number
    fiber?: number
  }
  weeklyReport?: AiReportResponse | null
}

export const analysisApi = {
  daily(date: string) {
    const params = new URLSearchParams({ date })
    return apiRequest<AnalysisDailyResponse>(`/v1/analysis/daily?${params}`)
  },

  weekly(startDate: string, endDate: string) {
    const params = new URLSearchParams({ startDate, endDate })
    return apiRequest<AnalysisWeeklyResponse>(`/v1/analysis/weekly?${params}`)
  }
}
