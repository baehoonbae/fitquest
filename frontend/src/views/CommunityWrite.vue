<template>
  <div class="max-w-4xl mx-auto px-5 my-1 md:my-2">
    <h1 class="text-3xl font-bold text-gray-800 mb-8">게시글 작성</h1>

    <form @submit.prevent="submitPost" class="bg-white p-8 md:p-5 rounded-lg shadow-sm">
      <div class="mb-6">
        <label for="tag" class="block font-semibold text-gray-700 mb-2">태그</label>
        <div class="relative">
          <!-- 관리자일 경우 disabled 상태의 공지 태그 표시 -->
          <button type="button" :disabled="isAdmin" @click.stop="toggleDropdown"
            class="w-full px-3 py-2.5 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500/10 focus:border-blue-600 flex justify-between items-center"
            :class="{ 'bg-gray-50': isAdmin }">
            <span class="text-gray-700">{{
              post.tag || "태그를 선택하세요"
            }}</span>
            <ChevronDownIcon v-if="!isAdmin" class="h-5 w-5 text-gray-400" />
          </button>

          <Transition name="dropdown">
            <div v-if="showDropdown && !isAdmin"
              class="absolute z-10 w-full mt-1 bg-white border rounded-md shadow-lg origin-top-right max-h-[240px] overflow-y-scroll">
              <div v-for="tag in COMMUNITY_TAGS" :key="tag" @click="selectTag(tag)"
                class="px-4 py-2.5 hover:bg-gray-100 cursor-pointer text-sm text-gray-700">
                {{ tag }}
              </div>
            </div>
          </Transition>
        </div>
      </div>
      <div class="mb-6">
        <label for="title" class="block font-semibold text-gray-700 mb-2">제목</label>
        <input type="text" id="title" v-model="post.title" required placeholder="제목을 입력하세요"
          class="w-full px-3 py-2.5 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500/10 focus:border-blue-600" />
      </div>
      <div class="mb-6">
        <label for="content" class="block font-semibold text-gray-700 mb-2">내용</label>
        <textarea id="content" v-model="post.content" required placeholder="내용을 입력하세요" rows="10"
          class="w-full px-3 py-2.5 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500/10 focus:border-blue-600 min-h-[200px] resize-y" />
      </div>
      <div class="mb-6">
        <label for="image" class="block font-semibold text-gray-700 mb-2">이미지</label>
        <input type="file" id="image" ref="fileInput" @change="handleImageChange" accept="image/*"
          class="px-3 py-2.5 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500/10 focus:border-blue-600" />
      </div>
      <div class="flex justify-end gap-3 mt-8">
        <button type="button"
          class="px-5 py-2.5 rounded-md font-medium bg-gray-100 text-gray-700 hover:bg-gray-200 transition-all duration-200"
          @click="router.go(-1)">
          취소
        </button>
        <button type="submit"
          class="px-5 py-2.5 rounded-md font-medium bg-blue-600 text-white hover:bg-blue-700 transition-all duration-200">
          등록
        </button>
      </div>
    </form>
  </div>
  <NeedLoginAlert @close="needLoginAlert = false" v-if="needLoginAlert" />
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { useBoardStore } from "@/stores/board";
import { COMMUNITY_TAGS } from "@/stores/tags";
import http from "@/api/http";
import { getChoseong } from "es-hangul";
import { ChevronDownIcon } from "@heroicons/vue/24/outline";
import NeedLoginAlert from "@/components/alert/NeedLoginAlert.vue"; 

const router = useRouter();
const authStore = useAuthStore();
const boardStore = useBoardStore();

const needLoginAlert = ref(false);

const isAdmin = computed(() => {
  return authStore.user.isAdmin === true || authStore.user.isAdmin === 1;
});

const fileInput = ref(null);

// post ref 수정
const post = ref({
  userId: null,
  writer: "",
  tag: "",
  title: "",
  content: "",
  postImage: null,
  isNotice: false, // 공지사항 여부 추가
});

onMounted(async () => {
  if (!authStore.user.isAuthenticated) {
    needLoginAlert.value = true;
    return;
  }
  
  // 관리자 권한 확인을 위한 서버 요청 추가
  try {
    const response = await http.get(`/user/admin/${authStore.user.id}`);
    const isAdminUser = response.data.isAdmin;
    
    post.value.userId = authStore.user.id;
    post.value.writer = authStore.user.name;

    // 관리자인 경우 자동으로 공지 태그 설정
    if (isAdminUser) {
      post.value.tag = "공지";
      post.value.isNotice = true;
    }
  } catch (error) {
    console.error("관리자 권한 확인 실패:", error);
  }
});

const handleImageChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    post.value.postImage = file;
  }
};

const submitPost = async () => {
  try {
    const isAuth = await authStore.checkAuth();
    if (!isAuth) {
      needLoginAlert.value = true;
      return;
    }

    // 초성 데이터 추가
    const postData = {
      userId: post.value.userId,
      writer: post.value.writer,
      tag: post.value.tag,
      title: post.value.title,
      content: post.value.content,
      isNotice: post.value.isNotice, // 공지사항 여부 전달
      choseong: {
        titleChoseong: getChoseong(post.value.title.replace(/\s+/g, "")),
        contentChoseong: getChoseong(post.value.content.replace(/\s+/g, "")),
        writerChoseong: getChoseong(post.value.writer.replace(/\s+/g, "")),
      },
    };
    const response = await boardStore.addBoard(postData);
    const boardId = response.id; // 서버에서 반환한 게시글 ID

    // 이미지가 있는 경우에만 이미지 업로드를 진행합니다
    const file = fileInput.value?.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        alert("파일 크기는 5MB 이하여야 합니다.");
        return;
      }
      try {
        const formData = new FormData();
        formData.append("image", file);
        await http.post(`/board/${boardId}/post-image`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        console.log("이미지 업로드 성공");
      } catch (error) {
        console.error("이미지 업로드 실패:", error);
        alert("이미지 업로드에 실패했습니다.");
      }
    }

    alert("게시글이 등록되었습니다.");
    await boardStore.fetchBoards();
    router.push({
      path: "/community",
      query: { page: 1 },
    });
  } catch (error) {
    console.error("Error posting:", error);
    if (error.response?.status === 401) {
      needLoginAlert.value = true;
    } else {
      alert(error.response?.data?.message || "게시글 등록에 실패했습니다.");
    }
  }
};

const showDropdown = ref(false);

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
};

const selectTag = (tag) => {
  if (!isAdmin.value) {
    post.value.tag = tag;
    post.value.isNotice = false;
    showDropdown.value = false;
  }
};

// 드롭다운 외부 클릭 감지
const handleClickOutside = (event) => {
  if (showDropdown.value) {
    showDropdown.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>

<!-- style 부분 추가 -->
<style scoped>
/* 슬라이드 + 페이드 효과 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

/* 또는 확장 효과 */

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: top;
}

.dropdown-enter-from,
.dropdown-leave-to {
  transform: scaleY(0);
  opacity: 0;
}

/* 또는 슬라이드 + 줌 효과 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-enter-from,
.dropdown-leave-to {
  transform: translateY(-10px) scale(0.95);
  opacity: 0;
}

/* 스크롤바 스타일링 (overflow-y-scroll로 변경하여 항상 보이게 함) */
.absolute::-webkit-scrollbar {
  width: 8px;
}

.absolute::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.absolute::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.absolute::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
