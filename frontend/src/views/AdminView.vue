<script setup>
import {ref, onMounted} from 'vue';
import {getMagazineListAPI} from "@/api/magazine";
import router from "@/router";

const magazines = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalItems = ref(0);

const getMagazineList = async function () {
    try {
        const request = {
            keyword: '',
            size: pageSize.value,
            page: currentPage.value - 1,
        };
        const response = await getMagazineListAPI(request);
        magazines.value = response.data.content;
        totalItems.value = response.data.totalElements;
    } catch (error) {
        console.error('매거진 목록을 불러오는 중 오류 발생:', error);
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

const changePage = async (page) => {
    currentPage.value = page;
    await getMagazineList();
};

const navigateToRegistration = () => {
    router.push('/admin/magazine/write')
};

const navigateToDetail = (magazineId) => {
    router.push(`/magazine/${magazineId}`);
};

onMounted(getMagazineList);
</script>

<template>
    <div class="admin-page">
        <div class="admin-header">
            <h1>관리자 페이지</h1>
            <button @click="navigateToRegistration" class="register-button">매거진 등록</button>
        </div>

        <div class="magazine-list">
            <h2>매거진 목록</h2>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>제목</th>
                    <th>작성일</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="magazine in magazines" :key="magazine.id"
                    @click="navigateToDetail(magazine.id)" class="clickable-row">
                    <td>{{ magazine.id }}</td>
                    <td>{{ magazine.title }}</td>
                    <td>{{ formatRelativeTime(magazine.createdAt) }}</td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="pagination">
            <button
                @click="changePage(currentPage - 1)"
                :disabled="currentPage === 1"
            >
                이전
            </button>
            <span>{{ currentPage }} / {{ Math.ceil(totalItems / pageSize) }}</span>
            <button
                @click="changePage(currentPage + 1)"
                :disabled="currentPage >= Math.ceil(totalItems / pageSize)"
            >
                다음
            </button>
        </div>
    </div>
</template>

<style scoped>
.admin-page {
    width: 100%;
    max-width: 1200px;
    padding: 0 20px;
}

.admin-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.admin-page h1 {
    color: #5c6ac4;
    font-size: 2.2rem;
    font-weight: 800;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    margin: 0;
}

.admin-page h2 {
    color: #5c6ac4;
    font-size: 1.8rem;
    font-weight: 700;
    margin: 2rem 0 1.5rem;
}

.register-button {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
    border: none;
    padding: 0.8rem 1.5rem;
    border-radius: 12px;
    cursor: pointer;
    font-size: 1rem;
    font-weight: 600;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.register-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(92, 106, 196, 0.3);
}

.magazine-list {
    background-color: #FFFFFF;
    border-radius: 20px;
    padding: 2rem;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
}

table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0;
    margin: 1rem 0;
}

th, td {
    padding: 1rem;
    text-align: left;
    border-bottom: 1px solid rgba(92, 106, 196, 0.1);
}

th {
    background-color: #F8F9FF;
    color: #5c6ac4;
    font-weight: 600;
    font-size: 1rem;
}

th:first-child {
    border-top-left-radius: 12px;
}

th:last-child {
    border-top-right-radius: 12px;
}

tr:last-child td:first-child {
    border-bottom-left-radius: 12px;
}

tr:last-child td:last-child {
    border-bottom-right-radius: 12px;
}

.clickable-row {
    cursor: pointer;
    transition: all 0.3s ease;
}

.clickable-row:hover {
    background-color: #F8F9FF;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.1);
}

td {
    color: #4A4A4A;
    font-size: 1rem;
}

/* ID 열 스타일 */
td:first-child {
    color: #8794d8;
    font-weight: 600;
}

/* 제목 열 스타일 */
td:nth-child(2) {
    font-weight: 500;
}

/* 날짜 열 스타일 */
td:last-child {
    color: #8794d8;
    font-size: 0.9rem;
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
    font-size: 1rem;
}

@media (max-width: 1024px) {
    .admin-page {
        width: 90%;
    }
}

@media (max-width: 768px) {
    .admin-page {
        width: 95%;
    }

    .admin-header {
        flex-direction: row;
        align-items: center;
        gap: 1rem;
    }

    .admin-page h1 {
        font-size: 1.8rem;
    }

    .admin-page h2 {
        font-size: 1.5rem;
    }

    .magazine-list {
        padding: 1rem;
        overflow-x: auto;
    }

    table {
        font-size: 0.9rem;
    }

    th, td {
        padding: 0.8rem;
    }

    .register-button {
        white-space: nowrap;
        padding: 0.8rem 1.2rem;
    }
}

@media (max-width: 480px) {
    .admin-page {
        padding: 0 1rem;
    }

    .admin-page h1 {
        font-size: 1.6rem;
    }

    .magazine-list {
        padding: 0.8rem;
    }

    th, td {
        padding: 0.6rem;
        font-size: 0.85rem;
    }

    .pagination {
        flex-direction: column;
        gap: 0.8rem;
    }

    .pagination button {
        width: 100%;
    }
}
</style>
