<template>
  <div class="app-card" :class="{ accent }" :style="{ padding: `${padding}px`, ...styleObject }">
    <slot />
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  padding?: number
  accent?: boolean
  styleObject?: Record<string, string | number>
}>(), {
  padding: 18,
  accent: false,
  styleObject: () => ({})
})
</script>

<script lang="ts">
export default { name: 'AppCard' }
</script>

<style scoped>
.app-card {
  background: linear-gradient(180deg, rgba(255,255,255,.98), rgba(255,252,247,.98));
  border: 1px solid var(--border);
  border-radius: 18px;
  box-shadow: var(--shadow);
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease;
}
.app-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-lg); border-color: var(--border-strong); }
.app-card.accent { border-color: var(--accent); }
</style>
