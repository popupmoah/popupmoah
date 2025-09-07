<template>
  <div class="nearby-search">
    <q-card class="search-card">
      <q-card-section class="q-pb-none">
        <div class="text-h6 q-mb-md">주변 검색</div>

        <!-- 검색 입력 -->
        <q-input
          v-model="searchQuery"
          placeholder="장소나 주소를 입력하세요"
          outlined
          dense
          clearable
          @keyup.enter="performSearch"
          @clear="clearResults"
        >
          <template #prepend>
            <q-icon name="search" />
          </template>
          <template #append>
            <q-btn
              flat
              dense
              icon="my_location"
              @click="getCurrentLocation"
              :loading="gettingLocation"
            >
              <q-tooltip>현재 위치</q-tooltip>
            </q-btn>
          </template>
        </q-input>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <!-- 필터 옵션 -->
        <div class="row q-gutter-sm q-mb-md">
          <div class="col-6">
            <q-select
              v-model="selectedCategory"
              :options="categoryOptions"
              placeholder="카테고리"
              outlined
              dense
              clearable
              emit-value
              map-options
            />
          </div>
          <div class="col-6">
            <q-select
              v-model="selectedRadius"
              :options="radiusOptions"
              placeholder="반경"
              outlined
              dense
              emit-value
              map-options
            />
          </div>
        </div>

        <!-- 검색 버튼 -->
        <q-btn
          color="primary"
          label="검색"
          icon="search"
          class="full-width"
          :loading="searching"
          @click="performSearch"
        />
      </q-card-section>

      <!-- 검색 결과 -->
      <q-card-section v-if="searchResults.length > 0" class="q-pt-none">
        <div class="text-subtitle2 q-mb-sm">검색 결과 ({{ searchResults.length }}개)</div>
        <q-list separator>
          <q-item
            v-for="(place, index) in searchResults"
            :key="place.id || index"
            clickable
            v-ripple
            @click="selectPlace(place)"
          >
            <q-item-section avatar>
              <q-icon :name="getCategoryIcon(place.category)" color="primary" />
            </q-item-section>
            <q-item-section>
              <q-item-label>{{ place.name }}</q-item-label>
              <q-item-label caption>{{ place.address }}</q-item-label>
              <q-item-label v-if="place.distance" caption>
                <q-icon name="place" size="xs" />
                {{ formatDistance(place.distance) }}
              </q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-btn flat dense icon="directions" @click.stop="showDirections(place)">
                <q-tooltip>길찾기</q-tooltip>
              </q-btn>
            </q-item-section>
          </q-item>
        </q-list>
      </q-card-section>

      <!-- 로딩 상태 -->
      <q-inner-loading :showing="searching">
        <q-spinner-dots size="50px" color="primary" />
      </q-inner-loading>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useMapStore } from '@/stores/mapStore'
import type { Coordinates, PlaceInfo } from '@/types'

interface Props {
  center?: Coordinates
}

interface Emits {
  (e: 'place-selected', place: PlaceInfo): void
  (e: 'location-found', coordinates: Coordinates): void
  (e: 'search-completed', results: PlaceInfo[]): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const mapStore = useMapStore()

// 검색 상태
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedRadius = ref(1000)
const searching = ref(false)
const gettingLocation = ref(false)
const searchResults = ref<PlaceInfo[]>([])

// 옵션들
const categoryOptions = [
  { label: '전체', value: '' },
  { label: '음식점', value: 'FD6' },
  { label: '카페', value: 'CE7' },
  { label: '편의점', value: 'CS2' },
  { label: '주유소', value: 'OL7' },
  { label: '지하철역', value: 'SW8' },
  { label: '은행', value: 'BK9' },
  { label: '병원', value: 'HP8' },
  { label: '약국', value: 'PM9' },
  { label: '마트', value: 'MT1' },
]

const radiusOptions = [
  { label: '100m', value: 100 },
  { label: '300m', value: 300 },
  { label: '500m', value: 500 },
  { label: '1km', value: 1000 },
  { label: '2km', value: 2000 },
  { label: '5km', value: 5000 },
]

// 메서드들
const performSearch = async () => {
  if (!searchQuery.value.trim()) {
    return
  }

  searching.value = true
  try {
    const results = await mapStore.searchPlaces({
      query: searchQuery.value,
      center: props.center,
      radius: selectedRadius.value,
      category: selectedCategory.value || undefined,
    })

    searchResults.value = results
    emit('search-completed', results)
  } catch (error) {
    console.error('장소 검색 실패:', error)
  } finally {
    searching.value = false
  }
}

const clearResults = () => {
  searchResults.value = []
}

const selectPlace = (place: PlaceInfo) => {
  emit('place-selected', place)
}

const getCurrentLocation = async () => {
  gettingLocation.value = true
  try {
    const coordinates = await mapStore.getCurrentLocation()
    emit('location-found', coordinates)
  } catch (error) {
    console.error('현재 위치 가져오기 실패:', error)
  } finally {
    gettingLocation.value = false
  }
}

const showDirections = (place: PlaceInfo) => {
  // 길찾기 기능 구현 (외부 지도 앱으로 연결)
  const url = `https://map.kakao.com/link/to/${place.name},${place.coordinates.lat},${place.coordinates.lng}`
  window.open(url, '_blank')
}

const getCategoryIcon = (category: string) => {
  const iconMap: Record<string, string> = {
    FD6: 'restaurant',
    CE7: 'local_cafe',
    CS2: 'store',
    OL7: 'local_gas_station',
    SW8: 'train',
    BK9: 'account_balance',
    HP8: 'local_hospital',
    PM9: 'local_pharmacy',
    MT1: 'shopping_cart',
  }
  return iconMap[category] || 'place'
}

const formatDistance = (distance: number) => {
  if (distance < 1000) {
    return `${Math.round(distance)}m`
  } else {
    return `${(distance / 1000).toFixed(1)}km`
  }
}
</script>

<style scoped>
.nearby-search {
  width: 100%;
  max-width: 400px;
}

.search-card {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
}

.q-item {
  border-radius: 8px;
  margin-bottom: 4px;
}

.q-item:hover {
  background-color: rgba(25, 118, 210, 0.04);
}
</style>
