<template>
  <span class="app-pill" :class="[tone, size]"><slot /></span>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  tone?: 'neutral' | 'accent' | 'ok' | 'bad' | 'dark'
  size?: 'sm' | 'md'
}>(), {
  tone: 'neutral',
  size: 'md'
})
</script>

<script lang="ts">
export default { name: 'AppPill' }
</script>

<style scoped>
.app-pill {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border-radius: 999px;
  font-weight: 800;
  border: 1px solid var(--border);
}
.md { padding: 4px 10px; font-size: 11px; }
.sm { padding: 2px 8px; font-size: 10px; }
.neutral { color: var(--ink-2); background: var(--surface-alt); border-color: var(--border); }
.accent { color: var(--accent-dark); background: linear-gradient(180deg,#fff2e7,var(--accent-soft)); border-color: var(--accent); }
.ok { color: var(--ok); background: var(--ok-soft); border-color: var(--ok); }
.bad { color: var(--bad); background: var(--bad-soft); border-color: var(--bad); }
.dark { color: #fff; background: linear-gradient(180deg,#514139,var(--ink)); border-color: var(--ink); }
</style>
