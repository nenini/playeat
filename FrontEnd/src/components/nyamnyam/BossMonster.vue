<template>
  <div
    class="boss-monster"
    :class="{ defeated: isDefeated, dragon: currentAssets.type === 'DRAGON' }"
    :style="{ width: `${size}px`, height: `${height}px` }"
  >
    <img v-if="isDefeated" class="boss-clear" :src="currentAssets.clear" :alt="`${bossName} 격파`" draggable="false" />
    <template v-else-if="currentAssets.type === 'DRAGON'">
      <img class="boss-wing" :src="currentAssets.wing" alt="" draggable="false" />
      <img class="boss-body" :src="currentAssets.alive" :alt="bossName" draggable="false" />
    </template>
    <img v-else class="boss-single" :src="currentAssets.alive" :alt="bossName" draggable="false" />
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { bossAssetsFor } from '../../utils/bossAssets'

const props = withDefaults(defineProps<{ size?: number; hp?: number; bossType?: string | null; bossName?: string; cleared?: boolean }>(), {
  size: 240,
  hp: 60,
  bossType: 'DRAGON',
  bossName: '보스',
  cleared: false
});
const height = computed(() => Math.round(props.size * (428 / 540)));
const currentAssets = computed(() => bossAssetsFor(props.bossType, props.bossType, props.bossName))
const isDefeated = computed(() => props.cleared || props.hp === 0);
</script>

<script lang="ts">
export default { name: "BossMonster" };
</script>

<style scoped>
.boss-monster {
  position: relative;
  z-index: 2;
  isolation: isolate;
}
.boss-wing {
  position: absolute;
  z-index: 1;
  left: 56%;
  top: 25%;
  width: 49%;
  height: 40%;
  transform-origin: 9.5% 67.5%;
  animation: bossWingFlap 1100ms cubic-bezier(0.45, 0.05, 0.35, 1) infinite;
  object-fit: contain;
  user-select: none;
}
.boss-body {
  position: absolute;
  z-index: 2;
  left: 0;
  top: 0;
  width: 98%;
  height: 111%;
  object-fit: contain;
  user-select: none;
}
.boss-single {
  position: absolute;
  z-index: 2;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  user-select: none;
}
.boss-clear {
  position: absolute;
  z-index: 2;
  left: 0;
  top: 25%;
  width: 95%;
  height: 95%;
  object-fit: contain;
  user-select: none;
}
.boss-monster:not(.dragon) .boss-clear {
  top: 4%;
  width: 100%;
  height: 100%;
}
.defeated {
  transform: none;
  filter: none;
}
@keyframes bossWingFlap {
  0% {
    transform: rotate(-20deg);
  }
  42% {
    transform: rotate(6deg);
  }
  58% {
    transform: rotate(2deg);
  }
  100% {
    transform: rotate(-20deg);
  }
}
</style>
