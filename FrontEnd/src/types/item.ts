export type ItemType = 'WEAPON' | 'ARMOR' | 'ACCESSORY' | 'BACKGROUND' | string
export interface UserItem { userItemId: number; itemId: number; itemName: string; itemType: ItemType; imageUrl?: string; equipped?: boolean; acquiredAt?: string }
export interface UserItemDetail extends UserItem { description?: string; price?: number; slotType?: string; metadata?: Record<string, unknown> }
export interface UserItemListResponse { items: UserItem[] }
