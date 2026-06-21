import type { ItemType } from './item'

export interface EquippedShopItem {
  slotType: string
  equipped: boolean
  userItemId: number | null
  itemId: number | null
  name: string | null
  description: string | null
  imageUrl: string | null
  equippedAt: string | null
}

export interface ShopItem {
  itemId: number
  name: string
  description: string | null
  itemType: ItemType
  slotType: string | null
  price: number
  imageUrl: string | null
  defaultItem: boolean
  purchasable: boolean
  owned: boolean
  equipped: boolean
  userItemId: number | null
}

export interface ShopMainResponse { balance: number; equippedItems: EquippedShopItem[]; items: ShopItem[] }
export interface ShopItemListResponse { items: ShopItem[] }
export type ShopItemDetail = ShopItem
export interface PurchaseShopItemResponse {
  itemId: number
  userItemId: number
  name: string
  price: number
  balanceAfter: number
  purchasedAt: string
}
