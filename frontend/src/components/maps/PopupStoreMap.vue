<template>
  <div class="popup-store-map">
    <!-- 주변 검색 패널 -->
    <div v-if="showNearbySearch" class="nearby-search-panel">
      <NearbySearch
        :center="mapCenter"
        @place-selected="onPlaceSelected"
        @location-found="onLocationFound"
        @search-completed="onSearchCompleted"
      />
    </div>

    <!-- 지도 뷰어 -->
    <MapViewer
      ref="mapViewerRef"
      :width="width"
      :height="height"
      :center="mapCenter"
      :zoom="zoom"
      :markers="mapMarkers"
      :default-provider="defaultProvider"
      :show-provider-selector="showProviderSelector"
      :show-controls="showControls"
      :enable-clustering="enableClustering"
      :show-cluster-settings="showClusterSettings"
      @map-loaded="onMapLoaded"
      @marker-click="onMarkerClick"
      @map-click="onMapClick"
      @provider-changed="onProviderChanged"
      @cluster-click="onClusterClick"
    />

    <!-- 팝업스토어 정보 패널 -->
    <div v-if="selectedPopupStore" class="popup-store-info-panel">
      <q-card class="info-card">
        <q-card-section>
          <div class="row items-center">
            <div class="col">
              <div class="text-h6">{{ selectedPopupStore.name }}</div>
              <div class="text-caption text-grey-6">{{ selectedPopupStore.category }}</div>
            </div>
            <div class="col-auto">
              <q-btn icon="close" flat round dense @click="clearSelectedPopupStore" />
            </div>
          </div>
        </q-card-section>

        <q-card-section v-if="selectedPopupStore.address" class="q-pt-none">
          <div class="text-body2">
            <q-icon name="place" class="q-mr-xs" />
            {{ selectedPopupStore.address }}
          </div>
        </q-card-section>

        <q-card-actions>
          <q-btn flat color="primary" label="상세보기" @click="viewPopupStoreDetail" />
          <q-btn flat color="secondary" label="길찾기" @click="openDirections" />
        </q-card-actions>
      </q-card>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-overlay">
      <q-spinner size="40px" color="primary" />
      <p>팝업스토어 정보를 불러오는 중...</p>
    </div>

    <!-- 에러 상태 -->
    <div v-if="error" class="error-overlay">
      <q-icon name="error" size="40px" color="negative" />
      <p>{{ error }}</p>
      <q-btn flat color="primary" label="다시 시도" @click="loadPopupStoreLocations" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import MapViewer from './MapViewer.vue'
import NearbySearch from './NearbySearch.vue'
import { useMapStore } from '@/stores/mapStore'
import type { Coordinates, MapProvider, PopupStoreLocation, MapMarker, PlaceInfo } from '@/types'

interface Props {
  width?: string
  height?: string
  center?: Coordinates
  zoom?: number
  defaultProvider?: MapProvider
  showProviderSelector?: boolean
  showControls?: boolean
  showActiveOnly?: boolean
  showNearbySearch?: boolean
  enableClustering?: boolean
  showClusterSettings?: boolean
  autoLoad?: boolean
}

interface Emits {
  (e: 'popup-store-selected', popupStore: PopupStoreLocation): void
  (e: 'popup-store-detail', popupStore: PopupStoreLocation): void
  (e: 'map-loaded', map: any, provider: MapProvider): void
  (e: 'place-selected', place: PlaceInfo): void
  (e: 'location-found', coordinates: Coordinates): void
}

const props = withDefaults(defineProps<Props>(), {
  width: '100%',
  height: '500px',
  center: () => ({ lat: 37.5665, lng: 126.978 }), // 서울시청
  zoom: 10,
  defaultProvider: 'kakao',
  showProviderSelector: true,
  showControls: true,
  showActiveOnly: false,
  showNearbySearch: false,
  enableClustering: true,
  showClusterSettings: false,
  autoLoad: true,
})

const emit = defineEmits<Emits>()

const router = useRouter()
const mapStore = useMapStore()
const mapViewerRef = ref()

// State
const selectedPopupStore = ref<PopupStoreLocation | null>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)

// Computed
const mapCenter = computed(() => {
  if (mapStore.currentLocation) {
    return mapStore.currentLocation
  }
  return props.center
})

const mapMarkers = computed((): MapMarker[] => {
  const locations = props.showActiveOnly
    ? mapStore.popupStoreLocations.filter((loc) => loc.active)
    : mapStore.popupStoreLocations

  return locations.map((location) => ({
    position: location.coordinates,
    title: location.name,
    content: createMarkerContent(location),
    clickable: true,
  }))
})

// Methods
const createMarkerContent = (popupStore: PopupStoreLocation): string => {
  return `
    <div class="marker-content">
      <div class="marker-title">${popupStore.name}</div>
      <div class="marker-category">${popupStore.category || '카테고리 없음'}</div>
      ${popupStore.address ? `<div class="marker-address">${popupStore.address}</div>` : ''}
    </div>
  `
}

const loadPopupStoreLocations = async () => {
  try {
    isLoading.value = true
    error.value = null

    if (props.showActiveOnly) {
      // 활성화된 팝업스토어만 로드하는 API 호출
      await mapStore.fetchPopupStoreLocations()
    } else {
      await mapStore.fetchPopupStoreLocations()
    }
  } catch (err: any) {
    error.value = err.message || '팝업스토어 정보를 불러오는데 실패했습니다.'
  } finally {
    isLoading.value = false
  }
}

const onMapLoaded = (map: any, provider: MapProvider) => {
  emit('map-loaded', map, provider)
}

const onMarkerClick = (marker: any, index: number, provider: MapProvider) => {
  const locations = props.showActiveOnly
    ? mapStore.popupStoreLocations.filter((loc) => loc.active)
    : mapStore.popupStoreLocations

  if (index < locations.length) {
    const popupStore = locations[index]
    selectedPopupStore.value = popupStore
    emit('popup-store-selected', popupStore)
  }
}

const onMapClick = (latlng: any, provider: MapProvider) => {
  // 지도 클릭 시 선택된 팝업스토어 해제
  clearSelectedPopupStore()
}

const onProviderChanged = (provider: MapProvider) => {
  mapStore.setProvider(provider)
}

const clearSelectedPopupStore = () => {
  selectedPopupStore.value = null
}

const viewPopupStoreDetail = () => {
  if (selectedPopupStore.value) {
    emit('popup-store-detail', selectedPopupStore.value)
    router.push(`/popupstores/${selectedPopupStore.value.id}`)
  }
}

const openDirections = () => {
  if (selectedPopupStore.value) {
    const { lat, lng } = selectedPopupStore.value.coordinates
    const url = `https://map.kakao.com/link/to/${selectedPopupStore.value.name},${lat},${lng}`
    window.open(url, '_blank')
  }
}

const getCurrentLocation = async () => {
  try {
    await mapStore.getCurrentLocationFromBrowser()
    if (mapViewerRef.value) {
      mapViewerRef.value.setCenter(mapStore.currentLocation!)
    }
  } catch (err: any) {
    console.error('현재 위치 가져오기 실패:', err)
  }
}

const setCenter = (coordinates: Coordinates) => {
  if (mapViewerRef.value) {
    mapViewerRef.value.setCenter(coordinates)
  }
}

const zoomIn = () => {
  if (mapViewerRef.value) {
    mapViewerRef.value.zoomIn()
  }
}

const zoomOut = () => {
  if (mapViewerRef.value) {
    mapViewerRef.value.zoomOut()
  }
}

// Lifecycle
onMounted(async () => {
  if (props.autoLoad) {
    await loadPopupStoreLocations()
  }
})

// Watchers
watch(
  () => props.showActiveOnly,
  () => {
    if (props.autoLoad) {
      loadPopupStoreLocations()
    }
  },
)

// Nearby Search Event Handlers
const onPlaceSelected = (place: PlaceInfo) => {
  // 선택된 장소로 지도 중심 이동
  if (mapViewerRef.value) {
    mapViewerRef.value.setCenter(place.coordinates)
  }
  emit('place-selected', place)
}

const onLocationFound = (coordinates: Coordinates) => {
  // 현재 위치로 지도 중심 이동
  if (mapViewerRef.value) {
    mapViewerRef.value.setCenter(coordinates)
  }
  emit('location-found', coordinates)
}

const onSearchCompleted = (results: PlaceInfo[]) => {
  // 검색 결과를 지도에 마커로 표시할 수 있음
  console.log('검색 완료:', results.length, '개 결과')
}

const onClusterClick = (cluster: any) => {
  console.log('클러스터 클릭됨:', cluster)
  // 클러스터 내의 팝업스토어들을 표시하거나 필터링할 수 있음
}

// Expose methods for parent components
defineExpose({
  loadPopupStoreLocations,
  getCurrentLocation,
  setCenter,
  zoomIn,
  zoomOut,
  clearSelectedPopupStore,
})
</script>

<style scoped>
.popup-store-map {
  position: relative;
  width: 100%;
  height: 100%;
}

.nearby-search-panel {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 1000;
  max-width: 400px;
  width: 90%;
  max-height: 60vh;
  overflow-y: auto;
}

.popup-store-info-panel {
  position: absolute;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  max-width: 400px;
  width: 90%;
}

.info-card {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-radius: 12px;
}

.loading-overlay,
.error-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.loading-overlay p,
.error-overlay p {
  margin: 10px 0 0 0;
  color: #666;
}

.error-overlay p {
  color: #d32f2f;
}

/* 마커 콘텐츠 스타일 */
:deep(.marker-content) {
  padding: 8px;
  font-size: 12px;
  line-height: 1.4;
}

:deep(.marker-title) {
  font-weight: bold;
  color: #1976d2;
  margin-bottom: 4px;
}

:deep(.marker-category) {
  color: #666;
  font-size: 11px;
  margin-bottom: 2px;
}

:deep(.marker-address) {
  color: #888;
  font-size: 10px;
}
</style>
