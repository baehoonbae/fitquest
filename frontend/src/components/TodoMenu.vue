<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-[999]" @click="closeMenu">
    <div class="bg-white rounded-lg p-6 w-[480px]" @click.stop>
      <template v-if="!showDatePicker">
        <div class="flex justify-center text-gray-700 font-semibold text-mg mb-4">
          {{ todoStore.todo.content }}
        </div>
        <div class="flex justify-between mb-6">
          <button @click="editTodo"
            class="flex-1 bg-gray-100 hover:bg-gray-200 py-5 px-4 mr-2 rounded-lg flex items-center justify-center">
            수정하기
          </button>
          <button @click="deleteTodo"
            class="flex-1 bg-gray-100 hover:bg-gray-200 py-5 px-4 ml-2 rounded-lg flex items-center justify-center">
            삭제하기
          </button>
        </div>
        <ul class="space-y-[25px]">
          <button class="flex items-center" @click="moveTomorrow">
            <span class="bg-blue-400 text-white w-8 h-8 rounded-full flex items-center justify-center mr-4">
              <ArrowRightIcon class="w-5 h-5" />
            </span>
            내일 하기
          </button>
          <button class="flex items-center" @click="openDatePicker">
            <span class="bg-purple-400 text-white w-8 h-8 rounded-full flex items-center justify-center mr-4">
              <ArrowPathIcon class="w-5 h-5" />
            </span>
            날짜 바꾸기
          </button>
        </ul>
      </template>
      <template v-if="showDatePicker">
        <DatePicker @closeDatePicker="closeDatePicker" />
      </template>
    </div>
  </div>
</template>

<script setup>
import { useTodoStore } from "@/stores/todo";
import { ArrowPathIcon, ArrowRightIcon } from "@heroicons/vue/24/outline";
import { onMounted, ref } from "vue";
import DatePicker from "./DatePicker.vue";

const showDatePicker = ref(false);
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
const moveTomorrow = () => {
  emit("moveTomorrow", props.selectedTodoId);
};

const openDatePicker = () => {
  showDatePicker.value = true;
};

const closeDatePicker = () => {
  showDatePicker.value = false;
  closeMenu();
};

onMounted(() => {
  todoStore.fetchTodo(props.selectedTodoId);
});
</script>
