<template>
  <div class="file-upload-view">
    <div class="container mx-auto px-4 py-8">
      <!-- 페이지 헤더 -->
      <div class="page-header">
        <h1 class="page-title">파일 업로드 및 이미지 관리</h1>
        <p class="page-description">이미지 파일을 업로드하고 최적화하여 관리할 수 있습니다.</p>
      </div>

      <div class="content-grid">
        <!-- 왼쪽 패널: 업로드 영역 -->
        <div class="upload-panel">
          <div class="panel-header">
            <h2 class="panel-title">파일 업로드</h2>
          </div>

          <!-- 드래그 앤 드롭 업로드 영역 -->
          <FileUploadZone
            :max-file-size="10"
            :accepted-types="['image/jpeg', 'image/png', 'image/gif', 'image/webp']"
            :multiple="true"
            @file-selected="handleFileSelected"
            @upload-start="handleUploadStart"
            @upload-progress="handleUploadProgress"
            @upload-success="handleUploadSuccess"
            @upload-error="handleUploadError"
          />

          <!-- 이미지 미리보기 -->
          <div v-if="selectedFiles.length > 0" class="preview-section">
            <h3 class="section-title">이미지 미리보기</h3>
            <ImagePreview
              :files="selectedFiles"
              @image-selected="handleImageSelected"
              @image-removed="handleImageRemoved"
              @preview-ready="handlePreviewReady"
            />
          </div>
        </div>

        <!-- 오른쪽 패널: 옵션 및 진행률 -->
        <div class="options-panel">
          <!-- 최적화 옵션 -->
          <div class="options-section">
            <h2 class="panel-title">최적화 옵션</h2>
            <ImageOptimizationOptions
              v-model="optimizationOptions"
              :original-size="totalOriginalSize"
            />
          </div>

          <!-- 업로드 진행률 -->
          <div v-if="uploads.length > 0" class="progress-section">
            <h2 class="panel-title">업로드 진행률</h2>
            <UploadProgress
              :uploads="uploads"
              @retry-upload="handleRetryUpload"
              @cancel-upload="handleCancelUpload"
              @clear-completed="handleClearCompleted"
            />
          </div>
        </div>
      </div>

      <!-- 액션 버튼 -->
      <div class="action-buttons">
        <button
          @click="startUpload"
          :disabled="selectedFiles.length === 0 || isUploading"
          class="primary-button"
        >
          <svg v-if="isUploading" class="w-4 h-4 mr-2 animate-spin" fill="none" viewBox="0 0 24 24">
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
          {{ isUploading ? '업로드 중...' : '업로드 시작' }}
        </button>

        <button @click="clearAll" :disabled="selectedFiles.length === 0" class="secondary-button">
          모두 지우기
        </button>
      </div>

      <!-- 결과 메시지 -->
      <div v-if="resultMessage" class="result-message" :class="resultType">
        <div class="message-content">
          <svg
            v-if="resultType === 'success'"
            class="w-5 h-5 text-green-500"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M5 13l4 4L19 7"
            ></path>
          </svg>
          <svg
            v-else-if="resultType === 'error'"
            class="w-5 h-5 text-red-500"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            ></path>
          </svg>
          <span>{{ resultMessage }}</span>
        </div>
        <button @click="clearResultMessage" class="close-button">
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import FileUploadZone from '@/components/fileupload/FileUploadZone.vue'
import ImagePreview from '@/components/fileupload/ImagePreview.vue'
import UploadProgress from '@/components/fileupload/UploadProgress.vue'
import ImageOptimizationOptions from '@/components/fileupload/ImageOptimizationOptions.vue'

// 타입 정의
interface FileUpload {
  id: string
  file: File
  progress: number
  status: 'uploading' | 'completed' | 'failed'
  error?: string
}

interface OptimizationOptions {
  quality: number
  maxWidth: number | 'original'
  format: 'auto' | 'jpeg' | 'webp' | 'png'
  removeMetadata: boolean
  compressionLevel: number
  colorProfile: 'srgb' | 'adobe-rgb' | 'prophoto-rgb' | 'keep'
  addWatermark: boolean
  watermarkText: string
  watermarkPosition: 'bottom-right' | 'bottom-left' | 'top-right' | 'top-left' | 'center'
}

// 반응형 상태
const selectedFiles = ref<File[]>([])
const uploads = ref<FileUpload[]>([])
const isUploading = ref(false)
const resultMessage = ref('')
const resultType = ref<'success' | 'error'>('success')

// 최적화 옵션 기본값
const optimizationOptions = ref<OptimizationOptions>({
  quality: 85,
  maxWidth: 1024,
  format: 'webp',
  removeMetadata: true,
  compressionLevel: 6,
  colorProfile: 'srgb',
  addWatermark: false,
  watermarkText: '',
  watermarkPosition: 'bottom-right',
})

// 계산된 속성들
const totalOriginalSize = computed(() => {
  return selectedFiles.value.reduce((total, file) => total + file.size, 0)
})

// 이벤트 핸들러들
const handleFileSelected = (files: File[]) => {
  selectedFiles.value = [...selectedFiles.value, ...files]
  clearResultMessage()
}

const handleUploadStart = (files: File[]) => {
  isUploading.value = true

  // 업로드 항목 생성
  const newUploads: FileUpload[] = files.map((file) => ({
    id: generateId(),
    file,
    progress: 0,
    status: 'uploading',
  }))

  uploads.value = [...uploads.value, ...newUploads]
}

const handleUploadProgress = (progress: number) => {
  // 전체 진행률을 각 업로드 항목에 분배
  const uploadingItems = uploads.value.filter((upload) => upload.status === 'uploading')
  if (uploadingItems.length > 0) {
    uploadingItems.forEach((upload) => {
      upload.progress = progress
    })
  }
}

const handleUploadSuccess = (result: any) => {
  // 업로드 완료 처리
  const uploadingItems = uploads.value.filter((upload) => upload.status === 'uploading')
  uploadingItems.forEach((upload) => {
    upload.status = 'completed'
    upload.progress = 100
  })

  isUploading.value = false
  showResultMessage('파일 업로드가 완료되었습니다.', 'success')
}

const handleUploadError = (error: string) => {
  // 업로드 실패 처리
  const uploadingItems = uploads.value.filter((upload) => upload.status === 'uploading')
  uploadingItems.forEach((upload) => {
    upload.status = 'failed'
    upload.error = error
  })

  isUploading.value = false
  showResultMessage(`업로드 실패: ${error}`, 'error')
}

const handleImageSelected = (index: number) => {
  console.log('이미지 선택됨:', index)
}

const handleImageRemoved = (index: number) => {
  selectedFiles.value.splice(index, 1)
}

const handlePreviewReady = (previews: any[]) => {
  console.log('미리보기 준비됨:', previews.length)
}

const handleRetryUpload = (index: number) => {
  const upload = uploads.value[index]
  if (upload) {
    upload.status = 'uploading'
    upload.progress = 0
    upload.error = undefined

    // 실제 재시도 로직 구현
    simulateUpload(upload)
  }
}

const handleCancelUpload = (index: number) => {
  const upload = uploads.value[index]
  if (upload && upload.status === 'uploading') {
    upload.status = 'failed'
    upload.error = '사용자에 의해 취소됨'
  }
}

const handleClearCompleted = () => {
  uploads.value = uploads.value.filter((upload) => upload.status !== 'completed')
}

const startUpload = async () => {
  if (selectedFiles.value.length === 0) return

  try {
    isUploading.value = true

    // 실제 업로드 로직 구현
    for (const file of selectedFiles.value) {
      await simulateFileUpload(file)
    }

    // 업로드 완료 후 파일 목록 초기화
    selectedFiles.value = []
    showResultMessage('모든 파일이 성공적으로 업로드되었습니다.', 'success')
  } catch (error) {
    showResultMessage(`업로드 중 오류가 발생했습니다: ${error}`, 'error')
  } finally {
    isUploading.value = false
  }
}

const clearAll = () => {
  selectedFiles.value = []
  uploads.value = []
  clearResultMessage()
}

const showResultMessage = (message: string, type: 'success' | 'error') => {
  resultMessage.value = message
  resultType.value = type

  // 5초 후 자동으로 메시지 제거
  setTimeout(() => {
    clearResultMessage()
  }, 5000)
}

const clearResultMessage = () => {
  resultMessage.value = ''
}

// 유틸리티 함수들
const generateId = (): string => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

const simulateFileUpload = async (file: File): Promise<void> => {
  return new Promise((resolve, reject) => {
    const upload = uploads.value.find((u) => u.file === file)
    if (!upload) {
      reject(new Error('업로드 항목을 찾을 수 없습니다.'))
      return
    }

    let progress = 0
    const interval = setInterval(() => {
      progress += Math.random() * 20
      if (progress >= 100) {
        progress = 100
        clearInterval(interval)
        upload.status = 'completed'
        upload.progress = 100
        resolve()
      } else {
        upload.progress = progress
      }
    }, 200)
  })
}

const simulateUpload = (upload: FileUpload): void => {
  let progress = 0
  const interval = setInterval(() => {
    progress += Math.random() * 20
    if (progress >= 100) {
      progress = 100
      clearInterval(interval)
      upload.status = 'completed'
      upload.progress = 100
    } else {
      upload.progress = progress
    }
  }, 200)
}

// 컴포넌트 마운트 시 초기화
onMounted(() => {
  console.log('파일 업로드 페이지가 로드되었습니다.')
})
</script>

<style scoped>
.file-upload-view {
  @apply min-h-screen bg-gray-50;
}

.container {
  @apply max-w-7xl;
}

.page-header {
  @apply text-center mb-8;
}

.page-title {
  @apply text-3xl font-bold text-gray-900 mb-2;
}

.page-description {
  @apply text-lg text-gray-600;
}

.content-grid {
  @apply grid grid-cols-1 lg:grid-cols-3 gap-8 mb-8;
}

.upload-panel {
  @apply lg:col-span-2 space-y-6;
}

.options-panel {
  @apply space-y-6;
}

.panel-header {
  @apply mb-4;
}

.panel-title {
  @apply text-xl font-semibold text-gray-900;
}

.preview-section {
  @apply bg-white rounded-lg border border-gray-200 p-6;
}

.section-title {
  @apply text-lg font-medium text-gray-900 mb-4;
}

.options-section {
  @apply bg-white rounded-lg border border-gray-200 p-6;
}

.progress-section {
  @apply bg-white rounded-lg border border-gray-200 p-6;
}

.action-buttons {
  @apply flex justify-center space-x-4;
}

.primary-button {
  @apply flex items-center px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors;
  @apply disabled:opacity-50 disabled:cursor-not-allowed;
}

.secondary-button {
  @apply px-6 py-3 bg-gray-200 text-gray-700 font-medium rounded-lg hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 transition-colors;
  @apply disabled:opacity-50 disabled:cursor-not-allowed;
}

.result-message {
  @apply fixed top-4 right-4 max-w-md bg-white border rounded-lg shadow-lg p-4 z-50;
  @apply flex items-center justify-between;
}

.result-message.success {
  @apply border-green-200 bg-green-50;
}

.result-message.error {
  @apply border-red-200 bg-red-50;
}

.message-content {
  @apply flex items-center space-x-2;
}

.close-button {
  @apply p-1 text-gray-400 hover:text-gray-600 transition-colors;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .content-grid {
    @apply grid-cols-1;
  }

  .upload-panel {
    @apply col-span-1;
  }

  .options-panel {
    @apply order-first;
  }
}

@media (max-width: 768px) {
  .page-title {
    @apply text-2xl;
  }

  .page-description {
    @apply text-base;
  }

  .action-buttons {
    @apply flex-col space-y-2 space-x-0;
  }

  .primary-button,
  .secondary-button {
    @apply w-full;
  }
}
</style>
