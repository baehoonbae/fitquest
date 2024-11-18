<template>
  <div v-for="todo in todoStore.todos" :key="todo.id">
    <div
      v-if="todo.categoryId === props.categoryId"
      class="flex items-center gap-2 pt-[15px] pl-[0.7px]"
    >
      <div
        class="relative w-[24px] h-[24px] cursor-pointer"
        @click="handleDone(todo.id)"
      >
        <svg
          v-if="todo.isDone"
          class="w-[21.5px] h-[21.5px] text-black"
          viewBox="0 0 20 20"
          fill="currentColor"
        >
          <rect width="18" height="18" x="1" y="1" rx="4" fill="currentColor" />
          <path
            fill="white"
            d="M13.293 7.293a1 1 0 0 1 1.414 1.414l-5 5a1 1 0 0 1-1.414 0l-2.5-2.5a1 1 0 0 1 1.414-1.414L9 11.586l4.293-4.293z"
          />
        </svg>
        <div
          v-else
          class="w-[21.5px] h-[21.5px] rounded border bg-[#dadddf] border-gray-300"
        ></div>
      </div>
      <div v-if="contentUpdateMode && todo.id === selectedTodoId">
        <div class="flex" @click.stop>
          <input
            type="text"
            placeholder="할 일 입력"
            class="pb-2 w-[344px] text-sm outline-none caret-blue-500"
            v-model="newTodoContent"
            :style="{
              'border-bottom': `2px solid ${categoryStore.category.color}`,
            }"
          />
          <EllipsisHorizontalIcon class="w-[21.5px] h-[21.5px]" />
        </div>
      </div>
      <div v-else>
        <button @click="toggleMenu(todo.id)" class="flex">
          <div class="w-[344px] text-sm text-left">{{ todo.content }}</div>
          <EllipsisHorizontalIcon class="w-[21.5px] h-[21.5px]" />
        </button>
      </div>
    </div>
  </div>
  <TodoMenu
    v-if="!contentUpdateMode && selectedTodoId !== null"
    :selectedTodoId="selectedTodoId"
    @close="closeMenu"
    @edit="handleContent"
    @delete="goDelete"
  />
</template>

<script setup>
import { useTodoStore } from "@/stores/todo";
import { onBeforeUnmount, onMounted, ref, watch } from "vue";
import { EllipsisHorizontalIcon } from "@heroicons/vue/24/outline";
import TodoMenu from "./TodoMenu.vue";
import { useCategoryStore } from "@/stores/category";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
const props = defineProps({
  categoryId: {
    type: Number,
    required: true,
  },
});

const categoryStore = useCategoryStore();
const todoStore = useTodoStore();
const selectedTodoId = ref(null);
const contentUpdateMode = ref(false);
const newTodoContent = ref(null);

const toggleMenu = (id) => {
  selectedTodoId.value = selectedTodoId.value === id ? null : id;
};

const goUpdate = async () => {
  try {
    if (contentUpdateMode.value) {
      const todo = todoStore.todos.find((t) => t.id === selectedTodoId.value);
      todo.content = newTodoContent.value;
      await todoStore.fetchTodoUpdate(todo);
      contentUpdateMode.value = false;
      closeMenu();
    }
  } catch (error) {
    console.error("할 일 수정 실패:", error);
  }
};

const goDelete = async (id) => {
  try {
    await todoStore.fetchTodoDelete(id);
    closeMenu();
  } catch (error) {
    console.error("할 일 삭제 실패:", error);
  }
};

const closeMenu = () => {
  selectedTodoId.value = null;
};

const handleDone = async (id) => {
  try {
    const todo = todoStore.todos.find((t) => t.id === id);
    todo.isDone = (todo.isDone + 1) % 2;
    await todoStore.fetchTodoUpdate(todo);
  } catch (error) {
    console.error("할 일 수정 실패:", error);
  }
};

const handleContent = async (id) => {
  const todo = todoStore.todos.find((t) => t.id === id);
  if (todo) {
    await categoryStore.fetchCategory(todo.categoryId);
    contentUpdateMode.value = true;
    newTodoContent.value = todo.content;
  }
};

onMounted(async () => {
  window.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
      if (!contentUpdateMode.value) closeMenu();
      else goUpdate();
    }
    if (event.key === "Enter") {
      goUpdate();
    }
  });
  window.addEventListener("click", goUpdate);
});

onBeforeUnmount(() => {
  window.removeEventListener("click", goUpdate);
});

watch(
  () => props.categoryId,
  async (newCategoryId) => {
    if (newCategoryId) {
      const today = new Date().toISOString().split("T")[0];
      await todoStore.fetchTodos(today);
    }
  }
);
</script>
