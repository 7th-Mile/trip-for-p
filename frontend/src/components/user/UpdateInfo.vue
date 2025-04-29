<script setup>
import {computed, onMounted, ref, watch} from 'vue';
import {getUserInfoAPI, updateUserInfoAPI, verifyNickNameAPI} from "@/api/user";

const password = ref('');
const passwordCheck = ref('');
const nickname = ref('');
const userInfo = ref(null);
const errorMessage = ref('');
const isNicknameVerified = ref(false);
const nicknameVerificationMessage = ref('');
const isNicknameVerificationFailed = ref(false);
const isPasswordValid = ref(true);
const passwordVerificationMessage = ref('');

const getUserInfo = async () => {
    try {
        const response = await getUserInfoAPI();
        return response.data;
    } catch (error) {
        console.error('API 호출 에러:', error);
        throw new Error('Error fetching user info');
    }
};

onMounted(async () => {
    try {
        const data = await getUserInfo();
        userInfo.value = data;
        nickname.value = data.nickname;
    } catch (error) {
        errorMessage.value = error.message;
    }
});

const verifyNickname = async () => {
    try {
        const response = await verifyNickNameAPI(nickname.value);
        if (response.status !== 'success') {
            isNicknameVerified.value = false;
            nicknameVerificationMessage.value = '이미 사용 중인 닉네임입니다.';
            isNicknameVerificationFailed.value = true;
        } else {
            isNicknameVerified.value = true;
            nicknameVerificationMessage.value = '사용 가능한 닉네임입니다.';
            isNicknameVerificationFailed.value = false;
        }
    } catch (error) {
        isNicknameVerified.value = false;
        nicknameVerificationMessage.value = error.message || '닉네임 중복 검사 중 오류가 발생했습니다.';
        isNicknameVerificationFailed.value = true;
    }
};

watch(nickname, (newValue, oldValue) => {
    if (newValue === '') {
        nicknameVerificationMessage.value = '';
        isNicknameVerified.value = false;
        isNicknameVerificationFailed.value = false;
    } else if (newValue !== oldValue) {
        isNicknameVerified.value = false;
        isNicknameVerificationFailed.value = false;
    }
});

const validatePassword = () => {
    const regex = /^(?=.*[a-z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,16}$/;
    return regex.test(password.value);
};

watch([password, passwordCheck], () => {
    if (password.value || passwordCheck.value) {
        if (!validatePassword()) {
            isPasswordValid.value = false;
            passwordVerificationMessage.value = '비밀번호는 8~16자이며 영문 소문자, 숫자를 포함해야 합니다.';
        } else if (password.value !== passwordCheck.value) {
            isPasswordValid.value = false;
            passwordVerificationMessage.value = '비밀번호가 일치하지 않습니다.';
        } else {
            isPasswordValid.value = true;
            passwordVerificationMessage.value = '';
        }
    }
});

const isFormValid = computed(() => {
    const isNicknameValid = nickname.value && isNicknameVerified.value;
    const isPasswordValid = !password.value || (password.value && passwordCheck.value
        && password.value === passwordCheck.value);
    return isNicknameValid || isPasswordValid;
});

const updateUserInfo = async () => {
    try {
        const updateUserRequest = {};

        if (nickname.value && isNicknameVerified.value && nickname.value
            !== userInfo.value.nickname) {
            updateUserRequest.nickname = nickname.value;
        }

        if (password.value) {
            if (!validatePassword()) {
                alert('비밀번호는 8~16자이며 영문 소문자, 숫자를 포함해야 합니다.');
                return;
            }
            if (password.value !== passwordCheck.value) {
                alert('비밀번호가 일치하지 않습니다.');
                return;
            }
            updateUserRequest.password = password.value;
        }

        if (Object.keys(updateUserRequest).length === 0) {
            alert('변경할 정보가 없습니다.');
            return;
        }

        await updateUserInfoAPI(updateUserRequest);

        alert('회원 정보가 수정되었습니다.');
        window.location.reload();
    } catch (error) {
        console.error('Error updating user info:', error.response || error);
        if (error.response?.data) {
            if (error.response.data.errors?.length > 0) {
                alert(error.response.data.errors[0]);
            } else {
                alert(error.response.data.message || '회원 정보 수정 중 오류가 발생했습니다.');
            }
        } else {
            alert('서버와의 통신 중 오류가 발생했습니다.');
        }
    }
};
</script>

<template>
    <div class="update-info">
        <div class="update-content">
            <h2 class="update-title">회원정보 수정</h2>

            <div class="update-form">
                <!-- 닉네임 섹션 -->
                <div class="form-section">
                    <label class="input-label">닉네임</label>
                    <div class="input-group">
                        <input
                            type="text"
                            class="update-input"
                            placeholder="닉네임을 입력하세요"
                            v-model="nickname"
                        >
                        <button
                            @click="verifyNickname"
                            :disabled="!nickname"
                            class="verification-button"
                        >
                            중복확인
                        </button>
                    </div>
                    <p v-if="nicknameVerificationMessage"
                       :class="['verification-message',
               { 'success': isNicknameVerified, 'error': isNicknameVerificationFailed }]"
                    >
                        {{ nicknameVerificationMessage }}
                    </p>
                </div>

                <!-- 비밀번호 섹션 -->
                <div class="form-section">
                    <label class="input-label">비밀번호</label>
                    <input
                        type="password"
                        class="update-input"
                        placeholder="새 비밀번호 (영문 소문자, 숫자 포함 8~16자)"
                        v-model="password"
                    >
                    <input
                        type="password"
                        class="update-input"
                        placeholder="비밀번호 확인"
                        v-model="passwordCheck"
                    >
                    <p v-if="passwordVerificationMessage"
                       :class="['verification-message',
               { 'success': isPasswordValid, 'error': !isPasswordValid }]"
                    >
                        {{ passwordVerificationMessage }}
                    </p>
                </div>

                <!-- 수정 버튼 -->
                <button
                    @click="updateUserInfo"
                    :disabled="!isFormValid"
                    class="submit-button"
                >
                    수정하기
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.update-info {
    width: 100%;
    height: 100%;
}

.update-content {
    width: 100%;
    max-width: 500px;
    margin: 0 auto;
}

.update-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #333;
    margin-bottom: 2rem;
    text-align: center;
}

.update-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.form-section {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.input-label {
    font-size: 0.9rem;
    font-weight: 600;
    color: #555;
}

.input-group {
    display: flex;
    gap: 0.5rem;
}

.update-input {
    width: 100%;
    height: 54px;
    border: 1px solid rgba(197, 204, 210, 0.8);
    border-radius: 12px;
    padding: 0 1.25rem;
    font-size: 0.95rem;
    transition: all 0.3s ease;
    background-color: rgba(255, 255, 255, 0.9);
}

.update-input:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 4px rgba(92, 106, 196, 0.1);
}

.verification-button {
    height: 54px;
    padding: 0 1.5rem;
    border-radius: 12px;
    border: none;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    white-space: nowrap;
}

.verification-button:disabled {
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.5) 0%, rgba(135, 148, 216, 0.5) 100%);
    cursor: not-allowed;
    opacity: 0.7;
}

.verification-button:not(:disabled):hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.verification-message {
    font-size: 0.85rem;
    margin-top: 0.25rem;
}

.verification-message.success {
    color: #5c6ac4;
}

.verification-message.error {
    color: #dc3545;
}

.submit-button {
    width: 100%;
    height: 54px;
    border-radius: 12px;
    border: none;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    margin-top: 1rem;
}

.submit-button:disabled {
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.5) 0%, rgba(135, 148, 216, 0.5) 100%);
    cursor: not-allowed;
    opacity: 0.7;
}

.submit-button:not(:disabled):hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

@media (max-width: 768px) {
    .update-content {
        padding: 0 1rem;
    }

    .update-title {
        font-size: 1.25rem;
    }

    .update-input,
    .verification-button,
    .submit-button {
        height: 48px;
    }
}

@media (max-width: 480px) {
    .update-content {
        padding: 0 0.5rem;
    }

    .update-title {
        font-size: 1.1rem;
        margin-bottom: 1.5rem;
    }

    .input-label {
        font-size: 0.85rem;
    }

    .update-input {
        font-size: 0.9rem;
    }

    .verification-button {
        padding: 0 1rem;
        font-size: 0.85rem;
    }
}
</style>
