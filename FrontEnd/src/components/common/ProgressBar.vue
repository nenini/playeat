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
  background: #eadfd4;
  overflow: hidden;
  box-shadow: inset 0 2px 4px rgba(72,43,24,.12);
}
.bar-fill {
  height: 100%;
  transition: width .8s cubic-bezier(.2,.8,.2,1);
  position: relative;
  overflow: hidden;
}
.bar-fill:after { content: ""; position: absolute; inset: 0; background: linear-gradient(90deg, transparent, rgba(255,255,255,.46), transparent); animation: bar-shine 2.2s infinite; }
.accent { background: linear-gradient(90deg,#ffad65,var(--accent)); }
.ok { background: linear-gradient(90deg,#76d995,var(--ok)); }
.bad { background: linear-gradient(90deg,#ff826d,var(--bad)); }
.dark, .neutral { background: var(--ink); }
@keyframes bar-shine { from { transform: translateX(-100%); } to { transform: translateX(100%); } }
</style>
