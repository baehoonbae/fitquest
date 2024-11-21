<template>
  <div class="h-[calc(100vh-4rem)]">
    <div 
      ref="scrollContainer"
      class="h-full overflow-y-auto rounded-[15px]" 
      @scroll="handleScroll"
    >
      <masonry-wall 
        :items="newsItems" 
        :column-width="300" 
        :gap="16" 
        class="px-4"
        @layout-complete="handleLayoutComplete"
      >
        <template #default="{ item }">
          <div
            class="bg-gray-50 rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 cursor-pointer overflow-hidden flex flex-col"
            @click="openNews(item.link)"
          >
            <div
              v-if="item.thumbnail"
              class="w-full overflow-hidden"
              :style="{
                aspectRatio: `${item.imageWidth}/${item.imageHeight}`,
              }"
            >
              <img
                :src="item.thumbnail"
                @error="handleImageError($event, item)"
                class="w-full h-full object-cover"
                loading="lazy"
                :alt="item.title"
                decoding="async"
                :width="item.imageWidth"
                :height="item.imageHeight"
              />
            </div>
            <div class="p-2 h-2/5">
              <h3 class="font-semibold text-gray-800 text-sm truncate" v-html="item.title"></h3>
              <div class="flex items-center justify-between mt-1">
                <span class="text-xs text-gray-600">{{ formatDate(item.postdate) }}</span>
              </div>
            </div>
          </div>
        </template>
      </masonry-wall>

      <!-- 로딩 인디케이터 -->
      <div v-if="isLoading" class="text-center py-4">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-gray-300 border-t-blue-600"></div>
      </div>

      <!-- 더 이상 데이터가 없을 때 메시지 -->
      <div v-if="!hasMore && !isLoading" class="text-center py-4 text-gray-500">
        더 이상 표시할 내용이 없습니다
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, watch, nextTick } from "vue";
import { searchBlog } from "@/api/news";

const searchQuery = ref("운동");
const newsItems = ref([]);
const currentPage = ref(1);
const isLoading = ref(false);
const hasMore = ref(true);

const INITIAL_LOAD_COUNT = 30; // 초기 로드 개수
const MORE_LOAD_COUNT = 20; // 추가 로드 개수

// App.vue에서 제공하는 검색어 감시
const providedQuery = inject("searchQuery");

const scrollContainer = ref(null);
const lastScrollPosition = ref(0);

// 스크롤 위치 저장
const handleScroll = async (e) => {
  const element = e.target;
  lastScrollPosition.value = element.scrollTop;
  
  if (
    element.scrollHeight - element.scrollTop <= element.clientHeight + 100 &&
    !isLoading.value &&
    hasMore.value
  ) {
    await loadMore();
  }
};

// 레이아웃 완료 후 스크롤 위치 복원
const handleLayoutComplete = () => {
  if (scrollContainer.value && lastScrollPosition.value > 0) {
    nextTick(() => {
      scrollContainer.value.scrollTop = lastScrollPosition.value;
    });
  }
};

// 추가 데이터 로드
const loadMore = async () => {
  if (isLoading.value) return;

  try {
    isLoading.value = true;
    const nextPage = currentPage.value + 1;
    const start = (nextPage - 1) * MORE_LOAD_COUNT + 1;

    const blogResponse = await searchBlog(searchQuery.value, start, MORE_LOAD_COUNT);

    if (!blogResponse.items || blogResponse.items.length === 0) {
      hasMore.value = false;
      return;
    }

    const newItems = blogResponse.items.map((item, index) => ({
      ...item,
      title: decodeHtmlEntities(item.title),
      postdate: item.postdate,
      thumbnail: getPicsumImage(newsItems.value.length + index),
    }));

    // 배열 업데이트를 nextTick으로 감싸서 처리
    await nextTick(() => {
      newsItems.value = [...newsItems.value, ...newItems];
    });
    
    currentPage.value = nextPage;
  } catch (error) {
    console.error("추가 데이터 로드 실패:", error);
  } finally {
    isLoading.value = false;
  }
};

// 랜덤 크기 생성 함수
const getRandomSize = () => {
  const widths = [300, 350, 400, 450]; // 가능한 너비 값들
  const heights = [250, 300, 350, 400]; // 가능한 높이 값들
  return {
    width: widths[Math.floor(Math.random() * widths.length)],
    height: heights[Math.floor(Math.random() * heights.length)],
  };
};

// 이미지 URL 캐시
const imageCache = new Map();
const sizeCache = new Map(); // 크기 캐시 추가

// 이미지 프리로딩을 위한 함수
const preloadImage = (src) => {
  return new Promise((resolve) => {
    if (imageCache.has(src)) {
      resolve();
      return;
    }
    const img = new Image();
    img.onload = () => {
      imageCache.set(src, true);
      resolve(img);
    };
    img.src = src;
  });
};

// 이미지 URL 생성 및 캐싱 함수
const getPicsumImage = (index) => {
  const cacheKey = `${searchQuery.value}_${index}`;

  if (imageCache.has(cacheKey)) {
    return {
      url: imageCache.get(cacheKey),
      ...sizeCache.get(cacheKey),
    };
  }

  // 랜덤 크기 생성 및 캐싱
  const size = getRandomSize();
  sizeCache.set(cacheKey, size);

  const imageUrl = `https://picsum.photos/seed/${cacheKey}/${size.width}/${size.height}`;
  imageCache.set(cacheKey, imageUrl);

  // 백그라운드에서 프리로드
  preloadImage(imageUrl);

  return {
    url: imageUrl,
    ...size,
  };
};

// 초기 검색 함수 수정
const handleSearch = async () => {
  try {
    isLoading.value = true;
    currentPage.value = 1;
    hasMore.value = true;

    const blogResponse = await searchBlog(searchQuery.value, 1, INITIAL_LOAD_COUNT);

    // 이미지 정보 미리 생성
    const newItems = blogResponse.items.map((item, index) => {
      const imageInfo = getPicsumImage(index);
      return {
        ...item,
        title: decodeHtmlEntities(item.title),
        postdate: item.postdate,
        thumbnail: imageInfo.url,
        imageWidth: imageInfo.width,
        imageHeight: imageInfo.height,
      };
    });

    // 다음 페이지 이미지 미리 로드
    const nextPageImages = Array.from({ length: MORE_LOAD_COUNT }, (_, i) =>
      getPicsumImage(INITIAL_LOAD_COUNT + i)
    );
    Promise.all(nextPageImages.map((imageInfo) => preloadImage(imageInfo.url)));

    newsItems.value = newItems;
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

// 이미지 에러 처리 개선
const handleImageError = (event, item) => {
  const cacheKey = `error_${item.link}`;
  if (!imageCache.has(cacheKey)) {
    const size = getRandomSize();
    const newUrl = `https://picsum.photos/${size.width}/${
      size.height
    }?random=${Date.now()}`;
    imageCache.set(cacheKey, newUrl);
    sizeCache.set(cacheKey, size);
    item.thumbnail = newUrl;
    item.imageWidth = size.width;
    item.imageHeight = size.height;
  } else {
    item.thumbnail = imageCache.get(cacheKey);
    const size = sizeCache.get(cacheKey);
    item.imageWidth = size.width;
    item.imageHeight = size.height;
  }
};

const openNews = (link) => {
  window.open(link, "_blank");
};

onMounted(() => {
  handleSearch();
});

watch(
  providedQuery,
  (newQuery) => {
    if (newQuery) {
      searchQuery.value = newQuery;
      handleSearch();
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.break-inside-avoid {
  -webkit-column-break-inside: avoid;
  page-break-inside: avoid;
  break-inside: avoid;
}
</style>
