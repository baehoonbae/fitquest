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
      meta: { hideLayout: true },
    },
    {
      path: "/login",
      name: "login",
      component: UserLoginView,
      meta: { hideLayout: true },
    },
    {
      path: "/config",
      name: "config",
      component: UserConfigView,
    },
    {
      path: "/community",
      name: "community",
      component: CommunityHomeView,
    },
    {
      path: "/community",
      name: "CommunityHome",
      component: CommunityHomeView,
    },
    {
      path: "/community/detail/:id",
      name: "CommunityDetail",
      component: CommunityDetail,
    },
    {
      path: "/community/write",
      name: "CommunityWrite",
      component: () => import("@/views/CommunityWrite.vue"),
    },
    {
      path: "/community/edit/:id",
      name: "CommunityUpdate",
      component: () => import("@/views/CommunityUpdate.vue"),
    },
    {
      path: "/news",
      name: "news",
      component: NewsHomeView,
    },
  ],
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.user.isAuthenticated) {
    next({ name: "root" });
    return;
  }

  if (
    authStore.user.isAuthenticated &&
    (to.name === "login" || to.name === "signup")
  ) {
    next({ name: "userHome" });
    return;
  }

  next();
});

export default router;
