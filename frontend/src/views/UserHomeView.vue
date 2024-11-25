<template>
  <div
    class="max-w-[900px] mx-auto px-2 flex flex-col gap-2"
    @keyup.esc="showRandomUsers = false"
  >
    <!-- 메인 컨텐츠 영역 -->
    <div class="flex flex-col md:flex-row gap-2">
      <!-- 왼쪽 섹션: 프로필 + 캘린더 -->
      <div class="md:w-1/2 flex flex-col gap-2 h-[600px]">
        <!-- 통합된 프로필 섹션 -->
        <div class="h-[200px] p-2 bg-white rounded-2xl shadow-md">
          <div class="flex items-center gap-4 mb-1">
            <!-- 프로필 이미지 -->
            <div class="relative group">
              <img
                :src="profileImage || ''"
                class="w-20 h-20 rounded-full object-cover border-3 border-white shadow-md group-hover:scale-105 transition duration-300"
                alt="프로필 이미지"
                @error="(e) => (e.target.src = '/default-profile.png')"
              />
            </div>
            <!-- 사용자 정보 -->
            <div class="flex flex-col">
              <div class="font-bold text-gray-800 text-lg mb-1">
                {{ authStore.user.name }}
              </div>
              <div class="text-gray-600 text-sm">
                {{ authStore.user.description || "자기소개를 입력해주세요." }}
              </div>
            </div>
          </div>
          <!-- 팔로워/팔로잉/완료 정보 -->
          <div class="flex justify-around pt-1 border-t border-gray-100">
            <div
              class="flex flex-col items-center px-4 py-2 hover:bg-gray-50 rounded-xl transition-all duration-200"
            >
              <span class="font-bold text-lg text-gray-800">{{
                doneTodoCount
              }}</span>
              <span class="material-icons text-gray-600 text-md"
                >check_circle</span
              >
              <span class="text-xs text-gray-500 font-medium">완료</span>
            </div>
            <button
              @click="showFollowers = true"
              class="flex flex-col items-center px-4 py-2 hover:bg-gray-50 rounded-xl transition-all duration-200"
            >
              <span class="font-bold text-lg text-gray-800">{{
                followerList.length
              }}</span>
              <span class="material-icons text-gray-600 text-md">group</span>
              <span class="text-xs text-gray-500 font-medium">팔로워</span>
            </button>
            <button
              @click="showFollowings = true"
              class="flex flex-col items-center px-4 py-2 hover:bg-gray-50 rounded-xl transition-all duration-200"
            >
              <span class="font-bold text-lg text-gray-800">{{
                followingList.length
              }}</span>
              <span class="material-icons text-gray-600 text-md"
                >person_add</span
              >
              <span class="text-xs text-gray-500 font-medium">팔로잉</span>
            </button>
          </div>
        </div>

        <!-- 캘린더 섹션 -->
        <div class="h-[400px] rounded-[15px]">
          <Calendar
            @dateSelected="handleDateSelected"
            :userId="Number(authStore.user.id)"
          />
        </div>
      </div>

      <!-- 오른쪽 섹션: 카테고리 -->
      <div class="md:w-1/2">
        <div class="p-3 rounded-[15px] bg-white shadow-md">
          <CategoryHeader />
          <div class="h-[532px] overflow-y-auto">
            <CategoryList
              :selectedDate="selectedDate"
              :userId="Number(authStore.user.id)"
              @doneTodoCountUpdate="fetchDoneTodoCount"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 잔디 그래프 섹션 -->
    <div class="w-full bg-[#f7f8f9] p-2 rounded-[15px]">
      <GrassGraph :userId="Number(authStore.user.id)" />
    </div>
  </div>

  <Followers
    v-if="showFollowers"
    @close="closeFollowers"
    :followers="followerList"
    :userId="Number(authStore.user.id)"
  />
  <Followings
    v-if="showFollowings"
    @close="closeFollowings"
    :followings="followingList"
    :userId="Number(authStore.user.id)"
  />
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
      followStore.fetchFollowings(authStore.user.id),
    ]);
    followerList.value = followers;
    followingList.value = followings;
  } catch (error) {
    console.error("팔로우 데이터 조회 실패:", error);
  }
};

onMounted(async () => {
  await Promise.all([fetchFollowData(), fetchDoneTodoCount()]);
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
    console.error("투두 수 조회 실패:", error);
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
