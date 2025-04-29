import store from "@/store";
import router from "@/router";
import {refreshTokenAPI} from "@/api/user.js";

const handleError = (error) => {
    const status = error.response?.status;

    switch (status) {
        case 502:
            window.location.href = '/502.html';
            break;
    }

    return Promise.reject(error);
};

const setInterceptors = function (instance) {

    instance.interceptors.response.use(
        (response) => {
            return response;
        },
        (error) => {
            return handleError(error)
        }
    );

    return instance
}

const setAuthInterceptors = function (instance) {
    instance.interceptors.request.use(
        async (config) => {
            const token = store.state.accessToken
            if (token) {
                const decodedToken = decodeJwtToken(token);
                const currentTime = Date.now() / 1000;  // 현재 시간(초 단위)
                if (decodedToken.exp < currentTime) {
                    try {
                        const response = await refreshTokenAPI();
                        const newToken = response.headers.access.split(" ")[1];
                        store.commit('setAccessToken', newToken);
                        config.headers.Authorization = 'Bearer ' + newToken;
                    } catch (error) {
                        store.commit('clearData');
                        alert('세션이 만료되었습니다.');
                        await router.push('/');
                        return Promise.reject('Token expired');
                    }
                } else {
                    // 토큰이 유효하면 Authorization 헤더에 추가
                    config.headers.Authorization = 'Bearer ' + token;
                }
            }

            return config;
        },
        (error) => {
            return Promise.reject(error)
        }
    )

    instance.interceptors.response.use(
        (response) => {
            return response;
        },
        async (error) => {
            const originalRequest = error.config;
            if (error.response.status === 401) {

                try {
                    const response = await refreshTokenAPI();
                    const newToken = response.headers.access.split(" ")[1];
                    store.commit('setAccessToken', newToken);
                    originalRequest.headers.Authorization = 'Bearer ' + newToken;
                    return instance(originalRequest);
                } catch (refreshError) {
                    store.commit('clearData');
                    alert('세션이 만료되었습니다.');
                    await router.push('/');
                    return Promise.reject('Token expired');
                }
            }

            return handleError(error);
        }
    );
    return instance
}

// JWT 디코딩 유틸리티 함수
function base64UrlDecode(base64Url) {
    // Base64Url을 Base64로 변환
    let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

    // Base64로 디코딩
    let decoded = atob(base64);

    // UTF-8 문자열로 변환
    try {
        return decodeURIComponent(escape(decoded));
    } catch (e) {
        console.error('디코딩 오류:', e);
        return null;
    }
}

// JWT에서 페이로드 추출 및 디코딩
function decodeJwtToken(token) {
    // JWT를 '.' 기준으로 분리
    let parts = token.split('.');

    if (parts.length !== 3) {
        throw new Error('잘못된 JWT 형식');
    }

    // 두 번째 부분 (페이로드) 디코딩
    let payload = parts[1];

    return JSON.parse(base64UrlDecode(payload));
}


export {setInterceptors, setAuthInterceptors}
