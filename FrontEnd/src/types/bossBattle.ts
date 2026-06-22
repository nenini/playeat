import type { BossDifficulty } from './boss'

export type BossBattleStatus = 'IN_PROGRESS' | 'DEFEATED' | 'EXPIRED' | 'FAILED'
export interface CreateBossBattleRequest { bossId: number }
export interface BossBattleCreateResponse { battleId: number; guildId: number; bossId: number; seasonId: number; bossName: string; difficulty: BossDifficulty; status: BossBattleStatus; maxHp: number; currentHp: number; startedAt: string }
export interface BossBattleSummary { battleId: number; guildId: number; bossId: number; bossName: string; difficulty: BossDifficulty; bossImageUrl?: string | null; imageUrl?: string | null; boss?: { imageUrl?: string | null } | null; status: BossBattleStatus; maxHp: number; currentHp: number; totalDamage: number; startedAt?: string; endedAt?: string; endsAt?: string; rewardClaimed: boolean }
export interface CurrentBossBattleResponse { battle: BossBattleSummary | null }
export interface BossBattleCondition { battleConditionId: number; title: string; description?: string; targetType?: string; thresholdValue?: number; thresholdUnit?: string; targetValue?: number; requiredDays?: number; currentValue?: number; damage?: number; unit?: string; completed: boolean; sortOrder?: number }
export interface BossBattleDamageLog { damageLogId?: number; userId?: number; nickname?: string; damage: number; createdAt?: string; sourceType?: string }
export interface BossBattleDetail extends BossBattleSummary { guildName: string; hpRate: number; commonConditions: BossBattleCondition[]; recentDamageLogs: BossBattleDamageLog[]; participantCount: number; activeParticipantCount: number; leftParticipantCount: number }
export interface CommonConditionVerifyItem { battleConditionId: number; title: string; currentValue: number; targetValue: number; completed: boolean; newlyCompleted: boolean; damage: number }
export interface CommonConditionVerifyResponse { battleId: number; bossBattleStatus: BossBattleStatus; currentHp: number; totalDamage: number; conditions: CommonConditionVerifyItem[] }
export interface BossBattleHp { battleId: number; status: BossBattleStatus; maxHp: number; currentHp: number; totalDamage: number; hpRate: number }
export interface BossBattleRewardResponse { sourceType: string; sourceId: number; xpAmount: number; coinAmount: number; claimedAt: string }
export interface BossBattleDashboard { battleId: number; guildId: number; bossName: string; difficulty: BossDifficulty; status: BossBattleStatus; maxHp: number; currentHp: number; totalDamage: number; hpRate: number; questCompletedCount: number; questTotalCount: number; commonConditionCompletedCount: number; commonConditionTotalCount: number; weeklyScore: number }
