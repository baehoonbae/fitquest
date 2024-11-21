import http from './http';

export const searchBlog = async (query, start = 1, display = 20) => {
    try {
        const response = await http.get(`/article/search/blog`, {
            params: {
                query,
                start,
                display
            }
        });
        return response.data;
    } catch (error) {
        console.error('블로그 검색 실패:', error);
        throw error;
    }
}; 

export const searchImage = async (query) => {
    try {
        const response = await http.get(`/article/search/image`, {
            params: {
                query: query
            }
        });
        return response.data;
    } catch (error) {
        console.error('이미지 검색 실패:', error);
        throw error;
    }
};
