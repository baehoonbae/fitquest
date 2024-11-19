<template>
  <header class="flex justify-between items-center pt-0 pb-3 border-b border-gray-100">
    <button 
      class="p-2 hover:bg-gray-100 rounded-md transition-colors duration-200"
      @click="goBack"
    >
      <i class="fas fa-arrow-left"></i>
    </button>
    <h1 class="text-2xl font-bold">커뮤니티</h1>
    <button 
      class="p-2 hover:bg-gray-100 rounded-md transition-colors duration-200"
      @click="toggleMenu"
    >
      <i class="fas fa-bars"></i>
    </button>
  </header>
</template>

<script setup>
const goBack = () => {
  window.history.back();
};

const toggleMenu = () => {
  // 메뉴 토글 로직
};
</script>