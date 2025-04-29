<script setup>
import {defineProps, defineEmits} from 'vue';
defineProps({
    isOpen: {
        type: Boolean,
        default: false
    }
});

defineEmits(['toggle']);
</script>

<template>
    <button
        @click="$emit('toggle')"
        class="toggle-button"
        v-show="!isOpen"
    >
        <span class="toggle-icon">☰</span>
        핫플
    </button>
</template>

<style scoped>
.toggle-button {
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 8px 16px;
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 500;
    color: #5c6ac4;
    cursor: pointer;
    z-index: 1004;
    box-shadow: 0 2px 4px rgba(92, 106, 196, 0.1);
    transition: all 0.2s ease;
}

.toggle-button:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.15);
}

.toggle-icon {
    font-size: 1.2em;
    color: #5c6ac4;
}

@media (max-width: 768px) {
    .toggle-button {
        top: auto;
        bottom: 20px;
    }
}
</style>
