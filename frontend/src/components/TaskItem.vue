<script setup lang="ts">
// [AI assisted 001]
import type { Task } from '@/types/task'

defineProps<{
  task: Task
}>()

const emit = defineEmits<{
  toggle: [task: Task]
  edit: [task: Task]
  remove: [task: Task]
}>()
</script>

<template>
  <li class="task-item" :class="{ 'task-item--done': task.completed }">
    <label class="task-item__title">
      <input
        type="checkbox"
        :checked="task.completed"
        @change="emit('toggle', task)"
      />
      <span>{{ task.title }}</span>
    </label>
    <p v-if="task.description" class="task-item__description">{{ task.description }}</p>
    <div class="task-item__actions">
      <button type="button" @click="emit('edit', task)">Edit</button>
      <button type="button" @click="emit('remove', task)">Delete</button>
    </div>
  </li>
</template>

<style scoped>
.task-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-bottom: 0.5rem;
}

.task-item--done .task-item__title span {
  text-decoration: line-through;
  color: #888;
}

.task-item__title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
}

.task-item__description {
  flex: 1;
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.task-item__actions {
  display: flex;
  gap: 0.5rem;
}
</style>
