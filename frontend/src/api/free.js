import {authInstance, instance} from "@/api/instance";

/**
 * FreePost API
 */
// 자유게시글 등록
const createFreePostAPI = function (request) {
    return authInstance.post(`/api/free-posts`, request);
};

// 자유게시글 수정
const updateFreePostAPI = function (id, request) {
    return authInstance.put(`/api/free-posts/${id}`, request)
};

// 자유게시글 삭제
const deleteFreePostAPI = function (id) {
    return authInstance.delete(`/api/free-posts/${id}`);
};

// 자유게시글 단일 조회
const getFreePostAPI = function (id) {
    return instance.get(`/api/free-posts/${id}`);
};

// 자유게시글 목록 조회
const getFreePostListAPI = function (request) {
    return instance.get(
        `/api/free-posts`, {
            params: {
                keyword: request.keyword,
                size: request.size,
                page: request.page
            }
        });
}

// 나의 자유게시글 목록 조회
const getMyFreePostListAPI = function (request) {
    return authInstance.get(`/api/free-posts/me`, {
        params: {
            size: request.size,
            page: request.page
        }
    });
}
/**
 * FreeComment API
 */
// 자유댓글 등록
const createFreeCommentAPI = function (request) {
    return authInstance.post(`/api/free-posts/${request.postId}/comments`,
        request);
};

// 자유댓글 수정
const updateFreeCommentAPI = function (id, request) {
    return authInstance.put(
        `/api/free-posts/${request.postId}/comments/${id}`,
        request);
};

// 자유댓글 삭제
const deleteFreeCommentAPI = function (id, postId) {
    return authInstance.delete(`/api/free-posts/${postId}/comments/${id}`);
};

// 자유댓글 목록 조회
const getFreeCommentListAPI = function (request) {
    return instance.get(
        `/api/free-posts/${request.postId}/comments`, {
            params: {
                sort: 'createdAt,desc',
                size: request.size,
                page: request.page
            }
        })
}

// 나의 자유댓글 목록 조회
const getMyFreePostCommentListAPI = function (request) {
    return authInstance.get(`/api/free-posts/1/comments/me`, {
        params: {
            size: request.size,
            page: request.page
        }
    });
}

export {
    createFreePostAPI,
    updateFreePostAPI,
    deleteFreePostAPI,
    getFreePostAPI,
    getFreePostListAPI,
    getMyFreePostListAPI,
    createFreeCommentAPI,
    updateFreeCommentAPI,
    deleteFreeCommentAPI,
    getFreeCommentListAPI,
    getMyFreePostCommentListAPI
}
