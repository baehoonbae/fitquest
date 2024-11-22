import { defineStore } from "pinia";
import { ref } from "vue";
import http from "@/api/http";

export const useBoardStore = defineStore("board", () => {
  const boards = ref([]);

  const fetchBoards = async () => {
    try {
      const response = await http.get("/board");
      if (response.status === 200) {
        // 날짜 기준 내림차순 정렬
        boards.value = response.data.sort((a, b) => {
          const dateA = new Date(a.date).getTime();
          const dateB = new Date(b.date).getTime();
          return dateB - dateA;
        });
      }
    } catch (error) {
      console.error("Error fetching boards:", error);
      throw error;
    }
  };

  const addBoard = async (postData) => {
    try {
      const response = await http.post("/board", postData);
      if (response.status === 201 || response.status === 200) {
        // 게시글 추가 후 즉시 목록 갱신
        await fetchBoards();
        return response.data;
      }
      throw new Error("게시글 등록에 실패했습니다.");
    } catch (error) {
      console.error("Error adding board:", error);
      throw error;
    }
  };

  // 검색 기능 추가
  const searchBoards = async (searchCondition) => {
    try {
      if (!searchCondition.word) {
        return await fetchBoards();
      }
      const response = await http.get(`/board/search/${searchCondition.key}/${searchCondition.word}`);
      if (response.status === 200) {
        boards.value = response.data.sort((a, b) => {
          const dateA = new Date(a.date).getTime();
          const dateB = new Date(b.date).getTime();
          return dateB - dateA;
        });
      } else if (response.status === 204) {
        boards.value = [];
      }
    } catch (error) {
      console.error("Error searching boards:", error);
      throw error;
    }
  };

  const fetchPost = async (id) => {
    try {
      const response = await http.get(`/board/${id}`);
      if (response.status === 200) {
        return response.data;
      }
      throw new Error("게시글 조회에 실패했습니다.");
    } catch (error) {
      console.error("Error fetching post:", error);
      throw error;
    }
  };

  const fetchUpdateBoard = async (id, postData) => {
    try {
      const response = await http.put(`/board/${id}`, postData);
      if (response.status === 200) {
        return response.data;
      }
      throw new Error("게시글 수정에 실패했습니다.");
    } catch (error) {
      console.error("Error updating board:", error);
      throw error;
    }
  };

  return {
    boards,
    fetchBoards,
    addBoard,
    searchBoards,
    fetchPost,
    fetchUpdateBoard,
  };
});
