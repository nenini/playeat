<template>
  <g stroke="var(--ink)" stroke-width="2.2" stroke-linecap="round" fill="var(--ink)">
    <circle :cx="cx - es" :cy="cy - 2" :r="er" />
    <circle :cx="cx + es" :cy="cy - 2" :r="er" />
    <circle :cx="cx - es + 1.2" :cy="cy - 3.5" r="0.9" fill="#fff" stroke="none" />
    <circle :cx="cx + es + 1.2" :cy="cy - 3.5" r="0.9" fill="#fff" stroke="none" />
    <circle :cx="cx - es - 6" :cy="cy + 4" r="3" fill="var(--accent)" opacity="0.35" stroke="none" />
    <circle :cx="cx + es + 6" :cy="cy + 4" r="3" fill="var(--accent)" opacity="0.35" stroke="none" />
    <ellipse v-if="mood === 'hungry'" :cx="cx" :cy="mY + 1" rx="3" ry="2.2" />
    <path v-else-if="mood === 'sad'" :d="`M${cx - 5} ${mY + 2} q5 -5 10 0`" fill="none" />
    <path v-else :d="`M${cx - 5} ${mY} q5 5 10 0`" fill="none" stroke="rgba(41,37,31,0)" />
  </g>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{ cx?: number, cy?: number, mood?: string, es?: number, er?: number, my?: number }>(), {
  cx: 60,
  cy: 60,
  mood: 'happy',
  es: 14,
  er: 3.2
})
const mY = computed(() => props.my ?? props.cy + 12)
</script>

<script lang="ts">
export default { name: 'NyamnyamFace' }
</script>
