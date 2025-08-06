<template>
  <q-header elevated class="bg-white text-gray-900">
    <q-toolbar>
      <q-toolbar-title class="flex items-center">
        <q-icon name="store" size="2rem" color="primary" class="mr-2" />
        <span class="text-xl font-bold">PopupMoah</span>
      </q-toolbar-title>

      <!-- Desktop Navigation -->
      <div class="hidden md:flex items-center space-x-4">
        <q-btn
          flat
          :label="$route.name === 'home' ? '홈' : '홈'"
          :color="$route.name === 'home' ? 'primary' : 'grey-7'"
          @click="$router.push('/')"
        />
        <q-btn
          flat
          :label="$route.name === 'popupstores' ? '팝업스토어' : '팝업스토어'"
          :color="$route.name === 'popupstores' ? 'primary' : 'grey-7'"
          @click="$router.push('/popupstores')"
        />
        <q-btn
          flat
          :label="$route.name === 'categories' ? '카테고리' : '카테고리'"
          :color="$route.name === 'categories' ? 'primary' : 'grey-7'"
          @click="$router.push('/categories')"
        />
        <q-btn
          flat
          :label="$route.name === 'about' ? '소개' : '소개'"
          :color="$route.name === 'about' ? 'primary' : 'grey-7'"
          @click="$router.push('/about')"
        />
      </div>

      <!-- User Actions -->
      <div class="flex items-center space-x-2">
        <q-btn flat round icon="search" color="grey-7" />
        <q-btn flat round icon="notifications" color="grey-7" />
        <q-btn flat round icon="account_circle" color="grey-7" />
      </div>

      <!-- Mobile Menu Button -->
      <q-btn
        flat
        round
        icon="menu"
        color="grey-7"
        class="md:hidden"
        @click="showMobileMenu = true"
      />
    </q-toolbar>
  </q-header>

  <!-- Mobile Menu -->
  <q-drawer v-model="showMobileMenu" side="right" overlay elevated class="bg-white">
    <q-list class="q-pa-md">
      <q-item clickable v-ripple :active="$route.name === 'home'" @click="navigateTo('/')">
        <q-item-section avatar>
          <q-icon name="home" :color="$route.name === 'home' ? 'primary' : 'grey-7'" />
        </q-item-section>
        <q-item-section>
          <q-item-label>홈</q-item-label>
        </q-item-section>
      </q-item>

      <q-item
        clickable
        v-ripple
        :active="$route.name === 'popupstores'"
        @click="navigateTo('/popupstores')"
      >
        <q-item-section avatar>
          <q-icon name="store" :color="$route.name === 'popupstores' ? 'primary' : 'grey-7'" />
        </q-item-section>
        <q-item-section>
          <q-item-label>팝업스토어</q-item-label>
        </q-item-section>
      </q-item>

      <q-item
        clickable
        v-ripple
        :active="$route.name === 'categories'"
        @click="navigateTo('/categories')"
      >
        <q-item-section avatar>
          <q-icon name="category" :color="$route.name === 'categories' ? 'primary' : 'grey-7'" />
        </q-item-section>
        <q-item-section>
          <q-item-label>카테고리</q-item-label>
        </q-item-section>
      </q-item>

      <q-item clickable v-ripple :active="$route.name === 'about'" @click="navigateTo('/about')">
        <q-item-section avatar>
          <q-icon name="info" :color="$route.name === 'about' ? 'primary' : 'grey-7'" />
        </q-item-section>
        <q-item-section>
          <q-item-label>소개</q-item-label>
        </q-item-section>
      </q-item>

      <q-separator class="q-my-md" />

      <q-item clickable v-ripple>
        <q-item-section avatar>
          <q-icon name="search" color="grey-7" />
        </q-item-section>
        <q-item-section>
          <q-item-label>검색</q-item-label>
        </q-item-section>
      </q-item>

      <q-item clickable v-ripple>
        <q-item-section avatar>
          <q-icon name="notifications" color="grey-7" />
        </q-item-section>
        <q-item-section>
          <q-item-label>알림</q-item-label>
        </q-item-section>
      </q-item>

      <q-item clickable v-ripple>
        <q-item-section avatar>
          <q-icon name="account_circle" color="grey-7" />
        </q-item-section>
        <q-item-section>
          <q-item-label>프로필</q-item-label>
        </q-item-section>
      </q-item>
    </q-list>
  </q-drawer>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const showMobileMenu = ref(false)

const navigateTo = (path: string) => {
  router.push(path)
  showMobileMenu.value = false
}
</script>
