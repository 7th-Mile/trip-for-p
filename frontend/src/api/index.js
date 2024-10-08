import axios from "axios";
import {setInterceptors} from "@/api/interceptor";

const instance = axios.create();

const createAuthInstance = function () {
    const instance = axios.create();
    return setInterceptors(instance);
};

const authInstance = createAuthInstance();

const getPlanListAPI = function (request) {
    return instance.get(
        `/api/plans?area=${request.area}&size=${request.size}&page=${request.page}`)
}

const processAlanAPI = function (request) {
    return instance.get(
        `/api/alan?content=${request.content}&client_id=${request.clientId}`);
}

const getPlanAPI = function (id) {
    return instance.get(`/api/plans/${id}`);
};

const createPlanAPI = function (request) {
    return authInstance.post(`/api/plans`, request);
}

const createUserAPI = async function (request) {
    //return instance.post(`/api/users/registration`, request);
    try {
        const response = await instance.post(`/api/users/registration`,
            request);
        return response.data;
    } catch (error) {
        console.log("회원가입", error);
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}

const loginAPI = function (formData) {
    return instance.post(`/api/users/signin`, formData,
        {
            headers: {
                "Content-Type": 'multipart/form-data'
            }
        })
};

//[회원가입, 비밀번호 재설정] 인증코드 이메일 전송
const sendVerificationEmailAPI = async function (request) {
    try {
        const response = await instance.post(`/api/mails/send-verification`,
            request);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}

//[회원가입, 비밀번호 재설정] 인증코드 검증
const verifyEmailAPI = async function (request) {
    try {
        const response = await instance.post(`/api/mails/verification`,
            request);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}
//[회원가입, 개인정보수정] 닉네임 중복 검사
const verifyNickNameAPI = async function (request) {
    var nickname = request
    try {
        const response = await instance.get(`/api/users/nickname-verification`,
            {
                params: {nickname}
            });
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}

const getMagazineListAPI = function (request) {
    return instance.get(
        `/api/magazines?keyword=&size=${request.size}&page=${request.page}`);
}

const getPopularPlaceListAPI = function () {
    return instance.get(`/api/plans/popular-places`);
};

const getPopularPlanListAPI = function () {
    return instance.get(`/api/plans/popular-plans`);
};

const getFreePostListAPI = function (request) {
    return instance.get(
        `/api/free-posts?keyword=${request.keyword}&size=${request.size}&page=${request.page}`);
}
//[마이페이지]작성한 여행코스 글 목록 조회
const getMyPlanCourseListAPI = function(request){
    return authInstance.get(`/api/plans/me`, {
        params: {
            size: request.size,
            page: request.page
        }
    });
}
//[마이페이지]작성한 자유게시글 목록 조회
const getMyFreePostListAPI = function(request){
    return authInstance.get(`/api/free-posts/me`, {
        params: {
            size: request.size,
            page: request.page
        }
    });
}
//[마이페이지]작성한 리뷰게시글 목록 조회
const getMyReviewListAPI = function(request){
    return authInstance.get('/api/review-posts/me', {
        params: {
            size: request.size,
            page: request.page
        }
    });
}
//[마이페이지]작성한 댓글-자유게시글 목록 조회
const getMyFreePostCommentListAPI = function(request){
    return authInstance.get(`/api/free-posts/1/comments/me`, {
        params: {
            size: request.size,
            page: request.page
        }
    });
}
//[마이페이지]작성한 댓글-리뷰게시글 목록 조회
const getMyReviewCommentListAPI = function(request){
    return authInstance.get('/api/review-posts/1/comments/me', {
        params: {
            size: request.size,
            page: request.page
        }
    });
}
//[마이페이지]좋아요 여행코스 목록 조회
const getMyLikedPlansAPI = function(request){
    return authInstance.get('/api/plan-likes/me', {
        params: {
            size: request.size,
            page: request.page
        }
    });
}
//[마이페이지]회원 탈퇴
const withdrawalUserAPI = function(){
    return authInstance.patch('/api/users/deletion');
}
const createFreePostAPI = function (request) {
    return authInstance.post(`/api/free-posts`, request);
};

const updateFreePostAPI = function (id, request) {
    return authInstance.put(`/api/free-posts/${id}`, request)
};

const deleteFreePostAPI = function (id) {
    return authInstance.delete(`/api/free-posts/${id}`);
};

const getFreePostAPI = function (id) {
    return instance.get(`/api/free-posts/${id}`);
};

const createFreeCommentAPI = function (request) {
    return authInstance.post(`/api/free-posts/${request.postId}/comments`,
        request);
};

const updateFreeCommentAPI = function (id, request) {
    return authInstance.put(
        `/api/free-posts/${request.postId}/comments/${id}`,
        request);
};

const deleteFreeCommentAPI = function (id, postId) {
    return authInstance.delete(`/api/free-posts/${postId}/comments/${id}`);
};

const getFreeCommentListAPI = function (request) {
    return instance.get(
        `/api/free-posts/${request.postId}/comments?&sort=createdAt,desc&size=${request.size}&page=${request.page}`)
}

const createReviewCommentAPI = function (request) {
    return authInstance.post(`/api/review-posts/${request.postId}/comments`,
        request);
};

const updateReviewCommentAPI = function (id, request) {
    return authInstance.put(
        `/api/review-posts/${request.postId}/comments/${id}`,
        request);
};

const deleteReviewCommentAPI = function (id, postId) {
    return authInstance.delete(`/api/review-posts/${postId}/comments/${id}`);
};

const getReviewCommentListAPI = function (request) {
    return instance.get(
        `/api/review-posts/${request.postId}/comments?&sort=createdAt,desc&size=${request.size}&page=${request.page}`)
}

const likePlanAPI = function (request) {
    return authInstance.post(`/api/plan-likes`, request);
};

const checkPlanLikeAPI = function (planId) {
    return authInstance.get(`/api/plan-likes/check?planId=${planId}`);
};

const updatePlanAPI = function (id, request) {
    return authInstance.put(`/api/plans/${id}`, request);
};

const deletePlanAPI = function (id) {
    return authInstance.delete(`/api/plans/${id}`);
};

//[비밀번호 재설정]이메일 인증코드 전송
const sendPasswordResetEmailAPI = async function (request) {
    try {
        const response = await instance.post(
            `/api/mails/password-reset-request`,
            request);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}
//[비밀번호 재설정]비밀번호 재설정
const resetPasswordAPI = async function (request) {
    try {
        const response = await instance.post(`/api/users/password/renewal`,
            request);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}

const createMagazineAPI = function (formData) {
    return authInstance.post(`/api/magazines`, formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
}

const updateMagazineAPI = function (id, formData) {
    return authInstance.put(`/api/magazines/${id}`, formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
}

const getMagazineAPI = function (id) {
    return instance.get(`/api/magazines/${id}`);
}

const deleteMagazineAPI = function (id) {
    return authInstance.delete(`/api/magazines/${id}`);
};

const getReviewPostListAPI = function (request) {
    return instance.get(`/api/review-posts?keyword=${request.keyword}&size=${request.size}&page=${request.page}`);
};

const getReviewPostAPI = function (id) {
    return instance.get(`/api/review-posts/${id}`);
};

const createReviewPostAPI = function (formData) {
    return authInstance.post(`/api/review-posts`, formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
}

const updateReviewPostAPI = function (id, formData) {
    return authInstance.put(`/api/review-posts/${id}`, formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
}

const deleteReviewPostAPI = function (id) {
    return authInstance.delete(`/api/review-posts/${id}`);
};

const getMyPlanListAPI = function (request) {
    return authInstance.get(`/api/plans/me?size=${request.size}&page=${request.page}`);
};

const refreshTokenAPI = function () {
    return instance.post(`/api/users/reissue`)
};

const getUserInfoAPI = function () {
    return authInstance.get(`/api/users/me`);
};

const getAIUserAPI = function (clientId) {
    return authInstance.get(`/api/alan/user?client_id=${clientId}`);
}

const getAIInfoAPI = function (request) {
    return instance.get(
        `/api/alan/area?client_id=${request.clientId}&area=${request.area}&startDate=${request.startDate}&endDate=${request.endDate}`);
}

export {
    getPopularPlanListAPI,
    getPopularPlaceListAPI,
    processAlanAPI,
    createPlanAPI,
    createUserAPI,
    loginAPI,
    getPlanAPI,
    getPlanListAPI,
    sendVerificationEmailAPI,
    verifyEmailAPI,
    verifyNickNameAPI,
    getMagazineListAPI,
    getFreePostListAPI,
    createFreePostAPI,
    getFreePostAPI,
    getFreeCommentListAPI,
    createFreeCommentAPI,
    updateFreeCommentAPI,
    deleteFreeCommentAPI,
    getReviewCommentListAPI,
    createReviewCommentAPI,
    updateReviewCommentAPI,
    deleteReviewCommentAPI,
    updateFreePostAPI,
    deleteFreePostAPI,
    likePlanAPI,
    checkPlanLikeAPI,
    updatePlanAPI,
    deletePlanAPI,
    sendPasswordResetEmailAPI,
    resetPasswordAPI,
    createMagazineAPI,
    getMagazineAPI,
    deleteMagazineAPI,
    updateMagazineAPI,
    getMyFreePostListAPI,
    getReviewPostAPI,
    createReviewPostAPI,
    updateReviewPostAPI,
    getReviewPostListAPI,
    deleteReviewPostAPI,
    getMyPlanListAPI,
    getMyPlanCourseListAPI,
    getMyReviewListAPI,
    getMyFreePostCommentListAPI,
    getMyReviewCommentListAPI,
    getMyLikedPlansAPI,
    withdrawalUserAPI,
    refreshTokenAPI,
    getUserInfoAPI,
    getAIUserAPI,
    getAIInfoAPI
}
