<script setup>
import { useRoute } from "vue-router";
import { ref, onMounted } from "vue";
import { updateMagazineAPI, getMagazineAPI } from "@/api/magazine";
import router from "@/router";

const title = ref('');
const content = ref('');
const newFiles = ref([]);
const route = useRoute();
const existingFiles = ref([]);
const loading = ref(true);
const error = ref(null);
const previewUrls = ref([]);

const handleFileChange = (event) => {
    previewUrls.value = []
    const addedFiles = Array.from(event.target.files);
    newFiles.value = [...newFiles.value, ...addedFiles];

    addedFiles.forEach(file => {
        const reader = new FileReader();
        reader.onload = (e) => {
            previewUrls.value.push(e.target.result);
        };
        reader.readAsDataURL(file);
    });
};

const updateMagazine = async function () {
    try {
        const formData = new FormData();

        const request = {
            title: title.value,
            content: content.value
        };

        formData.append('request', new Blob([JSON.stringify(request)], {
            type: 'application/json'
        }));

        if (newFiles.value.length > 0) {
            newFiles.value.forEach((file) => {
                formData.append('files', file);
            });
        }

        const response = await updateMagazineAPI(route.params.magazineId, formData);
        await router.push(`/magazine/${response.data.id}`);
    } catch (error) {
        console.error('매거진 수정 중 오류 발생:', error);
    }
};

const loadMagazine = async () => {
    try {
        loading.value = true;
        const response = await getMagazineAPI(route.params.magazineId);
        title.value = response.data.title;
        content.value = response.data.content;
        existingFiles.value = response.data.fileUrls || [];
        previewUrls.value = [...existingFiles.value];
    } catch (err) {
        console.error('매거진 데이터 로딩 중 오류 발생:', err);
        error.value = "매거진 데이터를 불러오는 데 실패했습니다.";
    } finally {
        loading.value = false;
    }
};

onMounted(loadMagazine);
</script>

<template>
    <div class="magazine-edit">
        <div v-if="loading" class="loading">매거진 데이터를 불러오는 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <template v-else>
            <h1>매거진 수정</h1>
            <form @submit.prevent="updateMagazine" class="edit-form">
                <div class="form-group">
                    <label for="title">제목</label>
                    <input type="text" id="title" v-model="title" required placeholder="제목을 입력하세요">
                </div>

                <div class="form-group">
                    <label for="content">내용</label>
                    <textarea id="content" v-model="content" required placeholder="내용을 입력하세요"></textarea>
                </div>

                <div class="form-group">
                    <label for="files" class="file-input-label">
                        이미지 업로드
                    </label>
                    <input type="file" id="files" accept="image/*" @change="handleFileChange" multiple>
                </div>

                <div class="file-preview" v-if="previewUrls.length > 0">
                    <div v-for="(url, index) in previewUrls" :key="index" class="preview-item">
                        <img :src="url" alt="File preview"/>
                        <button @click.prevent="removeFile(index)" class="remove-btn">×</button>
                    </div>
                </div>

                <button type="submit" class="submit-btn">수정 완료</button>
            </form>
        </template>
    </div>
</template>

<style scoped>
.magazine-edit {
    width: 100%;
    max-width: 800px;
    padding: 2.5rem;
    background-color: #FFFFFF;
    border-radius: 20px;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
}

.magazine-edit h1 {
    color: #5c6ac4;
    font-size: 2rem;
    font-weight: 800;
    margin-bottom: 2rem;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.loading, .error {
    text-align: center;
    padding: 3rem;
    font-size: 1.2rem;
    color: #5c6ac4;
    border-radius: 12px;
    background-color: #F8F9FF;
}

.error {
    color: #FF4444;
    background-color: #FFF5F5;
}

.edit-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

label {
    color: #4A4A4A;
    font-weight: 600;
    font-size: 1rem;
}

input[type="text"],
textarea {
    width: 100%;
    padding: 0.8rem 1.2rem;
    border: 1px solid rgba(92, 106, 196, 0.2);
    border-radius: 12px;
    font-size: 1rem;
    transition: all 0.3s ease;
    background-color: #FFFFFF;
}

textarea {
    height: 300px;
    resize: vertical;
    line-height: 1.6;
}

input[type="text"]:focus,
textarea:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 3px rgba(92, 106, 196, 0.1);
}

input[type="file"] {
    display: none;
}

.file-input-label {
    display: inline-block;
    padding: 0.8rem 1.5rem;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: #FFFFFF;
    font-weight: 600;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: center;
}

.file-input-label:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.file-preview {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 1rem;
    margin-top: 1rem;
}

.preview-item {
    position: relative;
    aspect-ratio: 1;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.1);
}

.preview-item img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.preview-item:hover img {
    transform: scale(1.05);
}

.remove-btn {
    position: absolute;
    top: 0.5rem;
    right: 0.5rem;
    background: rgba(92, 106, 196, 0.9);
    color: white;
    border: none;
    border-radius: 8px;
    width: 24px;
    height: 24px;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.remove-btn:hover {
    background: #5c6ac4;
    transform: scale(1.1);
}

.submit-btn {
    padding: 1rem 2.5rem;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: #FFFFFF;
    font-weight: 600;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    font-size: 1.1rem;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
    align-self: flex-end;
    min-width: 140px;
}

.submit-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(92, 106, 196, 0.3);
}

@media (max-width: 1024px) {
    .magazine-edit {
        width: 90%;
    }
}

@media (max-width: 768px) {
    .magazine-edit {
        width: 95%;
        padding: 1.5rem;
    }

    .magazine-edit h1 {
        font-size: 1.8rem;
        text-align: center;
    }

    .form-group {
        gap: 0.4rem;
    }

    textarea {
        height: 240px;
    }

    .file-preview {
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
        gap: 0.8rem;
    }

    .submit-btn {
        width: 100%;
        padding: 1rem;
    }
}

@media (max-width: 480px) {
    .magazine-edit {
        padding: 1.2rem;
    }

    .magazine-edit h1 {
        font-size: 1.6rem;
        margin-bottom: 1.5rem;
    }

    input[type="text"],
    textarea {
        padding: 0.7rem 1rem;
        font-size: 0.95rem;
    }

    .file-preview {
        grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
        gap: 0.6rem;
    }

    .remove-btn {
        width: 20px;
        height: 20px;
        font-size: 12px;
    }
}
</style>
