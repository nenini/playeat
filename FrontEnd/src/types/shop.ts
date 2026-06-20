import type { ItemType } from './item'

export interface EquippedShopItem { userItemId?: number; itemId: number; itemName: string; slotType?: string; imageUrl?: string }
export interface ShopItem { itemId: number; name: string; description?: string; itemType: ItemType; slotType?: string; price: number; imageUrl?: string; owned?: boolean; equipped?: boolean; purchasable?: boolean }
export interface ShopMainResponse { balance: number; equippedItems: EquippedShopItem[]; items: ShopItem[] }
export interface ShopItemParams { page?: number; size?: number; itemType?: ItemType; keyword?: string }
export interface ShopItemListResponse { items: ShopItem[]; page?: number; size?: number; hasNext?: boolean; totalElements?: number; totalPages?: number }
export interface ShopItemDetail extends ShopItem { createdAt?: string; updatedAt?: string }
export interface PurchaseShopItemResponse { userItemId?: number; itemId: number; itemName?: string; price?: number; balance?: number; purchasedAt?: string }
