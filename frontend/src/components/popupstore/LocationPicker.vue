<template>
  <div class="location-picker">
    <div class="mb-4">
      <h3 class="text-lg font-semibold text-gray-900 mb-2">위치 선택</h3>
      <p class="text-sm text-gray-600 mb-4">
        지도에서 위치를 클릭하거나 주소를 입력하여 좌표를 설정할 수 있습니다.
      </p>
    </div>

    <!-- 주소 검색 -->
    <div class="mb-4">
      <q-input
        v-model="searchAddress"
        label="주소 검색"
        outlined
        clearable
        @keyup.enter="searchLocation"
      >
        <template v-slot:prepend>
          <q-icon name="search" />
        </template>
        <template v-slot:append>
          <q-btn flat round icon="search" @click="searchLocation" :loading="searching" />
        </template>
      </q-input>
    </div>

    <!-- 지도 -->
    <div class="mb-4">
      <PopupStoreMap :height="300" :show-controls="true" :zoom="15" @map-click="handleMapClick" />
    </div>

    <!-- 좌표 입력 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <q-input
        v-model.number="latitude"
        label="위도"
        outlined
        type="number"
        step="0.000001"
        placeholder="37.5665"
        @update:model-value="updateCoordinates"
      />

      <q-input
        v-model.number="longitude"
        label="경도"
        outlined
        type="number"
        step="0.000001"
        placeholder="126.9780"
        @update:model-value="updateCoordinates"
      />
    </div>

    <!-- 현재 위치 버튼 -->
    <div class="mt-4">
      <q-btn
        color="secondary"
        icon="my_location"
        label="현재 위치 사용"
        @click="getCurrentLocation"
        :loading="gettingLocation"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useQuasar } from 'quasar'
import PopupStoreMap from './PopupStoreMap.vue'

interface Props {
  modelValue?: { latitude?: number; longitude?: number }
}

interface Emits {
  'update:modelValue': [value: { latitude?: number; longitude?: number }]
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const $q = useQuasar()

const searchAddress = ref('')
const searching = ref(false)
const gettingLocation = ref(false)

const latitude = ref<number | undefined>(props.modelValue?.latitude)
const longitude = ref<number | undefined>(props.modelValue?.longitude)

// 좌표 변경 시 부모에게 알림
const updateCoordinates = () => {
  emit('update:modelValue', {
    latitude: latitude.value,
    longitude: longitude.value,
  })
}

// 주소 검색
const searchLocation = async () => {
  if (!searchAddress.value.trim()) {
    $q.notify({
      type: 'warning',
      message: '검색할 주소를 입력해주세요.',
      position: 'top',
    })
    return
  }

  searching.value = true

  try {
    // Google Maps Geocoding API 사용
    const geocoder = new google.maps.Geocoder()

    geocoder.geocode({ address: searchAddress.value }, (results, status) => {
      searching.value = false

      if (status === 'OK' && results && results[0]) {
        const location = results[0].geometry.location
        latitude.value = location.lat()
        longitude.value = location.lng()
        updateCoordinates()

        $q.notify({
          type: 'positive',
          message: '위치가 설정되었습니다.',
          position: 'top',
        })
      } else {
        $q.notify({
          type: 'negative',
          message: '주소를 찾을 수 없습니다.',
          position: 'top',
        })
      }
    })
  } catch (error) {
    searching.value = false
    console.error('주소 검색 실패:', error)
    $q.notify({
      type: 'negative',
      message: '주소 검색에 실패했습니다.',
      position: 'top',
    })
  }
}

// 지도 클릭 처리
const handleMapClick = (event: google.maps.MapMouseEvent) => {
  if (event.latLng) {
    latitude.value = event.latLng.lat()
    longitude.value = event.latLng.lng()
    updateCoordinates()

    $q.notify({
      type: 'positive',
      message: '지도에서 위치가 선택되었습니다.',
      position: 'top',
    })
  }
}

// 현재 위치 가져오기
const getCurrentLocation = () => {
  if (!navigator.geolocation) {
    $q.notify({
      type: 'negative',
      message: '이 브라우저는 위치 정보를 지원하지 않습니다.',
      position: 'top',
    })
    return
  }

  gettingLocation.value = true

  navigator.geolocation.getCurrentPosition(
    (position) => {
      latitude.value = position.coords.latitude
      longitude.value = position.coords.longitude
      updateCoordinates()
      gettingLocation.value = false

      $q.notify({
        type: 'positive',
        message: '현재 위치가 설정되었습니다.',
        position: 'top',
      })
    },
    (error) => {
      gettingLocation.value = false
      console.error('위치 정보 가져오기 실패:', error)

      let message = '위치 정보를 가져올 수 없습니다.'
      switch (error.code) {
        case error.PERMISSION_DENIED:
          message = '위치 정보 접근이 거부되었습니다.'
          break
        case error.POSITION_UNAVAILABLE:
          message = '위치 정보를 사용할 수 없습니다.'
          break
        case error.TIMEOUT:
          message = '위치 정보 요청이 시간 초과되었습니다.'
          break
      }

      $q.notify({
        type: 'negative',
        message,
        position: 'top',
      })
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 60000,
    },
  )
}

// props 변경 시 좌표 업데이트
watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue) {
      latitude.value = newValue.latitude
      longitude.value = newValue.longitude
    }
  },
  { deep: true },
)
</script>

<style scoped>
.location-picker {
  width: 100%;
}
</style>

