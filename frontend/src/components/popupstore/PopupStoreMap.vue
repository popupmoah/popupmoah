<template>
  <div class="popup-store-map">
    <div
      ref="mapContainer"
      class="map-container"
      :style="{ height: height + 'px' }"
      role="application"
      :aria-label="`${store?.name || '팝업스토어'} 위치 지도`"
    ></div>

    <!-- 지도 로딩 상태 -->
    <div v-if="loading" class="map-loading-overlay">
      <q-spinner-dots size="50px" color="primary" />
      <p class="text-center mt-2">지도를 불러오는 중...</p>
    </div>

    <!-- 에러 상태 -->
    <div v-if="error" class="map-error-overlay">
      <q-icon name="error" size="3rem" color="negative" />
      <p class="text-center mt-2">{{ error }}</p>
      <q-btn color="primary" label="다시 시도" @click="initializeMap" class="mt-2" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import type { PopupStore } from '@/types'

interface Props {
  store?: PopupStore
  height?: number
  showControls?: boolean
  zoom?: number
  clickable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  height: 400,
  showControls: true,
  zoom: 15,
  clickable: false,
})

interface Emits {
  'map-click': [event: google.maps.MapMouseEvent]
}

const emit = defineEmits<Emits>()

const mapContainer = ref<HTMLDivElement>()
const loading = ref(true)
const error = ref<string | null>(null)
let map: google.maps.Map | null = null
let marker: google.maps.Marker | null = null

// Google Maps API 로드
const loadGoogleMapsAPI = (): Promise<void> => {
  return new Promise((resolve, reject) => {
    // 이미 로드된 경우
    if (window.google && window.google.maps) {
      resolve()
      return
    }

    // API 키는 환경변수에서 가져옴
    const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY
    if (!apiKey) {
      reject(new Error('Google Maps API 키가 설정되지 않았습니다.'))
      return
    }

    const script = document.createElement('script')
    script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey}&libraries=places`
    script.async = true
    script.defer = true

    script.onload = () => resolve()
    script.onerror = () => reject(new Error('Google Maps API 로드에 실패했습니다.'))

    document.head.appendChild(script)
  })
}

// 지도 초기화
const initializeMap = async () => {
  try {
    loading.value = true
    error.value = null

    await loadGoogleMapsAPI()

    if (!mapContainer.value) {
      throw new Error('지도 컨테이너를 찾을 수 없습니다.')
    }

    // 기본 위치 (서울 시청)
    const defaultLocation = { lat: 37.5665, lng: 126.978 }

    // 스토어 위치가 있으면 사용, 없으면 기본 위치 사용
    const location =
      props.store?.latitude && props.store?.longitude
        ? { lat: props.store.latitude, lng: props.store.longitude }
        : defaultLocation

    map = new google.maps.Map(mapContainer.value, {
      center: location,
      zoom: props.zoom,
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      mapTypeControl: props.showControls,
      streetViewControl: props.showControls,
      fullscreenControl: props.showControls,
      zoomControl: props.showControls,
      gestureHandling: 'cooperative',
      styles: [
        {
          featureType: 'poi',
          elementType: 'labels',
          stylers: [{ visibility: 'off' }],
        },
      ],
    })

    // 클릭 가능한 경우 클릭 이벤트 추가
    if (props.clickable) {
      map.addListener('click', (event: google.maps.MapMouseEvent) => {
        emit('map-click', event)
      })
    }

    // 마커 추가
    if (props.store?.latitude && props.store?.longitude) {
      marker = new google.maps.Marker({
        position: { lat: props.store.latitude, lng: props.store.longitude },
        map: map,
        title: props.store.name,
        animation: google.maps.Animation.DROP,
      })

      // 정보창 추가
      const infoWindow = new google.maps.InfoWindow({
        content: `
          <div style="padding: 10px; max-width: 200px;">
            <h3 style="margin: 0 0 5px 0; font-size: 16px; font-weight: bold;">${props.store.name}</h3>
            <p style="margin: 0; font-size: 14px; color: #666;">${props.store.location}</p>
            <p style="margin: 5px 0 0 0; font-size: 12px; color: #999;">${props.store.period}</p>
          </div>
        `,
      })

      marker.addListener('click', () => {
        infoWindow.open(map, marker)
      })
    }

    loading.value = false
  } catch (err) {
    console.error('지도 초기화 실패:', err)
    error.value = err instanceof Error ? err.message : '지도를 불러오는데 실패했습니다.'
    loading.value = false
  }
}

// 스토어 변경 시 지도 업데이트
watch(
  () => props.store,
  (newStore) => {
    if (map && newStore?.latitude && newStore?.longitude) {
      const newLocation = { lat: newStore.latitude, lng: newStore.longitude }

      // 기존 마커 제거
      if (marker) {
        marker.setMap(null)
      }

      // 지도 중심 이동
      map.setCenter(newLocation)

      // 새 마커 추가
      marker = new google.maps.Marker({
        position: newLocation,
        map: map,
        title: newStore.name,
        animation: google.maps.Animation.DROP,
      })

      // 정보창 추가
      const infoWindow = new google.maps.InfoWindow({
        content: `
        <div style="padding: 10px; max-width: 200px;">
          <h3 style="margin: 0 0 5px 0; font-size: 16px; font-weight: bold;">${newStore.name}</h3>
          <p style="margin: 0; font-size: 14px; color: #666;">${newStore.location}</p>
          <p style="margin: 5px 0 0 0; font-size: 12px; color: #999;">${newStore.period}</p>
        </div>
      `,
      })

      marker.addListener('click', () => {
        infoWindow.open(map, marker)
      })
    }
  },
  { deep: true },
)

onMounted(() => {
  nextTick(() => {
    initializeMap()
  })
})

onUnmounted(() => {
  if (marker) {
    marker.setMap(null)
  }
  if (map) {
    // Google Maps는 자동으로 정리됨
  }
})
</script>

<style scoped>
.popup-store-map {
  position: relative;
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.map-container {
  width: 100%;
  border-radius: 8px;
}

.map-loading-overlay,
.map-error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.map-error-overlay {
  background: rgba(255, 255, 255, 0.95);
}
</style>
