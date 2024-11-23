<template>
    <div class="max-w-[900px] mx-auto px-2 flex flex-col gap-2" @keyup.esc="showRandomUsers = false">
      <!-- 메인 컨텐츠 영역 -->
      <div class="flex flex-col md:flex-row gap-2">
        <!-- 왼쪽 섹션: 프로필 + 캘린더 -->
        <div class="md:w-1/2 flex flex-col gap-2">
          <!-- 프로필 섹션 -->
          <div class="flex items-center gap-6 py-2 pl-6">
            <!-- 프로필 이미지 -->
            <div class="relative">
              <img :src="profileImage" class="w-20 h-20 rounded-full object-cover" alt="프로필 이미지"
                @error="(e) => (e.target.src = '/default-profile.png')" />
            </div>
            <!-- 사용자 정보 -->
            <div class="space-y-2">
              <div class="font-bold text-lg">{{ userProfile.name }}</div>
              <div class="text-gray-600 text-sm">
                {{ userProfile.description || "자기소개가 없습니다." }}
              </div>
            </div>
          </div>
  
          <!-- 캘린더 섹션 -->
          <div class="p-4 rounded-[15px]">
            <Calendar @dateSelected="handleDateSelected" />
          </div>
        </div>
  
        <!-- 오른쪽 섹션: 카테고리 -->
        <div class="md:w-1/2 p-4 rounded-[15px]">
          <CategoryHeader v-if="isCurrentUser" />
          <div class="min-h-[calc(100vh-30rem)] max-h-[calc(100vh-20rem)] overflow-y-auto">
            <CategoryList :selectedDate="selectedDate" :userId="userId" />
          </div>
        </div>
      </div>
      <!-- 잔디 그래프 섹션 -->
      <div class="w-full bg-[#f7f8f9] p-2 rounded-[15px]">
        <GrassGraph :userId="userId" />
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from "vue";
  import { useRoute } from 'vue-router';
  import { useAuthStore } from "@/stores/auth";
  import Calendar from "@/components/Calendar.vue";
  import CategoryList from "@/components/CategoryList.vue";
  import CategoryHeader from "@/components/CategoryHeader.vue";
  import GrassGraph from "@/components/GrassGraph.vue";
  import http from "@/api/http";
  
  const route = useRoute();
  const authStore = useAuthStore();
  const selectedDate = ref(null);
  const userProfile = ref({});
  
  // URL의 userId 파라미터 또는 현재 로그인한 사용자의 ID
  const userId = computed(() => route.params.userId || authStore.user.id);
  
  // 현재 보고 있는 프로필이 로그인한 사용자의 것인지 확인
  const isCurrentUser = computed(() => !route.params.userId || route.params.userId === authStore.user.id?.toString());
  
  // 프로필 이미지 URL
  const profileImage = computed(() => {
    if (userProfile.value?.profileImage) {
      return `${http.defaults.baseURL}/user${userProfile.value.profileImage}`;
    }
    return "/default-profile.png";
  });
  
  // 사용자 프로필 정보 가져오기
  const fetchUserProfile = async () => {
    try {
      if (route.params.userId) {
        const response = await http.get(`/user/${route.params.userId}`);
        userProfile.value = response.data;
      } else {
        userProfile.value = authStore.user;
      }
    } catch (error) {
      console.error('Failed to fetch user profile:', error);
    }
  };
  
  const handleDateSelected = (date) => {
    selectedDate.value = date;
  };
  
  onMounted(() => {
    fetchUserProfile();
  });
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
  </style>
  