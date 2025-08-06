<template>
  <q-page class="q-pa-md">
    <div class="container-responsive">
      <!-- Loading State -->
      <div v-if="loading.isLoading" class="flex justify-center items-center py-12">
        <q-spinner-dots size="50px" color="primary" />
      </div>

      <!-- Error State -->
      <div v-else-if="loading.error" class="text-center py-12">
        <q-icon name="error" size="4rem" color="negative" />
        <h3 class="text-xl font-semibold text-gray-600 mt-4">오류가 발생했습니다</h3>
        <p class="text-gray-500 mt-2">{{ loading.error }}</p>
        <q-btn color="primary" label="다시 시도" @click="fetchStore" class="mt-4" />
      </div>

      <!-- Store Detail -->
      <div v-else-if="store" class="space-y-6">
        <!-- Header -->
        <div class="flex justify-between items-start">
          <div>
            <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ store.name }}</h1>
            <div class="flex items-center space-x-4 text-gray-600">
              <div class="flex items-center">
                <q-icon name="location_on" size="1.2em" color="primary" class="mr-1" />
                <span>{{ store.location }}</span>
              </div>
              <div class="flex items-center">
                <q-icon name="star" size="1.2em" color="amber" class="mr-1" />
                <span class="font-medium">{{ store.rating }}</span>
              </div>
              <q-badge :color="getStatusColor(store.status)" :label="store.status" />
            </div>
          </div>
          <div class="flex space-x-2">
            <q-btn color="primary" icon="edit" label="수정" @click="editStore" />
            <q-btn color="negative" icon="delete" label="삭제" @click="confirmDelete" />
          </div>
        </div>

        <!-- Images Gallery -->
        <div
          v-if="store.images && store.images.length > 0"
          class="bg-white rounded-lg shadow-md p-6"
        >
          <h2 class="text-xl font-semibold text-gray-900 mb-4">이미지</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <q-img
              v-for="(image, index) in store.images"
              :key="index"
              :src="image"
              :ratio="16 / 9"
              class="rounded-lg cursor-pointer"
              @click="openImageModal(image)"
            />
          </div>
        </div>

        <!-- Store Information -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Main Info -->
          <div class="lg:col-span-2 space-y-6">
            <!-- Description -->
            <q-card class="bg-white">
              <q-card-section>
                <h2 class="text-xl font-semibold text-gray-900 mb-4">소개</h2>
                <p class="text-gray-700 leading-relaxed">{{ store.description }}</p>
              </q-card-section>
            </q-card>

            <!-- Period -->
            <q-card class="bg-white">
              <q-card-section>
                <h2 class="text-xl font-semibold text-gray-900 mb-4">운영 기간</h2>
                <div class="flex items-center space-x-4">
                  <div class="flex items-center">
                    <q-icon name="schedule" size="1.2em" color="primary" class="mr-2" />
                    <span class="text-gray-700">{{ store.period }}</span>
                  </div>
                </div>
                <div class="mt-2 text-sm text-gray-500">
                  {{ formatDate(store.startDate) }} ~ {{ formatDate(store.endDate) }}
                </div>
              </q-card-section>
            </q-card>

            <!-- Location Details -->
            <q-card class="bg-white">
              <q-card-section>
                <h2 class="text-xl font-semibold text-gray-900 mb-4">위치 정보</h2>
                <div class="space-y-2">
                  <div class="flex items-center">
                    <q-icon name="location_on" size="1.2em" color="primary" class="mr-2" />
                    <span class="text-gray-700">{{ store.address }}</span>
                  </div>
                  <div v-if="store.latitude && store.longitude" class="flex items-center">
                    <q-icon name="map" size="1.2em" color="primary" class="mr-2" />
                    <span class="text-gray-700">{{ store.latitude }}, {{ store.longitude }}</span>
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>

          <!-- Sidebar -->
          <div class="space-y-6">
            <!-- Category -->
            <q-card class="bg-white">
              <q-card-section>
                <h3 class="text-lg font-semibold text-gray-900 mb-2">카테고리</h3>
                <q-chip :label="store.categoryName" color="primary" outline />
              </q-card-section>
            </q-card>

            <!-- Quick Actions -->
            <q-card class="bg-white">
              <q-card-section>
                <h3 class="text-lg font-semibold text-gray-900 mb-4">빠른 액션</h3>
                <div class="space-y-2">
                  <q-btn
                    color="primary"
                    icon="share"
                    label="공유하기"
                    class="full-width"
                    @click="shareStore"
                  />
                  <q-btn
                    color="secondary"
                    icon="favorite"
                    label="찜하기"
                    class="full-width"
                    @click="toggleFavorite"
                  />
                  <q-btn
                    color="accent"
                    icon="directions"
                    label="길찾기"
                    class="full-width"
                    @click="openDirections"
                  />
                </div>
              </q-card-section>
            </q-card>

            <!-- Store Stats -->
            <q-card class="bg-white">
              <q-card-section>
                <h3 class="text-lg font-semibold text-gray-900 mb-4">스토어 정보</h3>
                <div class="space-y-3">
                  <div class="flex justify-between">
                    <span class="text-gray-600">평점</span>
                    <span class="font-medium">{{ store.rating }}/5.0</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600">상태</span>
                    <q-badge :color="getStatusColor(store.status)" :label="store.status" />
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600">등록일</span>
                    <span class="font-medium">{{ formatDate(store.createdAt) }}</span>
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>
        </div>
      </div>

      <!-- Not Found -->
      <div v-else class="text-center py-12">
        <q-icon name="store" size="4rem" color="grey-4" />
        <h3 class="text-xl font-semibold text-gray-600 mt-4">팝업스토어를 찾을 수 없습니다</h3>
        <p class="text-gray-500 mt-2">요청하신 팝업스토어가 존재하지 않거나 삭제되었습니다.</p>
        <q-btn
          color="primary"
          label="목록으로 돌아가기"
          @click="$router.push('/popupstores')"
          class="mt-4"
        />
      </div>
    </div>

    <!-- Image Modal -->
    <q-dialog v-model="showImageModal" maximized>
      <q-card class="bg-black">
        <q-card-section class="flex justify-between items-center">
          <div></div>
          <q-btn flat round icon="close" color="white" @click="showImageModal = false" />
        </q-card-section>
        <q-card-section class="flex justify-center items-center">
          <q-img
            v-if="selectedImage"
            :src="selectedImage"
            style="max-height: 80vh; max-width: 80vw"
            class="rounded-lg"
          />
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Delete Confirmation Dialog -->
    <q-dialog v-model="showDeleteDialog" persistent>
      <q-card>
        <q-card-section>
          <div class="text-h6">팝업스토어 삭제</div>
        </q-card-section>

        <q-card-section>
          <p>정말로 "{{ store?.name }}" 팝업스토어를 삭제하시겠습니까?</p>
          <p class="text-red-600 text-sm mt-2">이 작업은 되돌릴 수 없습니다.</p>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="취소" color="primary" v-close-popup />
          <q-btn flat label="삭제" color="negative" @click="deleteStore" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePopupStore } from '@/stores/popupStore'
import type { PopupStore } from '@/types'

const route = useRoute()
const router = useRouter()
const popupStore = usePopupStore()

const store = computed(() => popupStore.currentStore)
const loading = computed(() => popupStore.loading)

const showImageModal = ref(false)
const showDeleteDialog = ref(false)
const selectedImage = ref('')

const fetchStore = async () => {
  const storeId = parseInt(route.params.id as string)
  if (storeId) {
    try {
      await popupStore.fetchStoreById(storeId)
    } catch (error) {
      console.error('Failed to fetch store:', error)
    }
  }
}

const getStatusColor = (status: string): string => {
  switch (status) {
    case '진행중':
      return 'positive'
    case '예정':
      return 'warning'
    case '종료':
      return 'grey'
    default:
      return 'primary'
  }
}

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('ko-KR')
}

const editStore = () => {
  if (store.value) {
    router.push(`/popupstores/${store.value.id}/edit`)
  }
}

const confirmDelete = () => {
  showDeleteDialog.value = true
}

const deleteStore = async () => {
  if (store.value) {
    try {
      await popupStore.deleteStore(store.value.id)
      showDeleteDialog.value = false
      router.push('/popupstores')
    } catch (error) {
      console.error('Failed to delete store:', error)
    }
  }
}

const openImageModal = (image: string) => {
  selectedImage.value = image
  showImageModal.value = true
}

const shareStore = () => {
  if (navigator.share && store.value) {
    navigator.share({
      title: store.value.name,
      text: store.value.description,
      url: window.location.href,
    })
  } else {
    // Fallback: copy URL to clipboard
    navigator.clipboard.writeText(window.location.href)
    // Show notification
    // You can use Quasar's Notify plugin here
  }
}

const toggleFavorite = () => {
  // TODO: Implement favorite functionality
  console.log('Toggle favorite')
}

const openDirections = () => {
  if (store.value?.latitude && store.value?.longitude) {
    const url = `https://www.google.com/maps/dir/?api=1&destination=${store.value.latitude},${store.value.longitude}`
    window.open(url, '_blank')
  }
}

onMounted(() => {
  fetchStore()
})
</script>
