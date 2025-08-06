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

      <!-- Filters -->
      <q-card class="mb-6">
        <q-card-section>
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <q-input
              v-model="searchQuery"
              label="검색"
              outlined
              clearable
              class="col-span-1 md:col-span-2"
            >
              <template v-slot:prepend>
                <q-icon name="search" />
              </template>
            </q-input>

            <q-select
              v-model="selectedCategory"
              :options="categoryOptions"
              label="카테고리"
              outlined
              clearable
              emit-value
              map-options
            />

            <q-select
              v-model="selectedStatus"
              :options="statusOptions"
              label="상태"
              outlined
              clearable
              emit-value
              map-options
            />
          </div>
        </q-card-section>
      </q-card>

      <!-- Stores Grid -->
      <div class="grid-responsive">
        <PopupStoreCard
          v-for="store in filteredStores"
          :key="store.id"
          :store="store"
          @view="viewStore"
        />
      </div>

      <!-- Pagination -->
      <div v-if="pagination.totalPages > 1" class="flex justify-center mt-8">
        <q-pagination
          v-model="pagination.currentPage"
          :max="pagination.totalPages"
          :max-pages="5"
          boundary-numbers
          @update:model-value="handlePageChange"
        />
      </div>

      <!-- Page Size Selector -->
      <div class="flex justify-between items-center mt-4">
        <div class="text-sm text-gray-600">총 {{ pagination.totalElements }}개의 팝업스토어</div>
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-600">페이지당:</span>
          <q-select
            v-model="pagination.size"
            :options="[6, 12, 24, 48]"
            dense
            outlined
            @update:model-value="handlePageSizeChange"
          />
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="filteredStores.length === 0 && !loading.isLoading" class="text-center py-12">
        <q-icon name="store" size="4rem" color="grey-4" />
        <h3 class="text-xl font-semibold text-gray-600 mt-4">팝업스토어가 없습니다</h3>
        <p class="text-gray-500 mt-2">조건을 변경해보세요</p>
      </div>

      <!-- Loading State -->
      <q-inner-loading :showing="loading.isLoading">
        <q-spinner-dots size="50px" color="primary" />
      </q-inner-loading>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePopupStore } from '@/stores/popupStore'
import type { PopupStore } from '@/types'
import PopupStoreCard from '@/components/popupstore/PopupStoreCard.vue'

const router = useRouter()
const popupStore = usePopupStore()

const { stores, loading, fetchStores, pagination, changePage, changePageSize } = popupStore

// Filters
const selectedCategory = ref('')
const selectedStatus = ref('')
const searchQuery = ref('')

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

const handlePageChange = (page: number) => {
  changePage(page)
}

const handlePageSizeChange = (size: number) => {
  changePageSize(size)
}

onMounted(() => {
  fetchStores()
})
</script>
