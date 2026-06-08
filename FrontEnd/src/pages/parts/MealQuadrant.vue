<template>
  <AppCard :padding="0" class="quadrant" :class="{ active, empty: entries.length === 0 }">
      <div class="quad-head" @click="$emit('activate')">
      <div class="kind"><div>{{ kind.emoji }}</div><span><strong>{{ kind.label }}</strong><small>{{ kind.window }}시</small></span></div>
      <AppPill :tone="entries.length ? 'ok' : 'neutral'" size="sm">{{ entries.length ? `${Math.round(total.kcal)} kcal` : '— 미기록' }}</AppPill>
    </div>
    <div v-if="entries.length" class="nutrition-line">탄 {{ Math.round(total.c) }}g · 단 {{ Math.round(total.p) }}g · 지 {{ Math.round(total.f) }}g</div>
    <div class="quad-body">
      <div v-if="entries.length === 0" class="empty-msg">← 좌측에서 검색 후 추가</div>
      <div v-for="entry in entries" v-else :key="entry.id" class="entry-row">
        <span>{{ food(entry.foodId)?.emoji }}</span><strong>{{ food(entry.foodId)?.name }}</strong><small>{{ entry.qty }}{{ food(entry.foodId)?.unit }}</small><small>{{ kcal(entry) }}kcal</small><button @click.stop="$emit('remove', entry.id)">×</button>
      </div>
    </div>
  </AppCard>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import AppCard from '../../components/common/AppCard.vue'
import AppPill from '../../components/common/AppPill.vue'
import { foodDb, totalsFor, type MealLog } from '../../services/mock/nyamnyamMock'

const props = defineProps<{ kind: { id: string, label: string, emoji: string, window: string }, entries: MealLog[], active: boolean }>()
defineEmits<{ activate: [], remove: [id: string] }>()
const total = computed(() => totalsFor(props.entries))
const food = (id: string) => foodDb.find((item) => item.id === id)
function kcal(entry: MealLog) {
  const f = food(entry.foodId)
  const per = Number(String(f?.per || '').match(/(\d+)/)?.[1] || 1)
  return Math.round((f?.kcal || 0) * (entry.qty / per))
}
</script>

<style scoped>
.quadrant { display: flex; flex-direction: column; }
.quadrant.active { border-color: var(--accent); box-shadow: 0 0 0 3px var(--accent-soft), var(--shadow); }
.quad-head { padding: 14px 16px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid var(--border); cursor: pointer; }
.quadrant.empty .quad-head { border-bottom: 0; }
.kind { display: flex; align-items: center; gap: 10px; }
.kind > div { width: 36px; height: 36px; border-radius: 10px; background: var(--surface-alt); display: flex; align-items: center; justify-content: center; font-size: 18px; }
.kind span { display: flex; flex-direction: column; } .kind strong { font-size: 14px; } .kind small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.quad-body { flex: 1; padding: 8px 12px; display: flex; flex-direction: column; gap: 4px; overflow: auto; }
.nutrition-line { padding: 7px 16px; border-bottom: 1px dashed var(--border); font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.empty-msg { flex: 1; display: flex; align-items: center; justify-content: center; color: var(--ink-3); font-size: 12px; font-style: italic; }
.entry-row { display: flex; align-items: center; gap: 8px; padding: 6px 8px; border-radius: 8px; background: var(--surface-alt); }
.entry-row strong { flex: 1; min-width: 0; font-size: 12px; } .entry-row small { font-family: var(--mono); font-size: 10px; color: var(--ink-2); } .entry-row button { border: 0; background: transparent; color: var(--ink-3); cursor: pointer; }
</style>
