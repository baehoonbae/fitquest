<template>
  <div class="max-w-4xl mx-auto px-5 my-1 md:my-2">
    <!-- 게시글 헤더 -->
    <div class="mb-10 pb-5 border-b-2 border-gray-200">
      <div class="flex justify-between items-center mb-4">
        <span
          class="bg-blue-50 text-blue-600 px-3 py-1.5 rounded-full text-sm font-medium"
        >
          #{{ board.tag }}
        </span>
        <div class="flex gap-2" v-if="isAuthor">
          <button
            class="px-4 py-2 rounded font-medium bg-gray-100 text-gray-600 hover:bg-gray-200 transition-all duration-200"
            @click="handleEdit"
          >
            수정
          </button>
          <button
            class="px-4 py-2 rounded font-medium bg-red-500 text-white hover:bg-red-600 transition-all duration-200"
            @click="handleDelete"
          >
            삭제
          </button>
        </div>
      </div>
      <h1 class="text-4xl md:text-2xl font-bold text-gray-800 mb-4 leading-tight">
        {{ board.title }}
      </h1>
      <div
        class="flex justify-between items-center text-gray-500 text-sm md:flex-col md:items-start md:gap-2"
      >
        <div class="flex gap-3">
          <span class="font-medium text-gray-700">{{ board.writer }}</span>
          <span>{{ formatDate(board.date) }}</span>
        </div>
        <!-- 조회수와 좋아요 부분 수정 -->
        <div class="flex items-center gap-4">
          <div class="flex items-center gap-1.5">
            <i class="fas fa-eye"></i>
            <span>{{ board.viewCount }}</span>
          </div>
          <div class="flex items-center gap-1.5 cursor-pointer" @click="toggleHit">
            <i
              :class="{
                'fas fa-heart text-xl transition-all duration-200': true,
                'text-red-500': isHit,
                'text-gray-300 hover:text-red-500': !isHit,
              }"
            ></i>
            <span>{{ hitCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 게시글 내용 -->
    <div class="min-h-[300px] text-lg md:text-base text-gray-700 leading-relaxed mb-10">
      <p>{{ board.content }}</p>
    </div>

    <!-- 댓글 섹션 -->
    <div class="mt-10 border-t-2 border-gray-200 pt-8">
      <h2 class="text-xl font-bold text-gray-800 mb-6">댓글</h2>

      <!-- 댓글 작성 폼 -->
      <CommentForm
        v-if="authStore.user.isAuthenticated"
        :boardId="Number(route.params.id)"
        @comment-added="refreshComments"
        class="mb-8"
      />
      <div v-else class="mb-8 p-4 bg-gray-50 rounded text-center">
        <p class="text-gray-600">댓글을 작성하려면 로그인이 필요합니다.</p>
      </div>

      <!-- 댓글 목록 -->
      <CommentList :boardId="Number(route.params.id)" ref="commentList" />
    </div>

    <!-- 하단 버튼 -->
    <div class="flex justify-center mt-10 pt-5 border-t border-gray-200">
      <button
        class="px-6 py-2.5 rounded font-medium bg-blue-600 text-white hover:bg-blue-700 transition-all duration-200"
        @click="goBack"
      >
        목록으로
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import http from "@/api/http";
import CommentForm from "@/components/comment/CommentForm.vue";
import CommentList from "@/components/comment/CommentList.vue";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const board = ref({});
const commentList = ref(null);

const isAuthor = computed(() => {
  console.log("Current user ID:", authStore.user.id);
  console.log("Board user ID:", board.value?.userId);
  return (
    authStore.user.isAuthenticated &&
    board.value?.userId &&
    authStore.user.id === board.value.userId
  );
});

const refreshComments = () => {
  if (commentList.value) {
    commentList.value.fetchComments();
  }
};

const incrementViewCount = async (boardData) => {
  try {
    console.log("조회수 증가 전 데이터:", boardData);

    const updatedBoard = {
      id: boardData.id,
      userId: boardData.userId,
      tag: boardData.tag,
      date: boardData.date,
      writer: boardData.writer,
      title: boardData.title,
      content: boardData.content,
      viewCount: (boardData.viewCount || 0) + 1,
    };

    console.log("업데이트할 데이터:", updatedBoard);

    const response = await http.put(`/board/${boardData.id}`, updatedBoard);
    console.log("서버 응답:", response);
  } catch (error) {
    console.error("Error incrementing view count:", error);
    if (error.response) {
      console.error("서버 응답:", error.response.data);
    }
  }
};

const fetchBoardDetail = async () => {
  try {
    const response = await http.get(`/board/${route.params.id}`);
    if (response.status === 200) {
      board.value = response.data;
      console.log("가져온 게시글 데이터:", board.value);

      // 게시글 데이터를 가져온 후 조회수 증가
      await incrementViewCount(board.value);
      // 증가된 조회수 반영을 위해 게시글 정보 다시 가져오기
      const updatedResponse = await http.get(`/board/${route.params.id}`);
      if (updatedResponse.status === 200) {
        board.value = updatedResponse.data;
        console.log("업데이트된 게시글 데이터:", board.value);
      }
    }
  } catch (error) {
    console.error("Error fetching board detail:", error);
    router.push("/community");
  }
};

const handleDelete = async () => {
  if (!authStore.user.isAuthenticated) {
    alert("로그인이 필요합니다.");
    router.push("/login");
    return;
  }

  if (!isAuthor.value) {
    alert("삭제 권한이 없습니다.");
    return;
  }

  if (!confirm("정말 삭제하시겠습니까?")) return;

  try {
    const token = authStore.getToken();
    const response = await http.delete(`/board/${route.params.id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.status === 200) {
      alert("게시글이 삭제되었습니다.");
      router.push("/community");
    }
  } catch (error) {
    console.error("Error deleting board:", error);
    if (error.response?.status === 401) {
      alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
      authStore.logout();
    } else {
      alert("게시글 삭제에 실패했습니다.");
    }
  }
};

const handleEdit = () => {
  if (!authStore.user.isAuthenticated) {
    alert("로그인이 필요합니다.");
    router.push("/login");
    return;
  }

  if (!isAuthor.value) {
    alert("수정 권한이 없습니다.");
    return;
  }

  router.push(`/community/edit/${route.params.id}`);
};

const goBack = () => {
  router.push("/community");
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
    second: "2-digit",
  });
};

// 기존 ref 선언부 아래에 추가
const isHit = ref(false);
const hitCount = ref(0);

// 좋아요 상태 확인 함수
const checkHitStatus = async () => {
  if (!authStore.user.isAuthenticated) return;

  try {
    const response = await http.get(
      `/hit/status/${route.params.id}/${authStore.user.id}`
    );
    isHit.value = response.data;
  } catch (error) {
    console.error("Error checking hit status:", error);
  }
};

// 좋아요 수 조회 함수
const fetchHitCount = async () => {
  try {
    const response = await http.get(`/hit/count/${route.params.id}`);
    console.log("Hit count response:", response);
    hitCount.value = response.data;
  } catch (error) {
    console.error("Error fetching hit count:", error);
  }
};

const toggleHit = async () => {
  if (!authStore.user.isAuthenticated) {
    alert("로그인이 필요합니다.");
    router.push("/login");
    return;
  }

  try {
    const response = await http.post(`/hit/${route.params.id}/${authStore.user.id}`);

    // 응답 상태 확인 및 데이터 처리
    if (response.status === 200 && response.data) {
      isHit.value = !isHit.value; // 상태 토글
      hitCount.value = response.data.hitCount; // 새로운 좋아요 수 업데이트
      console.log("Hit toggled:", {
        isHit: isHit.value,
        hitCount: hitCount.value,
      });
    }
  } catch (error) {
    console.error("Error toggling hit:", error);
    alert("좋아요 처리 중 오류가 발생했습니다.");
  }
};

// 페이지 로드 시 초기 상태 확인
onMounted(async () => {
  await fetchBoardDetail();
  await Promise.all([
    fetchHitCount(),
    authStore.user.isAuthenticated ? checkHitStatus() : Promise.resolve(),
  ]);
});
</script>
