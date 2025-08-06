<template>
  <div v-if="loading" class="flex justify-center items-center p-4">
    <q-spinner-dots size="2em" color="primary" />
    <span v-if="message" class="ml-2 text-gray-600">{{ message }}</span>
  </div>
</template>

<script setup lang="ts">
interface Props {
  loading: boolean
  message?: string
}

defineProps<Props>()
</script>
