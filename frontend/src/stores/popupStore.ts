import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface PopupStore {
  id: number
  name: string
  description: string
  location: string
  period: string
  rating: number
  category: string
  status: string
  latitude?: number
  longitude?: number
  images?: string[]
}

export const usePopupStore = defineStore('popupStore', () => {
  const stores = ref<PopupStore[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Computed
  const activeStores = computed(() => 
    stores.value.filter(store => store.status === '진행중')
  )

  const storesByCategory = computed(() => (category: string) =>
    stores.value.filter(store => store.category === category)
  )

  const topRatedStores = computed(() =>
    stores.value
      .sort((a, b) => b.rating - a.rating)
      .slice(0, 5)
  )

  // Actions
  async function fetchStores() {
    loading.value = true
    error.value = null
    try {
      // 실제 API 호출 로직 (예: axios.get('/api/popupstores'))
      // 현재는 더미 데이터 사용
      const dummyData: PopupStore[] = [
        {
          id: 1,
          name: '성수동 팝업스토어 1',
          description: '힙한 성수동의 최신 팝업스토어입니다.',
          location: '서울 성동구 성수동',
          period: '2023.10.01 - 2023.10.31',
          rating: 4.5,
          category: '패션',
          status: '진행중',
          images: ['https://via.placeholder.com/300x200?text=PopupStore1']
        },
        {
          id: 2,
          name: '강남역 팝업스토어 2',
          description: '강남역 근처에서 열리는 특별한 팝업스토어.',
          location: '서울 강남구 역삼동',
          period: '2023.11.01 - 2023.11.15',
          rating: 4.8,
          category: '식품',
          status: '예정',
          images: ['https://via.placeholder.com/300x200?text=PopupStore2']
        },
        {
          id: 3,
          name: '홍대 팝업스토어 3',
          description: '젊음의 거리 홍대에서 만나는 새로운 경험.',
          location: '서울 마포구 서교동',
          period: '2023.09.15 - 2023.10.10',
          rating: 4.2,
          category: '뷰티',
          status: '종료',
          images: ['https://via.placeholder.com/300x200?text=PopupStore3']
        },
        {
          id: 4,
          name: '이태원 팝업스토어 4',
          description: '다양한 문화가 공존하는 이태원의 팝업.',
          location: '서울 용산구 이태원동',
          period: '2023.10.20 - 2023.11.20',
          rating: 4.7,
          category: '라이프스타일',
          status: '진행중',
          images: ['https://via.placeholder.com/300x200?text=PopupStore4']
        },
        {
          id: 5,
          name: '명동 팝업스토어 5',
          description: '쇼핑의 중심 명동에서 만나는 특별한 브랜드.',
          location: '서울 중구 명동',
          period: '2023.11.05 - 2023.11.30',
          rating: 4.9,
          category: '패션',
          status: '예정',
          images: ['https://via.placeholder.com/300x200?text=PopupStore5']
        }
      ]
      stores.value = dummyData
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch popup stores'
    } finally {
      loading.value = false
    }
  }

  async function addStore(newStore: PopupStore) {
    loading.value = true
    error.value = null
    try {
      // 실제 API 호출 로직 (예: axios.post('/api/popupstores', newStore))
      // 현재는 클라이언트 측에서만 추가
      newStore.id = stores.value.length > 0 ? Math.max(...stores.value.map(s => s.id)) + 1 : 1
      stores.value.push(newStore)
    } catch (err: any) {
      error.value = err.message || 'Failed to add popup store'
    } finally {
      loading.value = false
    }
  }

  async function updateStore(id: number, updatedStore: Partial<PopupStore>) {
    loading.value = true
    error.value = null
    try {
      // 실제 API 호출 로직 (예: axios.put(`/api/popupstores/${id}`, updatedStore))
      const index = stores.value.findIndex(store => store.id === id)
      if (index !== -1) {
        stores.value[index] = { ...stores.value[index], ...updatedStore }
      } else {
        throw new Error('Store not found')
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to update popup store'
    } finally {
      loading.value = false
    }
  }

  async function deleteStore(id: number) {
    loading.value = true
    error.value = null
    try {
      // 실제 API 호출 로직 (예: axios.delete(`/api/popupstores/${id}`))
      stores.value = stores.value.filter(store => store.id !== id)
    } catch (err: any) {
      error.value = err.message || 'Failed to delete popup store'
    } finally {
      loading.value = false
    }
  }

  return {
    stores,
    loading,
    error,
    activeStores,
    storesByCategory,
    topRatedStores,
    fetchStores,
    addStore,
    updateStore,
    deleteStore
  }
}) 