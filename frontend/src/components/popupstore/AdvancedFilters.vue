<template>
  <q-card class="advanced-filters">
    <q-card-section>
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-semibold text-gray-900">고급 필터</h3>
        <q-btn
          flat
          round
          size="sm"
          :icon="isExpanded ? 'expand_less' : 'expand_more'"
          @click="toggleExpanded"
        />
      </div>

      <div v-if="isExpanded" class="space-y-4">
        <!-- 날짜 범위 필터 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <q-input v-model="filters.startDate" label="시작일 이후" outlined type="date" clearable />
          <q-input v-model="filters.endDate" label="종료일 이전" outlined type="date" clearable />
        </div>

        <!-- 평점 필터 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">최소 평점</label>
          <div class="flex items-center space-x-2">
            <q-slider
              v-model="filters.minRating"
              :min="0"
              :max="5"
              :step="0.5"
              color="primary"
              class="flex-1"
            />
            <span class="text-sm font-medium text-gray-600 w-12 text-center">
              {{ filters.minRating }}
            </span>
          </div>
        </div>

        <!-- 위치 필터 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">지역</label>
          <q-select
            v-model="filters.regions"
            :options="regionOptions"
            label="지역 선택"
            outlined
            multiple
            use-chips
            clearable
          />
        </div>

        <!-- 정렬 옵션 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">정렬</label>
          <q-select
            v-model="filters.sortBy"
            :options="sortOptions"
            label="정렬 기준"
            outlined
            emit-value
            map-options
          />
        </div>

        <!-- 필터 액션 -->
        <div class="flex justify-end space-x-2 pt-4 border-t">
          <q-btn color="secondary" label="초기화" @click="resetFilters" flat />
          <q-btn color="primary" label="적용" @click="applyFilters" />
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'

interface Filters {
  startDate: string
  endDate: string
  minRating: number
  regions: string[]
  sortBy: string
}

interface Props {
  modelValue: Filters
}

interface Emits {
  'update:modelValue': [filters: Filters]
  apply: [filters: Filters]
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const isExpanded = ref(false)

const filters = reactive<Filters>({
  startDate: '',
  endDate: '',
  minRating: 0,
  regions: [],
  sortBy: 'name',
})

const regionOptions = [
  { label: '강남구', value: '강남구' },
  { label: '서초구', value: '서초구' },
  { label: '마포구', value: '마포구' },
  { label: '종로구', value: '종로구' },
  { label: '중구', value: '중구' },
  { label: '용산구', value: '용산구' },
  { label: '성동구', value: '성동구' },
  { label: '광진구', value: '광진구' },
  { label: '동대문구', value: '동대문구' },
  { label: '중랑구', value: '중랑구' },
  { label: '성북구', value: '성북구' },
  { label: '강북구', value: '강북구' },
  { label: '도봉구', value: '도봉구' },
  { label: '노원구', value: '노원구' },
  { label: '은평구', value: '은평구' },
  { label: '서대문구', value: '서대문구' },
  { label: '양천구', value: '양천구' },
  { label: '강서구', value: '강서구' },
  { label: '구로구', value: '구로구' },
  { label: '금천구', value: '금천구' },
  { label: '영등포구', value: '영등포구' },
  { label: '동작구', value: '동작구' },
  { label: '관악구', value: '관악구' },
  { label: '송파구', value: '송파구' },
  { label: '강동구', value: '강동구' },
]

const sortOptions = [
  { label: '이름순', value: 'name' },
  { label: '평점순', value: 'rating' },
  { label: '최신순', value: 'createdAt' },
  { label: '시작일순', value: 'startDate' },
  { label: '종료일순', value: 'endDate' },
]

const toggleExpanded = () => {
  isExpanded.value = !isExpanded.value
}

const resetFilters = () => {
  Object.assign(filters, {
    startDate: '',
    endDate: '',
    minRating: 0,
    regions: [],
    sortBy: 'name',
  })
  emit('update:modelValue', { ...filters })
}

const applyFilters = () => {
  emit('update:modelValue', { ...filters })
  emit('apply', { ...filters })
}

// props 변경 시 로컬 상태 업데이트
watch(
  () => props.modelValue,
  (newValue) => {
    Object.assign(filters, newValue)
  },
  { deep: true, immediate: true },
)

// 로컬 상태 변경 시 부모에게 알림
watch(
  filters,
  (newFilters) => {
    emit('update:modelValue', { ...newFilters })
  },
  { deep: true },
)
</script>

<style scoped>
.advanced-filters {
  margin-bottom: 1.5rem;
}
</style>

