<template>
  <span class="icon" v-html="svg" />
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{ name: string, size?: number, color?: string }>(), {
  size: 16,
  color: 'currentColor'
})

const stroke = computed(() => `stroke="${props.color}" stroke-width="1.8" fill="none" stroke-linecap="round" stroke-linejoin="round"`)
const svg = computed(() => {
  const s = props.size
  const c = props.color
  const wrap = (body: string) => `<svg width="${s}" height="${s}" viewBox="0 0 20 20">${body}</svg>`
  switch (props.name) {
    case 'search': return wrap(`<circle cx="9" cy="9" r="6" ${stroke.value}/><line x1="13.5" y1="13.5" x2="17" y2="17" ${stroke.value}/>`)
    case 'plus': return wrap(`<line x1="10" y1="4" x2="10" y2="16" ${stroke.value}/><line x1="4" y1="10" x2="16" y2="10" ${stroke.value}/>`)
    case 'close': return wrap(`<line x1="5" y1="5" x2="15" y2="15" ${stroke.value}/><line x1="15" y1="5" x2="5" y2="15" ${stroke.value}/>`)
    case 'chev-l': return wrap(`<polyline points="12,4 6,10 12,16" ${stroke.value}/>`)
    case 'chev-r': return wrap(`<polyline points="8,4 14,10 8,16" ${stroke.value}/>`)
    case 'check': return wrap(`<polyline points="4,11 8,15 16,5" ${stroke.value}/>`)
    case 'edit': return wrap(`<path d="M14 3 L 17 6 L 7 16 L 3 17 L 4 13 Z" ${stroke.value}/>`)
    case 'fire': return wrap(`<path d="M10 18 C 4 16, 4 10, 8 8 C 8 6, 9 4, 11 3 C 10 6, 14 7, 14 12 C 14 16, 12 18, 10 18 Z" fill="${c}"/>`)
    case 'home': return wrap(`<polygon points="10,3 17,9 17,17 13,17 13,12 7,12 7,17 3,17 3,9" ${stroke.value} fill="${c}" fill-opacity=".12"/>`)
    case 'meal': return wrap(`<circle cx="10" cy="10" r="6.5" ${stroke.value}/><circle cx="10" cy="10" r="2.5" fill="${c}"/>`)
    case 'analyze': return wrap(`<polyline points="3,15 7,10 11,12 17,5" ${stroke.value}/>`)
    case 'boss': return wrap(`<path d="M3 17 L 5 8 L 10 4 L 15 8 L 17 17 Z" ${stroke.value} fill="${c}" fill-opacity=".12"/><circle cx="8" cy="11" r="1" fill="${c}"/><circle cx="12" cy="11" r="1" fill="${c}"/>`)
    case 'guild': return wrap(`<circle cx="7" cy="8" r="3" ${stroke.value}/><circle cx="14" cy="8" r="3" ${stroke.value}/><path d="M2 17 q5 -6 9 0" ${stroke.value}/><path d="M9 17 q5 -6 9 0" ${stroke.value}/>`)
    case 'bag': return wrap(`<path d="M6 8 V6 a4 4 0 0 1 8 0 v2" ${stroke.value}/><rect x="3" y="8" width="14" height="10" rx="2.5" ${stroke.value} fill="${c}" fill-opacity=".1"/><circle cx="10" cy="13" r="1.2" fill="${c}"/>`)
    case 'send': return wrap(`<polygon points="3,3 17,10 3,17 6,10" ${stroke.value} fill="${c}" fill-opacity=".15"/>`)
    case 'sparkle': return wrap(`<path d="M10 3 v4 M10 13 v4 M3 10 h4 M13 10 h4" ${stroke.value}/>`)
    default: return ''
  }
})
</script>

<script lang="ts">
export default { name: 'AppIcon' }
</script>

<style scoped>
.icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 0;
}
</style>
