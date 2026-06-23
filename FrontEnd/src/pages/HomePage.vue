<template>
  <section>
    <div class="greeting">
      <div>
        <div class="mono-label">{{ todayLabel }}</div>
        <div class="hello">안녕, {{ nickname }}님</div>
        <div class="muted">{{ homeError || (isLoading ? '홈 정보를 불러오는 중...' : '오늘의 식단 기록과 영양 상태를 확인하세요.') }}</div>
        <div class="hub-chips">
          <AppPill tone="dark" size="sm">{{ levelText }}</AppPill>
          <AppPill tone="accent" size="sm">{{ guildName }}</AppPill>
          <AppPill tone="ok" size="sm">{{ coinText }}</AppPill>
        </div>
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
            <NyamnyamCharacter
              :stage="characterStage"
              :size="165"
              :mood="characterMood"
              :appearance-type="character?.appearanceType || 'DEFAULT'"
              :hat-id="equipmentIconId(equippedHead)"
              :hat-image-url="equipmentImageUrl(equippedHead)"
            />
            <div v-if="equippedHand" class="weapon-on-hand">
              <img v-if="equippedHandImage && !handImageFailed" :src="equippedHandImage" :alt="equippedHand.name || '손 장비'" @error="handImageFailed = true">
              <WeaponIcon v-else-if="equipmentIconId(equippedHand)" :id="equipmentIconId(equippedHand) || undefined" />
            </div>
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
      <AppCard class="quest-card">
        <div class="banner-row">
          <div class="quest-scroll-icon" aria-hidden="true">📜</div>
          <div class="grow">
            <AppPill tone="accent" size="sm">보스 퀘스트</AppPill>
            <h3>{{ questCardTitle }}</h3>
            <p>{{ questCardDescription }}</p>
            <div v-if="myQuest" class="banner-meta">
              <span>{{ questStatusLabel }}</span>
              <strong>{{ safeNumber(myQuest.currentValue) }} / {{ safeNumber(myQuest.targetValue) }} {{ myQuest.unit || '' }}</strong>
            </div>
          </div>
          <AppButton size="sm" @click="$emit('navigate', questCardTarget)">{{ questCardButton }}</AppButton>
        </div>
      </AppCard>
      <AppCard>
        <div class="banner-row">
          <div class="boss-card-icon" aria-hidden="true">
            <BossMonster v-if="activeBattle" :size="58" :hp="bossHpPercent" />
            <span v-else>{{ inGuild ? '👾' : '🔒' }}</span>
          </div>
          <div class="grow">
            <AppPill tone="ok" size="sm">전투장</AppPill>
            <h3>{{ bossCardTitle }}</h3>
            <p>{{ bossCardDescription }}</p>
            <ProgressBar
              v-if="activeBattle"
              :value="bossCurrentHp"
              :max="bossMaxHp"
              :sub="`${bossCurrentHp.toLocaleString()} / ${bossMaxHp.toLocaleString()} HP`"
              tone="accent"
              :height="7"
              class="boss-progress"
            />
          </div>
          <AppButton variant="secondary" size="sm" @click="$emit('navigate', bossCardTarget)">{{ bossCardButton }}</AppButton>
        </div>
      </AppCard>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import ProgressBar from '../components/common/ProgressBar.vue'
import BossMonster from '../components/nyamnyam/BossMonster.vue'
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
import WeaponIcon from '../components/nyamnyam/WeaponIcon.vue'
import { mealKinds, type MealKindId, type NyamnyamMood, type PageId, type Stage } from '../services/mock/nyamnyamMock'
import HeroStat from './parts/HeroStat.vue'
import { analysisApi, type AnalysisDailyResponse } from '../services/analysisApi'
import { characterApi, moodFromBackend, stageFromBackend, type CharacterResponse } from '../services/characterApi'
import { userApi, type UserMeResponse } from '../services/userApi'
import { bossBattleApi } from '../services/api/bossBattleApi'
import { ApiError } from '../services/api/client'
import { characterEquipmentApi, equipmentIconId, equipmentImageUrl } from '../services/api/characterEquipmentApi'
import { guildApi } from '../services/api/guildApi'
import { coinApi } from '../services/api/coinApi'
import { questApi } from '../services/api/questApi'
import type { BossBattleDetail, BossBattleHp, BossBattleSummary } from '../types/bossBattle'
import type { CharacterEquipment } from '../types/characterEquipment'
import type { MyGuildStatus } from '../types/guild'
import type { QuestDetail } from '../types/quest'

defineProps<{ stage: Stage, equippedWeapon: string }>()
defineEmits<{ navigate: [page: PageId] }>()

const user = ref<UserMeResponse | null>(null)
const character = ref<CharacterResponse | null>(null)
const dailyAnalysis = ref<AnalysisDailyResponse | null>(null)
const equipments = ref<CharacterEquipment[]>([])
const guildStatus = ref<MyGuildStatus | null>(null)
const coinBalance = ref<number | null>(null)
const currentBattle = ref<BossBattleSummary | null>(null)
const battleDetail = ref<BossBattleDetail | null>(null)
const battleHp = ref<BossBattleHp | null>(null)
const myQuest = ref<QuestDetail | null>(null)
const homeError = ref('')
const isLoading = ref(true)
const handImageFailed = ref(false)

const today = toDateInputValue(new Date())
const todayLabel = computed(() => formatDateLabel(today))
const nickname = computed(() => user.value?.nickname || '사용자')
const scoreText = computed(() => dailyAnalysis.value ? String(dailyAnalysis.value.healthScore) : '-')
const characterStage = computed<Stage>(() => stageFromBackend(character.value?.stage))
const characterMood = computed<NyamnyamMood>(() => moodFromBackend(character.value?.mood))
const characterMessage = computed(() => {
  if (!character.value) return '캐릭터 정보를 불러오지 못했습니다.'
  const message = character.value.moodMessage?.trim()
  if (message) return `"${message}"`
  return `"${fallbackMoodMessage(characterMood.value)}"`
})
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
const equippedHand = computed(() => equipments.value.find((item) => item.slotType === 'HAND' && item.equipped && item.itemId !== null) ?? null)
const equippedHead = computed(() => equipments.value.find((item) => item.slotType === 'HEAD' && item.equipped && item.itemId !== null) ?? null)
const equippedHandImage = computed(() => equipmentImageUrl(equippedHand.value))
const guildId = computed(() => guildStatus.value?.guild?.guildId ?? guildStatus.value?.guildId ?? null)
const guildName = computed(() => guildStatus.value?.guild?.name || '길드 미가입')
const coinText = computed(() => coinBalance.value === null ? '코인 -' : `코인 ${coinBalance.value.toLocaleString()}`)
const inGuild = computed(() => guildId.value !== null)
const activeBattle = computed(() => battleDetail.value ?? currentBattle.value)
const bossCurrentHp = computed(() => Math.max(0, safeNumber(battleHp.value?.currentHp ?? activeBattle.value?.currentHp)))
const bossMaxHp = computed(() => Math.max(1, safeNumber(battleHp.value?.maxHp ?? activeBattle.value?.maxHp, 1)))
const bossHpPercent = computed(() => bossCurrentHp.value / bossMaxHp.value * 100)
const questStatusLabel = computed(() => {
  if (myQuest.value?.status === 'COMPLETED') return '보상 수령 가능'
  if (myQuest.value?.status === 'REWARDED') return '보상 수령 완료'
  if (myQuest.value?.status === 'EXPIRED') return '기간 종료'
  return '진행 중'
})
const questCardTitle = computed(() => {
  if (!inGuild.value) return '오늘의 퀘스트'
  if (!activeBattle.value) return '보스 퀘스트 준비 중'
  return myQuest.value?.title || '내 퀘스트가 아직 없어요'
})
const questCardDescription = computed(() => {
  if (!inGuild.value) return '길드에 가입하면 오늘의 퀘스트를 받을 수 있어요.'
  if (!activeBattle.value) return '진행 중인 보스전이 없어 퀘스트가 아직 생성되지 않았어요.'
  if (!myQuest.value) return '보스전은 진행 중이지만 내 퀘스트가 아직 생성되지 않았어요.'
  return myQuest.value.description || '오늘의 퀘스트를 완수하고 경험치를 획득하세요.'
})
const questCardTarget = computed<PageId>(() => inGuild.value ? 'boss' : 'guild')
const questCardButton = computed(() => inGuild.value ? '확인하기' : '길드 보기')
const bossCardTitle = computed(() => activeBattle.value?.bossName || (inGuild.value ? '진행 중인 보스전 없음' : '길드 보스전'))
const bossCardDescription = computed(() => {
  if (!inGuild.value) return '길드에 가입하면 보스전에 참여할 수 있어요.'
  if (!activeBattle.value) return '길드장이 보스전을 시작하면 참여할 수 있어요.'
  const difficulty = activeBattle.value.difficulty || '난이도 정보 없음'
  const status = activeBattle.value.status === 'DEFEATED' ? '격파 성공' : activeBattle.value.status === 'IN_PROGRESS' ? '진행 중' : activeBattle.value.status
  return `${difficulty} · ${status}`
})
const bossCardTarget = computed<PageId>(() => inGuild.value ? 'boss' : 'guild')
const bossCardButton = computed(() => inGuild.value ? '보스전 보기' : '길드 보기')
const mealByKind = computed(() => {
  const result: Partial<Record<MealKindId, NonNullable<AnalysisDailyResponse['diet']>['meals'][number]>> = {}
  for (const meal of dailyAnalysis.value?.diet?.meals || []) {
    const key = mealTypeToKind(meal.mealType)
    if (key) result[key] = meal
  }
  return result
})

watch(equippedHandImage, () => {
  handImageFailed.value = false
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
  isLoading.value = true
  homeError.value = ''
  const [meResult, characterResult, equipmentResult, analysisResult, guildResult, coinResult] = await Promise.allSettled([
    userApi.getMe(),
    characterApi.getMyCharacter(),
    characterEquipmentApi.getMyEquipments(),
    analysisApi.daily(today),
    guildApi.getMyGuildStatus(),
    coinApi.getMyCoin()
  ])
  if (meResult.status === 'fulfilled') user.value = meResult.value
  if (characterResult.status === 'fulfilled') character.value = characterResult.value
  if (equipmentResult.status === 'fulfilled') equipments.value = equipmentResult.value.equipments ?? []
  if (analysisResult.status === 'fulfilled') dailyAnalysis.value = analysisResult.value
  if (guildResult.status === 'fulfilled') guildStatus.value = guildResult.value
  if (coinResult.status === 'fulfilled') coinBalance.value = Number(coinResult.value.balance || 0)

  const failed = [meResult, characterResult, equipmentResult, analysisResult].filter((result) => result.status === 'rejected')
  if (failed.length) homeError.value = '일부 홈 데이터를 불러오지 못했습니다.'
  if (guildResult.status === 'rejected') {
    homeError.value = errorMessage(guildResult.reason, '길드 정보를 불러오지 못했습니다.')
    isLoading.value = false
    return
  }

  await loadBossSummary()
  isLoading.value = false
}

async function loadBossSummary() {
  currentBattle.value = null
  battleDetail.value = null
  battleHp.value = null
  myQuest.value = null
  if (!guildId.value) return

  try {
    const current = await bossBattleApi.getCurrentGuildBossBattle(guildId.value)
    currentBattle.value = current?.battle || null
  } catch (error) {
    if (error instanceof ApiError && error.status === 404) return
    homeError.value = errorMessage(error, '보스전 정보를 불러오지 못했습니다.')
    return
  }

  const battleId = currentBattle.value?.battleId
  if (!battleId) return

  const [detailResult, hpResult, questResult] = await Promise.allSettled([
    bossBattleApi.getBossBattle(battleId),
    bossBattleApi.getBossBattleHp(battleId),
    questApi.getMyBattleQuests(battleId)
  ])
  if (detailResult.status === 'fulfilled') battleDetail.value = detailResult.value
  if (hpResult.status === 'fulfilled') battleHp.value = hpResult.value
  if (questResult.status === 'fulfilled') myQuest.value = questResult.value

  if ([detailResult, hpResult].some((result) => result.status === 'rejected')) {
    homeError.value = '일부 보스전 데이터를 불러오지 못했습니다.'
  }
}

function safeNumber(value: unknown, defaultValue = 0) {
  const number = Number(value)
  return Number.isFinite(number) ? number : defaultValue
}

function errorMessage(error: unknown, defaultMessage: string) {
  return error instanceof Error && error.message ? error.message : defaultMessage
}

function mealTypeToKind(mealType: string): MealKindId | null {
  if (mealType === 'BREAKFAST') return 'breakfast'
  if (mealType === 'LUNCH') return 'lunch'
  if (mealType === 'SNACK') return 'snack'
  if (mealType === 'DINNER') return 'dinner'
  return null
}

function fallbackMoodMessage(mood: NyamnyamMood) {
  if (mood === 'hungry') return '배고파요. 밥 주세요ㅜㅜ'
  if (mood === 'chubby') return '오늘은 조금 가볍게 먹어볼까요?'
  if (mood === 'muscle') return '단백질 힘이 차올라요!'
  return '오늘도 냠냠 기록해요.'
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
section { display: flex; flex-direction: column; }
.greeting { margin-bottom: 24px; display: flex; justify-content: space-between; align-items: flex-end; }
.hello { font-size: 28px; font-weight: 800; margin-top: 4px; }
.hub-chips { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 12px; }
.hero { margin-bottom: 20px; background: linear-gradient(145deg, #fff8e8 0%, #fff 48%, #ffe7d4 100%); border-color: #e7bd94; overflow: hidden; position: relative; }
.hero:before { content: ""; position: absolute; inset: 0; pointer-events: none; opacity: .08; background: linear-gradient(90deg, transparent, rgba(255,255,255,.7), transparent); z-index: 0; }
.hero-grid { display: grid; grid-template-columns: 1fr auto 1fr; gap: 32px; align-items: center; }
.hero-side { display: flex; flex-direction: column; gap: 14px; align-items: flex-start; }
.hero-side.right { align-items: flex-end; }
.mascot-wrap { display: flex; flex-direction: column; align-items: center; gap: 14px; position: relative; z-index: 2; }
.mascot-stage { width: 280px; height: 280px; z-index: 1; padding-bottom: 18px; border-radius: 50%; background: radial-gradient(circle at 50% 38%, #fff 0%, #fff2d8 52%, #ffcfa9 100%); border: 3px solid #fff; display: flex; align-items: center; justify-content: center; position: relative; box-shadow: 0 0 0 6px rgba(240,120,60,.12), inset 0 -8px 24px rgba(232,138,77,0.10); animation: hero-float 4s ease-in-out infinite; }
.speech { position: absolute; z-index: 5; bottom: -18px; max-width: 280px; background: var(--surface); padding: 8px 16px; border: 1.5px solid var(--border); border-radius: 18px; font-size: 14px; font-weight: 700; line-height: 1.35; text-align: center; box-shadow: var(--shadow); }
.weapon-on-hand { position: absolute; right: 2px; top: 30%; width: 38px; height: 124px; z-index: 2; pointer-events: none; }
.weapon-on-hand :deep(svg) { width: 38px; height: 124px; }
.weapon-on-hand img { width: 38px; height: 124px; object-fit: contain; }
.xp { width: 280px; margin-top: 8px; }
.meal-strip { margin-bottom: 20px; order: 4; }
.meal-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.meal-tile { text-align: left; cursor: pointer; border: 1.5px dashed var(--border); border-radius: 14px; padding: 14px; background: var(--surface-alt); color: var(--ink); display: flex; flex-direction: column; gap: 8px; min-height: 110px; }
.meal-tile:hover { transform: translateY(-3px); border-color: var(--accent); box-shadow: var(--shadow); }
.meal-tile.done { border-style: solid; border-color: var(--border-strong); background: var(--surface); }
.tile-head, .tile-foot { display: flex; justify-content: space-between; align-items: baseline; }
.tile-head strong { font-size: 14px; }
.tile-head span, .tile-foot span { font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
.tile-body { flex: 1; font-size: 12px; color: var(--ink-2); font-style: italic; }
.meal-tile.done .tile-body { font-style: normal; }
.banners { display: grid; grid-template-columns: 1fr 1.15fr; gap: 16px; order: 3; margin-bottom: 20px; }
.banners :deep(.app-card) { min-height: 158px; display: flex; align-items: center; }
.quest-card { background: #fff8e5; }
.banner-row { display: flex; align-items: center; gap: 14px; }
.quest-scroll-icon, .boss-card-icon { width: 76px; height: 76px; border-radius: 18px; background: var(--surface); border: 2px solid var(--accent); display: flex; align-items: center; justify-content: center; font-size: 38px; flex-shrink: 0; overflow: hidden; box-shadow: 0 4px 0 rgba(143,207,85,.18); }
.quest-scroll-icon { background: linear-gradient(145deg, #fffaf0, #fff1d8); }
.boss-card-icon :deep(.boss-monster) { flex: 0 0 auto; z-index: 2; }
.grow { flex: 1; }
h3 { margin: 6px 0 0; font-size: 15px; }
p { margin: 4px 0 0; font-size: 11px; color: var(--ink-2); }
.banner-meta { margin-top: 9px; display: flex; align-items: center; gap: 10px; font-size: 11px; color: var(--ink-2); }
.banner-meta strong { font-family: var(--mono); color: var(--ink); }
.boss-progress { max-width: 260px; margin-top: 9px; }
@keyframes hero-float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-6px); } }
@media (max-width: 900px) { .hero-grid { grid-template-columns: 1fr; } .hero-side,.hero-side.right { align-items: center; } .banners { grid-template-columns: 1fr; } }
</style>
