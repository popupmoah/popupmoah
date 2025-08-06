<template>
  <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4 mb-4">
    <div class="flex items-center">
      <q-icon name="error" color="negative" size="1.5em" class="mr-2" />
      <div>
        <h3 class="text-red-800 font-medium">{{ title }}</h3>
        <p class="text-red-600 text-sm mt-1">{{ error }}</p>
      </div>
    </div>
    <div v-if="showRetry" class="mt-3">
      <q-btn color="negative" outline size="sm" @click="$emit('retry')" label="다시 시도" />
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  error: string | null
  title?: string
  showRetry?: boolean
}

interface Emits {
  retry: []
}

defineProps<Props>()
defineEmits<Emits>()
</script>
