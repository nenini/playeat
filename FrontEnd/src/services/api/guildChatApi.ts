import type { GuildChatListResponse, GuildChatMessage, GuildChatParams, SendGuildChatRequest } from '../../types/guildChat'
import { apiRequest } from './client'

export const guildChatApi = {
  async getGuildChats(guildId: number, params: GuildChatParams = {}): Promise<GuildChatMessage[]> {
    const query = new URLSearchParams()
    if (params.page !== undefined) query.set('page', String(params.page))
    if (params.size !== undefined) query.set('size', String(params.size))
    const suffix = query.size ? `?${query}` : ''
    const response = await apiRequest<GuildChatListResponse>(`/v1/guilds/${guildId}/chats${suffix}`)
    return response.chats
  },
  sendGuildChat(guildId: number, payload: SendGuildChatRequest): Promise<GuildChatMessage> {
    return apiRequest(`/v1/guilds/${guildId}/chats`, { method: 'POST', body: payload })
  }
}
