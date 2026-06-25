import dragonBackground from '../assets/dragon/dragon-background.png'
import dragonClear from '../assets/dragon/dragon_clear.png'
import nbody from '../assets/dragon/nbody.png'
import nwing from '../assets/dragon/nwing.png'
import golemAlive from '../assets/golem/golem-alive.png'
import golemClear from '../assets/golem/golem-clear.png'
import golemBackground from '../assets/golem/golem-background.png'
import knightAlive from '../assets/knight/knight-alive.png'
import knightClear from '../assets/knight/knight-clear.png'
import knightBackground from '../assets/knight/knight-background.png'

export type BossType = 'DRAGON' | 'GOLEM' | 'KNIGHT'

export interface BossAssetSet {
  type: BossType
  background: string
  alive: string
  clear: string
  wing?: string
}

export const bossAssets: Record<BossType, BossAssetSet> = {
  DRAGON: { type: 'DRAGON', background: dragonBackground, alive: nbody, clear: dragonClear, wing: nwing },
  GOLEM: { type: 'GOLEM', background: golemBackground, alive: golemAlive, clear: golemClear },
  KNIGHT: { type: 'KNIGHT', background: knightBackground, alive: knightAlive, clear: knightClear }
}

export function resolveBossType(value?: string | null, difficulty?: string | null, name?: string | null): BossType {
  const text = `${value || ''} ${difficulty || ''} ${name || ''}`.toUpperCase()
  if (text.includes('GOLEM') || text.includes('골렘') || text.includes('NORMAL')) return 'GOLEM'
  if (text.includes('KNIGHT') || text.includes('기사') || text.includes('해골') || text.includes('HARD')) return 'KNIGHT'
  return 'DRAGON'
}

export function bossAssetsFor(value?: string | null, difficulty?: string | null, name?: string | null): BossAssetSet {
  return bossAssets[resolveBossType(value, difficulty, name)]
}
