<template>
  <div class="flex flex-col min-h-screen bg-white">
    <!-- Main Content -->
    <main class="flex-1 flex justify-center items-start bg-white">
      <div class="w-[900px] mx-auto">
        <div class="justify-items-center py-10">
          <div class="w-20 h-20 bg-[#ebeef0] rounded-full"></div>
          <button class="text-[0.78rem] font-bold text-blue-500 mt-4">
            프로필 이미지
          </button>
        </div>
        <div class="space-y-[0.6rem]">
          <button
            class="flex justify-between items-center px-6 w-[900px] h-[50px] text-[0.9rem] font-medium p-2 rounded-[20px] bg-[#f6f6f6]"
            @click="showChangeNameModal = true"
          >
            <span>이름</span>
            <span class="font-bold">
              {{ authStore.user.name }}
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                class="w-4 h-4 inline"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M8.25 4.5l7.5 7.5-7.5 7.5"
                />
              </svg>
            </span>
          </button>
          <button
            class="flex justify-between items-center px-6 w-[900px] h-[50px] text-[0.9rem] font-medium p-2 rounded-[20px] bg-[#f6f6f6]"
            @click="showChangeDescriptionModal = true"
          >
            <span>자기소개</span>
            <span class="font-bold"
              >{{ localUser.description || "등록안됨" }}
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                class="w-4 h-4 inline"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M8.25 4.5l7.5 7.5-7.5 7.5"
                />
              </svg>
            </span>
          </button>
        </div>
      </div>
    </main>
  </div>
  <Transition name="fade">
    <ChangeName v-if="showChangeNameModal" @close="showChangeNameModal = false" />
  </Transition>
  <Transition name="fade">
    <ChangeDescription
      v-if="showChangeDescriptionModal"
      @close="showChangeDescriptionModal = false"
    />
  </Transition>
</template>

<script setup>
import { ref, computed } from "vue";
import ChangeName from "@/components/ChangeName.vue";
import ChangeDescription from "@/components/ChangeDescription.vue";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
const localUser = computed(() => ({ ...authStore.user }));

const showChangeNameModal = ref(false);
const showChangeDescriptionModal = ref(false);
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
