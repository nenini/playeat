<template>
  <div v-if="msg.system" class="system"><AppPill tone="accent" size="sm">SYSTEM</AppPill><span>{{ msg.text }}</span></div>
  <div v-else class="bubble" :class="{ mine: msg.mine }"><div class="avatar">{{ msg.name[0] }}</div><div class="body"><div class="meta"><strong>{{ msg.name }}</strong><small>{{ msg.time }}</small></div><p>{{ msg.text }}</p></div></div>
</template>
<script setup lang="ts">
import AppPill from '../../components/common/AppPill.vue'
defineProps<{ msg: any }>()
</script>
<style scoped>
.system { display: flex; align-items: center; gap: 8px; padding: 6px 12px; border-radius: 8px; background: var(--accent-soft); border: 1px dashed var(--accent); align-self: center; max-width: 80%; } .system span { font-family: var(--mono); font-size: 12px; color: var(--ink-2); }
.bubble { display: flex; gap: 10px; padding: 4px 0; } .bubble.mine { flex-direction: row-reverse; } .avatar { width: 34px; height: 34px; border-radius: 17px; background: var(--yolk); display: flex; align-items: center; justify-content: center; font-weight: 800; } .mine .avatar { background: var(--accent); color: #fff; }
.body { flex: 1; min-width: 0; } .mine .body { text-align: right; } .meta { display: flex; gap: 6px; align-items: baseline; } .mine .meta { flex-direction: row-reverse; } .meta strong { font-size: 12px; } .meta small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
p { display: inline-block; margin: 4px 0 0; padding: 8px 12px; background: var(--surface-alt); border-radius: 4px 14px 14px 14px; font-size: 13px; line-height: 1.5; max-width: 90%; text-align: left; } .mine p { background: var(--accent); color: #fff; border-radius: 14px 14px 4px 14px; }
</style>
