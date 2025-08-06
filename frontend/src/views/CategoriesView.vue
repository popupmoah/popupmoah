<template>
  <q-page class="q-pa-md">
    <div class="container-responsive">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">카테고리 관리</h1>
          <p class="text-gray-600 mt-2">팝업스토어 카테고리를 관리하세요.</p>
        </div>
        <q-btn color="primary" icon="add" label="새 카테고리 추가" @click="showAddDialog = true" />
      </div>

      <!-- Categories Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <q-card
          v-for="category in categories"
          :key="category.id"
          class="category-card hover:shadow-lg transition-shadow duration-200"
        >
          <q-card-section>
            <div class="flex justify-between items-start mb-4">
              <div>
                <h3 class="text-lg font-semibold text-gray-900">{{ category.name }}</h3>
                <p v-if="category.description" class="text-gray-600 text-sm mt-1">
                  {{ category.description }}
                </p>
              </div>
              <div class="flex space-x-1">
                <q-btn
                  flat
                  round
                  size="sm"
                  icon="edit"
                  color="primary"
                  @click="editCategory(category)"
                />
                <q-btn
                  flat
                  round
                  size="sm"
                  icon="delete"
                  color="negative"
                  @click="confirmDeleteCategory(category)"
                />
              </div>
            </div>

            <div class="space-y-2">
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">레벨:</span>
                <span class="font-medium">{{ category.level }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">순서:</span>
                <span class="font-medium">{{ category.orderIndex }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">상태:</span>
                <q-badge
                  :color="category.isActive ? 'positive' : 'grey'"
                  :label="category.isActive ? '활성' : '비활성'"
                  size="sm"
                />
              </div>
              <div v-if="category.children && category.children.length > 0" class="mt-3">
                <p class="text-sm text-gray-600 mb-2">하위 카테고리:</p>
                <div class="flex flex-wrap gap-1">
                  <q-chip
                    v-for="child in category.children"
                    :key="child.id"
                    :label="child.name"
                    size="sm"
                    color="primary"
                    outline
                  />
                </div>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>

      <!-- Empty State -->
      <div v-if="categories.length === 0" class="text-center py-12">
        <q-icon name="category" size="4rem" color="grey-4" />
        <h3 class="text-xl font-semibold text-gray-600 mt-4">카테고리가 없습니다</h3>
        <p class="text-gray-500 mt-2">새로운 카테고리를 추가해보세요.</p>
      </div>
    </div>

    <!-- Add/Edit Category Dialog -->
    <q-dialog v-model="showAddDialog" persistent>
      <q-card style="min-width: 400px">
        <q-card-section>
          <div class="text-h6">{{ editingCategory ? '카테고리 수정' : '새 카테고리 추가' }}</div>
        </q-card-section>

        <q-card-section>
          <q-form @submit="submitCategory" class="q-gutter-md">
            <q-input
              v-model="categoryForm.name"
              label="카테고리명 *"
              outlined
              :rules="[(val) => !!val || '카테고리명을 입력해주세요']"
            />

            <q-input
              v-model="categoryForm.description"
              label="설명"
              type="textarea"
              outlined
              rows="3"
            />

            <q-select
              v-model="categoryForm.parentId"
              :options="parentCategoryOptions"
              label="상위 카테고리"
              outlined
              clearable
              emit-value
              map-options
            />

            <q-input
              v-model.number="categoryForm.orderIndex"
              label="순서"
              type="number"
              outlined
              min="0"
            />

            <q-toggle v-model="categoryForm.isActive" label="활성 상태" color="primary" />
          </q-form>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="취소" color="primary" v-close-popup @click="resetForm" />
          <q-btn
            flat
            :label="editingCategory ? '수정' : '추가'"
            color="primary"
            @click="submitCategory"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- Delete Confirmation Dialog -->
    <q-dialog v-model="showDeleteDialog" persistent>
      <q-card>
        <q-card-section>
          <div class="text-h6">카테고리 삭제</div>
        </q-card-section>

        <q-card-section>
          <p>정말로 "{{ categoryToDelete?.name }}" 카테고리를 삭제하시겠습니까?</p>
          <p class="text-red-600 text-sm mt-2">하위 카테고리가 있는 경우 함께 삭제됩니다.</p>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="취소" color="primary" v-close-popup />
          <q-btn flat label="삭제" color="negative" @click="deleteCategory" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { Category, CreateCategoryRequest } from '@/types'

// Mock data for now - will be replaced with actual API calls
const categories = ref<Category[]>([
  {
    id: 1,
    name: '패션',
    description: '의류, 신발, 액세서리 등 패션 관련 팝업스토어',
    parentId: undefined,
    level: 1,
    orderIndex: 1,
    isActive: true,
    children: [
      {
        id: 5,
        name: '여성의류',
        description: '여성 의류 전용 팝업스토어',
        parentId: 1,
        level: 2,
        orderIndex: 1,
        isActive: true,
      },
      {
        id: 6,
        name: '남성의류',
        description: '남성 의류 전용 팝업스토어',
        parentId: 1,
        level: 2,
        orderIndex: 2,
        isActive: true,
      },
    ],
  },
  {
    id: 2,
    name: '식품',
    description: '음식, 음료, 간식 등 식품 관련 팝업스토어',
    parentId: undefined,
    level: 1,
    orderIndex: 2,
    isActive: true,
    children: [],
  },
  {
    id: 3,
    name: '뷰티',
    description: '화장품, 향수, 뷰티 관련 팝업스토어',
    parentId: undefined,
    level: 1,
    orderIndex: 3,
    isActive: true,
    children: [],
  },
  {
    id: 4,
    name: '라이프스타일',
    description: '홈, 데코, 라이프스타일 관련 팝업스토어',
    parentId: undefined,
    level: 1,
    orderIndex: 4,
    isActive: true,
    children: [],
  },
])

const showAddDialog = ref(false)
const showDeleteDialog = ref(false)
const editingCategory = ref<Category | null>(null)
const categoryToDelete = ref<Category | null>(null)

const categoryForm = ref<Partial<CreateCategoryRequest>>({
  name: '',
  description: '',
  parentId: undefined,
  orderIndex: 0,
  isActive: true,
})

const parentCategoryOptions = computed(() => {
  return categories.value
    .filter((cat) => cat.level === 1) // Only top-level categories can be parents
    .map((cat) => ({
      label: cat.name,
      value: cat.id,
    }))
})

const editCategory = (category: Category) => {
  editingCategory.value = category
  categoryForm.value = {
    name: category.name,
    description: category.description,
    parentId: category.parentId,
    orderIndex: category.orderIndex,
    isActive: category.isActive,
  }
  showAddDialog.value = true
}

const confirmDeleteCategory = (category: Category) => {
  categoryToDelete.value = category
  showDeleteDialog.value = true
}

const submitCategory = () => {
  if (editingCategory.value) {
    // Update existing category
    const index = categories.value.findIndex((cat) => cat.id === editingCategory.value!.id)
    if (index !== -1) {
      categories.value[index] = {
        ...categories.value[index],
        ...categoryForm.value,
      }
    }
  } else {
    // Add new category
    const newCategory: Category = {
      id: Math.max(...categories.value.map((cat) => cat.id)) + 1,
      name: categoryForm.value.name!,
      description: categoryForm.value.description,
      parentId: categoryForm.value.parentId,
      level: categoryForm.value.parentId ? 2 : 1,
      orderIndex: categoryForm.value.orderIndex || 0,
      isActive: categoryForm.value.isActive ?? true,
      children: [],
    }
    categories.value.push(newCategory)
  }

  resetForm()
  showAddDialog.value = false
}

const deleteCategory = () => {
  if (categoryToDelete.value) {
    // Remove category and its children
    categories.value = categories.value.filter(
      (cat) => cat.id !== categoryToDelete.value!.id && cat.parentId !== categoryToDelete.value!.id,
    )
    categoryToDelete.value = null
    showDeleteDialog.value = false
  }
}

const resetForm = () => {
  categoryForm.value = {
    name: '',
    description: '',
    parentId: undefined,
    orderIndex: 0,
    isActive: true,
  }
  editingCategory.value = null
}

onMounted(() => {
  // Load categories from API
  // TODO: Replace with actual API call
})
</script>

<style scoped>
.category-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.category-card .q-card__section {
  flex: 1;
}
</style>
