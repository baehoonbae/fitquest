<template>
  <div class="max-w-[900px] mx-auto px-2 flex flex-col gap-2">
    <div class="flex flex-col md:flex-row gap-6">
      <div
        class="md:w-1/2 border border-[#dddfe0] p-4 max-h-[calc(100vh-25rem)] rounded-[30px]"
      >
        <Calendar @dateSelected="handleDateSelected" />
      </div>
      <div
        class="md:w-1/2 border border-[#dddfe0] min-h-[calc(100vh-25rem)] max-h-[calc(100vh-25rem)] overflow-y-auto p-4 rounded-[30px]"
      >
        <CategoryList :selectedDate="selectedDate" />
      </div>
    </div>
    <div class="bg-[#f7f8f9] p-4 rounded-[30px]">
      <GrassGraph />
    </div>
  </div>
</template>

<script setup>
import Calendar from "@/components/Calendar.vue";
import CategoryList from "@/components/CategoryList.vue";
import GrassGraph from "@/components/GrassGraph.vue";
import { ref } from "vue";

const selectedDate = ref(null);

const handleDateSelected = (date) => {
  selectedDate.value = date;
};
</script>
