const genderLabels: Record<string, string> = {
  MALE: '남성',
  FEMALE: '여성',
  OTHER: '기타',
  NONE: '선택 안 함',
  UNKNOWN: '선택 안 함',
  NOT_SELECTED: '선택 안 함'
}

const healthGoalLabels: Record<string, string> = {
  LOSE_WEIGHT: '체중 감량',
  WEIGHT_LOSS: '체중 감량',
  GAIN_WEIGHT: '체중 증량',
  MUSCLE_GAIN: '근육 증가',
  MAINTAIN: '유지',
  HEALTH_MANAGEMENT: '건강 관리'
}

export function genderLabel(value: unknown) {
  return labelFromMap(value, genderLabels)
}

export function healthGoalLabel(value: unknown) {
  return labelFromMap(value, healthGoalLabels)
}

function labelFromMap(value: unknown, map: Record<string, string>) {
  if (typeof value !== 'string') return '-'
  const key = value.trim().toUpperCase()
  return key ? map[key] || '-' : '-'
}
