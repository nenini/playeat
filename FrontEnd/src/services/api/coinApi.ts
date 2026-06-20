import type { CoinBalance, CoinTransaction, CoinTransactionListResponse, CoinTransactionParams } from '../../types/coin'
import { apiRequest } from './client'

export const coinApi = {
  getMyCoinBalance(): Promise<CoinBalance> {
    return apiRequest('/v1/coins/me')
  },
  async getMyCoinTransactions(params: CoinTransactionParams = {}): Promise<CoinTransaction[]> {
    const query = new URLSearchParams()
    if (params.page !== undefined) query.set('page', String(params.page))
    if (params.size !== undefined) query.set('size', String(params.size))
    if (params.transactionType) query.set('transactionType', params.transactionType)
    const suffix = query.size ? `?${query}` : ''
    const response = await apiRequest<CoinTransactionListResponse>(`/v1/coins/me/transactions${suffix}`)
    return response.transactions
  }
}
