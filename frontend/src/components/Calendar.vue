<template>
  <!-- Left Calendar Section -->
  <div class="w-[23rem]">
    <!-- Date and Stats -->
    <div class="flex items-center justify-between mb-2">
      <div class="flex items-center gap-2">
        <span class="text-[0.95rem] font-bold pl-3">{{ currentYear }}년 {{ currentMonth }}월</span>
        <span class="text-[0.95rem] font-semibold text-gray-600">
          ✓ 0 😊 0 ❤️ 0
        </span>
      </div>
      <div class="flex gap-2">
        <button @click="previousMonth" class="text-gray-400">&lt;</button>
        <button @click="nextMonth" class="text-gray-400">&gt;</button>
      </div>
    </div>

    <!-- Calendar -->
    <div class="mb-7">
      <!-- Weekdays -->
      <div class="grid grid-cols-7 mb-2 font-semibold">
        <div
          v-for="{ day, color } in weekdays"
          :key="day"
          :class="[
            'text-center text-[0.72rem] w-11 h-7 flex items-center justify-center',
            color,
          ]"
        >
          {{ day }}
        </div>
      </div>
      <!-- Days -->
      <div class="grid grid-cols-7 gap-1">
        <!-- 빈 칸들 (월요일부터 시작) -->
        <template v-for="empty in firstDayOfMonth" :key="'empty-' + empty">
          <div class="aspect-square w-11 h-11"></div>
        </template>

        <!-- 1일부터 말일까지 -->
        <template v-for="day in daysInMonth" :key="day">
          <div
            class="aspect-square font-semibold w-11 h-11 flex items-center justify-center rounded-full text-[0.72rem] cursor-pointer"
            :class="{
              'hover:bg-gray-50': true,
              'bg-gray-200': isToday(day),
            }"
            @click="selectDate(day)"
          >
            {{ day }}
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

const authStore = useAuthStore();
const categoryStore = useCategoryStore();

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
  const firstDay = new Date(
    currentYear.value,
    currentMonth.value - 1,
    1
  ).getDay();
  return firstDay === 0 ? 6 : firstDay - 1; // 월요일을 시작으로 조정
});

// 해당 월의 총 일수 구하기
const daysInMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value, 0).getDate();
});

// 오늘 날짜 체크
const isToday = (day) => {
  const today = new Date();
  return (
    today.getDate() === day &&
    today.getMonth() === currentDate.value.getMonth() &&
    today.getFullYear() === currentDate.value.getFullYear()
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
  const selectedDate = new Date(currentYear.value, currentMonth.value - 1, day);
  console.log("Selected date:", selectedDate);
};
</script>
