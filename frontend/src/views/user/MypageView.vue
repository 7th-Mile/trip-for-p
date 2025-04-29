<script setup>
import {computed, ref, defineProps} from 'vue';
import UpdateInfo from '@/components/user/UpdateInfo.vue';
import MyCourses from '@/components/course/MyCourses.vue';
import MyReviewPosts from '@/components/review/MyReviewPosts.vue';
import MyFreePosts from '@/components/free/MyFreePosts.vue';
import MyReviewComments from '@/components/review/MyReviewComments.vue';
import MyFreeComments from '@/components/free/MyFreeComments.vue';
import DeleteAccount from '@/components/user/DeleteAccount.vue';
import LikedPlans from "@/components/course/LikedPlans.vue";

const currentPage = ref('updateInfo');
const subPage = ref(null);
const isMobileMenuOpen = ref(false);

defineProps({
    isSubmenuVisible: {
        type: Boolean,
        default: false
    }
})

const toggleMobileMenu = () => {
    isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

const contentClass = computed(() => {
    const classes = {
        'updateInfo': 'updateInfo',
        'myCourses': 'myCourses',
        'myPosts': 'myPosts',
        'myComments': 'myComments',
        'likedPosts': 'likedPosts',
        'deleteAccount': 'deleteAccount'
    };
    return classes[currentPage.value] || '';
});

const getCurrentPageTitle = computed(() => {
    const titles = {
        'updateInfo': '회원정보 수정',
        'myCourses': '내 코스',
        'myPosts': '내 게시글',
        'myComments': '내 댓글',
        'likedPosts': '좋아요한 여행코스',
        'deleteAccount': '회원탈퇴'
    };
    return titles[currentPage.value] || 'My Page';
});

const navigate = (page) => {
    currentPage.value = page;
    if (page === 'myPosts') {
        subPage.value = 'myReviewPosts';
    } else if (page === 'myComments') {
        subPage.value = 'myReviewComments';
    } else {
        subPage.value = null;
    }
    // 모바일에서 메뉴 선택 시 자동으로 닫기
    isMobileMenuOpen.value = false;
};

const navigateSubPage = (page) => {
    subPage.value = page;
    isMobileMenuOpen.value = false;
};

const isActive = (page) => currentPage.value === page;
const isSubActive = (page) => subPage.value === page;
</script>

<template>
    <div class="page-container">
        <!-- 모바일 메뉴 토글 버튼 -->
        <button class="mobile-menu-toggle" :class="{ 'submenu-open': isSubmenuVisible }" @click="toggleMobileMenu">
            <span class="menu-icon"></span>
            <span class="current-page">{{ getCurrentPageTitle }}</span>
        </button>

        <!-- 사이드바 배경 오버레이 -->
        <div
            v-if="isMobileMenuOpen"
            class="sidebar-overlay"
            @click="toggleMobileMenu"
        ></div>

        <!-- 사이드바 네비게이션 -->
        <nav :class="['sidebar', { 'mobile-open': isMobileMenuOpen }]">
            <div class="nav-header">
                <h2 class="nav-title">My Account</h2>
            </div>
            <div class="nav-menu">
                <button @click="navigate('updateInfo')"
                        :class="{'nav-button': true, 'active': isActive('updateInfo')}">
                    회원정보 수정
                </button>
                <button @click="navigate('myCourses')"
                        :class="{'nav-button': true, 'active': isActive('myCourses')}">
                    내 코스
                </button>

                <div class="nav-section">
                    <button @click="navigate('myPosts')"
                            :class="{'nav-button': true, 'active': isActive('myPosts')}">
                        내 게시글
                    </button>
                    <div v-if="currentPage === 'myPosts'" class="sub-menu">
                        <button @click="navigateSubPage('myReviewPosts')"
                                :class="{'sub-button': true, 'active': isSubActive('myReviewPosts')}">
                            <span class="dot"></span>리뷰 게시판
                        </button>
                        <button @click="navigateSubPage('myFreePosts')"
                                :class="{'sub-button': true, 'active': isSubActive('myFreePosts')}">
                            <span class="dot"></span>자유 게시판
                        </button>
                    </div>
                </div>

                <div class="nav-section">
                    <button @click="navigate('myComments')"
                            :class="{'nav-button': true, 'active': isActive('myComments')}">
                        내 댓글
                    </button>
                    <div v-if="currentPage === 'myComments'" class="sub-menu">
                        <button @click="navigateSubPage('myReviewComments')"
                                :class="{'sub-button': true, 'active': isSubActive('myReviewComments')}">
                            <span class="dot"></span>리뷰 게시판
                        </button>
                        <button @click="navigateSubPage('myFreeComments')"
                                :class="{'sub-button': true, 'active': isSubActive('myFreeComments')}">
                            <span class="dot"></span>자유 게시판
                        </button>
                    </div>
                </div>

                <button @click="navigate('likedPosts')"
                        :class="{'nav-button': true, 'active': isActive('likedPosts')}">
                    좋아요한 여행코스
                </button>

                <button @click="navigate('deleteAccount')"
                        :class="{'nav-button delete-account': true, 'active': isActive('deleteAccount')}">
                    회원탈퇴
                </button>
            </div>
        </nav>

        <!-- 컨텐츠 영역 -->
        <main class="content">
            <div :class="['content-container', contentClass]">
                <UpdateInfo v-if="currentPage === 'updateInfo'"/>
                <MyCourses v-else-if="currentPage === 'myCourses'"/>
                <MyReviewPosts v-if="currentPage === 'myPosts' && subPage === 'myReviewPosts'"/>
                <MyFreePosts v-if="currentPage === 'myPosts' && subPage === 'myFreePosts'"/>
                <MyReviewComments v-if="currentPage === 'myComments' && subPage === 'myReviewComments'"/>
                <MyFreeComments v-if="currentPage === 'myComments' && subPage === 'myFreeComments'"/>
                <LikedPlans v-if="currentPage === 'likedPosts'"/>
                <DeleteAccount v-else-if="currentPage === 'deleteAccount'"/>
            </div>
        </main>
    </div>
</template>

<style scoped>
.page-container {
    width: 100%;
    border-radius: 20px;
    display: flex;
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.1) 0%, rgba(135, 148, 216, 0.1) 100%);
}


.mobile-menu-toggle {
    display: none;
    position: fixed;
    top: 144px;
    left: 0;
    width: 100%;
    height: 60px;
    padding: 0 1.25rem;
    background: white;
    border: none;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    z-index: 1000;
    cursor: pointer;
    align-items: center;
    justify-content: flex-start;
}

.menu-icon {
    display: inline-block;
    width: 24px;
    height: 24px;
    position: relative;
    margin-right: 1rem;
}

.menu-icon::before {
    content: '';
    position: absolute;
    width: 100%;
    height: 2px;
    background: #5c6ac4;
    top: 6px;
    left: 0;
    box-shadow: 0 6px 0 #5c6ac4, 0 12px 0 #5c6ac4;
}

.current-page {
    font-size: 1rem;
    color: #5c6ac4;
    font-weight: 500;
}

.sidebar-overlay {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    z-index: 900;
}

.sidebar {
    width: 280px;
    background: white;
    display: flex;
    flex-direction: column;
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    box-shadow: 4px 0 15px rgba(0, 0, 0, 0.05);
    z-index: 1000;
    transition: transform 0.3s ease;
}

.nav-header {
    padding: 2rem;
    margin-top: 180px;
    text-align: center;
}

.nav-title {
    font-family: 'Montserrat', sans-serif;
    font-size: 1.5rem;
    font-weight: 700;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin: 0;
}

.nav-menu {
    padding: 1rem;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.nav-section {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
}

.nav-button {
    width: 100%;
    padding: 0.875rem 1.25rem;
    border: none;
    border-radius: 10px;
    background: transparent;
    color: #444;
    font-size: 0.95rem;
    font-weight: 500;
    text-align: left;
    cursor: pointer;
    transition: all 0.2s ease;
}

.nav-button:hover {
    background: rgba(92, 106, 196, 0.05);
    color: #5c6ac4;
}

.nav-button.active {
    background: rgba(92, 106, 196, 0.1);
    color: #5c6ac4;
    font-weight: 600;
}

.sub-menu {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
    padding-left: 1rem;
}

.sub-button {
    padding: 0.5rem 1.25rem;
    border: none;
    border-radius: 8px;
    background: transparent;
    color: #666;
    font-size: 0.875rem;
    text-align: left;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    transition: all 0.2s ease;
}

.sub-button:hover {
    background: rgba(92, 106, 196, 0.05);
    color: #5c6ac4;
}

.sub-button.active {
    color: #5c6ac4;
    font-weight: 500;
}

.dot {
    width: 4px;
    height: 4px;
    background-color: currentColor;
    border-radius: 50%;
}

.delete-account {
    margin-top: auto;
    color: #888;
    font-size: 0.875rem;
}

.content {
    flex: 1;
    padding: 3rem;
    display: flex;
    justify-content: center;
}

.content-container.updateInfo {
    width: 70%;
}

.content-container {
    width: 100%;
    background: white;
    border-radius: 20px;
    padding: 5rem;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
    height: 100%;
}



@media (max-width: 768px) {
    .page-container {
        flex-direction: column;
    }

    .mobile-menu-toggle {
        display: flex;
        z-index: 1003;
    }
    .mobile-menu-toggle.submenu-open {
        top: calc(144px + 228px);
    }

    .sidebar {
        transform: translateX(-100%);
        width: 80%;
        max-width: 320px;
        padding-top: 150px;
    }

    .sidebar.mobile-open {
        transform: translateX(0);
    }

    .sidebar-overlay {
        display: block;
    }

    .nav-header {
        margin-top: 80px;
        padding: 1.5rem;
    }

    .content {
        margin-left: 0;
        padding: 1rem;
    }

    .content-container {
        padding: 1.5rem;
    }

    .content-container.updateInfo {
        width: 100%;
    }
}

@media (max-width: 480px) {
    .sidebar {
        width: 85%;
    }

    .nav-header {
        padding: 1rem;
    }

    .content {
        padding: 0.75rem;
    }

    .content-container {
        padding: 1rem;
    }

    .nav-button {
        padding: 0.75rem 1rem;
    }

    .sub-button {
        padding: 0.5rem 1rem;
    }
}
</style>
