<template>
  <div class="max-w-[900px] mx-auto px-4 flex flex-col gap-6">
    <div class="flex flex-col md:flex-row gap-6">
      <div class="md:w-1/2">
        <Calendar />
      </div>
      <div class="md:w-1/2 min-h-[300px] max-h-[calc(100vh-25rem)] overflow-y-auto scrollbar-custom">
        <CategoryList />
      </div>
    </div>
    <div>
      <GrassGraph />
    </div>
  </div>
</template>

<script setup>
import Calendar from "@/components/Calendar.vue";
import CategoryList from "@/components/CategoryList.vue";
import GrassGraph from "@/components/GrassGraph.vue";
</script>

<style>
.scrollbar-custom::-webkit-scrollbar {
  display: none;
}
</style>
