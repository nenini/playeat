<template>
  <svg :viewBox="`0 0 ${size} ${size}`" :width="size" :height="size">
    <circle :cx="size / 2" :cy="size / 2" :r="r" fill="none" stroke="var(--border)" stroke-width="10" />
    <circle :cx="size / 2" :cy="size / 2" :r="r" fill="none" stroke="var(--accent)" stroke-width="10" :stroke-dasharray="dash" :stroke-dashoffset="c / 4" :transform="`rotate(-90 ${size / 2} ${size / 2})`" stroke-linecap="round" />
    <text :x="size / 2" :y="size / 2 + 8" text-anchor="middle" font-family="var(--mono)" :font-size="size * .3" font-weight="700" fill="var(--ink)">{{ value }}</text>
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{ value: number, size?: number }>(), { size: 110 })
const r = computed(() => props.size / 2 - 10)
const c = computed(() => 2 * Math.PI * r.value)
const dash = computed(() => `${c.value * props.value / 100} ${c.value}`)
</script>
