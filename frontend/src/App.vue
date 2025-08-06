<script setup lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
</script>

<template>
  <div id="app">
    <q-layout view="hHh lpR fFf">
      <!-- Header -->
      <AppHeader />

      <!-- Main Content -->
      <q-page-container>
        <router-view />
      </q-page-container>
    </q-layout>
  </div>
</template>

<style>
#app {
  font-family:
    'Inter',
    -apple-system,
    BlinkMacSystemFont,
    'Segoe UI',
    Roboto,
    sans-serif;
}

/* Quasar와 Tailwind CSS 통합 */
.q-layout {
  min-height: 100vh;
}

.q-page-container {
  background-color: #f8fafc;
}
</style>
