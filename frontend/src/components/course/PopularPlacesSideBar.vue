<script setup>
import {defineProps, defineEmits} from 'vue';
defineProps({
    popularPlaces: {
        type: Array,
        default: () => []
    },
    isOpen: {
        type: Boolean,
        default: false
    },
    city: {
        type: String,
        default: ""
    }
});

const emit = defineEmits(['close', 'select-place']);

const handleSelectPlace = (place) => {
    emit('select-place', place);
    if (window.innerWidth <= 768) {  // 모바일 사이즈일 때
        emit('close');  // 사이드바 닫기
    }
};
</script>
<template>
    <div class="sidebar" :class="{ 'sidebar-open': isOpen }">
        <div class="sidebar-content">
            <div class="sidebar-header">
                <h3>{{city}} 핫플</h3>
                <button @click="$emit('close')" class="delete-btn" title="닫기">&times;</button>
            </div>
            <div class="places-list">
                <div v-if="popularPlaces.length === 0" class="empty-state">
                    <div class="empty-icon">📍</div>
                    <p class="empty-text">등록된 핫플레이스가 없습니다</p>
                    <p class="empty-subtext">{{city}}의 첫 코스를 등록해주세요!</p>
                </div>
                <div v-else v-for="place in popularPlaces" :key="place.place.id" class="place-item">
                    <div class="place-content">
                        <div class="place-header">
                            <div class="place-info">
                                <div class="place-main-info">
                                    <h3>{{ place.place.name }}</h3>
                                    <p class="address">{{ place.place.address }}</p>
                                    <div class="place-details">
                                        <span v-if="place.place.category !== null" class="category">{{ place.place.category }}</span>
                                        <span class="rating">
                      <span class="rating-stars">★</span>
                      {{ place.place.rating.toFixed(1) }}
                      <span class="review-count">({{ place.place.reviewCount }})</span>
                    </span>
                                        <span class="registration-count">{{ place.count }}번 방문</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="button-section">
                        <button @click="handleSelectPlace(place)" class="add-button">
                            일정에 추가
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.sidebar {
    position: fixed;
    top: 0;
    right: -320px;
    width: 320px;
    height: 100vh;
    background: white;
    box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;
    z-index: 1005;
}

.sidebar-open {
    transform: translateX(-320px);
}

.sidebar-content {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 20px;
}

.sidebar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e5e7eb;
}

.sidebar-header h3 {
    margin: 0;
    font-size: 1.5em;
    color: #111827;
    font-weight: 600;
}

.places-list {
    overflow-y: auto;
    flex-grow: 1;
}

.place-item {
    margin-bottom: 16px;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(92, 106, 196, 0.1);
    overflow: hidden;
    transition: all 0.3s ease;
}

.place-item:hover {
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.15);
    transform: translateY(-2px);
}

.place-content {
    padding: 16px;
}

.place-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
}

.place-info {
    display: flex;
    align-items: flex-start;
    flex: 1;
}

.place-main-info {
    flex: 1;
}

.place-main-info h3 {
    font-size: 1.125rem;
    font-weight: 600;
    color: #111827;
    margin: 0 0 4px 0;
}

.address {
    color: #6b7280;
    font-size: 0.925rem;
    margin: 0 0 8px 0;
}

.place-details {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-top: 8px;
    flex-wrap: wrap;
}

.category {
    background-color: rgba(92, 106, 196, 0.1);
    color: #5c6ac4;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 0.875rem;
}

.rating {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #4b5563;
    font-size: 0.875rem;
}

.rating-stars {
    color: #5c6ac4;
}

.review-count {
    color: #9ca3af;
}

.registration-count {
    background-color: rgba(92, 106, 196, 0.1);
    color: #5c6ac4;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 0.875rem;
    font-weight: 500;
}

.button-section {
    padding: 0 16px 16px 16px;
}

.add-button {
    width: 100%;
    padding: 10px;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-weight: bold;
    transition: all 0.2s ease;
}

.add-button:hover {
    background: linear-gradient(135deg, #4f5bb4 0%, #7683c7 100%);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.2);
}

.delete-btn {
    padding: 4px 8px;
    font-size: 1.25rem;
    line-height: 1;
    color: #8794d8;
    background: transparent;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.delete-btn:hover {
    color: #5c6ac4;
    background-color: rgba(92, 106, 196, 0.1);
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 48px 24px;
    text-align: center;
}

.empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
}

.empty-text {
    font-size: 1.125rem;
    font-weight: 600;
    color: #374151;
    margin: 0 0 8px 0;
}

.empty-subtext {
    font-size: 0.875rem;
    color: #6b7280;
    margin: 0;
}

@media (max-width: 768px) {
    .sidebar {
        width: 100%;
        right: -100%;
    }

    .sidebar-open {
        transform: translateX(-100%);
    }
}

@media (max-width: 640px) {
    .sidebar-content {
        padding: 16px;
    }

    .place-content {
        padding: 12px;
    }

    .place-details {
        gap: 8px;
    }

    .button-section {
        padding: 0 12px 12px 12px;
    }
    .empty-state {
        padding: 32px 16px;
    }
}
</style>
