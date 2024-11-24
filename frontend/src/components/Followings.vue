<template>
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-[101]"
         @click="handleClose">
        <div class="bg-white rounded-lg w-full max-w-md mx-4"
             @click.stop>
            <!-- 모달 헤더 -->
            <div class="flex items-center justify-between p-4 border-b">
                <h2 class="text-lg font-semibold">팔로잉 목록</h2>
                <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
            </div>

            <!-- 모달 본문 -->
            <div class="p-4 max-h-[60vh] overflow-y-auto">
                <!-- 팔로잉이 없는 경우 -->
                <div v-if="!props.followings.length" class="text-center text-gray-500 py-8">
                    팔로잉하는 사용자가 없습니다.
                </div>

                <!-- 팔로잉 목록 -->
                <div v-else class="space-y-4">
                    <div v-for="user in props.followings" :key="user.id"
                        class="flex items-center justify-between p-2 hover:bg-gray-50 rounded-lg">
                        <!-- 사용자 정보 -->
                        <RouterLink :to="`/home/${user.id}`" class="flex items-center gap-3">
                            <img :src="getUserProfileImage(user)" :alt="user.name"
                                class="w-10 h-10 rounded-full object-cover" @error="handleImageError">
                            <div>
                                <div class="font-medium">{{ user.name }}</div>
                                <div class="text-sm text-gray-500">{{ user.description || "자기소개가 없습니다." }}</div>
                            </div>
                        </RouterLink>

                        <!-- 팔로우/언팔로우 버튼 -->
                        <div v-if="authStore.user.id !== user.id">
                            <button v-if="followStatus[user.id]" 
                                @click="handleUnfollow(user.id)"
                                class="ml-3 px-2 py-0.5 bg-gray-200 hover:bg-gray-300 text-gray-800 rounded-lg text-sm font-medium transition duration-200">
                                언팔로우
                            </button>
                            <button v-else 
                                @click="handleFollow(user.id)"
                                class="ml-3 px-2 py-0.5 bg-black hover:bg-gray-800 text-white rounded-lg text-sm font-medium transition duration-200">
                                팔로우
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount } from 'vue';
import http from '@/api/http';
import { useFollowStore } from '@/stores/follow';
import { useAuthStore } from '@/stores/auth';

const emit = defineEmits(['close', 'updateFollowList']);
const props = defineProps({
    userId: {
        type: Number,
        required: true
    },
    followings: {
        type: Array,
        required: true
    }
});

const followStore = useFollowStore();
const authStore = useAuthStore();
const followStatus = ref({});  // 각 사용자별 팔로우 상태를 저장할 객체

// 초기 팔로우 상태 설정
const initFollowStatus = async () => {
    for (const user of props.followings) {
        followStatus.value[user.id] = await followStore.fetchIsFollowing(user.id);
    }
};

// 팔로우 핸들러
const handleFollow = async (userId) => {
    await followStore.fetchFollow(userId);
    followStatus.value[userId] = true;
    emit('updateFollowList');
};

// 언팔로우 핸들러
const handleUnfollow = async (userId) => {
    await followStore.fetchUnfollow(userId);
    followStatus.value[userId] = false;
    emit('updateFollowList');
};

// ESC 키 이벤트 핸들러
const handleKeydown = (e) => {
    if (e.key === 'Escape') {
        emit('close');
    }
};

// 외부 클릭 핸들러
const handleClose = () => {
    emit('close');
};

// 컴포넌트 마운트 시 초기 상태 설정 및 이벤트 리스너 등록
onMounted(() => {
    initFollowStatus();
    window.addEventListener('keydown', handleKeydown);
});

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onBeforeUnmount(() => {
    window.removeEventListener('keydown', handleKeydown);
});

// props.followings가 변경될 때마다 상태 업데이트
watch(() => props.followings, () => {
    initFollowStatus();
}, { deep: true });

// 프로필 이미지 URL 생성
const getUserProfileImage = (user) => {
    if (user.profileImage) {
        return `${http.defaults.baseURL}/user${user.profileImage}`;
    }
    return "/default-profile.png";
};

// 이미지 로드 실패 시 기본 이미지로 대체
const handleImageError = (e) => {
    e.target.src = '/default-profile.png';
};
</script>

<style scoped>
/* 스크롤바 스타일링 */
.overflow-y-auto {
    scrollbar-width: thin;
    scrollbar-color: #CBD5E0 #EDF2F7;
}

.overflow-y-auto::-webkit-scrollbar {
    width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
    background: #EDF2F7;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
    background-color: #CBD5E0;
    border-radius: 3px;
}
</style>