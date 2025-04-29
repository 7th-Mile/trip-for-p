<script setup>
import { ref, onMounted } from 'vue';
import { getReviewPostListAPI } from "@/api/review";
import router from "@/router";

const posts = ref([]);
const currentPage = ref(1);
const totalPages = ref(1);
const searchKeyword = ref('');
const pageSize = ref(10);

const getReviewPostList = async function () {
    try {
        const request = {
            keyword: searchKeyword.value,
            size: pageSize.value,
            page: currentPage.value - 1,
        }

        const response = await getReviewPostListAPI(request);
        posts.value = response.data.content;
        totalPages.value = response.data.totalPages === 0 ? 1 : response.data.totalPages;
    } catch (error) {
        console.error('Error fetching posts:', error);
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

const search = () => {
    currentPage.value = 1;
    getReviewPostList();
};

const changePage = (page) => {
    currentPage.value = page;
    getReviewPostList();
};

const createPost = () => {
    router.push('/review-post/write');
};

const goToPostDetail = (postId) => {
    router.push(`/review-post/${postId}`);
};

onMounted(() => {
    getReviewPostList();
});
</script>

<template>
    <div class="review-post-board-container">
        <h2>리뷰게시판</h2>

        <div class="board-header">
            <button @click="createPost" class="create-post-btn">리뷰 작성</button>
        </div>

        <div v-for="post in posts" :key="post.id" class="review-post" @click="goToPostDetail(post.id)">
            <div class="post-header">
                <span class="post-author">{{ post.author }}</span>
                <span class="post-date">{{ formatRelativeTime(post.createdAt) }}</span>
            </div>
            <div class="post-title">{{ post.title }}</div>
            <div class="post-footer">
                <span class="post-views">조회수: {{ post.views }}</span>
                <span class="post-comments">댓글: {{ post.comments.length }}</span>
            </div>
        </div>

        <div class="search-container">
            <input v-model="searchKeyword" placeholder="검색어를 입력하세요" />
            <button @click="search" class="search-btn">검색</button>
        </div>

        <div class="pagination">
            <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">이전</button>
            <span>{{ currentPage }} / {{ totalPages }}</span>
            <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">다음</button>
        </div>
    </div>
</template>

<style scoped>
.review-post-board-container {
    width: 100%;
    max-width: 1200px;
    background-color: #FFFFFF;
    border-radius: 20px;
    padding: 2.5rem;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
}

.review-post-board-container h2 {
    color: #5c6ac4;
    font-size: 2rem;
    font-weight: 800;
    margin-bottom: 2rem;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.board-header {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 2rem;
}

.create-post-btn {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: #FFFFFF;
    font-weight: 600;
    padding: 0.8rem 1.5rem;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.create-post-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(92, 106, 196, 0.3);
}

.review-post {
    border: 1px solid rgba(92, 106, 196, 0.1);
    border-radius: 12px;
    padding: 1.5rem;
    margin-bottom: 1rem;
    background-color: #FFFFFF;
    transition: all 0.3s ease;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.05);
}

.review-post:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.1);
    border-color: rgba(92, 106, 196, 0.2);
}

.post-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.post-author {
    color: #5c6ac4;
    font-weight: 600;
    font-size: 1.1rem;
}

.post-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: #4A4A4A;
    margin-bottom: 1rem;
    width: 100%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.post-date {
    color: #8794d8;
    font-size: 0.9rem;
}

.search-container {
    display: flex;
    margin: 2rem 0;
    gap: 0.5rem;
}

.search-container input {
    flex-grow: 1;
    padding: 0.8rem 1.2rem;
    border: 1px solid rgba(92, 106, 196, 0.2);
    border-radius: 12px;
    font-size: 1rem;
    transition: all 0.3s ease;
}

.search-container input:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 3px rgba(92, 106, 196, 0.1);
}

.search-btn {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: #FFFFFF;
    font-weight: 600;
    padding: 0.8rem 1.5rem;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.search-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 2rem;
    gap: 1rem;
}

.pagination button {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: #FFFFFF;
    font-weight: 600;
    padding: 0.8rem 1.5rem;
    border: none;
    border-radius: 12px;
    cursor: pointer;
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

.post-footer {
    display: flex;
    justify-content: flex-end;
    gap: 1.5rem;
    color: #8794d8;
    font-size: 0.9rem;
}

@media (max-width: 1024px) {
    .review-post-board-container {
        width: 90%;
    }
}

@media (max-width: 768px) {
    .review-post-board-container {
        width: 95%;
        padding: 1.2rem;
    }

    .review-post-board-container h2 {
        font-size: 1.8rem;
        text-align: center;
        margin-bottom: 1.5rem;
    }

    .board-header {
        margin-bottom: 1.5rem;
    }

    .search-container {
        flex-direction: column;
        gap: 1rem;
        margin: 1.5rem 0;
    }

    .search-container input {
        width: 100%;
        padding: 1rem;
        font-size: 1rem;
        border-radius: 12px;
    }

    .search-btn {
        width: 100%;
        padding: 1rem;
        border-radius: 12px;
    }


    .review-post {
        padding: 1.2rem;
        margin-bottom: 1rem;
    }

    .post-title {
        font-size: 1.1rem;
        margin-bottom: 0.8rem;
    }

    .post-header {
        flex-direction: row;
        flex-wrap: wrap;
        gap: 0.5rem;
        margin-bottom: 0.8rem;
    }

    .post-author {
        font-size: 1rem;
    }

    .post-date {
        font-size: 0.9rem;
    }

    .pagination {
        flex-wrap: wrap;
        gap: 0.8rem;
        margin-top: 1.5rem;
    }

    .pagination button {
        flex: 1;
        min-width: 100px;
        padding: 0.8rem;
        font-size: 0.9rem;
    }
}

@media (max-width: 480px) {
    .review-post-board-container {
        padding: 1rem;
    }

    .review-post-board-container h2 {
        font-size: 1.6rem;
    }

    .post-header {
        flex-direction: column;
        align-items: flex-start;
    }

    .post-footer {
        flex-direction: row;
        justify-content: space-between;
        padding-top: 0.8rem;
        border-top: 1px solid rgba(92, 106, 196, 0.1);
        margin-top: 0.8rem;
    }

    .post-views,
    .post-comments {
        font-size: 0.85rem;
    }

    .pagination {
        flex-direction: column;
        width: 100%;
    }

    .pagination button,
    .pagination span {
        width: 100%;
        text-align: center;
    }
}
</style>
