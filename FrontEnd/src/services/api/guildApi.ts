import type {
  CreateGuildNoticeRequest,
  CreateGuildRequest,
  GuildDashboard,
  GuildDetail,
  GuildJoinRequest,
  GuildListResponse,
  GuildMember,
  GuildMemberDetail,
  GuildNotice,
  GuildNoticeDetail,
  GuildSearchParams,
  GuildWeeklyReport,
  JoinRequestByInviteCodeRequest,
  MyGuild,
  MyGuildStatus,
  UpdateGuildNoticeRequest,
  UpdateGuildRequest
} from '../../types/guild'
import { apiRequest } from './client'

export const guildApi = {
  getGuilds(params: GuildSearchParams = {}): Promise<GuildListResponse> {
    const query = new URLSearchParams()
    if (params.page !== undefined) query.set('page', String(params.page))
    if (params.size !== undefined) query.set('size', String(params.size))
    if (params.keyword?.trim()) query.set('keyword', params.keyword.trim())
    const suffix = query.size ? `?${query}` : ''
    return apiRequest<GuildListResponse>(`/v1/guilds${suffix}`)
  },
  createGuild(payload: CreateGuildRequest): Promise<GuildDetail> {
    return apiRequest('/v1/guilds', { method: 'POST', body: payload })
  },
  getGuild(guildId: number): Promise<GuildDetail> {
    return apiRequest(`/v1/guilds/${guildId}`)
  },
  async deleteGuild(guildId: number): Promise<void> {
    await apiRequest(`/v1/guilds/${guildId}`, { method: 'DELETE' })
  },
  updateGuild(guildId: number, payload: UpdateGuildRequest): Promise<GuildDetail> {
    return apiRequest(`/v1/guilds/${guildId}`, { method: 'PATCH', body: payload })
  },
  createJoinRequest(guildId: number): Promise<GuildJoinRequest> {
    return apiRequest(`/v1/guilds/${guildId}/join-requests`, { method: 'POST' })
  },
  createJoinRequestByInviteCode(payload: JoinRequestByInviteCodeRequest): Promise<GuildJoinRequest> {
    return apiRequest('/v1/guilds/join-requests', { method: 'POST', body: payload })
  },
  async getMyJoinRequests(): Promise<GuildJoinRequest[]> {
    const response = await apiRequest<{ requests: GuildJoinRequest[] }>('/v1/guilds/join-requests/me')
    return response.requests
  },
  async cancelJoinRequest(guildId: number, requestId: number): Promise<void> {
    await apiRequest(`/v1/guilds/${guildId}/join-requests/${requestId}`, { method: 'DELETE' })
  },
  async getMyGuilds(): Promise<MyGuild[]> {
    const response = await apiRequest<{ guilds: MyGuild[] }>('/v1/guilds/me')
    return response.guilds
  },
  getMyGuildStatus(): Promise<MyGuildStatus> {
    return apiRequest('/v1/guilds/me/status')
  },
  async getGuildMembers(guildId: number): Promise<GuildMember[]> {
    const response = await apiRequest<{ members: GuildMember[] }>(`/v1/guilds/${guildId}/members`)
    return response.members
  },
  getGuildMember(guildId: number, memberId: number): Promise<GuildMemberDetail> {
    return apiRequest(`/v1/guilds/${guildId}/members/${memberId}`)
  },
  async leaveGuild(guildId: number): Promise<void> {
    await apiRequest(`/v1/guilds/${guildId}/members/me`, { method: 'DELETE' })
  },
  async kickGuildMember(guildId: number, memberId: number): Promise<void> {
    await apiRequest(`/v1/guilds/${guildId}/members/${memberId}`, { method: 'DELETE' })
  },
  async getGuildJoinRequests(guildId: number): Promise<GuildJoinRequest[]> {
    const response = await apiRequest<{ requests: GuildJoinRequest[] }>(`/v1/guilds/${guildId}/join-requests`)
    return response.requests
  },
  approveJoinRequest(guildId: number, requestId: number): Promise<GuildJoinRequest> {
    return apiRequest(`/v1/guilds/${guildId}/join-requests/${requestId}/approve`, { method: 'POST' })
  },
  rejectJoinRequest(guildId: number, requestId: number): Promise<GuildJoinRequest> {
    return apiRequest(`/v1/guilds/${guildId}/join-requests/${requestId}/reject`, { method: 'POST' })
  },
  async getGuildNotices(guildId: number): Promise<GuildNotice[]> {
    const response = await apiRequest<{ notices: GuildNotice[] }>(`/v1/guilds/${guildId}/notices`)
    return response.notices
  },
  getGuildNotice(guildId: number, noticeId: number): Promise<GuildNoticeDetail> {
    return apiRequest(`/v1/guilds/${guildId}/notices/${noticeId}`)
  },
  createGuildNotice(guildId: number, payload: CreateGuildNoticeRequest): Promise<GuildNoticeDetail> {
    return apiRequest(`/v1/guilds/${guildId}/notices`, { method: 'POST', body: payload })
  },
  updateGuildNotice(guildId: number, noticeId: number, payload: UpdateGuildNoticeRequest): Promise<GuildNoticeDetail> {
    return apiRequest(`/v1/guilds/${guildId}/notices/${noticeId}`, { method: 'PATCH', body: payload })
  },
  async deleteGuildNotice(guildId: number, noticeId: number): Promise<void> {
    await apiRequest(`/v1/guilds/${guildId}/notices/${noticeId}`, { method: 'DELETE' })
  },
  getGuildDashboard(guildId: number): Promise<GuildDashboard> {
    return apiRequest(`/v1/guilds/${guildId}/dashboard`)
  },
  getGuildWeeklyReport(guildId: number): Promise<GuildWeeklyReport> {
    return apiRequest(`/v1/guilds/${guildId}/reports/weekly`)
  }
}
