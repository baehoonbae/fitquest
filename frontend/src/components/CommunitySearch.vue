<template>
  <div class="py-4">
    <div class="flex items-center border border-gray-200 rounded-md px-3 py-2">
      <input
        type="text"
        placeholder="글 검색"
        v-model="searchText"
        @keyup.enter="search"
        class="flex-1 border-none outline-none px-1"
      />
      <button
        class="p-1 px-2 cursor-pointer hover:text-gray-600 transition-colors duration-200"
        @click="search"
      >
        <i class="fas fa-search"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const emit = defineEmits(["search"]);
const searchText = ref("");

const search = () => {
  // 검색어의 모든 공백 제거
  const processedSearchText = searchText.value.replace(/\s+/g, "");
  emit("search", processedSearchText);
};
</script>
