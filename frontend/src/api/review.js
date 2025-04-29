import {authInstance, instance} from "@/api/instance";

// 리뷰게시글 등록
const createReviewPostAPI = function (formData) {
    return authInstance.post(`/api/review-posts`, formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
}

// 리뷰게시글 수정
const updateReviewPostAPI = function (id, formData) {
    return authInstance.put(`/api/review-posts/${id}`, formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
}

// 리뷰게시글 삭제
const deleteReviewPostAPI = function (id) {
    return authInstance.delete(`/api/review-posts/${id}`);
};

// 리뷰게시글 단일 조회
const getReviewPostAPI = function (id) {
    return instance.get(`/api/review-posts/${id}`);
};

// 리뷰게시글 목록 조회
const getReviewPostListAPI = function (request) {
    return instance.get(
        `/api/review-posts`, {
            params: {
                keyword: request.keyword,
                size: request.size,
                page: request.page
            }
        });
};

// 나의 리뷰게시글 목록 조회
const getMyReviewListAPI = function (request) {
    return authInstance.get('/api/review-posts/me', {
        params: {
            size: request.size,
            page: request.page
        }
    });
}

// 리뷰댓글 등록
const createReviewCommentAPI = function (request) {
    return authInstance.post(`/api/review-posts/${request.postId}/comments`,
        request);
};

// 리뷰댓글 수정
const updateReviewCommentAPI = function (id, request) {
    return authInstance.put(
        `/api/review-posts/${request.postId}/comments/${id}`,
        request);
};

// 리뷰댓글 삭제
const deleteReviewCommentAPI = function (id, postId) {
    return authInstance.delete(`/api/review-posts/${postId}/comments/${id}`);
};

// 리뷰댓글 목록 조회
const getReviewCommentListAPI = function (request) {
    return instance.get(
        `/api/review-posts/${request.postId}/comments`, {
            params: {
                sort: 'createdAt,desc',
                size: request.size,
                page: request.page
            }
        })
}

// 나의 리뷰댓글 목록 조회
const getMyReviewCommentListAPI = function (request) {
    return authInstance.get('/api/review-posts/1/comments/me', {
        params: {
            size: request.size,
            page: request.page
        }
    });
}

export {
    createReviewPostAPI,
    updateReviewPostAPI,
    deleteReviewPostAPI,
    getReviewPostAPI,
    getReviewPostListAPI,
    getMyReviewListAPI,
    createReviewCommentAPI,
    updateReviewCommentAPI,
    deleteReviewCommentAPI,
    getReviewCommentListAPI,
    getMyReviewCommentListAPI
}
