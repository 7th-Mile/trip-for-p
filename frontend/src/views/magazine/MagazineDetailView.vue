<script setup>
import { computed, onMounted, ref } from "vue";
import { getMagazineAPI, deleteMagazineAPI } from "@/api/magazine";
import { useRoute, useRouter } from "vue-router";
import store from "@/store";
import MarkdownViewer from "@/components/magazine/MarkdownViewer.vue";

const magazine = ref({});
const route = useRoute();
const router = useRouter();
const loading = ref(true);
const error = ref(null);

const isAdmin = computed(() => store.getters.getRole() === 'ADMIN');

const getMagazine = async function () {
    try {
        loading.value = true;
        const response = await getMagazineAPI(route.params.magazineId);
        magazine.value = response.data;
    } catch (err) {
        console.error(err);
        error.value = "매거진을 불러오는 데 실패했습니다.";
    } finally {
        loading.value = false;
    }
};

const editMagazine = () => {
    router.push(`/admin/magazine/${magazine.value.id}/edit`);
};

const deleteMagazine = async () => {
    if (confirm('매거진을 삭제하시겠습니까?')) {
        try {
            await deleteMagazineAPI(magazine.value.id);
            alert('매거진이 성공적으로 삭제되었습니다.');
            await router.push('/admin');
        } catch (err) {
            console.error(err);
            alert('매거진 삭제 중 오류가 발생했습니다.');
        }
    }
};

const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}.${month}.${day} ${hours}:${minutes}`;
};

onMounted(() => {
    getMagazine();
});
</script>

<template>
    <div class="magazine-detail">
        <div v-if="loading" class="loading">매거진을 불러오는 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else class="magazine-content">
            <h1>{{ magazine.title }}</h1>
            <div class="meta-info">
                <span>작성일: {{ formatDate(magazine.createdAt) }}</span>
                <span>조회수: {{ magazine.views }}</span>
            </div>
            <div class="content">
                <markdown-viewer :content="magazine.content"/>
            </div>
            <div v-if="magazine.fileUrls && magazine.fileUrls.length > 0" class="attachments">
                <div class="image-gallery">
                    <div v-for="fileUrl in magazine.fileUrls" :key="fileUrl"
                         class="image-container">
                        <img :src="fileUrl" alt="첨부 이미지">
                    </div>
                </div>
            </div>
        </div>
        <div class="admin-controls" v-if="isAdmin">
            <button @click="editMagazine" class="edit-btn">수정</button>
            <button @click="deleteMagazine" class="delete-btn">삭제</button>
        </div>
    </div>
</template>

<style scoped>
.magazine-detail {
    width: 100%;
    max-width: 1200px;
    padding: 0 20px;
}

.magazine-content {
    background-color: #FFFFFF;
    border-radius: 20px;
    padding: 2.5rem;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
    margin-bottom: 2rem;
}

h1 {
    color: #5c6ac4;
    font-size: 2.2rem;
    font-weight: 800;
    margin-bottom: 1.5rem;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.meta-info {
    color: #8794d8;
    font-size: 0.95rem;
    margin-bottom: 2rem;
    display: flex;
    gap: 1.5rem;
    padding-bottom: 1.5rem;
    border-bottom: 1px solid rgba(92, 106, 196, 0.1);
}

.content {
    color: #4A4A4A;
    font-size: 1.1rem;
    line-height: 1.7;
    margin-bottom: 2rem;
}

.attachments {
    margin-top: 2.5rem;
}

.image-gallery {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 1.5rem;
    width: 100%;
}

.image-container {
    width: 100%;
    padding-top: 75%; /* 4:3 비율 */
    border-radius: 12px;
    position: relative;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.1);
}

.image-container img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.image-container:hover img {
    transform: scale(1.05);
}

.loading, .error {
    text-align: center;
    padding: 3rem;
    font-size: 1.2rem;
    color: #5c6ac4;
    background: #FFFFFF;
    border-radius: 20px;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
}

.error {
    color: #FF4444;
}

.admin-controls {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 2rem;
}

.edit-btn,
.delete-btn {
    padding: 0.8rem 1.5rem;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    font-weight: 600;
    font-size: 1rem;
    transition: all 0.3s ease;
    min-width: 100px;
}

.edit-btn {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.delete-btn {
    background: #FFFFFF;
    color: #FF4444;
    border: 1px solid #FF4444;
}

.edit-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(92, 106, 196, 0.3);
}

.delete-btn:hover {
    background: #FF4444;
    color: white;
    transform: translateY(-2px);
}

@media (max-width: 1024px) {
    .magazine-detail {
        width: 90%;
    }
}

@media (max-width: 768px) {
    .magazine-detail {
        width: 95%;
    }

    .magazine-content {
        padding: 1.5rem;
    }

    h1 {
        font-size: 1.8rem;
    }

    .content {
        font-size: 1rem;
    }

    .image-gallery {
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 1rem;
    }
}

@media (max-width: 480px) {
    .magazine-detail {
        padding: 0 1rem;
    }

    .magazine-content {
        padding: 1.2rem;
    }

    h1 {
        font-size: 1.6rem;
    }

    .meta-info {
        flex-direction: column;
        gap: 0.5rem;
    }

    .image-gallery {
        grid-template-columns: 1fr;
        gap: 0.8rem;
    }

    .admin-controls {
        flex-direction: column;
        gap: 0.8rem;
    }

    .edit-btn,
    .delete-btn {
        width: 100%;
    }
}
</style>
