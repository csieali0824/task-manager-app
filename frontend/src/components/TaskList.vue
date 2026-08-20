<script setup lang="ts">
// [AI assisted 001]
import type { Task } from '@/types/task'
import TaskItem from './TaskItem.vue'

defineProps<{
  tasks: Task[]
}>()

const emit = defineEmits<{
  toggle: [task: Task]
  edit: [task: Task]
  remove: [task: Task]
}>()
</script>

<template>
  <ul class="task-list">
    <li v-if="tasks.length === 0" class="task-list__empty">No tasks yet — add one above.</li>
    <TaskItem
      v-for="task in tasks"
      :key="task.id"
      :task="task"
      @toggle="emit('toggle', $event)"
      @edit="emit('edit', $event)"
      @remove="emit('remove', $event)"
    />
  </ul>
</template>

<style scoped>
.task-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.task-list__empty {
  color: #888;
  text-align: center;
  padding: 1rem;
}
</style>
