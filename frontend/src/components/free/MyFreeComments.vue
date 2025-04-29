<script setup>
import {ref, onMounted} from 'vue';
import router from "@/router";
import {getMyFreePostCommentListAPI} from "@/api/free";

const comments = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);
const loading = ref(false);

const getMyFreeComments = async (page) => {
    loading.value = true;
    try {
        const response = await getMyFreePostCommentListAPI({
            page: page,
            size: pageSize.value,
        });
        comments.value = response.data.content;
        currentPage.value = response.data.number;
        totalPages.value = response.data.totalPages;
    } catch (error) {
        console.error('댓글을 불러오는 데 실패했습니다:', error);
    } finally {
        loading.value = false;
    }
};

const goToPage = (page) => {
    if (page >= 0 && page < totalPages.value) {
        getMyFreeComments(page);
    }
};

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

const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
};

const goToPostDetail = (postId) => {
    router.push(`/free-post/${postId}`);
};

onMounted(() => {
    getMyFreeComments(0);
});
</script>

<template>
    <div class="my-comments">
        <h2 class="page-title">나의 자유게시글 댓글</h2>

        <div class="comments-container">
            <div v-if="loading" class="loading-state">
                <div class="loading-spinner"></div>
                <span>로딩 중...</span>
            </div>

            <div v-else-if="comments.length" class="comments-list">
                <div v-for="comment in comments"
                     :key="comment.id"
                     class="comment-item"
                     @click="goToPostDetail(comment.postId)">
                    <div class="comment-content">
                        <p class="comment-text">{{ comment.content }}</p>
                        <div class="comment-footer">
                            <span class="post-link">원문으로 이동</span>
                            <span class="comment-date">{{
                                    formatRelativeTime(comment.createdAt)
                                }}</span>
                        </div>
                    </div>
                </div>
            </div>

            <div v-else class="empty-state">
                작성한 댓글이 없습니다.
            </div>

            <div class="pagination">
                <button
                    @click="goToPage(currentPage - 1)"
                    :disabled="currentPage === 0"
                    class="pagination-button"
                >
                    이전
                </button>
                <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
                <button
                    @click="goToPage(currentPage + 1)"
                    :disabled="currentPage >= totalPages - 1"
                    class="pagination-button"
                >
                    다음
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.my-comments {
    width: 100%;
}

.page-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #333;
    margin-bottom: 2rem;
    text-align: center;
}

.comments-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
    padding: 2rem;
    color: #666;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 3px solid rgba(92, 106, 196, 0.1);
    border-radius: 50%;
    border-top-color: #5c6ac4;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.comments-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.comment-item {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid rgba(92, 106, 196, 0.1);
}

.comment-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.1);
    border-color: rgba(92, 106, 196, 0.2);
}

.comment-content {
    padding: 1.5rem;
}

.comment-text {
    color: #444;
    font-size: 1rem;
    line-height: 1.6;
    margin: 0 0 1rem 0;
}

.comment-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.post-link {
    color: #5c6ac4;
    font-size: 0.9rem;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 0.25rem;
}

.post-link::after {
    content: '→';
    font-size: 1.1em;
}

.comment-date {
    color: #888;
    font-size: 0.9rem;
}

.empty-state {
    text-align: center;
    padding: 3rem;
    color: #666;
    background: white;
    border-radius: 12px;
    border: 1px solid rgba(92, 106, 196, 0.1);
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
    margin-top: 1rem;
}

.pagination-button {
    padding: 0.5rem 1rem;
    border-radius: 8px;
    border: none;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
    font-size: 0.9rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
}

.pagination-button:disabled {
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.5) 0%, rgba(135, 148, 216, 0.5) 100%);
    cursor: not-allowed;
}

.pagination-button:not(:disabled):hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.page-info {
    font-size: 0.95rem;
    color: #666;
    min-width: 80px;
    text-align: center;
}

@media (max-width: 768px) {
    .page-title {
        font-size: 1.25rem;
        margin-bottom: 1.5rem;
    }

    .comment-content {
        padding: 1rem;
    }

    .comment-text {
        font-size: 0.95rem;
    }
}

@media (max-width: 480px) {
    .page-title {
        font-size: 1.1rem;
    }

    .comment-footer {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.5rem;
    }

    .comment-content {
        padding: 1rem;
    }
}
</style>
