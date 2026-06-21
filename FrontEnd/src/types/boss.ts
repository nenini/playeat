export type BossDifficulty = 'EASY' | 'NORMAL' | 'HARD'
export interface BossCondition { conditionId: number; title: string; description?: string; targetType?: string; thresholdValue?: number; thresholdUnit?: string; targetValue?: number; requiredDays?: number; unit?: string; sortOrder?: number }
export interface BossSummary { bossId: number; name: string; description?: string; difficulty: BossDifficulty; maxHp: number; imageUrl?: string; rewardExp: number; rewardCoin: number; commonConditions: BossCondition[] }
export interface CurrentBossesResponse { seasonId: number; seasonName: string; startsAt: string; endsAt: string; bosses: BossSummary[] }
export interface BossDetail extends BossSummary { seasonId: number; status: string; startsAt: string; endsAt: string }
