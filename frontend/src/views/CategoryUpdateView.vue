<template>
  <div
    class="max-w-[900px] mx-auto"
    @click="showDropdown = false"
    @keydown.esc="showDropdown = false"
    @keydown.enter="handleUpdate"
  >
    <!-- 메인 컨텐츠 -->
    <main class="flex-1 flex justify-center items-start">
      <div class="w-[900px] p-4 mx-auto">
        <div class="space-y-6">
          <!-- 카테고리 입력 -->
          <div class="space-y-2">
            <input
              type="text"
              v-model="localCategory.title"
              class="w-full p-2 rounded-md border-b-[3px] focus:outline-none"
              :style="{
                borderBottomColor: localCategory.color,
                color: localCategory.color,
              }"
            />
          </div>
          <!-- 에러 메시지 표시 -->
          <p v-if="error" class="text-sm text-red-500">{{ error }}</p>

          <!-- 공개 설정 세팅 -->
          <div class="space-y-4">
            <div class="space-y-2 pb-2 border-b-[1px]">
              <div class="flex items-center justify-between relative">
                <div class="flex items-center space-x-2">
                  <span class="text-sm text-gray-700">공개설정</span>
                </div>
                <button @click.stop="toggleDropdown" class="flex items-center space-x-2">
                  <span class="text-sm text-gray-600">
                    <template v-if="localCategory.isPublic">
                      <GlobeAltIcon class="w-4 h-4 inline-block" />
                      전체공개
                    </template>
                    <template v-else>
                      <LockClosedIcon class="w-4 h-4 inline-block" />
                      나만보기
                    </template>
                  </span>
                  <ChevronDownIcon class="h-5 w-5 text-gray-400" />
                </button>
                <!-- 드롭다운 메뉴 -->
                <div
                  v-if="showDropdown"
                  @click.stop
                  class="absolute right-0 top-8 w-32 bg-white border rounded-md shadow-lg z-10"
                >
                  <div
                    @click="selectVisibility(1)"
                    class="px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm whitespace-nowrap"
                  >
                    <GlobeAltIcon class="w-4 h-4 inline-block" />
                    전체공개
                  </div>
                  <div
                    @click="selectVisibility(0)"
                    class="px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm whitespace-nowrap"
                  >
                    <LockClosedIcon class="w-4 h-4 inline-block" />
                    나만보기
                  </div>
                </div>
              </div>
            </div>

            <!-- 색상 선택 -->
            <div class="pb-2 border-b-[1px]">
              <div class="flex items-center justify-between relative">
                <span class="text-sm text-gray-700">색상</span>
                <input
                  type="color"
                  v-model="localCategory.color"
                  class="w-8 h-8 rounded-full overflow-hidden [&::-webkit-color-swatch-wrapper]:p-0 [&::-webkit-color-swatch]:border-none [&::-webkit-color-swatch]:rounded-full left-0"
                  style="direction: ltr"
                />
              </div>
            </div>

            <!-- 삭제 버튼 -->
            <div class="flex justify-center gap-3">
              <button
                @click="showDeleteModal = true"
                class="py-2 px-12 font-semibold rounded-md bg-[#f5f5f5] text-red-500 hover:bg-red-100"
              >
                삭제
              </button>
              <button
                @click="handleUpdate"
                class="py-2 px-12 font-semibold rounded-md bg-[#f5f5f5] text-blue-500 hover:bg-blue-100"
              >
                확인
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 삭제 확인 모달 -->
    <Transition name="modal">
      <div
        v-if="showDeleteModal"
        class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
        @click="showDeleteModal = false"
      >
        <div class="bg-white rounded-lg p-6 w-80" @click.stop>
          <p class="text-gray-600 mb-6 text-center">카테고리를 삭제하시겠습니까?</p>
          <div class="flex justify-center gap-3">
            <button
              @click="showDeleteModal = false"
              class="w-full px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-md"
            >
              취소
            </button>
            <button
              @click="categoryStore.fetchDeleteCategory(id)"
              class="w-full px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600"
            >
              삭제
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { onMounted, ref, computed, onUnmounted, watch } from "vue";
import { ChevronDownIcon, GlobeAltIcon, LockClosedIcon } from "@heroicons/vue/24/outline";
import { useRoute } from "vue-router";
import { useCategoryStore } from "@/stores/category";

const route = useRoute();
const showDropdown = ref(false);
const showDeleteModal = ref(false);
const categoryStore = useCategoryStore();
const id = route.params.id;
const localCategory = ref({
  title: "",
  color: "#000000",
  isPublic: 1,
});
const error = ref("");

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
};

const selectVisibility = (isPublic) => {
  localCategory.value.isPublic = isPublic;
  showDropdown.value = false;
};

const handleEscape = (e) => {
  if (e.key === "Escape" && showDeleteModal.value) {
    showDeleteModal.value = false;
  }
};

onMounted(async () => {
  try {
    await categoryStore.fetchCategory(id);
    localCategory.value = {
      ...categoryStore.category,
      title: categoryStore.category.title || "",
    };
  } catch (err) {
    error.value = "카테고리 정보를 불러오는데 실패했습니다";
  }
});

watch(showDeleteModal, (newValue) => {
  if (newValue) {
    document.addEventListener("keydown", handleEscape);
  } else {
    document.removeEventListener("keydown", handleEscape);
  }
});

onUnmounted(() => {
  document.removeEventListener("keydown", handleEscape);
});

// 유효성 검사
const isValid = computed(() => {
  if (!localCategory.value?.title?.trim()) {
    error.value = "카테고리 이름을 입력해주세요";
    return false;
  }
  if (localCategory.value.title.trim().length < 1) {
    error.value = "카테고리 이름은 1글자 이상 입력해주세요";
    return false;
  }
  error.value = "";
  return true;
});

// 업데이트 핸들러
const handleUpdate = async () => {
  if (!isValid.value) return;

  try {
    await categoryStore.fetchUpdateCategory(localCategory.value);
  } catch (err) {
    error.value = err.message;
  }
};
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
