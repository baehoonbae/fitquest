<template>
  <div class="pagination">
    <button
      class="page-button"
      :disabled="currentPage === 1"
      @click="changePage(currentPage - 1)"
    >
      이전
    </button>
    <span class="page-numbers"> {{ currentPage }} / {{ totalPages }} </span>
    <button
      class="page-button"
      :disabled="currentPage === totalPages"
      @click="changePage(currentPage + 1)"
    >
      다음
    </button>
  </div>
</template>

<script>
export default {
  name: "CommunityPagination",
  props: {
    currentPage: {
      type: Number,
      required: true,
    },
    totalPages: {
      type: Number,
      required: true,
    },
  },
  methods: {
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.$emit("page-change", page);
      }
    },
  },
};
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
  gap: 16px;
}

.page-button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
}

.page-button:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.page-numbers {
  font-size: 14px;
}
</style>
