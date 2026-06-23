import { resolveApiAssetUrl } from './imageUrl'
import stickImage from '../assets/item/stick.png'
import swordImage from '../assets/item/sword.png'
import staffImage from '../assets/item/staff.png'
import crownImage from '../assets/item/crown.png'

type ItemAssetKey = 'stick' | 'sword' | 'staff' | 'crown'

type ItemAssetCandidate = {
  itemId?: number | null
  name?: string | null
  imageUrl?: string | null
  effectValue?: string | null
}

const itemAssetMap: Record<ItemAssetKey, string> = {
  stick: stickImage,
  sword: swordImage,
  staff: staffImage,
  crown: crownImage
}

const itemIdAssetMap: Record<number, ItemAssetKey> = {
  1: 'stick',
  2: 'sword',
  3: 'staff',
  4: 'crown'
}

export function normalizeItemKey(value: unknown): ItemAssetKey | null {
  if (typeof value !== 'string') return null

  const raw = value.trim()
  if (!raw || raw === 'null' || raw === 'undefined') return null

  const lower = raw.toLowerCase()
  const compact = lower.replace(/[\s_-]/g, '')

  if (lower.includes('나무막대기') || compact.includes('woodstick') || compact.includes('woodenstick') || compact.includes('stick')) return 'stick'
  if (lower.includes('지팡이') || compact.includes('staff')) return 'staff'
  if (lower.includes('왕관') || compact.includes('crown')) return 'crown'
  if (lower.includes('칼') || compact.includes('sword')) return 'sword'

  return null
}

export function resolveItemAssetByKey(key: unknown) {
  const normalizedKey = normalizeItemKey(key)
  if (normalizedKey) return itemAssetMap[normalizedKey]

  if (isDisplayableAssetUrl(key)) return resolveDisplayableAssetUrl(key)
  return null
}

export function resolveItemAsset(item: ItemAssetCandidate | null | undefined) {
  if (!item) return null

  const imageKey = normalizeItemKey(item.imageUrl)
  if (imageKey) return itemAssetMap[imageKey]

  if (isDisplayableAssetUrl(item.imageUrl)) return resolveDisplayableAssetUrl(item.imageUrl)

  const effectKey = normalizeItemKey(item.effectValue)
  if (effectKey) return itemAssetMap[effectKey]

  const nameKey = normalizeItemKey(item.name)
  if (nameKey) return itemAssetMap[nameKey]

  if (typeof item.itemId === 'number') {
    const itemIdKey = itemIdAssetMap[item.itemId]
    if (itemIdKey) return itemAssetMap[itemIdKey]
  }

  return null
}

function isDisplayableAssetUrl(value: unknown) {
  if (typeof value !== 'string') return false
  const path = value.trim()
  if (!path || path === 'null' || path === 'undefined') return false
  return /^https?:\/\//i.test(path)
    || path.startsWith('blob:')
    || path.startsWith('data:')
    || path.startsWith('/uploads/')
    || path.startsWith('/api/uploads/')
    || path.startsWith('/images/')
}

function resolveDisplayableAssetUrl(value: unknown) {
  if (typeof value !== 'string') return null
  const path = value.trim()
  if (!path || path === 'null' || path === 'undefined') return null
  if (/^https?:\/\//i.test(path) || path.startsWith('blob:') || path.startsWith('data:')) return path
  if (path.startsWith('/uploads/') || path.startsWith('/api/uploads/')) return resolveApiAssetUrl(path)
  if (path.startsWith('/images/')) return path
  return null
}
