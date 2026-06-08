<template>
  <section>
    <div class="page-title-row">
      <div><div class="mono-label">MEALS · 매일 식단</div><div class="title-lg">오늘의 식단</div></div>
      <div class="date-box"><AppButton variant="ghost" size="sm"><AppIcon name="chev-l" :size="14" /></AppButton><strong>{{ dayLabel() }}</strong><AppButton variant="ghost" size="sm"><AppIcon name="chev-r" :size="14" /></AppButton></div>
    </div>
    <div class="meals-layout">
      <aside class="left-panel">
        <AppCard :padding="14">
          <div class="mono-label block-label">① 끼니 선택</div>
          <div class="kind-grid">
            <button v-for="kind in mealKinds" :key="kind.id" class="kind-btn" :class="{ active: activeKind === kind.id }" @click="activeKind = kind.id">
              <span>{{ kind.emoji }}</span><strong>{{ kind.label }}</strong>
            </button>
          </div>
        </AppCard>
        <AppCard :padding="14">
          <div class="mono-label block-label">② 음식 검색 <span>· 식약처 DB</span></div>
          <label class="app-input"><AppIcon name="search" color="var(--ink-3)" /><input v-model="query" placeholder="음식 이름 (예: 계란, 닭가슴살)" @input="pending = null"></label>
          <div class="search-list">
            <button v-for="food in filtered" :key="food.id" class="food-row" :class="{ active: pending?.food.id === food.id }" @click="pending = { food, qty: food.presets[Math.min(1, food.presets.length - 1)] || 1 }">
              <span class="food-emoji">{{ food.emoji }}</span>
              <span class="food-info"><strong>{{ food.name }}</strong><small>{{ food.kcal }}kcal · P{{ food.p }} C{{ food.c }} F{{ food.f }} / {{ food.per }}</small></span>
              <AppIcon :name="pending?.food.id === food.id ? 'check' : 'plus'" :size="14" :color="pending?.food.id === food.id ? 'var(--accent)' : 'var(--ink-3)'" />
            </button>
          </div>
        </AppCard>
        <AppCard v-if="pending" :padding="14" class="pending-card">
          <div class="mono-label block-label">③ 중량 · 추가</div>
          <div class="pending-head"><span>{{ pending.food.emoji }}</span><div><strong>{{ pending.food.name }}</strong><small>{{ mealKinds.find(k => k.id === activeKind)?.label }}에 추가</small></div></div>
          <div class="preset-row"><button v-for="preset in pending.food.presets" :key="preset" :class="{ active: pending.qty === preset }" @click="pending.qty = preset">{{ preset }}{{ pending.food.unit }}</button></div>
          <AppButton full size="lg" @click="commit"><AppIcon name="plus" color="#fff" />{{ pending.food.name }} {{ pending.qty }}{{ pending.food.unit }} 추가</AppButton>
        </AppCard>
        <AppCard :padding="14" class="fav-card">
          <div class="mono-label block-label">자주 먹는 음식</div>
          <div class="fav-list"><button v-for="food in favorites" :key="food.id" @click="selectFavorite(food)"><span>{{ food.emoji }}</span>{{ food.name }}</button></div>
        </AppCard>
      </aside>
      <main class="right-panel">
        <div class="quadrants">
          <MealQuadrant v-for="kind in mealKinds" :key="kind.id" :kind="kind" :entries="byKind[kind.id] || []" :active="activeKind === kind.id" @activate="activeKind = kind.id" @remove="$emit('removeLog', $event)" />
        </div>
        <AppCard>
          <div class="section-title"><div><div class="section-title-main">오늘 합계</div><div class="section-title-sub">오늘 누적 영양 · 목표 대비</div></div></div>
          <div class="summary-grid">
            <NutrientSummary label="칼로리" :value="totals.kcal" :max="goalDefaults.kcal" unit="kcal" />
            <NutrientSummary label="단백질" :value="totals.p" :max="goalDefaults.p" unit="g" :warn="totals.p < goalDefaults.p * .7" />
            <NutrientSummary label="탄수화물" :value="totals.c" :max="goalDefaults.c" unit="g" />
            <NutrientSummary label="지방" :value="totals.f" :max="goalDefaults.f" unit="g" />
            <NutrientSummary label="채소" :value="veggieCount(logs)" :max="goalDefaults.veggies" unit="종" :warn="veggieCount(logs) < goalDefaults.veggies" />
          </div>
        </AppCard>
      </main>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import MealQuadrant from './parts/MealQuadrant.vue'
import NutrientSummary from './parts/NutrientSummary.vue'
import { dayLabel, favoriteIds, foodDb, goalDefaults, mealKinds, recordsByKind, totalsFor, veggieCount, type Food, type MealKindId, type MealLog } from '../services/mock/nyamnyamMock'

const props = defineProps<{ logs: MealLog[] }>()
const emit = defineEmits<{ addLog: [payload: { foodId: string, mealKind: MealKindId, qty: number }], removeLog: [id: string] }>()
const activeKind = ref<MealKindId>('breakfast')
const query = ref('')
const pending = ref<{ food: Food, qty: number } | null>(null)
const totals = computed(() => totalsFor(props.logs))
const byKind = computed(() => recordsByKind(props.logs))
const filtered = computed(() => query.value.trim() ? foodDb.filter((food) => food.name.includes(query.value.trim())) : foodDb.slice(0, 6))
const favorites = computed(() => favoriteIds.map((id) => foodDb.find((food) => food.id === id)).filter(Boolean) as Food[])
function selectFavorite(food: Food) { query.value = food.name; pending.value = { food, qty: food.presets[Math.min(1, food.presets.length - 1)] || 1 } }
function commit() { if (!pending.value) return; emit('addLog', { foodId: pending.value.food.id, mealKind: activeKind.value, qty: pending.value.qty }); pending.value = null; query.value = '' }
</script>

<style scoped>
.date-box { display: flex; align-items: center; gap: 4px; background: var(--surface); border: 1px solid var(--border); border-radius: 12px; padding: 4px 6px; box-shadow: var(--shadow); }
.date-box strong { padding: 6px 14px; font-family: var(--mono); font-size: 13px; min-width: 200px; text-align: center; }
.meals-layout { display: grid; grid-template-columns: 360px 1fr; gap: 20px; }
.left-panel, .right-panel { display: flex; flex-direction: column; gap: 14px; }
.block-label { margin-bottom: 10px; }
.kind-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 6px; }
.kind-btn { padding: 10px 4px; border: 1.5px solid var(--border); background: var(--surface); border-radius: 10px; cursor: pointer; font-weight: 700; font-size: 13px; color: var(--ink); display: flex; flex-direction: column; gap: 2px; align-items: center; }
.kind-btn span { font-size: 18px; } .kind-btn.active { background: var(--accent); border-color: var(--accent); color: #fff; }
.search-list { margin-top: 10px; display: flex; flex-direction: column; gap: 4px; max-height: 220px; overflow: auto; }
.food-row { display: flex; align-items: center; gap: 8px; padding: 8px 10px; border-radius: 9px; border: 1.5px solid var(--border); background: var(--surface); cursor: pointer; text-align: left; color: var(--ink); }
.food-row.active { border-color: var(--accent); background: var(--accent-soft); }
.food-emoji { font-size: 18px; } .food-info { flex: 1; min-width: 0; display: flex; flex-direction: column; } .food-info strong { font-size: 12px; } .food-info small { font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
.pending-card { background: var(--accent-soft); border-color: var(--accent); }
.pending-head { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; } .pending-head span { font-size: 22px; } .pending-head small { display: block; font-family: var(--mono); color: var(--ink-2); font-size: 10px; margin-top: 2px; }
.preset-row { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 10px; }
.preset-row button { padding: 6px 12px; border: 1.5px solid var(--border); background: var(--surface); border-radius: 999px; font-weight: 700; color: var(--ink-2); cursor: pointer; } .preset-row button.active { background: var(--accent); color: #fff; border-color: var(--accent); }
.fav-card { flex: 1; } .fav-list { display: flex; flex-wrap: wrap; gap: 6px; }
.fav-list button { display: inline-flex; align-items: center; gap: 5px; padding: 6px 12px; border: 1px solid var(--border); background: var(--surface-alt); border-radius: 999px; font-size: 12px; font-weight: 600; cursor: pointer; }
.quadrants { display: grid; grid-template-columns: 1fr 1fr; grid-template-rows: 1fr 1fr; gap: 12px; min-height: 480px; }
.summary-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 18px; }
</style>
