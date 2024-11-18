// stores/category.js
import { defineStore } from "pinia";
import { ref } from "vue";
import axios from "axios";
import { useRouter } from 'vue-router';
import { useAuthStore } from "./auth";

export const useCategoryStore = defineStore("category", () => {
    const router = useRouter();
    const categories = ref([]);
    const category = ref({});
    const authStore = useAuthStore();

    // 카테고리 목록 조회
    const fetchCategories = async () => {
        const userId = authStore.user.id;
        const accessToken = authStore.getToken();

        try {
            const response = await axios.get(`http://localhost:8097/fitquest/api/category/${userId}`, {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            });
            if (response.data) {
                categories.value = response.data;
            } else {
                console.log('카테고리 조회 실패:', response.data);
            }
        } catch (error) {
            console.error('카테고리 조회 실패:', error.response?.data || error.message);
            if (error.response?.status === 401) {
                console.error('인증 토큰이 만료되었거나 유효하지 않습니다');
            }
        }
    }

    const fetchCategory = async (categoryId) => {
        const userId = authStore.user.id;
        const accessToken = authStore.getToken();

        try {   
            const response = await axios.get(`http://localhost:8097/fitquest/api/category/${userId}/${categoryId}`, {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            });
            if (response.data) {
                category.value = response.data;
            } else {
                console.log('카테고리 조회 실패:', response.data);
            }
        } catch (error) {
            console.error('카테고리 조회 실패:', error.response?.data || error.message);
            if (error.response?.status === 401) {
                console.error('인증 토큰이 만료되었거나 유효하지 않습니다');
            }
        }
    }
    return {
        categories,
        category,
        fetchCategories,
        fetchCategory,
    };
});
