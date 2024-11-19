<template>
  <div class="community-container">
    <CommunityHeader />
    <CommunitySearch @search="handleSearch" />
    <div class="action-buttons">
      <button class="btn btn-view-all" @click="viewAllPosts">전체 보기</button>
      <button class="btn btn-write" @click="goToWrite">글 쓰기</button>
    </div>
    <CommunityTag
      :tags="tags"
      :selectedTag="selectedTag"
      @select-tag="handleTagSelect"
    />
    <CommunityBoard :boards="paginatedBoards" />
    <CommunityPagenation
      :currentPage="currentPage"
      :totalPages="totalPages"
      @page-change="handlePageChange"
    />
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import CommunityHeader from "@/components/common/CommunityHeader.vue";
import CommunitySearch from "@/components/CommunitySearch.vue";
import CommunityTag from "@/components/CommunityTag.vue";
import CommunityBoard from "@/components/CommunityBoard.vue";
import CommunityPagenation from "@/components/CommunityPagenation.vue";

// 반응형 상태 정의
const router = useRouter(); // 라우터 초기화
const boards = ref([]);
const tags = ref([]);
const selectedTag = ref(null);
const searchQuery = ref("");
const currentPage = ref(1);
const itemsPerPage = ref(10);

// computed 속성
const filteredBoards = computed(() => {
  let result = [...boards.value];

  // 검색어 필터링
  if (searchQuery.value) {
    result = result.filter((board) =>
      board.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }

  // 태그 필터링
  if (selectedTag.value) {
    result = result.filter((board) => board.tag === selectedTag.value);
  }

  return result;
});

const paginatedBoards = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return filteredBoards.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(filteredBoards.value.length / itemsPerPage.value);
});

// 메서드 정의
const handleSearch = (query) => {
  searchQuery.value = query;
  currentPage.value = 1;
};

const handleTagSelect = (tag) => {
  selectedTag.value = tag;
  currentPage.value = 1;
};

const handlePageChange = (page) => {
  currentPage.value = page;
};

// API 호출
const fetchBoards = async () => {
  try {
    const response = await axios.get(
      "http://localhost:8097/fitquest/api/board"
    );
    if (response.status === 200) {
      boards.value = response.data;
      // 유니크한 태그 목록 추출
      tags.value = [...new Set(response.data.map((board) => board.tag))];
    }
  } catch (error) {
    console.error("Error fetching boards:", error);
  }
};

// 전체 보기 메서드 추가
const viewAllPosts = () => {
  selectedTag.value = null;
  searchQuery.value = "";
  currentPage.value = 1;
};

// 글쓰기 페이지로 이동하는 메서드 추가
const goToWrite = () => {
  router.push("/community/write");
};

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchBoards();
});
</script>

<style scoped>
.community-container {
  max-width: 768px;
  margin: 0 auto;
  padding: 0 16px;
}

/* 버튼 스타일 추가 */
.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin: 20px 0;
}

.btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
}

.btn-view-all {
  background-color: #f1f3f5;
  color: #495057;
}

.btn-view-all:hover {
  background-color: #e9ecef;
}

.btn-write {
  background-color: #1976d2;
  color: white;
}

.btn-write:hover {
  background-color: #1565c0;
}
</style>
