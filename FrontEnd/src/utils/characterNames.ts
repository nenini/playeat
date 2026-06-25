const characterNameMap: Record<string, string> = {
  NYAMNYAM: '짹짹이',
  DEFAULT: '짹짹이',
  DOG: '멍멍이',
  PENGUIN: '뒤뚱이'
}

export function characterDisplayName(value?: string | null) {
  const key = String(value || 'NYAMNYAM').trim().toUpperCase()
  return characterNameMap[key] || characterNameMap.NYAMNYAM
}
