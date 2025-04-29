<script setup>
import router from "@/router";
import {computed, onMounted, ref, watch} from "vue";
import {createUserAPI, sendVerificationEmailAPI, verifyEmailAPI, verifyNickNameAPI} from "@/api/user";

const email = ref("");
const password = ref("");
const passwordCheck = ref("");
const nickname = ref("");
const verificationCode = ref("");
const isEmailVerified = ref(false);
const showVerificationInput = ref(false);
const verificationMessage = ref("");
const isVerificationFailed = ref(false);
const isNicknameVerified = ref(false);
const nicknameVerificationMessage = ref("");
const isNicknameVerificationFailed = ref(false);
const isEmailVerificationLoading = ref(false);

const sendVerificationEmail = async () => {
    if (isEmailVerificationLoading.value) return;

    isEmailVerificationLoading.value = true;
    try {
        await sendVerificationEmailAPI({ email: email.value });
        alert("인증 이메일이 전송되었습니다.");
        showVerificationInput.value = true;
    } catch (error) {
        alert(error.message);
    } finally {
        isEmailVerificationLoading.value = false;
    }
};

const verifyEmail = async () => {
    try {
        await verifyEmailAPI({ email: email.value, code: verificationCode.value.trim() });
        isEmailVerified.value = true;
        verificationMessage.value = "이메일이 성공적으로 인증되었습니다.";
        isVerificationFailed.value = false;
    } catch (error) {
        isEmailVerified.value = false;
        verificationMessage.value = error.message || "인증 코드가 올바르지 않습니다.";
        isVerificationFailed.value = true;
    }
};

const verifyNickname = async () => {
    try {
        const isDuplicated = await verifyNickNameAPI(nickname.value);
        if (isDuplicated.status !== 'success') {
            isNicknameVerified.value = false;
            nicknameVerificationMessage.value = "이미 사용 중인 닉네임입니다.";
            isNicknameVerificationFailed.value = true;
        } else {
            isNicknameVerified.value = true;
            nicknameVerificationMessage.value = "사용 가능한 닉네임입니다.";
            isNicknameVerificationFailed.value = false;
        }
    } catch (error) {
        console.log(error)
        isNicknameVerified.value = false;
        nicknameVerificationMessage.value = error.message || "닉네임 중복 검사 중 오류가 발생했습니다.";
        isNicknameVerificationFailed.value = true;
    }
};

watch(nickname, (newValue, oldValue) => {
    if (newValue !== oldValue) {
        isNicknameVerified.value = false;
        nicknameVerificationMessage.value = "닉네임 중복 확인이 필요합니다.";
        isNicknameVerificationFailed.value = false;
    }
});

const isFormValid = computed(() => {
    return email.value && password.value && passwordCheck.value && nickname.value && isEmailVerified.value && isNicknameVerified.value;
});

const goLogin = function () {
    router.push("/login");
}

const signup = async function () {
    try {
        const createUserRequest = {
            email: email.value,
            password: password.value,
            passwordCheck: passwordCheck.value,
            nickname: nickname.value
        }
        if (!email.value) {
            document.querySelector('.signup-email').focus();
            return;
        }
        if (!password.value) {
            document.querySelector('.signup-password').focus();
            return;
        }
        if (!passwordCheck.value) {
            document.querySelector('.signup-password-check').focus();
            return;
        }
        if (!nickname.value) {
            document.querySelector('.signup-nickname').focus();
            return;
        }
        if (!isEmailVerified.value) {
            alert("이메일 인증이 필요합니다.");
            return;
        }
        if (!isNicknameVerified.value) {
            alert("닉네임 중복 확인이 필요합니다.");
            return;
        }
        if (!validateEmail()) {
            alert("올바르지 않은 이메일 형식입니다.")
            return;
        } else if (!validatePassword()) {
            alert("비밀번호는 8~16자이며 영문 소문자, 숫자를 포함해야 합니다.");
            return;
        } else if (!isSamePassword()) {
            alert("비밀번호가 다릅니다.")
            return;
        }
        await createUserAPI(createUserRequest);
        alert("가입이 완료되었습니다.");
        await router.replace('/login');
    } catch (error) {
        console.log(error.response.data);
        if (error.response.data.status == 409) {
            alert(error.response.data.message);
        }
    }
}

const isSamePassword = function () {
    return password.value === passwordCheck.value;
}

const validateEmail = function () {
    const regex = /^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\.[A-Za-z0-9\\-]+/;
    return regex.test(email.value);
}

const validatePassword = function () {
    const regex = /^(?=.*[a-z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,16}$/;
    return regex.test(password.value)
}

onMounted(() => {
    const inputs = document.querySelectorAll(
        '.signup-email, .signup-password, .signup-password-check, .signup-nickname');

    inputs.forEach(input => {
        input.addEventListener('focus', () => {
            input.dataset.placeholder = input.placeholder;
            input.placeholder = '';
        });

        input.addEventListener('blur', () => {
            if (input.dataset.placeholder !== undefined) {
                input.placeholder = input.dataset.placeholder;
                delete input.dataset.placeholder;
            }
        });
    });
})
</script>

<template>
    <div class="signup-wrap">
        <div class="signup-content">
            <h1 class="signup-title">
                <router-link to="/">
                    <span class="title-main">Trip For P</span>
                    <span class="title-sub">Just Set Your Direction</span>
                </router-link>
            </h1>
            <div class="signup-form" @keyup.enter="signup">
                <div class="email-verification-container">
                    <input type="email" class="signup-email" placeholder="이메일" v-model="email">
                    <button
                        @click="sendVerificationEmail"
                        :disabled="!email || isEmailVerificationLoading"
                        class="verification-button"
                    >
                        <span v-if="isEmailVerificationLoading" class="loading-spinner"></span>
                        <span>{{ isEmailVerificationLoading ? '처리 중...' : '인증' }}</span>
                    </button>
                </div>
                <div v-if="showVerificationInput" class="verification-code-container">
                    <input type="text" class="signup-verification" placeholder="인증 코드" v-model="verificationCode">
                    <button @click="verifyEmail" :disabled="!verificationCode" class="verify-button">확인</button>
                </div>
                <p v-if="verificationMessage" :class="[
                    'verification-message',
                    { 'verification-success': isEmailVerified, 'verification-failed': isVerificationFailed }
                ]">
                    {{ verificationMessage }}
                </p>
                <input type="password" class="signup-password" placeholder="비밀번호 (영문, 숫자 조합 8~16자)" v-model="password">
                <input type="password" class="signup-password-check" placeholder="비밀번호 확인" v-model="passwordCheck">
                <div class="nickname-verification-container">
                    <input type="text" class="signup-nickname" placeholder="닉네임" v-model="nickname">
                    <button @click="verifyNickname" :disabled="!nickname" class="verification-button">중복 확인</button>
                </div>
                <p v-if="nicknameVerificationMessage" :class="[
                    'verification-message',
                    { 'verification-success': isNicknameVerified, 'verification-failed': isNicknameVerificationFailed }
                ]">
                    {{ nicknameVerificationMessage }}
                </p>
                <button @click="signup" :disabled="!isFormValid" class="signup-button">가입하기</button>
            </div>
            <div class="signup-actions">
                <div class="go-login-button" @click="goLogin">
                    <span>이미 계정이 있으신가요?</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.signup-wrap {
    width: 100vw;
    min-height: calc(100vh - 120px);
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.1) 0%, rgba(135, 148, 216, 0.1) 100%);
    margin-left: calc(-50vw + 50%);
    margin-right: calc(-50vw + 50%);
}


.signup-content {
    width: 100%;
    max-width: 600px;
    padding: 3rem 3.5rem;
    background: white;
    border-radius: 20px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
}
.signup-title {
    text-align: center;
    margin-bottom: 2.5rem;
}

.signup-title a {
    text-decoration: none;
    display: block;
    transition: opacity 0.3s ease;
}

.signup-title a:hover {
    opacity: 0.8;
}

.title-main {
    display: block;
    font-size: 2.5rem;
    font-weight: 800;
    color: #5c6ac4;
    margin-bottom: 0.5rem;
    font-family: 'Montserrat', sans-serif;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.title-sub {
    display: block;
    font-size: 1rem;
    color: #888;
    letter-spacing: 0.5px;
}

.signup-form {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.signup-email,
.signup-password,
.signup-password-check,
.signup-nickname,
.signup-verification {
    width: 100%;
    height: 54px;
    border: 1px solid rgba(197, 204, 210, 0.8);
    border-radius: 12px;
    padding: 0 1.5rem;
    font-size: 1rem;
    transition: all 0.3s ease;
    background-color: rgba(255, 255, 255, 0.9);
}

.signup-email:focus,
.signup-password:focus,
.signup-password-check:focus,
.signup-nickname:focus,
.signup-verification:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 4px rgba(92, 106, 196, 0.1);
}

.signup-form button {
    height: 54px;
    border-radius: 12px;
    border: none;
    color: white;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}

.signup-form button:not(:disabled) {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
}

.signup-form button:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.signup-form button:disabled {
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.5) 0%, rgba(135, 148, 216, 0.5) 100%);
    cursor: not-allowed;
    opacity: 0.7;
}

.email-verification-container,
.verification-code-container,
.nickname-verification-container {
    display: flex;
    gap: 1rem;
    width: 100%;
}

.signup-email,
.signup-verification,
.signup-nickname {
    flex: 1;
}

.verification-button,
.verify-button {
    width: 120px;
}

.verification-message {
    font-size: 0.875rem;
    margin-top: 0.25rem;
}

.loading-spinner {
    display: inline-block;
    width: 12px;
    height: 12px;
    margin-right: 8px;
    border: 2px solid #ffffff;
    border-radius: 50%;
    border-top-color: transparent;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.verification-button {
    display: flex;
    align-items: center;
    justify-content: center;
}

.verification-success {
    color: #28a745;
}

.verification-failed {
    color: #dc3545;
}

.signup-button {
    margin-top: 1rem;
}

.signup-actions {
    margin-top: 1.5rem;
    display: flex;
    justify-content: center;
}

.go-login-button span {
    color: #888;
    font-size: 0.9rem;
    cursor: pointer;
    transition: color 0.3s ease;
}

.go-login-button span:hover {
    color: #5c6ac4;
}

@media (max-width: 768px) {
    .signup-content {
        max-width: 500px;
        padding: 2rem;
    }

    .title-main {
        font-size: 2rem;
    }
}

@media (max-width: 480px) {
    .signup-wrap {
        min-height: calc(100vh - 100px);
        padding: 1rem;
    }

    .signup-content {
        padding: 1.5rem 1rem;
    }

    .title-main {
        font-size: 1.8rem;
    }

    .title-sub {
        font-size: 0.9rem;
    }

    .signup-email,
    .signup-password,
    .signup-password-check,
    .signup-nickname,
    .signup-verification,
    .signup-form button {
        height: 48px;
        font-size: 0.9rem;
    }

    .verification-button,
    .verify-button {
        width: 80px;
        font-size: 0.8rem;
    }
}
</style>
