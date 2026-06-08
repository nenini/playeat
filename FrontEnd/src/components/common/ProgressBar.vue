<template>
  <div class="bar-wrap">
    <div v-if="label || sub" class="bar-head">
      <span v-if="label">{{ label }}</span>
      <span v-if="sub" class="bar-sub">{{ sub }}</span>
    </div>
    <div class="bar-track" :style="{ height: `${height}px`, borderRadius: `${height}px` }">
      <div class="bar-fill" :class="tone" :style="{ width: `${percent}%` }" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  value: number
  max?: number
  tone?: 'accent' | 'ok' | 'bad' | 'dark' | 'neutral'
  height?: number
  label?: string
  sub?: string
}>(), {
  max: 100,
  tone: 'accent',
  height: 8
})

const percent = computed(() => Math.min(100, Math.max(0, props.value / props.max * 100)))
</script>

<script lang="ts">
export default { name: 'ProgressBar' }
</script>

<style scoped>
.bar-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 4px;
  font-size: 11px;
  color: var(--ink);
  font-weight: 600;
}
.bar-sub {
  font-family: var(--mono);
  font-size: 10px;
  color: var(--ink-2);
}
.bar-track {
  background: var(--surface-alt);
  overflow: hidden;
}
.bar-fill {
  height: 100%;
  transition: width .3s ease;
}
.accent { background: var(--accent); }
.ok { background: var(--ok); }
.bad { background: var(--bad); }
.dark, .neutral { background: var(--ink); }
</style>
