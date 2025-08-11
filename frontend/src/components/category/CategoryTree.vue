<template>
  <div class="category-tree">
    <div class="tree-header mb-4">
      <h3 class="text-lg font-semibold text-gray-900">카테고리 구조</h3>
      <p class="text-sm text-gray-600">
        드래그하여 순서를 변경하거나 계층 구조를 조정할 수 있습니다.
      </p>
    </div>

    <div class="tree-container">
      <div
        v-for="category in sortedCategories"
        :key="category.id"
        class="tree-item"
        :class="{ 'is-dragging': draggedItem?.id === category.id }"
      >
        <CategoryTreeNode
          :category="category"
          :level="0"
          :dragged-item="draggedItem"
          :drop-target="dropTarget"
          @drag-start="handleDragStart"
          @drag-end="handleDragEnd"
          @drag-over="handleDragOver"
          @drop="handleDrop"
          @edit="handleEdit"
          @delete="handleDelete"
          @toggle="handleToggle"
        />
      </div>
    </div>

    <!-- 드래그 중일 때 표시할 드롭 영역 -->
    <div
      v-if="draggedItem && !dropTarget"
      class="drop-zone"
      @dragover.prevent
      @drop="handleDropToRoot"
    >
      <div class="drop-zone-content">
        <q-icon name="add" size="2rem" color="primary" />
        <p class="text-center mt-2">최상위 레벨로 이동</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { Category } from '@/types'
import CategoryTreeNode from './CategoryTreeNode.vue'

interface Props {
  categories: Category[]
}

interface Emits {
  edit: [category: Category]
  delete: [category: Category]
  reorder: [categoryId: number, newParentId: number | undefined, newOrderIndex: number]
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const draggedItem = ref<Category | null>(null)
const dropTarget = ref<Category | null>(null)
const expandedItems = ref<Set<number>>(new Set())

// 정렬된 카테고리 (최상위 레벨만)
const sortedCategories = computed(() => {
  return props.categories.filter((cat) => !cat.parentId).sort((a, b) => a.orderIndex - b.orderIndex)
})

// 드래그 시작
const handleDragStart = (category: Category, event: DragEvent) => {
  draggedItem.value = category
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/plain', category.id.toString())
  }
}

// 드래그 종료
const handleDragEnd = () => {
  draggedItem.value = null
  dropTarget.value = null
}

// 드래그 오버
const handleDragOver = (category: Category, event: DragEvent) => {
  event.preventDefault()
  if (draggedItem.value && draggedItem.value.id !== category.id) {
    dropTarget.value = category
  }
}

// 드롭 처리
const handleDrop = (targetCategory: Category, event: DragEvent) => {
  event.preventDefault()

  if (!draggedItem.value || draggedItem.value.id === targetCategory.id) {
    return
  }

  // 순환 참조 방지
  if (isDescendant(draggedItem.value, targetCategory)) {
    return
  }

  // 새로운 순서 인덱스 계산
  const siblings = getSiblings(targetCategory)
  const targetIndex = siblings.findIndex((cat) => cat.id === targetCategory.id)
  const newOrderIndex = targetIndex >= 0 ? targetIndex : 0

  emit('reorder', draggedItem.value.id, targetCategory.parentId, newOrderIndex)

  draggedItem.value = null
  dropTarget.value = null
}

// 최상위 레벨로 드롭
const handleDropToRoot = (event: DragEvent) => {
  event.preventDefault()

  if (!draggedItem.value) {
    return
  }

  const rootCategories = props.categories.filter((cat) => !cat.parentId)
  const newOrderIndex = rootCategories.length

  emit('reorder', draggedItem.value.id, undefined, newOrderIndex)

  draggedItem.value = null
  dropTarget.value = null
}

// 편집 처리
const handleEdit = (category: Category) => {
  emit('edit', category)
}

// 삭제 처리
const handleDelete = (category: Category) => {
  emit('delete', category)
}

// 토글 처리
const handleToggle = (categoryId: number) => {
  if (expandedItems.value.has(categoryId)) {
    expandedItems.value.delete(categoryId)
  } else {
    expandedItems.value.add(categoryId)
  }
}

// 자식 카테고리인지 확인
const isDescendant = (parent: Category, child: Category): boolean => {
  if (!parent.children) return false

  for (const childCategory of parent.children) {
    if (childCategory.id === child.id) return true
    if (isDescendant(childCategory, child)) return true
  }

  return false
}

// 형제 카테고리들 가져오기
const getSiblings = (category: Category): Category[] => {
  if (!category.parentId) {
    return props.categories.filter((cat) => !cat.parentId)
  }

  const parent = props.categories.find((cat) => cat.id === category.parentId)
  return parent?.children || []
}
</script>

<style scoped>
.category-tree {
  width: 100%;
}

.tree-header {
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 1rem;
}

.tree-container {
  min-height: 200px;
}

.tree-item {
  margin-bottom: 0.5rem;
}

.tree-item.is-dragging {
  opacity: 0.5;
}

.drop-zone {
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  padding: 2rem;
  text-align: center;
  margin-top: 1rem;
  background-color: #f9fafb;
  transition: all 0.2s ease;
}

.drop-zone:hover {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.drop-zone-content {
  color: #6b7280;
}

.drop-zone:hover .drop-zone-content {
  color: #3b82f6;
}
</style>
