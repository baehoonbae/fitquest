<template>
  <div class="max-w-4xl mx-auto px-5 my-1 md:my-2">
    <h1 class="text-3xl font-bold text-gray-800 mb-8">게시글 수정</h1>

    <form @submit.prevent="updatePost" class="bg-white p-8 md:p-5 rounded-lg shadow-sm">
      <div class="mb-6">
        <label for="tag" class="block font-semibold text-gray-700 mb-2">태그</label>
        <select 
          id="tag" 
          v-model="post.tag" 
          required
          class="w-full px-3 py-2.5 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500/10 focus:border-blue-600"
        >
          <option value="" disabled>태그를 선택하세요</option>
          <option value="헬스">헬스</option>
          <option value="요가">요가</option>
          <option value="필라테스">필라테스</option>
          <option value="러닝">러닝</option>
          <option value="홈트">홈트</option>
          <option value="식단">식단</option>
          <option value="기타">기타</option>
          
        </select>
      </div>

      <div class="mb-6">
        <label for="title" class="block font-semibold text-gray-700 mb-2">제목</label>
        <input
          type="text"
          id="title"
          v-model="post.title"
          required
          placeholder="제목을 입력하세요"
          class="w-full px-3 py-2.5 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500/10 focus:border-blue-600"
        />
      </div>

      <div class="mb-6">
        <label for="content" class="block font-semibold text-gray-700 mb-2">내용</label>
        <textarea
          id="content"
          v-model="post.content"
          required
          placeholder="내용을 입력하세요"
          rows="10"
          class="w-full px-3 py-2.5 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500/10 focus:border-blue-600 min-h-[200px] resize-y"
        ></textarea>
      </div>

      <div class="flex justify-end gap-3 mt-8">
        <button 
          type="button" 
          class="px-5 py-2.5 rounded-md font-medium bg-gray-100 text-gray-700 hover:bg-gray-200 transition-all duration-200"
          @click="goBack"
        >
          취소
        </button>
        <button 
          type="submit" 
          class="px-5 py-2.5 rounded-md font-medium bg-blue-600 text-white hover:bg-blue-700 transition-all duration-200"
        >
          수정 완료
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { useBoardStore } from "@/stores/board";
import http from "@/api/http";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const boardStore = useBoardStore();

const post = ref({
  id: null,
  userId: null,
  writer: "",
  tag: "",
  title: "",
  content: "",
});

const fetchPost = async () => {
  try {
    const response = await http.get(`/board/${route.params.id}`);
    if (response.status === 200) {
      const boardData = response.data;

      if (boardData.userId !== authStore.user.id) {
        alert("수정 권한이 없습니다.");
        router.push("/community");
        return;
      }

      post.value = {
        id: boardData.id,
        userId: boardData.userId,
        writer: boardData.writer,
        tag: boardData.tag,
        title: boardData.title,
        content: boardData.content,
      };
    }
  } catch (error) {
    console.error("Error fetching post:", error);
    alert("게시글을 불러오는데 실패했습니다.");
    router.push("/community");
  }
};

const updatePost = async () => {
  try {
    const token = authStore.getToken();
    if (!token) {
      alert("로그인이 필요합니다.");
      router.push("/login");
      return;
    }

    const response = await http.put(`/board/${post.value.id}`, post.value);

    if (response.status === 200) {
      alert("게시글이 수정되었습니다.");
      await boardStore.fetchBoards();
      router.push(`/community/detail/${post.value.id}`);
    }
  } catch (error) {
    console.error("Error updating post:", error);
    if (error.response?.status === 401) {
      alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
      authStore.logout();
    } else {
      alert("게시글 수정에 실패했습니다.");
    }
  }
};

const goBack = () => {
  router.push(`/community/detail/${route.params.id}`);
};

onMounted(async () => {
  if (!authStore.user.isAuthenticated) {
    alert("로그인이 필요합니다.");
    router.push("/login");
    return;
  }
  await fetchPost();
});
</script>