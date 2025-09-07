<template>
  <div v-if="showSettings" class="cluster-settings">
    <q-card class="settings-card">
      <q-card-section class="q-pb-none">
        <div class="text-subtitle2">클러스터 설정</div>
      </q-card-section>
      <q-card-section class="q-pt-none">
        <div class="row q-gutter-sm">
          <div class="col-6">
            <q-input
              v-model.number="clusterOptions.gridSize"
              label="그리드 크기"
              type="number"
              outlined
              dense
              min="10"
              max="200"
              @update:model-value="updateClustering"
            />
          </div>
          <div class="col-6">
            <q-input
              v-model.number="clusterOptions.minClusterSize"
              label="최소 클러스터 크기"
              type="number"
              outlined
              dense
              min="2"
              max="20"
              @update:model-value="updateClustering"
            />
          </div>
        </div>
        <div class="row q-gutter-sm q-mt-sm">
          <div class="col-6">
            <q-toggle
              v-model="clusterOptions.disableClickZoom"
              label="클릭 줌 비활성화"
              @update:model-value="updateClustering"
            />
          </div>
          <div class="col-6">
            <q-toggle
              v-model="clusterOptions.averageCenter"
              label="평균 중심점"
              @update:model-value="updateClustering"
            />
          </div>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import type { MapProvider, MapMarker } from '@/types'

interface Props {
  markers: MapMarker[]
  map: any
  provider: MapProvider
  showSettings?: boolean
}

interface Emits {
  (e: 'cluster-updated', clusters: any[]): void
  (e: 'cluster-click', cluster: any): void
}

const props = withDefaults(defineProps<Props>(), {
  showSettings: false,
})

const emit = defineEmits<Emits>()

// 클러스터 옵션
const clusterOptions = ref({
  gridSize: 60,
  minClusterSize: 2,
  disableClickZoom: false,
  averageCenter: true,
})

// 클러스터 인스턴스들
let kakaoClusterer: any = null
let naverClusterer: any = null

// 클러스터링 초기화
const initializeClustering = () => {
  if (!props.map || !props.markers.length) return

  if (props.provider === 'kakao') {
    initializeKakaoClustering()
  } else if (props.provider === 'naver') {
    initializeNaverClustering()
  }
}

// 카카오맵 클러스터링 초기화
const initializeKakaoClustering = () => {
  if (typeof window !== 'undefined' && (window as any).kakao) {
    const { kakao } = window as any

    // 기존 클러스터러 제거
    if (kakaoClusterer) {
      kakaoClusterer.clear()
    }

    // 마커들을 카카오맵 마커로 변환
    const kakaoMarkers = props.markers.map((marker) => {
      const position = new kakao.maps.LatLng(marker.position.lat, marker.position.lng)
      const kakaoMarker = new kakao.maps.Marker({
        position: position,
        title: marker.title,
      })

      // 마커 클릭 이벤트
      if (marker.clickable !== false) {
        kakao.maps.event.addListener(kakaoMarker, 'click', () => {
          // 마커 클릭 이벤트 처리
        })
      }

      return kakaoMarker
    })

    // 클러스터러 생성
    kakaoClusterer = new kakao.maps.MarkerClusterer({
      map: props.map,
      markers: kakaoMarkers,
      gridSize: clusterOptions.value.gridSize,
      minClusterSize: clusterOptions.value.minClusterSize,
      disableClickZoom: clusterOptions.value.disableClickZoom,
      averageCenter: clusterOptions.value.averageCenter,
    })

    // 클러스터 클릭 이벤트
    kakao.maps.event.addListener(kakaoClusterer, 'clusterclick', (cluster: any) => {
      const markers = cluster.getMarkers()
      const center = cluster.getCenter()

      emit('cluster-click', {
        markers: markers,
        center: {
          lat: center.getLat(),
          lng: center.getLng(),
        },
        count: markers.length,
      })
    })
  }
}

// 네이버맵 클러스터링 초기화
const initializeNaverClustering = () => {
  if (typeof window !== 'undefined' && (window as any).naver) {
    const { naver } = window as any

    // 기존 클러스터러 제거
    if (naverClusterer) {
      naverClusterer.clear()
    }

    // 마커들을 네이버맵 마커로 변환
    const naverMarkers = props.markers.map((marker) => {
      const position = new naver.maps.LatLng(marker.position.lat, marker.position.lng)
      const naverMarker = new naver.maps.Marker({
        position: position,
        title: marker.title,
        map: props.map,
      })

      // 마커 클릭 이벤트
      if (marker.clickable !== false) {
        naver.maps.Event.addListener(naverMarker, 'click', () => {
          // 마커 클릭 이벤트 처리
        })
      }

      return naverMarker
    })

    // 클러스터러 생성 (네이버맵은 별도 라이브러리 필요)
    // 실제 구현에서는 naver-maps-clustering 라이브러리를 사용해야 함
    console.log('네이버맵 클러스터링은 별도 라이브러리가 필요합니다')
  }
}

// 클러스터링 업데이트
const updateClustering = () => {
  initializeClustering()
}

// 마커 변경 감지
watch(
  () => props.markers,
  () => {
    initializeClustering()
  },
  { deep: true },
)

// 지도 변경 감지
watch(
  () => props.map,
  () => {
    initializeClustering()
  },
)

// 제공자 변경 감지
watch(
  () => props.provider,
  () => {
    initializeClustering()
  },
)

// 컴포넌트 마운트 시 초기화
onMounted(() => {
  initializeClustering()
})

// 컴포넌트 언마운트 시 정리
onUnmounted(() => {
  if (kakaoClusterer) {
    kakaoClusterer.clear()
  }
  if (naverClusterer) {
    naverClusterer.clear()
  }
})
</script>

<style scoped>
.cluster-settings {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 1000;
  max-width: 300px;
}

.settings-card {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}
</style>
