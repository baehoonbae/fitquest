<template>
  <Transition name="modal">
    <div class="fixed inset-0 z-[100]" @click="$emit('closeModal')">
      <!-- 모달 배경 -->
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm"></div>

      <!-- 모달 컨텐츠 -->
      <div class="relative h-full flex items-center justify-center p-4">
        <div class="bg-white rounded-2xl shadow-2xl w-[400px] max-w-2xl" @click.stop>
          <!-- 검색 영역 -->
          <div class="p-4">
            <div class="relative">
              <input v-model="searchQuery" type="text" placeholder="이름으로 검색하세요"
                class="w-full px-4 py-2 pr-10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                @keyup.enter="handleSearch" />
              <button class="absolute right-3 top-1/2 transform -translate-y-1/2" @click="handleSearch">
                <MagnifyingGlassIcon class="w-5 h-5 text-gray-400" />
              </button>
            </div>
          </div>

          <!-- 유저 리스트 -->
          <div class="px-4 pb-4 max-h-[60vh] overflow-y-auto">
            <div v-if="loading" class="flex justify-center py-8">
              <div class="animate-spin">
                <ArrowPathIcon class="w-8 h-8 text-blue-500" />
              </div>
            </div>
            <div v-else-if="users.length === 0 && !searchQuery" class="text-center py-8">
              운동 메이트를 찾아보세요!
            </div>
            <div v-else-if="users.length === 0 && searchQuery" class="text-center py-8 text-gray-500">
              검색 결과가 없습니다.
            </div>

            <div v-else class="grid grid-cols-1 gap-4">
              <div v-for="user in users" :key="user.id"
                class="flex items-center p-4 border rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
                @click="goToProfile(user.id)">
                <!-- 프로필 이미지 -->
                <div class="w-12 h-12 rounded-full bg-gray-200 overflow-hidden mr-4">
                  <img :src="user.profileImage || '/default-profile.png'" :alt="user.name"
                    class="w-full h-full object-cover" />
                </div>

                <!-- 유저 정보 -->
                <div class="flex-1">
                  <h3 class="font-semibold">{{ user.name }}</h3>
                  <p class="text-sm text-gray-500">{{ user.description || "자기소개가 없습니다." }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  MagnifyingGlassIcon,
  ArrowPathIcon,
} from '@heroicons/vue/24/outline';
import http from '@/api/http';
import { debounce } from 'lodash';
import { getChoseong, disassemble } from "es-hangul";
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const emit = defineEmits(['closeModal']);
const searchQuery = ref('');
const users = ref([]);
const loading = ref(false);
const authStore = useAuthStore();

// 디바운스된 검색 함수
const handleSearch = debounce(async () => {
  if (!searchQuery.value.trim()) {
    users.value = [];
    return;
  }
  const isChoseong = getChoseong(searchQuery.value) === searchQuery.value;
  if(isChoseong) {
    searchQuery.value = disassemble(searchQuery.value);
  }
  try {
    loading.value = true;
    const response = await http.post(`/user/search`, {
      word: searchQuery.value,
      isChoseong: isChoseong,
    });
    users.value = response.data.filter(user => user.id !== authStore.user.id);
  } catch (error) {
    console.error('Failed to search users:', error);
  } finally {
    loading.value = false;
  }
}, 300);

const goToProfile = (userId) => {
  emit('closeModal');
  router.push(`/home/${userId}`);
};

onMounted(() => {
  window.addEventListener('keyup', (e) => {
    if (e.key === 'Escape') {
      emit('closeModal');
    }
  });
});

onUnmounted(() => {
  window.removeEventListener('keyup', (e) => {
    if (e.key === 'Escape') {
      emit('closeModal');
    }
  });
});
</script>