import { defineStore } from "pinia";
import { ref } from "vue";

export const useDateStore = defineStore("date", () => {
  const selectedDate = ref(new Date().toISOString().split("T")[0]);
  const setSelectedDate = (date) => {
    selectedDate.value = date;
  };

  return {
    selectedDate,
    setSelectedDate,
  };
});
