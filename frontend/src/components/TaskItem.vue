<script setup lang="ts">
// [AI assisted 001, 002, 003]
import type { Task } from '@/types/task'
import { Check, Pencil, Trash2 } from 'lucide-vue-next'

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
  <li class="flex items-center gap-3 px-4 py-3 transition-colors hover:bg-stone-50">
    <button
      type="button"
      role="checkbox"
      :aria-checked="task.completed"
      :aria-label="task.completed ? '標記為未完成' : '標記為已完成'"
      class="flex size-[18px] shrink-0 cursor-pointer items-center justify-center rounded-[4px] border transition-colors duration-150 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-emerald-700"
      :class="
        task.completed
          ? 'border-emerald-700 bg-emerald-700'
          : 'border-stone-400 bg-white hover:border-emerald-700'
      "
      @click="emit('toggle', task)"
    >
      <Check v-if="task.completed" :size="12" :stroke-width="3" class="text-white" />
    </button>

    <div class="min-w-0 flex-1">
      <p class="truncate text-sm text-stone-900">
        {{ task.title }}
        <span
          v-if="task.completed"
          class="ml-1.5 align-middle text-[11px] font-medium text-emerald-700"
        >
          已完成
        </span>
      </p>
      <p v-if="task.description" class="mt-0.5 truncate text-[13px] text-stone-500">
        {{ task.description }}
      </p>
    </div>

    <div class="flex shrink-0 gap-1">
      <button
        type="button"
        aria-label="編輯"
        title="編輯"
        class="flex size-8 items-center justify-center rounded-md text-stone-400 transition-colors hover:bg-stone-200/60 hover:text-stone-700 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-stone-500"
        @click="emit('edit', task)"
      >
        <Pencil :size="16" />
      </button>
      <button
        type="button"
        aria-label="刪除"
        title="刪除"
        class="flex size-8 items-center justify-center rounded-md text-stone-400 transition-colors hover:bg-red-50 hover:text-red-600 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-500"
        @click="emit('remove', task)"
      >
        <Trash2 :size="16" />
      </button>
    </div>
  </li>
</template>
