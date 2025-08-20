<template>
  <div class="map-viewer">
    <!-- 지도 제공자 선택 -->
    <div class="map-provider-selector">
      <q-btn-toggle
        v-model="selectedProvider"
        :options="providerOptions"
        color="primary"
        toggle-color="primary"
        @update:model-value="onProviderChange"
      />
    </div>

    <!-- 카카오맵 -->
    <KakaoMap
      v-if="selectedProvider === 'kakao'"
      ref="kakaoMapRef"
      :width="width"
      :height="height"
      :center="center"
      :level="kakaoLevel"
      :markers="markers"
      :draggable="draggable"
      :scrollwheel="scrollwheel"
      @map-loaded="onMapLoaded"
      @marker-click="onMarkerClick"
      @map-click="onMapClick"
      @drag-end="onDragEnd"
      @zoom-changed="onZoomChanged"
    />

    <!-- 네이버맵 -->
    <NaverMap
      v-if="selectedProvider === 'naver'"
      ref="naverMapRef"
      :width="width"
      :height="height"
      :center="center"
      :zoom="naverZoom"
      :markers="markers"
      :draggable="draggable"
      :scrollwheel="scrollwheel"
      @map-loaded="onMapLoaded"
      @marker-click="onMarkerClick"
      @map-click="onMapClick"
      @drag-end="onDragEnd"
      @zoom-changed="onZoomChanged"
    />

    <!-- 지도 컨트롤 -->
    <MapControls
      :map="currentMap"
      :provider="selectedProvider"
      :show-zoom-level="true"
      :is-loading="loading"
      @location-found="onLocationFound"
      @zoom-changed="onZoomChanged"
      @layer-changed="onLayerChanged"
      @fullscreen-toggled="onFullscreenToggled"
    />

    <!-- 마커 클러스터링 -->
    <MarkerClusterer
      v-if="enableClustering"
      :markers="markers"
      :map="currentMap"
      :provider="selectedProvider"
      :show-settings="showClusterSettings"
      @cluster-updated="onClusterUpdated"
      @cluster-click="onClusterClick"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import KakaoMap from './KakaoMap.vue'
import NaverMap from './NaverMap.vue'
import MapControls from './MapControls.vue'
import MarkerClusterer from './MarkerClusterer.vue'
import { MAP_CONFIG, type Coordinates, type MapProvider, validateMapApiKeys } from '@/config/maps'

interface Props {
  width?: string
  height?: string
  center?: Coordinates
  zoom?: number
  markers?: Array<{
    position: Coordinates
    title?: string
    content?: string
    clickable?: boolean
  }>
  draggable?: boolean
  scrollwheel?: boolean
  defaultProvider?: MapProvider
  showProviderSelector?: boolean
  showControls?: boolean
  enableClustering?: boolean
  showClusterSettings?: boolean
}

interface Emits {
  (e: 'map-loaded', map: any, provider: MapProvider): void
  (e: 'marker-click', marker: any, index: number, provider: MapProvider): void
  (e: 'map-click', latlng: any, provider: MapProvider): void
  (e: 'drag-end', latlng: any, provider: MapProvider): void
  (e: 'zoom-changed', zoom: number, provider: MapProvider): void
  (e: 'provider-changed', provider: MapProvider): void
  (e: 'location-found', coordinates: Coordinates): void
  (e: 'layer-changed', layer: string): void
  (e: 'fullscreen-toggled', isFullscreen: boolean): void
  (e: 'cluster-click', cluster: any): void
}

const props = withDefaults(defineProps<Props>(), {
  width: '100%',
  height: '400px',
  center: () => MAP_CONFIG.COMMON.DEFAULT_CENTER,
  zoom: 10,
  markers: () => [],
  draggable: true,
  scrollwheel: true,
  defaultProvider: 'kakao',
  showProviderSelector: true,
  showControls: true,
  enableClustering: false,
  showClusterSettings: false,
})

const emit = defineEmits<Emits>()

const selectedProvider = ref<MapProvider>(props.defaultProvider)
const gettingLocation = ref(false)
const kakaoMapRef = ref()
const naverMapRef = ref()

// API 키 유효성 검사
const apiKeysValid = validateMapApiKeys()

// 사용 가능한 지도 제공자 옵션
const providerOptions = computed(() => {
  const options = []

  if (apiKeysValid.kakao) {
    options.push({ label: '카카오맵', value: 'kakao' })
  }

  if (apiKeysValid.naver) {
    options.push({ label: '네이버맵', value: 'naver' })
  }

  return options
})

// 카카오맵 레벨 (줌 레벨을 카카오맵 레벨로 변환)
const kakaoLevel = computed(() => {
  // 네이버맵 줌 레벨을 카카오맵 레벨로 변환 (대략적인 변환)
  const zoom = props.zoom || 10
  return Math.max(1, Math.min(14, 15 - zoom))
})

// 네이버맵 줌 레벨
const naverZoom = computed(() => props.zoom || 10)

// 현재 활성화된 지도 참조
const currentMapRef = computed(() => {
  return selectedProvider.value === 'kakao' ? kakaoMapRef.value : naverMapRef.value
})

// 지도 제공자 변경
const onProviderChange = (provider: MapProvider) => {
  selectedProvider.value = provider
  emit('provider-changed', provider)
}

// 지도 로드 완료
const onMapLoaded = (map: any) => {
  emit('map-loaded', map, selectedProvider.value)
}

// 마커 클릭
const onMarkerClick = (marker: any, index: number) => {
  emit('marker-click', marker, index, selectedProvider.value)
}

// 지도 클릭
const onMapClick = (latlng: any) => {
  emit('map-click', latlng, selectedProvider.value)
}

// 드래그 종료
const onDragEnd = (latlng: any) => {
  emit('drag-end', latlng, selectedProvider.value)
}

// 줌 변경
const onZoomChanged = (zoom: number) => {
  emit('zoom-changed', zoom, selectedProvider.value)
}

// 현재 위치 가져오기
const getCurrentLocation = () => {
  if (!navigator.geolocation) {
    console.error('Geolocation is not supported by this browser.')
    return
  }

  gettingLocation.value = true

  navigator.geolocation.getCurrentPosition(
    (position) => {
      const coordinates: Coordinates = {
        lat: position.coords.latitude,
        lng: position.coords.longitude,
      }

      setCenter(coordinates)
      emit('location-found', coordinates)
      gettingLocation.value = false
    },
    (error) => {
      console.error('Error getting location:', error)
      gettingLocation.value = false
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 60000,
    },
  )
}

// 지도 중심점 설정
const setCenter = (coordinates: Coordinates) => {
  if (currentMapRef.value) {
    currentMapRef.value.setCenter(coordinates)
  }
}

// 줌 인
const zoomIn = () => {
  if (currentMapRef.value) {
    if (selectedProvider.value === 'kakao') {
      const currentLevel = currentMapRef.value.map.getLevel()
      currentMapRef.value.setLevel(currentLevel - 1)
    } else {
      const currentZoom = currentMapRef.value.map.getZoom()
      currentMapRef.value.setZoom(currentZoom + 1)
    }
  }
}

// 줌 아웃
const zoomOut = () => {
  if (currentMapRef.value) {
    if (selectedProvider.value === 'kakao') {
      const currentLevel = currentMapRef.value.map.getLevel()
      currentMapRef.value.setLevel(currentLevel + 1)
    } else {
      const currentZoom = currentMapRef.value.map.getZoom()
      currentMapRef.value.setZoom(currentZoom - 1)
    }
  }
}

// 지도 크기 조정
const relayout = () => {
  if (currentMapRef.value) {
    currentMapRef.value.relayout()
  }
}

// 마커 추가
const addMarkers = (newMarkers: any[]) => {
  if (currentMapRef.value) {
    currentMapRef.value.addMarkers(newMarkers)
  }
}

// 마커 제거
const clearMarkers = () => {
  if (currentMapRef.value) {
    currentMapRef.value.clearMarkers()
  }
}

// props 변경 감지
watch(
  () => props.center,
  (newCenter) => {
    if (newCenter) {
      setCenter(newCenter)
    }
  },
)

// New event handlers
const onLayerChanged = (layer: string) => {
  emit('layer-changed', layer)
}

const onFullscreenToggled = (isFullscreen: boolean) => {
  emit('fullscreen-toggled', isFullscreen)
}

const onClusterUpdated = (clusters: any[]) => {
  console.log('클러스터 업데이트됨:', clusters.length, '개')
}

const onClusterClick = (cluster: any) => {
  console.log('클러스터 클릭됨:', cluster)
  emit('cluster-click', cluster)

  // 클러스터 중심으로 지도 이동
  if (cluster.center && mapViewerRef.value) {
    mapViewerRef.value.setCenter(cluster.center)
  }
}

// 외부에서 사용할 수 있는 메서드들
defineExpose({
  selectedProvider,
  setCenter,
  zoomIn,
  zoomOut,
  relayout,
  addMarkers,
  clearMarkers,
  getCurrentLocation,
})
</script>

<style scoped>
.map-viewer {
  position: relative;
  width: 100%;
  height: 100%;
}

.map-provider-selector {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  padding: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.map-controls {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.map-controls .q-btn {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.map-controls .q-btn:hover {
  background: rgba(255, 255, 255, 1);
}
</style>
