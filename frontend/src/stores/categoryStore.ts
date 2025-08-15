import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiClient, API_ENDPOINTS } from '@/config/api'
import type { Category, CreateCategoryRequest, ApiResponse, LoadingState } from '@/types'

export const useCategoryStore = defineStore('categoryStore', () => {
  const categories = ref<Category[]>([])
  const loading = ref<LoadingState>({ isLoading: false, error: null })

  // 루트 카테고리만 필터링
  const rootCategories = computed(() => {
    return categories.value.filter((category) => !category.parentId)
  })

  // 활성 카테고리만 필터링
  const activeCategories = computed(() => {
    return categories.value.filter((category) => category.isActive)
  })

  // ID로 카테고리 찾기
  const categoryById = computed(() => {
    return (id: number) => categories.value.find((category) => category.id === id)
  })

  // 카테고리 목록 조회
  async function fetchCategories() {
    loading.value = { isLoading: true, error: null }

    try {
      const response = await apiClient.get<ApiResponse<Category[]>>(API_ENDPOINTS.CATEGORIES)

      if (response.data.success) {
        categories.value = response.data.data
      } else {
        throw new Error(response.data.message || '카테고리 목록 조회에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '카테고리 목록 조회에 실패했습니다.'
      loading.value = { isLoading: false, error: errorMessage }
      throw error
    } finally {
      loading.value = { isLoading: false, error: null }
    }
  }

  // 카테고리 생성
  async function createCategory(categoryData: CreateCategoryRequest) {
    loading.value = { isLoading: true, error: null }

    try {
      const response = await apiClient.post<ApiResponse<Category>>(
        API_ENDPOINTS.CATEGORIES,
        categoryData,
      )

      if (response.data.success) {
        // 새로 생성된 카테고리를 목록에 추가
        categories.value.push(response.data.data)
        return response.data.data
      } else {
        throw new Error(response.data.message || '카테고리 생성에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '카테고리 생성에 실패했습니다.'
      loading.value = { isLoading: false, error: errorMessage }
      throw error
    } finally {
      loading.value = { isLoading: false, error: null }
    }
  }

  // 카테고리 수정
  async function updateCategory(id: number, categoryData: Partial<CreateCategoryRequest>) {
    loading.value = { isLoading: true, error: null }

    try {
      const response = await apiClient.put<ApiResponse<Category>>(
        API_ENDPOINTS.CATEGORY_BY_ID(id),
        categoryData,
      )

      if (response.data.success) {
        // 목록에서 해당 카테고리 업데이트
        const index = categories.value.findIndex((category) => category.id === id)
        if (index !== -1) {
          categories.value[index] = response.data.data
        }
        return response.data.data
      } else {
        throw new Error(response.data.message || '카테고리 수정에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '카테고리 수정에 실패했습니다.'
      loading.value = { isLoading: false, error: errorMessage }
      throw error
    } finally {
      loading.value = { isLoading: false, error: null }
    }
  }

  // 카테고리 삭제
  async function deleteCategory(id: number) {
    loading.value = { isLoading: true, error: null }

    try {
      const response = await apiClient.delete<ApiResponse<void>>(API_ENDPOINTS.CATEGORY_BY_ID(id))

      if (response.data.success) {
        // 목록에서 해당 카테고리 제거
        categories.value = categories.value.filter((category) => category.id !== id)
      } else {
        throw new Error(response.data.message || '카테고리 삭제에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '카테고리 삭제에 실패했습니다.'
      loading.value = { isLoading: false, error: errorMessage }
      throw error
    } finally {
      loading.value = { isLoading: false, error: null }
    }
  }

  // 카테고리 순서 변경
  async function reorderCategory(
    categoryId: number,
    newParentId: number | undefined,
    newOrderIndex: number,
  ) {
    loading.value = { isLoading: true, error: null }

    try {
      const response = await apiClient.put<ApiResponse<Category>>(
        `${API_ENDPOINTS.CATEGORY_BY_ID(categoryId)}/reorder`,
        null,
        {
          params: {
            newParentId: newParentId || 0, // undefined인 경우 0으로 전송
            newOrderIndex,
          },
        },
      )

      if (response.data.success) {
        // 목록에서 해당 카테고리 업데이트
        const index = categories.value.findIndex((category) => category.id === categoryId)
        if (index !== -1) {
          categories.value[index] = response.data.data
        }
        return response.data.data
      } else {
        throw new Error(response.data.message || '카테고리 순서 변경에 실패했습니다.')
      }
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || error.message || '카테고리 순서 변경에 실패했습니다.'
      loading.value = { isLoading: false, error: errorMessage }
      throw error
    } finally {
      loading.value = { isLoading: false, error: null }
    }
  }

  // 에러 초기화
  function clearError() {
    loading.value.error = null
  }

  return {
    categories,
    loading,
    rootCategories,
    activeCategories,
    categoryById,
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    reorderCategory,
    clearError,
  }
})
