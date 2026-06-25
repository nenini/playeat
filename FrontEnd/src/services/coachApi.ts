import { apiRequest } from './api'
import type { CoachFeedbackResponse } from './analysisApi'

export interface CoachResponse {
  coachId: number
  name: string
  role: string
  toneDescription: string
  sampleMessage: string
  selected: boolean
}

export interface CoachListResponse {
  coaches: CoachResponse[]
}

export const coachApi = {
  getCoaches() {
    return apiRequest<CoachListResponse>('/v1/coaches')
  },

  selectMyCoach(coachId: number) {
    return apiRequest('/v1/coaches/me', {
      method: 'PUT',
      body: { coachId }
    })
  },

  getDietFeedback(dietId: string | number) {
    return apiRequest<CoachFeedbackResponse>(`/v1/coaches/me/diets/${dietId}/feedback`)
  },

  createDietFeedback(dietId: string | number) {
    return apiRequest<CoachFeedbackResponse>(`/v1/coaches/me/diets/${dietId}/feedback`, {
      method: 'POST'
    })
  },

  createDietFeedbacksForAllCoaches(dietId: string | number) {
    return apiRequest<CoachFeedbackResponse[]>(`/v1/coaches/me/diets/${dietId}/feedbacks`, {
      method: 'POST'
    })
  }
}
