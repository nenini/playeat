import { normalizeItemKey } from './itemAssets'

export type AttackEffectType = 'DEFAULT' | 'STICK' | 'SWORD' | 'STAFF'

type AttackItemCandidate = {
  name?: string | null
  imageUrl?: string | null
  effectValue?: string | null
}

export function resolveAttackEffectType(item?: AttackItemCandidate | null): AttackEffectType {
  const key = normalizeItemKey(item?.effectValue) || normalizeItemKey(item?.imageUrl) || normalizeItemKey(item?.name)
  if (key === 'stick') return 'STICK'
  if (key === 'sword') return 'SWORD'
  if (key === 'staff') return 'STAFF'
  return 'DEFAULT'
}

export function attackEffectLabel(effectType: AttackEffectType) {
  if (effectType === 'STICK') return '강타'
  if (effectType === 'SWORD') return '검격'
  if (effectType === 'STAFF') return '마법 공격'
  return '공격'
}
