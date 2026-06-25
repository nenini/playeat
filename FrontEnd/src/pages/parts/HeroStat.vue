<template>
  <div class="hero-stat">
    <div class="hero-label">{{ label }}</div>
    <div class="hero-value" :class="{ accent: tone === 'accent' }">{{ value }}<span v-if="unit">{{ unit }}</span></div>
    <div v-if="sub" class="hero-sub">{{ sub }}</div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ label: string, value: string | number, unit?: string, sub?: string, tone?: 'accent' }>()
</script>

<script lang="ts">
export default { name: 'HeroStat' }
</script>

<style scoped>
.hero-stat { background: var(--surface); border: 1.5px solid var(--border); border-radius: 14px; padding: 14px 18px; min-width: 200px; box-shadow: var(--shadow); }
.hero-label { font-family: var(--mono); font-size: 10px; color: var(--ink-3); letter-spacing: 1.2px; }
.hero-value { font-family: var(--mono); font-size: 28px; font-weight: 700; color: var(--ink); margin-top: 4px; line-height: 1; }
.hero-value.accent { color: var(--accent); }
.hero-value span { font-size: 14px; color: var(--ink-3); font-weight: 600; margin-left: 4px; }
.hero-sub { font-family: var(--mono); font-size: 11px; color: var(--ink-3); margin-top: 6px; }
</style>
