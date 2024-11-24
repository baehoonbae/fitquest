<template>
  <div class="max-w-4xl mx-auto px-4 pb-16">
    <CommunitySearch v-model="searchText" @search="handleSearch" />
    <CommunityTag :tags="tags" :selectedTag="selectedTag" @select-tag="handleTagSelect" />
    <div class="flex justify-end gap-3 my-2">
      <button
        class="px-3 py-1.5 rounded-md font-medium text-sm bg-gray-100 text-gray-700 hover:bg-gray-200 transition-all duration-200"
        @click="viewAllPosts">
        전체 보기
      </button>
      <button
        class="px-3 py-1.5 rounded-md font-medium text-sm bg-black text-white hover:bg-gray-700 transition-all duration-200"
        @click="goToWrite">
        글 쓰기
      </button>
    </div>
    <CommunityBoard :boards="paginatedBoards" />
    <CommunityPagenation :currentPage="currentPage" :totalPages="totalPages" @page-change="handlePageChange" />
  </div>
  <NeedLoginAlert @close="needLoginAlert = false" v-if="needLoginAlert" />
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useBoardStore } from "@/stores/board";
import { COMMUNITY_TAGS } from "@/stores/tags";
import CommunitySearch from "@/components/CommunitySearch.vue";
import CommunityTag from "@/components/CommunityTag.vue";
import CommunityBoard from "@/components/CommunityBoard.vue";
import CommunityPagenation from "@/components/CommunityPagenation.vue";
import NeedLoginAlert from "@/components/alert/NeedLoginAlert.vue";
import { useAuthStore } from "@/stores/auth";

// 반응형 상태 정의
const router = useRouter();
const route = useRoute();
const boardStore = useBoardStore();
const authStore = useAuthStore();
const tags = ref([]);
const selectedTag = ref(null);
const searchText = ref("");
const currentPage = ref(1);
const itemsPerPage = ref(10);
const needLoginAlert = ref(false);

// URL 쿼리 감시 로직 수정
watch(
  () => route.query,
  (query) => {
    if (query.page) {
      currentPage.value = Number(query.page);
    }
    if (query.tag) {
      selectedTag.value = query.tag;
    }
  },
  { immediate: true, deep: true }
);

const filteredBoards = computed(() => {
  let result = [...boardStore.boards];

  // 공지사항과 일반 게시글 분리
  const notices = result.filter((board) => board.tag === "공지");
  const regularPosts = result.filter((board) => board.tag !== "공지");

  // 각각 ID 기준으로 내림차순 정렬
  notices.sort((a, b) => b.id - a.id);
  regularPosts.sort((a, b) => b.id - a.id);

  // 공지사항을 항상 위에 배치
  result = [...notices, ...regularPosts];

  // 태그 필터링 (공지사항은 제외)
  if (selectedTag.value && selectedTag.value !== "공지") {
    result = [
      ...notices,
      ...regularPosts.filter((board) => board.tag === selectedTag.value),
    ];
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

// 검색 처리 함수 수정
const handleSearch = async (searchCondition) => {
  try {
    await boardStore.searchBoards(searchCondition);
    // 검색 후에는 첫 페이지로 이동
    currentPage.value = 1;
    updateUrlQuery(1);
  } catch (error) {
    console.error("검색 중 오류 발생:", error);
  }
};

// 태그 선택 핸들러 수정
const handleTagSelect = (tag) => {
  selectedTag.value = tag;
  currentPage.value = 1;
  const query = { page: 1 };
  if (tag) {
    query.tag = tag;
  }
  router.push({ query }).catch(() => { });
};

const handlePageChange = (page) => {
  currentPage.value = page;
  updateUrlQuery(page);
};

// URL 쿼리 업데이트 함수 수정
const updateUrlQuery = (page) => {
  const query = { page };
  if (selectedTag.value) {
    query.tag = selectedTag.value;
  }
  router
    .push({
      query,
    })
    .catch(() => { });
};

// URL 쿼리 감시 로직 강화
watch(
  () => route.query,
  (query) => {
    if (query.page) {
      currentPage.value = Number(query.page);
    }
  },
  { immediate: true, deep: true }
);

// API 호출 함수 수정
const fetchBoards = async (preservePage = true) => {
  try {
    await boardStore.fetchBoards();
    // 정적 태그와 게시글의 태그를 합치고 중복 제거
    const dynamicTags = [
      ...new Set(boardStore.boards.map((board) => board.tag)),
    ];
    tags.value = [...new Set([...COMMUNITY_TAGS, ...dynamicTags])];

    // preservePage가 false일 때만 첫 페이지로 이동
    if (!preservePage) {
      currentPage.value = 1;
      updateUrlQuery(1);
    } else {
      const pageFromQuery = Number(route.query.page) || 1;
      currentPage.value = pageFromQuery;

      // 현재 페이지 번호가 전체 페이지 수보다 크다면 마지막 페이지로 이동
      const maxPage = Math.ceil(boardStore.boards.length / itemsPerPage.value);
      if (currentPage.value > maxPage) {
        currentPage.value = maxPage || 1;
        updateUrlQuery(currentPage.value);
      }
    }
  } catch (error) {
    console.error("Error fetching boards:", error);
  }
};

// 전체 보기 메서드 수정
const viewAllPosts = () => {
  selectedTag.value = null;
  searchText.value = "";
  router.push({ query: { page: 1 } }).catch(() => { });
  fetchBoards(false);
};

// 글쓰기 페이지로 이동하는 메서드
const goToWrite = async () => {
  if (!(await authStore.checkAuth())) {
    needLoginAlert.value = true;
    return;
  }
  const currentPage = route.query.page || 1;
  router.push({
    path: "/community/write",
    query: { returnPage: currentPage }, // 현재 페이지 정보 저장
  });
};

// 컴포넌트 마운트 시 실행
onMounted(async () => {
  const pageFromQuery = Number(route.query.page) || 1;
  const tagFromQuery = route.query.tag;
  currentPage.value = pageFromQuery;
  if (tagFromQuery) {
    selectedTag.value = tagFromQuery;
  }
  await fetchBoards(true);
});

// 라우트 변경 감시 로직 수정
watch(
  () => route.path,
  async (newPath, oldPath) => {
    if (newPath === "/community" && oldPath !== "/community") {
      const pageFromQuery = Number(route.query.page) || currentPage.value;
      const tagFromQuery = route.query.tag;
      if (tagFromQuery) {
        selectedTag.value = tagFromQuery;
      }
      await fetchBoards(true);
      currentPage.value = pageFromQuery;
    }
  }
);
</script>
