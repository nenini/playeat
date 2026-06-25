<template>
  <div class="stat" :class="{ accent }">
    <small>{{ label.toUpperCase() }}</small>
    <strong>{{ value }}</strong>
  </div>
</template>

<script setup lang="ts">
defineProps<{ label: string, value: string, accent?: boolean }>()
</script>
<style scoped>.stat{padding:12px;border-radius:10px;background:var(--surface);border:1px solid var(--border)}.stat.accent{background:var(--accent-soft);border-color:var(--accent)}small{font-family:var(--mono);color:var(--ink-3);font-size:10px;letter-spacing:.5px}strong{display:block;font-family:var(--mono);font-size:22px;font-weight:800;margin-top:4px}.accent strong{color:var(--accent-dark)}</style>
