<template>
  <div class="write-container">
    <h1 class="write-title">게시글 수정</h1>

    <form @submit.prevent="updatePost" class="write-form">
      <div class="form-group">
        <label for="tag">태그</label>
        <select id="tag" v-model="post.tag" required>
          <option value="" disabled>태그를 선택하세요</option>
          <option value="헬스">헬스</option>
          <option value="요가">요가</option>
          <option value="필라테스">필라테스</option>
          <option value="러닝">러닝</option>
          <option value="기타">기타</option>
        </select>
      </div>

      <div class="form-group">
        <label for="title">제목</label>
        <input
          type="text"
          id="title"
          v-model="post.title"
          required
          placeholder="제목을 입력하세요"
        />
      </div>

      <div class="form-group">
        <label for="content">내용</label>
        <textarea
          id="content"
          v-model="post.content"
          required
          placeholder="내용을 입력하세요"
          rows="10"
        ></textarea>
      </div>

      <div class="button-group">
        <button type="button" class="btn btn-cancel" @click="goBack">
          취소
        </button>
        <button type="submit" class="btn btn-submit">수정 완료</button>
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

// 기존 게시글 데이터 가져오기
const fetchPost = async () => {
  try {
    const response = await http.get(`/board/${route.params.id}`);
    if (response.status === 200) {
      const boardData = response.data;

      // 작성자 확인
      if (boardData.userId !== authStore.user.id) {
        alert("수정 권한이 없습니다.");
        router.push("/community");
        return;
      }

      // 데이터 설정
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
      await boardStore.fetchBoards(); // 게시글 목록 갱신
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

// 컴포넌트 마운트 시 로그인 체크 및 데이터 로드
onMounted(async () => {
  if (!authStore.user.isAuthenticated) {
    alert("로그인이 필요합니다.");
    router.push("/login");
    return;
  }
  await fetchPost();
});
</script>

<style scoped>
.write-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
}

.write-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #343a40;
  margin-bottom: 30px;
}

.write-form {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 8px;
  color: #495057;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 200px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
}

.btn {
  padding: 10px 20px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
}

.btn-cancel {
  background-color: #e9ecef;
  color: #495057;
}

.btn-cancel:hover {
  background-color: #dee2e6;
}

.btn-submit {
  background-color: #1976d2;
  color: white;
}

.btn-submit:hover {
  background-color: #1565c0;
}

@media (max-width: 768px) {
  .write-container {
    margin: 20px auto;
  }

  .write-form {
    padding: 20px;
  }
}
</style>
