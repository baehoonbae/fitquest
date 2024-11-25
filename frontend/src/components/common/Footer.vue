<template>
  <div class="flex justify-center gap-16 items-center h-[60px]">
    <RouterLink to="/" class="text-gray-700 hover:text-blue-600 transition-colors duration-200">
      <span class="material-icons text-4xl">home</span>
    </RouterLink>
    <RouterLink to="/community" class="text-gray-700 hover:text-blue-600 transition-colors duration-200">
      <span class="material-icons text-4xl">groups</span>
    </RouterLink>
    <button @click="openUserSearchModal"
      class="text-gray-700 hover:text-blue-600 transition-colors duration-200 focus:outline-none">
      <span class="material-icons text-3xl">search</span>
    </button>
    <RouterLink to="/video" class="text-gray-700 hover:text-blue-600 transition-colors duration-200">
      <span class="material-icons text-4xl">video_library</span>
    </RouterLink>
    <RouterLink to="/news" class="text-gray-700 hover:text-blue-600 transition-colors duration-200">
      <span class="material-icons text-4xl">newspaper</span>
    </RouterLink>
    <button @click="check" class="text-gray-700 hover:text-blue-600 transition-colors duration-200">
      <span class="material-icons text-4xl">person</span>
    </button>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const emit = defineEmits(['openUserSearchModal', 'needLoginAlert']);
const authStore = useAuthStore();
const router = useRouter();

const check = () => {
  if (!authStore.user.isAuthenticated) {
    emit('needLoginAlert');
  } else {
    router.push('/config');
  }
};

const openUserSearchModal = () => {
  emit('openUserSearchModal');
};
</script>