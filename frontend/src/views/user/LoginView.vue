<script setup>
import router from "@/router";
import {computed, onMounted, ref} from "vue";
import {loginAPI} from "@/api/user";
import store from "@/store";

const email = ref("");
const password = ref("");

const isFormValid = computed(() => {
    return email.value && password.value;
});

const goSignup = function () {
    router.push("/signup");
}

const goResetPassword = function () {
    router.push("/forgot-password");
}

const login = async function () {
    try {
        const formData = new FormData();
        formData.append('username', email.value);
        formData.append('password', password.value);

        const response = await loginAPI(formData);
        const access = response.headers.access;
        const token = access.split(" ")[1];
        store.commit('setAccessToken', token)
        console.log('Login successful');
        await router.replace('/');
    } catch (error) {
        alert("회원정보가 올바르지 않습니다.");
        console.log('Login failed: ', error);
    }
}

onMounted(() => {
    const inputs = document.querySelectorAll('.login-email, .login-password');

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

    document.querySelector('.login-email').focus();
})
</script>

<template>
    <div class="login-wrap">
        <div class="login-content">
            <h1 class="login-title">
                <router-link to="/">
                    <span class="title-main">Trip For P</span>
                    <span class="title-sub">Just Set Your Direction</span>
                </router-link>

            </h1>
            <div class="login-form" @keyup.enter="login">
                <input type="email" class="login-email" placeholder="이메일" v-model="email">
                <input type="password" class="login-password" placeholder="비밀번호" v-model="password">
                <button @click="login" :disabled="!isFormValid">로그인</button>
            </div>
            <div class="login-actions">
                <div class="go-signup-button" @click="goSignup">
                    <span>회원가입</span>
                </div>
                <div class="divider"></div>
                <div class="find-password-button" @click="goResetPassword">
                    <span>비밀번호 찾기</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.login-wrap {
    width: 100vw;
    min-height: calc(100vh - 120px);
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.1) 0%, rgba(135, 148, 216, 0.1) 100%);
    margin-left: calc(-50vw + 50%);
    margin-right: calc(-50vw + 50%);
}

.login-content {
    width: 100%;
    max-width: 450px;
    padding: 3rem 2rem;
    background: white;
    border-radius: 20px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
}

.login-title {
    text-align: center;
    margin-bottom: 2.5rem;
}
.login-title a:hover {
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

.login-form {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.login-email,
.login-password {
    width: 100%;
    height: 54px;
    border: 1px solid rgba(197, 204, 210, 0.8);
    border-radius: 12px;
    padding: 0 1.5rem;
    font-size: 1rem;
    transition: all 0.3s ease;
    background-color: rgba(255, 255, 255, 0.9);
}

.login-email:focus,
.login-password:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 4px rgba(92, 106, 196, 0.1);
}

.login-form button {
    margin-top: 0.5rem;
    height: 54px;
    border-radius: 12px;
    border: none;
    color: white;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}

.login-form button:not(:disabled) {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
}

.login-form button:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.login-form button:disabled {
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.5) 0%, rgba(135, 148, 216, 0.5) 100%);
    cursor: not-allowed;
    opacity: 0.7;
}

.login-actions {
    margin-top: 1.5rem;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
}

.go-signup-button span,
.find-password-button span {
    color: #888;
    font-size: 0.9rem;
    cursor: pointer;
    transition: color 0.3s ease;
}

.go-signup-button span:hover,
.find-password-button span:hover {
    color: #5c6ac4;
}

.divider {
    width: 1px;
    height: 14px;
    background-color: #E0E0E0;
}

@media (max-width: 768px) {
    .login-content {
        max-width: 400px;
        padding: 2rem 1.5rem;
    }

    .title-main {
        font-size: 2rem;
    }
}

@media (max-width: 480px) {
    .login-wrap {
        min-height: calc(100vh - 100px);
        padding: 1rem;
    }

    .login-content {
        padding: 1.5rem 1rem;
    }

    .title-main {
        font-size: 1.8rem;
    }

    .title-sub {
        font-size: 0.9rem;
    }

    .login-email,
    .login-password,
    .login-form button {
        height: 48px;
    }
}
</style>
