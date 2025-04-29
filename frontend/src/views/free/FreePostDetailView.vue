<script setup>
import {computed, onMounted, ref} from "vue";
import { useRoute } from "vue-router";
import {
    getFreeCommentListAPI,
    getFreePostAPI,
    createFreeCommentAPI,
    updateFreeCommentAPI, deleteFreeCommentAPI, deleteFreePostAPI
} from "@/api/free";
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

const getFreePost = async function () {
    try {
        const postId = route.params.postId;
        const response = await getFreePostAPI(postId);
        post.value = response.data;
        await getFreeCommentList();
    } catch (error) {
        console.log(error);
    }
};

const getFreeCommentList = async function () {
    try {
        const request = {
            postId: route.params.postId,
            size: pageSize.value,
            page: currentPage.value - 1,
        }
        const response = await getFreeCommentListAPI(request);
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
            await createFreeCommentAPI(request);
            newComment.value = '';
            await getFreeCommentList();
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
    await getFreeCommentList();
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
        await updateFreeCommentAPI(id, request);
        await getFreeCommentList(); // 댓글 목록 새로고침
        cancelEditComment();
    } catch (error) {
        console.error('댓글 수정 실패:', error);
    }
};

const deleteComment = async (id) => {
    if (window.confirm('댓글을 삭제하시겠습니까?')) {
        try {
            await deleteFreeCommentAPI(id, route.params.postId);
            await getFreeCommentList();
        } catch (error) {
            console.log(error);
        }
    }
};

const editPost = () => {
    router.push(`/free-post/${route.params.postId}/edit`);
};

const deletePost = async (id) => {
    if (window.confirm("게시글을 삭제하시겠습니까?")) {
        try {
            await deleteFreePostAPI(id);
            await router.push(`/free-post`)
        } catch (error) {
            console.log(error);
        }
    }
};



onMounted(() => {
    getFreePost();
});
</script>

<template>
    <div class="post-detail-container">
        <div class="post-content">
            <p class="post-info">
                <span>작성자: {{ post.author }}</span>
                <span>작성일: {{ formatDateFull(post.createdAt) }}</span>
                <span>조회수: {{ post.views }}</span>
            </p>
            <pre class="post-body">{{ post.content }}</pre>
            <div v-if="isPostAuthor || isAdmin" class="post-actions">
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
.post-detail-container {
    width: 100%;
    max-width: 1200px;
    margin: 120px auto 40px;
    padding: 0 20px;
}

.post-content {
    background-color: #FFFFFF;
    border-radius: 20px;
    padding: 2.5rem;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
    margin-bottom: 2rem;
    min-height: 300px;
    position: relative;
}

.post-info {
    color: #8794d8;
    font-size: 0.95rem;
    margin-bottom: 2rem;
    display: flex;
    flex-wrap: wrap;
    gap: 1.5rem;
    padding-bottom: 1.5rem;
    border-bottom: 1px solid rgba(92, 106, 196, 0.1);
}

.post-body {
    white-space: pre-wrap;
    word-wrap: break-word;
    line-height: 1.7;
    color: #4A4A4A;
    font-size: 1.1rem;
    margin-bottom: 3rem;
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

.comment-form .button-container {
    display: flex;
    justify-content: flex-end;
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
    min-width: 100px;
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
    margin-bottom: 2rem;
}

.comment-date {
    color: #8794d8;
    font-size: 0.9rem;
    position: absolute;
    bottom: 1rem;
    left: 1.5rem;
}

.post-actions,
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
    min-width: 80px;
}

.edit-btn,
.save-btn {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
}

.delete-btn {
    background: #FFFFFF;
    color: #FF4444;
    border: 1px solid #FF4444;
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
    font-size: 1rem;
}

.edit-comment-textarea:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 3px rgba(92, 106, 196, 0.1);
}

.edit-comment-actions {
    display: flex;
    justify-content: flex-end;
    gap: 0.8rem;
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
    min-width: 100px;
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
    .post-detail-container {
        width: 90%;
        margin: 120px auto 2rem;
    }
}

@media (max-width: 768px) {
    .post-detail-container {
        width: 95%;
        margin: 160px auto 1.5rem;
        padding: 0 1rem;
    }

    .post-content,
    .comments-section {
        padding: 1.5rem;
    }

    .post-info {
        font-size: 0.9rem;
        gap: 1rem;
    }

    .post-body {
        font-size: 1rem;
    }

    .comment {
        padding: 1rem;
    }

    .comment-actions {
        position: static;
        margin-top: 1rem;
    }

    .comment-date {
        position: static;
        margin-top: 0.5rem;
    }

    .edit-btn,
    .delete-btn,
    .save-btn,
    .cancel-btn {
        padding: 0.5rem 1rem;
    }
}

@media (max-width: 480px) {
    .post-detail-container {
        margin: 140px auto 1rem;
        padding: 0 0.8rem;
    }

    .post-content,
    .comments-section {
        padding: 1.2rem;
    }

    .post-info {
        flex-direction: column;
        gap: 0.5rem;
    }

    .post-body {
        font-size: 0.95rem;
    }

    .comment-form button,
    .pagination button {
        width: 100%;
    }

    .edit-comment-actions,
    .comment-actions {
        flex-direction: column;
        gap: 0.5rem;
    }

    .edit-btn,
    .delete-btn,
    .save-btn,
    .cancel-btn {
        width: 100%;
    }
}
</style>
