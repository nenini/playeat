<template>
  <section>
    <div class="page-title-row">
      <div>
        <div class="mono-label">MEALS · 매일 식단</div>
        <div class="title-lg">날짜별 식단</div>
      </div>
      <div class="date-box">
        <AppButton variant="ghost" size="sm" @click="shiftDay(-1)">
          <AppIcon name="chev-l" :size="14" />
        </AppButton>
        <button class="date-open" type="button" @click="showCalendar = !showCalendar">{{ selectedDateLabel }}</button>
        <input
          v-if="showCalendar"
          v-model="selectedDate"
          class="calendar-pop"
          type="date"
          :max="todayDate"
          @change="syncDateFromCalendar"
        >
        <AppButton variant="ghost" size="sm" :disabled="!canGoForward" @click="shiftDay(1)">
          <AppIcon name="chev-r" :size="14" />
        </AppButton>
      </div>
    </div>

    <div class="meals-layout">
      <aside class="left-panel">
        <AppCard :padding="14">
          <div class="mono-label block-label">끼니 선택</div>
          <div class="kind-grid">
            <button
              v-for="kind in mealKinds"
              :key="kind.id"
              class="kind-btn"
              :class="{ active: activeKind === kind.id }"
              @click="activeKind = kind.id"
            >
              <span>{{ kind.emoji }}</span>
              <strong>{{ kind.label }}</strong>
            </button>
          </div>
        </AppCard>

        <AppCard :padding="14">
          <div class="mono-label block-label">음식 검색 <span>· 식품 DB</span></div>
          <label class="app-input">
            <AppIcon name="search" color="var(--ink-3)" />
            <input v-model="query" placeholder="음식 이름 입력">
          </label>
          <div class="search-list">
            <p v-if="foodLoading" class="empty-text">음식을 검색하는 중입니다.</p>
            <p v-else-if="query.trim() && !filtered.length" class="empty-text">검색 결과가 없습니다.</p>
            <button
              v-for="food in filtered"
              :key="food.id"
              class="food-row"
              :class="{ active: isPendingFood(food.id) }"
              @click="selectFood(food)"
            >
              <span class="food-emoji">{{ food.emoji }}</span>
              <span class="food-info">
                <strong>{{ food.name }}</strong>
                <small>{{ food.kcal }}kcal · P{{ food.p }} C{{ food.c }} F{{ food.f }} / {{ food.per }}</small>
              </span>
              <AppIcon :name="isPendingFood(food.id) ? 'check' : 'plus'" :size="14" :color="isPendingFood(food.id) ? 'var(--accent)' : 'var(--ink-3)'" />
            </button>
          </div>
        </AppCard>

        <AppCard :padding="14" class="pending-card">
          <div class="pending-title">
            <div>
              <div class="mono-label block-label">담은 음식</div>
              <p>선택한 음식을 한 번에 식단에 추가해요.</p>
            </div>
            <strong>{{ pendingItems.length }}개</strong>
          </div>
          <p v-if="!pendingItems.length" class="pending-empty">음식을 눌러 식단에 담아보세요.</p>
          <div v-else class="pending-list">
            <div v-for="item in pendingItems" :key="item.food.id" class="pending-item">
              <div class="pending-head">
                <span>{{ item.food.emoji }}</span>
                <div>
                  <strong>{{ item.food.name }}</strong>
                  <small>{{ mealKinds.find(k => k.id === activeKind)?.label }}에 추가</small>
                </div>
                <button class="pending-remove" type="button" aria-label="담은 음식 삭제" @click="removePendingItem(item.food.id)">×</button>
              </div>
              <div class="mode-row">
                <button v-if="hasServingInput(item.food)" :class="{ active: item.unitMode === 'serving' }" @click="setMode(item, 'serving')">{{ servingModeText(item) }}</button>
                <button v-if="isBasisFood(item.food)" :class="{ active: item.unitMode === 'gram' }" @click="setMode(item, 'gram')">{{ basisUnitLabel(item) }}</button>
              </div>
              <div class="qty-control">
                <button type="button" @click="adjustQty(item, -1)">-</button>
                <input v-model.number="item.qty" type="number" step="1" min="1">
                <span>{{ unitLabel(item) }}</span>
                <button type="button" @click="adjustQty(item, 1)">+</button>
              </div>
              <div class="preset-row">
                <button
                  v-for="preset in presetOptions(item)"
                  :key="preset"
                  type="button"
                  :class="{ active: Number(item.qty) === preset }"
                  @click="setQty(item, preset)"
                >
                  {{ formatPresetLabel(item, preset) }}
                </button>
                <span>직접 입력 가능</span>
              </div>
            </div>
          </div>
          <p v-if="saveError" class="save-error">{{ saveError }}</p>
          <AppButton full size="lg" :disabled="saving || !pendingItems.length" @click="commit">
            <AppIcon name="check" color="#fff" />{{ saving ? '적용 중...' : '선택한 음식 적용하기' }}
          </AppButton>
        </AppCard>

        <AppCard :padding="14" class="fav-card">
          <div class="mono-label block-label">자주 먹은 음식</div>
          <p v-if="!favorites.length" class="empty-text">자주 먹은 음식 데이터가 없습니다.</p>
          <div v-else class="fav-list">
            <button v-for="food in favorites" :key="food.foodId" @click="selectFavorite(food.name)">
              <span>🍽️</span>{{ food.name }}
            </button>
          </div>
        </AppCard>
      </aside>

      <main class="right-panel">
        <div v-if="dayLoading" class="empty-panel">식단을 불러오는 중입니다.</div>
        <div v-else-if="dayError" class="empty-panel">{{ dayError }}</div>
        <div v-else class="quadrants">
          <MealQuadrant
            v-for="kind in mealKinds"
            :key="kind.id"
            :kind="kind"
            :entries="byKind[kind.id] || []"
            :active="activeKind === kind.id"
            @activate="activeKind = kind.id"
            @remove="removeDiet"
          />
        </div>

        <AppCard>
          <div class="section-title">
            <div>
              <div class="section-title-main">오늘 합계</div>
              <div class="section-title-sub">선택 날짜 누적 영양 · 목표 대비</div>
            </div>
          </div>
          <div class="summary-grid">
            <NutrientSummary label="칼로리" :value="summary.totalCalories" :max="summary.targetCalories" unit="kcal" />
            <NutrientSummary label="단백질" :value="summary.totalProtein" :max="summary.targetProtein" unit="g" :warn="isLow(summary.totalProtein, summary.targetProtein)" />
            <NutrientSummary label="탄수화물" :value="summary.totalCarbs" :max="summary.targetCarbs" unit="g" />
            <NutrientSummary label="지방" :value="summary.totalFat" :max="summary.targetFat" unit="g" />
            <NutrientSummary label="식이섬유" :value="summary.totalFiber" :max="summary.targetFiber" unit="g" :warn="isLow(summary.totalFiber, summary.targetFiber)" />
          </div>
        </AppCard>
      </main>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import MealQuadrant from './parts/MealQuadrant.vue'
import NutrientSummary from './parts/NutrientSummary.vue'
import { mealKinds, recordsByKind, registerApiFoods, totalsFor, type Food, type MealKindId, type MealLog } from '../services/mock/nyamnyamMock'
import { dietApi, logsFromDietDay, mealKindToBackendMealType, toBackendInputUnit, type DietDayResponse, type DietItemRequest } from '../services/dietApi'
import { foodApi, type FoodSearchItem } from '../services/foodApi'
import { coachApi } from '../services/coachApi'

defineProps<{ logs: MealLog[] }>()
const emit = defineEmits<{ dietChanged: [] }>()

type PendingItem = { food: Food, qty: number, unitMode: 'serving' | 'gram' }

const activeKind = ref<MealKindId>('breakfast')
const query = ref('')
const selectedDate = ref(toDateInputValue(new Date()))
const showCalendar = ref(false)
const pendingItems = ref<PendingItem[]>([])
const apiFoods = ref<Food[]>([])
const favorites = ref<Array<{ foodId: number, name: string }>>([])
const dietDay = ref<DietDayResponse | null>(null)
const dayLogs = ref<MealLog[]>([])
const dayLoading = ref(false)
const dayError = ref('')
const foodLoading = ref(false)
const saving = ref(false)
const saveError = ref('')
let dayRequestId = 0
let foodRequestId = 0
let searchDebounceTimer: ReturnType<typeof setTimeout> | null = null

const byKind = computed(() => recordsByKind(dayLogs.value))
const fallbackTotals = computed(() => totalsFor(dayLogs.value))
const summary = computed(() => ({
  totalCalories: Number(dietDay.value?.dailySummary?.totalCalories ?? fallbackTotals.value.kcal),
  targetCalories: Number(dietDay.value?.dailySummary?.targetCalories ?? 0),
  totalProtein: Number(dietDay.value?.dailySummary?.totalProtein ?? fallbackTotals.value.p),
  targetProtein: Number(dietDay.value?.dailySummary?.targetProtein ?? 0),
  totalCarbs: Number(dietDay.value?.dailySummary?.totalCarbs ?? fallbackTotals.value.c),
  targetCarbs: Number(dietDay.value?.dailySummary?.targetCarbs ?? 0),
  totalFat: Number(dietDay.value?.dailySummary?.totalFat ?? fallbackTotals.value.f),
  targetFat: Number(dietDay.value?.dailySummary?.targetFat ?? 0),
  totalFiber: Number(dietDay.value?.dailySummary?.totalFiber ?? 0),
  targetFiber: Number(dietDay.value?.dailySummary?.targetFiber ?? 0)
}))
const filtered = computed(() => {
  if (apiFoods.value.length) return apiFoods.value
  return []
})
const selectedDateLabel = computed(() => formatDateLabel(selectedDate.value))
const todayDate = toDateInputValue(new Date())
const canGoForward = computed(() => selectedDate.value < todayDate)
function servingLabel(food: Food) {
  return ['개', '조각', '장', '컵', '봉지'].includes(food.unit) ? '개' : '인분'
}

function isBasisFood(food: Food) {
  return food.unit === 'g' || food.unit === 'ml'
}

function hasServingInput(food: Food) {
  return Boolean(food.pieceUnitAvailable || (isBasisFood(food) && food.gramPerServing))
}

function shiftDay(delta: number) {
  const date = new Date(`${selectedDate.value}T00:00:00`)
  date.setDate(date.getDate() + delta)
  const nextDate = toDateInputValue(date)
  selectedDate.value = nextDate > todayDate ? todayDate : nextDate
}

function syncDateFromCalendar() {
  if (selectedDate.value > todayDate) selectedDate.value = todayDate
  showCalendar.value = false
}

function toDateInputValue(date: Date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function formatDateLabel(value: string) {
  const date = new Date(`${value}T00:00:00`)
  return `${date.getMonth() + 1}월 ${date.getDate()}일`
}

function selectFood(food: Food) {
  const existing = pendingItems.value.find((item) => item.food.id === food.id)
  if (existing) {
    adjustQty(existing, 1)
    return
  }
  const unitMode = isBasisFood(food) ? 'gram' : 'serving'
  pendingItems.value.push({ food, qty: unitMode === 'gram' ? 100 : 1, unitMode })
  saveError.value = ''
}

function isPendingFood(foodId: string) {
  return pendingItems.value.some((item) => item.food.id === foodId)
}

function removePendingItem(foodId: string) {
  pendingItems.value = pendingItems.value.filter((item) => item.food.id !== foodId)
}

function setMode(item: PendingItem, unitMode: 'serving' | 'gram') {
  if (unitMode === 'serving' && !hasServingInput(item.food)) return
  if (unitMode === 'gram' && !isBasisFood(item.food)) return
  item.unitMode = unitMode
  item.qty = unitMode === 'gram' ? 100 : 1
}

function adjustQty(item: PendingItem, delta: number) {
  item.qty = Math.max(1, Number(item.qty || 1) + delta)
}

function setQty(item: PendingItem, qty: number) {
  item.qty = Math.max(1, Number(qty || 1))
}

function presetOptions(item: PendingItem) {
  return item.unitMode === 'gram' ? [50, 100, 150, 200] : [1, 2, 3]
}

function formatPresetLabel(item: PendingItem, qty: number) {
  return `${qty}${unitLabel(item)}`
}

function servingModeText(item: PendingItem) {
  return servingLabel(item.food) === '개' ? '개수' : '인분'
}

function basisUnitLabel(item: PendingItem) {
  return item.food.unit === 'ml' ? 'ml' : 'g'
}

function unitLabel(item: PendingItem) {
  return item.unitMode === 'gram' ? basisUnitLabel(item) : servingLabel(item.food)
}

function selectFavorite(foodName: string) {
  query.value = foodName
}

async function commit() {
  if (!pendingItems.value.length || saving.value) return
  saving.value = true
  saveError.value = ''

  try {
    const meal = dietDay.value?.meals.find((item) => item.mealType === mealKindToBackendMealType(activeKind.value))
    const pendingRequests = pendingItems.value.map(toDietItemRequest)
    const saveRequest = {
      mealType: mealKindToBackendMealType(activeKind.value),
      eatenAt: meal?.eatenAt || `${selectedDate.value}T12:00:00`,
      memo: meal?.memo || '',
      items: mergeDietItems([...(meal ? itemsFromMeal(meal) : []), ...pendingRequests])
    }
    let savedDietId: number | string | undefined
    let shouldCreateFeedback = false
    if (meal?.dietId) {
      const updated = await dietApi.update(meal.dietId, saveRequest)
      savedDietId = updated.dietId || meal.dietId
    } else {
      const created = await dietApi.createMeal(saveRequest)
      savedDietId = created.dietId
      shouldCreateFeedback = true
    }
    if (savedDietId && shouldCreateFeedback) {
      void coachApi.createDietFeedbacksForAllCoaches(savedDietId).catch((error) => {
        console.warn('Coach feedback create API failed', error)
      })
    }
    pendingItems.value = []
    query.value = ''
    await loadDietDay()
    emit('dietChanged')
  } catch (error) {
    console.warn('Diet create API failed', error)
    saveError.value = errorMessage(error)
  } finally {
    saving.value = false
  }
}

async function removeDiet(id: string) {
  try {
    const meal = dietDay.value?.meals.find((candidate) => candidate.items?.some((item) => String(item.dietItemId) === id))
    if (!meal?.dietId) return
    const itemIndex = (meal.items || []).findIndex((dietItem) => String(dietItem.dietItemId) === id)
    const remainingItems = itemsFromMeal(meal)
    if (itemIndex >= 0) remainingItems.splice(itemIndex, 1)
    if (remainingItems.length) {
      await dietApi.update(meal.dietId, {
        mealType: meal.mealType,
        eatenAt: meal.eatenAt || `${selectedDate.value}T12:00:00`,
        memo: meal.memo || '',
        items: remainingItems
      })
    } else {
      await dietApi.remove(meal.dietId)
    }
    await loadDietDay()
    emit('dietChanged')
  } catch (error) {
    console.warn('Diet delete API failed', error)
  }
}

function itemsFromMeal(meal: NonNullable<DietDayResponse['meals'][number]>): DietItemRequest[] {
  return (meal.items || []).map((item) => ({
    foodId: Number(item.foodId),
    inputAmount: Number(item.inputAmount || 1),
    inputUnit: toBackendInputUnit(item.inputUnit)
  }))
}

function toDietItemRequest(item: PendingItem): DietItemRequest {
  const food = item.food
  const amount = Math.max(1, Number(item.qty || 1))
  const servingAmount = Number(food.gramPerServing || 0)
  const convertServingToBasis = item.unitMode === 'serving' && isBasisFood(food) && servingAmount > 0

  return {
    foodId: Number(food.id),
    inputAmount: convertServingToBasis ? amount * servingAmount : amount,
    inputUnit: convertServingToBasis
      ? toBackendInputUnit(food.unit)
      : toBackendInputUnit(item.unitMode === 'gram' ? food.unit : 'piece')
  }
}

function mergeDietItems(items: DietItemRequest[]) {
  const merged = new Map<string, DietItemRequest>()
  for (const item of items) {
    const key = `${item.foodId}-${item.inputUnit}`
    const existing = merged.get(key)
    if (existing) existing.inputAmount += item.inputAmount
    else merged.set(key, { ...item })
  }
  return [...merged.values()]
}

async function loadDietDay() {
  const requestId = ++dayRequestId
  dayLoading.value = true
  dayError.value = ''
  dietDay.value = null
  dayLogs.value = []

  try {
    const day = await dietApi.getDay(selectedDate.value)
    if (requestId !== dayRequestId) return
    dietDay.value = day
    dayLogs.value = logsFromDietDay(day)
  } catch (error) {
    console.warn('Diet day API failed', error)
    if (requestId === dayRequestId) dayError.value = errorMessage(error)
  } finally {
    if (requestId === dayRequestId) dayLoading.value = false
  }
}

async function loadFoods() {
  const keyword = query.value.trim()
  const requestId = ++foodRequestId
  if (!keyword) {
    apiFoods.value = []
    foodLoading.value = false
    return
  }

  foodLoading.value = true
  try {
    const result = await foodApi.search(keyword, 0, 20)
    if (requestId !== foodRequestId) return
    apiFoods.value = result.foods.map(toFood)
    registerApiFoods(apiFoods.value)
  } catch (error) {
    console.warn('Food search API failed', error)
    if (requestId === foodRequestId) apiFoods.value = []
  } finally {
    if (requestId === foodRequestId) foodLoading.value = false
  }
}

async function loadFrequentFoods() {
  try {
    const result = await foodApi.frequent()
    favorites.value = result.foods || []
  } catch (error) {
    console.warn('Frequent foods API failed', error)
    favorites.value = []
  }
}

function toFood(food: FoodSearchItem): Food {
  const amount = Number(food.nutritionBasisAmount || food.servingAmount || 100)
  const unit = food.nutritionBasisUnit || food.servingUnit || 'g'
  const gramPerPiece = Number(food.gramPerPiece || 0)
  // g/ml 기반 음식의 1인분(1개) 그램/ml 환산값
  // gramPerPiece가 있으면 우선 사용, 없으면 servingAmount → nutritionBasisAmount 순으로 대체
  const gramPerServing = (unit === 'g' || unit === 'ml')
    ? Number(gramPerPiece || food.servingAmount || food.nutritionBasisAmount || 100)
    : gramPerPiece || undefined
  return {
    id: String(food.foodId),
    name: food.name,
    emoji: '🍽️',
    src: food.brand || food.category || 'DB',
    per: `${amount}${unit}`,
    kcal: Number(food.calories || 0),
    p: Number(food.protein || 0),
    c: Number(food.carbs || 0),
    f: Number(food.fat || 0),
    unit,
    presets: unit === 'g' ? [50, 100, 150] : [1, 2, 3],
    gramPerServing,
    pieceUnitAvailable: gramPerPiece > 0
  }
}

function isLow(value: number, target: number) {
  return target > 0 && value < target * 0.7
}

function errorMessage(error: unknown) {
  if (error instanceof Error) return error.message
  return '식단을 불러오지 못했습니다.'
}

watch(selectedDate, () => {
  pendingItems.value = []
  saveError.value = ''
  void loadDietDay()
})
watch(activeKind, () => {
  pendingItems.value = []
  saveError.value = ''
})
watch(query, () => {
  if (searchDebounceTimer) clearTimeout(searchDebounceTimer)
  searchDebounceTimer = setTimeout(() => { void loadFoods() }, 300)
})
onMounted(() => {
  void loadDietDay()
  void loadFrequentFoods()
})
</script>

<style scoped>
.date-box { position: relative; display: flex; align-items: center; gap: 4px; background: var(--surface); border: 1px solid var(--border); border-radius: 12px; padding: 4px 6px; box-shadow: var(--shadow); }
.date-open { border: 0; background: transparent; padding: 6px 14px; font-family: var(--mono); font-size: 13px; font-weight: 800; min-width: 200px; text-align: center; cursor: pointer; color: var(--ink); }
.calendar-pop { position: absolute; right: 44px; top: 44px; z-index: 10; border: 1px solid var(--border-strong); border-radius: 10px; padding: 8px; box-shadow: var(--shadow-lg); }
.meals-layout { display: grid; grid-template-columns: 360px 1fr; gap: 20px; }
.left-panel, .right-panel { display: flex; flex-direction: column; gap: 14px; }
.block-label { margin-bottom: 10px; }
.kind-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 6px; }
.kind-btn { padding: 10px 4px; border: 1.5px solid var(--border); background: var(--surface); border-radius: 10px; cursor: pointer; font-weight: 700; font-size: 13px; color: var(--ink); display: flex; flex-direction: column; gap: 2px; align-items: center; }
.kind-btn span { font-size: 18px; }
.kind-btn.active { background: var(--accent); border-color: var(--accent); color: #fff; }
.search-list { margin-top: 10px; display: flex; flex-direction: column; gap: 4px; max-height: 220px; overflow: auto; }
.food-row { display: flex; align-items: center; gap: 8px; padding: 8px 10px; border-radius: 9px; border: 1.5px solid var(--border); background: var(--surface); cursor: pointer; text-align: left; color: var(--ink); }
.food-row.active { border-color: var(--accent); background: var(--accent-soft); }
.food-emoji { font-size: 18px; }
.food-info { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.food-info strong { font-size: 12px; }
.food-info small { font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
.pending-card { background: var(--accent-soft); border-color: var(--accent); }
.pending-title { display: flex; justify-content: space-between; gap: 12px; align-items: flex-start; margin-bottom: 10px; }
.pending-title .block-label { margin-bottom: 3px; }
.pending-title p { margin: 0; color: var(--ink-2); font-size: 11px; line-height: 1.45; }
.pending-title > strong { font-family: var(--mono); color: var(--accent); font-size: 12px; white-space: nowrap; }
.pending-empty { margin: 0 0 12px; padding: 16px 10px; text-align: center; border: 1px dashed var(--border-strong); border-radius: 10px; color: var(--ink-3); font-size: 12px; background: var(--surface); }
.pending-list { display: flex; flex-direction: column; gap: 8px; max-height: 340px; overflow-y: auto; margin-bottom: 10px; }
.pending-item { padding: 10px; border: 1px solid var(--border); border-radius: 10px; background: var(--surface); }
.pending-head { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.pending-head span { font-size: 22px; }
.pending-head > div { flex: 1; min-width: 0; }
.pending-head small { display: block; font-family: var(--mono); color: var(--ink-2); font-size: 10px; margin-top: 2px; }
.pending-remove { width: 26px; height: 26px; border: 0; border-radius: 50%; background: var(--surface-alt); color: var(--ink-3); cursor: pointer; font-size: 17px; }
.mode-row { display: grid; grid-template-columns: 1fr 1fr; gap: 6px; margin-bottom: 10px; }
.mode-row button { border: 1.5px solid var(--border); background: var(--surface); border-radius: 10px; padding: 9px; font-weight: 800; cursor: pointer; color: var(--ink-2); }
.mode-row button.active { border-color: var(--accent); background: var(--accent); color: #fff; }
.qty-control { display: grid; grid-template-columns: 36px 1fr auto 36px; gap: 6px; align-items: center; margin-bottom: 10px; }
.qty-control button { height: 36px; border: 1px solid var(--border); border-radius: 10px; background: var(--surface); cursor: pointer; font-size: 16px; font-weight: 900; }
.qty-control input { height: 36px; border: 1.5px solid var(--border-strong); border-radius: 10px; padding: 0 10px; text-align: center; font-family: var(--mono); }
.qty-control span { font-family: var(--mono); color: var(--ink-3); font-size: 12px; padding-right: 4px; }
.preset-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 6px; align-items: center; }
.preset-row button { border: 1px solid var(--border); background: var(--surface-alt); border-radius: 999px; padding: 7px 4px; font-family: var(--mono); font-size: 11px; font-weight: 800; cursor: pointer; color: var(--ink-2); }
.preset-row button.active { border-color: var(--accent); background: var(--accent-soft); color: var(--accent); }
.preset-row span { grid-column: 1 / -1; color: var(--ink-3); font-size: 10px; text-align: right; }
.save-error { margin: 0 0 10px; color: var(--danger); font-size: 12px; font-weight: 700; }
.fav-card { flex: 1; }
.fav-list { display: flex; flex-wrap: wrap; gap: 6px; }
.fav-list button { display: inline-flex; align-items: center; gap: 5px; padding: 6px 12px; border: 1px solid var(--border); background: var(--surface-alt); border-radius: 999px; font-size: 12px; font-weight: 600; cursor: pointer; }
.quadrants { display: grid; grid-template-columns: 1fr 1fr; grid-template-rows: 1fr 1fr; gap: 12px; min-height: 480px; }
.summary-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 18px; }
.empty-panel { min-height: 480px; border: 1px dashed var(--border); border-radius: 12px; background: var(--surface-alt); display: flex; align-items: center; justify-content: center; color: var(--ink-3); font-weight: 700; }
.empty-text { padding: 10px 12px; border: 1px dashed var(--border); border-radius: 10px; background: var(--surface-alt); color: var(--ink-3); font-size: 12px; }
</style>
