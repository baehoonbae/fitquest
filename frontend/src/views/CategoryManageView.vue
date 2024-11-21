<template>
  <div class="max-w-[900px] mx-auto">
    <!-- 카테고리 목록 -->
    <div
      class="space-y-2 h-[calc(100vh-160px)] overflow-auto [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]"
    >
      <div
        v-for="category in categoryStore.categories"
        :key="category.id"
        @click="goUpdate(category.id)"
        class="flex items-center p-3 rounded-lg transition-colors cursor-pointer"
      >
        <span
          class="text-base font-semibold bg-[#f2f2f2] px-3.5 py-2 rounded-full"
          :style="{ color: category.color }"
          >{{ category.title }}</span
        >
        <div class="ml-auto flex items-center gap-2">
          <GlobeAltIcon v-if="category.isPublic" class="w-5 h-5 text-gray-400" />
          <LockClosedIcon v-else class="w-5 h-5 text-gray-400" />
        </div>
      </div>
      <div class="flex justify-center">
        <RouterLink
          to="/category-regist"
          class="fixed bottom-24 left-1/2 -translate-x-1/2 flex justify-center items-center w-[120px] h-10 bg-[#f2f2f2] rounded-full"
        >
          <PlusIcon class="w-6 h-6 text-gray-600" />
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCategoryStore } from "@/stores/category";
import { GlobeAltIcon, LockClosedIcon, PlusIcon } from "@heroicons/vue/24/outline";
import { onMounted } from "vue";
import { useRouter } from "vue-router";

const categoryStore = useCategoryStore();

const router = useRouter();

const goUpdate = (id) => {
  router.push({
    name: "CategoryUpdate",
    params: { id },
  });
};

onMounted(async () => {
  await categoryStore.fetchCategories();
});

router.afterEach(async () => {
  await categoryStore.fetchCategories();
});
</script>
