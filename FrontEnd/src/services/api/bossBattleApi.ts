import type {
  BossBattleDashboard,
  BossBattleCreateResponse,
  BossBattleDetail,
  BossBattleHp,
  BossBattleRewardResponse,
  BossBattleSummary,
  CommonConditionVerifyResponse,
  CreateBossBattleRequest,
  CurrentBossBattleResponse
} from '../../types/bossBattle'
import { apiRequest } from './client'

export const bossBattleApi = {
  createBossBattle(guildId: number, payload: CreateBossBattleRequest): Promise<BossBattleCreateResponse> {
    return apiRequest(`/v1/guilds/${guildId}/boss-battles`, { method: 'POST', body: payload })
  },
  getCurrentGuildBossBattle(guildId: number): Promise<CurrentBossBattleResponse> {
    return apiRequest(`/v1/guilds/${guildId}/boss-battles/current`)
  },
  async getGuildBossBattleHistory(guildId: number): Promise<BossBattleSummary[]> {
    const response = await apiRequest<{ battles: BossBattleSummary[] }>(`/v1/guilds/${guildId}/boss-battles/history`)
    return response.battles
  },
  getBossBattle(battleId: number): Promise<BossBattleDetail> {
    return apiRequest(`/v1/boss-battles/${battleId}`)
  },
  verifyCommonConditions(battleId: number): Promise<CommonConditionVerifyResponse> {
    return apiRequest(`/v1/boss-battles/${battleId}/common-conditions/verify`, { method: 'POST' })
  },
  getBossBattleHp(battleId: number): Promise<BossBattleHp> {
    return apiRequest(`/v1/boss-battles/${battleId}/hp`)
  },
  claimBossBattleReward(battleId: number): Promise<BossBattleRewardResponse> {
    return apiRequest(`/v1/boss-battles/${battleId}/reward`, { method: 'POST' })
  },
  getBossBattleDashboard(battleId: number): Promise<BossBattleDashboard> {
    return apiRequest(`/v1/boss-battles/${battleId}/dashboard`)
  }
}
