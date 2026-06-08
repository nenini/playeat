<template>
  <div class="member" :class="{ mine: member.mine, done: member.done, idle: member.idle }">
    <div class="top"><div class="avatar">{{ member.name[0] }}</div><div class="who"><strong>{{ member.name }}</strong><small>LV.{{ member.lv }} · {{ member.role }}</small></div><AppPill :tone="member.done ? 'ok' : member.over ? 'bad' : member.idle ? 'neutral' : 'accent'" size="sm">{{ member.done ? '✓ 완료' : member.over ? '↑ 초과' : member.idle ? '대기' : '진행' }}</AppPill></div>
    <div><div class="quest"><span>{{ member.quest }}</span><small>{{ member.progress }}/{{ member.total }} {{ member.unit }}</small></div><ProgressBar :value="Math.min(member.progress, member.total)" :max="member.total" :tone="member.done ? 'ok' : member.over ? 'bad' : 'accent'" :height="5" /></div>
  </div>
</template>
<script setup lang="ts">
import AppPill from '../../components/common/AppPill.vue'
import ProgressBar from '../../components/common/ProgressBar.vue'
defineProps<{ member: any }>()
</script>
<style scoped>
.member { padding: 12px; border: 1.5px solid var(--border); background: var(--surface); border-radius: 12px; box-shadow: var(--shadow); display: flex; flex-direction: column; gap: 8px; } .member.mine { border-color: var(--accent); background: var(--accent-soft); } .member.done:not(.mine) { background: var(--ok-soft); } .member.idle { opacity: .55; }
.top { display: flex; align-items: center; gap: 10px; } .avatar { width: 32px; height: 32px; border-radius: 16px; background: var(--yolk); display: flex; align-items: center; justify-content: center; font-weight: 800; } .mine .avatar { background: var(--accent); color: #fff; }
.who { flex: 1; min-width: 0; display: flex; flex-direction: column; } .who strong { font-size: 13px; } .who small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.quest { display: flex; justify-content: space-between; margin-bottom: 4px; } .quest span { font-size: 11px; } .quest small { font-family: var(--mono); color: var(--ink-2); font-size: 10px; }
</style>
