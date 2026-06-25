<template>
  <div
    :key="playKey"
    class="boss-attack-effect"
    :class="effectClass"
    aria-hidden="true"
  >
    <div class="hit-flash" />
    <div class="attack-caption">
      <strong>-{{ formattedDamage }} HP</strong>
    </div>

    <template v-if="normalizedEffect === 'SWORD'">
      <i class="slash slash-a" />
      <i class="slash slash-b" />
      <i v-for="index in 5" :key="`s-${index}`" class="spark" :class="`spark-${index}`" />
    </template>

    <template v-else-if="normalizedEffect === 'STAFF'">
      <i class="magic-ring ring-a" />
      <i class="magic-ring ring-b" />
      <i v-for="index in 7" :key="`m-${index}`" class="magic-dot" :class="`magic-${index}`" />
    </template>

    <template v-else-if="normalizedEffect === 'STICK'">
      <i class="stick-line line-a" />
      <i class="stick-line line-b" />
      <i v-for="index in 6" :key="`d-${index}`" class="dust" :class="`dust-${index}`" />
      <i v-for="index in 4" :key="`w-${index}`" class="wood-chip" :class="`chip-${index}`" />
    </template>

    <template v-else>
      <i class="shockwave wave-a" />
      <i class="shockwave wave-b" />
      <i v-for="index in 5" :key="`p-${index}`" class="puff" :class="`puff-${index}`" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { AttackEffectType } from '../../utils/attackEffect'

const props = withDefaults(defineProps<{
  effectType?: AttackEffectType | string
  damage?: number
  playKey: string | number
}>(), {
  effectType: 'DEFAULT',
  damage: 0
})

const normalizedEffect = computed<AttackEffectType>(() => {
  const value = String(props.effectType || 'DEFAULT').toUpperCase()
  return value === 'STICK' || value === 'SWORD' || value === 'STAFF' ? value : 'DEFAULT'
})
const effectClass = computed(() => `effect-${normalizedEffect.value.toLowerCase()}`)
const formattedDamage = computed(() => Math.max(0, Math.round(Number(props.damage) || 0)).toLocaleString())
</script>

<script lang="ts">
export default { name: 'BossAttackEffect' }
</script>

<style scoped>
.boss-attack-effect {
  position: absolute;
  left: 50%;
  top: 45%;
  z-index: 3;
  width: clamp(160px, 32vw, 360px);
  height: clamp(160px, 28vw, 320px);
  transform: translate(-50%, -50%);
  pointer-events: none;
  overflow: visible;
}

.effect-stick {
  left: 52%;
  top: 46%;
}

.effect-sword {
  top: 45%;
}

.effect-staff {
  top: 46%;
}

.hit-flash {
  position: absolute;
  inset: 21% 15%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,.9), rgba(255,202,105,.38) 34%, transparent 68%);
  mix-blend-mode: screen;
  animation: hit-flash 560ms ease-out forwards;
}

.attack-caption {
  position: absolute;
  left: 50%;
  top: 14%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  white-space: nowrap;
  color: #fff2c0;
  text-align: center;
  text-shadow:
    0 2px 0 rgba(87, 25, 11, .82),
    0 5px 12px rgba(0, 0, 0, .58),
    0 0 22px rgba(255,104,54,.9);
  animation: damage-pop 920ms cubic-bezier(.16,.9,.2,1) forwards;
}

.attack-caption strong {
  color: #ff5b3c;
  font-family: var(--mono);
  font-size: clamp(28px, 4.2vw, 48px);
  font-weight: 900;
  letter-spacing: 1px;
  -webkit-text-stroke: 1px rgba(92, 25, 10, .72);
  paint-order: stroke fill;
}

.shockwave {
  position: absolute;
  left: 50%;
  top: 53%;
  width: 74px;
  height: 74px;
  border: 5px solid rgba(255,255,255,.88);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: shockwave 820ms ease-out forwards;
}
.wave-b { animation-delay: 100ms; border-color: rgba(255,214,132,.7); }
.puff {
  position: absolute;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #fff3d7;
  box-shadow: 0 0 14px rgba(255,236,196,.86);
  animation: puff-pop 760ms ease-out forwards;
}
.puff-1 { left: 36%; top: 40%; --x: -48px; --y: -22px; }
.puff-2 { left: 55%; top: 43%; --x: 54px; --y: -30px; }
.puff-3 { left: 44%; top: 60%; --x: -42px; --y: 42px; }
.puff-4 { left: 60%; top: 58%; --x: 46px; --y: 38px; }
.puff-5 { left: 50%; top: 52%; --x: 0; --y: -58px; }

.stick-line {
  position: absolute;
  left: 30%;
  top: 49%;
  width: 56%;
  height: 12px;
  border-radius: 999px;
  background: linear-gradient(90deg, transparent, #7a4a26 14%, #ffd46b 50%, #7a4a26 86%, transparent);
  box-shadow: 0 0 18px rgba(255,190,76,.72);
  transform: rotate(-24deg) scaleX(.18);
  transform-origin: center;
  animation: stick-smash 780ms cubic-bezier(.12,.85,.22,1) forwards;
}
.line-b { top: 56%; transform: rotate(-14deg) scaleX(.18); animation-delay: 70ms; }
.dust,
.wood-chip {
  position: absolute;
  left: 50%;
  top: 54%;
  animation: debris 900ms ease-out forwards;
}
.dust {
  width: 13px;
  height: 13px;
  border-radius: 50%;
  background: #d8b27a;
}
.wood-chip {
  width: 15px;
  height: 7px;
  border-radius: 2px;
  background: #8c5833;
}
.dust-1 { --x: -84px; --y: -36px; }
.dust-2 { --x: -54px; --y: 50px; }
.dust-3 { --x: 72px; --y: -42px; }
.dust-4 { --x: 92px; --y: 30px; }
.dust-5 { --x: -16px; --y: -76px; }
.dust-6 { --x: 22px; --y: 66px; }
.chip-1 { --x: -66px; --y: -10px; --r: 80deg; }
.chip-2 { --x: 76px; --y: -20px; --r: -70deg; }
.chip-3 { --x: -34px; --y: 60px; --r: 140deg; }
.chip-4 { --x: 42px; --y: 58px; --r: -130deg; }

.slash {
  position: absolute;
  left: 10%;
  top: 50%;
  width: 82%;
  height: 10px;
  border-radius: 999px;
  background: linear-gradient(90deg, transparent 0%, rgba(255,255,255,.98) 26%, #aee6ff 52%, rgba(255,255,255,.95) 76%, transparent 100%);
  box-shadow: 0 0 22px rgba(115,205,255,.95), 0 0 38px rgba(255,255,255,.7);
  transform: rotate(-19deg) translateX(-42%) scaleX(.08);
  animation: sword-slash 720ms cubic-bezier(.1,.9,.2,1) forwards;
}
.slash-b {
  top: 58%;
  transform: rotate(13deg) translateX(-42%) scaleX(.08);
  animation-delay: 90ms;
}
.spark {
  position: absolute;
  width: 9px;
  height: 9px;
  background: #fff;
  clip-path: polygon(50% 0, 62% 36%, 100% 50%, 62% 64%, 50% 100%, 38% 64%, 0 50%, 38% 36%);
  box-shadow: 0 0 14px #b8efff;
  animation: spark-fly 880ms ease-out forwards;
}
.spark-1 { left: 28%; top: 45%; --x: -22px; --y: -48px; }
.spark-2 { left: 60%; top: 42%; --x: 48px; --y: -34px; }
.spark-3 { left: 68%; top: 61%; --x: 36px; --y: 42px; }
.spark-4 { left: 40%; top: 66%; --x: -52px; --y: 32px; }
.spark-5 { left: 52%; top: 52%; --x: 8px; --y: -64px; }

.magic-ring {
  position: absolute;
  left: 50%;
  top: 54%;
  width: 78px;
  height: 78px;
  border: 4px solid rgba(167,138,255,.92);
  border-radius: 50%;
  box-shadow: 0 0 22px rgba(135,120,255,.88), inset 0 0 18px rgba(255,180,255,.48);
  transform: translate(-50%, -50%) scale(.18);
  animation: magic-ring 920ms ease-out forwards;
}
.ring-b {
  border-style: dashed;
  border-color: rgba(104,205,255,.84);
  animation-delay: 120ms;
}
.magic-dot {
  position: absolute;
  left: 50%;
  top: 54%;
  width: 11px;
  height: 11px;
  border-radius: 50%;
  background: #d9b7ff;
  box-shadow: 0 0 18px rgba(165,125,255,.92);
  animation: magic-dot 960ms ease-out forwards;
}
.magic-1 { --x: -82px; --y: -34px; }
.magic-2 { --x: -52px; --y: 56px; }
.magic-3 { --x: 0; --y: -86px; }
.magic-4 { --x: 72px; --y: -42px; }
.magic-5 { --x: 86px; --y: 28px; }
.magic-6 { --x: 24px; --y: 74px; }
.magic-7 { --x: -86px; --y: 12px; }

@keyframes hit-flash {
  from { opacity: 0; transform: scale(.5); }
  22% { opacity: 1; }
  to { opacity: 0; transform: scale(1.4); }
}

@keyframes damage-pop {
  from { opacity: 0; transform: translate(-50%, 18px) scale(.72); }
  18% { opacity: 1; transform: translate(-50%, 0) scale(1.1); }
  to { opacity: 0; transform: translate(-50%, -34px) scale(.98); }
}

@keyframes shockwave {
  from { opacity: .95; transform: translate(-50%, -50%) scale(.2); }
  to { opacity: 0; transform: translate(-50%, -50%) scale(2.6); }
}

@keyframes puff-pop {
  from { opacity: 1; transform: translate(-50%, -50%) scale(.5); }
  to { opacity: 0; transform: translate(calc(-50% + var(--x)), calc(-50% + var(--y))) scale(1.16); }
}

@keyframes stick-smash {
  0% { opacity: 0; transform: rotate(-24deg) translateX(-42%) scaleX(.08); }
  18% { opacity: 1; }
  58% { opacity: 1; transform: rotate(-24deg) translateX(0) scaleX(1); }
  100% { opacity: 0; transform: rotate(-24deg) translateX(24%) scaleX(.5); }
}

@keyframes debris {
  from { opacity: 1; transform: translate(-50%, -50%) rotate(0) scale(.6); }
  to { opacity: 0; transform: translate(calc(-50% + var(--x)), calc(-50% + var(--y))) rotate(var(--r, 120deg)) scale(1); }
}

@keyframes sword-slash {
  0% { opacity: 0; transform: rotate(-19deg) translateX(-50%) scaleX(.08); }
  16% { opacity: 1; }
  64% { opacity: 1; transform: rotate(-19deg) translateX(10%) scaleX(1); }
  100% { opacity: 0; transform: rotate(-19deg) translateX(50%) scaleX(.2); }
}

@keyframes spark-fly {
  from { opacity: 1; transform: translate(0, 0) scale(.6) rotate(0); }
  to { opacity: 0; transform: translate(var(--x), var(--y)) scale(1.15) rotate(160deg); }
}

@keyframes magic-ring {
  from { opacity: 0; transform: translate(-50%, -50%) scale(.2) rotate(0); }
  20% { opacity: 1; }
  to { opacity: 0; transform: translate(-50%, -50%) scale(2.4) rotate(130deg); }
}

@keyframes magic-dot {
  from { opacity: 1; transform: translate(-50%, -50%) scale(.5); }
  to { opacity: 0; transform: translate(calc(-50% + var(--x)), calc(-50% + var(--y))) scale(1.25); }
}

@media (prefers-reduced-motion: reduce) {
  .boss-attack-effect *,
  .boss-attack-effect {
    animation-duration: 1ms !important;
  }
}
</style>
