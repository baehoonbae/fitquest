<template>
  <div v-if="isOpen">
    <!-- 모달 오버레이 -->
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click="handleOverlayClick">
      <!-- 모달 컨텐츠 -->
      <div class="bg-white rounded-lg shadow-xl w-11/12 max-w-lg p-6" @click.stop>
        <!-- 헤더 -->
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold text-gray-800">최근 본 게시물</h2>
          <button @click="close" class="text-gray-400 hover:text-gray-500 focus:outline-none">
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- 게시물 목록 -->
        <div class="mt-4">
          <ul v-if="recentPosts.length > 0" class="divide-y divide-gray-200">
            <li v-for="post in recentPosts" :key="post.id" class="py-3 hover:bg-gray-50 transition-colors duration-150"
              @click="navigateToPost(post.id)">
              <router-link :to="`/community/detail/${post.id}`" class="block">
                <div class="flex flex-col">
                  <span class="text-gray-800 font-medium">{{
                    post.title
                  }}</span>
                  <span class="text-sm text-gray-500 mt-1">
                    {{ formatDate(post.createdAt) }}
                  </span>
                </div>
              </router-link>
            </li>
          </ul>

          <!-- 게시물이 없을 때 -->
          <div v-else class="text-center py-8 text-gray-500">
            최근 본 게시물이 없습니다.
          </div>
        </div>

        <!-- 하단 버튼 -->
        <div class="mt-6 flex justify-end">
          <button @click="close"
            class="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-800 rounded-md transition-colors duration-150 focus:outline-none focus:ring-2 focus:ring-gray-300">
            닫기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<!-- RecentBoard.vue -->
<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const props = defineProps({
  isOpen: Boolean,
});

const emit = defineEmits(["close"]);
const recentPosts = ref([]);

// 게시물 클릭 시 처리하는 함수
const navigateToPost = (postId) => {
  close(); // 모달 닫기
  
  // 현재 페이지의 게시물인 경우 router.push 스킵
  if (router.currentRoute.value.path === `/community/detail/${postId}`) {
    return;
  }
  
  router.push({
    path: `/community/detail/${postId}`,
    query: { page: 1 }
  });
};

const loadRecentPosts = () => {
  const posts = JSON.parse(localStorage.getItem("recentPosts") || "[]");
  recentPosts.value = posts;
};

const close = () => {
  emit("close");
};

const formatDate = (date) => {
  if (!date) return "";
  const dateObj = new Date(date);
  return dateObj.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

// 오버레이 클릭 핸들러 추가
const handleOverlayClick = (event) => {
  // 오버레이를 직접 클릭했을 때만 모달 닫기
  if (event.target === event.currentTarget) {
    close();
  }
};

onMounted(() => {
  loadRecentPosts();
  window.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
      close();
    }
  });
});

// isOpen prop이 변경될 때마다 데이터를 새로 로드
watch(
  () => props.isOpen,
  (newValue) => {
    if (newValue) {
      loadRecentPosts();
    }
  }
);
</script>
