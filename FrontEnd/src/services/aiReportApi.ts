import { apiRequest } from './api'
import type { AiReportResponse } from './analysisApi'

export const aiReportApi = {
  getDaily(date: string) {
    const params = new URLSearchParams({ date })
    return apiRequest<AiReportResponse>(`/v1/ai/reports/daily?${params}`)
  },

  createDaily(date: string) {
    return apiRequest<AiReportResponse>('/v1/ai/reports/daily', {
      method: 'POST',
      body: { date }
    })
  },

  getWeekly(startDate: string, endDate: string) {
    const params = new URLSearchParams({ startDate, endDate })
    return apiRequest<AiReportResponse>(`/v1/ai/reports/weekly?${params}`)
  },

  createWeekly(startDate: string, endDate: string) {
    return apiRequest<AiReportResponse>('/v1/ai/reports/weekly', {
      method: 'POST',
      body: { startDate, endDate }
    })
  }
}
