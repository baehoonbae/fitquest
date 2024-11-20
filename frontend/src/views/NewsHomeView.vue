<template>
  <div class="min-h-screen bg-gray-50 p-4">
    <!-- 그리드 컨테이너 -->
    <div
      class="columns-1 sm:columns-2 md:columns-3 lg:columns-4 xl:columns-5 gap-4 mx-auto max-w-7xl"
    >
      <!-- 카드 아이템 -->
      <div v-for="card in cards" :key="card.id" class="break-inside-avoid mb-4">
        <div
          class="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow duration-300 cursor-pointer overflow-hidden"
          @click="openCard(card)"
        >
          <!-- 이미지 -->
          <div class="relative group">
            <img
              :src="card.imageUrl"
              :alt="card.title"
              class="w-full h-auto object-cover"
            />
            <!-- 호버 오버레이 -->
            <div
              class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-20 transition-opacity duration-300"
            ></div>
          </div>

          <!-- 카드 콘텐츠 -->
          <div class="p-4">
            <h3 class="font-semibold text-gray-800 text-sm mb-1">{{ card.title }}</h3>
            <p class="text-gray-500 text-xs">{{ card.description }}</p>

            <!-- 하단 메타 정보 -->
            <div class="flex items-center justify-between mt-3">
              <div class="flex items-center space-x-2">
                <div class="w-6 h-6 rounded-full bg-gray-200"></div>
                <span class="text-xs text-gray-600">작성자</span>
              </div>
              <div class="flex items-center space-x-2">
                <button class="p-1.5 hover:bg-gray-100 rounded-full">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 text-gray-500"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"
                    />
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const getRandomSize = () => {
  // 300에서 500 사이의 랜덤한 숫자 생성
  return Math.floor(Math.random() * (500 - 300 + 1)) + 300;
};

const cards = ref([
  {
    id: 1,
    imageUrl: `https://picsum.photos/seed/1/${getRandomSize()}/${getRandomSize()}`,
    title: "상체 운동 루틴",
    description: "초보자를 위한 상체 운동 가이드",
  },
  {
    id: 2,
    imageUrl: `https://picsum.photos/seed/2/${getRandomSize()}/${getRandomSize()}`,
    title: "하체 운동 루틴",
    description: "하체 근력 강화를 위한 운동법",
  },
  {
    id: 3,
    imageUrl: `https://picsum.photos/seed/3/${getRandomSize()}/${getRandomSize()}`,
    title: "코어 운동 루틴",
    description: "효과적인 코어 운동 방법",
  },
  {
    id: 4,
    imageUrl: `https://picsum.photos/seed/4/${getRandomSize()}/${getRandomSize()}`,
    title: "스트레칭 가이드",
    description: "운동 전후 스트레칭 방법",
  },
  {
    id: 5,
    imageUrl: `https://picsum.photos/seed/5/${getRandomSize()}/${getRandomSize()}`,
    title: "유산소 운동",
    description: "체지방 감소를 위한 유산소 운동",
  },
  {
    id: 6,
    imageUrl: `https://picsum.photos/seed/6/${getRandomSize()}/${getRandomSize()}`,
    title: "식단 관리",
    description: "운동 효과를 높이는 식단 팁",
  },
  {
    id: 7,
    imageUrl: `https://picsum.photos/seed/7/${getRandomSize()}/${getRandomSize()}`,
    title: "홈트레이닝",
    description: "집에서 할 수 있는 운동 방법",
  },
  {
    id: 8,
    imageUrl: `https://picsum.photos/seed/8/${getRandomSize()}/${getRandomSize()}`,
    title: "근력 운동",
    description: "기초 근력 향상 프로그램",
  },
  {
    id: 9,
    imageUrl: `https://picsum.photos/seed/9/${getRandomSize()}/${getRandomSize()}`,
    title: "요가 루틴",
    description: "초보자를 위한 요가 동작",
  },
]);
const openCard = (card) => {
  console.log("카드 클릭:", card);
};
</script>

<style scoped>
/* Masonry 레이아웃을 위한 최소한의 CSS */
.break-inside-avoid {
  -webkit-column-break-inside: avoid;
  page-break-inside: avoid;
  break-inside: avoid;
}
</style>
