<template>
  <div class="min-h-screen flex flex-col">
    <header v-if="!hideLayout" class="fixed top-0 left-0 right-0 bg-white z-[99]">
      <div class="max-w-[950px] mx-auto px-5 md:px-4">
        <Header @search="handleSearch" />
      </div>
    </header>

    <main :class="['flex-1', { 'mt-[60px] mb-[60px]': !hideLayout, 'my-0': hideLayout }]">
      <div v-if="route.name === 'news'" class="max-w-[1600px] mx-auto px-5 md:px-4">
        <RouterView />
      </div>
      <div v-else class="max-w-7xl mx-auto px-5 md:px-4">
        <RouterView />
      </div>
    </main>

    <footer v-if="!hideLayout" class="fixed bottom-0 left-0 right-0 bg-white z-[99]">
      <div class="max-w-7xl mx-auto px-5 md:px-4">
        <Footer />
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
