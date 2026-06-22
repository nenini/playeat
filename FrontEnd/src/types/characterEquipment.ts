export type CharacterEquipmentSlot = 'HAND' | 'HEAD' | string

export interface CharacterEquipment {
  slotType: CharacterEquipmentSlot
  equipped: boolean
  userItemId: number | null
  itemId: number | null
  name: string | null
  description: string | null
  imageUrl: string | null
  equippedAt: string | null
}

export interface CharacterEquipmentListResponse {
  characterId: number
  equipments: CharacterEquipment[]
}

export interface EquipCharacterItemRequest {
  userItemId: number
}
