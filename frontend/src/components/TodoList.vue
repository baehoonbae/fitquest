<template>
  <draggable v-model="filteredTodos" :group="{ name: 'todos', pull: true, put: true }" item-key="id"
    @end="handleDragEnd" @start="handleDragStart" @change="handleChange" :animation="200">
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
    @edit="handleContent" @delete="goDelete" @moveTomorrow="handleMoveTomorrow" />
</template>

<script setup>
import { useCategoryStore } from "@/stores/category";
import { useTodoStore } from "@/stores/todo";
import { EllipsisHorizontalIcon } from "@heroicons/vue/24/outline";
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import draggable from "vuedraggable";
import TodoMenu from "./TodoMenu.vue";
import { useDragStore } from "@/stores/drag";
import { useAuthStore } from "@/stores/auth";
import { useActivityStore } from "@/stores/activity";

const props = defineProps({
  categoryId: {
    type: Number,
    required: true,
  },
  group: {
    // group prop 추가
    type: String,
    default: "todos",
  },
  onDragstart: {
    // dragstart 이벤트 prop 추가
    type: Function,
    default: () => { },
  },
  onDragend: {
    // dragend 이벤트 prop 추가
    type: Function,
    default: () => { },
  },
});

const authStore = useAuthStore();
const categoryStore = useCategoryStore();
const todoStore = useTodoStore();
const activityStore = useActivityStore();

const selectedTodoId = ref(null);
const contentUpdateMode = ref(false);
const newTodoContent = ref(null);
const isDragging = ref(false);

const filteredTodos = computed({
  get: () => {
    return todoStore.dailyTodos
      .filter((todo) => todo.categoryId === props.categoryId)
      .sort((a, b) => (a.todoOrder || 0) - (b.todoOrder || 0));
  },
  set: (value) => {
    const updatedTodos = todoStore.dailyTodos.map((todo) => {
      const newTodo = value.find((t) => t.id === todo.id);
      return newTodo || todo;
    });
    todoStore.dailyTodos = updatedTodos;
  },
});

const toggleMenu = (id) => {
  selectedTodoId.value = selectedTodoId.value === id ? null : id;
};

const goUpdate = async () => {
  try {
    if (contentUpdateMode.value) {
      const todo = todoStore.dailyTodos.find((t) => t.id === selectedTodoId.value);
      const updatedTodo = {
        id: todo.id,
        userId: todo.userId,
        categoryId: todo.categoryId,
        isDone: todo.isDone,
        content: newTodoContent.value,
        date: todo.date,
        todoOrder: todo.todoOrder,
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
  const todo = todoStore.dailyTodos.find((t) => t.id === id);
  try {
    await todoStore.fetchDeleteTodo(id);
    await activityStore.fetchUpdateDailyActivity(todo.date, todo.userId);
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
    const todo = todoStore.dailyTodos.find((t) => t.id === id);
    const updatedTodo = {
      id: todo.id,
      userId: todo.userId,
      categoryId: todo.categoryId,
      isDone: (todo.isDone + 1) % 2,
      content: todo.content,
      date: todo.date,
      todoOrder: todo.todoOrder,
    };
    await todoStore.fetchTodoUpdate(updatedTodo);
    await activityStore.fetchUpdateDailyActivity(todo.date, todo.userId);
  } catch (error) {
    console.error("할 일 수정 실패:", error);
  }
};

const handleContent = async (id) => {
  const todo = todoStore.dailyTodos.find((t) => t.id === id);
  if (todo) {
    await categoryStore.fetchCategory(todo.categoryId);
    contentUpdateMode.value = true;
    newTodoContent.value = todo.content;
  }
};

const handleMoveTomorrow = async (id) => {
  try {
    const todo = todoStore.dailyTodos.find((t) => t.id === id);
    const oldDate = todo.date;
    const tomorrow = new Date(todo.date);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const newDate = tomorrow.toISOString().split('T')[0];
    todo.date = newDate;
    await todoStore.fetchTodoUpdate(todo);

    await Promise.all([
      todoStore.fetchTodos(oldDate),
      todoStore.fetchTodos(newDate),
      activityStore.fetchUpdateDailyActivity(oldDate, todo.userId),
      activityStore.fetchUpdateDailyActivity(newDate, todo.userId)
    ]);
    closeMenu();
  } catch (error) {
    console.error("할 일 내일로 이동 실패:", error);
  }
};

const dragStore = useDragStore();

const handleDragStart = (event) => {
  isDragging.value = true;
  const draggedTodo = event.item.__draggable_context.element;
  dragStore.setDragStart(draggedTodo.id, props.categoryId, event.oldIndex);
};

const handleDragEnd = async (event) => {
  if (!isDragging.value || !dragStore.dragState.todoId) return;

  try {
    const draggedTodo = todoStore.dailyTodos.find(
      (t) => t.id === dragStore.dragState.todoId
    );
    if (!draggedTodo) return;

    // dragState를 사용하여 업데이트할 todo 생성
    const updatedDraggedTodo = {
      ...draggedTodo,
      categoryId: dragStore.dragState.endCategoryId,
      todoOrder: dragStore.dragState.endIndex,
    };

    // 시작 카테고리의 todos
    const startCategoryTodos = todoStore.dailyTodos
      .filter((t) => t.categoryId === dragStore.dragState.startCategoryId && t.id !== draggedTodo.id)
      .sort((a, b) => (a.todoOrder || 0) - (b.todoOrder || 0))
      .map((todo, index) => ({
        ...todo,
        todoOrder: index,
      }));

    // 도착 카테고리의 todos
    const endCategoryTodos = todoStore.dailyTodos
      .filter((t) => t.categoryId === dragStore.dragState.endCategoryId && t.id !== draggedTodo.id)
      .sort((a, b) => (a.todoOrder || 0) - (b.todoOrder || 0));

    // 드래그된 todo를 새 위치에 삽입
    endCategoryTodos.splice(dragStore.dragState.endIndex, 0, updatedDraggedTodo);

    const endUpdates = endCategoryTodos.map((todo, index) => ({
      ...todo,
      todoOrder: index,
    }));

    // 모든 업데이트를 한번에 실행
    const allUpdates = [...startCategoryTodos, ...endUpdates];
    await Promise.all(allUpdates.map((todo) => todoStore.fetchTodoUpdate(todo)));

    // 스토어 상태 업데이트
    todoStore.dailyTodos = todoStore.dailyTodos.map((todo) => {
      const updatedTodo = allUpdates.find((t) => t.id === todo.id);
      return updatedTodo || todo;
    });
  } catch (error) {
    console.error("Todo 순서 업데이트 실패:", error);
  } finally {
    dragStore.resetDragState();
    isDragging.value = false;
  }
};

const handleChange = (event) => {
  if (event.added || event.moved) {
    dragStore.setDragEnd(props.categoryId, event.added?.newIndex ?? event.moved.newIndex);
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
