<template>
  <div class="h-full">
    <!-- 그리드 컨테이너 -->
    <div
      class="h-[calc(100vh-8rem)] overflow-y-auto rounded-[15px] z-0"
      style="position: relative"
      @scroll="handleScroll"
    >
      <div
        class="columns-1 sm:columns-2 md:columns-3 lg:columns-4 xl:columns-5 gap-4 mx-auto max-w-[1400px]"
      >
        <!-- 카드 아이템 -->
        <div v-for="item in newsItems" :key="item.link" class="break-inside-avoid mb-4">
          <div
            class="bg-gray-50 rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 cursor-pointer overflow-hidden flex flex-col"
            @click="openNews(item.link)"
          >
            <!-- 썸네일 이미지 -->
            <div v-if="item.thumbnail" class="w-full h-4/5 overflow-hidden">
              <img
                :src="item.thumbnail"
                @error="handleImageError($event, item)"
                class="w-full h-full object-cover"
                loading="lazy"
                alt=""
              />
            </div>
            <!-- 카드 콘텐츠 -->
            <div class="p-2 h-1/5">
              <h3
                class="font-semibold text-gray-800 text-sm truncate"
                v-html="item.title"
              ></h3>
              <!-- 하단 메타 정보 -->
              <div class="flex items-center justify-between mt-1">
                <span class="text-xs text-gray-600">{{ formatDate(item.postdate) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 로딩 인디케이터 -->
      <div v-if="isLoading" class="text-center py-4">
        <div
          class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-gray-300 border-t-blue-600"
        ></div>
      </div>

      <!-- 더 이상 데이터가 없을 때 메시지 -->
      <div v-if="!hasMore && !isLoading" class="text-center py-4 text-gray-500">
        더 이상 표시할 내용이 없습니다
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { searchBlog } from "@/api/news";

const searchQuery = ref("운동");
const newsItems = ref([]);
const currentPage = ref(1);
const isLoading = ref(false);
const hasMore = ref(true);

const INITIAL_LOAD_COUNT = 20; // 초기 로드 개수
const MORE_LOAD_COUNT = 10; // 추가 로드 개수

// 스크롤 이벤트 핸들러
const handleScroll = async (e) => {
  const element = e.target;
  // 스크롤이 바닥에 도달했는지 체크 (여유값 100px)
  if (
    element.scrollHeight - element.scrollTop <= element.clientHeight + 100 &&
    !isLoading.value &&
    hasMore.value
  ) {
    await loadMore();
  }
};

// 추가 데이터 로드
const loadMore = async () => {
  if (isLoading.value) return;

  try {
    isLoading.value = true;
    const nextPage = currentPage.value + 1;
    const start = (nextPage - 1) * MORE_LOAD_COUNT + 1; // 시작 위치 계산

    const blogResponse = await searchBlog(
      searchQuery.value,
      start,
      MORE_LOAD_COUNT // 10개만 요청
    );

    // 더 이상 데이터가 없으면 hasMore를 false로 설정
    if (!blogResponse.items || blogResponse.items.length === 0) {
      hasMore.value = false;
      return;
    }

    // 새 아이템 추가
    const newItems = blogResponse.items.map((item, index) => ({
      ...item,
      title: decodeHtmlEntities(item.title),
      postdate: item.postdate,
      thumbnail: getPicsumImage(newsItems.value.length + index),
    }));

    newsItems.value = [...newsItems.value, ...newItems];
    currentPage.value = nextPage;
  } catch (error) {
    console.error("추가 데이터 로드 실패:", error);
  } finally {
    isLoading.value = false;
  }
};

// 초기 검색 함수 수정
const handleSearch = async () => {
  try {
    isLoading.value = true;
    currentPage.value = 1;
    hasMore.value = true;

    const blogResponse = await searchBlog(
      searchQuery.value,
      1,
      INITIAL_LOAD_COUNT // 처음에는 20개 요청
    );

    newsItems.value = blogResponse.items.map((item, index) => ({
      ...item,
      title: decodeHtmlEntities(item.title),
      postdate: item.postdate,
      thumbnail: getPicsumImage(index),
    }));
  } catch (error) {
    console.error("검색 실패:", error);
  } finally {
    isLoading.value = false;
  }
};

const decodeHtmlEntities = (text) => {
  const textArea = document.createElement("textarea");
  textArea.innerHTML = text;
  return textArea.value;
};

const formatDate = (dateStr) => {
  if (!dateStr) return "";

  let date;
  // YYYYMMDD 형식 처리 (예: "20241110")
  if (/^\d{8}$/.test(dateStr)) {
    const year = dateStr.substring(0, 4);
    const month = dateStr.substring(4, 6);
    const day = dateStr.substring(6, 8);
    return `${year}-${month}-${day}`;
  }

  try {
    date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  } catch (e) {
    return "";
  }
};

const handleImageError = (event, item) => {
  const width = Math.floor(Math.random() * (500 - 300 + 1)) + 300;
  const height = Math.floor(Math.random() * (400 - 200 + 1)) + 200;
  item.thumbnail = `https://picsum.photos/${width}/${height}?random=${Math.random()}`;
};

const openNews = (link) => {
  window.open(link, "_blank");
};

onMounted(() => {
  handleSearch();
});

// 랜덤 크기의 Picsum 이미지 URL 생성 함수
const getPicsumImage = (index) => {
  // 너비는 300~500 사이의 랜덤값
  const width = Math.floor(Math.random() * (500 - 300 + 1)) + 300;
  // 높이는 200~400 사이의 랜덤값
  const height = Math.floor(Math.random() * (400 - 200 + 1)) + 200;

  return `https://picsum.photos/seed/${searchQuery.value}_${index}/${width}/${height}`;
};
</script>

<style scoped>
.break-inside-avoid {
  -webkit-column-break-inside: avoid;
  page-break-inside: avoid;
  break-inside: avoid;
}
</style>
