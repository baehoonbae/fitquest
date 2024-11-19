import { defineStore } from "pinia";
import { ref } from "vue";

export const useDateStore = defineStore("date", () => {
  const selectedDate = ref(new Date().toISOString().split("T")[0]);
  const currentYear = ref(new Date().getFullYear());
  const currentMonth = ref(new Date().getMonth() + 1);

  const setSelectedDate = (date) => {
    selectedDate.value = date;
    const newDate = new Date(date);
    currentYear.value = newDate.getFullYear();
    currentMonth.value = newDate.getMonth() + 1;
  };

  return {
    selectedDate,
    currentYear,
    currentMonth,
    setSelectedDate,
  };
});
