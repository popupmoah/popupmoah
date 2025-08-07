<template>
  <div class="tree-node">
    <div
      class="node-content"
      :class="{
        'is-expanded': isExpanded,
        'is-drop-target': isDropTarget,
        'has-children': hasChildren,
      }"
      :style="{ paddingLeft: `${level * 20 + 12}px` }"
      draggable="true"
      @dragstart="handleDragStart"
      @dragend="handleDragEnd"
      @dragover="handleDragOver"
      @drop="handleDrop"
      @click="handleClick"
    >
      <!-- 확장/축소 아이콘 -->
      <div v-if="hasChildren" class="expand-icon" @click.stop="toggleExpanded">
        <q-icon :name="isExpanded ? 'expand_less' : 'expand_more'" size="1.2em" color="grey-6" />
      </div>
      <div v-else class="expand-placeholder"></div>

      <!-- 카테고리 정보 -->
      <div class="category-info">
        <div class="category-name">
          <q-icon name="category" size="1.2em" color="primary" class="mr-2" />
          <span class="font-medium">{{ category.name }}</span>
          <q-badge v-if="!category.isActive" label="비활성" color="grey" size="sm" class="ml-2" />
        </div>

        <div v-if="category.description" class="category-description">
          {{ category.description }}
        </div>

        <div class="category-meta">
          <span class="text-xs text-gray-500">레벨 {{ category.level }}</span>
          <span class="text-xs text-gray-500 mx-2">•</span>
          <span class="text-xs text-gray-500">순서 {{ category.orderIndex }}</span>
          <span v-if="hasChildren" class="text-xs text-gray-500 mx-2">•</span>
          <span v-if="hasChildren" class="text-xs text-gray-500">
            {{ category.children?.length }}개 하위
          </span>
        </div>
      </div>

      <!-- 액션 버튼들 -->
      <div class="action-buttons">
        <q-btn flat round size="sm" icon="edit" color="primary" @click.stop="handleEdit" />
        <q-btn flat round size="sm" icon="delete" color="negative" @click.stop="handleDelete" />
      </div>
    </div>

    <!-- 하위 카테고리들 -->
    <div v-if="isExpanded && hasChildren" class="children-container">
      <CategoryTreeNode
        v-for="child in sortedChildren"
        :key="child.id"
        :category="child"
        :level="level + 1"
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
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Category } from '@/types'

interface Props {
  category: Category
  level: number
  draggedItem?: Category | null
  dropTarget?: Category | null
}

interface Emits {
  'drag-start': [category: Category, event: DragEvent]
  'drag-end': [event: DragEvent]
  'drag-over': [category: Category, event: DragEvent]
  drop: [category: Category, event: DragEvent]
  edit: [category: Category]
  delete: [category: Category]
  toggle: [categoryId: number]
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 계산된 속성들
const hasChildren = computed(() => {
  return props.category.children && props.category.children.length > 0
})

const isExpanded = computed(() => {
  // 기본적으로 최상위 레벨은 확장된 상태
  return props.level === 0 || true
})

const isDropTarget = computed(() => {
  return props.dropTarget?.id === props.category.id
})

const sortedChildren = computed(() => {
  if (!props.category.children) return []
  return [...props.category.children].sort((a, b) => a.orderIndex - b.orderIndex)
})

// 이벤트 핸들러들
const handleDragStart = (event: DragEvent) => {
  emit('drag-start', props.category, event)
}

const handleDragEnd = (event: DragEvent) => {
  emit('drag-end', event)
}

const handleDragOver = (event: DragEvent) => {
  event.preventDefault()
  emit('drag-over', props.category, event)
}

const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  emit('drop', props.category, event)
}

const handleClick = () => {
  if (hasChildren.value) {
    toggleExpanded()
  }
}

const toggleExpanded = () => {
  emit('toggle', props.category.id)
}

const handleEdit = () => {
  emit('edit', props.category)
}

const handleDelete = () => {
  emit('delete', props.category)
}
</script>

<style scoped>
.tree-node {
  width: 100%;
}

.node-content {
  display: flex;
  align-items: center;
  padding: 0.75rem;
  border-radius: 8px;
  background-color: white;
  border: 1px solid #e5e7eb;
  margin-bottom: 0.25rem;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.node-content:hover {
  background-color: #f9fafb;
  border-color: #d1d5db;
}

.node-content.is-drop-target {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.node-content.is-expanded {
  border-left: 3px solid #3b82f6;
}

.expand-icon,
.expand-placeholder {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 0.5rem;
}

.category-info {
  flex: 1;
  min-width: 0;
}

.category-name {
  display: flex;
  align-items: center;
  font-size: 0.875rem;
  color: #374151;
}

.category-description {
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: 0.25rem;
  line-height: 1.4;
}

.category-meta {
  margin-top: 0.25rem;
  display: flex;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 0.25rem;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.node-content:hover .action-buttons {
  opacity: 1;
}

.children-container {
  margin-left: 1rem;
  border-left: 1px solid #e5e7eb;
  padding-left: 1rem;
}

/* 드래그 중일 때 스타일 */
.node-content[draggable='true']:active {
  opacity: 0.5;
}

/* 드롭 타겟 스타일 */
.node-content.is-drop-target {
  position: relative;
}

.node-content.is-drop-target::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2px dashed #3b82f6;
  border-radius: 8px;
  pointer-events: none;
}
</style>
