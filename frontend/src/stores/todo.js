// stores/todo.js
import { defineStore } from "pinia";
import { ref } from "vue";
import axios from "axios";
import { useAuthStore } from "./auth";

export const useTodoStore = defineStore("todo", () => {
  const todos = ref([]);
  const todo = ref({});
  const authStore = useAuthStore();

  // 투두 목록 조회(일자별로)
  const fetchTodos = async (date) => {
    const userId = authStore.user.id;
    const accessToken = authStore.getToken();
    try {
      const response = await axios.get(
        `http://localhost:8097/fitquest/api/todo/${date}/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      if (response.data) {
        todos.value = response.data;
      } else {
        console.log("할일 조회 실패:", response.data);
      }
    } catch (error) {
      console.error("할일 조회 실패:", error.response?.data || error.message);
      if (error.response?.status === 401) {
        console.error("인증 토큰이 만료되었거나 유효하지 않습니다");
      }
    }
  };

  // 할일 등록
  const fetchAddTodo = async (todoData) => {
    if (!todoData.content?.trim()) {
      throw new Error("할일 내용을 입력해주세요");
    }
    const categoryTodos = todos.value.filter(todo => todo.categoryId === todoData.categoryId);
    const maxOrder = categoryTodos.length > 0
      ? Math.max(...categoryTodos.map(t => t.todoOrder || 0))
      : -1;
    todoData.todoOrder = maxOrder + 1;
    try {
      const accessToken = authStore.getToken();
      const response = await axios.post(
        "http://localhost:8097/fitquest/api/todo",
        todoData,
        {
          headers: { Authorization: `Bearer ${accessToken}` },
        }
      );

      if (response.data) {
        await fetchTodos(todoData.date);
        return { success: true };
      }
    } catch (error) {
      if (error.response?.status === 401) {
        throw new Error("인증 토큰이 만료되었거나 유효하지 않습니다.");
      } else {
        throw new Error("할일 추가에 실패했습니다.");
      }
    }
  };

  // 할일 수정
  const fetchTodoUpdate = async (todoData) => {
    try {
      const accessToken = authStore.getToken();
      const response = await axios.put(
        `http://localhost:8097/fitquest/api/todo/${todoData.id}`,
        todoData,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      if (response.data) {
        setTimeout(async () => {
          await fetchTodos(todoData.date);
        }, 100);
        return { success: true };
      }
    } catch (error) {
      throw new Error(
        error.response?.data?.message || "할일 수정에 실패했습니다."
      );
    }
  };

  const fetchTodo = async (todoId) => {
    try {
      const accessToken = authStore.getToken();
      const response = await axios.get(
        `http://localhost:8097/fitquest/api/todo/${todoId}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      if (response.data) {
        todo.value = response.data;
      }
    } catch (error) {
      throw new Error(
        error.response?.data?.message || "할일 조회에 실패했습니다."
      );
    }
  };

  const fetchDeleteTodo = async (todoId) => {
    try {
      const accessToken = authStore.getToken();
      const response = await axios.delete(`http://localhost:8097/fitquest/api/todo/${todoId}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      if (response.data) {
        await fetchTodos(todo.value.date);
        return { success: true };
      }
    } catch (error) {
      throw new Error(
        error.response?.data?.message || "할일 삭제에 실패했습니다."
      );
    }
  }

  return {
    todos,
    todo,
    fetchTodo,
    fetchTodos,
    fetchAddTodo,
    fetchTodoUpdate,
    fetchDeleteTodo,
  };
});
