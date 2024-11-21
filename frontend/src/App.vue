<template>
  <div class="min-h-[calc(100vh-10rem)] flex flex-col">
    <header v-if="!hideLayout" class="flex-none fixed top-0 left-0 right-0 bg-white z-[99]">
      <div class="max-w-[950px] mx-auto px-5 md:px-4">
        <Header @search="handleSearch" />
      </div>
    </header>

    <main 
      :class="[
        'flex-1 overflow-hidden',
        { 'pt-[60px]': !hideLayout, 'pt-0': hideLayout }
      ]"
    >
      <div 
        v-if="route.name === 'news'" 
        class="max-w-[1600px] h-full mx-auto px-5 md:px-4"
      >
        <RouterView />
      </div>
      <div 
        v-else 
        class="max-w-7xl h-full mx-auto px-5 md:px-4"
      >
        <RouterView />
      </div>
    </main>

    <footer 
      v-if="!hideLayout" 
      class="flex-none fixed bottom-0 left-0 right-0 transition-transform duration-300"
      :class="[isFooterVisible ? 'translate-y-0' : 'translate-y-full']"
    >
      <div class="relative" @mouseenter="showFooter" @mouseleave="hideFooter">
        <!-- 호버 영역 -->
        <div class="absolute bottom-full left-0 right-0 h-2 bg-transparent">
          <div class="absolute bottom-0 left-1/2 -translate-x-1/2 text-gray-400">
            <span class="material-icons animate-bounce">expand_less</span>
          </div>
        </div>
        <!-- Footer 컨텐츠 -->
        <div class="bg-white rounded-t-xl w-[500px] mx-auto px-5 md:px-4 shadow-[0_-2px_2px_rgba(0,0,0,0.1)]">
          <Footer />
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, computed, provide, ref } from "vue";
import { useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import Header from "@/components/common/Header.vue";
import Footer from "@/components/common/Footer.vue";

const route = useRoute();
const authStore = useAuthStore();

// 현재 라우트의 meta.hideLayout 값을 확인
const hideLayout = computed(() => route.meta.hideLayout);

const searchQuery = ref(null);
provide("searchQuery", searchQuery);

// 검색 이벤트를 하위 컴포넌트에 제공
const handleSearch = (query) => {
  searchQuery.value = query;
};

const isFooterVisible = ref(false);

const showFooter = () => {
  isFooterVisible.value = true;
};

const hideFooter = () => {
  isFooterVisible.value = false;
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
</style>
