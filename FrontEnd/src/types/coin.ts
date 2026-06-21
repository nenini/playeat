export interface CoinBalance { userId: number; balance: number }
export interface CoinTransactionParams { page?: number; size?: number }
export interface CoinTransaction { transactionId: number; transactionType: string; amount: number; balanceAfter: number; sourceType?: string; sourceId?: number; description?: string; createdAt: string }
export interface CoinTransactionListResponse { transactions: CoinTransaction[]; page?: number; size?: number; hasNext?: boolean }
