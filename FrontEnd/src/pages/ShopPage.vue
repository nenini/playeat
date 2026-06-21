<template>
  <section v-if="isLoading" class="loading-state">
    <AppCard>상점 정보를 불러오는 중...</AppCard>
  </section>

  <section v-else class="shop-page">
    <div v-if="errorMessage" class="api-message error">{{ errorMessage }}</div>
    <div v-if="successMessage" class="api-message success">{{ successMessage }}</div>

    <div class="shop-layout">
      <aside class="shop-left">
        <AppCard :padding="14" class="coin-card">
          <div class="coin-row"><span>냠냠코인</span><strong>🪙 {{ coins.toLocaleString() }}</strong></div>
        </AppCard>
        <AppCard :padding="32" class="preview">
          <div class="mono-label">{{ hovered && isOwned(hovered) ? `미리보기 · ${hovered.name}` : `장착 중 · ${equipLabel}` }}</div>
          <div class="preview-circle">
            <NyamnyamCharacter :stage="stage" :size="185" :hat-id="displayHeadIcon" />
            <div v-if="displayHandIcon" class="hand"><WeaponIcon :id="displayHandIcon" /></div>
          </div>
          <p v-if="equippedItems.length"><strong>{{ equipLabel }}</strong></p>
          <p v-else class="empty">장착된 아이템이 없어요</p>
        </AppCard>
      </aside>

      <main class="shop-main">
        <div><div class="shop-title">냠냠 아이템 상점</div><p>냠냠코인으로 무기·머리 장식을 구매하고 냠냠이에게 입혀보세요</p></div>
        <div v-if="shopItems.length === 0" class="empty-state">판매 중인 아이템이 없습니다.</div>
        <div v-else class="item-list">
          <div
            v-for="item in shopItems"
            :key="item.itemId"
            class="item-row"
            :class="{ equipped: isEquipped(item), hover: hovered?.itemId === item.itemId }"
            @mouseenter="hovered = item"
            @mouseleave="hovered = null"
          >
            <div class="item-preview" :class="{ head: item.slotType === 'HEAD' }">
              <WeaponIcon v-if="itemIconId(item)" :id="itemIconId(item) || undefined" />
              <img v-else-if="item.imageUrl" :src="item.imageUrl" :alt="item.name">
              <span v-else class="no-image">이미지 없음</span>
            </div>
            <div class="grow">
              <div class="item-head"><strong>{{ item.name }}</strong><AppPill size="sm">{{ itemTypeLabel(item.itemType) }}</AppPill><AppPill size="sm">{{ slotLabel(item.slotType) }}</AppPill></div>
              <p>{{ item.description || '아이템 설명이 없습니다.' }}</p>
              <AppPill v-if="item.defaultItem" size="sm">무료 기본 아이템</AppPill>
              <b v-else>🪙 {{ item.price.toLocaleString() }} 코인</b>
            </div>
            <div class="actions">
              <template v-if="isEquipped(item)">
                <AppPill tone="accent" size="sm">장착 중 ✓</AppPill>
                <AppButton variant="secondary" :disabled="isPending" @click="unequip(item.slotType)">{{ pendingActionId === `unequip-${item.slotType}` ? '해제 중...' : '해제' }}</AppButton>
              </template>
              <AppButton v-else-if="isOwned(item)" variant="secondary" :disabled="isPending" @click="equip(item)">{{ pendingActionId === `equip-${item.itemId}` ? '장착 중...' : '장착하기' }}</AppButton>
              <AppButton v-else :disabled="isPending || !item.purchasable" @click="purchase(item)">{{ purchaseButtonLabel(item) }}</AppButton>
            </div>
          </div>
        </div>
      </main>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppPill from '../components/common/AppPill.vue'
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
import WeaponIcon from '../components/nyamnyam/WeaponIcon.vue'
import { ApiError } from '../services/api/client'
import { characterEquipmentApi } from '../services/api/characterEquipmentApi'
import { coinApi } from '../services/api/coinApi'
import { itemApi } from '../services/api/itemApi'
import { shopApi } from '../services/api/shopApi'
import type { CharacterEquipment } from '../types/characterEquipment'
import type { UserItem } from '../types/item'
import type { ShopItem } from '../types/shop'

type Stage = 'egg' | 'chick' | 'adult'

defineProps<{ stage: Stage; equippedWeapon?: string; equippedHat?: string | null }>()

const coins = ref(0)
const shopItems = ref<ShopItem[]>([])
const myItems = ref<UserItem[]>([])
const equipments = ref<CharacterEquipment[]>([])
const hovered = ref<ShopItem | null>(null)
const isLoading = ref(true)
const errorMessage = ref('')
const successMessage = ref('')
const pendingActionId = ref<string | null>(null)

const equippedItems = computed(() => equipments.value.filter((equipment) => equipment.equipped))
const equipLabel = computed(() => equippedItems.value.map((equipment) => equipment.name).filter(Boolean).join(' + ') || '장착 안 함')
const previewItem = computed(() => hovered.value && isOwned(hovered.value) ? hovered.value : null)
const displayHandIcon = computed(() => {
  if (previewItem.value?.slotType === 'HAND') return itemIconId(previewItem.value)
  const equipment = equippedItems.value.find((item) => item.slotType === 'HAND')
  return equipment ? equipmentIconId(equipment) : null
})
const displayHeadIcon = computed(() => {
  if (previewItem.value?.slotType === 'HEAD') return itemIconId(previewItem.value)
  const equipment = equippedItems.value.find((item) => item.slotType === 'HEAD')
  return equipment ? equipmentIconId(equipment) : null
})
const isPending = computed(() => pendingActionId.value !== null)

function setError(error: unknown) {
  errorMessage.value = error instanceof ApiError ? error.message : '상점 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.'
}

function clearFeedback() {
  errorMessage.value = ''
  successMessage.value = ''
}

async function loadShop() {
  isLoading.value = true
  clearFeedback()
  try {
    const [shop, itemList, coin, ownedItems, equipmentList] = await Promise.all([
      shopApi.getShop(),
      shopApi.getShopItems(),
      coinApi.getMyCoin(),
      itemApi.getMyItems(),
      characterEquipmentApi.getMyEquipments()
    ])
    coins.value = coin.balance
    shopItems.value = itemList.items ?? shop.items ?? []
    myItems.value = ownedItems ?? []
    equipments.value = equipmentList.equipments ?? shop.equippedItems ?? []
  } catch (error) {
    setError(error)
  } finally {
    isLoading.value = false
  }
}

async function refreshAfterPurchase() {
  const [coin, itemList, ownedItems, equipmentList] = await Promise.all([
    coinApi.getMyCoin(),
    shopApi.getShopItems(),
    itemApi.getMyItems(),
    characterEquipmentApi.getMyEquipments()
  ])
  coins.value = coin.balance
  shopItems.value = itemList.items ?? []
  myItems.value = ownedItems ?? []
  equipments.value = equipmentList.equipments ?? []
}

async function refreshEquipment() {
  const [ownedItems, equipmentList, itemList] = await Promise.all([
    itemApi.getMyItems(),
    characterEquipmentApi.getMyEquipments(),
    shopApi.getShopItems()
  ])
  myItems.value = ownedItems ?? []
  equipments.value = equipmentList.equipments ?? []
  shopItems.value = itemList.items ?? []
}

async function purchase(item: ShopItem) {
  if (pendingActionId.value || item.owned || !item.purchasable) return
  clearFeedback()
  pendingActionId.value = `purchase-${item.itemId}`
  try {
    await shopApi.purchaseItem(item.itemId)
    await refreshAfterPurchase()
    successMessage.value = `${item.name} 구매를 완료했습니다.`
  } catch (error) {
    setError(error)
  } finally {
    pendingActionId.value = null
  }
}

async function equip(item: ShopItem) {
  if (pendingActionId.value || !item.owned) return
  const userItemId = item.userItemId ?? myItems.value.find((ownedItem) => ownedItem.itemId === item.itemId)?.userItemId
  if (!userItemId) {
    errorMessage.value = '장착할 보유 아이템 정보를 확인할 수 없습니다.'
    return
  }

  clearFeedback()
  pendingActionId.value = `equip-${item.itemId}`
  try {
    await characterEquipmentApi.equipItem({ userItemId })
    await refreshEquipment()
    successMessage.value = `${item.name}을(를) 장착했습니다.`
  } catch (error) {
    setError(error)
  } finally {
    pendingActionId.value = null
  }
}

async function unequip(slotType: string | null) {
  if (pendingActionId.value || !slotType) return
  clearFeedback()
  pendingActionId.value = `unequip-${slotType}`
  try {
    await characterEquipmentApi.unequipItem(slotType)
    await refreshEquipment()
    successMessage.value = '아이템 장착을 해제했습니다.'
  } catch (error) {
    setError(error)
  } finally {
    pendingActionId.value = null
  }
}

function isOwned(item: ShopItem) {
  return item.owned === true
}

function isEquipped(item: ShopItem) {
  return item.equipped === true || equipments.value.some((equipment) => equipment.equipped && equipment.itemId === item.itemId)
}

function purchaseButtonLabel(item: ShopItem) {
  if (pendingActionId.value === `purchase-${item.itemId}`) return '구매 중...'
  return item.purchasable ? '🪙 구매하기' : '구매 불가'
}

function slotLabel(slotType: string | null) {
  if (slotType === 'HEAD') return '머리'
  if (slotType === 'HAND') return '손'
  return slotType || '장착 슬롯 없음'
}

function itemTypeLabel(itemType: string) {
  if (itemType === 'EQUIPMENT') return '장비'
  if (itemType === 'CONSUMABLE') return '소모품'
  return itemType || '유형 없음'
}

function itemIconId(item: Pick<ShopItem, 'name' | 'imageUrl'>) {
  return iconId(item.name, item.imageUrl)
}

function equipmentIconId(item: Pick<CharacterEquipment, 'name' | 'imageUrl'>) {
  return iconId(item.name, item.imageUrl)
}

function iconId(name: string | null, imageUrl: string | null) {
  const source = `${name ?? ''} ${imageUrl ?? ''}`.toLowerCase()
  if (source.includes('crown') || source.includes('왕관')) return 'crown'
  if (source.includes('sword') || source.includes('칼')) return 'sword'
  if (source.includes('staff') || source.includes('지팡이')) return 'staff'
  if (source.includes('wood-stick') || source.includes('나무막대기')) return 'stick'
  return null
}

onMounted(() => { void loadShop() })
</script>

<style scoped>
.loading-state { max-width: 560px; margin: 80px auto; text-align: center; }
.api-message { margin-bottom: 14px; padding: 11px 14px; border-radius: 10px; border: 1px solid var(--border); font-size: 13px; }
.api-message.error { color: var(--bad); border-color: var(--bad); background: var(--surface); }
.api-message.success { color: var(--ok); background: var(--surface); }
.empty-state { padding: 28px; border: 1.5px dashed var(--border); border-radius: 14px; background: var(--surface); text-align: center; color: var(--ink-3); font-size: 13px; }
.shop-layout { display: grid; grid-template-columns: 360px 1fr; gap: 24px; align-items: start; }
.shop-left, .shop-main { display: flex; flex-direction: column; gap: 12px; } .shop-main { gap: 16px; }
.coin-card { background: linear-gradient(135deg,#fff8dc 0%,#ffe57a 100%); border-color: var(--yolk-deep); } .coin-row { display: flex; justify-content: space-between; align-items: center; } .coin-row span { font-family: var(--mono); font-size: 11px; color: #7a5c00; letter-spacing: 1px; } .coin-row strong { font-family: var(--mono); font-size: 22px; color: #6b4c00; }
.preview { background: linear-gradient(180deg,#fffaf0 0%,#fff 100%); text-align: center; } .preview-circle { position: relative; width: 210px; height: 210px; border-radius: 50%; background: radial-gradient(circle at 50% 40%,#fff5e0 0%,#fbe5d3 100%); border: 2px solid var(--border); display: flex; align-items: center; justify-content: center; margin: 20px auto 0; box-shadow: inset 0 -8px 20px rgba(232,138,77,.08); }
.hand { position: absolute; right: -28px; top: 28%; z-index: 2; } .hand :deep(svg) { width: 52px; height: 175px; } .preview p { margin: 16px 0 0; font-size: 12px; color: var(--ink-2); line-height: 1.5; } .preview .empty { color: var(--ink-3); }
.shop-title { font-size: 22px; font-weight: 900; letter-spacing: -0.4px; margin-bottom: 4px; } .shop-main > div > p { margin: 0; color: var(--ink-2); font-size: 13px; }
.item-list { display: flex; flex-direction: column; gap: 12px; }
.item-row { display: flex; align-items: center; gap: 20px; padding: 18px 22px; border-radius: 16px; border: 1.5px solid var(--border); background: var(--surface); box-shadow: var(--shadow); cursor: default; transition: all .15s; } .item-row.hover { background: var(--surface-alt); border-color: var(--border-strong); box-shadow: var(--shadow-lg); transform: translateY(-2px); } .item-row.equipped { background: var(--accent-soft); border-color: var(--accent); }
.item-preview { width: 52px; height: 100px; flex: 0 0 52px; display: flex; align-items: center; justify-content: center; background: var(--surface-alt); border-radius: 12px; border: 1px solid var(--border); overflow: hidden; } .item-preview img { width: 100%; height: 100%; object-fit: contain; } .item-preview :deep(svg) { width: 38px; height: 90px; } .item-preview.head { height: 60px; } .item-preview.head :deep(svg) { width: 46px; height: 30px; } .no-image { padding: 4px; font-size: 9px; line-height: 1.3; color: var(--ink-3); text-align: center; }
.grow { flex: 1; } .item-head { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; } .item-head strong { font-size: 18px; } .item-row p { margin: 0 0 8px; font-size: 12px; color: var(--ink-2); } .item-row b { font-family: var(--mono); font-size: 13px; color: #7a5c00; }
.actions { display: flex; align-items: center; gap: 8px; flex: 0 0 auto; }
@media (max-width: 860px) { .shop-layout { grid-template-columns: 1fr; } .shop-left { display: grid; grid-template-columns: 1fr 1fr; } }
@media (max-width: 620px) { .shop-left { display: flex; } .item-row { align-items: flex-start; flex-wrap: wrap; } .actions { width: 100%; justify-content: flex-end; } }
</style>
