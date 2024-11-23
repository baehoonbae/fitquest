<template>
  <div>
    <div class="space-y-4">
      <div v-for="category in categoryStore.categories" :key="category.id">
        <div class="flex items-center bg-gray-100 rounded-[16px] py-1.5 px-2.5 cursor-pointer hover:bg-gray-200 w-fit"
          @click.stop="openTodoForm(category)">
          <div class="flex items-center gap-1.5">
            <GlobeAltIcon v-if="category.isPublic" class="w-3.5 h-3.5 text-gray-400" />
            <LockClosedIcon v-else class="w-3.5 h-3.5 text-gray-400" />
            <span class="text-sm font-bold bg-clip-text" :style="{
              color: category.color.includes('gradient') ? 'transparent' : category.color,
              backgroundImage: category.color.includes('gradient') ? category.color : 'none',
              '-webkit-background-clip': 'text'
            }">
              {{ truncateText(category.title) }}
            </span>
          </div>
          <span class="text-gray-400 ml-1.5">+</span>
        </div>
        <div class="mb-7">
          <TodoList :categoryId="category.id" :group="'todos'" />
          <Transition name="todo-form">
            <div v-if="selectedCategory && selectedCategory.id === category.id" class="mt-3.5 mb-7">
              <div class="flex items-center gap-1.5">
                <input class="w-[19px] h-[19px] rounded border bg-[#dddfe0] border-[#dddfe0]" />
                <input type="text" placeholder="할 일 입력" class="pb-1.5 w-full text-sm outline-none caret-blue-500" :ref="(el) => {
                  if (selectedCategory?.id === category.id) todoInput = el;
                }
                  " :style="{
                    borderImage: selectedCategory.color.includes('gradient') ?
                      `${selectedCategory.color} 1` :
                      'none',
                    borderBottom: selectedCategory.color.includes('gradient') ?
                      '2px solid transparent' :
                      `2px solid ${selectedCategory.color}`,
                    borderImageSlice: selectedCategory.color.includes('gradient') ? 1 : 'none'
                  }" v-model="todo.content" @click.stop @keyup.enter="handleAddTodo(todo)" />
              </div>
            </div>
          </Transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "@/stores/auth";
import { useCategoryStore } from "@/stores/category";
import {
  GlobeAltIcon,
  LockClosedIcon,
} from "@heroicons/vue/24/outline";
import { onMounted, watch, ref, onUnmounted } from "vue";
import TodoList from "@/components/TodoList.vue";
import { useTodoStore } from "@/stores/todo";
import { useDateStore } from "@/stores/date";
import { useActivityStore } from "@/stores/activity";

const authStore = useAuthStore();
const categoryStore = useCategoryStore();
const todoStore = useTodoStore();
const dateStore = useDateStore();
const activityStore = useActivityStore();
const selectedCategory = ref(null);
const todo = ref({
  userId: authStore.user.id,
  categoryId: null,
  content: "",
  date: dateStore.selectedDate,
});
const todoInput = ref(null);

const closeAddTodo = () => {
  selectedCategory.value = null;
  todo.value.content = "";
};

const openTodoForm = (category) => {
  selectedCategory.value = category;
  todo.value.categoryId = category.id;
  todo.value.date = dateStore.selectedDate;

  setTimeout(() => {
    if (todoInput.value) {
      todoInput.value.focus();
    }
  }, 0);
};

const handleAddTodo = async (todo) => {
  if (todo.content.trim()) {
    try {
      const result = await todoStore.fetchAddTodo(todo);
      if (result?.success) {
        await todoStore.fetchTodos(todo.date);
        todo.content = "";
        await activityStore.fetchUpdateDailyActivity(todo.date, todo.userId);
      }
    } catch (error) {
      console.error("할 일 추가 실패:", error);
    }
  }
  closeAddTodo();
};

const truncateText = (text) => {
  return text.length > 20 ? text.slice(0, 20) + "..." : text;
};

onMounted(async () => {
  if (!dateStore.selectedDate) {
    dateStore.setSelectedDate(new Date().toISOString().split("T")[0]);
  }
  if (authStore.user.isAuthenticated) {
    await categoryStore.fetchCategories();
    await todoStore.fetchTodos(dateStore.selectedDate);
  }
  window.addEventListener("click", closeAddTodo);
  window.addEventListener("keydown", (event) => {
    if (event.key === "Escape") closeAddTodo();
  });
});

watch(
  () => dateStore.selectedDate,
  async (newDate) => {
    await todoStore.fetchTodos(newDate);
  }
);

watch(
  () => authStore.user.isAuthenticated,
  async (isAuthenticated) => {
    if (isAuthenticated) {
      await categoryStore.fetchCategories();
      await todoStore.fetchTodos(dateStore.selectedDate);
    }
  }
);

onUnmounted(() => {
  window.removeEventListener("click", closeAddTodo);
  window.removeEventListener("keydown", closeAddTodo);
});
</script>

<style scoped>
.todo-form-enter-active {
  animation: bounce-in 0.5s;
}

.todo-form-leave-active {
  animation: bounce-in 0.5s reverse;
}

@keyframes bounce-in {
  0% {
    transform: scale(0.3);
    opacity: 0;
  }

  50% {
    transform: scale(1.05);
    opacity: 0.5;
  }

  100% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
