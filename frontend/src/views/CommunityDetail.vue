<template>
  <div class="detail-container">
    <!-- 게시글 헤더 -->
    <div class="detail-header">
      <div class="header-top">
        <span class="tag">#{{ board.tag }}</span>
        <div class="actions" v-if="isAuthor">
          <button class="btn btn-edit" @click="handleEdit">수정</button>
          <button class="btn btn-delete" @click="handleDelete">삭제</button>
        </div>
      </div>
      <h1 class="title">{{ board.title }}</h1>
      <div class="post-info">
        <div class="author-info">
          <span class="author">{{ board.writer }}</span>
          <span class="date">{{ formatDate(board.date) }}</span>
        </div>
        <div class="view-count">
          <i class="fas fa-eye"></i>
          <span>{{ board.viewCount }}</span>
        </div>
      </div>
    </div>

    <!-- 게시글 내용 -->
    <div class="detail-content">
      <p>{{ board.content }}</p>
    </div>

    <!-- 하단 버튼 -->
    <div class="detail-footer">
      <button class="btn btn-back" @click="goBack">목록으로</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth"; // auth store import
import axios from "axios";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore(); // auth store 사용
const board = ref({});

// 작성자 여부 확인 (로그인한 사용자의 id와 게시글 작성자의 userId 비교)
const isAuthor = computed(() => {
  return (
    authStore.user.id && board.value && authStore.user.id === board.value.userId
  );
});

// 게시글 데이터 가져오기
const fetchBoardDetail = async () => {
  try {
    const response = await axios.get(
      `http://localhost:8097/fitquest/api/board/${route.params.id}`
    );
    if (response.status === 200) {
      board.value = response.data;
    }
  } catch (error) {
    console.error("Error fetching board detail:", error);
    router.push("/community");
  }
};

// 게시글 삭제
const handleDelete = async () => {
  if (!confirm("정말 삭제하시겠습니까?")) return;

  try {
    const token = authStore.getToken();
    const response = await axios.delete(
      `http://localhost:8097/fitquest/api/board/${route.params.id}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (response.status === 200) {
      alert("게시글이 삭제되었습니다.");
      router.push("/community");
    }
  } catch (error) {
    console.error("Error deleting board:", error);
    if (error.response?.status === 401) {
      alert("로그인이 필요합니다.");
      router.push("/login");
    } else {
      alert("게시글 삭제에 실패했습니다.");
    }
  }
};

// 게시글 수정 페이지로 이동
const handleEdit = () => {
  router.push(`/community/edit/${route.params.id}`);
};

// 목록으로 돌아가기
const goBack = () => {
  router.push("/community");
};

// 날짜 포맷팅
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

onMounted(() => {
  fetchBoardDetail();
});
</script>

<style scoped>
.detail-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
}

.detail-header {
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e9ecef;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.tag {
  background-color: #e3f2fd;
  color: #1976d2;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 0.9rem;
  font-weight: 500;
}

.actions {
  display: flex;
  gap: 8px;
}

.title {
  font-size: 2rem;
  font-weight: 700;
  color: #343a40;
  margin-bottom: 16px;
  line-height: 1.3;
}

.post-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #868e96;
  font-size: 0.95rem;
}

.author-info {
  display: flex;
  gap: 12px;
}

.author {
  font-weight: 500;
  color: #495057;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-content {
  min-height: 300px;
  line-height: 1.7;
  color: #495057;
  font-size: 1.1rem;
  margin-bottom: 40px;
}

.detail-footer {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.btn {
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-edit {
  background-color: #e9ecef;
  color: #495057;
}

.btn-edit:hover {
  background-color: #dee2e6;
}

.btn-delete {
  background-color: #ff6b6b;
  color: white;
}

.btn-delete:hover {
  background-color: #fa5252;
}

.btn-back {
  background-color: #1976d2;
  color: white;
  padding: 10px 24px;
}

.btn-back:hover {
  background-color: #1565c0;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .detail-container {
    margin: 20px auto;
  }

  .title {
    font-size: 1.5rem;
  }

  .post-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .detail-content {
    font-size: 1rem;
  }
}
</style>
