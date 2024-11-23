<template>
  <div class="min-h-[calc(100vh-10rem)] flex flex-col">
    <header v-if="!hideLayout" class="flex-none fixed top-0 left-0 right-0 bg-white z-[99]">
      <div v-if="route.name !== 'news'" class="max-w-[950px] mx-auto px-5 md:px-4">
        <Header />
      </div>
    </header>
    <main :class="[
      'flex-1 overflow-hidden',
      { 'pt-[60px]': !hideLayout, 'pt-0': hideLayout }
    ]" tabindex="0" @click="openUserSearchModal = false" @keyup.esc="openUserSearchModal = false">
      <div v-if="route.name === 'news'" class="max-w-[1600px] h-full mx-auto px-5 md:px-4">
        <div v-if="openUserSearchModal" class="fixed top-0 left-0 right-0 bottom-0 z-[105]">
          <UserSearchModal @closeModal="openUserSearchModal = false" />
        </div>
        <RouterView />
      </div>
      <div v-else class="max-w-7xl h-full mx-auto px-5 md:px-4">
        <div v-if="openUserSearchModal" class="fixed top-0 left-0 right-0 bottom-0 z-[105]">
          <UserSearchModal @closeModal="openUserSearchModal = false" />
        </div>
        <RouterView />
      </div>
    </main>

    <footer v-if="!hideLayout" class="flex-none fixed bottom-0 left-0 right-0 transition-transform duration-300"
      :class="[isFooterVisible ? 'translate-y-0' : 'translate-y-full']">
      <div class="relative" @mouseenter="showFooter" @mouseleave="hideFooter">
        <!-- 호버 영역 -->
        <div class="absolute bottom-full left-0 right-0 h-2 bg-transparent">
          <div class="absolute bottom-0 left-1/2 -translate-x-1/2 text-gray-400">
            <span class="material-icons animate-bounce">expand_less</span>
          </div>
        </div>
        <!-- Footer 컨텐츠 -->
        <div class="bg-white rounded-t-xl w-[570px] mx-auto px-5 md:px-4 shadow-[0_-2px_2px_rgba(0,0,0,0.1)]">
          <Footer @openUserSearchModal="openUserSearchModal = true" />
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, computed, ref } from "vue";
import { useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import Header from "@/components/common/Header.vue";
import Footer from "@/components/common/Footer.vue";
import UserSearchModal from "./components/UserSearchModal.vue";

const route = useRoute();
const authStore = useAuthStore();
const openUserSearchModal = ref(false);

// 현재 라우트의 meta.hideLayout 값을 확인
const hideLayout = computed(() => route.meta.hideLayout);

const isFooterVisible = ref(false);

const showFooter = () => {
  isFooterVisible.value = true;
};

const hideFooter = () => {
  isFooterVisible.value = false;
};

const openRandomUsers = () => {
  openUserSearchModal.value = true;
};

onMounted(() => {
  authStore.fetchUserInfo();
});
</script>

<style>
/* 웹킷 기반 브라우저 (Chrome, Safari, Edge 등) */
::-webkit-scrollbar {
  display: none;
}

/* Firefox */
* {
  scrollbar-width: none;
}

/* IE, Edge legacy */
* {
  -ms-overflow-style: none;
}

/* 텍스트 선택 비활성화 */
* {
  -webkit-user-select: none;
  /* Chrome, Safari, Opera */
  -moz-user-select: none;
  /* Firefox */
  -ms-user-select: none;
  /* IE, Edge */
  user-select: none;
  /* 표준 문법 */
}

/* 입력 필드에서는 텍스트 선택 활성화 */
input,
textarea {
  -webkit-user-select: text;
  -moz-user-select: text;
  -ms-user-select: text;
  user-select: text;
}
</style>
