import type { CharacterEquipment, CharacterEquipmentListResponse, EquipCharacterItemRequest } from '../../types/characterEquipment'
import { apiRequest } from './client'

export const characterEquipmentApi = {
  getMyEquipments(): Promise<CharacterEquipmentListResponse> {
    return apiRequest('/v1/characters/me/equipments')
  },
  equipItem(payload: EquipCharacterItemRequest): Promise<CharacterEquipmentListResponse> {
    return apiRequest('/v1/characters/me/equipments', { method: 'POST', body: payload })
  },
  unequipItem(slotType: string): Promise<CharacterEquipmentListResponse> {
    return apiRequest(`/v1/characters/me/equipments/${encodeURIComponent(slotType)}`, { method: 'DELETE' })
  }
}

export function equipmentIconId(item: Pick<CharacterEquipment, 'name' | 'imageUrl'> | null | undefined) {
  const source = `${item?.name ?? ''} ${item?.imageUrl ?? ''}`.toLowerCase()
  if (source.includes('crown') || source.includes('왕관')) return 'crown'
  if (source.includes('sword') || source.includes('칼')) return 'sword'
  if (source.includes('staff') || source.includes('지팡이')) return 'staff'
  if (source.includes('wood-stick') || source.includes('나무막대기')) return 'stick'
  return null
}

export function equipmentImageUrl(item: Pick<CharacterEquipment, 'name' | 'imageUrl'> | null | undefined) {
  return equipmentIconId(item) ? null : item?.imageUrl || null
}
