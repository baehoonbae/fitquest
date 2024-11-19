<template>
  <div class="bg-white rounded-lg shadow-sm my-5">
    <div class="w-full">
      <!-- 게시판 헤더 -->
      <div class="hidden md:grid md:grid-cols-[100px_1fr_280px] md:px-5 md:py-4 bg-gray-50 border-b-2 border-gray-200 font-semibold text-gray-700">
        <div>태그</div>
        <div>제목</div>
        <div class="grid grid-cols-3 text-center">
          <span>작성자</span>
          <span>작성일</span>
          <span>조회수</span>
        </div>
      </div>
      
      <!-- 게시글 목록 -->
      <div
        v-for="board in boards"
        :key="board.id"
        class="grid md:grid-cols-[100px_1fr_280px] grid-cols-1 gap-2 md:gap-0 p-5 border-b border-gray-200 hover:bg-gray-50 transition-colors duration-200 cursor-pointer"
        @click="openBoard(board.id)"
      >
        <div class="text-blue-600 text-sm font-medium md:text-base">
          #{{ board.tag }}
        </div>
        <div class="md:pr-5">
          {{ board.title }}
          <p class="mt-2 md:mt-1 text-sm text-gray-500 line-clamp-1">
            {{ truncateContent(board.content) }}
          </p>
        </div>
        <div class="grid grid-cols-3 items-center text-center text-sm text-gray-700 mt-2.5 md:mt-0">
          <span class="font-medium">{{ board.writer }}</span>
          <span class="text-gray-500">{{ formatDate(board.date) }}</span>
          <span class="flex items-center justify-center gap-1">
            <i class="fas fa-eye text-gray-400 text-xs"></i>
            {{ board.viewCount }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

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