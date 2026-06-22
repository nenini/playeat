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
  gramPerServing?: number
  pieceUnitAvailable?: boolean
}

export interface MealLog {
  id: string
  foodId: string
  mealKind: MealKindId
  qty: number
  dietId?: string
  inputUnit?: string
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

const apiFoodDb = new Map<string, Food>()

export function registerApiFoods(foods: Food[]) {
  foods.forEach((food) => apiFoodDb.set(String(food.id), food))
}

export function findFoodById(id: string | number) {
  return foodDb.find((food) => String(food.id) === String(id)) || apiFoodDb.get(String(id))
}

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
  { id: 'warrior', name: '전사', color: '#cfe2d2', tagline: '직설적 피트니스 코치', glyph: '💪' },
  { id: 'healer', name: '힐러', color: '#f4d4d4', tagline: '따뜻하고 위로적', glyph: '🌷' },
  { id: 'wizard', name: '마법사', color: '#cfd5ec', tagline: '데이터·과학 중심', glyph: '🔮' },
  { id: 'rogue', name: '도적', color: '#f6e3b8', tagline: '장난꾼 친구 톤', glyph: '🎭' },
  { id: 'village-npc', name: '마을 NPC', color: '#e5dccc', tagline: '구수·친근한 동네 어른', glyph: '🍵' }
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
  { id: 5, name: '저염 라이프', members: 5, max: 10, score: 2300, rank: 5, focus: '나트륨 관리', emoji: '저' },
  { id: 6, name: '두부단', members: 14, max: 25, score: 2210, rank: 6, focus: '고단백 식단', emoji: '두' },
  { id: 7, name: '물마시자', members: 9, max: 20, score: 2140, rank: 7, focus: '수분 섭취', emoji: '물' },
  { id: 8, name: '야식금지단', members: 16, max: 30, score: 2070, rank: 8, focus: '야식 줄이기', emoji: '야' },
  { id: 9, name: '도시락 연구소', members: 19, max: 30, score: 1990, rank: 9, focus: '도시락 기록', emoji: '도' },
  { id: 10, name: '샐러드 클럽', members: 11, max: 18, score: 1900, rank: 10, focus: '채소 챙기기', emoji: '샐' },
  { id: 11, name: '아침밥 수호대', members: 7, max: 15, score: 1840, rank: 11, focus: '아침 먹기', emoji: '밥' },
  { id: 12, name: '당줄이기', members: 20, max: 30, score: 1760, rank: 12, focus: '당류 관리', emoji: '당' },
  { id: 13, name: '헬시런치', members: 13, max: 25, score: 1690, rank: 13, focus: '점심 균형', emoji: '런' },
  { id: 14, name: '단짠조절단', members: 18, max: 30, score: 1620, rank: 14, focus: '나트륨·당 관리', emoji: '단' },
  { id: 15, name: '집밥 원정대', members: 10, max: 20, score: 1550, rank: 15, focus: '집밥 기록', emoji: '집' },
  { id: 16, name: '간식개혁팀', members: 8, max: 16, score: 1480, rank: 16, focus: '간식 개선', emoji: '간' },
  { id: 17, name: '프로틴 길드', members: 21, max: 30, score: 1410, rank: 17, focus: '단백질 보충', emoji: '프' },
  { id: 18, name: '외식관리반', members: 12, max: 20, score: 1340, rank: 18, focus: '외식 조절', emoji: '외' },
  { id: 19, name: '야채삼총사', members: 15, max: 24, score: 1270, rank: 19, focus: '채소 3종', emoji: '야' },
  { id: 20, name: '천천히먹기', members: 6, max: 12, score: 1200, rank: 20, focus: '식사 속도', emoji: '천' },
  { id: 21, name: '저녁가볍게', members: 17, max: 30, score: 1130, rank: 21, focus: '저녁 관리', emoji: '저' },
  { id: 22, name: '기록습관단', members: 24, max: 30, score: 1060, rank: 22, focus: '매일 기록', emoji: '기' },
  { id: 23, name: '균형의 기사단', members: 9, max: 18, score: 990, rank: 23, focus: '영양 균형', emoji: '균' },
  { id: 24, name: '든든아침단', members: 4, max: 10, score: 920, rank: 24, focus: '아침 루틴', emoji: '든' },
  { id: 25, name: '건강한 한입', members: 11, max: 20, score: 850, rank: 25, focus: '식습관 개선', emoji: '건' }
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

export type OnboardingInputType = 'date' | 'number' | 'time' | 'single' | 'multi' | 'text'

export interface OnboardingQuestion {
  id: string
  label: string
  type: OnboardingInputType
  unit?: string
  options?: string[]
  placeholder?: string
}

export interface OnboardingStep {
  id: string
  title: string
  eyebrow: string
  description: string
  questions: OnboardingQuestion[]
}

export const onboardingSteps: OnboardingStep[] = [
  {
    id: 'basic',
    eyebrow: 'STEP 1 · 기본 정보',
    title: '기본 정보를 알려주세요',
    description: '냠냠코치가 하루 권장 섭취량과 목표를 계산할 때 사용해요.',
    questions: [
      { id: 'birthday', label: '생년월일을 입력해주세요', type: 'date' },
      { id: 'gender', label: '성별을 선택해주세요', type: 'single', options: ['여성', '남성', '선택 안 함'] },
      { id: 'height', label: '키를 입력해주세요', type: 'number', unit: 'cm' },
      { id: 'currentWeight', label: '현재 몸무게를 입력해주세요', type: 'number', unit: 'kg' },
      { id: 'targetWeight', label: '목표 체중을 입력해주세요', type: 'number', unit: 'kg' }
    ]
  },
  {
    id: 'activity',
    eyebrow: 'STEP 2 · 활동 정보',
    title: '평소 움직임을 선택해주세요',
    description: '활동량에 맞춰 칼로리와 단백질 목표를 더 현실적으로 잡아요.',
    questions: [
      { id: 'activityLevel', label: '평소 활동량은 어느 정도인가요?', type: 'single', options: ['거의 앉아 있어요', '가벼운 활동이 있어요', '많이 걷거나 움직여요', '육체 활동이 많아요'] },
      { id: 'exerciseFrequency', label: '운동은 얼마나 자주 하나요?', type: 'single', options: ['거의 안 해요', '주 1~2회', '주 3~4회', '주 5회 이상'] }
    ]
  },
  {
    id: 'goal',
    eyebrow: 'STEP 3 · 목표 설정',
    title: '가장 중요한 목표를 정해요',
    description: '냠냠이가 어떤 방향으로 응원할지 정하는 단계예요.',
    questions: [
      { id: 'mainGoal', label: '가장 큰 목표는 무엇인가요?', type: 'single', options: ['감량', '유지', '증량'] },
      { id: 'improveTarget', label: '무엇을 개선하고 싶나요?', type: 'single', options: ['건강', '에너지', '식습관 개선'] },
      { id: 'healthFocus', label: '건강 관리가 필요한 항목이 있나요?', type: 'multi', options: ['혈당', '혈압', '콜레스테롤', '없음'], placeholder: '직접 입력 후 추가' }
    ]
  },
  {
    id: 'concerns',
    eyebrow: 'STEP 4 · 식습관 고민',
    title: '바꾸고 싶은 습관을 골라주세요',
    description: '실패 원인을 먼저 알면 더 오래 이어갈 수 있어요.',
    questions: [
      { id: 'changeNeeds', label: '식습관에서 가장 바꾸고 싶은 부분은 무엇인가요?', type: 'multi', options: ['야식 줄이기', '단 음료 줄이기', '단백질 늘리기', '채소 챙기기', '규칙적으로 먹기'] },
      { id: 'difficultReasons', label: '꾸준히 이어가기 어려운 이유는 무엇인가요?', type: 'multi', options: ['시간 부족', '메뉴 고민', '외식이 많음', '기록이 귀찮음', '동기 부족'] }
    ]
  },
  {
    id: 'preference',
    eyebrow: 'STEP 5 · 식단 선호와 제한',
    title: '먹는 방식과 제한 음식을 알려주세요',
    description: '추천 식단에서 피해야 할 것을 미리 제외해요.',
    questions: [
      { id: 'dietStyle', label: '선호하는 식단 스타일이 있나요?', type: 'single', options: ['한식 위주', '간편식 위주', '고단백', '저탄수', '채식 위주', '상관 없음'] },
      { id: 'restrictedFoods', label: '피해야 하거나 제한하는 음식이 있나요?', type: 'multi', options: ['돼지고기', '소고기', '유제품', '밀가루', '카페인', '없음'] },
      { id: 'allergies', label: '알러지가 있나요?', type: 'multi', options: ['달걀', '우유', '땅콩', '갑각류', '복숭아', '없음'], placeholder: '직접 입력' }
    ]
  },
  {
    id: 'record',
    eyebrow: 'STEP 6 · 기록 계획',
    title: '기록 방식을 정하면 끝이에요',
    description: '부담 없는 빈도로 시작하고, 필요하면 언제든 바꿀 수 있어요.',
    questions: [
      { id: 'recordFrequency', label: '식사는 얼마나 자주 기록할 계획인가요?', type: 'single', options: ['매일', '주 5일', '주 3일', '가끔'] },
      { id: 'calorieTrackingExperience', label: '칼로리 추적을 해본 적 있나요?', type: 'single', options: ['처음이에요', '가끔 해봤어요', '꾸준히 해봤어요'] }
    ]
  }
]

export const seedLogs: MealLog[] = [
  { id: 'l1', foodId: 'oatmeal', mealKind: 'breakfast', qty: 80 },
  { id: 'l2', foodId: 'banana', mealKind: 'breakfast', qty: 1 },
  { id: 'l3', foodId: 'rice', mealKind: 'lunch', qty: 210 },
  { id: 'l4', foodId: 'kimchi-stew', mealKind: 'lunch', qty: 1 },
  { id: 'l5', foodId: 'protein-shake', mealKind: 'snack', qty: 1 }
]

const findFood = (id: string | number) => findFoodById(id)
const perBase = (food: Food) => Number(String(food.per).match(/(\d+)/)?.[1] ?? 1)

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
  if (coachId === 'warrior') return lowProtein ? `"P ${Math.round(totals.p)}g. 부족. ${Math.max(0, 60 - Math.round(totals.p))}g 더. 닭가슴살 1팩 ㄱ.💪"` : '"오늘 평균. 더 가즈아. 단백질 우선."'
  if (coachId === 'healer') return recordsToday >= 2 ? '"이미 두 끼나 챙기셨네요. 정말 잘하셨어요 🌷 무리하지 마세요."' : '"천천히, 한 입씩만 챙겨보세요."'
  if (coachId === 'wizard') return `"오늘 ${Math.round(totals.kcal)}kcal · P${Math.round(totals.p)} C${Math.round(totals.c)} F${Math.round(totals.f)}. 목표 대비 ${Math.round(totals.kcal / 2000 * 100)}%. ${overSodium ? '나트륨 초과.' : '균형 양호.'}"`
  if (coachId === 'rogue') return recordsToday === 0 ? '"오~ 아직도 기록 안 했지? 냠냠이가 다 봤다 ㅋㅋ"' : '"오케이! 잘하고 있네 ㅎㅎ 저녁엔 뭐 먹을 거임? 궁금~"'
  if (coachId === 'village-npc') return veggies < 1 ? '"채소도 좀 챙겨야 혀. 시금치 무침이라도 한 술 어떠?"' : '"우리 지은이 오늘 잘 챙겨먹었네~ 저녁도 푸짐하게 한 상 차려봐~"'
  return lowProtein ? '"그대, 단백질이 부족하구나. 저녁엔 닭가슴살을 곁들이는 게 어떠한가?"' : '"훌륭하도다. 오늘도 정진하라."'
}
