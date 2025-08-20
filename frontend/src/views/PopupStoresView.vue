<template>
  <q-page class="q-pa-md">
    <div class="container-responsive">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">팝업스토어 목록</h1>
          <p class="text-gray-600 mt-2">서울의 다양한 팝업스토어를 둘러보세요</p>
        </div>
        <div class="flex space-x-2">
          <q-btn
            color="secondary"
            icon="category"
            label="카테고리 관리"
            @click="$router.push('/categories')"
          />
          <q-btn
            color="primary"
            icon="add"
            label="새 팝업스토어 등록"
            @click="$router.push('/popupstores/create')"
          />
        </div>
      </div>

      <!-- View Toggle -->
      <div class="mb-4">
        <q-btn-toggle
          v-model="viewMode"
          :options="viewModeOptions"
          color="primary"
          toggle-color="primary"
          @update:model-value="onViewModeChange"
        />
      </div>

      <!-- Search and Filters -->
      <div class="mb-6">
        <div class="flex flex-col lg:flex-row gap-4">
          <!-- Search Bar -->
          <div class="flex-1">
            <q-input
              v-model="searchQuery"
              placeholder="팝업스토어 검색..."
              outlined
              clearable
              @update:model-value="handleSearch"
            >
              <template v-slot:prepend>
                <q-icon name="search" />
              </template>
            </q-input>
          </div>

          <!-- Category Filter -->
          <div class="w-full lg:w-64">
            <q-select
              v-model="selectedCategory"
              :options="categoryOptions"
              label="카테고리"
              outlined
              clearable
              emit-value
              map-options
              @update:model-value="handleCategoryChange"
            />
          </div>

          <!-- Status Filter -->
          <div class="w-full lg:w-48">
            <q-select
              v-model="selectedStatus"
              :options="statusOptions"
              label="상태"
              outlined
              clearable
              emit-value
              map-options
              @update:model-value="handleStatusChange"
            />
          </div>
        </div>

        <!-- Advanced Filters -->
        <AdvancedFilters v-model="advancedFilters" @apply="handleAdvancedFilters" />
      </div>

      <!-- Map View -->
      <div v-if="viewMode === 'map'" class="map-container">
        <PopupStoreMap
          ref="popupStoreMapRef"
          :width="'100%'"
          :height="'600px'"
          :show-active-only="showActiveOnly"
          :show-nearby-search="true"
          :auto-load="true"
          @popup-store-selected="onPopupStoreSelected"
          @popup-store-detail="onPopupStoreDetail"
          @place-selected="onPlaceSelected"
          @location-found="onLocationFound"
        />
      </div>

      <!-- List View -->
      <div v-else>
        <!-- Stores Grid -->
        <div class="grid-responsive">
          <PopupStoreCard
            v-for="store in filteredStores"
            :key="store.id"
            :store="store"
            @view="viewStore"
          />
        </div>

        <!-- Empty State -->
        <div v-if="filteredStores.length === 0 && !loading.isLoading" class="text-center py-12">
          <q-icon name="store" size="4rem" color="grey-4" />
          <h3 class="text-xl font-semibold text-gray-600 mt-4">팝업스토어가 없습니다</h3>
          <p class="text-gray-500 mt-2">조건을 변경해보세요</p>
        </div>
      </div>

      <!-- Loading State -->
      <q-inner-loading :showing="loading.isLoading">
        <q-spinner-dots size="50px" color="primary" />
      </q-inner-loading>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { usePopupStore } from '@/stores/popupStore'
import type { PopupStore, PopupStoreLocation, Coordinates, PlaceInfo } from '@/types'
import PopupStoreCard from '@/components/popupstore/PopupStoreCard.vue'
import AdvancedFilters from '@/components/popupstore/AdvancedFilters.vue'
import PopupStoreMap from '@/components/maps/PopupStoreMap.vue'

const router = useRouter()
const popupStore = usePopupStore()

const { stores, loading, fetchStores } = popupStore

// View Mode
const viewMode = ref<'list' | 'map'>('list')
const viewModeOptions = [
  { label: '목록 보기', value: 'list', icon: 'view_list' },
  { label: '지도 보기', value: 'map', icon: 'map' },
]

// Map Reference
const popupStoreMapRef = ref()

// Filters
const selectedCategory = ref('')
const selectedStatus = ref('')
const searchQuery = ref('')
const showActiveOnly = ref(false)

const categoryOptions = [
  { label: '패션', value: '패션' },
  { label: '식품', value: '식품' },
  { label: '뷰티', value: '뷰티' },
  { label: '라이프스타일', value: '라이프스타일' },
]

const statusOptions = [
  { label: '진행중', value: '진행중' },
  { label: '예정', value: '예정' },
  { label: '종료', value: '종료' },
]

const filteredStores = computed(() => {
  let filtered = stores

  if (selectedCategory.value) {
    filtered = filtered.filter((store: PopupStore) => store.categoryName === selectedCategory.value)
  }

  if (selectedStatus.value) {
    filtered = filtered.filter((store: PopupStore) => store.status === selectedStatus.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (store: PopupStore) =>
        store.name.toLowerCase().includes(query) ||
        store.description.toLowerCase().includes(query) ||
        store.location.toLowerCase().includes(query),
    )
  }

  return filtered
})

const viewStore = (id: number) => {
  router.push(`/popupstores/${id}`)
}

onMounted(() => {
  fetchStores()
})

// Advanced filters
const advancedFilters = ref({
  startDate: '',
  endDate: '',
  minRating: 0,
  regions: [],
  sortBy: 'name',
})

const handleSearch = () => {
  // 검색어 변경 시 필터 적용
  fetchStores()
}

const handleCategoryChange = () => {
  // 카테고리 변경 시 필터 적용
  fetchStores()
}

const handleStatusChange = () => {
  // 상태 변경 시 필터 적용
  fetchStores()
}

const handleAdvancedFilters = (filters: any) => {
  // 고급 필터 적용 로직
  console.log('Advanced filters applied:', filters)
  // TODO: API 호출 시 필터 파라미터 추가
  fetchStores()
}

// View Mode Methods
const onViewModeChange = (mode: 'list' | 'map') => {
  viewMode.value = mode
  if (mode === 'map' && popupStoreMapRef.value) {
    // 지도 뷰로 전환 시 지도 새로고침
    popupStoreMapRef.value.loadPopupStoreLocations()
  }
}

// Map Event Handlers
const onPopupStoreSelected = (popupStore: PopupStoreLocation) => {
  console.log('팝업스토어 선택됨:', popupStore)
}

const onPopupStoreDetail = (popupStore: PopupStoreLocation) => {
  router.push(`/popupstores/${popupStore.id}`)
}

// Nearby Search Event Handlers
const onPlaceSelected = (place: PlaceInfo) => {
  console.log('장소 선택됨:', place)
  // 선택된 장소 주변의 팝업스토어를 필터링할 수 있음
}

const onLocationFound = (coordinates: Coordinates) => {
  console.log('현재 위치 발견:', coordinates)
  // 현재 위치 기반으로 팝업스토어를 필터링할 수 있음
}
</script>

<style scoped>
.map-container {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.grid-responsive {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.container-responsive {
  max-width: 1200px;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .grid-responsive {
    grid-template-columns: 1fr;
  }

  .container-responsive {
    padding: 0 1rem;
  }
}
</style>
