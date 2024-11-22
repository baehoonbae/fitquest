<template>
  <div class="fixed top-0 left-0 right-0 z-[9999]">
    <div class="max-w-[950px] mx-auto px-5 md:px-4">
      <NewsHeader @search="(query) => handleSearch(query)" />
    </div>
  </div>
  <div class="h-[calc(100vh-4rem)]">
    <div ref="scrollContainer" class="h-full overflow-y-auto rounded-[15px]" @scroll="handleScroll">
      <masonry-wall :items="newsItems" :column-width="300" :gap="16" class="px-4"
        @layout-complete="handleLayoutComplete">
        <template #default="{ item }">
          <div
            class="bg-gray-50 rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 cursor-pointer overflow-hidden flex flex-col"
            @click="openNews(item)">
            <div v-if="item.thumbnail" class="w-full overflow-hidden" :style="{
              aspectRatio: `${item.imageWidth}/${item.imageHeight}`,
            }">
              <img :src="item.thumbnail" @error="handleImageError($event, item)" class="w-full h-full object-cover"
                loading="lazy" :alt="item.title" decoding="async" :width="item.imageWidth" :height="item.imageHeight" />
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
import { searchBlog, searchImage } from "@/api/news";
import NewsHeader from "@/components/NewsHeader.vue";
const imageQuery = ref("Cross Fit Exercises");

const searchQuery = ref("운동");
const imageItems = ref([]); // 이미지 아이템 저장
const usedImageIndices = ref(new Set()); // 사용된 이미지 인덱스 추적
const newsItems = ref([]);
const currentPage = ref(1);
const isLoading = ref(false);
const hasMore = ref(true);

const INITIAL_LOAD_COUNT = 30;
const MORE_LOAD_COUNT = 20;

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

// 랜덤 이미지 선택 함수
const getUniqueRandomImage = () => {
  if (imageItems.value.length === 0) return null;
  if (usedImageIndices.value.size >= imageItems.value.length) {
    usedImageIndices.value.clear(); // 모든 이미지가 사용되었다면 초기화
  }
  let randomIndex;
  do {
    randomIndex = Math.floor(Math.random() * imageItems.value.length);
  } while (usedImageIndices.value.has(randomIndex));

  usedImageIndices.value.add(randomIndex);
  return imageItems.value[randomIndex];
};

// 이미지 URL 캐시
const imageCache = new Map();
const sizeCache = new Map();

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

// 초기 검색 함수
const handleSearch = async (query) => {
  try {
    isLoading.value = true;
    currentPage.value = 1;
    hasMore.value = true;

    // 블로그와 이미지 동시 검색
    const [blogResponse, imageResponse] = await Promise.all([
      searchBlog(query, 1, INITIAL_LOAD_COUNT),
      searchImage(imageQuery.value),
    ]);

    // 이미지 아이템 설정
    imageItems.value = imageResponse.items.map((item) => ({
      thumbnail: item.link,
      width: item.sizewidth || 400,
      height: item.sizeheight || 300,
    }));

    usedImageIndices.value.clear();

    // 블로그 아이템에 이미지 매핑
    const newItems = blogResponse.items.map((item, index) => {
      const uniqueImage = getUniqueRandomImage();
      const imageInfo = uniqueImage || {
        thumbnail: `https://picsum.photos/400/300?random=${index}`,
        width: 400,
        height: 300,
      };

      return {
        ...item,
        id: `${item.link}_${Date.now()}_${index}`,
        title: decodeHtmlEntities(item.title),
        postdate: item.postdate,
        thumbnail: imageInfo.thumbnail,
        imageWidth: imageInfo.width,
        imageHeight: imageInfo.height,
      };
    });

    // 이미지 프리로드
    Promise.all(newItems.map((item) => preloadImage(item.thumbnail)));

    newsItems.value = newItems;
  } catch (error) {
    console.error("검색 실패:", error);
  } finally {
    isLoading.value = false;
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

    const newItems = blogResponse.items.map((item, index) => {
      const uniqueImage = getUniqueRandomImage();
      const imageInfo = uniqueImage || {
        thumbnail: `https://picsum.photos/400/300?random=${newsItems.value.length + index
          }`,
        width: 400,
        height: 300,
      };

      return {
        ...item,
        id: `${item.link}_${Date.now()}_${newsItems.value.length + index}`,
        title: decodeHtmlEntities(item.title),
        postdate: item.postdate,
        thumbnail: imageInfo.thumbnail,
        imageWidth: imageInfo.width,
        imageHeight: imageInfo.height,
      };
    });

    // 이미지 프리로드
    Promise.all(newItems.map((item) => preloadImage(item.thumbnail)));

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

// 이미지 에러 처리
const handleImageError = (event, item) => {
  const uniqueImage = getUniqueRandomImage();
  if (uniqueImage) {
    item.thumbnail = uniqueImage.thumbnail;
    item.imageWidth = uniqueImage.width;
    item.imageHeight = uniqueImage.height;
  } else {
    const cacheKey = `error_${item.link}`;
    if (!imageCache.has(cacheKey)) {
      const newUrl = `https://picsum.photos/400/300?random=${Date.now()}`;
      imageCache.set(cacheKey, newUrl);
      sizeCache.set(cacheKey, { width: 400, height: 300 });
      item.thumbnail = newUrl;
      item.imageWidth = 400;
      item.imageHeight = 300;
    } else {
      item.thumbnail = imageCache.get(cacheKey);
      const size = sizeCache.get(cacheKey);
      item.imageWidth = size.width;
      item.imageHeight = size.height;
    }
  }
};

// HTML 태그 제거 함수
const stripHtmlTags = (html) => {
  const tmp = document.createElement("div");
  tmp.innerHTML = html;
  return tmp.textContent || tmp.innerText || "";
};

// 최근 본 카드뉴스 저장
const saveRecentNews = (news) => {
  try {
    let recentNews = JSON.parse(localStorage.getItem("recentNews") || "[]");
    recentNews = recentNews.filter((p) => p.link !== news.link);

    // 최신 뉴스를 배열 앞에 추가
    recentNews.unshift({
      id: news.id || `${news.link}_${Date.now()}`,
      title: stripHtmlTags(news.title),
      link: news.link,
      postdate: formatDate(news.postdate),
      thumbnail: news.thumbnail,
    });

    // 최대 5개만 유지
    recentNews = recentNews.slice(0, 5);

    localStorage.setItem("recentNews", JSON.stringify(recentNews));
  } catch (error) {
    console.error("최근 본 뉴스 저장 실패:", error);
  }
};

const openNews = (news) => {
  saveRecentNews(news);
  window.open(news.link, "_blank");
};

onMounted(() => {
  handleSearch(searchQuery.value);
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
