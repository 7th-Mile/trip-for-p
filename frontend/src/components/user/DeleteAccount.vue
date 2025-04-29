<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import store from '@/store';
import {withdrawUserAPI} from "@/api/user";

const router = useRouter();
const isLoading = ref(false);
const error = ref(null);

const handleDeleteAccount = async () => {
    if (!confirm('정말로 회원 탈퇴하시겠습니까?\n이 작업은 되돌릴 수 없습니다.')) {
        return;
    }

    isLoading.value = true;
    error.value = null;

    try {
        await withdrawUserAPI();
        alert('회원 탈퇴가 성공적으로 처리되었습니다.');
        await store.commit('clearData');
        router.push('/');
    } catch (err) {
        console.error('회원 탈퇴 중 오류 발생:', err);
        error.value = '회원 탈퇴 처리 중 오류가 발생했습니다. 나중에 다시 시도해주세요.';
    } finally {
        isLoading.value = false;
    }
};
</script>

<template>
    <div class="delete-account">
        <h2 class="page-title">회원 탈퇴</h2>

        <div class="delete-content">
            <div class="warning-box">
                <div class="warning-icon">⚠️</div>
                <p class="warning-text">
                    회원 탈퇴 시 모든 데이터가 영구적으로 삭제되며, 이 작업은 되돌릴 수 없습니다.
                </p>
            </div>

            <div class="info-section">
                <h3 class="info-title">탈퇴 전 꼭 확인해주세요</h3>

                <div class="info-list">
                    <div class="info-item">
                        <span class="info-bullet">•</span>
                        <p>모든 개인 정보 및 서비스 이용 기록이 삭제됩니다.</p>
                    </div>
                    <div class="info-item">
                        <span class="info-bullet">•</span>
                        <p>삭제된 데이터는 복구할 수 없습니다.</p>
                    </div>
                    <div class="info-item">
                        <span class="info-bullet">•</span>
                        <p>작성한 게시물 및 댓글은 삭제되지 않습니다.</p>
                    </div>
                    <div class="info-item">
                        <span class="info-bullet">•</span>
                        <p>탈퇴 후 1개월간 재가입이 제한됩니다.</p>
                    </div>
                    <div class="info-item">
                        <span class="info-bullet">•</span>
                        <p>개인정보 보호를 위해 탈퇴 후 1개월간 관련 정보를 보관합니다.</p>
                    </div>
                </div>
            </div>

            <div class="action-section">
                <button
                    @click="handleDeleteAccount"
                    class="delete-button"
                    :disabled="isLoading"
                >
                    <span v-if="isLoading" class="loading-spinner"></span>
                    <span>{{ isLoading ? '처리 중...' : '회원 탈퇴' }}</span>
                </button>

                <p v-if="error" class="error-message">
                    {{ error }}
                </p>
            </div>
        </div>
    </div>
</template>

<style scoped>
.delete-account {
    width: 100%;
}

.page-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #333;
    margin-bottom: 2rem;
    text-align: center;
}

.delete-content {
    max-width: 600px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 2rem;
}

.warning-box {
    background: rgba(220, 53, 69, 0.05);
    border: 1px solid rgba(220, 53, 69, 0.2);
    border-radius: 12px;
    padding: 1.5rem;
    display: flex;
    align-items: center;
    gap: 1rem;
}

.warning-icon {
    font-size: 1.5rem;
}

.warning-text {
    color: #dc3545;
    font-size: 0.95rem;
    line-height: 1.5;
    margin: 0;
}

.info-section {
    background: white;
    border-radius: 12px;
    padding: 1.5rem;
    border: 1px solid rgba(92, 106, 196, 0.1);
}

.info-title {
    font-size: 1.1rem;
    font-weight: 600;
    color: #333;
    margin: 0 0 1.5rem 0;
    text-align: center;
}

.info-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.info-item {
    display: flex;
    gap: 0.75rem;
    align-items: flex-start;
}

.info-bullet {
    color: #5c6ac4;
    font-size: 1.2rem;
    line-height: 1;
}

.info-item p {
    color: #666;
    font-size: 0.95rem;
    line-height: 1.5;
    margin: 0;
}

.action-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
    margin-top: 1rem;
}

.delete-button {
    padding: 0.75rem 2rem;
    border-radius: 8px;
    border: none;
    background: linear-gradient(135deg, #dc3545 0%, #e35d6a 100%);
    color: white;
    font-size: 0.95rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.delete-button:not(:disabled):hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(220, 53, 69, 0.2);
}

.delete-button:disabled {
    background: linear-gradient(135deg, rgba(220, 53, 69, 0.5) 0%, rgba(227, 93, 106, 0.5) 100%);
    cursor: not-allowed;
}

.loading-spinner {
    width: 16px;
    height: 16px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: white;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.error-message {
    color: #dc3545;
    font-size: 0.9rem;
    text-align: center;
    margin: 0;
}

@media (max-width: 768px) {
    .page-title {
        font-size: 1.25rem;
        margin-bottom: 1.5rem;
    }

    .delete-content {
        padding: 0 1rem;
    }

    .warning-box,
    .info-section {
        padding: 1.25rem;
    }
}

@media (max-width: 480px) {
    .page-title {
        font-size: 1.1rem;
    }

    .warning-text,
    .info-item p {
        font-size: 0.9rem;
    }

    .info-title {
        font-size: 1rem;
    }

    .delete-button {
        width: 100%;
        justify-content: center;
    }
}
</style>
