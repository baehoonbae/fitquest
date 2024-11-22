<template>
  <div class="py-4 border-b border-gray-100">
    <div class="flex gap-2 overflow-x-auto pb-2">
      <button
        v-for="tag in tags"
        :key="tag"
        class="whitespace-nowrap px-3 py-1.5 rounded-full bg-gray-100 hover:bg-gray-200 transition-colors duration-200"
        :class="{ 'bg-blue-600 text-white hover:bg-blue-700': selectedTag === tag }"
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

const emit = defineEmits(['select-tag']);

const selectTag = (tag) => {
  emit('select-tag', props.selectedTag === tag ? null : tag);
};
</script>