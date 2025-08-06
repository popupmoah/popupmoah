<template>
  <q-page class="q-pa-md">
    <div class="container-responsive">
      <!-- Hero Section -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">팝업스토어를 발견하세요</h1>
        <p class="text-xl text-gray-600 mb-8">특별한 경험을 제공하는 팝업스토어들을 찾아보세요</p>
        <q-btn
          color="primary"
          size="lg"
          label="팝업스토어 둘러보기"
          @click="$router.push('/popupstores')"
        />
      </div>

      <!-- Top Rated Stores -->
      <div class="mb-12">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">인기 팝업스토어</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <q-card
            v-for="store in topRatedStores"
            :key="store.id"
            class="cursor-pointer hover:shadow-lg transition-shadow duration-300"
            @click="viewStore(store.id)"
          >
            <q-img
              :src="store.images?.[0] || 'https://via.placeholder.com/300x200'"
              height="200px"
            />

            <q-card-section>
              <div class="flex items-center justify-between mb-2">
                <q-badge :color="getStatusColor(store.status)" :label="store.status" />
                <div class="flex items-center">
                  <q-icon name="star" color="amber" size="sm" />
                  <span class="ml-1 text-sm font-medium">{{ store.rating }}</span>
                </div>
              </div>

              <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ store.name }}</h3>
              <p class="text-gray-600 text-sm mb-2">{{ store.description }}</p>
              <p class="text-gray-500 text-sm">
                <q-icon name="location_on" size="sm" />
                {{ store.location }}
              </p>
              <p class="text-gray-500 text-sm mt-1">
                <q-icon name="schedule" size="sm" />
                {{ store.period }}
              </p>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Categories -->
      <div class="mb-12">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">카테고리별 둘러보기</h2>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <q-card
            v-for="category in categories"
            :key="category.name"
            class="text-center cursor-pointer hover:shadow-md transition-shadow duration-300"
            @click="filterByCategory(category.name)"
          >
            <q-card-section>
              <q-icon :name="category.icon" size="3rem" :color="category.color" />
              <h3 class="text-lg font-semibold mt-2">{{ category.name }}</h3>
              <p class="text-gray-600 text-sm">{{ category.count }}개</p>
            </q-card-section>
          </q-card>
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
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { usePopupStore } from '@/stores/popupStore'
import type { PopupStore } from '@/types'

const router = useRouter()
const popupStore = usePopupStore()

const { stores, loading, topRatedStores, fetchStores } = popupStore

const categories = computed(() => [
  {
    name: '패션',
    icon: 'style',
    color: 'purple',
    count: stores.filter((s: PopupStore) => s.categoryName === '패션').length,
  },
  {
    name: '식품',
    icon: 'restaurant',
    color: 'orange',
    count: stores.filter((s: PopupStore) => s.categoryName === '식품').length,
  },
  {
    name: '뷰티',
    icon: 'face',
    color: 'pink',
    count: stores.filter((s: PopupStore) => s.categoryName === '뷰티').length,
  },
  {
    name: '라이프스타일',
    icon: 'home',
    color: 'green',
    count: stores.filter((s: PopupStore) => s.categoryName === '라이프스타일').length,
  },
])

const getStatusColor = (status: string) => {
  switch (status) {
    case '진행중':
      return 'positive'
    case '예정':
      return 'info'
    case '종료':
      return 'grey'
    default:
      return 'primary'
  }
}

const viewStore = (id: number) => {
  router.push(`/popupstores/${id}`)
}

const filterByCategory = (category: string) => {
  router.push(`/popupstores?category=${category}`)
}

onMounted(() => {
  fetchStores()
})
</script>
