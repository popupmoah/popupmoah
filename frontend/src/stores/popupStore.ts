import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiClient, API_ENDPOINTS } from '@/config/api'
import type {
  PopupStore,
  CreatePopupStoreRequest,
  UpdatePopupStoreRequest,
  ApiResponse,
  PaginatedResponse,
  SearchParams,
  LoadingState,
  FileUploadResponse,
} from '@/types'

export const usePopupStore = defineStore('popupStore', () => {
  // State
  const stores = ref<PopupStore[]>([])
  const currentStore = ref<PopupStore | null>(null)
  const loading = ref<LoadingState>({
    isLoading: false,
    error: null,
  })

  // 페이지네이션 상태
  const pagination = ref({
    currentPage: 1,
    totalPages: 1,
    totalElements: 0,
    size: 12,
    first: true,
    last: false,
  })

  // Computed
  const activeStores = computed(() => stores.value.filter((store) => store.status === '진행중'))

  const storesByCategory = computed(
    () => (categoryId: number) => stores.value.filter((store) => store.categoryId === categoryId),
  )

  const topRatedStores = computed(() =>
    stores.value.sort((a, b) => b.rating - a.rating).slice(0, 5),
  )

  const storesByStatus = computed(
    () => (status: string) => stores.value.filter((store) => store.status === status),
  )

  // Actions
  async function fetchStores(params?: SearchParams & { page?: number; size?: number }) {
    loading.value.isLoading = true
    loading.value.error = null

    try {
      const searchParams = {
        ...params,
        page: params?.page || pagination.value.currentPage - 1, // Spring Boot는 0-based pagination
        size: params?.size || pagination.value.size,
      }

      const response = await apiClient.get<ApiResponse<PaginatedResponse<PopupStore>>>(
        API_ENDPOINTS.POPUPSTORES,
        { params: searchParams },
      )

      const paginatedData = response.data.data
      stores.value = paginatedData.content

      // 페이지네이션 정보 업데이트
      pagination.value = {
        currentPage: paginatedData.currentPage + 1, // 1-based로 변환
        totalPages: paginatedData.totalPages,
        totalElements: paginatedData.totalElements,
        size: paginatedData.size,
        first: paginatedData.first,
        last: paginatedData.last,
      }
    } catch (error: any) {
      loading.value.error =
        error.response?.data?.message || '팝업스토어 목록을 불러오는데 실패했습니다.'
      console.error('Error fetching stores:', error)
    } finally {
      loading.value.isLoading = false
    }
  }

  async function fetchStoreById(id: number) {
    loading.value.isLoading = true
    loading.value.error = null

    try {
      const response = await apiClient.get<ApiResponse<PopupStore>>(
        API_ENDPOINTS.POPUPSTORE_BY_ID(id),
      )
      currentStore.value = response.data.data
      return response.data.data
    } catch (error: any) {
      loading.value.error =
        error.response?.data?.message || '팝업스토어 정보를 불러오는데 실패했습니다.'
      console.error('Error fetching store:', error)
      throw error
    } finally {
      loading.value.isLoading = false
    }
  }

  async function createStore(storeData: CreatePopupStoreRequest) {
    loading.value.isLoading = true
    loading.value.error = null

    try {
      const response = await apiClient.post<ApiResponse<PopupStore>>(
        API_ENDPOINTS.POPUPSTORES,
        storeData,
      )
      const newStore = response.data.data
      stores.value.push(newStore)
      return newStore
    } catch (error: any) {
      loading.value.error = error.response?.data?.message || '팝업스토어 생성에 실패했습니다.'
      console.error('Error creating store:', error)
      throw error
    } finally {
      loading.value.isLoading = false
    }
  }

  async function updateStore(id: number, storeData: UpdatePopupStoreRequest) {
    loading.value.isLoading = true
    loading.value.error = null

    try {
      const response = await apiClient.put<ApiResponse<PopupStore>>(
        API_ENDPOINTS.POPUPSTORE_BY_ID(id),
        storeData,
      )
      const updatedStore = response.data.data

      // 목록에서 업데이트
      const index = stores.value.findIndex((store) => store.id === id)
      if (index !== -1) {
        stores.value[index] = updatedStore
      }

      // 현재 선택된 스토어가 업데이트된 스토어라면 업데이트
      if (currentStore.value?.id === id) {
        currentStore.value = updatedStore
      }

      return updatedStore
    } catch (error: any) {
      loading.value.error = error.response?.data?.message || '팝업스토어 수정에 실패했습니다.'
      console.error('Error updating store:', error)
      throw error
    } finally {
      loading.value.isLoading = false
    }
  }

  async function deleteStore(id: number) {
    loading.value.isLoading = true
    loading.value.error = null

    try {
      await apiClient.delete(API_ENDPOINTS.POPUPSTORE_BY_ID(id))

      // 목록에서 제거
      stores.value = stores.value.filter((store) => store.id !== id)

      // 현재 선택된 스토어가 삭제된 스토어라면 초기화
      if (currentStore.value?.id === id) {
        currentStore.value = null
      }
    } catch (error: any) {
      loading.value.error = error.response?.data?.message || '팝업스토어 삭제에 실패했습니다.'
      console.error('Error deleting store:', error)
      throw error
    } finally {
      loading.value.isLoading = false
    }
  }

  // 이미지 업로드 기능
  async function uploadImage(file: File): Promise<FileUploadResponse> {
    const formData = new FormData()
    formData.append('file', file)

    try {
      const response = await apiClient.post<ApiResponse<FileUploadResponse>>(
        API_ENDPOINTS.FILE_UPLOAD,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        },
      )
      return response.data.data
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || '이미지 업로드에 실패했습니다.'
      console.error('Error uploading image:', error)
      throw new Error(errorMessage)
    }
  }

  // 여러 이미지 업로드
  async function uploadImages(files: File[]): Promise<FileUploadResponse[]> {
    const uploadPromises = files.map((file) => uploadImage(file))
    return Promise.all(uploadPromises)
  }

  // 검색 기능
  async function searchStores(searchParams: SearchParams) {
    return await fetchStores(searchParams)
  }

  // 페이지 변경
  async function changePage(page: number) {
    if (page >= 1 && page <= pagination.value.totalPages) {
      await fetchStores({ page })
    }
  }

  // 페이지 크기 변경
  async function changePageSize(size: number) {
    pagination.value.size = size
    pagination.value.currentPage = 1
    await fetchStores({ size })
  }

  // 초기화
  function clearError() {
    loading.value.error = null
  }

  function clearCurrentStore() {
    currentStore.value = null
  }

  return {
    // State
    stores,
    currentStore,
    loading,
    pagination,

    // Computed
    activeStores,
    storesByCategory,
    topRatedStores,
    storesByStatus,

    // Actions
    fetchStores,
    fetchStoreById,
    createStore,
    updateStore,
    deleteStore,
    uploadImage,
    uploadImages,
    searchStores,
    changePage,
    changePageSize,
    clearError,
    clearCurrentStore,
  }
})
