<template>
  <div class="max-w-[900px] mx-auto px-2 flex flex-col gap-2">
    <div class="flex flex-col md:flex-row gap-2">
      <!-- 왼쪽 섹션: 프로필 + 캘린더 -->
      <div class="md:w-1/2 flex flex-col gap-2">
        <!-- 프로필 섹션 -->
        <div class="flex items-center gap-6 py-4 pl-6 bg-white rounded-2xl shadow-md">
          <!-- 프로필 이미지 -->
          <div class="relative group">
            <img :src="profileImage"
              class="w-20 h-20 rounded-full object-cover border-3 border-white shadow-md group-hover:scale-105 transition duration-300"
              alt="프로필 이미지" @error="(e) => (e.target.src = '/default-profile.png')" />
          </div>
          <!-- 팔로워/팔로잉 정보 -->
          <div class="flex gap-4 items-center">
            <div class="flex flex-col items-center px-4 py-2 hover:bg-gray-50 rounded-xl transition-all duration-200">
              <span class="font-bold text-lg text-gray-800">{{ doneTodoCount }}</span>
              <span class="material-icons text-gray-600 text-md">check_circle</span>
              <span class="text-xs text-gray-500 font-medium">완료</span>
            </div>
            <button @click="showFollowers = true"
              class="flex flex-col items-center px-4 py-2 hover:bg-gray-50 rounded-xl transition-all duration-200">
              <span class="font-bold text-lg text-gray-800">{{ followerList.length }}</span>
              <span class="material-icons text-gray-600 text-md">group</span>
              <span class="text-xs text-gray-500 font-medium">팔로워</span>
            </button>
            <button @click="showFollowings = true"
              class="flex flex-col items-center px-4 py-2 hover:bg-gray-50 rounded-xl transition-all duration-200">
              <span class="font-bold text-lg text-gray-800">{{ followingList.length }}</span>
              <span class="material-icons text-gray-600 text-md">person_add</span>
              <span class="text-xs text-gray-500 font-medium">팔로잉</span>
            </button>
          </div>
        </div>
        <!-- 사용자 정보 -->
        <div class="pl-6 py-3 bg-white rounded-2xl shadow-md">
          <div class="flex items-center justify-between">
            <div class="font-bold text-gray-800">{{ userProfile.name }}</div>
            <div v-if="authStore.user.id !== userId">
              <button v-if="!isFollowing" @click="handleFollow"
                class="mr-4 px-2 py-0.5 bg-black hover:bg-gray-800 text-white rounded-lg text-sm font-medium transition duration-200 whitespace-nowrap">
                팔로우
              </button>
              <button v-else @click="handleUnfollow"
                class="mr-4 px-2 py-0.5 bg-gray-200 hover:bg-gray-300 text-gray-800 rounded-lg text-sm font-medium transition duration-200 whitespace-nowrap">
                언팔로우
              </button>
            </div>
          </div>
          <div class="text-gray-600 text-sm">
            {{ userProfile.description || "자기소개가 없습니다." }}
          </div>
        </div>
        <!-- 캘린더 섹션 -->
        <div class="rounded-[15px]">
          <Calendar @dateSelected="handleDateSelected" :userId="Number(userId)" />
        </div>
      </div>
      <!-- 오른쪽 섹션: 카테고리 -->
      <div class="md:w-1/2">
        <div class="p-4 rounded-[15px] bg-white shadow-md">
          <div class="h-[632px] overflow-y-auto">
            <CategoryList :selectedDate="selectedDate" :userId="Number(userId)"
              @doneTodoCountUpdate="fetchDoneTodoCount" />
          </div>
        </div>
      </div>
    </div>
    <!-- 잔디 그래프 섹션 (전체 너비) -->
    <div class="w-full bg-[#f7f8f9] p-2 rounded-[15px]">
      <GrassGraph :userId="Number(userId)" />
    </div>
  </div>

  <Followers v-if="showFollowers" @close="closeFollowers" :followers="followerList" :userId="Number(userId)" />
  <Followings v-if="showFollowings" @close="closeFollowings" :followings="followingList" :userId="Number(userId)" />
  <NeedLoginAlert v-if="needLoginAlert" @close="needLoginAlert = false" />
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from 'vue-router';
import Calendar from "@/components/Calendar.vue";
import CategoryList from "@/components/CategoryList.vue";
import GrassGraph from "@/components/GrassGraph.vue";
import http from "@/api/http";
import { useAuthStore } from "@/stores/auth";
import { useFollowStore } from "@/stores/follow";
import Followers from "@/components/Followers.vue";
import Followings from "@/components/Followings.vue";
import NeedLoginAlert from "@/components/alert/NeedLoginAlert.vue";

const route = useRoute();
const authStore = useAuthStore();
const followStore = useFollowStore();
const selectedDate = ref(null);
const userProfile = ref({});
const doneTodoCount = ref(0);
const followerList = ref([]);
const followingList = ref([]);
const isFollowing = ref(false);
const showFollowers = ref(false);
const showFollowings = ref(false);
const needLoginAlert = ref(false);

// URL의 userId 파라미터 또는 현재 로그인한 사용자의 ID
const userId = computed(() => route.params.userId);

// 프로필 이미지 URL
const profileImage = computed(() => {
    if (userProfile.value?.profileImage) {
        return `${http.defaults.baseURL}/user${userProfile.value.profileImage}`;
    }
    return "/default-profile.png";
});

// 투두 수 가져오기
const fetchDoneTodoCount = async () => {
    try {
        const response = await http.get(`/todo/count/${userId.value}`);
        doneTodoCount.value = response.data;
    } catch (error) {
        console.error('투두 수 조회 실패:', error);
    }
};

// 사용자 프로필 정보 가져오기
const fetchUserProfile = async () => {
    try {
        if (userId.value) {
            const response = await http.get(`/user/${userId.value}`);
            userProfile.value = response.data;
        }
    } catch (error) {
        console.error('프로필 정보 조회 실패:', error);
    }
};

const handleDateSelected = (date) => {
    selectedDate.value = date;
};

// 팔로워/팔로잉/팔로우 상태 조회
const fetchFollowData = async () => {
    try {
        const [followers, followings, followStatus] = await Promise.all([
            followStore.fetchFollowers(userId.value),
            followStore.fetchFollowings(userId.value),
            followStore.fetchIsFollowing(userId.value)
        ]);
        followerList.value = followers;
        followingList.value = followings;
        isFollowing.value = followStatus;
    } catch (error) {
        console.error('팔로우 데이터 조회 실패:', error);
    }
};

// 팔로우/언팔로우 핸들러
const handleFollow = async () => {
    const isAuth = await authStore.checkAuth();
    if (!isAuth) {
        needLoginAlert.value = true;
        return;
    }
    await followStore.fetchFollow(userId.value);
    await fetchFollowData();
};

const handleUnfollow = async () => {
    const isAuth = await authStore.checkAuth();
    if (!isAuth) {
        needLoginAlert.value = true;
        return;
    }
    await followStore.fetchUnfollow(userId.value);
    await fetchFollowData();
};

const closeFollowers = async () => {
    showFollowers.value = false;
    await fetchFollowData();
};

const closeFollowings = async () => {
    showFollowings.value = false;
    await fetchFollowData();
};

onMounted(async () => {
    await fetchUserProfile();
    await Promise.all([
        fetchFollowData(),
        fetchDoneTodoCount()
    ]);
});

watch(() => userId.value, async () => {
    await fetchUserProfile();
    await Promise.all([
        fetchFollowData(),
        fetchDoneTodoCount()
    ]);
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