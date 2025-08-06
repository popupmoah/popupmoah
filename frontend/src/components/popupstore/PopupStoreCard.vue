<template>
  <q-card
    class="popup-store-card cursor-pointer hover:shadow-lg transition-shadow duration-200"
    tabindex="0"
    role="article"
    :aria-label="`${store.name} 팝업스토어`"
    @click="$emit('view', store.id)"
    @keydown.enter="$emit('view', store.id)"
    @keydown.space.prevent="$emit('view', store.id)"
  >
    <q-img
      :src="store.images?.[0] || '/images/default-store.jpg'"
      :ratio="16 / 9"
      class="rounded-t-lg"
      :alt="`${store.name} 이미지`"
    >
      <div class="absolute-top-right q-pa-sm">
        <q-badge
          :color="getStatusColor(store.status)"
          :label="store.status"
          class="text-caption"
          :aria-label="`상태: ${store.status}`"
        />
      </div>
    </q-img>

    <q-card-section class="q-pa-md">
      <div class="text-h6 text-weight-medium text-gray-900 mb-2">
        {{ store.name }}
      </div>

      <p class="text-gray-600 text-body2 mb-3 line-clamp-2">
        {{ store.description }}
      </p>

      <div class="flex items-center justify-between mb-3">
        <div class="flex items-center" role="group" aria-label="위치 정보">
          <q-icon name="location_on" size="1em" color="primary" class="mr-1" aria-hidden="true" />
          <span class="text-gray-700 text-sm">{{ store.location }}</span>
        </div>

        <div class="flex items-center" role="group" aria-label="평점 정보">
          <q-icon name="star" size="1em" color="amber" class="mr-1" aria-hidden="true" />
          <span class="text-gray-700 text-sm font-medium" aria-label="평점 {{ store.rating }}점">{{
            store.rating
          }}</span>
        </div>
      </div>

      <div class="flex items-center justify-between">
        <div class="text-gray-500 text-xs" aria-label="운영 기간">
          {{ store.period }}
        </div>

        <q-chip
          :label="store.categoryName"
          size="sm"
          color="primary"
          outline
          :aria-label="`카테고리: ${store.categoryName}`"
        />
      </div>
    </q-card-section>

    <q-card-actions class="q-pa-md pt-0">
      <q-btn
        flat
        color="primary"
        label="상세보기"
        @click="$emit('view', store.id)"
        class="full-width"
        aria-label="팝업스토어 상세 정보 보기"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup lang="ts">
import type { PopupStore } from '@/types'

interface Props {
  store: PopupStore
}

interface Emits {
  view: [id: number]
}

defineProps<Props>()
defineEmits<Emits>()

function getStatusColor(status: string): string {
  switch (status) {
    case '진행중':
      return 'positive'
    case '예정':
      return 'warning'
    case '종료':
      return 'grey'
    default:
      return 'grey'
  }
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.popup-store-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.popup-store-card .q-card__section {
  flex: 1;
}

/* 키보드 포커스 스타일 */
.popup-store-card:focus {
  outline: 2px solid #1976d2;
  outline-offset: 2px;
}

/* 스크린 리더를 위한 숨김 텍스트 */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>
