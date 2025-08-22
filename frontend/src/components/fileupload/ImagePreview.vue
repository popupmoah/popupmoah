<template>
  <div class="image-preview-container">
    <!-- 이미지 미리보기 그리드 -->
    <div v-if="previews.length > 0" class="preview-grid">
      <div
        v-for="(preview, index) in previews"
        :key="index"
        class="preview-item"
        :class="{ selected: selectedIndex === index }"
        @click="selectImage(index)"
      >
        <!-- 이미지 미리보기 -->
        <div class="image-container">
          <img
            :src="preview.url"
            :alt="preview.file.name"
            class="preview-image"
            @load="onImageLoad(index)"
            @error="onImageError(index)"
          />

          <!-- 로딩 오버레이 -->
          <div v-if="preview.loading" class="loading-overlay">
            <svg class="w-6 h-6 text-white animate-spin" fill="none" viewBox="0 0 24 24">
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              ></circle>
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              ></path>
            </svg>
          </div>

          <!-- 에러 오버레이 -->
          <div v-if="preview.error" class="error-overlay">
            <svg class="w-6 h-6 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              ></path>
            </svg>
          </div>

          <!-- 삭제 버튼 -->
          <button @click.stop="removeImage(index)" class="remove-button" title="이미지 제거">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              ></path>
            </svg>
          </button>
        </div>

        <!-- 파일 정보 -->
        <div class="file-info">
          <p class="file-name" :title="preview.file.name">
            {{ truncateFileName(preview.file.name) }}
          </p>
          <p class="file-size">
            {{ formatFileSize(preview.file.size) }}
          </p>
          <p class="file-dimensions" v-if="preview.dimensions">
            {{ preview.dimensions.width }} × {{ preview.dimensions.height }}
          </p>
        </div>
      </div>
    </div>

    <!-- 선택된 이미지 상세 보기 -->
    <div v-if="selectedPreview" class="selected-image-detail">
      <div class="detail-header">
        <h3 class="detail-title">선택된 이미지</h3>
        <button @click="closeDetail" class="close-button" title="닫기">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
            ></path>
          </svg>
        </button>
      </div>

      <div class="detail-content">
        <img :src="selectedPreview.url" :alt="selectedPreview.file.name" class="detail-image" />

        <div class="detail-info">
          <div class="info-item">
            <span class="info-label">파일명:</span>
            <span class="info-value">{{ selectedPreview.file.name }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">크기:</span>
            <span class="info-value">{{ formatFileSize(selectedPreview.file.size) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">타입:</span>
            <span class="info-value">{{ selectedPreview.file.type }}</span>
          </div>
          <div class="info-item" v-if="selectedPreview.dimensions">
            <span class="info-label">해상도:</span>
            <span class="info-value"
              >{{ selectedPreview.dimensions.width }} ×
              {{ selectedPreview.dimensions.height }}</span
            >
          </div>
          <div class="info-item">
            <span class="info-label">수정일:</span>
            <span class="info-value">{{ formatDate(selectedPreview.file.lastModified) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

// 타입 정의
interface ImagePreview {
  file: File
  url: string
  loading: boolean
  error: boolean
  dimensions?: {
    width: number
    height: number
  }
}

// Props 정의
interface Props {
  files: File[]
}

const props = defineProps<Props>()

// Emits 정의
const emit = defineEmits<{
  'image-selected': [index: number]
  'image-removed': [index: number]
  'preview-ready': [previews: ImagePreview[]]
}>()

// 반응형 상태
const previews = ref<ImagePreview[]>([])
const selectedIndex = ref<number>(-1)

// 선택된 이미지 미리보기
const selectedPreview = computed(() => {
  if (selectedIndex.value >= 0 && selectedIndex.value < previews.value.length) {
    return previews.value[selectedIndex.value]
  }
  return null
})

// 파일 목록 변경 감지
watch(
  () => props.files,
  (newFiles) => {
    if (newFiles) {
      generatePreviews(newFiles)
    }
  },
  { immediate: true },
)

// 이미지 미리보기 생성
const generatePreviews = async (files: File[]) => {
  const newPreviews: ImagePreview[] = []

  for (const file of files) {
    const preview: ImagePreview = {
      file,
      url: '',
      loading: true,
      error: false,
    }

    try {
      // FileReader를 사용하여 이미지 URL 생성
      const url = await createImageUrl(file)
      preview.url = url
      preview.loading = false

      // 이미지 크기 정보 가져오기
      const dimensions = await getImageDimensions(url)
      preview.dimensions = dimensions
    } catch (error) {
      preview.loading = false
      preview.error = true
      console.error('이미지 미리보기 생성 실패:', error)
    }

    newPreviews.push(preview)
  }

  previews.value = newPreviews
  emit('preview-ready', newPreviews)
}

// 이미지 URL 생성
const createImageUrl = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      if (e.target?.result) {
        resolve(e.target.result as string)
      } else {
        reject(new Error('파일 읽기 실패'))
      }
    }
    reader.onerror = () => reject(new Error('파일 읽기 오류'))
    reader.readAsDataURL(file)
  })
}

// 이미지 크기 정보 가져오기
const getImageDimensions = (url: string): Promise<{ width: number; height: number }> => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => {
      resolve({
        width: img.naturalWidth,
        height: img.naturalHeight,
      })
    }
    img.onerror = () => reject(new Error('이미지 로드 실패'))
    img.src = url
  })
}

// 이미지 로드 완료
const onImageLoad = (index: number) => {
  if (previews.value[index]) {
    previews.value[index].loading = false
  }
}

// 이미지 로드 오류
const onImageError = (index: number) => {
  if (previews.value[index]) {
    previews.value[index].loading = false
    previews.value[index].error = true
  }
}

// 이미지 선택
const selectImage = (index: number) => {
  selectedIndex.value = index
  emit('image-selected', index)
}

// 이미지 제거
const removeImage = (index: number) => {
  // URL 해제 (메모리 누수 방지)
  if (previews.value[index]?.url) {
    URL.revokeObjectURL(previews.value[index].url)
  }

  previews.value.splice(index, 1)

  // 선택된 이미지가 제거된 경우 선택 해제
  if (selectedIndex.value === index) {
    selectedIndex.value = -1
  } else if (selectedIndex.value > index) {
    selectedIndex.value--
  }

  emit('image-removed', index)
}

// 상세 보기 닫기
const closeDetail = () => {
  selectedIndex.value = -1
}

// 유틸리티 함수들
const truncateFileName = (name: string, maxLength: number = 20): string => {
  if (name.length <= maxLength) return name

  const extension = name.split('.').pop()
  const nameWithoutExt = name.substring(0, name.lastIndexOf('.'))
  const truncatedName = nameWithoutExt.substring(0, maxLength - 3)

  return `${truncatedName}...${extension ? '.' + extension : ''}`
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'

  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (timestamp: number): string => {
  return new Date(timestamp).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<style scoped>
.image-preview-container {
  @apply space-y-4;
}

.preview-grid {
  @apply grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4;
}

.preview-item {
  @apply relative bg-white rounded-lg border border-gray-200 overflow-hidden cursor-pointer transition-all duration-200;
  @apply hover:border-blue-300 hover:shadow-md;
}

.preview-item.selected {
  @apply border-blue-500 ring-2 ring-blue-200;
}

.image-container {
  @apply relative aspect-square bg-gray-100;
}

.preview-image {
  @apply w-full h-full object-cover;
}

.loading-overlay {
  @apply absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center;
}

.error-overlay {
  @apply absolute inset-0 bg-red-100 bg-opacity-50 flex items-center justify-center;
}

.remove-button {
  @apply absolute top-2 right-2 w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center;
  @apply hover:bg-red-600 transition-colors opacity-0 group-hover:opacity-100;
}

.preview-item:hover .remove-button {
  @apply opacity-100;
}

.file-info {
  @apply p-3 space-y-1;
}

.file-name {
  @apply text-sm font-medium text-gray-900 truncate;
}

.file-size {
  @apply text-xs text-gray-500;
}

.file-dimensions {
  @apply text-xs text-gray-400;
}

.selected-image-detail {
  @apply bg-white rounded-lg border border-gray-200 overflow-hidden;
}

.detail-header {
  @apply flex items-center justify-between p-4 border-b border-gray-200;
}

.detail-title {
  @apply text-lg font-medium text-gray-900;
}

.close-button {
  @apply p-1 text-gray-400 hover:text-gray-600 transition-colors;
}

.detail-content {
  @apply p-4;
}

.detail-image {
  @apply w-full max-h-96 object-contain rounded-lg mb-4;
}

.detail-info {
  @apply space-y-2;
}

.info-item {
  @apply flex justify-between items-center;
}

.info-label {
  @apply text-sm font-medium text-gray-700;
}

.info-value {
  @apply text-sm text-gray-900;
}
</style>
