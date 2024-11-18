<template>
  <div class="min-h-screen flex flex-col">
    <header v-if="!hideLayout" class="fixed top-0 left-0 right-0 bg-white z-45">
      <div class="max-w-5xl mx-auto px-5 md:px-4">
        <Header />
      </div>
    </header>

    <main :class="['flex-1', { 'mt-[60px] mb-[60px]': !hideLayout, 'my-0': hideLayout }]">
      <div class="max-w-7xl mx-auto px-5 md:px-4">
        <RouterView />
      </div>
    </main>

    <footer v-if="!hideLayout" class="fixed bottom-0 left-0 right-0 bg-white z-45">
      <div class="max-w-7xl mx-auto px-5 md:px-4">
        <Footer />
      </div>
    </footer>
    <div>ㅎㅇㅎㅇ</div>
    <div>ㅎㅇㅎㅇ</div>
    <div>ㅎㅇㅎㅇ</div>
    <div>ㅎㅇㅎㅇ</div>
    <div>ㅎㅇㅎㅇ</div>
    <div>ㅎㅇㅎㅇ</div>
    <div>ㅎㅇㅎㅇ</div>
    <div>ㅎㅇㅎㅇ</div>
    <div>ㅎㅇㅎㅇ</div>
    <div>ㅎㅇㅎㅇ</div>
    <div>ㅎㅇㅎㅇ</div>
  </div>
</template>

<script setup>
import { onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import Header from "@/components/common/Header.vue";
import Footer from "@/components/common/Footer.vue";

const route = useRoute();
const authStore = useAuthStore();

// 현재 라우트의 meta.hideLayout 값을 확인
const hideLayout = computed(() => route.meta.hideLayout);

onMounted(() => {
  authStore.checkAuth();
});
</script>
