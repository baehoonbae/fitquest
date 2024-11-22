<template>
  <div class="py-4 relative search-container">
    <div class="flex items-center gap-2 mb-5">
      <!-- 검색 필터 선택 -->
      <select 
        v-model="searchType" 
        class="border border-gray-200 rounded-md px-2 py-1"
      >
        <option value="all">전체</option>
        <option value="title">제목</option>
        <option value="writer">작성자</option>
        <option value="content">내용</option>
      </select>

      <!-- 검색창 -->
      <div class="flex items-center border border-gray-200 rounded-md px-3 flex-1">
        <input
          type="text"
          :placeholder="getPlaceholder"
          v-model="searchText"
          @input="handleInput"
          @keyup.enter="search"
          class="flex-1 border-none outline-none px-1 text-[20px]"
        />
        <button
          class="p-1 px-2 cursor-pointer hover:text-gray-600 transition-colors duration-200"
          @click="search"
        >
          <i class="fas fa-search"></i>
        </button>
      </div>
    </div>

    <!-- 연관검색어 부분은 동일하게 유지 -->
    <div
      v-if="enableRelatedSearches && showRelatedSearches && relatedSearches.length > 0"
      class="absolute w-full bg-white border border-gray-200 rounded-md mt-1 shadow-lg z-10"
    >
      <div
        v-for="(search, index) in relatedSearches"
        :key="index"
        class="px-4 py-2 hover:bg-gray-100 cursor-pointer"
        @click="selectRelatedSearch(search)"
      >
        {{ search }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, computed } from "vue";
import { useAuthStore } from "@/stores/auth";
import http from "@/api/http";

const emit = defineEmits(["search"]);
const searchType = ref("all");
const searchText = ref("");
const relatedSearches = ref([]);
const showRelatedSearches = ref(false);
const authStore = useAuthStore();
const lastInputValue = ref("");

let debounceTimer;
let clickOutsideHandler;

const props = defineProps({
  enableRelatedSearches: {
    type: Boolean,
    default: true,
  },
});

const getPlaceholder = computed(() => {
  switch (searchType.value) {
    case "title": return "제목으로 검색";
    case "writer": return "작성자로 검색";
    case "content": return "내용으로 검색";
    default: return "전체 검색";
  }
});

const debounceFetchSearchHistory = () => {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => {
    if (lastInputValue.value !== searchText.value && props.enableRelatedSearches) {
      lastInputValue.value = searchText.value;
      fetchSearchHistory();
    }
  }, 100);
};

const fetchSearchHistory = async () => {
  const query = searchText.value.trim();
  if (!query) {
    relatedSearches.value = [];
    showRelatedSearches.value = false;
    return;
  }
  try {
    const response = await http.get(`/board/history/${authStore.user.id}/${query}`);
    relatedSearches.value = response.data.filter((item) =>
      item.toLowerCase().includes(query.toLowerCase())
    );
    showRelatedSearches.value = relatedSearches.value.length > 0;
  } catch (error) {
    console.error("연관 검색어 조회 실패:", error);
    relatedSearches.value = [];
    showRelatedSearches.value = false;
  }
};

const handleInput = (e) => {
  searchText.value = e.target.value;
  debounceFetchSearchHistory();
};

const search = async () => {
  if (!searchText.value.trim()) return;

  try {
    await http.post(`/board/history`, {
      userId: authStore.user.id,
      content: searchText.value.trim(),
    });
  } catch (error) {
    console.error("검색 기록 저장 실패:", error);
  }

  emit("search", {
    key: searchType.value,
    word: searchText.value.trim()
  });
  showRelatedSearches.value = false;
};

const selectRelatedSearch = (search) => {
  searchText.value = search;
  search();
};

onMounted(() => {
  clickOutsideHandler = (e) => {
    if (!e.target.closest(".search-container")) {
      showRelatedSearches.value = false;
    }
  };
  document.addEventListener("click", clickOutsideHandler);
});

onUnmounted(() => {
  clearTimeout(debounceTimer);
  document.removeEventListener("click", clickOutsideHandler);
});
</script>
