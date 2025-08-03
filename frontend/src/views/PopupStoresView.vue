<template>
  <q-page class="q-pa-md">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-8">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">팝업스토어</h1>
          <p class="text-gray-600 mt-2">서울의 다양한 팝업스토어를 둘러보세요</p>
        </div>
        <q-btn 
          color="primary" 
          icon="add" 
          label="새 팝업스토어 등록"
          @click="showAddDialog = true"
        />
      </div>

      <!-- Filters -->
      <div class="bg-white rounded-lg shadow-sm p-4 mb-6">
        <div class="flex flex-wrap gap-4">
          <q-select
            v-model="selectedCategory"
            :options="categoryOptions"
            label="카테고리"
            outlined
            dense
            style="min-width: 150px"
            clearable
          />
          <q-select
            v-model="selectedStatus"
            :options="statusOptions"
            label="상태"
            outlined
            dense
            style="min-width: 150px"
            clearable
          />
          <q-input
            v-model="searchQuery"
            label="검색"
            outlined
            dense
            style="min-width: 200px"
            clearable
          >
            <template v-slot:append>
              <q-icon name="search" />
            </template>
          </q-input>
        </div>
      </div>

      <!-- Store Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <q-card 
          v-for="store in filteredStores" 
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

          <q-card-actions align="right">
            <q-btn flat color="primary" label="상세보기" />
            <q-btn flat color="secondary" label="리뷰" />
          </q-card-actions>
        </q-card>
      </div>

      <!-- Empty State -->
      <div v-if="filteredStores.length === 0 && !loading" class="text-center py-12">
        <q-icon name="store" size="4rem" color="grey-4" />
        <h3 class="text-xl font-semibold text-gray-900 mt-4">팝업스토어가 없습니다</h3>
        <p class="text-gray-600 mt-2">조건을 변경해보세요</p>
      </div>

      <!-- Loading State -->
      <q-inner-loading :showing="loading">
        <q-spinner-dots size="50px" color="primary" />
      </q-inner-loading>
    </div>

    <!-- Add Store Dialog -->
    <q-dialog v-model="showAddDialog" persistent>
      <q-card style="min-width: 400px">
        <q-card-section>
          <div class="text-h6">새 팝업스토어 등록</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="addNewStore" class="q-gutter-md">
            <q-input
              v-model="newStore.name"
              label="스토어명"
              outlined
              :rules="[val => !!val || '스토어명을 입력하세요']"
            />
            <q-input
              v-model="newStore.description"
              label="설명"
              type="textarea"
              outlined
            />
            <q-input
              v-model="newStore.location"
              label="위치"
              outlined
            />
            <q-select
              v-model="newStore.category"
              :options="['패션', '식품', '뷰티', '라이프스타일']"
              label="카테고리"
              outlined
            />
          </q-form>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="취소" color="primary" v-close-popup />
          <q-btn flat label="등록" color="primary" @click="addNewStore" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePopupStore, type PopupStore } from '@/stores/popupStore'

const router = useRouter()
const popupStore = usePopupStore()

const { stores, loading, fetchStores, addStore } = popupStore

// Filters
const selectedCategory = ref('')
const selectedStatus = ref('')
const searchQuery = ref('')
const showAddDialog = ref(false)

// New store form
const newStore = ref<Partial<PopupStore>>({
  name: '',
  description: '',
  location: '',
  category: '패션',
  status: '예정',
  rating: 0,
  period: ''
})

const categoryOptions = ['패션', '식품', '뷰티', '라이프스타일']
const statusOptions = ['진행중', '예정', '종료']

const filteredStores = computed(() => {
  let filtered = stores.value

  if (selectedCategory.value) {
    filtered = filtered.filter(store => store.category === selectedCategory.value)
  }

  if (selectedStatus.value) {
    filtered = filtered.filter(store => store.status === selectedStatus.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(store => 
      store.name.toLowerCase().includes(query) ||
      store.description.toLowerCase().includes(query) ||
      store.location.toLowerCase().includes(query)
    )
  }

  return filtered
})

const getStatusColor = (status: string) => {
  switch (status) {
    case '진행중': return 'positive'
    case '예정': return 'info'
    case '종료': return 'grey'
    default: return 'primary'
  }
}

const viewStore = (id: number) => {
  router.push(`/popupstores/${id}`)
}

const addNewStore = async () => {
  if (newStore.value.name) {
    await addStore(newStore.value as PopupStore)
    showAddDialog.value = false
    newStore.value = {
      name: '',
      description: '',
      location: '',
      category: '패션',
      status: '예정',
      rating: 0,
      period: ''
    }
  }
}

onMounted(() => {
  fetchStores()
})
</script> 