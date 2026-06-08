<template>
  <div>
    <div class="goal-head"><span>{{ goal.name }}</span><span><b>{{ Math.round(goal.value) }}<i>/{{ goal.max }}{{ goal.unit }}</i></b><AppPill :tone="tone" size="sm">{{ label }}</AppPill></span></div>
    <ProgressBar :value="goal.value" :max="goal.max" :tone="tone" :height="6" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import AppPill from '../../components/common/AppPill.vue'
import ProgressBar from '../../components/common/ProgressBar.vue'
const props = defineProps<{ goal: { name: string, value: number, max: number, unit: string, state: string } }>()
const tone = computed(() => props.goal.state === 'over' ? 'bad' : props.goal.state === 'low' ? 'accent' : 'ok')
const label = computed(() => props.goal.state === 'over' ? '↑ 초과' : props.goal.state === 'low' ? '부족' : '적정')
</script>

<style scoped>
.goal-head { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 4px; }
.goal-head > span:first-child { font-size: 12px; font-weight: 700; }
.goal-head > span:last-child { display: inline-flex; align-items: center; gap: 6px; }
b { font-family: var(--mono); font-size: 11px; color: var(--ink-2); font-weight: 400; } i { color: var(--ink-3); font-style: normal; }
</style>
