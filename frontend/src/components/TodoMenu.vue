<template>
  <div
    class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
    @click="closeMenu"
  >
    <div class="bg-white rounded-lg p-6 w-[480px]" @click.stop>
      <div class="flex justify-center text-gray-700 font-semibold text-mg mb-4">
        {{ todoStore.todo.content }}
      </div>
      <div class="flex justify-between mb-6">
        <button
          @click="editTodo"
          class="flex-1 bg-gray-100 hover:bg-gray-200 py-5 px-4 mr-2 rounded-lg flex items-center justify-center"
        >
          수정하기
        </button>
        <button
          @click="deleteTodo"
          class="flex-1 bg-gray-100 hover:bg-gray-200 py-5 px-4 ml-2 rounded-lg flex items-center justify-center"
        >
          삭제하기
        </button>
      </div>
      <ul class="space-y-[25px]">
        <li class="flex items-center">
          <span
            class="bg-yellow-300 text-white w-8 h-8 rounded-full flex items-center justify-center mr-4"
            >📒</span
          >
          메모
        </li>
        <li class="flex items-center">
          <span
            class="bg-pink-300 text-white w-8 h-8 rounded-full flex items-center justify-center mr-4"
            >⏰</span
          >
          시간 알림
        </li>
        <li class="flex items-center">
          <span
            class="bg-blue-400 text-white w-8 h-8 rounded-full flex items-center justify-center mr-4"
            >➡️</span
          >
          내일 하기
        </li>
        <li class="flex items-center">
          <span
            class="bg-purple-400 text-white w-8 h-8 rounded-full flex items-center justify-center mr-4"
            >🔄</span
          >
          날짜 바꾸기
        </li>
        <li class="flex items-center">
          <span
            class="bg-indigo-300 text-white w-8 h-8 rounded-full flex items-center justify-center mr-4"
            >✔️</span
          >
          루틴 등록하기
        </li>
        <li class="flex items-center">
          <span
            class="bg-blue-500 text-white w-8 h-8 rounded-full flex items-center justify-center mr-4"
            >📂</span
          >
          보관함으로 이동
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { useTodoStore } from "@/stores/todo";
import { onMounted } from "vue";

const emit = defineEmits(["close"]);
const props = defineProps(["selectedTodoId"]);
const todoStore = useTodoStore();

const closeMenu = () => {
  emit("close");
};
const editTodo = () => {
  emit("edit", props.selectedTodoId);
};
const deleteTodo = () => {
  emit("delete", props.selectedTodoId);
};
onMounted(() => {
  todoStore.fetchTodo(props.selectedTodoId);
});
</script>
