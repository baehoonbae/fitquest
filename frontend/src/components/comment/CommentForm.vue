<!-- components/comment/CommentForm.vue -->
<template>
  <div class="comment-form">
    <textarea
      v-model="content"
      placeholder="댓글을 입력하세요"
      class="w-full min-h-[100px] p-3 border border-gray-300 rounded-lg resize-y focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
    ></textarea>
    <div class="flex justify-end gap-2 mt-3">
      <button
        v-if="parentId"
        @click="$emit('cancel')"
        class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors duration-200"
      >
        취소
      </button>
      <button
        @click="submitComment"
        class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 transition-colors duration-200"
      >
        등록
      </button>
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
      parentId: props.parentId > 0 ? props.parentId : null, // 0 대신 null 사용
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
</script>
