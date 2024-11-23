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
                        <div class="flex items-center gap-2">
                            <div class="font-bold text-lg">{{ userProfile.name }}</div>
                            <button @click="handleFollow"
                                class="ml-3 px-2 py-0.5 bg-black hover:bg-gray-800 text-white rounded-lg text-sm font-medium transition duration-200">
                                팔로우
                            </button>
                        </div>
                        <div class="text-gray-600 text-sm">
                            {{ userProfile.description || "자기소개가 없습니다." }}
                        </div>
                    </div>
                </div>

                <!-- 캘린더 섹션 -->
                <div class="p-4 rounded-[15px]">
                    <Calendar @dateSelected="handleDateSelected" :userId="Number(userId)" />
                </div>
            </div>

            <!-- 오른쪽 섹션: 카테고리 -->
            <div class="md:w-1/2 p-4 rounded-[15px]">
                <div class="mb-[44px]" />
                <div class="min-h-[calc(100vh-30rem)] max-h-[calc(100vh-20rem)] overflow-y-auto">
                    <CategoryList :selectedDate="selectedDate" :userId="Number(userId)" />
                </div>
            </div>
        </div>
        <!-- 잔디 그래프 섹션 -->
        <div class="w-full bg-[#f7f8f9] p-2 rounded-[15px]">
            <GrassGraph :userId="Number(userId)" />
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from 'vue-router';
import Calendar from "@/components/Calendar.vue";
import CategoryList from "@/components/CategoryList.vue";
import GrassGraph from "@/components/GrassGraph.vue";
import http from "@/api/http";

const route = useRoute();
const selectedDate = ref(null);
const userProfile = ref({});

// URL의 userId 파라미터 또는 현재 로그인한 사용자의 ID
const userId = computed(() => route.params.userId);

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
        if (userId.value) {
            const response = await http.get(`/user/${userId.value}`);
            userProfile.value = response.data;
        }
    } catch (error) {
        console.error('Failed to fetch user profile:', error);
    }
};

const handleDateSelected = (date) => {
    selectedDate.value = date;
};

const handleFollow = async () => {
    await http.post(`/user/${userId.value}/follow`);
    fetchUserProfile();
};

onMounted(() => {
    fetchUserProfile();
});

// route.params.userId가 변경될 때마다 프로필 정보 다시 가져오기
watch(() => userId.value, () => {
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