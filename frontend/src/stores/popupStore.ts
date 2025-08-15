import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiClient, API_ENDPOINTS } from '@/config/api'
import type {
  PopupStore,
  CreatePopupStoreRequest,
  UpdatePopupStoreRequest,
  ApiResponse,
  LoadingState,
} from '@/types'

export const usePopupStore = defineStore('popupStore', () => {
  const stores = ref<PopupStore[]>([])
  const loading = ref<LoadingState>({ isLoading: false, error: null })

  // 페이지네이션 상태
  const pagination = ref({
    currentPage: 1,
    size: 12,
    totalElements: 0,
    totalPages: 0,
  })

  // 팝업스토어 목록 조회
  async function fetchStores(params?: {
    search?: string
    categoryId?: number
    startDate?: string
    endDate?: string
    page?: number
    size?: number
  }) {
    loading.value = { isLoading: true, error: null }

    try {
      const response = await apiClient.get<
        ApiResponse<{
          content: PopupStore[]
          totalElements: number
          totalPages: number
          number: number
          size: number
        }>
      >(API_ENDPOINTS.POPUPSTORES, {
        params: {
          search: params?.search,
          categoryId: params?.categoryId,
          startDate: params?.startDate,
          endDate: params?.endDate,
          page: params?.page || pagination.value.currentPage - 1, // Spring Boot는 0-based pagination
          size: params?.size || pagination.value.size,
        },
      })

      if (response.data.success) {
        stores.value = response.data.data.content
        pagination.value = {
          currentPage: response.data.data.number + 1, // 1-based로 변환
          size: response.data.data.size,
          totalElements: response.data.data.totalElements,
          totalPages: response.data.data.totalPages,
        }
      } else {
        throw new Error(response.data.message || '팝업스토어 목록 조회에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '팝업스토어 목록 조회에 실패했습니다.'
      loading.value = { isLoading: false, error: errorMessage }
      throw error
    } finally {
      loading.value = { isLoading: false, error: null }
    }
  }

  // 팝업스토어 상세 조회
  async function fetchStore(id: number) {
    loading.value = { isLoading: true, error: null }

    try {
      const response = await apiClient.get<ApiResponse<PopupStore>>(
        API_ENDPOINTS.POPUPSTORE_BY_ID(id),
      )

      if (response.data.success) {
        return response.data.data
      } else {
        throw new Error(response.data.message || '팝업스토어 조회에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '팝업스토어 조회에 실패했습니다.'
      loading.value = { isLoading: false, error: errorMessage }
      throw error
    } finally {
      loading.value = { isLoading: false, error: null }
    }
  }

  // 팝업스토어 생성
  async function createStore(storeData: CreatePopupStoreRequest) {
    loading.value = { isLoading: true, error: null }

    try {
      const response = await apiClient.post<ApiResponse<PopupStore>>(
        API_ENDPOINTS.POPUPSTORES,
        storeData,
      )

      if (response.data.success) {
        // 새로 생성된 스토어를 목록에 추가
        stores.value.unshift(response.data.data)
        return response.data.data
      } else {
        throw new Error(response.data.message || '팝업스토어 생성에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '팝업스토어 생성에 실패했습니다.'
      loading.value = { isLoading: false, error: errorMessage }
      throw error
    } finally {
      loading.value = { isLoading: false, error: null }
    }
  }

  // 팝업스토어 수정
  async function updateStore(id: number, storeData: UpdatePopupStoreRequest) {
    loading.value = { isLoading: true, error: null }

    try {
      const response = await apiClient.put<ApiResponse<PopupStore>>(
        API_ENDPOINTS.POPUPSTORE_BY_ID(id),
        storeData,
      )

      if (response.data.success) {
        // 목록에서 해당 스토어 업데이트
        const index = stores.value.findIndex((store) => store.id === id)
        if (index !== -1) {
          stores.value[index] = response.data.data
        }
        return response.data.data
      } else {
        throw new Error(response.data.message || '팝업스토어 수정에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '팝업스토어 수정에 실패했습니다.'
      loading.value = { isLoading: false, error: errorMessage }
      throw error
    } finally {
      loading.value = { isLoading: false, error: null }
    }
  }

  // 팝업스토어 삭제
  async function deleteStore(id: number) {
    loading.value = { isLoading: true, error: null }

    try {
      const response = await apiClient.delete<ApiResponse<void>>(API_ENDPOINTS.POPUPSTORE_BY_ID(id))

      if (response.data.success) {
        // 목록에서 해당 스토어 제거
        stores.value = stores.value.filter((store) => store.id !== id)
      } else {
        throw new Error(response.data.message || '팝업스토어 삭제에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '팝업스토어 삭제에 실패했습니다.'
      loading.value = { isLoading: false, error: errorMessage }
      throw error
    } finally {
      loading.value = { isLoading: false, error: null }
    }
  }

  // 이미지 업로드
  async function uploadImage(
    file: File,
  ): Promise<{ fileId: string; fileName: string; fileUrl: string }> {
    const formData = new FormData()
    formData.append('file', file)

    try {
      const response = await apiClient.post<
        ApiResponse<{ fileId: string; fileName: string; fileUrl: string }>
      >(API_ENDPOINTS.FILE_UPLOAD, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })

      if (response.data.success) {
        return response.data.data
      } else {
        throw new Error(response.data.message || '이미지 업로드에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '이미지 업로드에 실패했습니다.'
      throw new Error(errorMessage)
    }
  }

  // 다중 이미지 업로드
  async function uploadImages(
    files: File[],
  ): Promise<{ fileId: string; fileName: string; fileUrl: string }[]> {
    const uploadPromises = files.map((file) => uploadImage(file))
    return Promise.all(uploadPromises)
  }

  // 페이지 변경
  async function changePage(page: number) {
    pagination.value.currentPage = page
    await fetchStores()
  }

  // 페이지 크기 변경
  async function changePageSize(size: number) {
    pagination.value.size = size
    pagination.value.currentPage = 1 // 첫 페이지로 리셋
    await fetchStores()
  }

  // 에러 초기화
  function clearError() {
    loading.value.error = null
  }

  return {
    stores,
    loading,
    pagination,
    fetchStores,
    fetchStore,
    createStore,
    updateStore,
    deleteStore,
    uploadImage,
    uploadImages,
    changePage,
    changePageSize,
    clearError,
  }
})
