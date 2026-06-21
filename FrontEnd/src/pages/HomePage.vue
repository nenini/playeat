<template>
  <section>
    <div class="greeting">
      <div>
        <div class="mono-label">{{ todayLabel }}</div>
        <div class="hello">안녕, {{ nickname }}님</div>
        <div class="muted">{{ homeError || '오늘의 식단 기록과 영양 상태를 확인하세요.' }}</div>
      </div>
      <AppButton @click="$emit('navigate', 'meals')">
        <AppIcon name="plus" color="#fff" />식단 기록하기
      </AppButton>
    </div>

    <AppCard :padding="32" class="hero">
      <div class="hero-grid">
        <div class="hero-side right">
          <HeroStat label="HEALTH SCORE" :value="scoreText" unit="점" tone="accent" :sub="dailyAnalysis ? '오늘 분석 기준' : '분석 데이터 없음'" />
          <HeroStat label="LEVEL" :value="levelText" :sub="characterSubText" />
        </div>
        <div class="mascot-wrap">
          <div class="mascot-stage">
            <NyamnyamCharacter :stage="characterStage" :size="240" :mood="characterMood" />
            <div v-if="equippedWeapon" class="weapon-on-hand"><WeaponIcon :id="equippedWeapon" /></div>
            <div class="speech">{{ characterMessage }}</div>
          </div>
          <ProgressBar :value="characterXp" :max="requiredXp" label="XP" :sub="`${characterXp} / ${requiredXp} XP`" tone="accent" :height="10" class="xp" />
        </div>
        <div class="hero-side">
          <HeroStat label="STREAK" :value="streakText" unit="일" tone="accent" :sub="bestStreakText" />
          <HeroStat label="TODAY KCAL" :value="todayCaloriesText" unit="kcal" :sub="calorieSubText" />
        </div>
      </div>
    </AppCard>

    <AppCard class="meal-strip">
      <div class="section-title">
        <div>
          <div class="section-title-main">오늘의 끼니</div>
          <div class="section-title-sub">아침 / 점심 / 간식 / 저녁</div>
        </div>
        <AppButton variant="ghost" size="sm" @click="$emit('navigate', 'meals')">전체 보기</AppButton>
      </div>
      <div class="meal-grid">
        <button v-for="kind in mealKinds" :key="kind.id" class="meal-tile" :class="{ done: mealByKind[kind.id]?.recorded }" @click="$emit('navigate', 'meals')">
          <div class="tile-head"><strong>{{ kind.emoji }} {{ kind.label }}</strong><span>{{ kind.window }}</span></div>
          <div class="tile-body">{{ mealSummary(kind.id) }}</div>
          <div class="tile-foot">
            <span>{{ mealCalories(kind.id) }}</span>
            <AppPill :tone="mealByKind[kind.id]?.recorded ? 'ok' : 'accent'" size="sm">{{ mealByKind[kind.id]?.recorded ? '기록 완료' : '+ 추가' }}</AppPill>
          </div>
        </button>
      </div>
    </AppCard>

    <div class="banners">
      <AppCard class="quest-card" :class="{ locked: !inGuild }">
        <div class="banner-row">
          <div class="quest-emoji">{{ inGuild ? '⚔️' : '🔒' }}</div>
          <div class="grow">
            <AppPill tone="accent" size="sm">보스 퀘스트</AppPill>
            <h3>보스 퀘스트 이어가기</h3>
            <p>{{ inGuild ? '오늘의 퀘스트를 완수하고 경험치를 획득하세요.' : '길드에 가입하면 이용할 수 있어요.' }}</p>
          </div>
          <AppButton v-if="inGuild" size="sm" @click="$emit('navigate', 'boss')">이어가기</AppButton>
        </div>
      </AppCard>
      <AppCard :class="{ locked: !inGuild }">
        <div class="banner-row">
          <div class="quest-emoji">{{ inGuild ? '🏟️' : '🔒' }}</div>
          <div class="grow">
            <AppPill tone="ok" size="sm">전투장</AppPill>
            <h3>전투장 입장하기</h3>
            <p>{{ inGuild ? '길드원들과 함께 보스를 처치하세요.' : '길드에 가입하면 이용할 수 있어요.' }}</p>
          </div>
          <AppButton v-if="inGuild" variant="secondary" size="sm" @click="$emit('navigate', 'boss')">입장하기</AppButton>
        </div>
      </AppCard>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import ProgressBar from '../components/common/ProgressBar.vue'
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
import WeaponIcon from '../components/nyamnyam/WeaponIcon.vue'
import { mealKinds, type MealKindId, type PageId, type Stage } from '../services/mock/nyamnyamMock'
import HeroStat from './parts/HeroStat.vue'
import { analysisApi, type AnalysisDailyResponse } from '../services/analysisApi'
import { characterApi, stageFromBackend, type CharacterResponse } from '../services/characterApi'
import { userApi, type UserMeResponse } from '../services/userApi'
import { guildApi } from '../services/nyamnyamApi'

defineProps<{ stage: Stage, equippedWeapon: string }>()
defineEmits<{ navigate: [page: PageId] }>()

const user = ref<UserMeResponse | null>(null)
const character = ref<CharacterResponse | null>(null)
const dailyAnalysis = ref<AnalysisDailyResponse | null>(null)
const inGuild = ref(false)
const homeError = ref('')

const today = toDateInputValue(new Date())
const todayLabel = computed(() => formatDateLabel(today))
const nickname = computed(() => user.value?.nickname || '사용자')
const scoreText = computed(() => dailyAnalysis.value ? String(dailyAnalysis.value.healthScore) : '-')
const characterStage = computed<Stage>(() => stageFromBackend(character.value?.stage))
const characterMood = computed<'happy' | 'hungry' | 'sad'>(() => {
  const mood = String(character.value?.mood || '').toLowerCase()
  if (mood.includes('hungry')) return 'hungry'
  if (mood.includes('sad')) return 'sad'
  return 'happy'
})
const characterMessage = computed(() => character.value ? `"${character.value.name}와 함께 기록해요."` : '캐릭터 정보를 불러오지 못했습니다.')
const levelText = computed(() => character.value ? `LV.${character.value.level}` : '-')
const characterSubText = computed(() => character.value ? `${character.value.name} · ${character.value.stage}` : '캐릭터 데이터 없음')
const characterXp = computed(() => Number(character.value?.xp || 0))
const requiredXp = computed(() => Math.max(1, Number(character.value?.requiredXp || 1)))
const streakText = computed(() => character.value ? String(character.value.streakDays || 0) : '-')
const bestStreakText = computed(() => character.value?.bestStreakDays !== undefined ? `개인 최고 ${character.value.bestStreakDays}일` : '연속 기록 데이터 없음')
const calorie = computed(() => dailyAnalysis.value?.nutrition?.nutrients?.find((nutrient) => nutrient.code === 'calories'))
const todayCaloriesText = computed(() => dailyAnalysis.value ? Math.round(Number(calorie.value?.current || 0)).toLocaleString() : '-')
const calorieSubText = computed(() => {
  if (!dailyAnalysis.value) return '칼로리 데이터 없음'
  return `목표 ${Math.round(Number(calorie.value?.target || 0)).toLocaleString()} · ${Math.round(Number(calorie.value?.achievementRate || 0))}%`
})
const mealByKind = computed(() => {
  const result: Partial<Record<MealKindId, NonNullable<AnalysisDailyResponse['diet']>['meals'][number]>> = {}
  for (const meal of dailyAnalysis.value?.diet?.meals || []) {
    const key = mealTypeToKind(meal.mealType)
    if (key) result[key] = meal
  }
  return result
})

function mealSummary(kindId: MealKindId) {
  const meal = mealByKind.value[kindId]
  if (!meal?.recorded) return '아직 기록 없음'
  return meal.items?.map((item) => item.foodName).join(', ') || '기록 완료'
}

function mealCalories(kindId: MealKindId) {
  const meal = mealByKind.value[kindId]
  return meal?.recorded ? `${Math.round(Number(meal.totalCalories || 0))} kcal` : '- kcal'
}

async function loadHomeSummary() {
  homeError.value = ''
  try {
    const [meResult, characterResult, analysisResult, guildResult] = await Promise.allSettled([
      userApi.getMe(),
      characterApi.getMe(),
      analysisApi.daily(today),
      guildApi.me()
    ])
    if (meResult.status === 'fulfilled') user.value = meResult.value
    if (characterResult.status === 'fulfilled') character.value = characterResult.value
    if (analysisResult.status === 'fulfilled') dailyAnalysis.value = analysisResult.value
    inGuild.value = guildResult.status === 'fulfilled' && !!guildResult.value

    const failed = [meResult, characterResult, analysisResult].filter((result) => result.status === 'rejected')
    if (failed.length) homeError.value = '일부 홈 데이터를 불러오지 못했습니다.'
  } catch (error) {
    console.warn('Home summary API failed', error)
    homeError.value = error instanceof Error ? error.message : '홈 데이터를 불러오지 못했습니다.'
  }
}

function mealTypeToKind(mealType: string): MealKindId | null {
  if (mealType === 'BREAKFAST') return 'breakfast'
  if (mealType === 'LUNCH') return 'lunch'
  if (mealType === 'SNACK') return 'snack'
  if (mealType === 'DINNER') return 'dinner'
  return null
}

function toDateInputValue(date: Date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function formatDateLabel(value: string) {
  const date = new Date(`${value}T00:00:00`)
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`
}

onMounted(() => {
  void loadHomeSummary()
})
</script>

<style scoped>
.greeting { margin-bottom: 24px; display: flex; justify-content: space-between; align-items: flex-end; }
.hello { font-size: 28px; font-weight: 800; margin-top: 4px; }
.hero { margin-bottom: 20px; background: linear-gradient(180deg, #fffaf0 0%, #ffffff 100%); }
.hero-grid { display: grid; grid-template-columns: 1fr auto 1fr; gap: 32px; align-items: center; }
.hero-side { display: flex; flex-direction: column; gap: 14px; align-items: flex-start; }
.hero-side.right { align-items: flex-end; }
.mascot-wrap { display: flex; flex-direction: column; align-items: center; gap: 14px; }
.mascot-stage { width: 280px; height: 280px; border-radius: 50%; background: radial-gradient(circle at 50% 40%, #fff5e0 0%, #fbe5d3 100%); border: 2px solid var(--border); display: flex; align-items: center; justify-content: center; position: relative; box-shadow: inset 0 -8px 24px rgba(232,138,77,0.08); }
.speech { position: absolute; bottom: -12px; background: var(--surface); padding: 8px 18px; border: 1.5px solid var(--border); border-radius: 999px; font-size: 14px; font-weight: 700; box-shadow: var(--shadow); white-space: nowrap; }
.weapon-on-hand { position: absolute; right: -36px; top: 26%; width: 58px; height: 195px; z-index: 2; pointer-events: none; }
.weapon-on-hand :deep(svg) { width: 58px; height: 195px; }
.xp { width: 280px; margin-top: 8px; }
.meal-strip { margin-bottom: 20px; }
.meal-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.meal-tile { text-align: left; cursor: pointer; border: 1.5px dashed var(--border); border-radius: 12px; padding: 14px; background: var(--surface-alt); color: var(--ink); display: flex; flex-direction: column; gap: 8px; min-height: 110px; }
.meal-tile.done { border-style: solid; border-color: var(--border-strong); background: var(--surface); }
.tile-head, .tile-foot { display: flex; justify-content: space-between; align-items: baseline; }
.tile-head strong { font-size: 14px; }
.tile-head span, .tile-foot span { font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
.tile-body { flex: 1; font-size: 12px; color: var(--ink-2); font-style: italic; }
.meal-tile.done .tile-body { font-style: normal; }
.banners { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.quest-card { background: linear-gradient(135deg, var(--accent-soft) 0%, var(--surface) 100%); }
.locked { opacity: 0.55; filter: grayscale(0.4); pointer-events: none; }
.banner-row { display: flex; align-items: center; gap: 14px; }
.quest-emoji { width: 60px; height: 60px; border-radius: 14px; background: var(--surface); border: 1.5px solid var(--accent); display: flex; align-items: center; justify-content: center; font-size: 30px; flex-shrink: 0; }
.grow { flex: 1; }
h3 { margin: 6px 0 0; font-size: 15px; }
p { margin: 4px 0 0; font-size: 11px; color: var(--ink-2); }
</style>
