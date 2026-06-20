export type QuestStatus = 'IN_PROGRESS' | 'COMPLETED' | 'REWARDED'
export interface QuestSummary { questId: number; userId?: number; nickname?: string; title: string; questType?: string; targetValue?: number; currentValue?: number; unit?: string; damage?: number; rewardExp?: number; rewardCoin?: number; status: QuestStatus }
export interface BattleQuestListResponse { battleId?: number; quests: QuestSummary[]; totalCount?: number; completedCount?: number }
export interface QuestContribution { userId: number; nickname: string; completedQuestCount?: number; totalDamage?: number; contributionRate?: number }
export interface QuestContributionListResponse { battleId?: number; contributions: QuestContribution[] }
export interface QuestGenerateResponse { battleId?: number; generatedCount?: number; quests?: QuestSummary[] }
export interface MyQuestResponse extends QuestSummary { progressRate?: number; completedAt?: string; rewardedAt?: string }
export interface QuestDetail extends MyQuestResponse { description?: string; conditionCategory?: string; metricType?: string; comparisonType?: string; aggregationType?: string; evaluationScope?: string; thresholdValue?: number; thresholdMinValue?: number; thresholdMaxValue?: number; thresholdUnit?: string }
export interface QuestRewardResponse { questId: number; rewardExp?: number; rewardCoin?: number; status?: QuestStatus; rewardedAt?: string }
export interface QuestVerifyResponse { questId: number; verified: boolean; currentValue?: number; targetValue?: number; status?: QuestStatus; damage?: number }
