import basicSound from '../assets/sound/basic.mp3'
import staffSound from '../assets/sound/staff.mp3'
import stickSound from '../assets/sound/stick.mp3'
import swordSound from '../assets/sound/sword.mp3'

const STORAGE_KEY = 'nyamnyam:attack-sound-enabled'

const soundMap = {
  DEFAULT: basicSound,
  STICK: stickSound,
  SWORD: swordSound,
  STAFF: staffSound
} as const

type AttackSoundType = keyof typeof soundMap

export function getAttackSoundEnabled(): boolean {
  if (typeof localStorage === 'undefined') return true
  return localStorage.getItem(STORAGE_KEY) !== 'false'
}

export function setAttackSoundEnabled(enabled: boolean): void {
  if (typeof localStorage === 'undefined') return
  localStorage.setItem(STORAGE_KEY, String(enabled))
}

export function toggleAttackSound(): boolean {
  const next = !getAttackSoundEnabled()
  setAttackSoundEnabled(next)
  return next
}

export function playAttackSound(effectType: string): void {
  if (!getAttackSoundEnabled()) return

  try {
    const source = soundMap[normalizeSoundType(effectType)]
    const audio = new Audio(source)
    audio.preload = 'auto'
    audio.volume = 0.72
    audio.currentTime = 0
    void audio.play().catch(() => undefined)
  } catch {
    // Sound should never block battle UI interactions.
  }
}

function normalizeSoundType(effectType: string): AttackSoundType {
  const value = String(effectType || 'DEFAULT').toUpperCase()
  if (value === 'STICK' || value === 'SWORD' || value === 'STAFF') return value
  return 'DEFAULT'
}
