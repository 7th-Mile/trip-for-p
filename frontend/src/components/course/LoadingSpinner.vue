<script setup>
import { defineProps } from 'vue';
defineProps({
    isLoading: {
        type: Boolean,
        default: false
    }
});
</script>

<template>
    <div class="loading-overlay" v-if="isLoading">
        <div class="loading-content">
            <div class="loading-spinner"></div>
            <p class="loading-text">코스를 불러오는 중입니다...</p>
        </div>
    </div>
</template>

<style scoped>
.loading-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.9);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
}

.loading-content {
    text-align: center;
}

.loading-spinner {
    width: 50px;
    height: 50px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #5c6ac4;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 1rem;
}

.loading-text {
    color: #1a1a1a;
    font-size: 1.1rem;
    font-weight: 500;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}
</style>
