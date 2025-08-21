<template>
  <div class="kakao-map-container">
    <div ref="mapContainer" class="kakao-map" :style="{ width: width, height: height }"></div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="map-loading">
      <q-spinner size="40px" color="primary" />
      <p>지도를 불러오는 중...</p>
    </div>

    <!-- 에러 상태 -->
    <div v-if="error" class="map-error">
      <q-icon name="error" size="40px" color="negative" />
      <p>{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { MAP_CONFIG, type Coordinates, type MapProvider } from '@/config/maps'

interface Props {
  width?: string
  height?: string
  center?: Coordinates
  level?: number
  markers?: Array<{
    position: Coordinates
    title?: string
    content?: string
    clickable?: boolean
  }>
  draggable?: boolean
  scrollwheel?: boolean
  disableDoubleClick?: boolean
  disableDoubleClickZoom?: boolean
  projectionId?: string
  tileAnimation?: boolean
  keyboardShortcuts?: boolean
}

interface Emits {
  (e: 'map-loaded', map: any): void
  (e: 'marker-click', marker: any, index: number): void
  (e: 'map-click', latlng: any): void
  (e: 'drag-end', latlng: any): void
  (e: 'zoom-changed', level: number): void
}

const props = withDefaults(defineProps<Props>(), {
  width: '100%',
  height: '400px',
  center: () => MAP_CONFIG.COMMON.DEFAULT_CENTER,
  level: 3,
  markers: () => [],
  draggable: true,
  scrollwheel: true,
  disableDoubleClick: false,
  disableDoubleClickZoom: false,
  projectionId: 'WGS84',
  tileAnimation: true,
  keyboardShortcuts: true,
})

const emit = defineEmits<Emits>()

const mapContainer = ref<HTMLElement>()
const map = ref<any>(null)
const markers = ref<any[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

// 카카오맵 API 로드 확인
const isKakaoMapLoaded = () => {
  return typeof window !== 'undefined' && window.kakao && window.kakao.maps
}

// 카카오맵 API 로드
const loadKakaoMapAPI = (): Promise<void> => {
  return new Promise((resolve, reject) => {
    if (isKakaoMapLoaded()) {
      resolve()
      return
    }

    const script = document.createElement('script')
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${MAP_CONFIG.KAKAO.API_KEY}&autoload=false`
    script.onload = () => {
      window.kakao.maps.load(() => {
        resolve()
      })
    }
    script.onerror = () => {
      reject(new Error('카카오맵 API 로드에 실패했습니다.'))
    }
    document.head.appendChild(script)
  })
}

// 지도 초기화
const initMap = async () => {
  try {
    loading.value = true
    error.value = null

    await loadKakaoMapAPI()

    if (!mapContainer.value) {
      throw new Error('지도 컨테이너를 찾을 수 없습니다.')
    }

    const center = new window.kakao.maps.LatLng(props.center.lat, props.center.lng)

    const options = {
      center,
      level: props.level,
      draggable: props.draggable,
      scrollwheel: props.scrollwheel,
      disableDoubleClick: props.disableDoubleClick,
      disableDoubleClickZoom: props.disableDoubleClickZoom,
      projectionId: props.projectionId,
      tileAnimation: props.tileAnimation,
      keyboardShortcuts: props.keyboardShortcuts,
    }

    map.value = new window.kakao.maps.Map(mapContainer.value, options)

    // 이벤트 리스너 등록
    setupEventListeners()

    // 마커 추가
    addMarkers()

    emit('map-loaded', map.value)
    loading.value = false
  } catch (err) {
    error.value = err instanceof Error ? err.message : '지도 초기화에 실패했습니다.'
    loading.value = false
  }
}

// 이벤트 리스너 설정
const setupEventListeners = () => {
  if (!map.value) return

  // 지도 클릭 이벤트
  window.kakao.maps.event.addListener(map.value, 'click', (mouseEvent: any) => {
    const latlng = mouseEvent.latLng
    emit('map-click', latlng)
  })

  // 지도 드래그 종료 이벤트
  window.kakao.maps.event.addListener(map.value, 'dragend', () => {
    const center = map.value.getCenter()
    emit('drag-end', center)
  })

  // 지도 줌 변경 이벤트
  window.kakao.maps.event.addListener(map.value, 'zoom_changed', () => {
    const level = map.value.getLevel()
    emit('zoom-changed', level)
  })
}

// 마커 추가
const addMarkers = () => {
  if (!map.value || !props.markers.length) return

  // 기존 마커 제거
  clearMarkers()

  props.markers.forEach((markerData, index) => {
    const position = new window.kakao.maps.LatLng(markerData.position.lat, markerData.position.lng)

    const marker = new window.kakao.maps.Marker({
      position,
      title: markerData.title,
    })

    // 마커 클릭 이벤트
    if (markerData.clickable !== false) {
      window.kakao.maps.event.addListener(marker, 'click', () => {
        emit('marker-click', marker, index)
      })
    }

    // 인포윈도우 추가
    if (markerData.content) {
      const infowindow = new window.kakao.maps.InfoWindow({
        content: markerData.content,
      })

      window.kakao.maps.event.addListener(marker, 'click', () => {
        infowindow.open(map.value, marker)
      })
    }

    marker.setMap(map.value)
    markers.value.push(marker)
  })
}

// 마커 제거
const clearMarkers = () => {
  markers.value.forEach((marker) => {
    marker.setMap(null)
  })
  markers.value = []
}

// 지도 중심점 변경
const setCenter = (coordinates: Coordinates) => {
  if (!map.value) return
  const center = new window.kakao.maps.LatLng(coordinates.lat, coordinates.lng)
  map.value.setCenter(center)
}

// 지도 레벨 변경
const setLevel = (level: number) => {
  if (!map.value) return
  map.value.setLevel(level)
}

// 지도 크기 조정
const relayout = () => {
  if (!map.value) return
  map.value.relayout()
}

// 컴포넌트 마운트 시 지도 초기화
onMounted(async () => {
  await nextTick()
  await initMap()
})

// 컴포넌트 언마운트 시 정리
onUnmounted(() => {
  clearMarkers()
  if (map.value) {
    window.kakao.maps.event.removeListener(map.value)
  }
})

// props 변경 감지
watch(
  () => props.center,
  (newCenter) => {
    if (newCenter && map.value) {
      setCenter(newCenter)
    }
  },
)

watch(
  () => props.level,
  (newLevel) => {
    if (newLevel && map.value) {
      setLevel(newLevel)
    }
  },
)

watch(
  () => props.markers,
  () => {
    if (map.value) {
      addMarkers()
    }
  },
)

// 외부에서 사용할 수 있는 메서드들
defineExpose({
  map,
  setCenter,
  setLevel,
  relayout,
  addMarkers,
  clearMarkers,
})
</script>

<style scoped>
.kakao-map-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.kakao-map {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.map-loading,
.map-error {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.map-loading p,
.map-error p {
  margin: 10px 0 0 0;
  color: #666;
}

.map-error p {
  color: #d32f2f;
}
</style>

