<template>
  <div class="flex justify-between items-center h-[60px] px-4">
    <button @click="goBack" class="flex items-center">
      <span class="material-icons mr-1">arrow_back</span>
    </button>
    <button>
      <span class="material-icons">menu</span>
    </button>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

const goBack = () => {
  router.go(-1);
};
</script>
