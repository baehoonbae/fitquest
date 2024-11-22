<template>
  <div class="max-w-[900px] mx-auto px-2 flex flex-col gap-2">
    <!-- 메인 컨텐츠 영역 -->
    <div class="flex flex-col md:flex-row gap-2">
      <!-- 왼쪽 섹션: 프로필 + 캘린더 -->
      <div class="md:w-1/2 flex flex-col gap-2">
        <!-- 프로필 섹션 -->
        <div class="flex items-center gap-6 py-2 pl-6">
          <!-- 프로필 이미지 -->
          <div class="relative">
            <img
              :src="profileImage || ''"
              class="w-20 h-20 rounded-full object-cover"
              alt="프로필 이미지"
              @error="(e) => (e.target.src = '')"
            />
          </div>
          <!-- 사용자 정보 -->
          <div class="space-y-2">
            <div class="font-bold text-lg">{{ authStore.user.name }}</div>
            <div class="text-gray-600 text-sm">
              {{ authStore.user.description || "자기소개가 없습니다." }}
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
        <CategoryHeader />
        <div class="min-h-[calc(100vh-30rem)] max-h-[calc(100vh-30rem)] overflow-y-auto">
          <CategoryList :selectedDate="selectedDate" />
        </div>
      </div>
    </div>

    <!-- 잔디 그래프 섹션 (전체 너비) -->
    <div class="w-full bg-[#f7f8f9] p-2 rounded-[15px]">
      <GrassGraph />
    </div>
  </div>
</template>

<script setup>
import Calendar from "@/components/Calendar.vue";
import CategoryList from "@/components/CategoryList.vue";
import CategoryHeader from "@/components/CategoryHeader.vue";
import GrassGraph from "@/components/GrassGraph.vue";
import { ref, onMounted } from "vue";
import { useAuthStore } from "@/stores/auth";
import http from "@/api/http";

const authStore = useAuthStore();
const selectedDate = ref(null);
const profileImage = ref(null);

const handleDateSelected = (date) => {
  selectedDate.value = date;
};

// 프로필 이미지 로드
onMounted(() => {
  if (authStore.user.profileImage) {
    // 이미지 URL을 직접 사용
    profileImage.value = `${http.defaults.baseURL}/user${authStore.user.profileImage}`;
  }
});
</script>
