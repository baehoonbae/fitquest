<template>
  <div class="max-w-[900px] mx-auto px-4 flex flex-col gap-2">
    <div class="flex flex-col md:flex-row gap-6">
      <div class="md:w-1/2">
        <Calendar @dateSelected="handleDateSelected" />
      </div>
      <div class="md:w-1/2 min-h-[450px] max-h-[calc(100vh-25rem)] overflow-y-auto scrollbar-custom">
        <CategoryList :selectedDate="selectedDate" />
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
import { ref } from "vue";

const selectedDate=ref(null);

const handleDateSelected = (date) => {
  selectedDate.value = date;
};
</script>

<style>
.scrollbar-custom::-webkit-scrollbar {
  display: none;
}
</style>
