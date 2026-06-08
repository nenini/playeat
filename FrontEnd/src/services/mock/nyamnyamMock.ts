export type PageId = 'home' | 'meals' | 'analyze' | 'boss' | 'guild' | 'shop' | 'mypage'
export type MealKindId = 'breakfast' | 'lunch' | 'snack' | 'dinner'
export type Stage = 'egg' | 'chick' | 'adult'

export interface Food {
  id: string
  name: string
  emoji: string
  src: string
  per: string
  kcal: number
  p: number
  c: number
  f: number
  unit: string
  presets: number[]
}

export interface MealLog {
  id: string
  foodId: string
  mealKind: MealKindId
  qty: number
}

export const pages: Array<{ id: PageId, label: string, icon: string, path: string }> = [
  { id: 'home', label: '메인', icon: 'home', path: '/' },
  { id: 'meals', label: '식단', icon: 'meal', path: '/meals' },
  { id: 'analyze', label: '분석', icon: 'analyze', path: '/analyze' },
  { id: 'boss', label: '보스', icon: 'boss', path: '/boss' },
  { id: 'guild', label: '길드', icon: 'guild', path: '/guild' },
  { id: 'shop', label: '상점', icon: 'bag', path: '/shop' }
]

export const foodDb: Food[] = [
  { id: 'egg-boil', name: '삶은 계란', emoji: '🥚', src: '식약처', per: '개', kcal: 78, p: 6, c: 0.5, f: 5, unit: '개', presets: [1, 2, 3] },
  { id: 'egg-roll', name: '계란말이', emoji: '🍳', src: '식약처', per: '100g', kcal: 230, p: 14, c: 3, f: 16, unit: 'g', presets: [50, 100, 150] },
  { id: 'oatmeal', name: '오트밀', emoji: '🥣', src: '식약처', per: '100g', kcal: 380, p: 13, c: 67, f: 7, unit: 'g', presets: [40, 80, 100] },
  { id: 'wb-bread', name: '통밀빵', emoji: '🍞', src: '식약처', per: '조각', kcal: 90, p: 4, c: 16, f: 1, unit: '조각', presets: [1, 2] },
  { id: 'banana', name: '바나나', emoji: '🍌', src: '식약처', per: '개', kcal: 89, p: 1, c: 23, f: 0, unit: '개', presets: [1, 2] },
  { id: 'milk', name: '우유', emoji: '🥛', src: '식약처', per: '200ml', kcal: 124, p: 6, c: 10, f: 7, unit: 'ml', presets: [200, 250, 500] },
  { id: 'rice', name: '밥', emoji: '🍚', src: '식약처', per: '공기 210g', kcal: 310, p: 6, c: 67, f: 1, unit: 'g', presets: [150, 210, 280] },
  { id: 'kimchi-stew', name: '김치찌개', emoji: '🍲', src: '식약처', per: '1인분', kcal: 380, p: 18, c: 22, f: 22, unit: '인분', presets: [1] },
  { id: 'doenjang', name: '된장찌개', emoji: '🥘', src: '식약처', per: '1인분', kcal: 290, p: 16, c: 18, f: 14, unit: '인분', presets: [1] },
  { id: 'bibimbap', name: '비빔밥', emoji: '🍱', src: '식약처', per: '1인분', kcal: 560, p: 18, c: 84, f: 12, unit: '인분', presets: [1] },
  { id: 'rame', name: '라면', emoji: '🍜', src: '식약처', per: '1봉지', kcal: 510, p: 11, c: 80, f: 16, unit: '봉지', presets: [1] },
  { id: 'chicken-breast', name: '닭가슴살 (찐)', emoji: '🍗', src: '식약처', per: '100g', kcal: 165, p: 31, c: 0, f: 4, unit: 'g', presets: [80, 100, 150] },
  { id: 'tofu', name: '두부', emoji: '⬜', src: '식약처', per: '100g', kcal: 76, p: 8, c: 2, f: 4, unit: 'g', presets: [100, 200, 300] },
  { id: 'protein-shake', name: '단백질 쉐이크', emoji: '🥤', src: '식약처', per: '1잔', kcal: 130, p: 25, c: 5, f: 1, unit: '잔', presets: [1] },
  { id: 'salad', name: '샐러드', emoji: '🥗', src: '식약처', per: '100g', kcal: 60, p: 2, c: 8, f: 2, unit: 'g', presets: [100, 150, 200] },
  { id: 'tomato', name: '토마토', emoji: '🍅', src: '식약처', per: '개', kcal: 22, p: 1, c: 5, f: 0, unit: '개', presets: [1, 2] },
  { id: 'spinach', name: '시금치', emoji: '🥬', src: '식약처', per: '100g', kcal: 23, p: 3, c: 4, f: 0, unit: 'g', presets: [50, 100] },
  { id: 'cabbage', name: '양배추', emoji: '🥬', src: '식약처', per: '100g', kcal: 25, p: 1, c: 6, f: 0, unit: 'g', presets: [50, 100] },
  { id: 'cu-salad', name: '닭가슴살 샐러드', emoji: '🥗', src: 'CU PB', per: '1팩', kcal: 230, p: 24, c: 12, f: 7, unit: '팩', presets: [1] },
  { id: 'kimbap', name: '삼각김밥 (참치)', emoji: '🍙', src: 'GS25', per: '1개', kcal: 220, p: 6, c: 36, f: 5, unit: '개', presets: [1, 2] },
  { id: 'protein-bar', name: '단백질 바', emoji: '🍫', src: 'CU PB', per: '1개', kcal: 180, p: 15, c: 14, f: 7, unit: '개', presets: [1] },
  { id: 'yogurt', name: '그릭 요거트', emoji: '🍦', src: '식약처', per: '100g', kcal: 97, p: 9, c: 4, f: 5, unit: 'g', presets: [100, 200] },
  { id: 'apple', name: '사과', emoji: '🍎', src: '식약처', per: '개', kcal: 95, p: 0, c: 25, f: 0, unit: '개', presets: [1] },
  { id: 'nuts', name: '견과류 믹스', emoji: '🥜', src: '식약처', per: '30g', kcal: 180, p: 6, c: 6, f: 16, unit: 'g', presets: [15, 30] }
]

export const favoriteIds = ['oatmeal', 'kimchi-stew', 'cu-salad', 'banana', 'protein-shake', 'salad']

export const mealKinds: Array<{ id: MealKindId, label: string, emoji: string, window: string }> = [
  { id: 'breakfast', label: '아침', emoji: '☀️', window: '06–10' },
  { id: 'lunch', label: '점심', emoji: '🌤', window: '11–14' },
  { id: 'snack', label: '간식', emoji: '🍪', window: '14–17' },
  { id: 'dinner', label: '저녁', emoji: '🌙', window: '17–22' }
]

export const goalDefaults = { kcal: 2000, p: 90, c: 260, f: 65, sodium: 2300, veggies: 2 }

export const boss = {
  id: 'sugar-dragon',
  name: '당분 드래곤',
  dDay: 3,
  baseHP: 100,
  conquerXP: 800,
  description: '최근 7일 당류 섭취가 권장치를 초과해 출현. 매일 당류 ≤ 50g 유지 시 격파.'
}

export const bossDiffs = [
  { id: 'easy', label: 'EASY', ko: '쉬움', desc: 'HP 절반 · 격파 조건 완화', reward: '기본 보상', rewardMult: '×1', tone: 'ok', hp: 50 },
  { id: 'normal', label: 'NORMAL', ko: '보통', desc: '기본 HP · 일반 격파 조건', reward: '1.5× 보상', rewardMult: '×1.5', tone: 'accent', hp: 100 },
  { id: 'hard', label: 'HARD', ko: '어려움', desc: 'HP 2배 · 격파 조건 강화', reward: '3× 보상', rewardMult: '×3', tone: 'bad', hp: 200 }
] as const

export const npcCoaches = [
  { id: 'knight', name: '기사단장', color: '#d6c6a8', tagline: '정중·도도한 기사 말투', glyph: '⚔️' },
  { id: 'trainer', name: '접령좌', color: '#cfe2d2', tagline: '직설적 피트니스 코치', glyph: '💪' },
  { id: 'healer', name: '힐러', color: '#f4d4d4', tagline: '따뜻하고 위로적', glyph: '🌷' },
  { id: 'wizard', name: '마법사', color: '#cfd5ec', tagline: '데이터·과학 중심', glyph: '🔮' },
  { id: 'jester', name: '장난꾼이', color: '#f6e3b8', tagline: '장난꾼 친구 톤', glyph: '🎭' },
  { id: 'elder', name: '앤아이 어른', color: '#e5dccc', tagline: '구수·친근한 동네 어른', glyph: '🍵' }
]

export const guildMembers = [
  { id: 'me', name: '지은', role: '단백질 담당', lv: 7, online: true },
  { id: 'minjun', name: '민준', role: '길드장 · 단백질', lv: 12, online: true },
  { id: 'jiyoung', name: '지영', role: '채소 담당', lv: 14, online: true },
  { id: 'youngsook', name: '영숙', role: '나트륨 담당', lv: 9, online: true },
  { id: 'yein', name: '예인', role: '✓ 전문가', lv: 18, online: false },
  { id: 'taehyung', name: '태형', role: '기록 담당', lv: 5, online: false }
]

export const seedChat = [
  { id: 'minjun', name: '민준 · 길드장', time: '09:12', text: '아침 다들 챙기셨나요? ☀️ 보스 격파까지 D-3' },
  { id: 'sys', name: '시스템', time: '09:14', text: '지영님이 "채소 3종 기록"을 완료했어요! 보스 HP −8', system: true },
  { id: 'jiyoung', name: '지영', time: '09:15', text: '아침에 토마토 + 시금치 + 양배추 다 해치웠어요 🥬' },
  { id: 'yein', name: '예인 · ✓ 전문가', time: '09:22', text: '채소 3종이면 사실상 단백질·비타민까지 같이 가는 좋은 조합이에요!' },
  { id: 'youngsook', name: '영숙', time: '12:30', text: '저는 오늘 짠 거 다 끊고 두부 위주로 갈래요~ 같이 응원해줘요^^' }
]

export const guildList = [
  { id: 1, name: '잘먹잘싸', members: 6, max: 30, score: 2840, rank: 1, focus: '균형 식단', emoji: '잘' },
  { id: 2, name: '단백질 부대', members: 12, max: 20, score: 2710, rank: 2, focus: '단백질 강화', emoji: '단' },
  { id: 3, name: '아침 챔피언즈', members: 8, max: 15, score: 2620, rank: 3, focus: '아침 식단', emoji: '아' },
  { id: 4, name: '채소 사랑', members: 23, max: 30, score: 2400, rank: 4, focus: '채식 위주', emoji: '채' },
  { id: 5, name: '저염 라이프', members: 5, max: 10, score: 2300, rank: 5, focus: '나트륨 관리', emoji: '저' }
]

export const shopItems = [
  { id: 'stick', slot: 'hand', name: '나무막대기', desc: '그냥 주운 나뭇가지', price: 0, ownedDefault: true },
  { id: 'sword', slot: 'hand', name: '칼', desc: '번쩍이는 강철 검', price: 500, ownedDefault: false },
  { id: 'staff', slot: 'hand', name: '지팡이', desc: '마법의 기운이 흐른다', price: 900, ownedDefault: false },
  { id: 'crown', slot: 'head', name: '왕관', desc: '길드 최고의 명예', price: 1500, ownedDefault: false }
] as const

export const badges = [
  { id: 'first', name: '첫 기록', desc: '첫 번째 식단 기록', emoji: '🍽️', earned: true, date: '04·20' },
  { id: 'streak3', name: '3일 연속', desc: '연속 3일 기록 달성', emoji: '🔥', earned: true, date: '04·23' },
  { id: 'streak7', name: '7일 연속', desc: '연속 7일 기록 달성', emoji: '🔥', earned: true, date: '05·01' },
  { id: 'veggie', name: '채식 전사', desc: '채소 3종 3일 연속', emoji: '🥬', earned: true, date: '05·10' },
  { id: 'boss1', name: '보스 사냥꾼', desc: '첫 번째 보스 격파', emoji: '⚔️', earned: true, date: '05·10' },
  { id: 'guild', name: '길드원', desc: '길드에 가입', emoji: '🛡️', earned: true, date: '04·21' },
  { id: 'protein', name: '단백질 챔피언', desc: '단백질 목표 7일 연속', emoji: '💪', earned: false },
  { id: 'perfect', name: '퍼펙트 데이', desc: '4끼 + 전 목표 달성', emoji: '⭐', earned: false }
]

export const seedLogs: MealLog[] = [
  { id: 'l1', foodId: 'oatmeal', mealKind: 'breakfast', qty: 80 },
  { id: 'l2', foodId: 'banana', mealKind: 'breakfast', qty: 1 },
  { id: 'l3', foodId: 'rice', mealKind: 'lunch', qty: 210 },
  { id: 'l4', foodId: 'kimchi-stew', mealKind: 'lunch', qty: 1 },
  { id: 'l5', foodId: 'protein-shake', mealKind: 'snack', qty: 1 }
]

const findFood = (id: string) => foodDb.find((f) => f.id === id)
const perBase = (food: Food) => Number(String(food.per).match(/^(\d+)/)?.[1] ?? 1)

export function totalsFor(logs: MealLog[]) {
  return logs.reduce((acc, entry) => {
    const food = findFood(entry.foodId)
    if (!food) return acc
    const ratio = entry.qty / perBase(food)
    acc.kcal += food.kcal * ratio
    acc.p += food.p * ratio
    acc.c += food.c * ratio
    acc.f += food.f * ratio
    return acc
  }, { kcal: 0, p: 0, c: 0, f: 0 })
}

export function recordsByKind(logs: MealLog[]) {
  return mealKinds.reduce((acc, kind) => {
    acc[kind.id] = logs.filter((log) => log.mealKind === kind.id)
    return acc
  }, {} as Record<MealKindId, MealLog[]>)
}

export function veggieCount(logs: MealLog[]) {
  const veggieIds = new Set(['salad', 'tomato', 'spinach', 'cabbage', 'cu-salad'])
  return new Set(logs.filter((log) => veggieIds.has(log.foodId)).map((log) => log.foodId)).size
}

export function healthScore(totals: ReturnType<typeof totalsFor>) {
  const kcalScore = Math.max(0, 40 - Math.abs(totals.kcal - goalDefaults.kcal * 0.75) / 35)
  const proteinScore = Math.min(30, totals.p / goalDefaults.p * 30)
  const balanceScore = Math.min(30, totals.c > 0 && totals.f > 0 ? 24 : 12)
  return Math.max(48, Math.min(96, Math.round(kcalScore + proteinScore + balanceScore)))
}

export function dayLabel(day = '2026-05-15') {
  const [, month, date] = day.split('-')
  return `2026년 ${Number(month)}월 ${Number(date)}일 · 금요일`
}

export function coachSpeak(coachId: string, logs: MealLog[]) {
  const totals = totalsFor(logs)
  const recordsToday = logs.length
  const lowProtein = totals.p < 60
  const overSodium = true
  const veggies = veggieCount(logs)
  if (coachId === 'trainer') return lowProtein ? `"P ${Math.round(totals.p)}g. 부족. ${Math.max(0, 60 - Math.round(totals.p))}g 더. 닭가슴살 1팩 ㄱ.💪"` : '"오늘 평균. 더 가즈아. 단백질 우선."'
  if (coachId === 'healer') return recordsToday >= 2 ? '"이미 두 끼나 챙기셨네요. 정말 잘하셨어요 🌷 무리하지 마세요."' : '"천천히, 한 입씩만 챙겨보세요."'
  if (coachId === 'wizard') return `"오늘 ${Math.round(totals.kcal)}kcal · P${Math.round(totals.p)} C${Math.round(totals.c)} F${Math.round(totals.f)}. 목표 대비 ${Math.round(totals.kcal / 2000 * 100)}%. ${overSodium ? '나트륨 초과.' : '균형 양호.'}"`
  if (coachId === 'jester') return recordsToday === 0 ? '"오~ 아직도 기록 안 했지? 냠냠이가 다 봤다 ㅋㅋ"' : '"오케이! 잘하고 있네 ㅎㅎ 저녁엔 뭐 먹을 거임? 궁금~"'
  if (coachId === 'elder') return veggies < 1 ? '"채소도 좀 챙겨야 혀. 시금치 무침이라도 한 술 어떠?"' : '"우리 지은이 오늘 잘 챙겨먹었네~ 저녁도 푸짐하게 한 상 차려봐~"'
  return lowProtein ? '"그대, 단백질이 부족하구나. 저녁엔 닭가슴살을 곁들이는 게 어떠한가?"' : '"훌륭하도다. 오늘도 정진하라."'
}
