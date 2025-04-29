<script setup>
import { ref } from 'vue';
import {createFreePostAPI} from "@/api/free";
import router from "@/router";

const content = ref('');

const createFreePost = async function() {
    try {
        const request = {
            content: content.value
        }
        const response = await createFreePostAPI(request);
        await router.push(`/free-post/${response.data.id}`);
    } catch (error) {
        console.log(error);
    }
};
</script>

<template>
    <div class="post-create-container">
        <h2>자유 게시글 작성</h2>
        <form @submit.prevent="createFreePost" class="post-form">
            <div class="form-group">
                <label for="content">내용</label>
                <textarea id="content" v-model="content" required placeholder="내용을 입력하세요"></textarea>
            </div>
            <div class="form-actions">
                <button type="submit" class="submit-btn">게시글 작성</button>
            </div>
        </form>
    </div>
</template>

<style scoped>
.post-create-container {
    width: 100%;
    max-width: 800px;
    background-color: #FFFFFF;
    border-radius: 20px;
    padding: 2.5rem;
    box-shadow: 0 4px 20px rgba(92, 106, 196, 0.1);
}

.post-create-container h2 {
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
    margin-top: 1rem;
}

.submit-btn {
    padding: 1rem 2.5rem;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: #FFFFFF;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    font-size: 1.1rem;
    font-weight: 600;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
    min-width: 140px;
}

.submit-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(92, 106, 196, 0.3);
}

@media (max-width: 1024px) {
    .post-create-container {
        width: 90%;
    }
}

@media (max-width: 768px) {
    .post-create-container {
        width: 95%;
        padding: 1.5rem;
    }

    .post-create-container h2 {
        font-size: 1.8rem;
        text-align: center;
    }

    .form-group {
        gap: 0.4rem;
    }

    textarea {
        min-height: 240px;
    }

    .submit-btn {
        width: 100%;
        padding: 1rem;
    }
}

@media (max-width: 480px) {
    .post-create-container {
        padding: 1.2rem;
    }

    .post-create-container h2 {
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
