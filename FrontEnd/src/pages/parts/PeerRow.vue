<template>
  <div class="peer-row"><strong>{{ name }}</strong><span><b>내 {{ mine }}</b><b class="peer">기준 {{ peer }}</b><AppPill :tone="tone" size="sm">{{ text }}</AppPill></span></div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import AppPill from '../../components/common/AppPill.vue'
const props = defineProps<{ name: string, mine: string, peer: string, state: string }>()
const tone = computed(() => props.state === 'over' ? 'bad' : props.state === 'low' ? 'accent' : 'ok')
const text = computed(() => props.state === 'over' ? '↑ 초과' : props.state === 'low' ? '↓ 부족' : '적정')
</script>

<style scoped>
.peer-row { display: flex; align-items: center; justify-content: space-between; padding: 10px 12px; border: 1px dashed var(--border); border-radius: 10px; }
.peer-row strong { font-size: 13px; }
.peer-row span { display: flex; gap: 10px; align-items: baseline; } b { font-family: var(--mono); font-size: 11px; font-weight: 400; } .peer { color: var(--ink-3); }
</style>
