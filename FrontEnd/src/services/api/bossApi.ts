import type { BossDetail, CurrentBossesResponse } from '../../types/boss'
import { apiRequest } from './client'

export const bossApi = {
  getCurrentBosses(): Promise<CurrentBossesResponse> {
    return apiRequest('/v1/bosses/current')
  },
  getBoss(bossId: number): Promise<BossDetail> {
    return apiRequest(`/v1/bosses/${bossId}`)
  }
}
