export interface GuildRanking { rank: number; guildId: number; guildName: string; myGuild: boolean; weeklyScore: number; recordRate: number; questCompletionRate: number; bossDamage: number }
export interface BossRanking { rank: number; guildId: number; guildName: string; myGuild: boolean; status: string; maxHp: number; currentHp: number; totalDamage: number; hpRate: number; startedAt?: string; endedAt?: string }
export interface GuildRankingResponse { weekStartDate: string; weekEndDate: string; myGuildRank?: number; rankings: GuildRanking[] }
export interface BossRankingResponse { bossId?: number; bossName?: string; rankings: BossRanking[]; myGuildRank?: BossRanking }
