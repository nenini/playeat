import type { CharacterEquipment, CharacterEquipmentListResponse, EquipCharacterItemRequest } from '../../types/characterEquipment'
import { resolveItemAsset, normalizeItemKey } from '../../utils/itemAssets'
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

type EquipmentDisplayCandidate = Pick<CharacterEquipment, 'name' | 'imageUrl'> & {
  itemId?: number | null
  effectValue?: string | null
}

export function equipmentIconId(item: EquipmentDisplayCandidate | null | undefined) {
  return normalizeItemKey(item?.imageUrl) || normalizeItemKey(item?.effectValue) || normalizeItemKey(item?.name)
}

export function equipmentImageUrl(item: EquipmentDisplayCandidate | null | undefined) {
  const imageUrl = item?.imageUrl?.trim()
  if (imageUrl && ['NYAMNYAM', 'PENGUIN', 'DOG'].includes(imageUrl.toUpperCase())) return null
  const asset = resolveItemAsset(item)
  if (asset) return asset
  return null
}
