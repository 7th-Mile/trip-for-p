<script setup>
// script 부분은 동일하게 유지
import {defineEmits, defineProps, ref} from 'vue';
import draggable from 'vuedraggable';
import DistanceDisplay from "@/components/course/DistanceDisplay.vue";
import {optimizeRouteAPI} from "@/api/course";

const props = defineProps({
    places: {
        type: Array,
        required: true
    },
    currentDate: {
        type: String,
        required: true
    }
});

const emit = defineEmits(['update:memo', 'reorder', 'delete']);

const dragOptions = ref({
    animation: 200,
    group: "description",
    disabled: false,
    ghostClass: "ghost"
});

const updateMemo = (index, event) => {
    emit('update:memo', index, event.target.value);
};

const deletePlace = (index) => {
    emit('delete', index);
};

const onEnd = (event) => {
    emit('reorder', props.currentDate, event.oldIndex, event.newIndex);
};
const openPlaceUrl = (uri) => {
    if (uri) {
        window?.open(uri, '_blank');
    }
};
const optimizeRoute = async (places) => {

    const requestPlaces = [];
    for (const place of places) {
        console.log(place);
        requestPlaces.push({
            sequence: place.sequence,
            location: {
                lat: place.place.latitude,
                lng: place.place.longitude
            }
        })
    }
    const request = {
        places: requestPlaces
    }
    try {
        const response = await optimizeRouteAPI(request);
        console.log(response.data);

        // 추천 순서에 따라 sequence 재정렬
        if (response.data) {
            const newOrder = response.data.split(',').map(Number);  // "2,1" -> [2, 1]

            // 현재 순서와 추천 순서를 매핑
            const reorderedPlaces = [...places];
            newOrder.forEach((newPosition, index) => {
                // newPosition은 1부터 시작하는 새로운 순서
                // index는 0부터 시작하는 배열 인덱스
                const placeToMove = places.find(p => p.sequence === newPosition);
                if (placeToMove) {
                    reorderedPlaces[index] = {
                        ...placeToMove,
                        sequence: index + 1  // 새로운 순서 할당 (1부터 시작)
                    };
                }
            });

            // places 배열 업데이트
            places.splice(0, places.length, ...reorderedPlaces);
            alert("동선 최적화 완료");
        }
    } catch (error) {
        console.log(error);
    }
}
</script>

<template>
    <div class="itinerary">
        <draggable
            :list="places"
            v-bind="dragOptions"
            @end="onEnd"
            item-key="sequence"
            handle=".drag-handle"
        >
            <template #item="{ element, index }">
                <div class="itinerary-item">
                    <div v-if="index > 0" class="distance-wrapper">
                        <DistanceDisplay
                            :origin-lat="places[index - 1].place.latitude"
                            :origin-lon="places[index - 1].place.longitude"
                            :destination-lat="element.place.latitude"
                            :destination-lon="element.place.longitude"
                            unit="km"
                        />
                    </div>
                    <div class="place-item">
                        <div class="drag-handle">&#9776;</div>
                        <div class="place-content">
                            <div class="place-header">
                                <div class="place-info">
                                    <span class="sequence">{{ element.sequence }}</span>
                                    <div class="place-main-info"
                                         v-if="element.place.uri"
                                         @click="openPlaceUrl(element.place.uri)"
                                         :class="{ 'has-link': element.place.uri }">
                                        <h3>{{ element.place.name }}</h3>
                                        <p class="address">{{ element.place.address }}</p>
                                        <div class="place-details">
                                            <span class="category">{{
                                                    element.place.category
                                                }}</span>
                                            <span class="rating">
                <span class="rating-stars">★</span>
                {{ element.place.rating }}
                <span class="review-count">({{ element.place.reviewCount }})</span>
            </span>
                                        </div>
                                    </div>
                                    <div class="place-main-info" v-else>
                                        <h3>{{ element.place.name }}</h3>
                                        <p class="address">{{ element.place.address }}</p>
                                        <div class="place-details">
                                            <span class="category">{{
                                                    element.place.category
                                                }}</span>
                                            <span class="rating">
                <span class="rating-stars">★</span>
                {{ element.place.rating }}
                <span class="review-count">({{ element.place.reviewCount }})</span>
            </span>
                                        </div>
                                    </div>
                                </div>
                                <button @click="deletePlace(index)" class="delete-btn" title="삭제">
                                    ×
                                </button>
                            </div>
                            <div class="memo-section">
                                <textarea
                                    :value="element.memo"
                                    @input="updateMemo(index, $event)"
                                    placeholder="메모를 입력하세요 (예: 예약시간, 주문할 메뉴 등)"
                                    class="memo-input"
                                ></textarea>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </draggable>
        <div v-if="places.length >= 2" class="recommend-router-button-wrapper">
            <button
                @click="optimizeRoute(places)"
                class="recommend-router-button"
            >
                동선 최적화
            </button>
        </div>
    </div>
</template>

<style scoped>
.itinerary {
    margin-top: 20px;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.itinerary-item {
    transition: all 0.2s ease;
}

.place-item {
    display: flex;
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

.drag-handle {
    padding: 16px 12px;
    color: #8794d8;
    cursor: move;
    background-color: rgba(92, 106, 196, 0.05);
    transition: all 0.2s ease;
}

.drag-handle:hover {
    color: #5c6ac4;
    background-color: rgba(92, 106, 196, 0.1);
}

.place-main-info.has-link {
    cursor: pointer;
    position: relative;
}

.place-main-info.has-link:hover {
    color: #5c6ac4;
}

.place-main-info.has-link:hover h3 {
    color: #5c6ac4;
    text-decoration: underline;
}

.place-main-info.has-link:hover .address {
    color: #5c6ac4;
}

.place-content {
    flex: 1;
    padding: 16px;
}

.place-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
}

.place-info {
    display: flex;
    align-items: flex-start;
    flex: 1;
}

.sequence {
    font-size: 20px;
    font-weight: 600;
    color: #5c6ac4;
    margin-right: 16px;
    min-width: 24px;
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

.memo-section {
    margin-top: 12px;
}

.memo-input {
    width: 100%;
    min-height: 80px;
    padding: 12px;
    border: 1px solid #e5e7eb;
    border-radius: 6px;
    background-color: #f9fafb;
    font-size: 0.925rem;
    resize: vertical;
    transition: all 0.2s ease;
}

.memo-input:focus {
    outline: none;
    border-color: #5c6ac4;
    background-color: white;
    box-shadow: 0 0 0 3px rgba(92, 106, 196, 0.1);
}

.memo-input::placeholder {
    color: #9ca3af;
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

.distance-wrapper {
    display: flex;
    justify-content: center;
    padding: 8px 0;
    margin: 16px 0;
}

.ghost {
    opacity: 0.5;
    background: rgba(92, 106, 196, 0.1);
}

.recommend-router-button-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 24px;
}

.recommend-router-button {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    font-size: 1rem;
    font-weight: 600;
    color: white;
    background-color: #5c6ac4;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    box-shadow: 0 2px 4px rgba(92, 106, 196, 0.2);
}

.recommend-router-button:hover:not(:disabled) {
    background-color: #4c59a3;
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(92, 106, 196, 0.3);
}

.recommend-router-button:disabled {
    background-color: #9ca3af;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
}

@media (max-width: 640px) {
    .place-item {
        flex-direction: column;
    }

    .drag-handle {
        padding: 8px;
        text-align: center;
    }

    .place-content {
        padding: 12px;
    }

    .recommend-router-button {
        width: 100%;
        justify-content: center;
    }
}
</style>
