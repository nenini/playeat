import type { BossDifficulty } from './boss'

export type BossBattleStatus = 'IN_PROGRESS' | 'DEFEATED' | 'EXPIRED' | 'FAILED'
export interface CreateBossBattleRequest { bossId: number; difficulty?: BossDifficulty }
export interface BossBattleSummary { battleId: number; guildId: number; bossId: number; bossName?: string; difficulty?: BossDifficulty; status: BossBattleStatus; maxHp: number; currentHp: number; startedAt?: string; endedAt?: string }
export interface BossBattleDetail extends BossBattleSummary { totalDamage?: number; rewardClaimed?: boolean; commonConditionsVerified?: boolean }
export interface CommonConditionVerifyItem { conditionId?: number; title?: string; verified: boolean; currentValue?: number; targetValue?: number; unit?: string }
export interface CommonConditionVerifyResponse { battleId: number; verified: boolean; conditions: CommonConditionVerifyItem[] }
export interface BossBattleHp { battleId: number; maxHp: number; currentHp: number; totalDamage?: number; hpRate?: number; status?: BossBattleStatus }
export interface BossBattleRewardResponse { battleId: number; rewardExp?: number; rewardCoin?: number; claimedAt?: string }
export interface BossBattleDashboard { battleId: number; guildId?: number; bossName?: string; status?: BossBattleStatus; maxHp?: number; currentHp?: number; totalDamage?: number; completedQuestCount?: number; totalQuestCount?: number }
