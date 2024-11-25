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
import OtherUserHomeView from "@/views/OtherUserHomeView.vue";
import { useLoadingStore } from "@/stores/loading";

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
      path: "/home/:userId",
      name: "userHomeDetail",
      component: OtherUserHomeView,
    },
    {
      path: "/signup",
      name: "signup",
      component: UserRegistView,
      meta: {
        hideLayout: true,
        title: "회원가입",
      },
    },
    {
      path: "/login",
      name: "login",
      component: UserLoginView,
      meta: {
        hideLayout: true,
        title: "로그인",
      },
    },
    {
      path: "/config",
      name: "config",
      component: UserConfigView,
      meta: {
        requiresAuth: true,
        title: "설정",
      },
    },
    {
      path: "/community",
      name: "community",
      component: CommunityHomeView,
      meta: {
        title: "커뮤니티",
      },
    },
    {
      path: "/community",
      name: "CommunityHome",
      component: CommunityHomeView,
      meta: {
        title: "커뮤니티",
      },
    },
    {
      path: "/community/detail/:id",
      name: "CommunityDetail",
      component: CommunityDetail,
      meta: {
        title: "커뮤니티",
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
        title: "카테고리 등록",
      },
    },
    {
      path: "/category-manage",
      name: "CategoryManage",
      component: CategoryManageView,
      meta: {
        requiresAuth: true,
        title: "카테고리 관리",
      },
    },
    {
      path: "/category-update/:id",
      name: "CategoryUpdate",
      component: CategoryUpdateView,
      meta: {
        requiresAuth: true,
        title: "카테고리 수정",
      },
    },
  ],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }
    if (to.hash) {
      return { el: to.hash };
    }
    return { top: 0 };
  },
});

router.beforeEach(async (to, from, next) => {
  const loadingStore = useLoadingStore();
  const authStore = useAuthStore();

  loadingStore.show(true); // 라우트 전환임을 표시
  const isAuth = await authStore.checkAuth();
  if ((to.name === "login" || to.name === "signup") && isAuth) {
    next({ name: "userHome" });
  } else if (to.meta.requiresAuth) {
    try {
      if (!isAuth) {
        authStore.logout();
        next({ name: "login" });
        return;
      } else {
        if (to.path === "/login" || to.path === "/signup") {
          next({ name: "userHome" });
        }
      }
      next();
    } catch (error) {
      console.error("Auth check failed:", error);
      authStore.logout();
      next({ name: "login" });
    }
  } else {
    next();
  }
});

router.afterEach(() => {
  const loadingStore = useLoadingStore();
  setTimeout(() => {
    loadingStore.hide();
  }, 300);
});

export default router;
