import type { CharacterEquipmentListResponse, EquipCharacterItemRequest } from '../../types/characterEquipment'
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
