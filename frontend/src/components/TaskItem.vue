<script setup lang="ts">
// [AI assisted 001, 003]
import BaseButton from '@/components/BaseButton.vue'
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
  <li
    class="group flex items-center gap-3.5 rounded-2xl bg-white/80 px-4 py-3.5 shadow-[0_1px_2px_rgb(0_0_0/0.04)] ring-1 ring-slate-900/[0.07] backdrop-blur transition-all duration-200 hover:shadow-[0_4px_16px_-4px_rgb(0_0_0/0.08)] hover:ring-slate-900/[0.12] dark:bg-white/[0.04] dark:ring-white/10 dark:hover:bg-white/[0.06] dark:hover:ring-white/[0.15]"
  >
    <button
      type="button"
      role="checkbox"
      :aria-checked="task.completed"
      :aria-label="task.completed ? '標記為未完成' : '標記為已完成'"
      class="flex size-[22px] shrink-0 cursor-pointer items-center justify-center rounded-full transition-all duration-200 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500"
      :class="
        task.completed
          ? 'bg-gradient-to-b from-indigo-500 to-indigo-600 shadow-[0_1px_4px_rgb(99_102_241/0.4)]'
          : 'ring-[1.5px] ring-inset ring-slate-300 hover:ring-indigo-400 dark:ring-slate-600 dark:hover:ring-indigo-400'
      "
      @click="emit('toggle', task)"
    >
      <svg
        viewBox="0 0 12 12"
        fill="none"
        class="size-3 text-white transition-all duration-200"
        :class="task.completed ? 'scale-100 opacity-100' : 'scale-50 opacity-0'"
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
      <p
        class="truncate text-sm font-medium transition-colors duration-200"
        :class="
          task.completed
            ? 'text-slate-400 line-through decoration-slate-300 dark:text-slate-500 dark:decoration-slate-600'
            : 'text-slate-900 dark:text-white'
        "
      >
        {{ task.title }}
      </p>
      <p
        v-if="task.description"
        class="mt-0.5 truncate text-[13px] transition-colors duration-200"
        :class="
          task.completed
            ? 'text-slate-300 dark:text-slate-600'
            : 'text-slate-500 dark:text-slate-400'
        "
      >
        {{ task.description }}
      </p>
    </div>

    <div
      class="flex shrink-0 gap-1.5 transition-opacity duration-150 sm:opacity-0 sm:group-focus-within:opacity-100 sm:group-hover:opacity-100"
    >
      <BaseButton variant="secondary" @click="emit('edit', task)">編輯</BaseButton>
      <BaseButton variant="danger" @click="emit('remove', task)">刪除</BaseButton>
    </div>
  </li>
</template>
