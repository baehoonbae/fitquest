// stores/auth.js
import { defineStore } from "pinia";
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";
import http from "@/api/http";

export const useAuthStore = defineStore("auth", () => {
  const router = useRouter();
  const user = ref({
    id: null,
    email: null,
    name: "",
    isAuthenticated: false,
  });

  const getToken = () => sessionStorage.getItem('accessToken');

  // 로그인 액션
  const login = async (loginData) => {
    try {
      const response = await http.post(
        "/user/login",
        loginData,
        { withCredentials: true }
      );

      const data = response.data;

      if (!data.accessToken) {
        throw new Error('로그인 응답에 토큰이 없습니다.');
      }

      // 유저 정보 저장
      user.value = {
        id: data.id,
        email: data.email,
        name: data.name,
        isAuthenticated: true,
      };

      // 토큰 설정
      sessionStorage.setItem("accessToken", data.accessToken);
      axios.defaults.headers.common["Authorization"] = `Bearer ${data.accessToken}`;

      return { success: true };

    } catch (error) {
      let errorMessage = '로그인 처리 중 오류가 발생했습니다.';
      if (error.response) {
        if (error.response.status === 401) {
          errorMessage = '아이디 또는 비밀번호가 올바르지 않습니다.';
        } else {
          errorMessage = error.response.data.message || '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
        }
      } else if (error.request) {
        errorMessage = '서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.';
      }

      return { success: false, error: errorMessage };
    }
  };

  // 로그아웃
  const logout = () => {
    user.value = {
      id: null,
      email: null,
      name: "",
      isAuthenticated: false,
    };
    sessionStorage.clear();
    delete axios.defaults.headers.common["Authorization"];
    router.push('/');
  };

  // 인증 상태 체크 (페이지 새로고침 시 호출)
  const checkAuth = () => {
    const accessToken = getToken();
    if (accessToken && user.value.isAuthenticated) {
      // 토큰이 있고 user가 인증된 상태면 헤더만 다시 설정
      http.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
      return true;
    }
    return false;
  };

  // fetchUserInfo는 필요한 경우에만 명시적으로 호출
  // 예: 프로필 업데이트 후 최신 정보 가져오기
  const fetchUserInfo = async () => {
    const accessToken = getToken();
    if (!accessToken || !user.value.id) return;

    try {
      const response = await http.get(
        `/user/${user.value.id}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );

      if (response.data) {
        user.value = {
          ...response.data,
          isAuthenticated: true,
        };
      }
    } catch (error) {
      console.error("유저 정보 조회 실패:", error);
      if (error.response?.status === 401) {
        logout();
      }
    }
  };

  // 회원가입
  const regist = async (userData) => {
    try {
      await http.post('/user/regist', userData);
      return { success: true, message: '회원가입이 완료되었습니다.' };
    } catch (error) {
      return {
        success: false,
        message: '회원가입에 실패하였습니다.',
        error: error.response?.data?.message || '서버 오류가 발생했습니다.'
      };
    }
  };

  // 닉네임 중복 체크
  const checkDuplicateName = async (name) => {
    try {
      const response = await http.get(
        `/user/check-name/${name}`,
        {
          validateStatus: (status) => status >= 200 && status < 300 || status === 409
        }
      );
      return {
        isDuplicated: response.status === 409,
        message: response.status === 409 ? '사용할 수 없는 닉네임입니다.' : '사용 가능한 닉네임입니다.'
      };
    } catch (error) {
      console.error('닉네임 중복 체크 실패:', error);
      return { isDuplicated: true, message: '중복 확인 중 오류가 발생했습니다.' };
    }
  };

  // 이메일 중복 체크
  const checkDuplicateEmail = async (email) => {
    try {
      const response = await http.get(
        `/user/check-email/${email}`,
        {
          validateStatus: (status) => status >= 200 && status < 300 || status === 409
        }
      );
      return {
        isDuplicated: response.status === 409,
        message: response.status === 409 ? '사용할 수 없는 아이디입니다.' : '사용 가능한 아이디입니다.'
      };
    } catch (error) {
      console.error('이메일 중복 체크 실패:', error);
      return { isDuplicated: true, message: '중복 확인 중 오류가 발생했습니다.' };
    }
  };

  return {
    user,
    login,
    logout,
    checkAuth,
    fetchUserInfo,
    getToken,
    regist,
    checkDuplicateName,
    checkDuplicateEmail,
  };
}, {
  persist: {
    storage: sessionStorage,  // 브라우저 닫으면 삭제
    paths: ['user']  // user 상태만 저장
  }
});
