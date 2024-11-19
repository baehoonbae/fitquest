<template>
  <div class="boards-container">
    <div class="board-list">
      <!-- 게시판 헤더 -->
      <div class="board-header">
        <div class="header-tag">태그</div>
        <div class="header-title">제목</div>
        <div class="header-info">
          <span class="header-writer">작성자</span>
          <span class="header-date">작성일</span>
          <span class="header-views">조회수</span>
        </div>
      </div>
      <!-- 게시글 목록 -->
      <div
        v-for="board in boards"
        :key="board.id"
        class="board-item"
        @click="openBoard(board.id)"
      >
        <div class="board-tag">#{{ board.tag }}</div>
        <div class="board-title">
          {{ board.title }}
          <p class="board-preview">{{ truncateContent(board.content) }}</p>
        </div>
        <div class="board-info">
          <span class="board-writer">{{ board.writer }}</span>
          <span class="board-date">{{ formatDate(board.date) }}</span>
          <span class="board-views">
            <i class="fas fa-eye"></i> {{ board.viewCount }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "CommunityBoard",
  props: {
    boards: {
      type: Array,
      required: true,
    },
  },
  methods: {
    formatDate(date) {
      if (!date) return "";
      const dateObj = new Date(date);
      const year = dateObj.getFullYear();
      const month = String(dateObj.getMonth() + 1).padStart(2, "0");
      const day = String(dateObj.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    },
    openBoard(id) {
      this.$router.push(`/community/detail/${id}`);
    },
    truncateContent(content) {
      if (!content) return "";
      return content.length > 50 ? content.slice(0, 50) + "..." : content;
    },
  },
};
</script>

<style scoped>
.boards-container {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin: 20px 0;
}

.board-list {
  width: 100%;
}

/* 게시판 헤더 스타일 */
.board-header {
  display: grid;
  grid-template-columns: 100px 1fr 280px;
  padding: 15px 20px;
  background-color: #f8f9fa;
  border-bottom: 2px solid #e9ecef;
  font-weight: 600;
  color: #495057;
}

.header-info {
  display: grid;
  grid-template-columns: 1fr 1fr 80px;
  text-align: center;
}

/* 게시글 아이템 스타일 */
.board-item {
  display: grid;
  grid-template-columns: 100px 1fr 280px;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
  transition: background-color 0.2s ease;
  cursor: pointer;
}

.board-item:hover {
  background-color: #f8f9fa;
}

.board-tag {
  color: #1976d2;
  font-size: 0.9rem;
  font-weight: 500;
}

.board-title {
  padding-right: 20px;
}

.board-preview {
  font-size: 0.85rem;
  color: #868e96;
  margin-top: 5px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.board-info {
  display: grid;
  grid-template-columns: 1fr 1fr 80px;
  align-items: center;
  text-align: center;
  font-size: 0.9rem;
  color: #495057;
}

.board-writer {
  font-weight: 500;
}

.board-date {
  color: #868e96;
}

.board-views {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.board-views i {
  font-size: 0.8rem;
  color: #adb5bd;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .board-header {
    display: none; /* 모바일에서는 헤더 숨김 */
  }

  .board-item {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .board-info {
    grid-template-columns: repeat(3, 1fr);
    margin-top: 10px;
  }

  .board-tag {
    font-size: 0.8rem;
  }

  .board-title {
    padding-right: 0;
  }

  .board-preview {
    margin-top: 8px;
  }
}
</style>
