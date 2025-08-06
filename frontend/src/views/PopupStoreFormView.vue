<template>
  <q-page class="q-pa-md">
    <div class="container-responsive">
      <!-- Header -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900">
          {{ isEdit ? '팝업스토어 수정' : '새 팝업스토어 등록' }}
        </h1>
        <p class="text-gray-600 mt-2">
          {{ isEdit ? '팝업스토어 정보를 수정하세요.' : '새로운 팝업스토어를 등록하세요.' }}
        </p>
      </div>

      <!-- Loading State -->
      <div v-if="loading.isLoading" class="flex justify-center items-center py-12">
        <q-spinner-dots size="50px" color="primary" />
      </div>

      <!-- Form -->
      <div v-else class="max-w-4xl mx-auto">
        <q-form @submit="submitForm" class="space-y-6">
          <!-- Basic Information -->
          <q-card class="bg-white">
            <q-card-section>
              <h2 class="text-xl font-semibold text-gray-900 mb-4">기본 정보</h2>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <q-input
                  v-model="form.name"
                  label="스토어명 *"
                  outlined
                  :rules="[(val) => !!val || '스토어명을 입력해주세요']"
                />

                <q-select
                  v-model="form.categoryId"
                  :options="categoryOptions"
                  label="카테고리 *"
                  outlined
                  emit-value
                  map-options
                  :rules="[(val) => !!val || '카테고리를 선택해주세요']"
                />

                <q-input
                  v-model="form.location"
                  label="위치 *"
                  outlined
                  :rules="[(val) => !!val || '위치를 입력해주세요']"
                />

                <q-input v-model="form.address" label="상세 주소" outlined />
              </div>

              <q-input
                v-model="form.description"
                label="설명 *"
                type="textarea"
                outlined
                rows="4"
                :rules="[(val) => !!val || '설명을 입력해주세요']"
                class="mt-4"
              />
            </q-card-section>
          </q-card>

          <!-- Period Information -->
          <q-card class="bg-white">
            <q-card-section>
              <h2 class="text-xl font-semibold text-gray-900 mb-4">운영 기간</h2>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <q-input
                  v-model="form.startDate"
                  label="시작일 *"
                  outlined
                  type="date"
                  :rules="[(val) => !!val || '시작일을 선택해주세요']"
                />

                <q-input
                  v-model="form.endDate"
                  label="종료일 *"
                  outlined
                  type="date"
                  :rules="[(val) => !!val || '종료일을 선택해주세요']"
                />
              </div>
            </q-card-section>
          </q-card>

          <!-- Location Coordinates -->
          <q-card class="bg-white">
            <q-card-section>
              <h2 class="text-xl font-semibold text-gray-900 mb-4">위치 좌표</h2>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <q-input
                  v-model.number="form.latitude"
                  label="위도"
                  outlined
                  type="number"
                  step="0.000001"
                  placeholder="37.5665"
                />

                <q-input
                  v-model.number="form.longitude"
                  label="경도"
                  outlined
                  type="number"
                  step="0.000001"
                  placeholder="126.9780"
                />
              </div>
              <p class="text-sm text-gray-500 mt-2">
                위도와 경도는 선택사항입니다. Google Maps에서 좌표를 확인할 수 있습니다.
              </p>
            </q-card-section>
          </q-card>

          <!-- Image Upload -->
          <q-card class="bg-white">
            <q-card-section>
              <h2 class="text-xl font-semibold text-gray-900 mb-4">이미지</h2>

              <!-- Image Upload Area -->
              <div class="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                <q-icon name="cloud_upload" size="3rem" color="grey-4" class="mb-4" />
                <h3 class="text-lg font-medium text-gray-900 mb-2">이미지 업로드</h3>
                <p class="text-gray-500 mb-4">
                  팝업스토어 이미지를 업로드하세요. (최대 5개, 각 5MB 이하)
                </p>
                <q-btn color="primary" icon="upload" label="이미지 선택" @click="selectImages" />
                <input
                  ref="fileInput"
                  type="file"
                  multiple
                  accept="image/*"
                  class="hidden"
                  @change="handleImageUpload"
                />
              </div>

              <!-- Uploaded Images -->
              <div v-if="uploadedImages.length > 0" class="mt-6">
                <h4 class="text-lg font-medium text-gray-900 mb-3">업로드된 이미지</h4>
                <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                  <div v-for="(image, index) in uploadedImages" :key="index" class="relative group">
                    <q-img :src="image.url" :ratio="16 / 9" class="rounded-lg" />
                    <div
                      class="absolute inset-0 bg-black bg-opacity-50 opacity-0 group-hover:opacity-100 transition-opacity duration-200 rounded-lg flex items-center justify-center"
                    >
                      <q-btn
                        round
                        color="negative"
                        icon="delete"
                        size="sm"
                        @click="removeImage(index)"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </q-card-section>
          </q-card>

          <!-- Form Actions -->
          <div class="flex justify-end space-x-4">
            <q-btn color="secondary" label="취소" @click="cancelForm" />
            <q-btn
              color="primary"
              :label="isEdit ? '수정' : '등록'"
              type="submit"
              :loading="submitting"
            />
          </div>
        </q-form>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePopupStore } from '@/stores/popupStore'
import type { CreatePopupStoreRequest, UpdatePopupStoreRequest, PopupStore } from '@/types'

const route = useRoute()
const router = useRouter()
const popupStore = usePopupStore()

const isEdit = computed(() => route.name === 'popupstore-edit')
const storeId = computed(() => (route.params.id ? parseInt(route.params.id as string) : null))

const loading = computed(() => popupStore.loading)
const submitting = ref(false)

const fileInput = ref<HTMLInputElement>()

// Form data
const form = ref<Partial<CreatePopupStoreRequest>>({
  name: '',
  description: '',
  location: '',
  address: '',
  startDate: '',
  endDate: '',
  categoryId: 1,
  latitude: undefined,
  longitude: undefined,
})

// Image upload
const uploadedImages = ref<Array<{ file: File; url: string }>>([])

const categoryOptions = [
  { label: '패션', value: 1 },
  { label: '식품', value: 2 },
  { label: '뷰티', value: 3 },
  { label: '라이프스타일', value: 4 },
]

const fetchStore = async () => {
  if (isEdit.value && storeId.value) {
    try {
      await popupStore.fetchStoreById(storeId.value)
      const store = popupStore.currentStore
      if (store) {
        form.value = {
          name: store.name,
          description: store.description,
          location: store.location,
          address: store.address,
          startDate: store.startDate,
          endDate: store.endDate,
          categoryId: store.categoryId,
          latitude: store.latitude,
          longitude: store.longitude,
        }
      }
    } catch (error) {
      console.error('Failed to fetch store:', error)
    }
  }
}

const selectImages = () => {
  fileInput.value?.click()
}

const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files

  if (files) {
    Array.from(files).forEach((file) => {
      if (file.size > 5 * 1024 * 1024) {
        // Show error for files larger than 5MB
        console.error('File too large:', file.name)
        return
      }

      if (uploadedImages.value.length >= 5) {
        // Show error for too many files
        console.error('Too many images')
        return
      }

      const url = URL.createObjectURL(file)
      uploadedImages.value.push({ file, url })
    })
  }

  // Reset input
  target.value = ''
}

const removeImage = (index: number) => {
  const image = uploadedImages.value[index]
  URL.revokeObjectURL(image.url)
  uploadedImages.value.splice(index, 1)
}

const submitForm = async () => {
  submitting.value = true

  try {
    if (isEdit.value && storeId.value) {
      // Update existing store
      const updateData: UpdatePopupStoreRequest = {
        id: storeId.value,
        ...form.value,
      }
      await popupStore.updateStore(storeId.value, updateData)
    } else {
      // Create new store
      const createData: CreatePopupStoreRequest = {
        name: form.value.name!,
        description: form.value.description!,
        location: form.value.location!,
        address: form.value.address || '',
        startDate: form.value.startDate!,
        endDate: form.value.endDate!,
        categoryId: form.value.categoryId!,
        latitude: form.value.latitude,
        longitude: form.value.longitude,
      }
      await popupStore.createStore(createData)
    }

    // Navigate back to list
    router.push('/popupstores')
  } catch (error) {
    console.error('Failed to submit form:', error)
  } finally {
    submitting.value = false
  }
}

const cancelForm = () => {
  router.push('/popupstores')
}

onMounted(() => {
  fetchStore()
})
</script>
