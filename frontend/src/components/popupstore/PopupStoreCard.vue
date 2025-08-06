<template>
  <q-card class="popup-store-card cursor-pointer hover:shadow-lg transition-shadow duration-200">
    <q-img
      :src="store.images?.[0] || '/images/default-store.jpg'"
      :ratio="16 / 9"
      class="rounded-t-lg"
    >
      <div class="absolute-top-right q-pa-sm">
        <q-badge :color="getStatusColor(store.status)" :label="store.status" class="text-caption" />
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
        <div class="flex items-center">
          <q-icon name="location_on" size="1em" color="primary" class="mr-1" />
          <span class="text-gray-700 text-sm">{{ store.location }}</span>
        </div>

        <div class="flex items-center">
          <q-icon name="star" size="1em" color="amber" class="mr-1" />
          <span class="text-gray-700 text-sm font-medium">{{ store.rating }}</span>
        </div>
      </div>

      <div class="flex items-center justify-between">
        <div class="text-gray-500 text-xs">
          {{ store.period }}
        </div>

        <q-chip :label="store.categoryName" size="sm" color="primary" outline />
      </div>
    </q-card-section>

    <q-card-actions class="q-pa-md pt-0">
      <q-btn
        flat
        color="primary"
        label="상세보기"
        @click="$emit('view', store.id)"
        class="full-width"
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
</style>
