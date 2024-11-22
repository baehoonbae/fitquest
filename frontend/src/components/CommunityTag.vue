<template>
  <div class="py-4 border-b border-gray-100">
    <div class="flex flex-wrap gap-2">
      <!-- overflow-x-auto와 pb-2 제거, flex-wrap 추가 -->
      <button
        v-for="tag in tags"
        :key="tag"
        class="whitespace-nowrap px-3 py-1.5 rounded-full bg-gray-100 hover:bg-gray-200 transition-colors duration-200"
        :class="{
          'bg-blue-600 text-white hover:bg-blue-700': selectedTag === tag,
        }"
        @click="selectTag(tag)"
      >
        #{{ tag }}
      </button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  tags: {
    type: Array,
    required: true,
  },
  selectedTag: {
    type: String,
    default: null,
  },
});

const emit = defineEmits(["select-tag"]);

const selectTag = (tag) => {
  emit("select-tag", props.selectedTag === tag ? null : tag);
};
</script>

<style scoped>
/* 선택적: 애니메이션 효과를 주고 싶다면 */
.flex-wrap button {
  transition: all 0.2s ease;
}

/* 선택적: 태그들 사이의 간격을 더 조정하고 싶다면 */
.flex-wrap {
  margin: -4px; /* 네거티브 마진으로 정렬 보정 */
}

.flex-wrap button {
  margin: 4px; /* 각 버튼의 실제 마진 */
}
</style>
