<template>
  <div>
    <div class="flex justify-between items-center px-8">
      <div class="flex items-center gap-8">
        <h3 class="text-lg font-semibold">잔디</h3>
        <select
          v-model="thisYear"
          class="appearance-none px-4 py-2 border border-gray-300 rounded-lg text-sm bg-white hover:border-gray-400 focus:outline-none focus:border-black focus:ring-2 focus:ring-gray-400 transition-colors duration-200 cursor-pointer bg-[url('data:image/svg+xml;charset=utf-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20viewBox%3D%220%200%2020%2020%22%3E%3Cpath%20d%3D%22M5.293%207.293a1%201%200%20011.414%200L10%2010.586l3.293-3.293a1%201%200%20111.414%201.414l-4%204a1%201%200%2001-1.414%200l-4-4a1%201%200%20010-1.414z%22%20fill%3D%22%236b7280%22%2F%3E%3C%2Fsvg%3E')] bg-[length:1.5em_1.5em] bg-[right_0.5rem_center] bg-no-repeat pr-10"
        >
          <option v-for="year in availableYears" :key="year" :value="year" class="py-1">
            {{ year }}년
          </option>
        </select>
      </div>
      <div class="hidden md:flex items-center gap-5 justify-center">
        <template v-for="(level, index) in activityLevels" :key="index">
          <div class="flex gap-1 items-center">
            <span class="text-xs text-gray-400">{{ level.label }}</span>
            <div class="w-4 h-4 rounded-sm" :class="level.color"></div>
          </div>
        </template>
      </div>
      <div class="flex md:hidden items-center gap-2 justify-center">
        <template v-for="(level, index) in activityLevels" :key="index">
          <div class="flex gap-0.5 items-center">
            <span class="text-[10px] text-gray-400">{{ level.label }}</span>
            <div class="w-3 h-3 rounded-sm" :class="level.color"></div>
          </div>
        </template>
      </div>
    </div>
    <div class="grass-graph overflow-x-auto justify-center">
      <div class="w-[900px] px-10 pt-10">
        <div class="flex flex-col gap-1">
          <div v-for="row in 7" :key="row" class="flex gap-1">
            <template v-for="col in 53" :key="col">
              <div
                v-if="isCurrentYear(getDate(row - 1, col - 1))"
                class="w-3.5 h-3.5 rounded-sm relative group cursor-pointer hover:ring-2 hover:ring-gray-400"
                :class="getActivityColor(getActivityLevel(row - 1, col - 1))"
                @click="handleDateClick(getDate(row - 1, col - 1))"
              >
                <!-- 툴팁 -->
                <div
                  class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 text-xs text-white bg-gray-800 rounded-md whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none z-50"
                >
                  {{ formatDate(getDate(row - 1, col - 1)) }}
                  <div
                    class="absolute bottom-0 left-1/2 transform -translate-x-1/2 translate-y-full border-4 border-transparent border-t-gray-800"
                  ></div>
                </div>
              </div>
              <div v-else class="w-3.5 h-3.5"></div>
            </template>
          </div>
        </div>
        <div class="grid grid-cols-12 justify-between mt-2 w-full text-sm text-gray-400">
          <span class="w-10" v-for="month in 12" :key="month">{{ month }}월</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed, ref } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useActivityStore } from "@/stores/activity";
import { useDateStore } from "@/stores/date";

const authStore = useAuthStore();
const activityStore = useActivityStore();
const dateStore = useDateStore();
const thisYear = ref(new Date().getFullYear());

const activityLevels = [
  { label: "0%", color: "bg-[#dddfe0]" },
  { label: "1~25%", color: "bg-[#c6e48b]" },
  { label: "26~50%", color: "bg-[#7bc96f]" },
  { label: "51~75%", color: "bg-[#239a3b]" },
  { label: "76~100%", color: "bg-[#196127]" },
];

const availableYears = computed(() => {
  const currentYear = new Date().getFullYear();
  const years = [];
  for (let year = 2018; year <= currentYear; year++) {
    years.push(year);
  }
  return years;
});

const fetchActivities = async () => {
  const userId = authStore.user.id;
  await activityStore.fetchActivities(userId, thisYear.value);
};

// 1년치 날짜 배열 생성
const yearDates = computed(() => {
  const startDate = new Date(thisYear.value, 0, 1);
  const endDate = new Date(thisYear.value, 11, 31);
  const dates = [];

  let currentDate = new Date(startDate);
  while (currentDate <= endDate) {
    dates.push(currentDate.toISOString().split("T")[0]);
    currentDate.setDate(currentDate.getDate() + 1);
  }

  // 53주 x 7일 = 371일로 확장하여 모든 날짜 표시 가능
  const totalDays = 53 * 7;
  while (dates.length < totalDays) {
    const nextDate = new Date(dates[dates.length - 1]);
    nextDate.setDate(nextDate.getDate() + 1);
    dates.push(nextDate.toISOString().split("T")[0]);
  }

  return dates;
});

const getActivityLevel = (row, col) => {
  const index = col * 7 + row;
  if (index >= yearDates.value.length) return 0;

  const dateString = yearDates.value[index];
  if (!isCurrentYear(dateString)) return 0;

  const ratio = activityStore.activities[dateString] || 0;

  if (ratio === 0) return 0;
  if (ratio <= 0.25) return 1;
  if (ratio <= 0.5) return 2;
  if (ratio <= 0.75) return 3;
  return 4;
};

const getActivityColor = (level) => {
  return activityLevels[level].color;
};

const isCurrentYear = (dateString) => {
  if (!dateString) return false;
  return new Date(dateString).getFullYear() === thisYear.value;
};

// 날짜 포맷팅 함수 추가
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const dayOfWeek = ["일", "월", "화", "수", "목", "금", "토"][date.getDay()];

  return `${month}월 ${day}일 (${dayOfWeek})`;
};

// 날짜 가져오기 함수 추가
const getDate = (row, col) => {
  const index = col * 7 + row;
  return index < yearDates.value.length ? yearDates.value[index] : null;
};

// 날짜 클릭 핸들러 추가
const handleDateClick = (dateString) => {
  if (dateString && isCurrentYear(dateString)) {
    dateStore.setSelectedDate(dateString);
  }
};

onMounted(() => {
  fetchActivities();
});
</script>
<style scoped>
/* 툴팁이 잘리지 않도록 부모 요소에 overflow 처리 */
.grass-graph {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.grass-graph::-webkit-scrollbar {
  display: none;
}
</style>
