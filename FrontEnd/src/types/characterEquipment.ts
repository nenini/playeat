export interface CharacterEquipment {
  slotType: string
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
