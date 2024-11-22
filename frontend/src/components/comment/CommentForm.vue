<!-- components/comment/CommentForm.vue -->
<template>
  <div class="comment-form">
    <div class="flex items-start gap-4">
      <!-- 댓글 입력 영역 -->
      <div class="flex-1 flex gap-1">
        <textarea
          v-model="content"
          placeholder="댓글을 남겨주세요."
          class="flex-1 p-3 border border-gray-300 rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        ></textarea>
        <div class="flex items-start">
          <button
            v-if="parentId"
            @click="$emit('cancel')"
            class="mr-2 px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors duration-200"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useAuthStore } from "@/stores/auth";
import http from "@/api/http";

const props = defineProps({
  boardId: {
    type: Number,
    required: true,
  },
  parentId: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits(["comment-added", "cancel"]);
const authStore = useAuthStore();
const content = ref("");

const submitComment = async () => {
  if (!content.value.trim()) {
    alert("댓글 내용을 입력해주세요.");
    return;
  }

  try {
    const comment = {
      boardId: props.boardId,
      userId: authStore.user.id,
      writer: authStore.user.name,
      content: content.value.trim(),
      parentId: props.parentId > 0 ? props.parentId : null,
    };

    await http.post("/comment", comment);
    content.value = "";
    emit("comment-added");
  } catch (error) {
    console.error("댓글 작성 실패:", error);
    alert("댓글 작성에 실패했습니다.");
  }
};

console.log("전송할 댓글 데이터:", {
  boardId: props.boardId,
  userId: authStore.user.id,
  writer: authStore.user.name,
  content: content.value.trim(),
  parentId: props.parentId || 0,
});

// submitComment 메서드를 defineExpose를 통해 외부에서 접근 가능하게 만듦
defineExpose({
  submitComment,
});
</script>

<style scoped>
textarea {
  min-height: 36px; /* 높이 줄임 */
  max-height: 200px;
}
</style>
