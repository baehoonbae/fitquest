<template>
  <div class="space-y-6">
    <div
      v-for="comment in comments"
      :key="comment.id"
      class="comment-container"
    >
      <!-- 댓글 내용 -->
      <div
        :class="[
          'p-4 rounded-lg',
          comment.parentId ? `ml-${8 * getCommentDepth(comment)}` : 'ml-0',
          'bg-white border border-gray-200',
        ]"
      >
        <div class="flex justify-between items-start mb-2">
          <div class="flex items-center gap-2">
            <span class="font-medium text-gray-900">{{ comment.writer }}</span>
            <span class="text-sm text-gray-500">{{
              formatDate(comment.date)
            }}</span>
          </div>
          <div class="flex items-center gap-2">
            <button
              v-if="isAuthenticated"
              @click="showReplyForm(comment.id)"
              class="text-sm text-gray-600 hover:text-blue-600"
            >
              답글
            </button>
            <button
              v-if="canDelete(comment)"
              @click="handleDelete(comment.id)"
              class="text-sm text-red-500 hover:text-red-600"
            >
              삭제
            </button>
          </div>
        </div>
        <p class="text-gray-700 whitespace-pre-line">{{ comment.content }}</p>
      </div>

      <!-- 대댓글 작성 폼 -->
      <div v-if="activeReplyId === comment.id" class="mt-3 ml-8">
        <CommentForm
          :boardId="boardId"
          :parentId="comment.id"
          @comment-added="handleCommentAdded"
          @cancel="activeReplyId = null"
        />
      </div>
    </div>

    <!-- 댓글이 없을 때 -->
    <div v-if="comments.length === 0" class="text-center py-8 text-gray-500">
      첫 댓글을 작성해보세요.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useAuthStore } from "@/stores/auth";
import CommentForm from "./CommentForm.vue";
import http from "@/api/http";

const props = defineProps({
  boardId: {
    type: Number,
    required: true,
  },
});

const authStore = useAuthStore();
const comments = ref([]);
const activeReplyId = ref(null);

const isAuthenticated = computed(() => authStore.user.isAuthenticated);

// 댓글의 깊이를 계산하는 함수
const getCommentDepth = (comment) => {
  let depth = 0;
  let currentComment = comment;

  while (currentComment.parentId) {
    depth++;
    currentComment = comments.value.find(
      (c) => c.id === currentComment.parentId
    );
    if (!currentComment) break;
  }

  return depth;
};

const fetchComments = async () => {
  try {
    const response = await http.get(`/comment/board/${props.boardId}`);
    comments.value = response.data;
  } catch (error) {
    console.error("댓글 로딩 실패:", error);
  }
};

const showReplyForm = (id) => {
  activeReplyId.value = id;
};

const handleCommentAdded = () => {
  activeReplyId.value = null;
  fetchComments();
};

const handleDelete = async (id) => {
  if (!confirm("댓글을 삭제하시겠습니까?")) return;

  try {
    await http.delete(`/api/comment/${id}`);
    fetchComments();
  } catch (error) {
    console.error("댓글 삭제 실패:", error);
    alert("댓글 삭제에 실패했습니다.");
  }
};

const canDelete = (comment) => {
  return isAuthenticated.value && comment.userId === authStore.user.id;
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

defineExpose({
  fetchComments,
});

onMounted(() => {
  fetchComments();
});
</script>
