<script setup>
import {computed, onMounted, ref} from "vue";
import { useRoute } from "vue-router";
import {
    getReviewCommentListAPI,
    getReviewPostAPI,
    createReviewCommentAPI,
    updateReviewCommentAPI, deleteReviewCommentAPI, deleteReviewPostAPI
} from "@/api/review";
import store from "@/store";
import router from "@/router";

const post = ref({});
const comments = ref([]);
const route = useRoute();
const newComment = ref('');
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = ref(10);
const editingCommentId = ref(null);
const editingCommentContent = ref('');

// 현재 로그인한 사용자의 닉네임을 가져옵니다.
const currentUserNickname = computed(() => store.getters.getNickname());

// 현재 사용자가 게시글 작성자인지 확인합니다.
const isPostAuthor = computed(() => currentUserNickname.value === post.value.author);
const isAdmin = computed(() => store.getters.getRole() ==='ADMIN')

const getReviewPost = async function () {
    try {
        const postId = route.params.postId;
        const response = await getReviewPostAPI(postId);
        post.value = response.data;
        await getReviewCommentList();
    } catch (error) {
        console.log(error);
    }
};

const getReviewCommentList = async function () {
    try {
        const request = {
            postId: route.params.postId,
            size: pageSize.value,
            page: currentPage.value - 1,
        }
        const response = await getReviewCommentListAPI(request);
        comments.value = response.data.content;
        totalPages.value = response.data.totalPages === 0 ? 1 : response.data.totalPages;
    } catch (error) {
        console.log(error);
    }
};

const submitComment = async () => {
    if (!store.getters.isAccessTokenValid()) {
        if (window.confirm("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?")) {
            await router.push('/login');
        }
    } else {
        try {
            const request = {
                postId: route.params.postId,
                content: newComment.value
            };
            await createReviewCommentAPI(request);
            newComment.value = '';
            await getReviewCommentList();
        } catch (error) {
            console.log(error);
        }
    }
};

// 상대적 시간 포맷팅 함수 추가
const formatRelativeTime = (dateString) => {

    const now = new Date().toISOString();
    const cleanDateString = dateString.replace(/\[.*\]$/, '');
    const past = new Date(cleanDateString).toISOString();
    const diffInMilliseconds = new Date(now) - new Date(past);
    const diffInSeconds = Math.floor(diffInMilliseconds / 1000);

    if (diffInSeconds < 60) {
        return '방금 전';
    } else if (diffInSeconds < 3600) {
        const minutes = Math.floor(diffInSeconds / 60);
        return `${minutes}분 전`;
    } else if (diffInSeconds < 86400) {
        const hours = Math.floor(diffInSeconds / 3600);
        return `${hours}시간 전`;
    } else {
        return formatDate(dateString);
    }
};

// 날짜 포맷팅 함수 추가
const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const formatDateFull = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}.${month}.${day} ${hours}:${minutes}`;
};

const changePage = async (page) => {
    currentPage.value = page;
    await getReviewCommentList();
};

const startEditComment = (comment) => {
    editingCommentId.value = comment.id;
    editingCommentContent.value = comment.content;
};

const cancelEditComment = () => {
    editingCommentId.value = null;
    editingCommentContent.value = '';
};

const saveEditComment = async (id) => {
    try {
        const request = {
            postId: route.params.postId,
            content: editingCommentContent.value,
        }
        await updateReviewCommentAPI(id, request);
        await getReviewCommentList(); // 댓글 목록 새로고침
        cancelEditComment();
    } catch (error) {
        console.error('댓글 수정 실패:', error);
    }
};

const deleteComment = async (id) => {
    if (window.confirm('댓글을 삭제하시겠습니까?')) {
        try {
            await deleteReviewCommentAPI(id, route.params.postId);
            await getReviewCommentList();
        } catch (error) {
            console.log(error);
        }
    }
};

const editPost = () => {
    router.push(`/review-post/${route.params.postId}/edit`);
};

const deletePost = async (id) => {
    if (window.confirm("리뷰를 삭제하시겠습니까?")) {
        try {
            await deleteReviewPostAPI(id);
            await router.push(`/review-post`)
        } catch (error) {
            console.log(error);
        }
    }
};

const goToPlan = () => {
    if (post.value.planId) {
        router.push(`/plan/${post.value.planId}`);
    }
};

onMounted(() => {
    getReviewPost();
});
</script>

<template>
    <div class="review-detail-container">
        <div class="review-content">
            <h2 class="review-title">{{ post.title }}</h2>
            <p class="review-info">
                <span>작성자: {{ post.author }}</span>
                <span>작성일: {{ formatDateFull(post.createdAt) }}</span>
                <span>조회수: {{ post.views }}</span>
            </p>
            <div class="review-body">
                <pre class="review-content-text">{{ post.content }}</pre>
                <div v-if="post.fileUrls && post.fileUrls.length > 0" class="review-images">
                    <img v-for="(image, index) in post.fileUrls" :key="index" :src="image" :alt="`Review image ${index + 1}`">
                </div>
                <div v-if="post.planId" class="plan-link-container">
                    <button @click="goToPlan" class="plan-link">이 리뷰와 관련된 여행 코스 보기</button>
                </div>
            </div>
            <div v-if="isPostAuthor || isAdmin" class="review-actions">
                <button v-if="isPostAuthor" @click="editPost" class="edit-btn">수정</button>
                <button @click="deletePost(post.id)" class="delete-btn">삭제</button>
            </div>
        </div>

        <div class="comments-section">
            <h3>댓글</h3>
            <div class="comment-form">
                <textarea v-model="newComment" placeholder="댓글을 입력하세요"></textarea>
                <div class="button-container">
                    <button @click="submitComment">댓글 등록</button>
                </div>
            </div>
            <div v-for="comment in comments" :key="comment.id" class="comment">
                <p class="comment-author">{{ comment.author }}</p>
                <template v-if="editingCommentId === comment.id">
                    <textarea v-model="editingCommentContent" class="edit-comment-textarea"></textarea>
                    <div class="edit-comment-actions">
                        <button @click="saveEditComment(comment.id)" class="save-btn">저장</button>
                        <button @click="cancelEditComment" class="cancel-btn">취소</button>
                    </div>
                </template>
                <template v-else>
                    <pre class="comment-content">{{ comment.content }}</pre>
                    <p class="comment-date">{{ formatRelativeTime(comment.createdAt) }}</p>
                    <div v-if="currentUserNickname === comment.author || isPostAuthor || isAdmin" class="comment-actions">
                        <button @click="startEditComment(comment)" class="edit-btn" v-if="currentUserNickname === comment.author">수정</button>
                        <button @click="deleteComment(comment.id)" class="delete-btn">삭제</button>
                    </div>
                </template>
            </div>
            <div class="pagination">
                <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">이전</button>
                <span>{{ currentPage }} / {{ totalPages }}</span>
                <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">다음</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.review-detail-container {
    width: 100%;
    max-width: 1200px;
    padding: 0 20px;
}

.review-content {
    background-color: #FFFFFF;
    border-radius: 20px;
    padding: 2.5rem;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
    margin-bottom: 2rem;
    min-height: 300px;
    position: relative;
}

.review-title {
    color: #5c6ac4;
    font-size: 2rem;
    font-weight: 800;
    margin-bottom: 1.5rem;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    width: 100%;
}

.review-info {
    color: #8794d8;
    font-size: 0.95rem;
    margin-bottom: 2rem;
    display: flex;
    flex-wrap: wrap;
    gap: 1.5rem;
    padding-bottom: 1.5rem;
    border-bottom: 1px solid rgba(92, 106, 196, 0.1);
}

.review-body {
    line-height: 1.7;
    color: #4A4A4A;
    margin-bottom: 3rem;
}

.review-content-text {
    white-space: pre-wrap;
    word-wrap: break-word;
    text-align: left;
    margin: 1.5rem 0;
    padding: 0;
    font-family: inherit;
    font-size: 1.1rem;
    line-height: inherit;
    max-width: 100%;
    overflow-x: auto;
}

.review-images {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1.5rem;
    margin: 2rem 0;
}

.review-images img {
    width: 100%;
    aspect-ratio: 1;
    object-fit: cover;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.1);
    transition: transform 0.3s ease;
}

.review-images img:hover {
    transform: scale(1.02);
}

.plan-link-container {
    margin-top: 2rem;
    width: 100%;
}

.plan-link {
    display: block;
    width: 100%;
    padding: 1rem;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
    text-align: center;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    font-size: 1.1rem;
    font-weight: 600;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.plan-link:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(92, 106, 196, 0.3);
}

.comments-section {
    background-color: #FFFFFF;
    border-radius: 20px;
    padding: 2.5rem;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
}

.comments-section h3 {
    color: #5c6ac4;
    font-size: 1.5rem;
    font-weight: 700;
    margin-bottom: 1.5rem;
}

.comment-form {
    margin-bottom: 2rem;
}

.comment-form .button-container {
    display: flex;
    justify-content: flex-end;
}

.comment-form textarea {
    width: 100%;
    height: 120px;
    padding: 1rem;
    border: 1px solid rgba(92, 106, 196, 0.2);
    border-radius: 12px;
    resize: vertical;
    transition: all 0.3s ease;
    font-size: 1rem;
    margin-bottom: 1rem;
}

.comment-form textarea:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 3px rgba(92, 106, 196, 0.1);
}

.comment-form button {
    padding: 0.8rem 1.5rem;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.3s ease;
}

.comment-form button:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.comment {
    background-color: #F8F9FF;
    border-radius: 12px;
    padding: 1.5rem;
    margin-bottom: 1rem;
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.05);
    position: relative;
}

.comment-author {
    color: #5c6ac4;
    font-weight: 600;
    font-size: 1.1rem;
    margin-bottom: 0.5rem;
}

.comment-content {
    margin: 1rem 0;
    color: #4A4A4A;
    line-height: 1.6;
    font-size: 1rem;
    white-space: pre-wrap;
}

.comment-date {
    color: #8794d8;
    font-size: 0.9rem;
}

.review-actions,
.comment-actions {
    display: flex;
    gap: 0.8rem;
    justify-content: flex-end;
}

.edit-btn,
.delete-btn,
.save-btn,
.cancel-btn {
    padding: 0.6rem 1.2rem;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
    font-size: 0.9rem;
    transition: all 0.3s ease;
}

.edit-btn {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
}

.delete-btn {
    background: #FFFFFF;
    color: #FF4444;
    border: 1px solid #FF4444;
}

.save-btn {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
}

.cancel-btn {
    background: #FFFFFF;
    color: #4A4A4A;
    border: 1px solid rgba(92, 106, 196, 0.2);
}

.edit-btn:hover,
.save-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.delete-btn:hover {
    background: #FF4444;
    color: white;
}

.cancel-btn:hover {
    border-color: #4A4A4A;
}

.edit-comment-textarea {
    width: 100%;
    min-height: 80px;
    padding: 1rem;
    border: 1px solid rgba(92, 106, 196, 0.2);
    border-radius: 12px;
    resize: vertical;
    transition: all 0.3s ease;
    margin: 1rem 0;
}

.edit-comment-textarea:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 3px rgba(92, 106, 196, 0.1);
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 2rem;
    gap: 1rem;
}

.pagination button {
    padding: 0.8rem 1.5rem;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.3s ease;
}

.pagination button:disabled {
    background: #E0E0E0;
    cursor: not-allowed;
    transform: none;
}

.pagination button:not(:disabled):hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.pagination span {
    color: #5c6ac4;
    font-weight: 600;
}

@media (max-width: 1024px) {
    .review-detail-container {
        width: 90%;
    }
}

@media (max-width: 768px) {
    .review-detail-container {
        width: 95%;
        padding: 0 1rem;
    }

    .review-content,
    .comments-section {
        padding: 1.5rem;
    }

    .review-title {
        font-size: 1.8rem;
    }

    .review-info {
        font-size: 0.9rem;
        gap: 1rem;
    }

    .review-content-text {
        font-size: 1rem;
    }

    .review-images {
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 1rem;
    }

    .comment {
        padding: 1rem;
    }
}

@media (max-width: 480px) {
    .review-detail-container {
        padding: 0 0.8rem;
    }

    .review-content,
    .comments-section {
        padding: 1.2rem;
    }

    .review-title {
        font-size: 1.6rem;
    }

    .review-images {
        grid-template-columns: 1fr;
    }

    .comment-form button,
    .pagination button {
        width: auto;
        min-width: 100px;
    }

    .review-actions,
    .comment-actions {
        flex-direction: row;
        gap: 0.5rem;
        justify-content: flex-end;
    }

    .edit-btn,
    .delete-btn,
    .save-btn,
    .cancel-btn {
        width: auto;
        min-width: 80px;
        text-align: center;
    }

    .comment-form .button-container {
        justify-content: flex-end;
    }
}
</style>
