// stores/todo.js
// 할일 관련 스토어
// 월별 투두 목록과 일자별 투두 목록을 관리함
// 할일 삭제, 추가, 수정 시 월별 투두 목록과 일자별 투두 목록을 갱신함
import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { useAuthStore } from "./auth";
import http from "@/api/http";
import { useDateStore } from "./date";

export const useTodoStore = defineStore("todo", () => {
  const monthlyTodos = ref([]);
  const dailyTodos = ref([]);
  const todo = ref({});
  const dateStore = useDateStore();
  const authStore = useAuthStore();
  const monthlyUndoneCounts = computed(() => {
    const counts = {};
    monthlyTodos.value.forEach((todo) => {
      if (!todo.isDone) {
        counts[todo.date] = (counts[todo.date] || 0) + 1;
      }
    });
    return counts;
  });

  // 투두 목록 조회(일자별로)
  const fetchTodos = async (date) => {
    const userId = authStore.user.id;
    const accessToken = authStore.getToken();
    try {
      const response = await http.get(`/todo/${date}/${userId}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      if (response.data) {
        dailyTodos.value = response.data;
      }
    } catch (error) {
      console.error("할일 조회 실패:", error);
    }
  };

  // 할일 등록
  const fetchAddTodo = async (todoData) => {
    if (!todoData.content?.trim()) {
      throw new Error("할일 내용을 입력해주세요");
    }
    const categoryTodos = dailyTodos.value.filter(
      (todo) => todo.categoryId === todoData.categoryId
    );
    const maxOrder =
      categoryTodos.length > 0
        ? Math.max(...categoryTodos.map((t) => t.todoOrder || 0))
        : -1;
    todoData.todoOrder = maxOrder + 1;
    try {
      const accessToken = authStore.getToken();
      const response = await http.post("/todo", todoData, {
        headers: { Authorization: `Bearer ${accessToken}` },
      });

      if (response.data) {
        await fetchTodos(todoData.date);
        // todo를 추가할 때 is_done이 0이므로 추가한 날짜의 월 투두 목록을 조회한다.
        const date = new Date(todoData.date);
        await fetchMonthlyTodos(date.getFullYear(), date.getMonth() + 1);
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
    const originalTodos = [...dailyTodos.value];
    try {
      // 즉시 로컬 상태 업데이트
      dailyTodos.value = dailyTodos.value.map(todo => 
        todo.id === todoData.id ? { ...todo, ...todoData } : todo
      );

      const accessToken = authStore.getToken();
      const response = await http.put(`/todo/${todoData.id}`, todoData, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });

      if (response.data) {
        // 월간 데이터만 업데이트
        const date = new Date(todoData.date);
        await fetchMonthlyTodos(date.getFullYear(), date.getMonth() + 1);
        return { success: true };
      }
    } catch (error) {
      // 실패 시 원래 상태로 복구
      dailyTodos.value = originalTodos;
      throw new Error(error.response?.data?.message || "할일 수정에 실패했습니다.");
    }
  };

  const fetchTodo = async (todoId) => {
    try {
      const accessToken = authStore.getToken();
      const response = await http.get(`/todo/${todoId}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
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
      const response = await http.delete(`/todo/${todoId}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      if (response.data) {
        await fetchTodos(todo.value.date);
        const date = new Date(todo.value.date);
        await fetchMonthlyTodos(date.getFullYear(), date.getMonth() + 1);
        return { success: true };
      }
    } catch (error) {
      throw new Error(
        error.response?.data?.message || "할일 삭제에 실패했습니다."
      );
    }
  };

  const getUndoneTodoCount = (date) => {
    return monthlyUndoneCounts.value[date] || 0;
  };

  const fetchMonthlyTodos = async (year, month) => {
    if (month < 10) {
      month = "0" + month;
    }
    try {
      const userId = authStore.user.id;
      const accessToken = authStore.getToken();
      const response = await http.get(`/todo/${userId}/${year}/${month}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      monthlyTodos.value = response.data;
    } catch (error) {
      console.error('todos 조회 실패:', error);
    }
  };

  // 로컬 상태 즉시 업데이트를 위한 메서드
  const updateLocalTodos = (todos) => {
    dailyTodos.value = todos;
  };

  return {
    monthlyTodos,
    dailyTodos,
    todo,
    monthlyUndoneCounts,
    getUndoneTodoCount,
    fetchTodo,
    fetchTodos,
    fetchAddTodo,
    fetchTodoUpdate,
    fetchDeleteTodo,
    fetchMonthlyTodos,
    updateLocalTodos,
  };
});
