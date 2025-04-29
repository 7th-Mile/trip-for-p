<script setup>
import router from "@/router";
import {computed, onMounted, ref} from "vue";
import {sendPasswordResetEmailAPI, verifyEmailAPI, resetPasswordAPI} from "@/api/user";

const email = ref("");
const password = ref("");
const passwordCheck = ref("");
const verificationCode = ref("");
const isEmailVerified = ref(false);
const showVerificationInput = ref(false);
const verificationMessage = ref("");
const isVerificationFailed = ref(false);

const sendPasswordResetEmail = async () => {
    try {
        await sendPasswordResetEmailAPI({ email: email.value });
        alert("인증 이메일이 전송되었습니다.");
        showVerificationInput.value = true;
    } catch (error) {
        alert(error.message);
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

const isFormValid = computed(() => {
    return email.value && password.value && passwordCheck.value && isEmailVerified.value;
});

const goLogin = function () {
    router.push("/login");
}

const resetpw = async function () {
    try {
        const findPasswordRequest = {
            email: email.value,
            newPassword: password.value,
        }
        if (!email.value || !password.value || !passwordCheck.value || !isEmailVerified.value) {
            alert("모든 필드를 입력해주세요.");
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
        await resetPasswordAPI(findPasswordRequest);
        alert("비밀번호가 재설정되었습니다.");
        await router.replace('/login');
    } catch (error) {
        console.log(error.response.data);
        if (error.response.data.status == 409) {
            alert(error.response.data.message);
        }
    }
}

const isSamePassword = () => password.value === passwordCheck.value;
const validateEmail = () => /^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\.[A-Za-z0-9\\-]+/.test(email.value);
const validatePassword = () => /^(?=.*[a-z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,16}$/.test(password.value);

onMounted(() => {
    const inputs = document.querySelectorAll('.resetpw-input');
    inputs.forEach(input => {
        input.addEventListener('focus', () => {
            input.dataset.placeholder = input.placeholder;
            input.placeholder = '';
        });

        input.addEventListener('blur', () => {
            if (input.dataset.placeholder) {
                input.placeholder = input.dataset.placeholder;
                delete input.dataset.placeholder;
            }
        });
    });
})
</script>

<template>
    <div class="resetpw-wrap">
        <div class="resetpw-content">
            <h1 class="resetpw-title">
                <router-link to="/">
                    <span class="title-main">Trip For P</span>
                    <span class="title-sub">Reset Your Password</span>
                </router-link>
            </h1>
            <div class="resetpw-form" @keyup.enter="resetpw">
                <div class="input-group">
                    <input type="email" class="resetpw-input" placeholder="이메일" v-model="email">
                    <button @click="sendPasswordResetEmail" :disabled="!email" class="verification-button">
                        인증하기
                    </button>
                </div>

                <div v-if="showVerificationInput" class="input-group">
                    <input type="text" class="resetpw-input" placeholder="인증 코드" v-model="verificationCode">
                    <button @click="verifyEmail" :disabled="!verificationCode" class="verification-button">
                        확인
                    </button>
                </div>

                <p v-if="verificationMessage" :class="['verification-message',
                    { 'success': isEmailVerified, 'error': isVerificationFailed }]">
                    {{ verificationMessage }}
                </p>

                <template v-if="isEmailVerified">
                    <input type="password" class="resetpw-input"
                           placeholder="새 비밀번호 (영문 소문자, 숫자 포함 8~16자)"
                           v-model="password">
                    <input type="password" class="resetpw-input"
                           placeholder="새 비밀번호 확인"
                           v-model="passwordCheck">
                    <button @click="resetpw" :disabled="!isFormValid" class="submit-button">
                        비밀번호 재설정
                    </button>
                </template>
            </div>

            <div class="login-actions">
                <div class="go-login-button" @click="goLogin">
                    <span>로그인으로 돌아가기</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.resetpw-wrap {
    width: 100vw;
    min-height: calc(100vh - 120px);
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.1) 0%, rgba(135, 148, 216, 0.1) 100%);
    margin-left: calc(-50vw + 50%);
    margin-right: calc(-50vw + 50%);
}

.resetpw-content {
    width: 100%;
    max-width: 450px;
    padding: 3rem 2rem;
    background: white;
    border-radius: 20px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
}

.resetpw-title {
    text-align: center;
    margin-bottom: 2.5rem;
}

.resetpw-title a:hover {
    opacity: 0.8;
}

.title-main {
    display: block;
    font-size: 2.5rem;
    font-weight: 800;
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

.resetpw-form {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.input-group {
    display: flex;
    gap: 0.5rem;
}

.resetpw-input {
    width: 100%;
    height: 54px;
    border: 1px solid rgba(197, 204, 210, 0.8);
    border-radius: 12px;
    padding: 0 1.5rem;
    font-size: 1rem;
    transition: all 0.3s ease;
    background-color: rgba(255, 255, 255, 0.9);
}

.resetpw-input:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 4px rgba(92, 106, 196, 0.1);
}

.verification-button {
    min-width: 100px;
    height: 54px;
    border-radius: 12px;
    border: none;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
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
    margin-top: 0.5rem;
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

.verification-message {
    font-size: 0.9rem;
    margin: 0.5rem 0;
}

.verification-message.success {
    color: #5c6ac4;
}

.verification-message.error {
    color: #dc3545;
}

.login-actions {
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
    .resetpw-content {
        max-width: 400px;
        padding: 2rem 1.5rem;
    }

    .title-main {
        font-size: 2rem;
    }
}

@media (max-width: 480px) {
    .resetpw-wrap {
        min-height: calc(100vh - 100px);
        padding: 1rem;
    }

    .resetpw-content {
        padding: 1.5rem 1rem;
    }

    .title-main {
        font-size: 1.8rem;
    }

    .title-sub {
        font-size: 0.9rem;
    }

    .resetpw-input,
    .verification-button,
    .submit-button {
        height: 48px;
    }
}
</style>
