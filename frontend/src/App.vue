<script setup lang="ts">
import { ref } from 'vue'

const leftDrawerOpen = ref(false)
</script>

<template>
  <div id="app">
    <q-layout view="hHh lpR fFf">
      <!-- Header -->
      <q-header elevated class="bg-primary text-white">
        <q-toolbar>
          <q-toolbar-title class="text-h6">
            <router-link to="/" class="text-white no-underline">
              PopupMoah
            </router-link>
          </q-toolbar-title>
          
          <q-space />
          
          <q-btn flat round dense icon="search" />
          <q-btn flat round dense icon="notifications" />
          <q-btn flat round dense icon="account_circle" />
        </q-toolbar>
      </q-header>

      <!-- Navigation -->
      <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
        <q-list>
          <q-item-label header>메뉴</q-item-label>
          
          <q-item clickable v-ripple to="/" exact>
            <q-item-section avatar>
              <q-icon name="home" />
            </q-item-section>
            <q-item-section>홈</q-item-section>
          </q-item>
          
          <q-item clickable v-ripple to="/popupstores">
            <q-item-section avatar>
              <q-icon name="store" />
            </q-item-section>
            <q-item-section>팝업스토어</q-item-section>
          </q-item>
          
          <q-item clickable v-ripple to="/community">
            <q-item-section avatar>
              <q-icon name="forum" />
            </q-item-section>
            <q-item-section>커뮤니티</q-item-section>
          </q-item>
          
          <q-item clickable v-ripple to="/login">
            <q-item-section avatar>
              <q-icon name="login" />
            </q-item-section>
            <q-item-section>로그인</q-item-section>
          </q-item>
        </q-list>
      </q-drawer>

      <!-- Main Content -->
      <q-page-container>
        <router-view />
      </q-page-container>
    </q-layout>
  </div>
</template>

<style>
#app {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* Quasar와 Tailwind CSS 통합 */
.q-layout {
  min-height: 100vh;
}

.q-page-container {
  background-color: #f8fafc;
}
</style>
