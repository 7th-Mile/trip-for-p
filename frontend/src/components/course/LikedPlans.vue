<script setup>
import {ref, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {getMyLikedCoursesAPI} from "@/api/course";

const router = useRouter();
const courses = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);
const loading = ref(false);

const getMyLikedPlans = async (page) => {
    loading.value = true;
    try {
        const response = await getMyLikedCoursesAPI({
            page: page,
            size: pageSize.value,
        });
        courses.value = response.data.content;
        currentPage.value = response.data.number;
        totalPages.value = response.data.totalPages;
    } catch (error) {
        console.error('좋아요한 여행 코스를 불러오는 데 실패했습니다:', error);
    } finally {
        loading.value = false;
    }
};

const goToPage = (page) => {
    if (page >= 0 && page < totalPages.value) {
        getMyLikedPlans(page);
    }
};

const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
};

const goToCourseDetail = (courseId) => {
    router.push(`/course/${courseId}`);
};

onMounted(() => {
    getMyLikedPlans(0);
});
</script>

<template>
    <div class="liked-plans">
        <h2 class="page-title">내가 좋아하는 여행 코스</h2>

        <div class="plans-container">
            <div v-if="loading" class="loading-state">
                <div class="loading-spinner"></div>
                <span>로딩 중...</span>
            </div>

            <div v-else-if="courses.length" class="plans-list">
                <div v-for="course in courses"
                     :key="course.id"
                     class="plan-item"
                     @click="goToCourseDetail(course.id)">
                    <div class="plan-content">
                        <h3 class="plan-title">{{ course.title }}</h3>
                        <div class="plan-details">
                            <div class="detail-item">
                                <span class="detail-label">작성자</span>
                                <span class="detail-value">{{ course.writer }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">지역</span>
                                <span class="detail-value">{{ course.province }} {{ course.city }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">작성일</span>
                                <span class="detail-value">{{ formatDate(course.createdAt) }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">조회수</span>
                                <span class="detail-value">{{ course.views }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">좋아요</span>
                                <span class="detail-value">{{ course.likes }}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div v-else class="empty-state">
                좋아요한 여행 코스가 없습니다.
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
.liked-plans {
    width: 100%;
}

.page-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #333;
    margin-bottom: 2rem;
    text-align: center;
}

.plans-container {
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

.plans-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.plan-item {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid rgba(92, 106, 196, 0.1);
}

.plan-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.1);
    border-color: rgba(92, 106, 196, 0.2);
}

.plan-content {
    padding: 1.5rem;
}

.plan-title {
    font-size: 1.1rem;
    font-weight: 600;
    color: #333;
    margin: 0 0 1rem 0;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.plan-details {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 1rem;
}

.detail-item {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
}

.detail-label {
    font-size: 0.85rem;
    color: #666;
}

.detail-value {
    font-size: 0.95rem;
    color: #333;
    font-weight: 500;
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

    .plan-content {
        padding: 1rem;
    }

    .plan-title {
        font-size: 1rem;
    }

    .plan-details {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 480px) {
    .page-title {
        font-size: 1.1rem;
    }

    .plan-details {
        grid-template-columns: 1fr;
    }

    .detail-item {
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
    }
}
</style>
