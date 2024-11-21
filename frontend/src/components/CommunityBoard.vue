<template>
  <div class="bg-white rounded-lg shadow-sm my-5 w-full max-w-7xl mx-auto">
    <div class="w-full">
      <!-- 게시판 헤더 -->
      <div
        class="hidden md:grid md:grid-cols-[100px_2fr_350px] md:px-5 md:py-4 gap-6 bg-gray-50 border-b-2 border-gray-200 font-semibold text-gray-700"
      >
        <div class="text-center">태그</div>
        <div class="text-center px-4">제목</div>
        <div class="grid grid-cols-[2fr_2fr_1fr_1fr] text-center gap-4">
          <span>작성자</span>
          <span>작성일</span>
          <span>조회수</span>
          <span>좋아요</span>
        </div>
      </div>

      <!-- 게시글 목록 -->
      <div
        v-for="board in boards"
        :key="board.id"
        class="grid md:grid-cols-[100px_2fr_350px] grid-cols-1 gap-6 p-5 border-b border-gray-200 hover:bg-gray-50 transition-colors duration-200 cursor-pointer items-center"
        @click="openBoard(board.id)"
      >
        <div class="text-blue-600 text-sm font-medium md:text-base text-center">
          #{{ board.tag }}
        </div>
        <div class="text-center">
          {{ board.title }}
          <span
            v-if="board.commentCount > 0"
            class="text-gray-500 text-sm ml-1"
          >
            [{{ board.commentCount }}]
          </span>
        </div>
        <div
          class="grid grid-cols-[2fr_2fr_1fr_1fr] items-center text-center text-sm text-gray-700 gap-4"
        >
          <span class="font-medium break-words">{{ board.writer }}</span>
          <span class="text-gray-500">{{ formatDate(board.date) }}</span>
          <span class="flex items-center justify-center gap-1">
            <i class="fas fa-eye text-gray-400 text-xs"></i>
            {{ board.viewCount }}
          </span>
          <span class="flex items-center justify-center gap-1">
            <i class="fas fa-heart text-gray-400 text-xs"></i>
            {{ board.hitCount || 0 }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

const props = defineProps({
  boards: {
    type: Array,
    required: true,
  },
});

const formatDate = (date) => {
  if (!date) return "";
  const dateObj = new Date(date);
  const year = dateObj.getFullYear();
  const month = String(dateObj.getMonth() + 1).padStart(2, "0");
  const day = String(dateObj.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const openBoard = (id) => {
  router.push(`/community/detail/${id}`);
};

const truncateContent = (content) => {
  if (!content) return "";
  return content.length > 50 ? content.slice(0, 50) + "..." : content;
};
</script>
