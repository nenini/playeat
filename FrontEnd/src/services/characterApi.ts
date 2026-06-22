import { apiRequest } from './api'
import type { Stage } from './mock/nyamnyamMock'

export interface CharacterResponse {
  characterId: number
  name: string
  level: number
  xp: number
  requiredXp: number
  stage: string
  mood: string
  streakDays: number
  bestStreakDays?: number
  appearanceType?: string
}

export interface CharacterEquipmentResponse {
  equipments?: Array<{
    userItemId: number
    itemId: number
    slotType: string
    itemName: string
    imageUrl?: string
  }>
}

export const characterApi = {
  getMyCharacter() {
    return apiRequest<CharacterResponse>('/v1/characters/me')
  },

  getMe() {
    return apiRequest<CharacterResponse>('/v1/characters/me')
  },

  updateName(name: string) {
    return apiRequest('/v1/characters/me/name', {
      method: 'PATCH',
      body: { name }
    })
  },

  getXpHistory() {
    return apiRequest('/v1/characters/me/xp-history')
  },

  getEquipments() {
    return apiRequest<CharacterEquipmentResponse>('/v1/characters/me/equipments')
  }
}

export function stageFromBackend(stage?: string): Stage {
  const value = String(stage || '').toLowerCase()
  if (value.includes('adult')) return 'adult'
  if (value.includes('egg')) return 'egg'
  return 'chick'
}
