<template>
  <div class="max-w-4xl mx-auto px-5 my-1 md:my-2 pb-16">
    <!-- 게시글 헤더 -->
    <div class="mb-10 pb-5 border-b-2 border-gray-200">
      <div class="flex justify-between items-center mb-4">
        <span class="bg-blue-50 text-blue-600 px-3 py-1.5 rounded-full text-sm font-medium">
          #{{ board.tag }}
        </span>
        <div class="flex gap-2" v-if="isAuthor">
          <button
            class="px-4 py-2 rounded font-medium bg-gray-100 text-gray-600 hover:bg-gray-200 transition-all duration-200"
            @click="handleEdit">
            수정
          </button>
          <button
            class="px-4 py-2 rounded font-medium bg-red-500 text-white hover:bg-red-600 transition-all duration-200"
            @click="handleDelete">
            삭제
          </button>
        </div>
      </div>
      <h1 class="text-4xl md:text-2xl font-bold text-gray-800 mb-4 leading-tight">
        {{ board.title }}
      </h1>
      <div class="flex justify-between items-center text-gray-500 text-sm md:flex-col md:items-start md:gap-2">
        <div class="flex gap-3">
          <RouterLink :to="`/home/${board.userId}`" class="font-medium text-gray-700">{{ board.writer }}</RouterLink>
          <span>{{ formatDate(board.date) }}</span>
        </div>
        <!-- 조회수와 좋아요 부분 수정 -->
        <div class="flex items-center gap-4">
          <div class="flex items-center gap-1.5">
            <i class="fas fa-eye"></i>
            <span>{{ board.viewCount }}</span>
          </div>
          <div class="flex items-center gap-1.5 cursor-pointer" @click="toggleHit">
            <i :class="{
              'fas fa-heart text-xl transition-all duration-200': true,
              'text-red-500': isHit,
              'text-gray-300 hover:text-red-500': !isHit,
            }"></i>
            <span>{{ hitCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 게시글 내용 -->
    <div class="min-h-[300px] text-lg md:text-base text-gray-700 leading-relaxed mb-10">
      <div v-if="board.postImage !== null" class="mb-6">
        <img 
          :src="board.postImage" 
          alt="게시글 이미지" 
          class="max-w-full max-h-[600px] object-contain rounded-lg shadow-md" 
        />
      </div>
      <p>{{ board.content }}</p>
    </div>

    <!-- 댓글 섹션 -->
    <div class="mt-10 border-t-2 border-gray-200 pt-6">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-bold text-gray-800">댓글</h2>
        <button v-if="authStore.user.isAuthenticated" @click="$refs.commentForm.submitComment()"
          class="px-4 py-2 text-sm font-medium text-white bg-gray-900 rounded-md hover:bg-gray-800 transition-colors duration-200">
          등록
        </button>
      </div>

      <!-- 댓글 작성 폼 -->
      <CommentForm v-if="authStore.user.isAuthenticated" :boardId="Number(route.params.id)"
        @comment-added="refreshComments" class="mb-4" ref="commentForm" />
      <div v-else class="mb-4 p-4 bg-gray-50 rounded text-center">
        <p class="text-gray-600">댓글을 작성하려면 로그인이 필요합니다.</p>
      </div>

      <!-- 댓글 목록 -->
      <CommentList :boardId="Number(route.params.id)" ref="commentList" />
    </div>

    <!-- 하단 버튼 -->
    <div class="flex justify-center mt-10 pt-5 border-t border-gray-200">
      <button
        class="px-6 py-2.5 rounded font-medium bg-blue-600 text-white hover:bg-blue-700 transition-all duration-200"
        @click="goBack">
        목록으로
      </button>
    </div>
  </div>
  <NeedLoginAlert @close="needLoginAlert = false" v-if="needLoginAlert" />
  <!-- Alert 컴포넌트들 추가 -->
  <NeedLoginAlert @close="needLoginAlert = false" v-if="needLoginAlert" />
  <BoardDeleteSuccessAlert v-if="showDeleteSuccessAlert" @close="handleDeleteSuccess" />
  <BoardDeleteFailAlert v-if="showDeleteFailAlert" @close="showDeleteFailAlert = false" />
  <NoEditPermissionAlert v-if="showNoPermissionAlert" @close="showNoPermissionAlert = false" />
  <BoardDeleteConfirmAlert 
    v-if="showDeleteConfirmAlert" 
    @close="showDeleteConfirmAlert = false"
    @confirm="confirmDelete" 
  />
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import http from "@/api/http";
import CommentForm from "@/components/comment/CommentForm.vue";
import CommentList from "@/components/comment/CommentList.vue";
import { getChoseong } from "es-hangul";
import NeedLoginAlert from "@/components/alert/NeedLoginAlert.vue";
import BoardDeleteSuccessAlert from "@/components/alert/BoardDeleteSuccessAlert.vue";
import BoardDeleteFailAlert from "@/components/alert/BoardDeleteFailAlert.vue";
import NoEditPermissionAlert from "@/components/alert/NoEditPermissionAlert.vue";
import { useLoadingStore } from "@/stores/loading";
import BoardDeleteConfirmAlert from "@/components/alert/BoardDeleteConfirmAlert.vue";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const board = ref({});
const commentList = ref(null);
const needLoginAlert = ref(false);

// 기존 ref 선언부에 새로운 alert 상태 추가
const showDeleteSuccessAlert = ref(false);
const showDeleteFailAlert = ref(false);
const showNoPermissionAlert = ref(false);
const showDeleteConfirmAlert = ref(false);

// 이전 상태를 저장할 ref 추가
const previousState = ref({
  page: route.query.page || "1",
  tag: route.query.tag,
});

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
      choseong: {
        titleChoseong: getChoseong(boardData.title.replace(/\s+/g, "")),
        contentChoseong: getChoseong(boardData.content.replace(/\s+/g, "")),
        writerChoseong: getChoseong(boardData.writer.replace(/\s+/g, "")),
      },
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
  const loadingStore = useLoadingStore();
  try {
    loadingStore.show();
    const response = await http.get(`/board/${route.params.id}`);
    if (response.status === 200) {
      board.value = response.data;
      saveRecentPost(board.value);
      await incrementViewCount(board.value);

      const updatedResponse = await http.get(`/board/${route.params.id}`);
      if (updatedResponse.status === 200) {
        board.value = updatedResponse.data;
        console.log("업데이트된 게시글 데이터:", board.value);
      }
      if (board.value.postImage !== null) {
        board.value.postImage = `${http.defaults.baseURL}/board${board.value.postImage}`;
      }
    }
  } catch (error) {
    console.error("Error fetching board detail:", error);
    navigateToList();
  } finally {
    loadingStore.hide();
  }
};
// navigateToList 함수 확인
const navigateToList = () => {
  console.log("Navigating back with state:", previousState.value);

  const query = { page: previousState.value.page };
  if (previousState.value.tag) {
    query.tag = previousState.value.tag;
  }

  router.push({
    path: "/community",
    query,
  });
};

// handleDelete 함수 수정
const handleDelete = () => {
  if (!authStore.checkAuth()) {
    needLoginAlert.value = true;
    return;
  }
  showDeleteConfirmAlert.value = true;
};

// 새로운 함수 추가
const confirmDelete = async () => {
  showDeleteConfirmAlert.value = false;
  try {
    const token = authStore.getToken();
    const response = await http.delete(`/board/${route.params.id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.status === 200) {
      const recentPosts = JSON.parse(localStorage.getItem("recentPosts") || "[]");
      const filteredPosts = recentPosts.filter(
        (post) => post.id !== Number(route.params.id)
      );
      localStorage.setItem("recentPosts", JSON.stringify(filteredPosts));
      showDeleteSuccessAlert.value = true;
    }
  } catch (error) {
    console.error("Error deleting board:", error);
    if (error.response?.status === 401) {
      authStore.logout();
      needLoginAlert.value = true;
    } else {
      showDeleteFailAlert.value = true;
    }
  }
};

// handleDeleteSuccess 함수 수정
const handleDeleteSuccess = () => {
  showDeleteSuccessAlert.value = false;
  navigateToList(); // alert가 닫힌 후 목록으로 이동
};

// handleEdit 함수 수정
const handleEdit = () => {
  if (!authStore.user.isAuthenticated) {
    needLoginAlert.value = true;
    return;
  }

  if (!isAuthor.value) {
    showNoPermissionAlert.value = true;
    return;
  }

  router.push({
    path: `/community/edit/${route.params.id}`,
    query: {
      returnPage: previousState.value.page,
      returnTag: previousState.value.tag,
    },
  });
};

const goBack = navigateToList;

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

// 기존 ref 선언부 아래에 추
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
  if (!authStore.checkAuth()) {
    needLoginAlert.value = true;
    return;
  }

  try {
    const token = authStore.getToken();
    const response = await http.post(
      `/hit/${route.params.id}/${authStore.user.id}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (response.status === 200) {
      isHit.value = !isHit.value;
      hitCount.value = response.data.hitCount;
    }
  } catch (error) {
    console.error("Error toggling hit:", error);
  }
};

// 최근 본 게시물 저장 함수 추가
const saveRecentPost = (post) => {
  let recentPosts = JSON.parse(localStorage.getItem("recentPosts") || "[]");

  // 중복 제거
  recentPosts = recentPosts.filter((p) => p.id !== post.id);

  // 최신 게시물을 배열 앞에 추가
  recentPosts.unshift({
    id: post.id,
    title: post.title,
    createdAt: post.date,
    writer: post.writer,
    tag: post.tag,
  });

  // 최대 5개만 유지
  recentPosts = recentPosts.slice(0, 5);

  localStorage.setItem("recentPosts", JSON.stringify(recentPosts));
};

// route.params.id가 변경될 때마다 게시글 데이터를 다시 불러오는 watch 추가
watch(
  () => route.params.id,
  async (newId) => {
    if (newId) {
      // 이전 상태 업데이트
      previousState.value = {
        page: route.query.page || "1",
        tag: route.query.tag,
      };

      // 게시글 데이터 새로 불러오기
      await fetchBoardDetail();
      await Promise.all([
        fetchHitCount(),
        authStore.user.isAuthenticated ? checkHitStatus() : Promise.resolve(),
      ]);
    }
  }
);

// 마운트 시 이전 상태 저장 및 초기화
onMounted(async () => {
  // 이전 상태 저장
  previousState.value = {
    page: route.query.page || "1",
    tag: route.query.tag,
  };

  console.log("Initial state saved:", previousState.value);

  await fetchBoardDetail();
  await Promise.all([
    fetchHitCount(),
    authStore.user.isAuthenticated ? checkHitStatus() : Promise.resolve(),
  ]);
});
</script>
