<template>
  <div class="max-w-4xl mx-auto px-4 pb-16">
    <CommunitySearch @search="handleSearch" />
    <CommunityTag :tags="tags" :selectedTag="selectedTag" @select-tag="handleTagSelect" />
    <div class="flex justify-end gap-3 my-2">
      <button
        class="px-3 py-1.5 rounded-md font-medium text-sm bg-gray-100 text-gray-700 hover:bg-gray-200 transition-all duration-200"
        @click="viewAllPosts"
      >
        전체 보기
      </button>
      <button
        class="px-3 py-1.5 rounded-md font-medium text-sm bg-blue-600 text-white hover:bg-blue-700 transition-all duration-200"
        @click="goToWrite"
      >
        글 쓰기
      </button>
    </div>
    <CommunityBoard :boards="paginatedBoards" />
    <CommunityPagenation
      :currentPage="currentPage"
      :totalPages="totalPages"
      @page-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useBoardStore } from "@/stores/board";
import CommunitySearch from "@/components/CommunitySearch.vue";
import CommunityTag from "@/components/CommunityTag.vue";
import CommunityBoard from "@/components/CommunityBoard.vue";
import CommunityPagenation from "@/components/CommunityPagenation.vue";
import { COMMUNITY_TAGS } from "@/stores/tags";

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
      board.title
        .replace(/\s+/g, "")
        .toLowerCase()
        .includes(searchQuery.value.toLowerCase())
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
  searchQuery.value = query.replace(/\s+/g, "");
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
  // 정적 태그와 게시글의 태그를 합치고 중복 제거
  const dynamicTags = [...new Set(boardStore.boards.map((board) => board.tag))];
  tags.value = [...new Set([...COMMUNITY_TAGS, ...dynamicTags])];
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
