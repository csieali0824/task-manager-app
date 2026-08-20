<script setup lang="ts">
// [AI assisted 001, 003]
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
  <div
    v-if="tasks.length === 0"
    class="flex flex-col items-center gap-3 rounded-2xl border border-dashed border-slate-300/80 py-14 dark:border-white/10"
  >
    <div
      class="flex size-10 items-center justify-center rounded-full bg-slate-900/[0.04] dark:bg-white/[0.06]"
    >
      <svg viewBox="0 0 20 20" fill="none" class="size-5 text-slate-400 dark:text-slate-500" aria-hidden="true">
        <path
          d="M7 3.5h8.5a1 1 0 0 1 1 1V15M4.5 6.5H13a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1H4.5a1 1 0 0 1-1-1v-8a1 1 0 0 1 1-1Z"
          stroke="currentColor"
          stroke-width="1.4"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
    </div>
    <p class="text-[13px] text-slate-400 dark:text-slate-500">還沒有任務，從上方新增第一個吧</p>
  </div>
  <TransitionGroup v-else tag="ul" name="task" class="relative space-y-2.5">
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
