import {authInstance, instance} from "@/api/instance";

// 회원가입
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

// 닉네임 중복 체크
const verifyNickNameAPI = async function (request) {
    const nickname = request
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

// 로그인
const loginAPI = function (formData) {
    return instance.post(`/api/users/signin`, formData,
        {
            headers: {
                "Content-Type": 'multipart/form-data'
            }
        })
};

const logoutAPI = function () {
    return instance.post(`/api/users/signout`);
}

// 회원 탈퇴
const withdrawUserAPI = function () {
    return authInstance.patch('/api/users/deletion');
}

// 회원 수정
const updateUserInfoAPI = function (request) {
    return authInstance.patch(`/api/users/me`, request)
}

// 리프레시토큰 재발급
const refreshTokenAPI = function () {
    return instance.post(`/api/users/reissue`)
};

// 나의 정보 조회
const getUserInfoAPI = function () {
    return authInstance.get(`/api/users/me`);
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

export {
    createUserAPI,
    verifyNickNameAPI,
    loginAPI,
    withdrawUserAPI,
    refreshTokenAPI,
    getUserInfoAPI,
    sendPasswordResetEmailAPI,
    resetPasswordAPI,
    sendVerificationEmailAPI,
    verifyEmailAPI,
    logoutAPI,
    updateUserInfoAPI
}


