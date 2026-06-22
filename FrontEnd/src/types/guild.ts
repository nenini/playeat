import type { CharacterEquipment } from './characterEquipment'

export type GuildRole = 'OWNER' | 'MEMBER'
export type GuildJoinRequestStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELED'
export type MyGuildJoinStatus = 'NONE' | 'PENDING' | 'JOINED'

export interface GuildSearchParams { page?: number; size?: number; keyword?: string }
export interface GuildSummary { guildId: number; name: string; description?: string; inviteCode?: string; memberCount: number; maxMembers: number; guildPoint: number; ownerNickname?: string; visibility?: string; status?: string; myJoinStatus?: GuildJoinRequestStatus; joinRequestId?: number; alreadyJoinedAnyGuild?: boolean }
export interface GuildListResponse { guilds: GuildSummary[]; page: number; size: number; hasNext: boolean }
export interface CreateGuildRequest { name: string; description?: string; maxMembers?: number; visibility?: string }
export interface UpdateGuildRequest { name?: string; description?: string; maxMembers?: number; visibility?: string }
export interface GuildDetail extends GuildSummary { ownerUserId?: number; myRole?: GuildRole; createdAt?: string; updatedAt?: string }
export interface JoinRequestByInviteCodeRequest { inviteCode: string }
export interface GuildJoinRequest { requestId: number; guildId: number; guildName?: string; userId?: number; nickname?: string; profileImageUrl?: string; characterId?: number; characterName?: string; characterLevel?: number; inviteCode?: string; guildDescription?: string; status: GuildJoinRequestStatus; createdAt?: string; handledAt?: string; handledByNickname?: string; memberId?: number }
export interface MyGuild { guildId: number; name: string; description?: string; inviteCode?: string; myRole?: GuildRole; memberCount?: number; maxMembers?: number; guildPoint?: number; joinedAt?: string }
export interface MyGuildStatusInfo { guildId: number; name: string; inviteCode: string; role: GuildRole }
export interface MyGuildStatusRequest { requestId: number; status: GuildJoinRequestStatus; createdAt: string }
export interface MyGuildStatus { status: MyGuildJoinStatus; guildId?: number; guild: MyGuildStatusInfo | null; joinRequest: MyGuildStatusRequest | null }
export interface GuildMember { memberId: number; guildId?: number; userId: number; nickname: string; profileImageUrl?: string; characterId?: number; characterName?: string; characterLevel?: number; characterStage?: string; characterMood?: string; characterAppearanceType?: string; role: GuildRole; joinedAt?: string; isMe?: boolean }
export interface GuildMemberDetail extends GuildMember { streakDays?: number; weeklyRecordRate?: number; bossContribution?: number; completedQuestCount?: number; equippedItems?: CharacterEquipment[] }
export interface GuildNotice { noticeId: number; guildId: number; writerUserId?: number; writerNickname?: string; title: string; content: string; createdAt: string; updatedAt?: string; editable?: boolean }
export type GuildNoticeDetail = GuildNotice
export interface CreateGuildNoticeRequest { title: string; content: string }
export interface UpdateGuildNoticeRequest { title: string; content: string }
export interface GuildDailyStat { date: string; dayOfWeek: string; recordCount: number; activeMemberCount: number; recordRate: number; questCompletedCount: number; damage: number; score: number }
export interface DailyScore { dayOfWeek: string; score: number }
export interface GuildDashboard { guildId: number; guildName: string; myRank?: number; weeklyScore: number; recordRate: number; bossDamage: number; questCompletedCount: number; questTotalCount: number; dailyScores: DailyScore[] }
export interface GuildWeeklyReport { guildId: number; guildName: string; weekStartDate: string; weekEndDate: string; recordRate: number; bossDamage: number; weeklyScore: number; questCompletedCount: number; questTotalCount: number; dailyStats: GuildDailyStat[] }
