import { createRouter, createWebHistory } from "vue-router";
import GuestHomeView from "../views/GuestHomeView.vue";
import UserRegistView from "@/views/UserRegistView.vue";
import UserLoginView from "@/views/UserLoginView.vue";
import { useAuthStore } from "@/stores/auth";
import UserHomeView from "@/views/UserHomeView.vue";
import UserConfigView from "@/views/UserConfigView.vue";
import CommunityHomeView from "@/views/CommunityHomeView.vue";
import NewsHomeView from "@/views/NewsHomeView.vue";
import CommunityDetail from "@/views/CommunityDetail.vue";
import CommunityWrite from "@/views/CommunityWrite.vue";
import CommunityUpdate from "@/views/CommunityUpdate.vue";
import CategoryRegistView from "@/views/CategoryRegistView.vue";
import CategoryManageView from "@/views/CategoryManageView.vue";
import CategoryUpdateView from "@/views/CategoryUpdateView.vue";
import CommunitySearch from "@/components/CommunitySearch.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "root",
      component: GuestHomeView,
      meta: { hideLayout: true },
      beforeEnter: (to, from, next) => {
        const authStore = useAuthStore();
        if (authStore.user.isAuthenticated) {
          next({ name: "userHome" });
        } else {
          next();
        }
      },
    },
    {
      path: "/home",
      name: "userHome",
      component: UserHomeView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: "/signup",
      name: "signup",
      component: UserRegistView,
      meta: {   
        hideLayout: true,
        title: '회원가입',
      },
    },
    {
      path: "/login",
      name: "login",
      component: UserLoginView,
      meta: {
        hideLayout: true,
        title: '로그인',
      },
    },
    {
      path: "/config",
      name: "config",
      component: UserConfigView,
      meta: {
        requiresAuth: true,
        title: '설정',
      },
    },
    {
      path: "/community",
      name: "community",
      component: CommunityHomeView,
      meta: {
        title: '커뮤니티',
      },
    },
    {
      path: "/community",
      name: "CommunityHome",
      component: CommunityHomeView,
      meta: {
        title: '커뮤니티',
      },
    },
    {
      path: "/community/detail/:id",
      name: "CommunityDetail",
      component: CommunityDetail,
      meta: {
        title: '커뮤니티',
      },
    },
    {
      path: "/community/write",
      name: "CommunityWrite",
      component: CommunityWrite,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: "/community/edit/:id",
      name: "CommunityUpdate",
      component: CommunityUpdate,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: "/news",
      name: "news",
      component: NewsHomeView,
      meta: {
        title: CommunitySearch,
      },
    },
    {
      path: "/category-regist",
      name: "CategoryRegist",
      component: CategoryRegistView,
      meta: {
        requiresAuth: true,
        title: '카테고리 등록',
      },
    },
    {
      path: "/category-manage",
      name: "CategoryManage",
      component: CategoryManageView,
      meta: {
        requiresAuth: true,
        title: '카테고리 관리',
      },
    },
    {
      path: "/category-update/:id",
      name: "CategoryUpdate",
      component: CategoryUpdateView,
      meta: {
        requiresAuth: true,
        title: '카테고리 수정',
      },
    },
  ],
});

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth) {
    try {
      const hasRefreshToken = await authStore.checkRefreshTokenExists();     
      if (!hasRefreshToken) {
        authStore.logout();
        next({ name: 'login' });
        return;
      }

      // 액세스 토큰 체크 및 갱신
      const accessToken = authStore.getToken();
      if (!accessToken || !authStore.validateAccessToken()) {
        const hasRefreshToken = await authStore.checkRefreshTokenExists();
        if (!hasRefreshToken) {
          authStore.logout();
          next({ name: 'login' });
          return;
        }
      }

      next(); // 인증 성공시 다음으로 진행
    } catch (error) {
      console.error('Auth check failed:', error);
      authStore.logout();
      next({ name: 'login' });
    }
  } else {
    // 인증이 필요없는 페이지는 바로 진행
    next();
  }
});

export default router;
