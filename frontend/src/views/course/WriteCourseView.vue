<script setup>
import {computed, onMounted, ref} from 'vue';
import SelectDayComponent from "@/components/course/SelectDayComponent.vue";
import SelectPlaceComponent from "@/components/course/SelectPlaceComponent.vue";

const currentStep = ref('day');
const startDate = ref(null);
const endDate = ref(null);

const currentComponent = computed(() => {
    switch (currentStep.value) {
        case 'day': return SelectDayComponent;
        case 'place': return SelectPlaceComponent;
        default: return SelectDayComponent;
    }
});

const handleDatesSelected = (dates) => {
    startDate.value = dates.start;
    endDate.value = dates.end;
    currentStep.value = 'place';
    setTimeout(() => {
        const button = document.querySelector('.map-search-container');
        if (button) {
            button.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }, 100);
};

const handleBackToDay = () => {
    currentStep.value = 'day';
};

const handleTripPlanCompleted = () => {
    console.log('Trip plan completed');
};

onMounted(() => {
});
</script>

<template>
    <div class="travel-planner-container">
        <div class="main-content">
            <component
                :is="currentComponent"
                :startDate="startDate"
                :endDate="endDate"
                @dates-selected="handleDatesSelected"
                @back-to-day="handleBackToDay"
                @trip-plan-completed="handleTripPlanCompleted"
            />
        </div>
    </div>
</template>

<style scoped>
.travel-planner-container {
    display: flex;
    width: 100%;
    height: auto;
    position: relative;
}

.main-content {
    flex-grow: 1;
    width: 100%;
    padding-top: 2em;
}
</style>
