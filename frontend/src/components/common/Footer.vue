<template>
  <div class="flex justify-center gap-20 items-center h-[60px]">
    <RouterLink to="/" class="text-gray-700 hover:text-blue-600 transition-colors duration-200">
      <span class="material-icons text-4xl">home</span>
    </RouterLink>
    <RouterLink to="/community" class="text-gray-700 hover:text-blue-600 transition-colors duration-200">
      <span class="material-icons text-4xl">groups</span>
    </RouterLink>
    <RouterLink to="/news" class="text-gray-700 hover:text-blue-600 transition-colors duration-200">
      <span class="material-icons text-4xl">newspaper</span>
    </RouterLink>
    <RouterLink to="/config" class="text-gray-700 hover:text-blue-600 transition-colors duration-200">
      <span class="material-icons text-4xl">person</span>
    </RouterLink>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router';
</script>