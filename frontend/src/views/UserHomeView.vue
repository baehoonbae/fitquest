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
            <img :src="profileImage || ''" class="w-20 h-20 rounded-full object-cover" alt="프로필 이미지"
              @error="(e) => (e.target.src = '')" />
          </div>
          <!-- 팔로워/팔로잉 정보 -->
          <div class="flex gap-4 mt-2">
            <div class="flex flex-col items-center px-3 py-1 rounded-lg transition-colors">
              <span class="font-semibold">{{ doneTodoCount }}</span>
              <span class="material-icons text-gray-500 text-sm">check_circle</span>
            </div>
            <button @click="showFollowers = true" class="flex flex-col items-center px-3 py-1 rounded-lg transition-colors">
              <span class="font-semibold">{{ followerList.length }}</span>
              <span class="text-sm text-gray-500">팔로워</span>
            </button>
            <button @click="showFollowings = true" class="flex flex-col items-center px-3 py-1 rounded-lg transition-colors">
              <span class="font-semibold">{{ followingList.length }}</span>
              <span class="text-sm text-gray-500">팔로잉</span>
            </button>
          </div>
        </div>
        <!-- 사용자 정보 -->
        <div class="space-y-2 flex-1 pl-6">
          <div class="font-bold text-lg">{{ authStore.user.name }}</div>
          <div class="text-gray-600 text-sm">
            {{ authStore.user.description || "자기소개가 없습니다." }}
          </div>
        </div>

        <!-- 캘린더 섹션 -->
        <div class="p-4 rounded-[15px]">
          <Calendar @dateSelected="handleDateSelected" :userId="Number(authStore.user.id)" />
        </div>
      </div>

      <!-- 오른쪽 섹션: 카테고리 -->
      <div class="md:w-1/2 p-4 rounded-[15px]">
        <CategoryHeader />
        <div class="min-h-[calc(100vh-30rem)] max-h-[calc(100vh-20rem)] overflow-y-auto">
          <CategoryList :selectedDate="selectedDate" :userId="Number(authStore.user.id)"
            @doneTodoCountUpdate="fetchDoneTodoCount" />
        </div>
      </div>
    </div>
    <!-- 잔디 그래프 섹션 (전체 너비) -->
    <div class="w-full bg-[#f7f8f9] p-2 rounded-[15px]">
      <GrassGraph :userId="Number(authStore.user.id)" />
    </div>
  </div>

  <Followers v-if="showFollowers" @close="closeFollowers" :followers="followerList" :userId="Number(authStore.user.id)" />
  <Followings v-if="showFollowings" @close="closeFollowings" :followings="followingList" :userId="Number(authStore.user.id)" />
</template>

<script setup>
import Calendar from "@/components/Calendar.vue";
import CategoryList from "@/components/CategoryList.vue";
import CategoryHeader from "@/components/CategoryHeader.vue";
import GrassGraph from "@/components/GrassGraph.vue";
import { ref, computed, onMounted } from "vue";
import { useAuthStore } from "@/stores/auth";
import http from "@/api/http";
import Followers from "@/components/Followers.vue";
import Followings from "@/components/Followings.vue";
import { useFollowStore } from "@/stores/follow";

const authStore = useAuthStore();
const selectedDate = ref(null);
const profileImage = computed(() => {
  if (authStore.user?.profileImage) {
    return `${http.defaults.baseURL}/user${authStore.user.profileImage}`;
  }
  return "/default-profile.png";
});
const followerList = ref([]);
const followingList = ref([]);
const doneTodoCount = ref(0);
const showFollowers = ref(false);
const showFollowings = ref(false);
const followStore = useFollowStore();

// 팔로워/팔로잉 조회
const fetchFollowData = async () => {
    try {
        const [followers, followings] = await Promise.all([
            followStore.fetchFollowers(authStore.user.id),
            followStore.fetchFollowings(authStore.user.id)
        ]);
        followerList.value = followers;
        followingList.value = followings;
    } catch (error) {
        console.error('팔로우 데이터 조회 실패:', error);
    }
};

onMounted(async () => {
    await Promise.all([
        fetchFollowData(),
        fetchDoneTodoCount()
    ]);
});

// 모달 닫을 때 데이터 새로고침
const closeFollowers = async () => {
    showFollowers.value = false;
    await fetchFollowData();
};

const closeFollowings = async () => {
    showFollowings.value = false;
    await fetchFollowData();
};

const handleDateSelected = (date) => {
  selectedDate.value = date;
};

// 투두 수 가져오기
const fetchDoneTodoCount = async () => {
  try {
    const response = await http.get(`/todo/count/${authStore.user.id}`);
    doneTodoCount.value = response.data;
  } catch (error) {
    console.error('투두 수 조회 실패:', error);
  }
};
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
