<template>
  <draggable v-model="filteredTodos" :group="{ name: 'todos', pull: true, put: true }" item-key="id" @end="handleDragEnd" @start="handleDragStart"
    @change="handleChange" :animation="200">
    <template #item="{ element: todo }">
      <div class="flex items-center gap-1.5 pt-3.5">
        <div class="relative w-[19px] h-[19px] cursor-pointer" @click="handleDone(todo.id)">
          <svg v-if="todo.isDone" class="w-[19px] h-[19px] text-black" viewBox="0 0 20 20" fill="currentColor">
            <rect width="18" height="18" x="1" y="1" rx="4" fill="currentColor" />
            <path fill="white"
              d="M13.293 7.293a1 1 0 0 1 1.414 1.414l-5 5a1 1 0 0 1-1.414 0l-2.5-2.5a1 1 0 0 1 1.414-1.414L9 11.586l4.293-4.293z" />
          </svg>
          <div v-else class="w-[19px] h-[19px] rounded border bg-[#dadddf] border-gray-300"></div>
        </div>
        <div v-if="contentUpdateMode && todo.id === selectedTodoId" class="flex-1">
          <div class="flex" @click.stop>
            <input type="text" placeholder="할 일 입력" class="pb-1.5 w-full text-xs outline-none caret-blue-500"
              v-model="newTodoContent" :style="{
                'border-bottom': `2px solid ${categoryStore.category.color}`,
              }" />
            <EllipsisHorizontalIcon class="w-[19px] h-[19px] flex-shrink-0" />
          </div>
        </div>
        <div v-else class="flex-1">
          <button @click="toggleMenu(todo.id)" class="flex w-full">
            <div class="w-full text-xs text-left truncate">{{ todo.content }}</div>
            <EllipsisHorizontalIcon class="w-[19px] h-[19px] flex-shrink-0" />
          </button>
        </div>
      </div>
    </template>
  </draggable>
  <TodoMenu v-if="!contentUpdateMode && selectedTodoId !== null" :selectedTodoId="selectedTodoId" @close="closeMenu"
    @edit="handleContent" @delete="goDelete" />
</template>

<script setup>
import draggable from 'vuedraggable';
import { useTodoStore } from "@/stores/todo";
import { onBeforeUnmount, onMounted, ref, watch, computed } from "vue";
import { EllipsisHorizontalIcon } from "@heroicons/vue/24/outline";
import TodoMenu from "./TodoMenu.vue";
import { useCategoryStore } from "@/stores/category";

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
const isDragging = ref(false);
const draggedTodoId = ref(null);
const oldIndex = ref(null);
const newIndex = ref(null);

const filteredTodos = computed({
  get: () => {
    return todoStore.todos
      .filter(todo => todo.categoryId === props.categoryId)
      .sort((a, b) => (a.todoOrder || 0) - (b.todoOrder || 0));
  },
  set: (value) => {
    const updatedTodos = todoStore.todos.map(todo => {
      const newTodo = value.find(t => t.id === todo.id);
      return newTodo || todo;
    });
    todoStore.todos = updatedTodos;
  }
});

const toggleMenu = (id) => {
  selectedTodoId.value = selectedTodoId.value === id ? null : id;
};

const goUpdate = async () => {
  try {
    if (contentUpdateMode.value) {
      const todo = todoStore.todos.find((t) => t.id === selectedTodoId.value);
      const updatedTodo = {
        id: todo.id,
        userId: todo.userId,
        categoryId: todo.categoryId,
        isDone: todo.isDone,
        content: newTodoContent.value,
        date: todo.date,
        todoOrder: todo.todoOrder
      };
      await todoStore.fetchTodoUpdate(updatedTodo);
      contentUpdateMode.value = false;
      closeMenu();
    }
  } catch (error) {
    console.error("할 일 수정 실패:", error);
  }
};

const goDelete = async (id) => {
  try {
    await todoStore.fetchDeleteTodo(id);
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
    const updatedTodo = {
      id: todo.id,
      userId: todo.userId,
      categoryId: todo.categoryId,
      isDone: (todo.isDone + 1) % 2,
      content: todo.content,
      date: todo.date,
      todoOrder: todo.todoOrder
    };
    await todoStore.fetchTodoUpdate(updatedTodo);
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

const emit = defineEmits(['dragstart', 'dragend', 'todoAdded', 'todoRemoved']);

const handleDragStart = (event) => {
  isDragging.value = true;
  draggedTodoId.value = event.item.__draggable_context.element.id;
  oldIndex.value = event.oldIndex;
  
  emit('dragstart', {
    todoId: draggedTodoId.value,
    categoryId: props.categoryId
  });
};

const handleDragEnd = (event) => {
  emit('dragend', {
    todoId: draggedTodoId.value,
    categoryId: props.categoryId,
    newIndex: newIndex.value
  });
  draggedTodoId.value = null;
  oldIndex.value = null;
  newIndex.value = null;
  isDragging.value = false;
};

const handleChange = (event) => {
  if (event.added) {
    emit('todoAdded', {
      todoId: event.added.element.id,
      newCategoryId: props.categoryId,
      newIndex: event.added.newIndex
    });
  } else if (event.removed) {
    emit('todoRemoved', {
      todoId: event.removed.element.id,
      oldCategoryId: props.categoryId,
      oldIndex: event.removed.oldIndex
    });
  } else if (event.moved) {
    newIndex.value = event.moved.newIndex;
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

<style scoped>
[draggable="true"] {
  cursor: move;
}

[draggable="true"]:hover {
  background-color: rgba(0, 0, 0, 0.02);
}
</style>
