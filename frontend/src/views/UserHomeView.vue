<template>
  <div>
    <div class="flex flex-col md:flex-row justify-around">
      <!-- 달력 -->
      <Calendar />
      <CategoryList />
    </div>
    <div class="flex justify-center">
      <!-- 로그인된 유저의 잔디 그래프 -->
      <GrassGraph />
    </div>
  </div>
</template>

<script setup>
import Calendar from "@/components/Calendar.vue";
import CategoryList from "@/components/CategoryList.vue";
import GrassGraph from "@/components/GrassGraph.vue";
</script>
