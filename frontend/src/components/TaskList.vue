<script setup lang="ts">
// [AI assisted 001, 002, 003]
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
  <p v-if="tasks.length === 0" class="py-12 text-center text-[13px] text-stone-400">
    目前沒有任務，從上方新增第一個。
  </p>
  <TransitionGroup
    v-else
    tag="ul"
    name="task"
    class="relative divide-y divide-stone-200 rounded-lg border border-stone-300 bg-white"
  >
    <TaskItem
      v-for="task in tasks"
      :key="task.id"
      :task="task"
      @toggle="emit('toggle', $event)"
      @edit="emit('edit', $event)"
      @remove="emit('remove', $event)"
    />
  </TransitionGroup>
</template>
