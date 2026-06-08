<template>
  <div>
    <div class="head"><span :class="{ warn }">{{ label }}</span><small>{{ pct }}%</small></div>
    <div class="value">{{ Math.round(value) }}<span>/{{ max }}{{ unit }}</span></div>
    <ProgressBar :value="value" :max="max" :tone="warn ? 'accent' : 'neutral'" :height="5" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import ProgressBar from '../../components/common/ProgressBar.vue'
const props = defineProps<{ label: string, value: number, max: number, unit: string, warn?: boolean }>()
const pct = computed(() => Math.round(props.value / props.max * 100))
</script>

<script lang="ts">
export default { name: 'NutrientSummary' }
</script>

<style scoped>
.head { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 4px; }
.head span { font-size: 12px; font-weight: 700; } .head span.warn { color: var(--accent); } .head small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.value { font-family: var(--mono); font-size: 16px; font-weight: 700; margin-bottom: 6px; }
.value span { font-size: 10px; color: var(--ink-3); margin-left: 2px; }
</style>
