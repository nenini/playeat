import type { BossRanking, BossRankingResponse, GuildRanking, GuildRankingResponse } from '../../types/ranking'
import { apiRequest } from './client'

export const rankingApi = {
  async getGuildRankings(): Promise<GuildRanking[]> {
    const response = await apiRequest<GuildRankingResponse>('/v1/guilds/rankings')
    return response.rankings
  },
  async getBossRankings(bossId: number): Promise<BossRanking[]> {
    const response = await apiRequest<BossRankingResponse>(`/v1/bosses/${bossId}/rankings`)
    return response.rankings
  }
}
