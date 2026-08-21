<script setup lang="ts">
// [AI assisted 001, 003, 004]
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
      <svg
        v-if="task.completed"
        viewBox="0 0 12 12"
        fill="none"
        class="size-3 text-white"
        aria-hidden="true"
      >
        <path
          d="M2.5 6.5 5 9l4.5-5.5"
          stroke="currentColor"
          stroke-width="1.8"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
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
        <svg viewBox="0 0 16 16" fill="none" class="size-4" aria-hidden="true">
          <path
            d="M11.3 2.3a1 1 0 0 1 1.4 0l1 1a1 1 0 0 1 0 1.4L6 12.4l-3 .6.6-3 7.7-7.7Z"
            stroke="currentColor"
            stroke-width="1.4"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
      <button
        type="button"
        aria-label="刪除"
        title="刪除"
        class="flex size-8 items-center justify-center rounded-md text-stone-400 transition-colors hover:bg-red-50 hover:text-red-600 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-500"
        @click="emit('remove', task)"
      >
        <svg viewBox="0 0 16 16" fill="none" class="size-4" aria-hidden="true">
          <path
            d="M2.5 4h11M6.5 4V2.8a.8.8 0 0 1 .8-.8h1.4a.8.8 0 0 1 .8.8V4m2.7 0-.5 9.2a1 1 0 0 1-1 .95H5.3a1 1 0 0 1-1-.95L3.8 4M6.5 7v4.5M9.5 7v4.5"
            stroke="currentColor"
            stroke-width="1.3"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
    </div>
  </li>
</template>
