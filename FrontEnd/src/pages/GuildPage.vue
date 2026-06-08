<template>
  <section v-if="!joined">
    <div class="pre-head"><div class="title-lg">길드 참여하기</div><p>마음에 드는 길드에 참여하거나, 코드로 직접 입장하세요</p></div>
    <div class="pre-grid">
      <main class="guild-list"><div class="section-title"><div><div class="section-title-main">길드 목록</div><div class="section-title-sub">총 {{ guildList.length }}개 길드</div></div></div><div v-for="guild in guildList" :key="guild.id" class="guild-row"><div class="guild-logo">{{ guild.emoji }}</div><div class="grow"><h3>{{ guild.name }} <AppPill size="sm">#{{ guild.rank }}위</AppPill></h3><p>👥 {{ guild.members }}/{{ guild.max }}명 · 🏆 {{ guild.score.toLocaleString() }} pt · 🎯 {{ guild.focus }}</p></div><AppButton size="sm" @click="joined = true">참여하기</AppButton></div></main>
      <AppCard class="code-card"><div class="section-title-main">코드로 참여</div><p>길드 코드를 알고 있다면 직접 입력해 바로 참여할 수 있어요.</p><div class="mono-label">길드 코드</div><label class="app-input"><input v-model="code" placeholder="예: NYAM-2840"></label><AppButton full class="join-btn" @click="joined = true">코드로 참여하기</AppButton><small>💡 길드 코드는 길드장에게 받을 수 있어요.<br>영문 + 숫자 4~12자리 형식입니다.</small></AppCard>
    </div>
  </section>
  <section v-else>
    <AppCard :padding="20" class="guild-header">
      <div class="guild-logo big">잘</div><div class="grow"><div class="pill-row"><AppPill tone="accent" size="sm">길드 코드 NYAM-2840</AppPill><AppPill tone="ok" size="sm">전체 1위</AppPill></div><h1>잘먹잘싸</h1><p>6명 · 주간 점수 <strong>2,840 pt</strong> · 이번 주 기록률 92%</p></div><div class="header-actions"><AppButton variant="secondary" size="sm">+ 친구 초대</AppButton><AppButton variant="secondary" size="sm" @click="joined = false">길드 둘러보기</AppButton></div>
    </AppCard>
    <div class="board">
      <AppCard :padding="0" class="chat-card"><div class="chat-head"><span></span><strong>길드 채팅</strong><small>· 6명 활동 중</small></div><div class="feed"><ChatBubble v-for="(msg, i) in allChat" :key="i" :msg="msg" /></div><div class="chat-input"><label><AppIcon name="plus" color="var(--ink-3)" /><input v-model="message" placeholder="메시지 보내기…" @keydown.enter="send"><AppPill size="sm">😀</AppPill><AppPill size="sm">🍱</AppPill><AppButton size="sm" :disabled="!message.trim()" @click="send"><AppIcon name="send" color="#fff" />보내기</AppButton></label></div></AppCard>
      <AppCard class="notice"><div class="section-title-main">📢 공지사항</div><div class="notice-box"><strong>이번 주 보스 격파 가즈아 🐲</strong><small>어제</small><p>이번 주 보스 격파 가즈아 🐲<br>당분 드래곤 D-3 남았습니다. 일요일까지 다 같이 밀어봅시다!</p></div></AppCard>
      <AppCard class="ranking"><div class="section-title"><div><div class="section-title-main">🏆 길드 순위</div><div class="section-title-sub">이번 주 길드 순위</div></div><AppPill tone="ok" size="sm">우리 1위 🏆</AppPill></div><RankRow v-for="rank in rankings" :key="String(rank[0])" :row="rank" /></AppCard>
      <AppCard class="members"><div class="section-title-main">👥 길드원</div><div class="member-grid"><button v-for="member in guildMembers" :key="member.id" :class="{ offline: !member.online }"><span>{{ member.name[0] }}</span><strong>{{ member.name }} <small>LV.{{ member.lv }}</small></strong><em>{{ member.role }}</em></button></div></AppCard>
      <AppCard class="stats"><div class="section-title-main">📊 길드 통계</div><div class="stat-grid"><StatBlock label="기록률" value="92%" /><StatBlock label="보스 데미지" value="−108 HP" /><StatBlock label="주간 점수" value="2,840 pt" accent /><StatBlock label="퀘스트 완료" value="17/24" /></div><div class="guild-chart"><div v-for="(v, i) in [68,72,80,75,88,92,0]" :key="i"><b :style="{ height: v ? `${v}%` : '6px' }" :class="{ zero: v === 0, today: i === 5 }"></b><small>{{ ['월','화','수','목','금','토','일'][i] }}</small></div></div></AppCard>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import ChatBubble from './parts/ChatBubble.vue'
import RankRow from './parts/RankRow.vue'
import StatBlock from './parts/StatBlock.vue'
import { guildList, guildMembers, seedChat } from '../services/mock/nyamnyamMock'

const joined = ref(true)
const code = ref('')
const message = ref('')
const chat = ref<any[]>([])
const allChat = computed(() => [...seedChat, ...chat.value])
const rankings: Array<[string, string, string, number, boolean]> = [['1', '잘먹잘싸', '우리 길드', 2840, true], ['2', '단백질 부대', '', 2710, false], ['3', '아침 챔피언즈', '', 2620, false], ['4', '채소 사랑', '', 2400, false], ['5', '저염 라이프', '', 2300, false]]
function send() { if (!message.value.trim()) return; chat.value.push({ id: 'me', name: '지은', time: '방금', text: message.value, mine: true }); message.value = '' }
</script>

<style scoped>
.pre-head { margin-bottom: 24px; } p { margin: 6px 0 0; font-size: 13px; color: var(--ink-2); line-height: 1.65; }
.pre-grid { display: grid; grid-template-columns: 1fr 340px; gap: 20px; align-items: start; }
.guild-list { display: flex; flex-direction: column; gap: 10px; }
.guild-row { display: flex; align-items: center; gap: 16px; padding: 16px 20px; border-radius: 14px; border: 1.5px solid var(--border); background: var(--surface); box-shadow: var(--shadow); }
.guild-logo { width: 50px; height: 50px; border-radius: 12px; background: var(--yolk); border: 1.5px solid var(--border-strong); display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 900; } .guild-logo.big { width: 64px; height: 64px; font-size: 28px; }
.grow { flex: 1; } h3, h1 { margin: 0; } h3 { font-size: 16px; } h1 { font-size: 24px; font-weight: 900; }
.code-card { position: sticky; top: 88px; } .join-btn { margin-top: 12px; } .code-card small { display: block; margin-top: 16px; padding-top: 14px; border-top: 1px solid var(--border); color: var(--ink-3); font-size: 11px; line-height: 1.7; }
.guild-header { margin-bottom: 18px; background: linear-gradient(135deg,#fffaf0 0%,#fff 100%); display: flex; align-items: center; gap: 18px; } .guild-header strong { color: var(--accent); } .pill-row, .header-actions { display: flex; gap: 8px; }
.board { display: grid; grid-template-columns: repeat(12,1fr); grid-auto-rows: minmax(110px,auto); gap: 14px; }
.chat-card { grid-column: span 7; grid-row: span 6; display: flex; flex-direction: column; min-height: 620px; } .notice,.ranking,.members { grid-column: span 5; grid-row: span 3; } .stats { grid-column: span 7; grid-row: span 3; }
.chat-head { padding: 14px 18px; border-bottom: 1px solid var(--border); display: flex; align-items: center; gap: 10px; background: var(--surface-alt); } .chat-head span { width: 8px; height: 8px; border-radius: 4px; background: var(--ok); } .chat-head small { font-family: var(--mono); color: var(--ink-3); }
.feed { flex: 1; padding: 14px; overflow: auto; display: flex; flex-direction: column; gap: 6px; }
.chat-input { padding: 10px 14px; border-top: 1px solid var(--border); } .chat-input label { display: flex; align-items: center; gap: 8px; border: 1.5px solid var(--border-strong); border-radius: 12px; padding: 8px 12px; } .chat-input input { flex: 1; border: 0; outline: 0; font-size: 14px; }
.notice-box { padding: 12px; border: 1px solid var(--border); border-radius: 10px; } .notice-box small { float: right; font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.member-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 6px; margin-top: 10px; } .member-grid button { display: grid; grid-template-columns: 28px 1fr; column-gap: 8px; padding: 7px 9px; border-radius: 8px; background: var(--surface-alt); border: 1px solid var(--border); text-align: left; cursor: pointer; } .member-grid button.offline { opacity: .55; } .member-grid span { width: 28px; height: 28px; border-radius: 14px; background: var(--yolk); display: flex; align-items: center; justify-content: center; font-weight: 800; } .member-grid strong { font-size: 11px; } .member-grid small, .member-grid em { font-family: var(--mono); font-size: 9px; color: var(--ink-3); font-style: normal; }
.stat-grid { display: grid; grid-template-columns: repeat(4,1fr); gap: 10px; margin: 10px 0 14px; } .guild-chart { display: flex; align-items: flex-end; gap: 8px; height: 90px; } .guild-chart div { flex: 1; align-self: stretch; display: flex; flex-direction: column; align-items: center; justify-content: flex-end; gap: 4px; } .guild-chart b { width: 100%; background: var(--ink); border-radius: 4px 4px 0 0; } .guild-chart b.today { background: var(--accent); } .guild-chart b.zero { background: var(--surface-alt); border: 1px dashed var(--border); } .guild-chart small { font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
</style>
