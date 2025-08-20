<template>
  <div class="map-controls">
    <!-- 줌 컨트롤 -->
    <div class="zoom-controls">
      <q-btn
        icon="add"
        size="sm"
        round
        color="white"
        text-color="primary"
        @click="zoomIn"
        :disable="!map"
      >
        <q-tooltip>확대</q-tooltip>
      </q-btn>
      <q-btn
        icon="remove"
        size="sm"
        round
        color="white"
        text-color="primary"
        @click="zoomOut"
        :disable="!map"
      >
        <q-tooltip>축소</q-tooltip>
      </q-btn>
    </div>

    <!-- 현재 위치 버튼 -->
    <div class="location-control">
      <q-btn
        icon="my_location"
        size="sm"
        round
        color="white"
        text-color="primary"
        @click="getCurrentLocation"
        :loading="gettingLocation"
        :disable="!map"
      >
        <q-tooltip>현재 위치</q-tooltip>
      </q-btn>
    </div>

    <!-- 레이어 선택 -->
    <div v-if="showLayerSelector" class="layer-control">
      <q-btn
        icon="layers"
        size="sm"
        round
        color="white"
        text-color="primary"
        @click="showLayerMenu = !showLayerMenu"
        :disable="!map"
      >
        <q-tooltip>레이어</q-tooltip>
      </q-btn>

      <q-menu v-model="showLayerMenu" anchor="bottom left" self="top left">
        <q-list style="min-width: 120px">
          <q-item
            v-for="layer in layerOptions"
            :key="layer.value"
            clickable
            v-close-popup
            @click="changeLayer(layer.value)"
            :active="currentLayer === layer.value"
          >
            <q-item-section avatar>
              <q-icon :name="layer.icon" />
            </q-item-section>
            <q-item-section>{{ layer.label }}</q-item-section>
          </q-item>
        </q-list>
      </q-menu>
    </div>

    <!-- 전체화면 버튼 -->
    <div class="fullscreen-control">
      <q-btn
        :icon="isFullscreen ? 'fullscreen_exit' : 'fullscreen'"
        size="sm"
        round
        color="white"
        text-color="primary"
        @click="toggleFullscreen"
        :disable="!map"
      >
        <q-tooltip>{{ isFullscreen ? '전체화면 해제' : '전체화면' }}</q-tooltip>
      </q-btn>
    </div>

    <!-- 줌 레벨 표시 -->
    <div v-if="showZoomLevel" class="zoom-level">
      <q-chip :label="`줌: ${currentZoom}`" size="sm" color="white" text-color="primary" dense />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { MapProvider } from '@/types'

interface Props {
  map: any
  provider: MapProvider
  showZoomLevel?: boolean
  showLayerSelector?: boolean
  isLoading?: boolean
}

interface Emits {
  (e: 'location-found', coordinates: { lat: number; lng: number }): void
  (e: 'zoom-changed', zoom: number): void
  (e: 'layer-changed', layer: string): void
  (e: 'fullscreen-toggled', isFullscreen: boolean): void
}

const props = withDefaults(defineProps<Props>(), {
  showZoomLevel: true,
  showLayerSelector: true,
  isLoading: false,
})

const emit = defineEmits<Emits>()

// 상태
const gettingLocation = ref(false)
const showLayerMenu = ref(false)
const isFullscreen = ref(false)
const currentLayer = ref('standard')
const currentZoom = ref(10)

// 레이어 옵션
const layerOptions = [
  { label: '일반', value: 'standard', icon: 'map' },
  { label: '위성', value: 'satellite', icon: 'satellite' },
  { label: '하이브리드', value: 'hybrid', icon: 'layers' },
  { label: '지형', value: 'terrain', icon: 'terrain' },
]

// 메서드들
const zoomIn = () => {
  if (!props.map) return

  if (props.provider === 'kakao') {
    const level = props.map.getLevel()
    props.map.setLevel(level - 1)
    currentZoom.value = level - 1
  } else if (props.provider === 'naver') {
    const zoom = props.map.getZoom()
    props.map.setZoom(zoom + 1)
    currentZoom.value = zoom + 1
  }

  emit('zoom-changed', currentZoom.value)
}

const zoomOut = () => {
  if (!props.map) return

  if (props.provider === 'kakao') {
    const level = props.map.getLevel()
    props.map.setLevel(level + 1)
    currentZoom.value = level + 1
  } else if (props.provider === 'naver') {
    const zoom = props.map.getZoom()
    props.map.setZoom(zoom - 1)
    currentZoom.value = zoom - 1
  }

  emit('zoom-changed', currentZoom.value)
}

const getCurrentLocation = async () => {
  if (!navigator.geolocation) {
    console.error('Geolocation is not supported by this browser.')
    return
  }

  gettingLocation.value = true

  try {
    const position = await new Promise<GeolocationPosition>((resolve, reject) => {
      navigator.geolocation.getCurrentPosition(resolve, reject, {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 300000,
      })
    })

    const { latitude, longitude } = position.coords
    const coordinates = { lat: latitude, lng: longitude }

    // 지도 중심 이동
    if (props.map) {
      if (props.provider === 'kakao') {
        const moveLatLon = new (window as any).kakao.maps.LatLng(latitude, longitude)
        props.map.setCenter(moveLatLon)
      } else if (props.provider === 'naver') {
        const moveLatLon = new (window as any).naver.maps.LatLng(latitude, longitude)
        props.map.setCenter(moveLatLon)
      }
    }

    emit('location-found', coordinates)
  } catch (error) {
    console.error('Error getting current location:', error)
  } finally {
    gettingLocation.value = false
  }
}

const changeLayer = (layer: string) => {
  if (!props.map) return

  currentLayer.value = layer

  if (props.provider === 'kakao') {
    const mapTypeId = getKakaoMapTypeId(layer)
    props.map.setMapTypeId(mapTypeId)
  } else if (props.provider === 'naver') {
    const mapTypeId = getNaverMapTypeId(layer)
    props.map.setMapTypeId(mapTypeId)
  }

  emit('layer-changed', layer)
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    // 전체화면 진입
    const mapContainer = props.map?.getContainer?.() || document.querySelector('.map-viewer')
    if (mapContainer && mapContainer.requestFullscreen) {
      mapContainer.requestFullscreen()
      isFullscreen.value = true
    }
  } else {
    // 전체화면 해제
    if (document.exitFullscreen) {
      document.exitFullscreen()
      isFullscreen.value = false
    }
  }

  emit('fullscreen-toggled', isFullscreen.value)
}

// 카카오맵 타입 ID 변환
const getKakaoMapTypeId = (layer: string) => {
  const typeMap: Record<string, any> = {
    standard: (window as any).kakao.maps.MapTypeId.ROADMAP,
    satellite: (window as any).kakao.maps.MapTypeId.SATELLITE,
    hybrid: (window as any).kakao.maps.MapTypeId.HYBRID,
    terrain: (window as any).kakao.maps.MapTypeId.ROADMAP, // 카카오맵은 지형 타입이 없음
  }
  return typeMap[layer] || typeMap.standard
}

// 네이버맵 타입 ID 변환
const getNaverMapTypeId = (layer: string) => {
  const typeMap: Record<string, any> = {
    standard: (window as any).naver.maps.MapTypeId.NORMAL,
    satellite: (window as any).naver.maps.MapTypeId.SATELLITE,
    hybrid: (window as any).naver.maps.MapTypeId.HYBRID,
    terrain: (window as any).naver.maps.MapTypeId.TERRAIN,
  }
  return typeMap[layer] || typeMap.standard
}

// 전체화면 상태 변경 감지
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
  emit('fullscreen-toggled', isFullscreen.value)
}

// 이벤트 리스너 등록
if (typeof window !== 'undefined') {
  document.addEventListener('fullscreenchange', handleFullscreenChange)
}
</script>

<style scoped>
.map-controls {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.zoom-controls {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.location-control,
.layer-control,
.fullscreen-control {
  display: flex;
  justify-content: center;
}

.zoom-level {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.q-btn {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.q-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.q-chip {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
</style>
