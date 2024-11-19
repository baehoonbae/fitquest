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
import { ref, computed, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useBoardStore } from "@/stores/board";
import CommunityHeader from "@/components/common/CommunityHeader.vue";
import CommunitySearch from "@/components/CommunitySearch.vue";
import CommunityTag from "@/components/CommunityTag.vue";
import CommunityBoard from "@/components/CommunityBoard.vue";
import CommunityPagenation from "@/components/CommunityPagenation.vue";

// 반응형 상태 정의
const router = useRouter();
const route = useRoute();
const boardStore = useBoardStore();
const tags = ref([]);
const selectedTag = ref(null);
const searchQuery = ref("");
const currentPage = ref(1);
const itemsPerPage = ref(10);

// 라우터 파라미터 감시
watch(
  () => route.query,
  (query) => {
    if (query.page) {
      currentPage.value = Number(query.page);
    }
  }
);

const filteredBoards = computed(() => {
  let result = [...boardStore.boards];

  // ID 기준으로 내림차순 정렬
  result.sort((a, b) => b.id - a.id);

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
  updateUrlQuery(1);
};

const handleTagSelect = (tag) => {
  selectedTag.value = tag;
  currentPage.value = 1;
  updateUrlQuery(1);
};

const handlePageChange = (page) => {
  currentPage.value = page;
  updateUrlQuery(page);
};

// URL 쿼리 업데이트 함수
const updateUrlQuery = (page) => {
  router.push({
    query: {
      ...route.query,
      page: page,
    },
  });
};

// API 호출
const fetchBoards = async () => {
  await boardStore.fetchBoards();
  // 유니크한 태그 목록 추출
  tags.value = [...new Set(boardStore.boards.map((board) => board.tag))];
  // 새로운 데이터를 불러올 때 첫 페이지로 이동
  currentPage.value = 1;
  updateUrlQuery(1);
};

// 전체 보기 메서드 추가
const viewAllPosts = () => {
  selectedTag.value = null;
  searchQuery.value = "";
  currentPage.value = 1;
  updateUrlQuery(1);
};

// 글쓰기 페이지로 이동하는 메서드 추가
const goToWrite = () => {
  router.push("/community/write");
};

// 컴포넌트 마운트 시 데이터 로드 및 페이지 파라미터 확인
onMounted(async () => {
  await fetchBoards();
  // URL에 페이지 파라미터가 있으면 해당 페이지로 이동
  if (route.query.page) {
    currentPage.value = Number(route.query.page);
  }
});

// 라우트 변경 감지 (페이지 새로 진입할 때마다 데이터 갱신)
watch(
  () => route.path,
  async (newPath, oldPath) => {
    if (newPath === "/community" && oldPath !== "/community") {
      await fetchBoards();
    }
  }
);
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
