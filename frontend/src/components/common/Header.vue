<template>
  <div class="flex justify-between items-center h-[50px] px-4">
    <button @click="router.go(-1)" class="flex items-center">
      <span class="material-icons text-4xl">arrow_back</span>
    </button>
    <div class="relative">
      <button @click="isDropdownOpen = !isDropdownOpen">
        <span class="material-icons text-4xl">menu</span>
      </button>
      <Transition name="dropdown">
        <div
          v-if="isDropdownOpen"
          class="absolute right-0 mt-2 w-[155px] font-semibold bg-white rounded-[20px] shadow-[0_4px_20px_rgba(0,0,0,0.15)] py-2 z-50"
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
import { RouterLink, useRouter } from "vue-router";
import { onMounted, onUnmounted, ref } from "vue";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
const isDropdownOpen = ref(false);
const router = useRouter();

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
});

// 컴포넌트가 언마운트될 때 이벤트 리스너 제거
onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
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
