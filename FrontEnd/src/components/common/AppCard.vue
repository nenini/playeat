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
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 14px;
  box-shadow: var(--shadow);
}
.app-card.accent { border-color: var(--accent); }
</style>
