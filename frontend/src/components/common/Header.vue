<template>
  <div class="flex justify-between items-center h-[50px] px-4">
    <button @click="router.go(-1)" class="flex items-center">
      <span class="material-icons text-4xl">arrow_back</span>
    </button>
    <h1 class="text-lg font-bold w-[700px] text-center">
      <component 
        :is="headerComponent" 
        v-if="headerComponent"
        @search="$emit('search', $event)"
      />
      <template v-else>{{ headerTitle }}</template>
    </h1>
    <div class="relative">
      <button @click="isDropdownOpen = !isDropdownOpen">
        <span class="material-icons text-4xl">menu</span>
      </button>
      <Transition name="dropdown">
        <div
          v-if="isDropdownOpen"
          class="absolute right-0 mt-2 w-[155px] font-semibold bg-white rounded-[20px] shadow-[0_4px_20px_rgba(0,0,0,0.15)] py-2"
        >
          <button class="block px-4 py-2 text-sm text-gray-700">최근 본 게시물</button>
          <div class="h-[1px] bg-gray-100"></div>
          <button class="block px-4 py-2 text-sm text-gray-700">최근 본 카드뉴스</button>
          <div class="h-[1px] bg-gray-100"></div>
          <button
            @click="authStore.logout()"
            class="block w-full text-left px-4 py-2 text-sm text-gray-700"
          >
            로그아웃
          </button>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from "vue-router";
import { onMounted, onUnmounted, ref, computed, shallowRef, watch } from "vue";
import { useAuthStore } from "@/stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const isDropdownOpen = ref(false);

const headerComponent = shallowRef(null);
const headerTitle = computed(() => {
  return route.meta.title || '';
});

// 현재 라우트에 따라 컴포넌트 동적 로드
const loadHeaderComponent = async () => {
  if (route.name === 'news') {
    const CommunitySearch = (await import('@/components/CommunitySearch.vue')).default;
    headerComponent.value = CommunitySearch;
  } else {
    headerComponent.value = null;
  }
};

// 라우트 변경 감지
watch(() => route.name, loadHeaderComponent, { immediate: true });

// 드롭다운 외부 클릭 감지를 위한 이벤트 리스너
const handleClickOutside = (event) => {
  const dropdown = document.querySelector(".relative");
  if (dropdown && !dropdown.contains(event.target)) {
    isDropdownOpen.value = false;
  }
};

// 컴포넌트가 마운트될 때 이벤트 리스너 추가
onMounted(() => {
  document.addEventListener("click", handleClickOutside);
  loadHeaderComponent();
});

// 컴포넌트가 언마운트될 때 이벤트 리스너 제거
onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});

defineEmits(['search']);
</script>

<style scoped>
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
