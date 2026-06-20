import type {
  BattleQuestListResponse,
  MyQuestResponse,
  QuestContributionListResponse,
  QuestDetail,
  QuestGenerateResponse,
  QuestRewardResponse,
  QuestVerifyResponse
} from '../../types/quest'
import { apiRequest } from './client'

export const questApi = {
  getBattleQuests(battleId: number): Promise<BattleQuestListResponse> {
    return apiRequest(`/v1/boss-battles/${battleId}/quests`)
  },
  getBattleQuestContributions(battleId: number): Promise<QuestContributionListResponse> {
    return apiRequest(`/v1/boss-battles/${battleId}/quests/contributions`)
  },
  generateBattleQuests(battleId: number): Promise<QuestGenerateResponse> {
    return apiRequest(`/v1/boss-battles/${battleId}/quests/generate`, { method: 'POST' })
  },
  async getMyBattleQuests(battleId: number): Promise<MyQuestResponse[]> {
    const response = await apiRequest<{ quests: MyQuestResponse[] }>(`/v1/boss-battles/${battleId}/quests/me`)
    return response.quests
  },
  getQuest(questId: number): Promise<QuestDetail> {
    return apiRequest(`/v1/quests/${questId}`)
  },
  claimQuestReward(questId: number): Promise<QuestRewardResponse> {
    return apiRequest(`/v1/quests/${questId}/reward`, { method: 'POST' })
  },
  verifyQuest(questId: number): Promise<QuestVerifyResponse> {
    return apiRequest(`/v1/quests/${questId}/verify`, { method: 'POST' })
  }
}
