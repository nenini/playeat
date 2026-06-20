export type BossDifficulty = 'EASY' | 'NORMAL' | 'HARD'
export interface BossSummary { bossId: number; name: string; description?: string; difficulty?: BossDifficulty; imageUrl?: string; baseHp?: number; rewardExp?: number; rewardCoin?: number; startsAt?: string; endsAt?: string }
export interface CurrentBossesResponse { bosses: BossSummary[]; seasonName?: string; seasonStartsAt?: string; seasonEndsAt?: string }
export interface BossCondition { conditionId?: number; title: string; description?: string; metricType?: string; comparisonType?: string; thresholdValue?: number; unit?: string }
export interface BossDetail extends BossSummary { commonConditions?: BossCondition[]; createdAt?: string; updatedAt?: string }
