<template>
  <span class="brand-logo" :class="{ compact }">
    <img
      class="brand-logo-mark"
      :src="activeLogoSrc"
      alt="냠냠코치 로고"
      :style="markStyle"
    />
    <img
      v-if="showText"
      class="brand-logo-text"
      :src="logoText"
      :alt="text"
      :style="textStyle"
    />
  </span>
</template>

<script setup lang="ts">
import { computed } from "vue";
import logo1 from "../../assets/logo/logo1.png";
import logo2 from "../../assets/logo/logo2.png";
import logo3 from "../../assets/logo/logo3.png";
import logoText from "../../assets/logo/logotext.png";

type LogoKey = "logo1" | "logo2" | "logo3";

// logo1, logo2, logo3 중 하나로 바꾸면 헤더/시작/로그인/회원가입/온보딩 로고가 같이 바뀝니다.
const ACTIVE_LOGO: LogoKey = "logo3";

const logoMap: Record<LogoKey, string> = {
  logo1,
  logo2,
  logo3,
};

const props = withDefaults(
  defineProps<{
    text?: string;
    showText?: boolean;
    markWidth?: number;
    markHeight?: number;
    textWidth?: number;
    textHeight?: number;
    compact?: boolean;
  }>(),
  {
    text: "PlayEat",
    showText: true,
    markWidth: 56,
    markHeight: 56,
    textWidth: 140,
    textHeight: 90,
    compact: false,
  },
);

const activeLogoSrc = computed(() => logoMap[ACTIVE_LOGO]);
const markStyle = computed(() => ({
  width: `${props.markWidth}px`,
  height: `${props.markHeight}px`,
}));
const textStyle = computed(() => ({
  width: `${props.compact ? Math.round(props.textWidth * 0.78) : props.textWidth}px`,
  height: `${props.compact ? Math.round(props.textHeight * 0.78) : props.textHeight}px`,
}));
</script>

<style scoped>
.brand-logo {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  color: inherit;
  line-height: 1;
  width: auto !important;
  height: auto !important;
  border-radius: 0 !important;
  background: transparent !important;
  box-shadow: none !important;
  font-weight: inherit !important;
}
.brand-logo.compact {
  gap: 1px;
}
.brand-logo-mark {
  display: block;
  flex: 0 0 auto;
  object-fit: contain;
  mix-blend-mode: multiply;
}
.brand-logo-text {
  display: block;
  flex: 0 0 auto;
  object-fit: contain;
  margin-left: -10px;
}
</style>
