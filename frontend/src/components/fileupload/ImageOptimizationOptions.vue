<template>
  <div class="optimization-options">
    <div class="options-header">
      <h3 class="options-title">이미지 최적화 옵션</h3>
      <button @click="toggleAdvanced" class="toggle-button" :class="{ expanded: showAdvanced }">
        {{ showAdvanced ? '간단히' : '고급 옵션' }}
        <svg
          class="w-4 h-4 ml-1 transition-transform"
          :class="{ 'rotate-180': showAdvanced }"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M19 9l-7 7-7-7"
          ></path>
        </svg>
      </button>
    </div>

    <!-- 기본 옵션 -->
    <div class="basic-options">
      <!-- 품질 설정 -->
      <div class="option-group">
        <label class="option-label">이미지 품질</label>
        <div class="quality-control">
          <input
            v-model="options.quality"
            type="range"
            min="10"
            max="100"
            step="5"
            class="quality-slider"
          />
          <span class="quality-value">{{ options.quality }}%</span>
        </div>
        <p class="option-description">낮은 품질은 파일 크기를 줄이지만 화질이 저하됩니다.</p>
      </div>

      <!-- 최대 크기 설정 -->
      <div class="option-group">
        <label class="option-label">최대 크기</label>
        <div class="size-control">
          <select v-model="options.maxWidth" class="size-select">
            <option value="800">800px</option>
            <option value="1024">1024px</option>
            <option value="1280">1280px</option>
            <option value="1920">1920px</option>
            <option value="original">원본 크기</option>
          </select>
          <span class="size-text">너비 기준</span>
        </div>
        <p class="option-description">이미지가 이 크기를 초과하면 자동으로 리사이즈됩니다.</p>
      </div>

      <!-- 포맷 설정 -->
      <div class="option-group">
        <label class="option-label">출력 포맷</label>
        <div class="format-control">
          <label class="format-option">
            <input v-model="options.format" type="radio" value="auto" class="format-radio" />
            <span class="format-text">자동 선택</span>
          </label>
          <label class="format-option">
            <input v-model="options.format" type="radio" value="jpeg" class="format-radio" />
            <span class="format-text">JPEG</span>
          </label>
          <label class="format-option">
            <input v-model="options.format" type="radio" value="webp" class="format-radio" />
            <span class="format-text">WebP</span>
          </label>
          <label class="format-option">
            <input v-model="options.format" type="radio" value="png" class="format-radio" />
            <span class="format-text">PNG</span>
          </label>
        </div>
        <p class="option-description">
          WebP는 더 작은 파일 크기를 제공하지만 일부 브라우저에서 지원되지 않을 수 있습니다.
        </p>
      </div>
    </div>

    <!-- 고급 옵션 -->
    <div v-if="showAdvanced" class="advanced-options">
      <div class="advanced-section">
        <h4 class="section-title">고급 설정</h4>

        <!-- 메타데이터 제거 -->
        <div class="option-group">
          <label class="option-label">
            <input v-model="options.removeMetadata" type="checkbox" class="checkbox" />
            메타데이터 제거
          </label>
          <p class="option-description">EXIF 데이터를 제거하여 파일 크기를 줄입니다.</p>
        </div>

        <!-- 압축 레벨 -->
        <div class="option-group">
          <label class="option-label">압축 레벨</label>
          <div class="compression-control">
            <input
              v-model="options.compressionLevel"
              type="range"
              min="1"
              max="9"
              step="1"
              class="compression-slider"
            />
            <span class="compression-value">{{ options.compressionLevel }}</span>
          </div>
          <p class="option-description">
            높은 압축 레벨은 더 작은 파일 크기를 제공하지만 처리 시간이 길어집니다.
          </p>
        </div>

        <!-- 색상 프로파일 -->
        <div class="option-group">
          <label class="option-label">색상 프로파일</label>
          <select v-model="options.colorProfile" class="profile-select">
            <option value="srgb">sRGB (웹 표준)</option>
            <option value="adobe-rgb">Adobe RGB</option>
            <option value="prophoto-rgb">ProPhoto RGB</option>
            <option value="keep">원본 유지</option>
          </select>
          <p class="option-description">
            색상 프로파일을 변경하여 색상 정확도를 조정할 수 있습니다.
          </p>
        </div>

        <!-- 워터마크 설정 -->
        <div class="option-group">
          <label class="option-label">
            <input v-model="options.addWatermark" type="checkbox" class="checkbox" />
            워터마크 추가
          </label>
          <div v-if="options.addWatermark" class="watermark-options">
            <input
              v-model="options.watermarkText"
              type="text"
              placeholder="워터마크 텍스트"
              class="watermark-input"
            />
            <select v-model="options.watermarkPosition" class="watermark-position">
              <option value="bottom-right">우하단</option>
              <option value="bottom-left">좌하단</option>
              <option value="top-right">우상단</option>
              <option value="top-left">좌상단</option>
              <option value="center">중앙</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- 프리셋 -->
    <div class="presets-section">
      <h4 class="section-title">빠른 설정</h4>
      <div class="preset-buttons">
        <button
          @click="applyPreset('web')"
          class="preset-button"
          :class="{ active: isPresetActive('web') }"
        >
          웹 최적화
        </button>
        <button
          @click="applyPreset('mobile')"
          class="preset-button"
          :class="{ active: isPresetActive('mobile') }"
        >
          모바일 최적화
        </button>
        <button
          @click="applyPreset('print')"
          class="preset-button"
          :class="{ active: isPresetActive('print') }"
        >
          인쇄용
        </button>
        <button
          @click="applyPreset('social')"
          class="preset-button"
          :class="{ active: isPresetActive('social') }"
        >
          소셜미디어
        </button>
      </div>
    </div>

    <!-- 예상 결과 -->
    <div class="preview-section">
      <h4 class="section-title">예상 결과</h4>
      <div class="preview-info">
        <div class="preview-item">
          <span class="preview-label">예상 파일 크기:</span>
          <span class="preview-value">{{ estimatedSize }}</span>
        </div>
        <div class="preview-item">
          <span class="preview-label">압축률:</span>
          <span class="preview-value">{{ compressionRatio }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

// 타입 정의
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

// Props 정의
interface Props {
  modelValue: OptimizationOptions
  originalSize?: number // 원본 파일 크기 (bytes)
}

const props = defineProps<Props>()

// Emits 정의
const emit = defineEmits<{
  'update:modelValue': [options: OptimizationOptions]
}>()

// 반응형 상태
const showAdvanced = ref(false)
const options = ref<OptimizationOptions>({ ...props.modelValue })

// Props 변경 감지
watch(
  () => props.modelValue,
  (newValue) => {
    options.value = { ...newValue }
  },
  { deep: true },
)

// 옵션 변경 감지
watch(
  options,
  (newValue) => {
    emit('update:modelValue', { ...newValue })
  },
  { deep: true },
)

// 프리셋 정의
const presets = {
  web: {
    quality: 85,
    maxWidth: 1024,
    format: 'webp' as const,
    removeMetadata: true,
    compressionLevel: 6,
    colorProfile: 'srgb' as const,
    addWatermark: false,
    watermarkText: '',
    watermarkPosition: 'bottom-right' as const,
  },
  mobile: {
    quality: 75,
    maxWidth: 800,
    format: 'jpeg' as const,
    removeMetadata: true,
    compressionLevel: 7,
    colorProfile: 'srgb' as const,
    addWatermark: false,
    watermarkText: '',
    watermarkPosition: 'bottom-right' as const,
  },
  print: {
    quality: 95,
    maxWidth: 'original' as const,
    format: 'png' as const,
    removeMetadata: false,
    compressionLevel: 3,
    colorProfile: 'adobe-rgb' as const,
    addWatermark: false,
    watermarkText: '',
    watermarkPosition: 'bottom-right' as const,
  },
  social: {
    quality: 80,
    maxWidth: 1280,
    format: 'jpeg' as const,
    removeMetadata: true,
    compressionLevel: 5,
    colorProfile: 'srgb' as const,
    addWatermark: false,
    watermarkText: '',
    watermarkPosition: 'bottom-right' as const,
  },
}

// 계산된 속성들
const estimatedSize = computed(() => {
  if (!props.originalSize) return '계산 중...'

  let ratio = 1

  // 품질에 따른 크기 감소
  ratio *= options.value.quality / 100

  // 포맷에 따른 크기 감소
  switch (options.value.format) {
    case 'webp':
      ratio *= 0.7
      break
    case 'jpeg':
      ratio *= 0.8
      break
    case 'png':
      ratio *= 0.9
      break
  }

  // 메타데이터 제거
  if (options.value.removeMetadata) {
    ratio *= 0.95
  }

  const estimated = Math.round(props.originalSize * ratio)
  return formatFileSize(estimated)
})

const compressionRatio = computed(() => {
  if (!props.originalSize) return '계산 중...'

  const ratio = options.value.quality / 100
  const percentage = Math.round((1 - ratio) * 100)
  return `${percentage}% 감소`
})

// 메서드들
const toggleAdvanced = () => {
  showAdvanced.value = !showAdvanced.value
}

const applyPreset = (presetName: keyof typeof presets) => {
  options.value = { ...presets[presetName] }
}

const isPresetActive = (presetName: keyof typeof presets) => {
  const preset = presets[presetName]
  return Object.keys(preset).every(
    (key) =>
      options.value[key as keyof OptimizationOptions] === preset[key as keyof OptimizationOptions],
  )
}

// 유틸리티 함수
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'

  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>

<style scoped>
.optimization-options {
  @apply bg-white rounded-lg border border-gray-200 p-6 space-y-6;
}

.options-header {
  @apply flex items-center justify-between;
}

.options-title {
  @apply text-lg font-medium text-gray-900;
}

.toggle-button {
  @apply flex items-center text-sm text-blue-600 hover:text-blue-700 transition-colors;
}

.toggle-button.expanded {
  @apply text-gray-600;
}

.basic-options {
  @apply space-y-6;
}

.option-group {
  @apply space-y-2;
}

.option-label {
  @apply block text-sm font-medium text-gray-700;
}

.option-description {
  @apply text-xs text-gray-500;
}

.quality-control {
  @apply flex items-center space-x-3;
}

.quality-slider {
  @apply flex-1 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer;
}

.quality-slider::-webkit-slider-thumb {
  @apply appearance-none w-4 h-4 bg-blue-500 rounded-full cursor-pointer;
}

.quality-value {
  @apply text-sm font-medium text-gray-700 min-w-[3rem];
}

.size-control {
  @apply flex items-center space-x-2;
}

.size-select {
  @apply px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500;
}

.size-text {
  @apply text-sm text-gray-500;
}

.format-control {
  @apply flex flex-wrap gap-3;
}

.format-option {
  @apply flex items-center space-x-2 cursor-pointer;
}

.format-radio {
  @apply text-blue-600 focus:ring-blue-500;
}

.format-text {
  @apply text-sm text-gray-700;
}

.advanced-options {
  @apply border-t border-gray-200 pt-6;
}

.advanced-section {
  @apply space-y-6;
}

.section-title {
  @apply text-md font-medium text-gray-900;
}

.checkbox {
  @apply text-blue-600 focus:ring-blue-500;
}

.compression-control {
  @apply flex items-center space-x-3;
}

.compression-slider {
  @apply flex-1 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer;
}

.compression-slider::-webkit-slider-thumb {
  @apply appearance-none w-4 h-4 bg-blue-500 rounded-full cursor-pointer;
}

.compression-value {
  @apply text-sm font-medium text-gray-700 min-w-[1rem];
}

.profile-select {
  @apply w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500;
}

.watermark-options {
  @apply mt-2 space-y-2;
}

.watermark-input {
  @apply w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500;
}

.watermark-position {
  @apply w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500;
}

.presets-section {
  @apply border-t border-gray-200 pt-6;
}

.preset-buttons {
  @apply flex flex-wrap gap-2;
}

.preset-button {
  @apply px-3 py-2 text-sm bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors;
}

.preset-button.active {
  @apply bg-blue-500 text-white hover:bg-blue-600;
}

.preview-section {
  @apply border-t border-gray-200 pt-6;
}

.preview-info {
  @apply space-y-2;
}

.preview-item {
  @apply flex justify-between items-center;
}

.preview-label {
  @apply text-sm text-gray-600;
}

.preview-value {
  @apply text-sm font-medium text-gray-900;
}
</style>
