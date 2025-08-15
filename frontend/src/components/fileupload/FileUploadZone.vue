<template>
  <div
    ref="dropZone"
    class="file-upload-zone"
    :class="{
      'drag-over': isDragOver,
      uploading: isUploading,
      error: hasError,
    }"
    @drop="handleDrop"
    @dragover="handleDragOver"
    @dragenter="handleDragEnter"
    @dragleave="handleDragLeave"
    @click="triggerFileInput"
  >
    <!-- 파일 입력 (숨겨진) -->
    <input
      ref="fileInput"
      type="file"
      multiple
      accept="image/*"
      @change="handleFileSelect"
      class="hidden"
    />

    <!-- 업로드 영역 내용 -->
    <div class="upload-content">
      <div v-if="!isUploading && !hasError" class="upload-prompt">
        <div class="upload-icon">
          <svg
            class="w-12 h-12 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
            ></path>
          </svg>
        </div>
        <div class="upload-text">
          <p class="text-lg font-medium text-gray-900">
            {{ isDragOver ? '파일을 여기에 놓으세요' : '파일을 드래그하거나 클릭하여 업로드' }}
          </p>
          <p class="text-sm text-gray-500 mt-1">PNG, JPG, GIF, WEBP 파일 (최대 10MB)</p>
        </div>
      </div>

      <!-- 업로드 중 상태 -->
      <div v-if="isUploading" class="upload-progress">
        <div class="progress-icon">
          <svg class="w-8 h-8 text-blue-500 animate-spin" fill="none" viewBox="0 0 24 24">
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
        <p class="text-sm text-gray-600 mt-2">업로드 중...</p>
      </div>

      <!-- 에러 상태 -->
      <div v-if="hasError" class="upload-error">
        <div class="error-icon">
          <svg class="w-8 h-8 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            ></path>
          </svg>
        </div>
        <p class="text-sm text-red-600 mt-2">{{ errorMessage }}</p>
        <button
          @click.stop="retryUpload"
          class="mt-2 px-3 py-1 text-sm bg-red-100 text-red-700 rounded hover:bg-red-200 transition-colors"
        >
          다시 시도
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

// Props 정의
interface Props {
  maxFileSize?: number // MB 단위
  acceptedTypes?: string[]
  multiple?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  maxFileSize: 10, // 기본 10MB
  acceptedTypes: () => ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
  multiple: true,
})

// Emits 정의
const emit = defineEmits<{
  'file-selected': [files: File[]]
  'upload-start': [files: File[]]
  'upload-progress': [progress: number]
  'upload-success': [result: any]
  'upload-error': [error: string]
}>()

// 반응형 상태
const dropZone = ref<HTMLElement>()
const fileInput = ref<HTMLInputElement>()
const isDragOver = ref(false)
const isUploading = ref(false)
const hasError = ref(false)
const errorMessage = ref('')

// 파일 검증
const validateFile = (file: File): string | null => {
  // 파일 크기 검증
  if (file.size > props.maxFileSize * 1024 * 1024) {
    return `파일 크기가 너무 큽니다. 최대 ${props.maxFileSize}MB까지 업로드 가능합니다.`
  }

  // 파일 타입 검증
  if (!props.acceptedTypes.includes(file.type)) {
    return '지원하지 않는 파일 형식입니다.'
  }

  return null
}

// 파일 선택 처리
const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files) {
    processFiles(Array.from(target.files))
  }
}

// 드래그 앤 드롭 처리
const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = false

  if (event.dataTransfer?.files) {
    processFiles(Array.from(event.dataTransfer.files))
  }
}

const handleDragOver = (event: DragEvent) => {
  event.preventDefault()
}

const handleDragEnter = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = true
}

const handleDragLeave = (event: DragEvent) => {
  event.preventDefault()
  // 드래그가 실제로 영역을 벗어났는지 확인
  if (!dropZone.value?.contains(event.relatedTarget as Node)) {
    isDragOver.value = false
  }
}

// 파일 처리
const processFiles = (files: File[]) => {
  // 에러 상태 초기화
  hasError.value = false
  errorMessage.value = ''

  // 파일 검증
  const validFiles: File[] = []
  const errors: string[] = []

  files.forEach((file) => {
    const error = validateFile(file)
    if (error) {
      errors.push(`${file.name}: ${error}`)
    } else {
      validFiles.push(file)
    }
  })

  // 에러가 있으면 표시
  if (errors.length > 0) {
    hasError.value = true
    errorMessage.value = errors.join('\n')
    return
  }

  // 유효한 파일이 있으면 업로드 시작
  if (validFiles.length > 0) {
    emit('file-selected', validFiles)
    startUpload(validFiles)
  }
}

// 업로드 시작
const startUpload = async (files: File[]) => {
  isUploading.value = true
  hasError.value = false

  try {
    emit('upload-start', files)

    // 실제 업로드 로직은 부모 컴포넌트에서 처리
    // 여기서는 시뮬레이션
    for (let i = 0; i <= 100; i += 10) {
      emit('upload-progress', i)
      await new Promise((resolve) => setTimeout(resolve, 100))
    }

    emit('upload-success', { files })
  } catch (error) {
    hasError.value = true
    errorMessage.value = error instanceof Error ? error.message : '업로드 중 오류가 발생했습니다.'
    emit('upload-error', errorMessage.value)
  } finally {
    isUploading.value = false
  }
}

// 파일 입력 트리거
const triggerFileInput = () => {
  if (!isUploading.value) {
    fileInput.value?.click()
  }
}

// 재시도
const retryUpload = () => {
  hasError.value = false
  errorMessage.value = ''
  if (fileInput.value?.files) {
    processFiles(Array.from(fileInput.value.files))
  }
}
</script>

<style scoped>
.file-upload-zone {
  @apply border-2 border-dashed border-gray-300 rounded-lg p-8 text-center cursor-pointer transition-all duration-200;
  @apply hover:border-blue-400 hover:bg-blue-50;
}

.file-upload-zone.drag-over {
  @apply border-blue-500 bg-blue-50;
}

.file-upload-zone.uploading {
  @apply border-blue-500 bg-blue-50 cursor-not-allowed;
}

.file-upload-zone.error {
  @apply border-red-500 bg-red-50;
}

.upload-content {
  @apply flex flex-col items-center justify-center min-h-[200px];
}

.upload-prompt {
  @apply flex flex-col items-center;
}

.upload-icon {
  @apply mb-4;
}

.upload-text {
  @apply space-y-2;
}

.upload-progress {
  @apply flex flex-col items-center;
}

.progress-icon {
  @apply mb-2;
}

.upload-error {
  @apply flex flex-col items-center;
}

.error-icon {
  @apply mb-2;
}

.hidden {
  @apply hidden;
}
</style>
