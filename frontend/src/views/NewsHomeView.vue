<template>
  <div class="h-full">
    <!-- 그리드 컨테이너 -->
    <div class="h-[calc(100vh-8rem)] overflow-y-auto rounded-[15px] z-0" style="position: relative;">
      <div class="columns-1 sm:columns-2 md:columns-3 lg:columns-4 xl:columns-5 gap-4 mx-auto max-w-[1400px]">
        <!-- 카드 아이템 -->
        <div v-for="item in newsItems" :key="item.link" class="break-inside-avoid mb-4">
          <div
            class="bg-gray-50 rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 cursor-pointer overflow-hidden flex flex-col"
            @click="openNews(item.link)">
            <!-- 썸네일 이미지 -->
            <div v-if="item.thumbnail" class="w-full h-4/5 overflow-hidden">
              <img :src="item.thumbnail" @error="handleImageError($event, item)" class="w-full h-full object-cover"
                loading="lazy" alt="" />
            </div>
            <!-- 카드 콘텐츠 -->
            <div class="p-2 h-1/5">
              <h3 class="font-semibold text-gray-800 text-sm truncate" v-html="item.title"></h3>
              <!-- 하단 메타 정보 -->
              <div class="flex items-center justify-between mt-1">
                <span class="text-xs text-gray-600">{{ formatDate(item.postdate) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, watch } from "vue";
import { searchBlog, searchImage } from '@/api/news';

const searchQuery = ref('운동');
const imageQuery = ref('보디빌딩');
const newsItems = ref([]);
const imageItems = ref([]);
const usedImageIndices = ref(new Set());

// App.vue에서 제공하는 검색어 감시
const providedQuery = inject('searchQuery');
watch(providedQuery, (newQuery) => {
  if (newQuery) {
    searchQuery.value = newQuery;
    handleSearch();
  }
}, { immediate: true });

const decodeHtmlEntities = (text) => {
  const textArea = document.createElement('textarea');
  textArea.innerHTML = text;
  return textArea.value;
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';

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
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  } catch (e) {
    return '';
  }
};

const handleImageError = (event, item) => {
  // 이미지 로드 실패시 다른 이미지로 교체
  const newImage = getUniqueRandomImage();
  if (newImage) {
    item.thumbnail = newImage.thumbnail;
  } else {
    // 대체 이미지가 없는 경우 기본 이미지 사용
    item.thumbnail = '/default-image.jpg'; // 기본 이미지 경로 설정 필요
  }
};

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

const handleSearch = async () => {
  try {
    const [blogResponse, imageResponse] = await Promise.all([
      searchBlog(searchQuery.value),
      searchImage(imageQuery.value)
    ]);

    imageItems.value = imageResponse.items.map(item => ({
      thumbnail: item.link
    }));

    usedImageIndices.value.clear();

    newsItems.value = blogResponse.items.map(item => {
      const uniqueImage = getUniqueRandomImage();
      return {
        ...item,
        title: decodeHtmlEntities(item.title),
        postdate: item.postdate,
        thumbnail: uniqueImage?.thumbnail
      };
    });
  } catch (error) {
    console.error('검색 실패:', error);
  }
};

const openNews = (link) => {
  window.open(link, '_blank');
};

onMounted(() => {
  handleSearch();
});
</script>

<style scoped>
.break-inside-avoid {
  -webkit-column-break-inside: avoid;
  page-break-inside: avoid;
  break-inside: avoid;
}
</style>
