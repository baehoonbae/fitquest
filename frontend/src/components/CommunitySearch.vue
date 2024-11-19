<template>
  <div class="search-container">
    <div class="search-box">
      <input
        type="text"
        placeholder="글 검색"
        v-model="searchText"
        @keyup.enter="search"
      />
      <button class="search-icon" @click="search">
        <i class="fas fa-search"></i>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: "CommunitySearch",
  data() {
    return {
      searchText: "",
    };
  },
  methods: {
    search() {
      this.$emit("search", this.searchText);
    },
  },
};
</script>

<style scoped>
.search-container {
  padding: 16px 0;
}

.search-box {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 8px 12px;
}

input {
  flex: 1;
  border: none;
  outline: none;
  padding: 4px;
}

.search-icon {
  background: none;
  border: none;
  padding: 4px 8px;
  cursor: pointer;
}
</style>
