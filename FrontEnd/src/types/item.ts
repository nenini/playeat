export type ItemType = 'EQUIPMENT' | 'CONSUMABLE' | string
export interface UserItem {
  userItemId: number
  itemId: number
  name: string
  description: string | null
  itemType: ItemType
  slotType: string | null
  imageUrl: string | null
  acquiredType: string
  acquiredAt: string
  equipped: boolean
}
export type UserItemDetail = UserItem
export interface UserItemListResponse { items: UserItem[] }
