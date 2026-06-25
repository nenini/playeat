import background1 from '../assets/background/background1.png'
import background2 from '../assets/background/background2.png'
import background3 from '../assets/background/background3.png'

type BackgroundAssetKey = 'BACKGROUND_1' | 'BACKGROUND_2' | 'BACKGROUND_3'

type BackgroundCandidate = {
  name?: string | null
  imageUrl?: string | null
  effectValue?: string | null
}

const backgroundAssetMap: Record<BackgroundAssetKey, string> = {
  BACKGROUND_1: background1,
  BACKGROUND_2: background2,
  BACKGROUND_3: background3
}

export function normalizeBackgroundKey(value: unknown): BackgroundAssetKey | null {
  if (typeof value !== 'string') return null
  const raw = value.trim()
  if (!raw || raw === 'null' || raw === 'undefined') return null

  const compact = raw.toLowerCase().replace(/[\s_-]/g, '')
  if (compact.includes('background1') || raw.includes('푸른 숲')) return 'BACKGROUND_1'
  if (compact.includes('background2') || raw.includes('달콤한 월드')) return 'BACKGROUND_2'
  if (compact.includes('background3') || raw.includes('모험 캠프')) return 'BACKGROUND_3'
  return null
}

export function resolveBackgroundAsset(item: BackgroundCandidate | null | undefined) {
  const key = normalizeBackgroundKey(item?.effectValue) || normalizeBackgroundKey(item?.imageUrl) || normalizeBackgroundKey(item?.name)
  return key ? backgroundAssetMap[key] : null
}

export function resolveBackgroundAssetByKey(value: unknown) {
  const key = normalizeBackgroundKey(value)
  return key ? backgroundAssetMap[key] : null
}
