<template>
  <svg viewBox="0 0 120 120" :width="size" :height="size" class="nyam-svg" :class="`appearance-${appearanceType}`">
    <g :class="{ 'nyam-hop': stage === 'chick' }">
      <g v-if="stage === 'egg'">
        <path d="M60 14 C 36 14, 22 56, 30 82 C 38 106, 82 106, 90 82 C 98 56, 84 14, 60 14 Z" fill="var(--yolk)" stroke="var(--ink)" stroke-width="2.4" />
        <path d="M30 60 L 38 56 L 46 62 L 54 56 L 62 62 L 70 56 L 80 62 L 88 58" fill="none" stroke="var(--ink)" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
        <Face :cx="60" :cy="76" :mood="mood" :es="10" :er="2.6" :my="92" />
      </g>
      <g v-else-if="stage === 'adult'">
        <path d="M52 14 q 4 -8 8 0 q 4 -8 8 0" fill="var(--yolk-deep)" stroke="var(--ink)" stroke-width="1.6" />
        <path d="M60 22 C 22 22, 16 76, 30 100 C 42 116, 78 116, 90 100 C 104 76, 98 22, 60 22 Z" fill="var(--yolk)" stroke="var(--ink)" stroke-width="2.4" />
        <path d="M22 70 q -4 6 2 12" fill="none" stroke="var(--ink)" stroke-width="2" stroke-linecap="round" />
        <path d="M98 70 q 4 6 -2 12" fill="none" stroke="var(--ink)" stroke-width="2" stroke-linecap="round" />
        <ellipse cx="60" cy="86" rx="18" ry="14" fill="var(--surface-alt)" stroke="var(--ink)" stroke-width="1.4" />
        <path d="M53 62 L 67 62 L 60 70 Z" fill="var(--accent)" stroke="var(--ink)" stroke-width="1.6" stroke-linejoin="round" />
        <path d="M48 112 l -3 6 M48 112 l 3 6 M48 112 l 0 7" stroke="var(--accent)" stroke-width="2.2" stroke-linecap="round" fill="none" />
        <path d="M72 112 l -3 6 M72 112 l 3 6 M72 112 l 0 7" stroke="var(--accent)" stroke-width="2.2" stroke-linecap="round" fill="none" />
        <Face :cx="60" :cy="52" :mood="mood" :es="11" :er="3.2" :my="66" />
      </g>
      <g v-else>
        <path d="M30 30 L 38 22 L 46 30 L 54 22 L 62 30 L 70 22 L 80 30" fill="none" stroke="var(--ink)" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
        <ellipse cx="60" cy="68" rx="38" ry="36" fill="rgb(255, 219, 110)" stroke="var(--ink)" stroke-width="2.4" />
        <path class="nyam-chick-wl" d="M28 70 q 6 8 0 16" fill="var(--yolk-deep)" stroke="var(--ink)" stroke-width="1.8" stroke-linejoin="round" />
        <path class="nyam-chick-wr" d="M92 70 q -6 8 0 16" fill="var(--yolk-deep)" stroke="var(--ink)" stroke-width="1.8" stroke-linejoin="round" />
        <path d="M55 70 L 65 70 L 60 76 Z" fill="var(--accent)" stroke="var(--ink)" stroke-width="1.6" stroke-linejoin="round" />
        <path d="M50 102 l -2 6 M50 102 l 2 6 M50 102 l 0 7" stroke="var(--accent)" stroke-width="2" stroke-linecap="round" fill="none" />
        <path d="M70 102 l -2 6 M70 102 l 2 6 M70 102 l 0 7" stroke="var(--accent)" stroke-width="2" stroke-linecap="round" fill="none" />
        <Face :cx="60" :cy="62" :mood="mood" :es="9" :er="3" :my="82" />
      </g>
      <image v-if="hatImageUrl" :href="hatImageUrl" x="30" y="0" width="60" height="38" preserveAspectRatio="xMidYMid meet" class="hat-image" />
      <WeaponIcon v-else-if="hatId === 'crown'" id="crown" class="hat" />
    </g>
  </svg>
</template>

<script setup lang="ts">
import Face from './NyamnyamFace.vue'
import WeaponIcon from './WeaponIcon.vue'
import type { Stage } from '../../services/mock/nyamnyamMock'

withDefaults(defineProps<{ stage?: Stage, size?: number, mood?: 'happy' | 'hungry' | 'sad', appearanceType?: string, hatId?: string | null, hatImageUrl?: string | null }>(), {
  stage: 'chick',
  size: 120,
  mood: 'happy',
  appearanceType: 'DEFAULT',
  hatId: null,
  hatImageUrl: null
})
</script>

<script lang="ts">
export default { name: 'NyamnyamCharacter' }
</script>

<style scoped>
.nyam-svg { overflow: visible; }
@keyframes nyamHop { 0%,86%,100%{transform:translateY(0)} 90%{transform:translateY(-12px)} 93%{transform:translateY(0)} 95%{transform:translateY(-5px)} 97%{transform:translateY(0)} }
@keyframes nyamFlutterL { 0%,82%,96%,100%{transform:rotate(0)} 84%{transform:rotate(-32deg)} 86%{transform:rotate(8deg)} 88%{transform:rotate(-28deg)} 90%{transform:rotate(8deg)} 92%{transform:rotate(-22deg)} 94%{transform:rotate(0)} }
@keyframes nyamFlutterR { 0%,82%,96%,100%{transform:rotate(0)} 84%{transform:rotate(32deg)} 86%{transform:rotate(-8deg)} 88%{transform:rotate(28deg)} 90%{transform:rotate(-8deg)} 92%{transform:rotate(22deg)} 94%{transform:rotate(0)} }
.nyam-hop { transform-box: fill-box; transform-origin: 50% 100%; animation: nyamHop 5.4s ease-in-out infinite; }
.nyam-chick-wl { transform-box: fill-box; transform-origin: 100% 0%; animation: nyamFlutterL 5.4s ease-in-out infinite; }
.nyam-chick-wr { transform-box: fill-box; transform-origin: 0% 0%; animation: nyamFlutterR 5.4s ease-in-out infinite; }
.hat { transform: translate(28px, -6px) scale(1.05); }
.hat-image { pointer-events: none; }
</style>
