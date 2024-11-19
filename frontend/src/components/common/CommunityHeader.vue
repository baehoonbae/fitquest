<template>
  <header class="header">
    <button class="back-button" @click="goBack">
      <i class="fas fa-arrow-left"></i>
    </button>
    <h1 class="title">커뮤니티</h1>
    <button class="menu-button" @click="toggleMenu">
      <i class="fas fa-bars"></i>
    </button>
  </header>
</template>

<script>
export default {
  name: "CommunityHeader",
  methods: {
    goBack() {
      window.history.back();
    },
    toggleMenu() {
      // 메뉴 토글 로직
    },
  },
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #eee;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.back-button,
.menu-button {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
}
</style>
