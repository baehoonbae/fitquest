<template>
  <div class="max-w-7xl mx-auto py-8">
    <!-- 유저 상세 모달 -->
    <Transition name="modal">
      <div v-if="selectedUser" class="fixed inset-0 z-50" @click="$emit('closeModal')" @keyup.esc="$emit('closeModal')" tabindex="0">
        <!-- 모달 배경 -->
        <div class="absolute inset-0 bg-black/50 backdrop-blur-sm"></div>

        <div class="relative h-full flex items-center justify-center">
          <!-- 이전 유저 버튼 -->
          <button @click.stop="showPrevUser"
            class="absolute left-4 md:left-8 p-2 text-white hover:bg-white/10 rounded-full transition-colors"
            :disabled="!hasPrevUser">
            <ChevronLeftIcon class="w-8 h-8" :class="{ 'opacity-50': !hasPrevUser }" />
          </button>

          <!-- 유저 카드 -->
          <div class="bg-white rounded-2xl shadow-2xl w-[90%] max-w-md mx-4" style="height: 85vh; max-height: 800px"
            @click.stop>
            <!-- 닫기 버튼 -->
            <button @click="$emit('closeModal')" class="absolute top-4 right-4 z-10 text-gray-500 hover:text-gray-700">
              <XMarkIcon class="w-6 h-6" />
            </button>

            <!-- 스크롤 가능한 컨텐츠 영역 -->
            <div class="h-full overflow-y-auto">
              <!-- 프로필 헤더 -->
              <div class="relative h-48 bg-gradient-to-br from-blue-500 to-blue-600">
                <div class="absolute -bottom-16 w-full flex justify-center">
                  <div class="w-32 h-32 rounded-full border-4 border-white bg-gray-200 overflow-hidden">
                    <img :src="selectedUser.profileImage || '/default-profile.png'" :alt="selectedUser.nickname"
                      class="w-full h-full object-cover" />
                  </div>
                </div>
              </div>

              <!-- 유저 정보 -->
              <div class="pt-20 px-6 pb-8">
                <div class="text-center mb-8">
                  <h2 class="text-2xl font-bold text-gray-800">{{ selectedUser.nickname }}</h2>
                  <p class="text-gray-500 mt-1">가입일: {{ formatDate(selectedUser.createdAt) }}</p>
                </div>

                <!-- 통계 -->
                <div class="grid grid-cols-2 gap-4 mb-8">
                  <div class="bg-gray-50 rounded-xl p-4 text-center">
                    <p class="text-gray-500">게시글</p>
                    <p class="text-2xl font-bold text-gray-800 mt-1">{{ selectedUser.postCount || 0 }}</p>
                  </div>
                  <div class="bg-gray-50 rounded-xl p-4 text-center">
                    <p class="text-gray-500">댓글</p>
                    <p class="text-2xl font-bold text-gray-800 mt-1">{{ selectedUser.commentCount || 0 }}</p>
                  </div>
                </div>

                <!-- 최근 활동 -->
                <div class="space-y-4">
                  <h3 class="font-semibold text-gray-800">최근 활동</h3>
                  <div class="space-y-3">
                    <div v-for="activity in selectedUser.recentActivities" :key="activity.id"
                      class="p-4 bg-gray-50 rounded-xl">
                      <p class="text-sm text-gray-600">{{ activity.content }}</p>
                      <p class="text-xs text-gray-400 mt-1">{{ formatDate(activity.date) }}</p>
                    </div>
                  </div>
                </div>

                <!-- 프로필 버튼 -->
                <button @click="goToProfile(selectedUser.id)"
                  class="w-full mt-8 px-4 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-colors">
                  프로필 보기
                </button>
              </div>
            </div>
          </div>

          <!-- 다음 유저 버튼 -->
          <button @click.stop="showNextUser"
            class="absolute right-4 md:right-8 p-2 text-white hover:bg-white/10 rounded-full transition-colors"
            :disabled="loading">
            <div v-if="loading" class="animate-spin">
              <ArrowPathIcon class="w-8 h-8" />
            </div>
            <ChevronRightIcon v-else class="w-8 h-8" />
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeMount } from 'vue';
import { useRouter } from 'vue-router';
import {
  ArrowPathIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline';
import http from '@/api/http';

const router = useRouter();
const users = ref([]);
const shuffledUsers = ref([]);
const userHistory = ref([]);
const currentIndex = ref(-1);
const selectedUser = ref(null);
const loading = ref(false);

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

const shuffleUsers = () => {
  shuffledUsers.value = [...users.value]
    .sort(() => Math.random() - 0.5)
    .slice(0, 9);
};

const openUserModal = (user) => {
  selectedUser.value = user;
  currentIndex.value = shuffledUsers.value.findIndex(u => u.id === user.id);
};

const showPrevUser = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--;
    selectedUser.value = userHistory.value[currentIndex.value];
  }
};

const showNextUser = () => {
  if (currentIndex.value < userHistory.value.length - 1) {
    currentIndex.value++;
    selectedUser.value = userHistory.value[currentIndex.value];
  } else {
    fetchRandomUser();
  }
};

const goToProfile = (userId) => {
  router.push(`/profile/${userId}`);
};

const fetchRandomUser = async () => {
  try {
    loading.value = true;
    const response = await http.get('/user/random');
    const newUser = response.data;

    if (currentIndex.value < userHistory.value.length - 1) {
      userHistory.value = userHistory.value.slice(0, currentIndex.value + 1);
    }

    userHistory.value.push(newUser);
    currentIndex.value++;
    selectedUser.value = newUser;
  } catch (error) {
    console.error('Failed to fetch random user:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchRandomUser();
});

const hasPrevUser = computed(() => currentIndex.value > 0);
const hasNextUser = computed(() => true);
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .bg-white {
  transform: scale(0.95);
}

.modal-enter-active .bg-white {
  transition: transform 0.3s ease;
}

.modal-enter-to .bg-white {
  transform: scale(1);
}

/* 스크롤바 스타일링 */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background-color: #94a3b8;
}
</style>