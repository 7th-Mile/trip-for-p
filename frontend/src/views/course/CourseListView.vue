<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import router from "@/router";
import { useRoute } from "vue-router";
import { getCourseListAPI } from "@/api/course";
import { Heart, Eye, ChevronLeft, ChevronRight } from 'lucide-vue-next';

const route = useRoute();

const plans = ref([]);
const currentPage = ref(1);
const itemsPerPage = ref(9); // 페이지당 아이템 수 조정
const isLoading = ref(false);
const searchKeyword = ref('');
const windowWidth = ref(window.innerWidth);
const totalElements = ref(0);
const totalPages = ref(0);

const loadSearchFromRoute = () => {
    const keywordFromRoute = route.query.keyword;
    console.log(route.query.keyword)
    if (keywordFromRoute !== undefined) {
        searchKeyword.value = keywordFromRoute;
        fetchPlans();
    } else {
        searchKeyword.value = '';
    }
};

const fetchPlans = async () => {
    try {
        isLoading.value = true;
        const getPlanListRequest = {
            keyword: searchKeyword.value,
            size: itemsPerPage.value,
            page: currentPage.value - 1
        }
        const response = await getCourseListAPI(getPlanListRequest);
        plans.value = response.data.content;
        totalElements.value = response.data.totalElements;
        totalPages.value = response.data.totalPages;
    } catch (error) {
        console.error('Error fetching plans:', error);
    } finally {
        isLoading.value = false;
    }
};

const handleSearch = () => {
    currentPage.value = 1;
    router.push({
        path: '/course/search',
        query: { keyword: searchKeyword.value }
    });
    fetchPlans();
};

const changePage = (page) => {
    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
};

const updateWindowWidth = () => {
    windowWidth.value = window.innerWidth;
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
    return `${year}-${month}-${day}`;
};

const navigateToPlanDetails = (planId) => {
    router.push(`/course/${planId}`);
};

onMounted(() => {
    window.addEventListener('resize', updateWindowWidth);
    loadSearchFromRoute();
});

onUnmounted(() => {
    window.removeEventListener('resize', updateWindowWidth);
});

const isMobile = computed(() => windowWidth.value < 768);

watch(currentPage, fetchPlans);

watch(
    () => route.query.keyword,
    (newKeyword) => {
        if (newKeyword !== undefined) {
            searchKeyword.value = newKeyword;
            currentPage.value = 1;
            fetchPlans();
        }
    }
);
</script>

<template>
    <div class="container">
        <div class="search-container">
            <div class="search-box">
                <input
                    v-model="searchKeyword"
                    placeholder="시/도 또는 구/군 이름으로 검색"
                    @keyup.enter="handleSearch"
                    class="search-input"
                />
                <button @click="handleSearch" class="search-button">
                    검색
                </button>
            </div>
        </div>

        <div class="plans-container">
            <div v-if="isLoading" class="loading-spinner">
                <div class="spinner"></div>
            </div>
            <div v-else>
                <div v-if="plans.length > 0" class="plans-grid">
                    <div v-for="plan in plans"
                         :key="plan.id"
                         class="plan-card"
                         @click="navigateToPlanDetails(plan.id)">
                        <div class="card-header">
                            <div class="location-tags">
                                <span class="location-tag">{{ plan.province }} {{ plan.city }}</span>
                                <span class="duration-tag">{{ plan.duration === 0 ? '당일치기' : `${plan.duration}박${plan.duration + 1}일`}}</span>
                            </div>
                            <h3 class="card-title">{{ plan.title }}</h3>
                        </div>
                        <div class="card-content">
                            <div class="author-info">
                                <span class="author text-truncate">{{ plan.writer }}</span>
                                <span class="date">{{ formatRelativeTime(plan.createdAt) }}</span>
                            </div>
                            <div class="card-stats">
                                <div class="stat-item">
                                    <Heart class="stat-icon" :size="18" />
                                    <span class="stat-number">{{ plan.likes }}</span>
                                </div>
                                <div class="stat-item">
                                    <Eye class="stat-icon" :size="18" />
                                    <span class="stat-number">{{ plan.views }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <p v-else class="no-results">검색 결과가 없습니다.</p>

                <div class="pagination">
                    <!-- 첫 페이지로 이동 -->
                    <button @click="changePage(1)"
                            :disabled="currentPage === 1"
                            class="page-button">
                        &lt;&lt;
                    </button>
                    <!-- 이전 페이지로 이동 -->
                    <button @click="changePage(currentPage - 1)"
                            :disabled="currentPage === 1"
                            class="page-button">
                        <ChevronLeft size="18"/>
                    </button>
                    <template v-for="page in totalPages" :key="page">
                        <button
                            v-if="!isMobile &&
                  (page >= Math.max(1, currentPage - 4) &&
                   page <= Math.min(totalPages, Math.max(currentPage - 4, 1) + 9))"
                            @click="changePage(page)"
                            :class="{ active: page === currentPage }"
                            class="page-button"
                        >
                            {{ page }}
                        </button>
                        <button
                            v-if="isMobile &&
                  (page >= currentPage - 1 &&
                   page <= currentPage + 1)"
                            @click="changePage(page)"
                            :class="{ active: page === currentPage }"
                            class="page-button"
                        >
                            {{ page }}
                        </button>
                    </template>
                    <!-- 다음 페이지로 이동 -->
                    <button @click="changePage(currentPage + 1)"
                            :disabled="currentPage === totalPages"
                            class="page-button">
                        <ChevronRight size="18"/>
                    </button>
                    <!-- 마지막 페이지로 이동 -->
                    <button @click="changePage(totalPages)"
                            :disabled="currentPage === totalPages"
                            class="page-button">
                        &gt;&gt;
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.container {
    max-width: 1440px;
    margin: 0 auto;
}

.search-container {
    margin: 2em 0 3em;
}

.search-box {
    max-width: 800px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    background: transparent;
    padding: 8px;
}

.search-input {
    flex: 1;
    padding: 16px 25px;
    border: 1px solid rgba(92, 106, 196, 0.2);
    border-radius: 12px;
    font-size: 16px;
    outline: none;
    color: #333333;
    background: #ffffff;
    margin-right: 10px;
}

.search-input::placeholder {
    color: #a0a0a0;
}

.search-button {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background-color: #5c6ac4;
    color: #ffffff;
    border: none;
    border-radius: 8px;
    padding: 0 30px;
    height: 54px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s;
    white-space: nowrap;
}

.search-button:hover {
    background-color: #4a55b5;
}

.plans-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 30px;
    padding: 20px 0;
}

.plan-card {
    background: #ffffff;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
    transition: transform 0.3s, box-shadow 0.3s;
    cursor: pointer;
    border: 1px solid rgba(92, 106, 196, 0.1);
    min-height: 240px;
    display: flex;
    flex-direction: column;
}

.plan-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(92, 106, 196, 0.2);
}

.card-header {
    padding: 25px;
    background: #ffffff;
    border-bottom: 1px solid rgba(92, 106, 196, 0.1);
    flex: 1;
}

.location-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 15px;
}

.location-tag, .duration-tag {
    display: inline-block;
    background: rgba(92, 106, 196, 0.1);
    padding: 6px 14px;
    border-radius: 20px;
    font-size: 14px;
    color: #5c6ac4;
    font-weight: 500;
}

.card-title {
    margin: 0;
    font-size: 18px;
    color: #333333;
    line-height: 1.5;
    font-weight: 600;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    max-height: 54px;
}

.card-content {
    padding: 20px 25px;
    background: #ffffff;
}

.author-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    gap: 10px;
}

.author {
    font-weight: 500;
    color: #333333;
    min-width: 0;
}

.text-truncate {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.date {
    color: #888888;
    font-size: 14px;
}

.card-stats {
    display: flex;
    justify-content: flex-end;
    gap: 16px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #666666;
}

.stat-icon {
    color: #666666;
}

.stat-number {
    min-width: 24px;
    text-align: left;
}

.loading-spinner {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 300px;
}

.spinner {
    width: 50px;
    height: 50px;
    border: 4px solid rgba(92, 106, 196, 0.1);
    border-top: 4px solid #5c6ac4;
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

.no-results {
    text-align: center;
    padding: 60px;
    color: #666666;
    font-size: 18px;
}

.pagination {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-top: 50px;
}

.page-button {
    min-width: 45px;
    height: 45px;
    border: 1px solid rgba(92, 106, 196, 0.2);
    border-radius: 8px;
    background: #ffffff;
    color: #666666;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.3s;
    display: flex;
    align-items: center;
    justify-content: center;
}

.page-button:hover:not(:disabled) {
    background: #5c6ac4;
    color: #ffffff;
    border-color: #5c6ac4;
}

.page-button.active {
    background: #5c6ac4;
    color: #ffffff;
    border-color: #5c6ac4;
}

.page-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

@media (max-width: 1200px) {
    .plans-grid {
        grid-template-columns: repeat(2, 1fr);
        gap: 25px;
    }

    .container {
        padding: 30px 20px;
    }
}

@media (max-width: 768px) {
    .container {
        padding: 20px 15px;
    }

    .search-box {
        margin: 0 10px;
    }

    .search-input {
        padding: 12px 20px;
    }

    .search-button {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        height: 45px;
        padding: 0 20px;
        font-size: 14px;
    }

    .plans-grid {
        grid-template-columns: 1fr;
        gap: 20px;
        padding: 15px 10px;
    }

    .card-header {
        padding: 20px;
    }

    .card-content {
        padding: 15px 20px;
    }

    .card-title {
        font-size: 16px;
        max-height: 48px;
    }

    .stat-number {
        min-width: 20px;
    }
}
</style>
