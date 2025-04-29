<script setup>
import {onMounted, ref} from "vue";
import router from "@/router";
import {getMyReviewListAPI} from "@/api/review";

const posts = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);
const loading = ref(false);

const getMyReviewList = async (page) => {
    loading.value = true;
    try {
        const response = await getMyReviewListAPI({
            page: page,
            size: pageSize.value,
        });
        posts.value = response.data.content;
        currentPage.value = response.data.number;
        totalPages.value = response.data.totalPages;
    } catch (error) {
        console.error('리뷰 게시글을 불러오는 데 실패했습니다:', error);
    } finally {
        loading.value = false;
    }
};

const goToPage = (page) => {
    if (page >= 0 && page < totalPages.value) {
        getMyReviewList(page);
    }
};

const goToReviewDetail = (postId) => {
    router.push(`/review-post/${postId}`);
};

const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
};

onMounted(() => {
    getMyReviewList(0);
});
</script>

<template>
    <div class="my-reviews">
        <h2 class="page-title">나의 리뷰 게시글</h2>

        <div class="reviews-container">
            <div v-if="loading" class="loading-state">
                <div class="loading-spinner"></div>
                <span>로딩 중...</span>
            </div>

            <div v-else-if="posts.length" class="reviews-list">
                <div v-for="post in posts"
                     :key="post.id"
                     class="review-item"
                     @click="goToReviewDetail(post.id)">
                    <div class="review-content">
                        <div class="review-header">
                            <h3 class="review-title">{{ post.title }}</h3>
                            <div class="review-meta">
                                <span class="review-author">{{ post.userId }}</span>
                                <span class="review-date">{{ formatDate(post.createdAt) }}</span>
                            </div>
                        </div>

                        <p class="review-text">
                            {{ post.content.substring(0, 100) }}{{
                                post.content.length > 100 ? '...' : ''
                            }}
                        </p>

                        <div class="review-footer">
              <span class="review-views">
                조회수 {{ post.views }}
              </span>
                        </div>
                    </div>
                </div>
            </div>

            <div v-else class="empty-state">
                작성한 리뷰 게시글이 없습니다.
            </div>

            <div  class="pagination">
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
.my-reviews {
    width: 100%;
}

.page-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #333;
    margin-bottom: 2rem;
    text-align: center;
}

.reviews-container {
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

.reviews-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.review-item {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid rgba(92, 106, 196, 0.1);
}

.review-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.1);
    border-color: rgba(92, 106, 196, 0.2);
}

.review-content {
    padding: 1.5rem;
}

.review-header {
    margin-bottom: 1rem;
}

.review-title {
    font-size: 1.1rem;
    font-weight: 600;
    color: #333;
    margin: 0 0 0.5rem 0;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.review-meta {
    display: flex;
    align-items: center;
    gap: 1rem;
    font-size: 0.9rem;
}

.review-author {
    color: #5c6ac4;
    font-weight: 500;
}

.review-date {
    color: #888;
}

.review-text {
    color: #666;
    font-size: 0.95rem;
    line-height: 1.6;
    margin: 0;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.review-footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 1rem;
}

.review-views {
    font-size: 0.9rem;
    color: #5c6ac4;
    display: flex;
    align-items: center;
    gap: 0.25rem;
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

    .review-content {
        padding: 1rem;
    }

    .review-title {
        font-size: 1rem;
    }

    .review-text {
        font-size: 0.9rem;
    }
}

@media (max-width: 480px) {
    .page-title {
        font-size: 1.1rem;
    }

    .review-meta {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.25rem;
    }

    .review-content {
        padding: 1rem;
    }
}
</style>
