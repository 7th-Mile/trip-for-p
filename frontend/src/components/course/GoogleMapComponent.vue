<script setup>
/* global google */
import {defineProps, nextTick, ref, watch, onMounted} from 'vue';
import {GoogleMap, Marker, Polyline} from 'vue3-google-map';
import {mapLoadAPI} from "@/api/google";
import store from "@/store";
import router from "@/router";

const props = defineProps({
    places: {
        type: Array,
        required: true
    },
    date: {
        type: String,
        required: true
    },
    apiKey: {
        type: String,
        required: true
    }
});

const mapCenter = ref({ lat: 36.3, lng: 127.8 });
const mapRef = ref(null);
const markers = ref([]);
const mapZoom = ref(7);
const path = ref([]);
const isMapReady = ref(false);
const isPolylineVisible = ref(false);

const updatePolylinePath = () => {
    path.value = props.places
    .filter(place => place.place?.latitude && place.place?.longitude)
    .map(place => ({
        lat: place.place.latitude,
        lng: place.place.longitude
    }));
};

const adjustMapBounds = () => {
    if (!mapRef.value?.map || props.places.length === 0) {
        mapZoom.value = 7;
        return;
    }

    const bounds = new google.maps.LatLngBounds();
    props.places.forEach(place => {
        if (place.place?.latitude && place.place?.longitude) {
            bounds.extend(new google.maps.LatLng(
                place.place.latitude,
                place.place.longitude
            ));
        }
    });

    const padding = {
        top: 100,
        right: 100,
        bottom: 100,
        left: 100
    };

    mapRef.value.map.fitBounds(bounds, padding);

    // 줌 레벨 조정
    google.maps.event.addListenerOnce(mapRef.value.map, 'bounds_changed', () => {
        const currentZoom = mapRef.value?.map.getZoom();
        if (currentZoom > 16) {
            mapRef.value.map.setZoom(16);
        }
        mapZoom.value = mapRef.value.map.getZoom();
        isPolylineVisible.value = true;
    });
};

const createMarkers = () => {
    if (!isMapReady.value || !mapRef.value?.map) return;

    markers.value = props.places
    .filter(place => place.place?.latitude && place.place?.longitude)
    .map((place) => ({
        position: {
            lat: place.place.latitude,
            lng: place.place.longitude
        },
        title: place.place.name,
        icon: {
            path: 'M12,0C7.6,0,3,3.4,3,9c0,5.3,8,13.4,8.3,13.7c0.2,0.2,0.4,0.3,0.7,0.3s0.5-0.1,0.7-0.3C13,22.4,21,14.3,21,9 C21,3.4,16.4,0,12,0z',
            fillColor: '#FF385C',
            fillOpacity: 1,
            strokeColor: '#FFFFFF',
            strokeWeight: 2,
            scale: 1.5,
            anchor: new google.maps.Point(12, 24),
            labelOrigin: new google.maps.Point(12, 10)
        },
        label: {
            text: place.sequence + '',
            color: 'white',
            fontSize: '14px',
            fontWeight: 'bold',
            fontFamily: 'Arial'
        }
    }));

    updatePolylinePath();
    adjustMapBounds();
};

const initMap = () => {
    if (!mapRef.value?.map) return;

    mapRef.value.map.setOptions({
        gestureHandling: 'cooperative'
    });

    isMapReady.value = true;
    createMarkers();

    // 초기 렌더링 시 폴리라인 표시
    nextTick(() => {
        setTimeout(() => {
            isPolylineVisible.value = true;
        }, 100);
    });
};

const mapLoad = async function () {
    try {
        await mapLoadAPI();
    } catch (error) {
        if (error.status === 429) {
            if (store.getters.isAccessTokenValid()) {
                alert("일일한도량을 초과하였습니다.");
                await router.push('/');
            } else {
                if (window.confirm(
                    "일일한도량을 초과하였습니다.\n로그인 페이지로 이동하시겠습니까?")) {
                    await router.push('/login');
                } else {
                    await router.push('/');
                }
            }
        }
    }
};

// Map ready 감지
watch(() => mapRef.value?.ready, (ready) => {
    if (!ready) return;
    initMap();
    mapLoad();
});

// Places 변경 감지
watch(() => props.places, () => {
    if (isMapReady.value) {
        isPolylineVisible.value = false;  // 폴리라인 일시적으로 숨기기
        nextTick(() => {
            createMarkers();
            isPolylineVisible.value = true;
        });
    }
}, {deep: true});

onMounted(() => {
    if (mapRef.value?.ready) {
        initMap();
    }
});
</script>

<template>
    <div class="map-container">
        <GoogleMap
            ref="mapRef"
            :api-key="apiKey"
            mapId="DEMO_MAP_ID"
            :center="mapCenter"
            :zoom="mapZoom"
            class="map"
        >
            <template v-if="isMapReady">
                <Marker
                    v-for="marker in markers"
                    :key="`${marker.position.lat}-${marker.position.lng}-${marker.label.text}`"
                    :options="marker"
                />
                <Polyline
                    v-if="path.length >= 2 && isPolylineVisible"
                    :options="{
                        path: path,
                        strokeColor: '#FF385C',
                        strokeWeight: 3,
                        strokeOpacity: 1,
                        geodesic: true,
                    }"
                />
            </template>
        </GoogleMap>
    </div>
</template>

<style scoped>
.map-container {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}

.map {
    border-radius: 8px 8px 0 0;
    width: 100%;
    height: 100%;
}

@media (max-width: 1024px) {
    .map {
        height: 500px;
    }
}
</style>
