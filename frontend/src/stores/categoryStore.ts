import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiClient, API_ENDPOINTS } from '@/config/api'
import type { Category, CreateCategoryRequest, ApiResponse, LoadingState } from '@/types'

export const useCategoryStore = defineStore('categoryStore', () => {
  // State
  const categories = ref<Category[]>([])
  const loading = ref<LoadingState>({
    isLoading: false,
    error: null,
  })

  // Computed
  const rootCategories = computed(() => {
    return categories.value
      .filter((cat) => !cat.parentId)
      .sort((a, b) => a.orderIndex - b.orderIndex)
  })

  const activeCategories = computed(() => {
    return categories.value.filter((cat) => cat.isActive)
  })

  const categoryById = computed(() => {
    return (id: number) => categories.value.find((cat) => cat.id === id)
  })

  // Actions
  async function fetchCategories() {
    loading.value.isLoading = true
    loading.value.error = null

    try {
      const response = await apiClient.get<ApiResponse<Category[]>>(API_ENDPOINTS.CATEGORIES)
      categories.value = response.data.data
    } catch (error: any) {
      loading.value.error =
        error.response?.data?.message || '카테고리 목록을 불러오는데 실패했습니다.'
      console.error('Error fetching categories:', error)
    } finally {
      loading.value.isLoading = false
    }
  }

  async function createCategory(categoryData: CreateCategoryRequest) {
    loading.value.isLoading = true
    loading.value.error = null

    try {
      const response = await apiClient.post<ApiResponse<Category>>(
        API_ENDPOINTS.CATEGORIES,
        categoryData,
      )
      const newCategory = response.data.data
      categories.value.push(newCategory)
      return newCategory
    } catch (error: any) {
      loading.value.error = error.response?.data?.message || '카테고리 생성에 실패했습니다.'
      console.error('Error creating category:', error)
      throw error
    } finally {
      loading.value.isLoading = false
    }
  }

  async function updateCategory(id: number, categoryData: Partial<CreateCategoryRequest>) {
    loading.value.isLoading = true
    loading.value.error = null

    try {
      const response = await apiClient.put<ApiResponse<Category>>(
        API_ENDPOINTS.CATEGORY_BY_ID(id),
        categoryData,
      )
      const updatedCategory = response.data.data

      // 목록에서 업데이트
      const index = categories.value.findIndex((cat) => cat.id === id)
      if (index !== -1) {
        categories.value[index] = updatedCategory
      }

      return updatedCategory
    } catch (error: any) {
      loading.value.error = error.response?.data?.message || '카테고리 수정에 실패했습니다.'
      console.error('Error updating category:', error)
      throw error
    } finally {
      loading.value.isLoading = false
    }
  }

  async function deleteCategory(id: number) {
    loading.value.isLoading = true
    loading.value.error = null

    try {
      await apiClient.delete(API_ENDPOINTS.CATEGORY_BY_ID(id))

      // 목록에서 제거 (하위 카테고리도 함께 제거)
      categories.value = categories.value.filter((cat) => cat.id !== id && cat.parentId !== id)
    } catch (error: any) {
      loading.value.error = error.response?.data?.message || '카테고리 삭제에 실패했습니다.'
      console.error('Error deleting category:', error)
      throw error
    } finally {
      loading.value.isLoading = false
    }
  }

  async function reorderCategory(
    categoryId: number,
    newParentId: number | undefined,
    newOrderIndex: number,
  ) {
    loading.value.isLoading = true
    loading.value.error = null

    try {
      const response = await apiClient.put<ApiResponse<Category>>(
        API_ENDPOINTS.CATEGORY_BY_ID(categoryId),
        {
          parentId: newParentId,
          orderIndex: newOrderIndex,
        },
      )
      const updatedCategory = response.data.data

      // 목록에서 업데이트
      const index = categories.value.findIndex((cat) => cat.id === categoryId)
      if (index !== -1) {
        categories.value[index] = updatedCategory
      }

      return updatedCategory
    } catch (error: any) {
      loading.value.error = error.response?.data?.message || '카테고리 순서 변경에 실패했습니다.'
      console.error('Error reordering category:', error)
      throw error
    } finally {
      loading.value.isLoading = false
    }
  }

  // 초기화
  function clearError() {
    loading.value.error = null
  }

  return {
    // State
    categories,
    loading,

    // Computed
    rootCategories,
    activeCategories,
    categoryById,

    // Actions
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    reorderCategory,
    clearError,
  }
})
