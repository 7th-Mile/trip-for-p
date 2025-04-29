<script setup>
import {computed, defineEmits, ref} from "vue";
import store from "@/store";
import {logoutAPI} from "@/api/user";

const hasAccessToken = computed(() => store.getters.getAccessToken());
const isAdmin = computed(() => store.getters.getRole() ==='ADMIN');
const isSubmenuVisible = ref(false);
const emit = defineEmits(['submenu-expanded']);


const logout = function () {
    logoutAPI();
    store.commit('clearData');
};

const toggleSubmenu = () => {
    isSubmenuVisible.value = !isSubmenuVisible.value;
    emit('submenu-expanded', isSubmenuVisible.value);
};
</script>

<template>
    <header>
        <div class="header-container">
            <div class="header-content">
                <div class="header-logo">
                    <router-link to="/" class="logo-text">
                        <span class="logo-main">Trip For P</span>
                    </router-link>
                </div>
                <!-- 모바일 토글 버튼 추가 -->
                <button @click="toggleSubmenu" class="submenu-toggle">
                    <span class="toggle-icon"></span>
                </button>
                <ul class="auth-menu">
                    <template v-if="!hasAccessToken">
                        <li>
                            <router-link to="/signup" class="auth-link">회원가입</router-link>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <router-link to="/login" class="auth-link">로그인</router-link>
                        </li>
                    </template>
                    <template v-else>
                        <li v-if="!isAdmin">
                            <router-link to="/mypage" class="auth-link">마이페이지</router-link>
                        </li>
                        <li v-else>
                            <router-link to="/admin" class="auth-link">관리자</router-link>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <router-link to="/" @click="logout" class="auth-link">로그아웃</router-link>
                        </li>
                    </template>
                </ul>
            </div>
        </div>
        <nav class="submenu-nav">

            <!-- v-show로 토글 기능 추가 -->
            <ul class="submenu" :class="{ 'submenu-visible': isSubmenuVisible }">
                <li><router-link to="/course/search?keyword="  @click="toggleSubmenu">코스 찾기</router-link></li>
                <li><router-link to="/course/write" @click="toggleSubmenu">코스 등록</router-link></li>
                <li><router-link to="/free-post" @click="toggleSubmenu">자유게시판</router-link></li>
                <li><router-link to="/review-post" @click="toggleSubmenu">리뷰게시판</router-link></li>
            </ul>

        </nav>
    </header>
</template>

<style scoped>
header {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    background-color: #FFFFFF;
}

.header-container {
    position: relative;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    width: 100%;
    padding: 1.8rem 0;
}

.header-content {
    width: 60%;
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
}

.header-logo {
    display: flex;
    align-items: center;
}

.logo-text {
    text-decoration: none;
    display: flex;
    align-items: center;
}

.logo-main {
    font-size: 2.2rem;
    font-weight: 800;
    color: #FFFFFF;
    letter-spacing: -0.5px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    line-height: 1;
}

.auth-menu {
    display: flex;
    gap: 1rem;
    list-style-type: none;
    padding: 0;
    margin: 0;
    align-items: center;
}

.auth-link {
    color: #FFFFFF;
    font-weight: 600;
    text-decoration: none;
    padding: 0.8rem 1.5rem;
    border-radius: 12px;
    transition: all 0.3s ease;
    background-color: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(8px);
}

.auth-link:hover {
    background-color: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.divider {
    width: 1px;
    height: 24px;
    background-color: rgba(255, 255, 255, 0.3);
}

.submenu-nav {
    width: 100%;
    background-color: #FFFFFF;
    padding: 0;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
}

.submenu {
    width: 60%;
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: center;
    list-style-type: none;
    padding: 0;
    gap: 0;
}

.submenu li {
    position: relative;
    flex: 1;
    text-align: center;
}

.submenu li a {
    display: block;
    padding: 1.5rem 1rem;
    color: #4A4A4A;
    font-weight: 600;
    font-size: 1.05rem;
    text-decoration: none;
    transition: all 0.3s ease;
    width: 100%;
}

.submenu li a:hover {
    color: #5c6ac4;
    background-color: rgba(92, 106, 196, 0.05);
}

.submenu li a::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 0;
    height: 3px;
    background: linear-gradient(90deg, #5c6ac4 0%, #8794d8 100%);
    transition: all 0.3s ease;
}

.submenu li a:hover::after {
    width: 100%;
}

.submenu-toggle {
    display: none;
    background: none;
    border: none;
    padding: 0.5rem;
    color: #FFFFFF;
    cursor: pointer;
}

.toggle-icon {
    position: relative;
    width: 24px;
    height: 2px;
    background-color: #FFFFFF;
    transition: all 0.3s ease;
    display: block;
}

.toggle-icon::before,
.toggle-icon::after {
    content: '';
    position: absolute;
    width: 24px;
    height: 2px;
    background-color: #FFFFFF;
    transition: all 0.3s ease;
}

.toggle-icon::before {
    transform: translateY(-8px);
}

.toggle-icon::after {
    transform: translateY(8px);
}

@media (max-width: 1024px) {
    .header-content,
    .submenu {
        width: 80%;
    }
}

@media (max-width: 768px) {
    .header-content,
    .submenu {
        width: 90%;
    }

    .header-container {
        position: relative;
        padding: 1rem 1rem 2rem 1rem;
    }

    .submenu-toggle {
        display: block;
        position: absolute;  /* 추가 */
        right: 20px;        /* 추가 */
        top: 50%;           /* 추가 */
        transform: translateY(-50%);  /* 추가 */
    }

    .submenu {
        width: 100%;
        display: none;
        flex-direction: column;
        padding: 0;
        margin: 0;
        max-height: 0;
        overflow: hidden;
        transition: max-height 0.3s ease;
    }

    .submenu-visible {
        display: flex;
        max-height: 300px;
    }

    .submenu li {
        width: 100%;
        border-top: 1px solid rgba(0, 0, 0, 0.05);
        flex: 0 0 auto;
    }

    .submenu li a {
        padding: 1rem;
        justify-content: center;
    }

    .header-content {
        flex-direction: column;
        text-align: center;
        gap: 1.5rem;
    }

    .logo-main {
        font-size: 2rem;
    }

    .auth-menu {
        margin-top: 1rem;
    }

    /* 토글 버튼 활성화 시 아이콘 애니메이션 */
    .submenu-visible ~ .submenu-nav .submenu-toggle .toggle-icon {
        background: transparent;
    }

    .submenu-visible ~ .submenu-nav .submenu-toggle .toggle-icon::before {
        transform: rotate(45deg) translate(0, 0);
    }

    .submenu-visible ~ .submenu-nav .submenu-toggle .toggle-icon::after {
        transform: rotate(-45deg) translate(0, 0);
    }
}

@media (max-width: 480px) {
    .header-content,
    .submenu {
        width: 95%;
    }

    .auth-menu {
        flex-wrap: wrap;
        justify-content: center;
        gap: 0.8rem;
    }

    .divider {
        display: none;
    }

    .submenu {
        width: 100%;
        flex-direction: column;
        align-items: center;
    }

    .submenu li {
        width: 100%;
    }

    .submenu li a {
        justify-content: center;
        padding: 1rem;
    }
}
</style>
