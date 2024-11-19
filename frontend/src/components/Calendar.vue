<template>
  <!-- Left Calendar Section -->
  <div class="w-full md:w-[24rem]">
    <!-- Date and Stats -->
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center gap-3">
        <span class="text-[1.1rem] font-bold pl-3">
          {{ currentYear }}년 {{ currentMonth }}월
        </span>
      </div>
      <div class="flex gap-3">
        <button @click="previousMonth" class="text-gray-400 text-xl">&lt;</button>
        <button @click="nextMonth" class="text-gray-400 text-xl">&gt;</button>
      </div>
    </div>

    <!-- Calendar -->
    <div class="mb-8">
      <!-- Weekdays -->
      <div class="grid grid-cols-7 mb-3 font-semibold">
        <div
          v-for="{ day, color } in weekdays"
          :key="day"
          :class="[
            'text-center text-[0.85rem] w-full sm:w-12 h-8 flex items-center justify-center',
            color,
          ]"
        >
          {{ day }}
        </div>
      </div>
      <!-- Days -->
      <div class="grid grid-cols-7 gap-2">
        <!-- 빈 칸들 (월요일부터 시작) -->
        <template v-for="empty in firstDayOfMonth" :key="'empty-' + empty">
          <div class="aspect-square w-full sm:w-12 h-auto sm:h-12"></div>
        </template>

        <!-- 1일부터 말일까지 -->
        <template v-for="day in daysInMonth" :key="day">
          <div
            class="aspect-square font-semibold w-full sm:w-12 h-auto sm:h-[3.3rem] flex flex-col items-center justify-center rounded-lg text-[0.85rem] cursor-pointer relative"
            @click="selectDate(day)"
          >
            <div
              class="w-6 h-6 text-center text-gray-600 border border-gray-200 bg-gray-200 rounded-[0.3rem] mb-0.5"
            >
              {{ undoneTodoCount }}
            </div>
            <div
              class="text-center rounded-full px-2 py-1"
              :class="[{ 'bg-black text-white': isSelectedDate(day) }]"
            >
              {{ day }}
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useCategoryStore } from "@/stores/category";
import { useDateStore } from "@/stores/date";
import { useTodoStore } from "@/stores/todo";

const authStore = useAuthStore();
const categoryStore = useCategoryStore();
const dateStore = useDateStore();
const todoStore = useTodoStore();

onMounted(async () => {
  await authStore.checkAuth();
  await categoryStore.fetchCategories();
});

// 현재 날짜 상태 관리
const currentDate = ref(new Date());
const currentYear = computed(() => currentDate.value.getFullYear());
const currentMonth = computed(() => currentDate.value.getMonth() + 1);
const weekdays = [
  { day: "월", color: "" },
  { day: "화", color: "" },
  { day: "수", color: "" },
  { day: "목", color: "" },
  { day: "금", color: "" },
  { day: "토", color: "text-blue-500" },
  { day: "일", color: "text-red-500" },
];

// 해당 월의 첫 번째 날의 요일 구하기 (0: 일요일, 1: 월요일, ...)
const firstDayOfMonth = computed(() => {
  const firstDay = new Date(currentYear.value, currentMonth.value - 1, 1).getDay();
  return firstDay === 0 ? 6 : firstDay - 1; // 월요일을 시작으로 조정
});

// 해당 월의 총 일수 구하기
const daysInMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value, 0).getDate();
});

// 오늘 날짜 체크
const isSelectedDate = (day) => {
  return (
    dateStore.selectedDate ===
    new Date(currentYear.value, currentMonth.value - 1, day + 1)
      .toISOString()
      .split("T")[0]
  );
};

// 이전 달로 이동
const previousMonth = () => {
  currentDate.value = new Date(currentYear.value, currentMonth.value - 2, 1);
};

// 다음 달로 이동
const nextMonth = () => {
  currentDate.value = new Date(currentYear.value, currentMonth.value, 1);
};

// 날짜 선택
const selectDate = (day) => {
  const selectedDate = new Date(currentYear.value, currentMonth.value - 1, day + 1);
  const formattedDate = selectedDate.toISOString().split("T")[0];
  dateStore.setSelectedDate(formattedDate);
};

onMounted(async () => {
  await todoStore.fetchCountUndoneTodo(
    currentYear.value,
    currentMonth.value,
    daysInMonth.value
  );
});
</script>
