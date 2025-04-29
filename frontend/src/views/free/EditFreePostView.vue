<script setup>
import {onMounted, ref} from 'vue';
import {useRoute} from 'vue-router';
import {getFreePostAPI, updateFreePostAPI} from "@/api/free";
import router from "@/router";

const route = useRoute();

const content = ref('');

const getPostData = async () => {
    try {
        const response = await getFreePostAPI(route.params.postId);
        content.value = response.data.content;
    } catch (error) {
        console.error('게시글 데이터 로드 실패:', error);
    }
};

const editFreePost = async function () {
    try {
        const request = {
            content: content.value,
        };
        await updateFreePostAPI(route.params.postId, request);
        await router.push(`/free-post/${route.params.postId}`); // 수정 후 상세 페이지로 이동
    } catch (error) {
        console.error('게시글 수정 실패:', error);
    }
};

onMounted(() => {
    getPostData();
});
</script>

<template>
    <div class="post-edit-container">
        <h2>게시글 수정</h2>
        <form @submit.prevent="editFreePost" class="post-form">
            <div class="form-group">
                <label for="content">내용</label>
                <textarea id="content" v-model="content" required placeholder="내용을 입력하세요"></textarea>
            </div>
            <div class="form-actions">
                <button type="submit" class="submit-btn">수정 완료</button>
                <button type="button" @click="router.go(-1)" class="cancel-btn">취소</button>
            </div>
        </form>
    </div>
</template>

<style scoped>
.post-edit-container {
    width: 100%;
    max-width: 800px;
    background-color: #FFFFFF;
    border-radius: 20px;
    padding: 2.5rem;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
}

.post-edit-container h2 {
    color: #5c6ac4;
    font-size: 2rem;
    font-weight: 800;
    margin-bottom: 2rem;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.post-form {
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

textarea {
    width: 100%;
    min-height: 300px;
    padding: 1rem 1.2rem;
    border: 1px solid rgba(92, 106, 196, 0.2);
    border-radius: 12px;
    font-size: 1rem;
    line-height: 1.6;
    transition: all 0.3s ease;
    resize: vertical;
}

textarea:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 3px rgba(92, 106, 196, 0.1);
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 1rem;
}

.submit-btn,
.cancel-btn {
    padding: 1rem 2.5rem;
    font-weight: 600;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    font-size: 1.1rem;
    transition: all 0.3s ease;
    min-width: 140px;
}

.submit-btn {
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: #FFFFFF;
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
}

.submit-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(92, 106, 196, 0.3);
}

.cancel-btn {
    background: #FFFFFF;
    color: #5c6ac4;
    border: 2px solid rgba(92, 106, 196, 0.2);
}

.cancel-btn:hover {
    transform: translateY(-2px);
    border-color: #5c6ac4;
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.1);
}

@media (max-width: 1024px) {
    .post-edit-container {
        width: 90%;
    }
}

@media (max-width: 768px) {
    .post-edit-container {
        width: 95%;
        padding: 1.5rem;
    }

    .post-edit-container h2 {
        font-size: 1.8rem;
        text-align: center;
    }

    .form-group {
        gap: 0.4rem;
    }

    textarea {
        min-height: 240px;
    }

    .form-actions {
        flex-direction: column;
        gap: 0.8rem;
    }

    .submit-btn,
    .cancel-btn {
        width: 100%;
        padding: 1rem;
        text-align: center;
    }
}

@media (max-width: 480px) {
    .post-edit-container {
        padding: 1.2rem;
    }

    .post-edit-container h2 {
        font-size: 1.6rem;
        margin-bottom: 1.5rem;
    }

    textarea {
        padding: 0.8rem 1rem;
        font-size: 0.95rem;
        min-height: 200px;
    }
}
</style>
