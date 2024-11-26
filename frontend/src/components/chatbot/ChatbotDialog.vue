<template>
  <div v-if="isOpen" class="fixed bottom-24 right-4 w-80 z-50">
    <div class="bg-white rounded-lg shadow-xl border">
      <!-- 헤더 -->
      <div class="flex justify-between items-center p-3 border-b">
        <h3 class="font-semibold">AI 트레이너</h3>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- 채팅 내용 -->
      <div class="h-96 overflow-y-auto p-4">
        <div v-if="messages.length === 0" class="text-gray-400 text-center mt-4">
          대화를 시작해보세요!
        </div>
        
        <div v-for="(msg, index) in messages" :key="index" class="mb-4">
          <div v-if="msg.type === 'user'" class="flex justify-end">
            <div class="bg-blue-500 text-white rounded-lg p-3 max-w-[80%]">
              {{ msg.content }}
            </div>
          </div>
          
          <div v-else class="flex justify-start">
            <div class="bg-gray-100 rounded-lg p-3 max-w-[80%]">
              <div class="whitespace-pre-line">{{ msg.content }}</div>
            </div>
          </div>
        </div>

        <div v-if="loading" class="flex justify-center items-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>
      </div>

      <!-- 입력 폼 -->
      <div class="border-t p-3">
        <form @submit.prevent="sendMessage" class="flex gap-2">
          <input
            v-model="message"
            type="text"
            placeholder="메시지를 입력하세요..."
            class="flex-1 p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            :disabled="loading"
          >
          <button
            type="submit"
            class="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 disabled:opacity-50"
            :disabled="loading || !message.trim()"
          >
            전송
          </button>
        </form>

        <button
          @click="getWorkoutRecommendation"
          class="mt-2 w-full bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600 disabled:opacity-50"
          :disabled="loading"
        >
          운동 추천받기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { chatbotApi } from '@/api/chatbot';
import { useAuthStore } from '@/stores/auth';

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close']);

const authStore = useAuthStore();
const message = ref('');
const messages = ref([]);
const loading = ref(false);

// 일반 메시지 전송
const sendMessage = async () => {
  if (!message.value.trim() || loading.value) return;
  
  try {
    loading.value = true;
    // 사용자 메시지 추가
    messages.value.push({
      type: 'user',
      content: message.value
    });
  
    // API 호출
    const response = await chatbotApi.generateResponse(message.value);
    
    // 챗봇 응답 추가
    messages.value.push({
      type: 'bot',
      content: response.generation
    });
  
    message.value = ''; // 입력창 초기화
  } catch (error) {
    messages.value.push({
      type: 'bot',
      content: '죄송합니다. 오류가 발생했습니다.'
    });
  } finally {
    loading.value = false;
  }
};

// 운동 추천 받기
const getWorkoutRecommendation = async () => {
  if (loading.value) return;
  
  try {
    loading.value = true;
    const userId = authStore.user.id;
    
    const response = await chatbotApi.getWorkoutRecommendation(userId);
    
    messages.value.push({
      type: 'bot',
      content: response.recommendation
    });
  } catch (error) {
    console.error('운동 추천 에러:', error);
    messages.value.push({
      type: 'bot',
      content: '운동 추천을 가져오는데 실패했습니다.'
    });
  } finally {
    loading.value = false;
  }
};
</script>