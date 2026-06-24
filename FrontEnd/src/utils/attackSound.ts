const STORAGE_KEY = 'nyamnyam:attack-sound-enabled'

type OscillatorTypeName = OscillatorType
type AudioContextLike = AudioContext

let audioContext: AudioContextLike | null = null

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
    const context = getAudioContext()
    if (!context) return

    if (context.state === 'suspended') {
      void context.resume().catch(() => undefined)
    }

    const type = String(effectType || 'DEFAULT').toUpperCase()
    if (type === 'STICK') {
      playStickSound(context)
    } else if (type === 'SWORD') {
      playSwordSound(context)
    } else if (type === 'STAFF') {
      playStaffSound(context)
    } else {
      playDefaultSound(context)
    }
  } catch {
    // Sound is optional; autoplay or device restrictions should never block the UI.
  }
}

function getAudioContext() {
  if (audioContext) return audioContext
  const AudioContextConstructor = window.AudioContext || window.webkitAudioContext
  if (!AudioContextConstructor) return null
  audioContext = new AudioContextConstructor()
  return audioContext
}

function playDefaultSound(context: AudioContextLike) {
  const now = context.currentTime
  playTone(context, {
    type: 'sine',
    startFrequency: 120,
    endFrequency: 58,
    startTime: now,
    duration: 0.12,
    gain: 0.18
  })
}

function playStickSound(context: AudioContextLike) {
  const now = context.currentTime
  playTone(context, {
    type: 'triangle',
    startFrequency: 170,
    endFrequency: 72,
    startTime: now,
    duration: 0.16,
    gain: 0.22
  })
  playNoise(context, now + 0.01, 0.08, 0.12, 650)
}

function playSwordSound(context: AudioContextLike) {
  const now = context.currentTime
  playTone(context, {
    type: 'sawtooth',
    startFrequency: 980,
    endFrequency: 1800,
    startTime: now,
    duration: 0.12,
    gain: 0.08
  })
  playTone(context, {
    type: 'sine',
    startFrequency: 1320,
    endFrequency: 920,
    startTime: now + 0.07,
    duration: 0.11,
    gain: 0.11
  })
  playNoise(context, now, 0.08, 0.04, 2800)
}

function playStaffSound(context: AudioContextLike) {
  const now = context.currentTime
  playTone(context, {
    type: 'sine',
    startFrequency: 420,
    endFrequency: 880,
    startTime: now,
    duration: 0.3,
    gain: 0.12
  })
  playTone(context, {
    type: 'triangle',
    startFrequency: 920,
    endFrequency: 1480,
    startTime: now + 0.06,
    duration: 0.22,
    gain: 0.08
  })
  playTone(context, {
    type: 'sine',
    startFrequency: 1760,
    endFrequency: 2200,
    startTime: now + 0.18,
    duration: 0.09,
    gain: 0.06
  })
}

function playTone(
  context: AudioContextLike,
  options: {
    type: OscillatorTypeName
    startFrequency: number
    endFrequency: number
    startTime: number
    duration: number
    gain: number
  }
) {
  const oscillator = context.createOscillator()
  const gain = context.createGain()
  const endTime = options.startTime + options.duration

  oscillator.type = options.type
  oscillator.frequency.setValueAtTime(options.startFrequency, options.startTime)
  oscillator.frequency.exponentialRampToValueAtTime(Math.max(1, options.endFrequency), endTime)

  gain.gain.setValueAtTime(0.0001, options.startTime)
  gain.gain.exponentialRampToValueAtTime(options.gain, options.startTime + 0.012)
  gain.gain.exponentialRampToValueAtTime(0.0001, endTime)

  oscillator.connect(gain)
  gain.connect(context.destination)
  oscillator.start(options.startTime)
  oscillator.stop(endTime + 0.02)
}

function playNoise(context: AudioContextLike, startTime: number, duration: number, gainValue: number, filterFrequency: number) {
  const sampleRate = context.sampleRate
  const frameCount = Math.max(1, Math.floor(sampleRate * duration))
  const buffer = context.createBuffer(1, frameCount, sampleRate)
  const data = buffer.getChannelData(0)
  for (let index = 0; index < frameCount; index += 1) {
    data[index] = (Math.random() * 2 - 1) * (1 - index / frameCount)
  }

  const noise = context.createBufferSource()
  const filter = context.createBiquadFilter()
  const gain = context.createGain()
  const endTime = startTime + duration

  noise.buffer = buffer
  filter.type = 'bandpass'
  filter.frequency.setValueAtTime(filterFrequency, startTime)
  filter.Q.setValueAtTime(4, startTime)
  gain.gain.setValueAtTime(0.0001, startTime)
  gain.gain.exponentialRampToValueAtTime(gainValue, startTime + 0.01)
  gain.gain.exponentialRampToValueAtTime(0.0001, endTime)

  noise.connect(filter)
  filter.connect(gain)
  gain.connect(context.destination)
  noise.start(startTime)
  noise.stop(endTime + 0.02)
}

declare global {
  interface Window {
    webkitAudioContext?: typeof AudioContext
  }
}
