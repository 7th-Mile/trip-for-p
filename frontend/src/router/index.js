import {createRouter, createWebHistory} from 'vue-router'
import WriteCourseView from "@/views/course/WriteCourseView.vue";
import LoginView from "@/views/user/LoginView.vue";
import SignupView from "@/views/user/SignupView.vue";
import CourseDetailView from "@/views/course/CourseDetailView.vue";
import FreePostListView from "@/views/free/FreePostListView.vue";
import HomeView from "@/views/HomeView.vue";
import MypageView from "@/views/user/MypageView.vue";
import ReviewPostListView from "@/views/review/ReviewPostListView.vue";
import store from "@/store";
import WriteFreePostView from "@/views/free/WriteFreePostView.vue";
import FreePostDetailView from "@/views/free/FreePostDetailView.vue";
import EditFreePostView from "@/views/free/EditFreePostView.vue";
import ResetPasswordView from "@/views/user/ResetPasswordView.vue";
import AdminView from "@/views/AdminView.vue";
import WriteMagazineView from "@/views/magazine/WriteMagazineView.vue";
import MagazineDetailView from "@/views/magazine/MagazineDetailView.vue";
import EditMagazineView from "@/views/magazine/EditMagazineView.vue";
import WriteReviewPostView from "@/views/review/WriteReviewPostView.vue";
import ReviewPostDetailView from "@/views/review/ReviewPostDetailView.vue";
import EditReviewPostView from "@/views/review/EditReviewPostView.vue";
import CourseListView from "@/views/course/CourseListView.vue";
import EditCourseView from "@/views/course/EditCourseView.vue";
import {refreshTokenAPI} from "@/api/user";

const routes = [
    {
        path: '/',
        name: 'Home',
        component: HomeView
    },
    {
        path: '/mypage',
        name: 'MyPage',
        component: MypageView,
        meta: {requiresAuth: true}
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView
    },
    {
        path: '/signup',
        name: 'Signup',
        component: SignupView
    },
    {
        path: '/course/search/:keyword?',
        name: 'CourseList',
        component: CourseListView
    },
    {
        path: '/course/write',
        name: 'WriteCourse',
        component: WriteCourseView,
        meta: {requiresAuth: true}
    },
    {
        path: '/course/:courseId/edit',
        name: 'EditCourse',
        component: EditCourseView,
        meta: {requiresAuth: true}
    },
    {
        path: '/course/:courseId',
        name: 'CourseDetail',
        component: CourseDetailView
    },
    {
        path: '/free-post',
        name: 'FreePostList',
        component: FreePostListView
    },
    {
        path: '/free-post/write',
        name: 'WriteFreePost',
        component: WriteFreePostView,
        meta: {requiresAuth: true}
    },
    {
        path: '/free-post/:postId/edit',
        name: 'EditFreePost',
        component: EditFreePostView,
        meta: {requiresAuth: true}
    },
    {
        path: '/free-post/:postId',
        name: 'FreePostDetail',
        component: FreePostDetailView
    },
    {
        path: '/review-post',
        name: 'ReviewPostList',
        component: ReviewPostListView
    },
    {
        path: '/review-post/write',
        name: 'WriteReviewPost',
        component: WriteReviewPostView,
        meta: {requiresAuth: true}
    },
    {
        path: '/review-post/:postId/edit',
        name: 'EditReviewPost',
        component: EditReviewPostView,
        meta: {requiresAuth: true}
    },
    {
        path: '/review-post/:postId',
        name: 'ReviewPostDetail',
        component: ReviewPostDetailView
    },
    {
        path: '/forgot-password',
        name: 'ResetPassword',
        component: ResetPasswordView
    },
    {
        path: '/admin',
        name: 'Admin',
        component: AdminView,
        meta: {requiresAdminAuth: true}
    },
    {
        path: '/admin/magazine/write',
        name: 'WriteMagazine',
        component: WriteMagazineView,
        meta: {requiresAdminAuth: true}
    },
    {
        path: '/magazine/:magazineId',
        name: 'MagazineDetail',
        component: MagazineDetailView,
    },
    {
        path: '/admin/magazine/:magazineId/edit',
        name: 'EditMagazine',
        component: EditMagazineView,
        meta: {requiresAdminAuth: true}
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

router.beforeEach(async (to, from, next) => {
    const hasToken = !!store.getters.getAccessToken();
    const isValid = store.getters.isAccessTokenValid();
    const isAdmin = store.getters.getRole() === 'ADMIN';

    if (to.matched.some(record => !record.meta)) {
        if (hasToken) {
            if (!isValid) {
                try {
                    const response = await refreshTokenAPI();
                    const newToken = response.headers.access.split(" ")[1];
                    store.commit('setAccessToken', newToken);
                    next();
                } catch (error) {
                    store.commit('clearData');
                    alert('세션이 만료되었습니다.');
                    next(false);
                }
            } else {
                next();
            }
        } else {
            next();
        }
        return;
    }
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!hasToken) {
            if (window.confirm("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?")) {
                next('/login')
            } else {
                next(false);
            }
            return;
        }
        if (!isValid) {
            try {
                const response = await refreshTokenAPI();
                const newToken = response.headers.access.split(" ")[1];
                store.commit('setAccessToken', newToken);
                next();
            } catch (error) {
                store.commit('clearData');
                alert('세션이 만료되었습니다.');
                next('/');
            }
        } else {
            next();
        }
        return;
    }

    if (to.matched.some(record => record.meta.requiresAdminAuth)) {
        if (!hasToken) {
            alert('관리자만 접근 가능합니다,');
            next(false);
        }
        if (!isValid) {
            try {
                const response = await refreshTokenAPI();
                const newToken = response.headers.access.split(" ")[1];
                store.commit('setAccessToken', newToken);
                if (!isAdmin) {
                    alert('관리자만 접근 가능합니다.');
                    next(false);
                } else {
                    next();
                }
            } catch (error) {
                store.commit('clearData');
                alert('세션이 만료되었습니다.');
                next('/');
            }
        } else {
            if (!isAdmin) {
                alert('관리자만 접근 가능합니다.');
                next(false);
            } else {
                next();
            }
        }
        return;
    }
    next()
})

export default router
