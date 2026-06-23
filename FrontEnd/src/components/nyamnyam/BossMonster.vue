<template>
  <div
    class="boss-monster"
    :class="{ defeated: hp === 0 }"
    :style="{ width: `${size}px`, height: `${height}px` }"
  >
    <img v-if="isDefeated" class="boss-clear" :src="dragonClear" alt="격파된 당분 드래곤" draggable="false" />
    <template v-else>
      <img class="boss-wing" :src="nwing" alt="" draggable="false" />
      <img class="boss-body" :src="nbody" alt="당분 드래곤" draggable="false" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import nbody from "../../assets/nbody.png";
import nwing from "../../assets/nwing.png";
import dragonClear from "../../assets/dragon_clear.png";

const props = withDefaults(defineProps<{ size?: number; hp?: number }>(), {
  size: 240,
  hp: 60,
});
const height = computed(() => Math.round(props.size * (428 / 540)));
const isDefeated = computed(() => props.hp === 0);
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
.boss-clear {
  position: absolute;
  z-index: 2;
  left: 0;
  top: 0;
  width: 95%;
  height: 95%;
  object-fit: contain;
  user-select: none;
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
