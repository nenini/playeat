import type { PurchaseShopItemResponse, ShopItemDetail, ShopItemListResponse, ShopMainResponse } from '../../types/shop'
import { apiRequest } from './client'

export const shopApi = {
  getShop(): Promise<ShopMainResponse> {
    return apiRequest('/v1/shop')
  },
  getShopItems(): Promise<ShopItemListResponse> {
    return apiRequest('/v1/shop/items')
  },
  getShopItem(itemId: number): Promise<ShopItemDetail> {
    return apiRequest(`/v1/shop/items/${itemId}`)
  },
  purchaseItem(itemId: number): Promise<PurchaseShopItemResponse> {
    return apiRequest(`/v1/shop/items/${itemId}/purchase`, { method: 'POST' })
  }
}
