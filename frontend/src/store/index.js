import {createStore} from "vuex";
import createPersistedState from 'vuex-persistedstate';

const store = createStore({
    state: {
        accessToken: null
    },
    getters: {
        getAccessToken: (state) => () => {
            return state.accessToken;
        },
        isAccessTokenValid: (state) => () => {
            if (!state.accessToken) {
                return false;
            }
            const decodedToken = decodeJwtToken(state.accessToken)
            const currentTime = Date.now() / 1000;
            return decodedToken.exp > currentTime;
        },
        getNickname: (state) => () => {
            if (!state.accessToken) {
                return false;
            }
            try {
                const decodedToken = decodeJwtToken(state.accessToken);
                if (!decodedToken.nickname) {
                    return false;
                }
                return decodedToken.nickname;
            } catch (error) {
                console.error('Error decoding nickname:', error);
                return false;
            }
        },
        getRole: (state) => () => {
            if (!state.accessToken) {
                return false;
            }
            try {
                const decodedToken = decodeJwtToken(state.accessToken)
                return decodedToken.role
            } catch (error) {
                return false;
            }
        }
    },
    mutations: {
        setAccessToken(state, accessToken) {
            state.accessToken = accessToken;
        },
        clearData(state) {
            state.accessToken = "";
        }
    },
    plugins: [ createPersistedState({
        paths: ['accessToken']
    })]
})

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

export default store;
