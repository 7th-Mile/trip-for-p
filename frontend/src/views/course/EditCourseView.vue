<script setup>
import {onMounted, ref} from 'vue';
import SelectPlaceComponent from "@/components/course/SelectPlaceComponent.vue";
import {getCourseAPI} from "@/api/course";
import {useRoute} from "vue-router";

const route = useRoute();


const title = ref('');
const startDate = ref(null);
const endDate = ref(null);
const province = ref('');
const city = ref('');
const spots = ref([]);
const spotsData = history.state.spots;
const isLoading = ref(true);

const formatDate = (date) => {
    if (!date) {
        return '';
    }
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const getPlan = async function () {
    try {
        const response = await getCourseAPI(route.params.courseId);
        const plan = response.data;
        title.value = plan.title;
        province.value = plan.province
        city.value = plan.city;
        const start = new Date(plan.startDate);
        const end = plan.endDate ? new Date(plan.endDate) : start;

        // 변환된 날짜를 포맷에 맞게 가공
        startDate.value = formatDate(start);
        endDate.value = formatDate(end);

        spots.value = spotsData.map(item => ({
            id: item.id,
            place: {
                id: item.place.id,
                name: item.place.name,
                address: item.place.address,
                category: item.place.category,
                lat: item.place.latitude,
                lng: item.place.longitude
            },
            tripDate: item.tripDate,
            sequence: item.sequence,
            memo: item.memo
        }));
        isLoading.value = false;
    } catch (error) {
        console.log(error)
        isLoading.value = false;
    }
};

onMounted(() => {
    getPlan();
})
</script>

<template>
    <div class="update-plan-container">
        <div v-if="isLoading">Loading...</div>
        <SelectPlaceComponent
            v-else
            :title="title"
            :province="province"
            :city="city"
            :startDate="startDate"
            :endDate="endDate"
            :spots="spotsData || []"
            mode="update"
        />
    </div>
</template>

<style scoped>
.update-plan-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    margin-top: 50px;
}
</style>
