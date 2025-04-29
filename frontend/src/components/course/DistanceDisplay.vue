<script setup>
import {computed, defineProps} from 'vue';

const props = defineProps({
    originLat: {
        type: Number,
        required: true
    },
    originLon: {
        type: Number,
        required: true
    },
    destinationLat: {
        type: Number,
        required: true
    },
    destinationLon: {
        type: Number,
        required: true
    },
    unit: {
        type: String,
        default: 'km',
        validator: (value) => ['km', 'm'].includes(value)
    },
    decimals: {
        type: Number,
        default: 1
    }
});

const calculateDistance = (lat1, lon1, lat2, lon2) => {
    // 좌표 값이 유효한지 확인
    if (!lat1 || !lon1 || !lat2 || !lon2) {
        return null;
    }

    const R = 6371;
    const lat1Rad = (lat1 * Math.PI) / 180;
    const lon1Rad = (lon1 * Math.PI) / 180;
    const lat2Rad = (lat2 * Math.PI) / 180;
    const lon2Rad = (lon2 * Math.PI) / 180;

    const dLat = lat2Rad - lat1Rad;
    const dLon = lon2Rad - lon1Rad;

    const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1Rad) * Math.cos(lat2Rad) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
};

const distance = computed(() => {
    const kmDistance = calculateDistance(
        props.originLat,
        props.originLon,
        props.destinationLat,
        props.destinationLon
    );

    if (kmDistance === null) {
        return '-';
    }

    if (props.unit === 'm') {
        return `${(kmDistance * 1000).toFixed(props.decimals)}`;
    }
    return `${kmDistance.toFixed(props.decimals)}`;
});
</script>

<template>
    <div class="distance-display" v-if="distance !== '-'">
        <span class="distance-icon">↕</span>
        <span class="distance-value">{{ distance }}</span>
        <span class="distance-unit">{{ unit }}</span>
    </div>
</template>

<style scoped>
.distance-display {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 4px 8px;
    font-size: 0.875rem;
    color: #5c6ac4;
    background-color: rgba(92, 106, 196, 0.1);
    border-radius: 4px;
}

.distance-icon {
    color: #8794d8;
}

.distance-value {
    font-weight: 500;
}

.distance-unit {
    color: #8794d8;
    font-size: 0.75rem;
}
</style>
