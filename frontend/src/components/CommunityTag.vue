<template>
  <div class="tags-container">
    <div class="tags-scroll">
      <button
        v-for="tag in tags"
        :key="tag"
        class="tag"
        :class="{ active: selectedTag === tag }"
        @click="selectTag(tag)"
      >
        #{{ tag }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: "CommunityTags",
  props: {
    tags: {
      type: Array,
      required: true,
    },
    selectedTag: {
      type: String,
      default: null,
    },
  },
  methods: {
    selectTag(tag) {
      this.$emit("select-tag", this.selectedTag === tag ? null : tag);
    },
  },
};
</script>

<style scoped>
.tags-container {
  padding: 16px 0;
  border-bottom: 1px solid #eee;
}

.tags-scroll {
  display: flex;
  overflow-x: auto;
  gap: 8px;
  padding-bottom: 8px;
}

.tag {
  white-space: nowrap;
  padding: 6px 12px;
  border-radius: 16px;
  background-color: #f5f5f5;
  border: none;
  cursor: pointer;
}

.tag.active {
  background-color: #007bff;
  color: white;
}
</style>
