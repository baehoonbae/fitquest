<template>
  <div class="fixed top-0 left-0 right-0 z-[100]">
    <div class="max-w-[950px] mx-auto px-5 md:px-4">
      <NewsHeader @search="(query) => handleSearch(query)" />
    </div>
  </div>
  <div class="h-[calc(100vh-4rem)]">
    <div
      ref="scrollContainer"
      class="h-full overflow-y-auto rounded-[15px]"
      @scroll="handleScroll"
    >
      <masonry-wall :items="videos" :column-width="300" :gap="16" class="px-4">
        <template #default="{ item }">
          <div
            class="bg-gray-50 rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 cursor-pointer overflow-hidden flex flex-col"
            @click="openVideo(item.id)"
          >
            <div
              v-if="item.thumbnail"
              class="w-full overflow-hidden"
              :style="{
                aspectRatio: '16/9',
              }"
            >
              <img
                :src="item.thumbnail"
                class="w-full h-full object-cover"
                loading="lazy"
                :alt="item.title"
                decoding="async"
              />
            </div>
            <div class="p-2 h-2/5">
              <h3
                class="font-semibold text-gray-800 text-sm truncate"
              >{{ item.title }}</h3>
              <div class="flex items-center justify-between mt-1">
                <span class="text-xs text-gray-600">{{ item.channelTitle }}</span>
                <span class="text-xs text-gray-600">{{
                  formatDate(item.publishedAt)
                }}</span>
              </div>
            </div>
          </div>
        </template>
      </masonry-wall>

      <div v-if="isLoading" class="text-center py-4">
        <div
          class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-gray-300 border-t-blue-600"
        ></div>
      </div>
    </div>
  </div>
  <div v-if="!hasMore && !isLoading" class="text-center py-4 text-gray-500">
    더 이상 표시할 내용이 없습니다
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { searchVideo } from '@/api/news';
import NewsHeader from "@/components/common/NewsHeader.vue";

const videos = ref([]);
const isLoading = ref(false);
const searchQuery = ref("스트레칭");
const currentPage = ref(1);
const hasMore = ref(true);
const scrollContainer = ref(null);

const INITIAL_LOAD_COUNT = 30;
const MORE_LOAD_COUNT = 20;

const handleSearch = async (query) => {
  try {
    isLoading.value = true;
    currentPage.value = 1;
    hasMore.value = true;
    searchQuery.value = query;
    
    const response = await searchVideo(query, INITIAL_LOAD_COUNT);
    
    if (!response.items || response.items.length === 0) {
      hasMore.value = false;
      return;
    }
    
    videos.value = response.items.map(item => ({
      id: item.id.videoId,
      title: item.snippet.title,
      description: item.snippet.description,
      thumbnail: item.snippet.thumbnails.medium.url,
      channelTitle: item.snippet.channelTitle,
      publishedAt: item.snippet.publishedAt
    }));
  } catch (error) {
    console.error('비디오 검색 실패:', error);
  } finally {
    isLoading.value = false;
  }
};

const loadMore = async () => {
  if (isLoading.value || !hasMore.value) return;

  try {
    isLoading.value = true;
    const nextPage = currentPage.value + 1;
    
    const response = await searchVideo(searchQuery.value, MORE_LOAD_COUNT, nextPage);
    
    if (!response.items || response.items.length === 0) {
      hasMore.value = false;
      return;
    }

    const newVideos = response.items.map(item => ({
      id: item.id.videoId,
      title: item.snippet.title,
      description: item.snippet.description,
      thumbnail: item.snippet.thumbnails.medium.url,
      channelTitle: item.snippet.channelTitle,
      publishedAt: item.snippet.publishedAt
    }));

    await nextTick(() => {
      videos.value = [...videos.value, ...newVideos];
    });

    currentPage.value = nextPage;
  } catch (error) {
    console.error('추가 데이터 로드 실패:', error);
    hasMore.value = false;
  } finally {
    isLoading.value = false;
  }
};

const handleScroll = async (e) => {
  const element = e.target;
  if (
    element.scrollHeight - element.scrollTop <= element.clientHeight + 100 &&
    !isLoading.value &&
    hasMore.value
  ) {
    await loadMore();
  }
};

const openVideo = (videoId) => {
  window.open(`https://www.youtube.com/watch?v=${videoId}`, '_blank');
};

const formatDate = (dateStr) => {
  if (!dateStr) return "";

  try {
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  } catch (e) {
    return "";
  }
};

onMounted(() => {
  handleSearch(searchQuery.value);
});
</script>

<style scoped>
.break-inside-avoid {
  break-inside: avoid;
}
</style>
