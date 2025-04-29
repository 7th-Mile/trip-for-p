<script setup>
import {computed, onMounted, ref} from 'vue';
import {useRoute} from "vue-router";
import {checkCourseLikeAPI, deleteCourseAPI, getCourseAPI, likeCourseAPI} from "@/api/course";
import router from "@/router";
import store from "@/store";
import GoogleMapComponent from "@/components/course/GoogleMapComponent.vue";
import {detailPlaceAPI} from "@/api/google";
import DistanceDisplay from "@/components/course/DistanceDisplay.vue";
import LoadingSpinner from "@/components/course/LoadingSpinner.vue";

const currentDateIndex = ref(0);
const plan = ref(null);
const placesDetail = ref({});
const route = useRoute();
const isLiked = ref(false);
const isFetched = ref(false);
const isLoading = ref(true);
const GOOGLE_MAP_API_KEY = process.env.VUE_APP_GOOGLE_MAP_API_KEY

const getPlan = async function () {
    try {
        isLoading.value = true;
        const response = await getCourseAPI(route.params.courseId);
        plan.value = response.data;

        if (store.getters.getAccessToken()) {
            await checkPlanLike();
        }
    } catch (error) {
        console.log(error);
    } finally {
        isLoading.value = false;
    }
};

const fetchPlaceDetails = async () => {
    if (!plan.value || !plan.value.spots) {
        return;
    }

    try {
        isLoading.value = true;
        for (const spot of plan.value.spots) {
            try {
                const response = await detailPlaceAPI(spot.place.mapPlaceId);
                placesDetail.value[spot.place.mapPlaceId] = response.data;
            } catch (error) {
                if (error.status === 429) {
                    if (store.getters.isAccessTokenValid()) {
                        alert("일일한도량을 초과하였습니다.");
                        await router.push('/');
                    } else {
                        if (window.confirm("일일한도량을 초과하였습니다.\n로그인 페이지로 이동하시겠습니까?")) {
                            await router.push('/login');
                        } else {
                            await router.push('/');
                        }
                    }
                }
                break;
            }
        }
    } finally {
        isLoading.value = false;
        isFetched.value = true;
    }
};

const selectedPlaces = computed(() => {
    if (!plan.value || !plan.value.spots) {
        return {};
    }

    const groupedPlaces = {};
    plan.value.spots.forEach(spot => {
        const dateString = typeof spot.tripDate === 'string'
            ? spot.tripDate
            : new Date(spot.tripDate).toISOString().split('T')[0];

        if (!groupedPlaces[dateString]) {
            groupedPlaces[dateString] = [];
        }

        const detail = placesDetail.value[spot.place.mapPlaceId];
        groupedPlaces[dateString].push({
            id: spot.id,
            place: {
                ...detail
            },
            memo: spot.memo,
            sequence: spot.sequence,
            tripDate: dateString
        });
    });

    return groupedPlaces;
});

const dates = computed(() => {
    if (!plan.value || !plan.value.startDate || !plan.value.endDate) {
        return [];
    }
    const start = new Date(plan.value.startDate);
    const end = new Date(plan.value.endDate);
    const dateArray = [];
    for (let dt = new Date(start); dt <= end; dt.setDate(dt.getDate() + 1)) {
        dateArray.push(dt.toISOString().split('T')[0]);
    }
    return dateArray;
});

const currentDate = computed(() => {
    if (dates.value.length === 0) {
        return null;
    }
    return dates.value[currentDateIndex.value];
});

// script 부분에 새로운 computed 속성 추가
const currentDateWeekDay = computed(() => {
    if (!currentDate.value) {
        return '';
    }
    const date = new Date(currentDate.value);
    const weekDays = ['(일)', '(월)', '(화)', '(수)', '(목)', '(금)', '(토)'];
    return weekDays[date.getDay()];
});

const prevDate = () => {
    if (currentDateIndex.value > 0) {
        currentDateIndex.value--;
    }
};

const nextDate = () => {
    if (currentDateIndex.value < dates.value.length - 1) {
        currentDateIndex.value++;
    }
};

const goBackToSelection = () => {
    router.go(-1);
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

const likePlan = async function () {
    if (!store.getters.isAccessTokenValid()) {
        if (window.confirm("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?")) {
            await router.push('/login');
        }
    } else {
        try {
            await likeCourseAPI(route.params.courseId);
            isLiked.value = !isLiked.value;
            plan.value.likeCount += isLiked.value ? 1 : -1;
        } catch (error) {
            console.log(error);
        }
    }
};

const checkPlanLike = async function () {
    try {
        const response = await checkCourseLikeAPI(route.params.courseId);
        isLiked.value = response.data;
    } catch (error) {
        console.log(error);
    }
};

const goUpdatePlan = function (id) {
    // 객체를 배열로 변환
    const spotsArray = Object.entries(selectedPlaces.value).flatMap(([date, places]) =>
        places.map(place => ({
            ...place,
            tripDate: date
        }))
    );

    router.push({
        path: `/course/${id}/edit`,
        state: {
            spots: spotsArray
        }
    });
};

const deletePlan = async function (id) {
    if (window.confirm('여행 코스를 삭제하시겠습니까?')) {
        try {
            await deleteCourseAPI(id);
            await router.push('/course/search?keyword=')
        } catch (error) {
            console.log(error)
        }
    }
};

const openPlaceUrl = (uri) => {
    if (uri) {
        window?.open(uri, '_blank');
    }
};

const currentUserNickname = computed(() => store.getters.getNickname());
const isPostAuthor = computed(() => currentUserNickname.value === plan.value?.writer);

onMounted(async () => {
    await getPlan();
    await fetchPlaceDetails();
});
</script>

<template>
    <div class="plan-detail-view">
        <LoadingSpinner :is-loading="isLoading" />
        <div v-if="plan && !isLoading">
            <div class="header-section">
                <img
                    @click="goBackToSelection"
                    class="back-button"
                    src="../../assets/backbutton.png"
                    alt="뒤로가기 버튼"
                />
                <div class="title-info">
                    <h2>{{ plan.title }}</h2>
                    <p>{{ formatDate(plan.createdAt) }}</p>
                </div>
                <div class="plan-info">
                    <span>{{ plan.writer }}</span>
                    <p>
                        <span>조회 {{ plan.views }}</span>
                    </p>
                </div>
                <div v-if="isPostAuthor" class="author-actions">
                    <button @click="goUpdatePlan(plan.id)" class="edit-button">수정</button>
                    <button @click="deletePlan(plan.id)" class="delete-button">삭제</button>
                </div>
            </div>

            <!-- Date Navigation -->
            <div class="date-navigation" v-if="dates.length > 0">
                <button @click="prevDate" :disabled="currentDateIndex === 0"
                        class="nav-button prev-button">&lt; 이전
                </button>
                <span>{{ currentDate }} {{ currentDateWeekDay }}</span>
                <button @click="nextDate" :disabled="currentDateIndex === dates.length - 1"
                        class="nav-button next-button">다음 &gt;
                </button>
            </div>

            <!-- Main Content Section -->
            <div class="main-content">
                <!-- Left Section - Map -->
                <div class="map-section">
                    <GoogleMapComponent
                        class="google-map-component"
                        v-if="isFetched"
                        :places="selectedPlaces[currentDate] || []"
                        :date="currentDate"
                        :apiKey="GOOGLE_MAP_API_KEY"
                    />
                </div>

                <!-- Right Section - Itinerary -->
                <div class="itinerary-section">
                    <div class="itinerary">
                        <h2>일정</h2>
                        <div v-for="(item, index) in selectedPlaces[currentDate]" :key="item.sequence"
                             class="itinerary-item">
                            <div v-if="index > 0" class="distance-wrapper">
                                <DistanceDisplay
                                    :origin-lat="selectedPlaces[currentDate][index - 1].place.latitude"
                                    :origin-lon="selectedPlaces[currentDate][index - 1].place.longitude"
                                    :destination-lat="item.place.latitude"
                                    :destination-lon="item.place.longitude"
                                    unit="km"
                                />
                            </div>
                            <div class="place-item">
                                <div class="place-content">
                                    <div class="place-header">
                                        <div class="place-info">
                                            <span class="sequence">{{ item.sequence }}</span>
                                            <div
                                                class="place-main-info"
                                                :class="{ 'has-link': item.place.uri }"
                                                @click="openPlaceUrl(item.place.uri)"
                                            >
                                                <h3>{{ item.place.name }}</h3>
                                                <p class="address">{{ item.place.address }}</p>
                                                <div class="place-details" v-if="item.place.category || item.place.rating">
                <span class="category" v-if="item.place.category">
                    {{ item.place.category }}
                </span>
                                                    <span class="rating" v-if="item.place.rating">
                    <span class="rating-stars">★</span>
                    {{ item.place.rating }}
                    <span class="review-count" v-if="item.place.reviewCount">
                        ({{ item.place.reviewCount }})
                    </span>
                </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div v-if="item.memo" class="memo-section">
                                        <div class="memo-content">
                                            {{ item.memo }}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Like Button -->
            <div class="like-button-container">
                <button @click="likePlan" class="like-button" :class="{ 'liked': isLiked }">
                    {{ isLiked ? '♥' : '♡' }} 좋아요 {{ plan.likeCount }}
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.plan-detail-view {
    max-width: 1600px;
    width: 100%;
    margin: 2em auto;
    padding: 0 40px;
}

.header-section {
    margin-bottom: 1.5rem;
}

.back-button {
    width: 32px;
    height: 32px;
    opacity: 0.7;
    transition: opacity 0.2s ease;
    margin-bottom: 1em;
    cursor: pointer;
}

.back-button:hover {
    opacity: 1;
}

.title-info {
    padding: 24px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    margin-bottom: 1rem;
}

.title-info h2 {
    font-size: 1.8rem;
    font-weight: 700;
    color: #1a1a1a;
    margin: 0;
    line-height: 1.4;
}

.title-info p {
    margin: 8px 0 0;
    color: #666;
    font-size: 0.95rem;
}

.plan-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    margin-bottom: 1rem;
}

.plan-info span {
    color: #1a1a1a;
    font-size: 0.95rem;
    display: flex;
    align-items: center;
    gap: 8px;
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

.author-actions {
    display: flex;
    gap: 12px;
    margin-bottom: 1rem;
    justify-content: flex-end;
}

.edit-button, .delete-button {
    padding: 10px 20px;
    font-size: 0.9rem;
    font-weight: 600;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.edit-button {
    background-color: #f3f4f6;
    color: #1a1a1a;
}

.edit-button:hover {
    background-color: #e5e7eb;
}

.delete-button {
    background-color: #fee2e2;
    color: #dc2626;
}

.delete-button:hover {
    background-color: #fecaca;
}

/* Date Navigation */
.date-navigation {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    margin-bottom: 1.5rem;
}

.date-navigation span {
    font-weight: 600;
    color: #1a1a1a;
    font-size: 1.1rem;
}

.nav-button {
    padding: 10px 20px;
    font-size: 0.9rem;
    font-weight: 600;
    background-color: #5c6ac4;
    color: white;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.nav-button:hover:not(:disabled) {
    background-color: #4c5aa0;
}

.nav-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

/* Main Content Layout */
.main-content {
    display: flex;
    gap: 24px;
    margin-bottom: 1.5rem;
    min-height: 600px;
}

.map-section {
    flex: 1;
    height: calc(100vh - 300px);
    position: sticky;
    top: 20px;
}

.google-map-component {
    width: 100%;
    height: 100%;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.itinerary-section {
    flex: 1;
    max-width: 500px;
}

.itinerary {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.itinerary h2 {
    font-size: 1.5rem;
    font-weight: 700;
    color: #1a1a1a;
    margin: 0 0 1.5rem;
}

/* Place Items */

.place-item {
    background: #f8f9fa;
    border-radius: 12px;
    overflow: hidden;
    transition: transform 0.2s ease;
}

.place-item:hover {
    transform: translateY(-2px);
}

.place-content {
    padding: 20px;
}

.place-header {
    display: flex;
    align-items: flex-start;
    gap: 16px;
}

.sequence {
    width: 32px;
    height: 32px;
    background: #5c6ac4;
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    font-size: 0.9rem;
    margin-bottom: 10px;
}

.place-main-info h3 {
    font-size: 1.1rem;
    font-weight: 600;
    margin: 0 0 8px;
    color: #1a1a1a;
}

.address {
    font-size: 0.9rem;
    color: #666;
    margin: 0 0 12px;
}

.place-details {
    display: flex;
    gap: 12px;
    margin-top: 12px;
}

.category {
    padding: 6px 12px;
    background: #eef2ff;
    color: #5c6ac4;
    border-radius: 8px;
    font-size: 0.8rem;
    font-weight: 500;
}

.rating {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #1a1a1a;
    font-size: 0.9rem;
}

.rating-stars {
    color: #fbbf24;
}

.review-count {
    color: #666;
}

.memo-section {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #e5e7eb;
}

.memo-content {
    font-size: 0.9rem;
    color: #4b5563;
    line-height: 1.6;
    padding: 16px;
    background: white;
    border-radius: 8px;
}

/* Like Button */
.like-button-container {
    margin: 2rem 0;
    display: flex;
    justify-content: center;
}

.like-button {
    padding: 12px 24px;
    font-size: 1rem;
    font-weight: 600;
    color: white;
    background: #5c6ac4;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    gap: 8px;
}

.like-button:hover {
    background: #4c5aa0;
    transform: translateY(-1px);
}

.like-button.liked {
    background: #4c5aa0;
}

.distance-wrapper {
    display: flex;
    justify-content: center;
    padding: 8px 0;
    margin: 16px 0;
}

/* Responsive Design */
@media (max-width: 1024px) {
    .plan-detail-view {
        padding: 0 24px;
    }

    .main-content {
        flex-direction: column;
    }

    .map-section {
        position: relative;
        height: 400px;
        top: 0;
    }

    .itinerary-section {
        max-width: 100%;
    }
}

@media (max-width: 768px) {
    .plan-detail-view {
        padding: 0 16px;
    }

    .title-info,
    .plan-info,
    .date-navigation,
    .itinerary {
        padding: 16px;
        border-radius: 12px;
    }

    .title-info h2 {
        font-size: 1.5rem;
    }

    .map-section {
        height: 300px;
    }

    .place-header {
        flex-direction: column;
    }

    .sequence {
        margin-bottom: 12px;
    }

    .place-details {
        flex-wrap: wrap;
    }
}

@media (max-width: 480px) {
    .plan-detail-view {
        padding: 0 12px;
    }

    .date-navigation {
        flex-direction: column;
        align-items: stretch;
        gap: 10px;
        text-align: center;
    }

    .author-actions {
        flex-direction: column;
    }

    .edit-button,
    .delete-button {
        width: 100%;
    }

    .like-button {
        width: 100%;
        justify-content: center;
    }
}
</style>
