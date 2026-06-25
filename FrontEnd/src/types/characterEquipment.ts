export type CharacterEquipmentSlot = 'HAND' | 'HEAD' | 'CHARACTER' | 'BACKGROUND' | string

export interface CharacterEquipment {
  slotType: CharacterEquipmentSlot
  equipped: boolean
  userItemId: number | null
  itemId: number | null
  name: string | null
  description: string | null
  imageUrl: string | null
  effectValue?: string | null
  equippedAt: string | null
}

export interface CharacterEquipmentListResponse {
  characterId: number
  equipments: CharacterEquipment[]
}

export interface EquipCharacterItemRequest {
  userItemId: number
}
