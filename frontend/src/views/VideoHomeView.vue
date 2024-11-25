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
import { getPlaylistVideos } from '@/api/news';
import NewsHeader from "@/components/common/NewsHeader.vue";

const videos = ref([]);
const isLoading = ref(false);
const searchQuery = ref("PLjfaq5HQzMgbwR57apoXNB6hMxDV1sjbR");
const hasMore = ref(true);
const scrollContainer = ref(null);
const nextPageToken = ref(null);


// PLjfaq5HQzMgbwR57apoXNB6hMxDV1sjbR
// 
const handleSearch = async (playlistId) => {
  try {
    isLoading.value = true;
    hasMore.value = true;
    nextPageToken.value = null;
    videos.value = [];
    
    const response = await getPlaylistVideos(playlistId);
    const data = response;

    console.log(data);
    
    if (!data.items || data.items.length === 0) {
      hasMore.value = false;
      return;
    }
    
    nextPageToken.value = data.nextPageToken;
    hasMore.value = !!data.nextPageToken;
    
    videos.value = data.items.map(item => ({
      id: item.snippet.resourceId.videoId,
      title: item.snippet.title,
      description: item.snippet.description,
      thumbnail: item.snippet.thumbnails.high?.url || item.snippet.thumbnails.medium?.url,
      channelTitle: item.snippet.channelTitle,
      publishedAt: item.snippet.publishedAt
    }));
  } catch (error) {
    console.error('비디오 검색 실패:', error);
    hasMore.value = false;
  } finally {
    isLoading.value = false;
  }
};

const loadMore = async () => {
  if (isLoading.value || !hasMore.value || !nextPageToken.value) return;
  
  isLoading.value = true;
  try {
    const response = await getPlaylistVideos(searchQuery.value, nextPageToken.value);
    const data = JSON.parse(response);
    
    nextPageToken.value = data.nextPageToken;
    hasMore.value = !!data.nextPageToken;

    const newVideos = data.items.map(item => ({
      id: item.snippet.resourceId.videoId,
      title: item.snippet.title,
      thumbnail: item.snippet.thumbnails.high?.url || item.snippet.thumbnails.medium?.url,
      channelTitle: item.snippet.channelTitle,
      publishedAt: item.snippet.publishedAt
    }));

    videos.value = [...videos.value, ...newVideos];
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
