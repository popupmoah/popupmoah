<template>
  <div class="upload-progress-container">
    <!-- 전체 진행률 -->
    <div v-if="uploads.length > 0" class="overall-progress">
      <div class="progress-header">
        <h3 class="progress-title">업로드 진행 상황</h3>
        <div class="progress-stats">
          <span class="stat-item"> 완료: {{ completedCount }} </span>
          <span class="stat-item"> 진행중: {{ inProgressCount }} </span>
          <span class="stat-item"> 실패: {{ failedCount }} </span>
        </div>
      </div>

      <!-- 전체 진행률 바 -->
      <div class="overall-progress-bar">
        <div class="progress-bar">
          <div
            class="progress-fill"
            :style="{ width: `${overallProgress}%` }"
            :class="progressBarClass"
          ></div>
        </div>
        <span class="progress-text">{{ Math.round(overallProgress) }}%</span>
      </div>
    </div>

    <!-- 개별 파일 업로드 진행률 -->
    <div class="file-uploads">
      <div
        v-for="(upload, index) in uploads"
        :key="upload.id"
        class="file-upload-item"
        :class="upload.status"
      >
        <!-- 파일 정보 -->
        <div class="file-info">
          <div class="file-icon">
            <svg
              v-if="upload.status === 'completed'"
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
              v-else-if="upload.status === 'failed'"
              class="w-5 h-5 text-red-500"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              ></path>
            </svg>
            <svg
              v-else
              class="w-5 h-5 text-blue-500"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
              ></path>
            </svg>
          </div>

          <div class="file-details">
            <p class="file-name" :title="upload.file.name">
              {{ truncateFileName(upload.file.name) }}
            </p>
            <p class="file-size">{{ formatFileSize(upload.file.size) }}</p>
          </div>
        </div>

        <!-- 진행률 바 -->
        <div class="file-progress">
          <div class="progress-bar">
            <div
              class="progress-fill"
              :style="{ width: `${upload.progress}%` }"
              :class="getProgressBarClass(upload.status)"
            ></div>
          </div>
          <span class="progress-text">{{ Math.round(upload.progress) }}%</span>
        </div>

        <!-- 상태 및 액션 -->
        <div class="file-actions">
          <span v-if="upload.status === 'uploading'" class="status-text uploading">
            업로드 중...
          </span>
          <span v-else-if="upload.status === 'completed'" class="status-text completed">
            완료
          </span>
          <span v-else-if="upload.status === 'failed'" class="status-text failed"> 실패 </span>

          <!-- 재시도 버튼 -->
          <button
            v-if="upload.status === 'failed'"
            @click="retryUpload(index)"
            class="retry-button"
            title="다시 시도"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
              ></path>
            </svg>
          </button>

          <!-- 취소 버튼 -->
          <button
            v-if="upload.status === 'uploading'"
            @click="cancelUpload(index)"
            class="cancel-button"
            title="취소"
          >
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

    <!-- 완료된 업로드 정리 -->
    <div v-if="completedCount > 0" class="completed-actions">
      <button @click="clearCompleted" class="clear-button">완료된 항목 정리</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

// 타입 정의
interface FileUpload {
  id: string
  file: File
  progress: number
  status: 'uploading' | 'completed' | 'failed'
  error?: string
}

// Props 정의
interface Props {
  uploads: FileUpload[]
}

const props = defineProps<Props>()

// Emits 정의
const emit = defineEmits<{
  'retry-upload': [index: number]
  'cancel-upload': [index: number]
  'clear-completed': []
}>()

// 반응형 상태
const uploads = ref<FileUpload[]>([])

// Props 변경 감지
watch(
  () => props.uploads,
  (newUploads) => {
    uploads.value = [...newUploads]
  },
  { immediate: true },
)

// 계산된 속성들
const completedCount = computed(
  () => uploads.value.filter((upload) => upload.status === 'completed').length,
)

const inProgressCount = computed(
  () => uploads.value.filter((upload) => upload.status === 'uploading').length,
)

const failedCount = computed(
  () => uploads.value.filter((upload) => upload.status === 'failed').length,
)

const overallProgress = computed(() => {
  if (uploads.value.length === 0) return 0

  const totalProgress = uploads.value.reduce((sum, upload) => {
    if (upload.status === 'completed') return sum + 100
    if (upload.status === 'failed') return sum + 0
    return sum + upload.progress
  }, 0)

  return totalProgress / uploads.value.length
})

const progressBarClass = computed(() => {
  if (failedCount.value > 0) return 'bg-red-500'
  if (completedCount.value === uploads.value.length) return 'bg-green-500'
  return 'bg-blue-500'
})

// 진행률 바 클래스 결정
const getProgressBarClass = (status: string) => {
  switch (status) {
    case 'completed':
      return 'bg-green-500'
    case 'failed':
      return 'bg-red-500'
    default:
      return 'bg-blue-500'
  }
}

// 액션 메서드들
const retryUpload = (index: number) => {
  emit('retry-upload', index)
}

const cancelUpload = (index: number) => {
  emit('cancel-upload', index)
}

const clearCompleted = () => {
  emit('clear-completed')
}

// 유틸리티 함수들
const truncateFileName = (name: string, maxLength: number = 25): string => {
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
</script>

<style scoped>
.upload-progress-container {
  @apply space-y-4;
}

.overall-progress {
  @apply bg-white rounded-lg border border-gray-200 p-4;
}

.progress-header {
  @apply flex items-center justify-between mb-3;
}

.progress-title {
  @apply text-lg font-medium text-gray-900;
}

.progress-stats {
  @apply flex space-x-4 text-sm;
}

.stat-item {
  @apply text-gray-600;
}

.overall-progress-bar {
  @apply flex items-center space-x-3;
}

.progress-bar {
  @apply flex-1 h-2 bg-gray-200 rounded-full overflow-hidden;
}

.progress-fill {
  @apply h-full transition-all duration-300 ease-out;
}

.progress-text {
  @apply text-sm font-medium text-gray-700 min-w-[3rem] text-right;
}

.file-uploads {
  @apply space-y-2;
}

.file-upload-item {
  @apply bg-white rounded-lg border border-gray-200 p-4 flex items-center space-x-4;
  @apply transition-all duration-200;
}

.file-upload-item.completed {
  @apply border-green-200 bg-green-50;
}

.file-upload-item.failed {
  @apply border-red-200 bg-red-50;
}

.file-info {
  @apply flex items-center space-x-3 flex-1 min-w-0;
}

.file-icon {
  @apply flex-shrink-0;
}

.file-details {
  @apply min-w-0 flex-1;
}

.file-name {
  @apply text-sm font-medium text-gray-900 truncate;
}

.file-size {
  @apply text-xs text-gray-500;
}

.file-progress {
  @apply flex items-center space-x-3 flex-1;
}

.file-actions {
  @apply flex items-center space-x-2 flex-shrink-0;
}

.status-text {
  @apply text-sm font-medium;
}

.status-text.uploading {
  @apply text-blue-600;
}

.status-text.completed {
  @apply text-green-600;
}

.status-text.failed {
  @apply text-red-600;
}

.retry-button {
  @apply p-1 text-red-500 hover:text-red-700 hover:bg-red-100 rounded transition-colors;
}

.cancel-button {
  @apply p-1 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded transition-colors;
}

.completed-actions {
  @apply flex justify-center;
}

.clear-button {
  @apply px-4 py-2 text-sm bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors;
}
</style>
