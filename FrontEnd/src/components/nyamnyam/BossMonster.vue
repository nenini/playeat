<template>
  <div
    class="boss-monster"
    :class="{ defeated: hp === 0 }"
    :style="{ width: `${size}px`, height: `${height}px` }"
  >
    <img class="boss-wing" :src="nwing" alt="" draggable="false" />
    <img class="boss-body" :src="nbody" alt="당분 드래곤" draggable="false" />
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import nbody from "../../assets/nbody.png";
import nwing from "../../assets/nwing.png";

const props = withDefaults(defineProps<{ size?: number; hp?: number }>(), {
  size: 240,
  hp: 60,
});
const height = computed(() => Math.round(props.size * (428 / 540)));
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
.defeated {
  transform: rotate(6deg);
  filter: grayscale(0.18);
}
.defeated .boss-wing {
  animation-duration: 1500ms;
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
