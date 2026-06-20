import type { PurchaseShopItemResponse, ShopItemDetail, ShopItemListResponse, ShopItemParams, ShopMainResponse } from '../../types/shop'
import { apiRequest } from './client'

export const shopApi = {
  getShopMain(): Promise<ShopMainResponse> {
    return apiRequest('/v1/shop')
  },
  getShopItems(params: ShopItemParams = {}): Promise<ShopItemListResponse> {
    const query = new URLSearchParams()
    if (params.page !== undefined) query.set('page', String(params.page))
    if (params.size !== undefined) query.set('size', String(params.size))
    if (params.itemType) query.set('itemType', params.itemType)
    if (params.keyword?.trim()) query.set('keyword', params.keyword.trim())
    const suffix = query.size ? `?${query}` : ''
    return apiRequest(`/v1/shop/items${suffix}`)
  },
  getShopItem(itemId: number): Promise<ShopItemDetail> {
    return apiRequest(`/v1/shop/items/${itemId}`)
  },
  purchaseShopItem(itemId: number): Promise<PurchaseShopItemResponse> {
    return apiRequest(`/v1/shop/items/${itemId}/purchase`, { method: 'POST' })
  }
}
