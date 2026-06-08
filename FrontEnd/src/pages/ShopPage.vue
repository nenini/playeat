<template>
  <section class="shop-layout">
    <aside class="shop-left">
      <AppCard :padding="14" class="coin-card"><div class="coin-row"><span>냠냠코인</span><strong>🪙 {{ coins.toLocaleString() }}</strong></div></AppCard>
      <AppCard :padding="32" class="preview">
        <div class="mono-label">{{ hovered && owned.includes(hovered.id) ? `미리보기 · ${hovered.name}` : `장착 중 · ${equipLabel}` }}</div>
        <div class="preview-circle"><NyamnyamCharacter :stage="stage" :size="185" :hat-id="displayHeadId" /><div v-if="displayHandId" class="hand"><WeaponIcon :id="displayHandId" /></div></div>
        <p v-if="equippedItem"><strong>{{ equippedItem.name }}</strong> · <span>{{ equippedItem.desc }}</span></p>
        <p v-else class="empty">장착된 아이템이 없어요</p>
      </AppCard>
    </aside>
    <main class="shop-main">
      <div><div class="shop-title">냠냠 아이템 상점</div><p>냠냠코인으로 무기·머리 장식을 구매하고 냠냠이에게 입혀보세요</p></div>
      <div class="item-list">
        <div v-for="item in shopItems" :key="item.id" class="item-row" :class="{ equipped: isEquipped(item), hover: hovered?.id === item.id }" @mouseenter="hovered = item" @mouseleave="hovered = null" @click="owned.includes(item.id) && $emit('equip', item)">
          <div class="item-preview" :class="{ head: item.slot === 'head' }"><WeaponIcon :id="item.id" /></div>
          <div class="grow"><div class="item-head"><strong>{{ item.name }}</strong><AppPill size="sm">{{ item.slot === 'head' ? '머리' : '손' }}</AppPill></div><p>{{ item.desc }}</p><AppPill v-if="item.price === 0" size="sm">무료 기본 아이템</AppPill><b v-else>🪙 {{ item.price.toLocaleString() }} 코인</b></div>
          <div class="actions">
            <template v-if="isEquipped(item)"><AppPill tone="accent" size="sm">장착 중 ✓</AppPill><AppButton variant="secondary" @click.stop="$emit('unequip', item.slot === 'head' ? 'head' : 'hand')">해제</AppButton></template>
            <AppButton v-else-if="owned.includes(item.id)" variant="secondary" @click.stop="$emit('equip', item)">장착하기</AppButton>
            <AppButton v-else :disabled="coins < item.price" @click.stop="buy(item)">{{ coins >= item.price ? '🪙 구매하기' : '코인 부족' }}</AppButton>
          </div>
        </div>
      </div>
    </main>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppPill from '../components/common/AppPill.vue'
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
import WeaponIcon from '../components/nyamnyam/WeaponIcon.vue'
import { shopItems, type Stage } from '../services/mock/nyamnyamMock'

const props = defineProps<{ stage: Stage, equippedWeapon: string, equippedHat: string | null }>()
defineEmits<{ equip: [item: { id: string, slot: string }], unequip: [slot: 'head' | 'hand'] }>()
const coins = ref(1600)
const owned = ref<string[]>(shopItems.filter((item) => item.ownedDefault).map((item) => item.id))
const hovered = ref<typeof shopItems[number] | null>(null)
const displayHandId = computed(() => hovered.value && owned.value.includes(hovered.value.id) && hovered.value.slot !== 'head' ? hovered.value.id : props.equippedWeapon)
const displayHeadId = computed(() => hovered.value && owned.value.includes(hovered.value.id) && hovered.value.slot === 'head' ? hovered.value.id : props.equippedHat)
const equippedItem = computed(() => shopItems.find((item) => item.id === props.equippedWeapon))
const equipLabel = computed(() => [shopItems.find((item) => item.id === props.equippedWeapon)?.name, shopItems.find((item) => item.id === props.equippedHat)?.name].filter(Boolean).join(' + ') || '장착 안 함')
function isEquipped(item: { id: string, slot: string }) { return item.slot === 'head' ? props.equippedHat === item.id : props.equippedWeapon === item.id }
function buy(item: typeof shopItems[number]) { if (owned.value.includes(item.id) || coins.value < item.price) return; coins.value -= item.price; owned.value.push(item.id) }
</script>

<style scoped>
.shop-layout { display: grid; grid-template-columns: 360px 1fr; gap: 24px; align-items: start; }
.shop-left, .shop-main { display: flex; flex-direction: column; gap: 12px; } .shop-main { gap: 16px; }
.coin-card { background: linear-gradient(135deg,#fff8dc 0%,#ffe57a 100%); border-color: var(--yolk-deep); } .coin-row { display: flex; justify-content: space-between; align-items: center; } .coin-row span { font-family: var(--mono); font-size: 11px; color: #7a5c00; letter-spacing: 1px; } .coin-row strong { font-family: var(--mono); font-size: 22px; color: #6b4c00; }
.preview { background: linear-gradient(180deg,#fffaf0 0%,#fff 100%); text-align: center; } .preview-circle { position: relative; width: 210px; height: 210px; border-radius: 50%; background: radial-gradient(circle at 50% 40%,#fff5e0 0%,#fbe5d3 100%); border: 2px solid var(--border); display: flex; align-items: center; justify-content: center; margin: 20px auto 0; box-shadow: inset 0 -8px 20px rgba(232,138,77,.08); }
.hand { position: absolute; right: -28px; top: 28%; z-index: 2; } .hand :deep(svg) { width: 52px; height: 175px; } .preview p { margin: 16px 0 0; font-size: 12px; color: var(--ink-2); line-height: 1.5; } .preview .empty { color: var(--ink-3); }
.shop-title { font-size: 22px; font-weight: 900; letter-spacing: -0.4px; margin-bottom: 4px; } .shop-main > div > p { margin: 0; color: var(--ink-2); font-size: 13px; }
.item-list { display: flex; flex-direction: column; gap: 12px; }
.item-row { display: flex; align-items: center; gap: 20px; padding: 18px 22px; border-radius: 16px; border: 1.5px solid var(--border); background: var(--surface); box-shadow: var(--shadow); cursor: default; transition: all .15s; } .item-row.hover { background: var(--surface-alt); border-color: var(--border-strong); box-shadow: var(--shadow-lg); transform: translateY(-2px); } .item-row.equipped { background: var(--accent-soft); border-color: var(--accent); }
.item-preview { width: 52px; height: 100px; flex: 0 0 52px; display: flex; align-items: center; justify-content: center; background: var(--surface-alt); border-radius: 12px; border: 1px solid var(--border); overflow: hidden; } .item-preview :deep(svg) { width: 38px; height: 90px; } .item-preview.head { height: 60px; } .item-preview.head :deep(svg) { width: 46px; height: 30px; }
.grow { flex: 1; } .item-head { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; } .item-head strong { font-size: 18px; } .item-row p { margin: 0 0 8px; font-size: 12px; color: var(--ink-2); } .item-row b { font-family: var(--mono); font-size: 13px; color: #7a5c00; }
.actions { display: flex; align-items: center; gap: 8px; flex: 0 0 auto; }
</style>
