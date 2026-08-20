<script setup lang="ts">
// [AI assisted 001, 003]
import { reactive, watch } from 'vue'
import BaseButton from '@/components/BaseButton.vue'
import type { Task, TaskInput } from '@/types/task'

const props = defineProps<{
  editingTask: Task | null
}>()

const emit = defineEmits<{
  submit: [input: TaskInput]
  cancel: []
}>()

const form = reactive<TaskInput>({
  title: '',
  description: '',
  completed: false,
})

watch(
  () => props.editingTask,
  (task) => {
    form.title = task?.title ?? ''
    form.description = task?.description ?? ''
    form.completed = task?.completed ?? false
  },
  { immediate: true },
)

function onSubmit() {
  if (!form.title.trim()) return
  emit('submit', { ...form })
  if (!props.editingTask) {
    form.title = ''
    form.description = ''
    form.completed = false
  }
}
</script>

<template>
  <form
    class="mb-6 rounded-2xl bg-white/80 p-2 shadow-[0_1px_2px_rgb(0_0_0/0.04),0_8px_24px_-12px_rgb(0_0_0/0.1)] ring-1 ring-slate-900/[0.07] backdrop-blur transition-shadow focus-within:shadow-[0_1px_2px_rgb(0_0_0/0.04),0_8px_24px_-8px_rgb(99_102_241/0.25)] focus-within:ring-indigo-500/40 dark:bg-white/[0.04] dark:ring-white/10 dark:focus-within:ring-indigo-400/40"
    @submit.prevent="onSubmit"
  >
    <p
      v-if="editingTask"
      class="flex items-center gap-1.5 px-3 pt-2 pb-1 text-xs font-medium text-indigo-600 dark:text-indigo-400"
    >
      <svg viewBox="0 0 16 16" fill="none" class="size-3.5" aria-hidden="true">
        <path
          d="M11.3 2.3a1 1 0 0 1 1.4 0l1 1a1 1 0 0 1 0 1.4L6 12.4l-3 .6.6-3 7.7-7.7Z"
          stroke="currentColor"
          stroke-width="1.4"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
      正在編輯「{{ editingTask.title }}」
    </p>
    <div class="flex flex-col gap-2 sm:flex-row sm:items-center">
      <input
        v-model="form.title"
        type="text"
        placeholder="新增任務…"
        required
        class="w-full rounded-xl bg-transparent px-3 py-2 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none dark:text-white dark:placeholder:text-slate-500 sm:flex-1"
      />
      <input
        v-model="form.description"
        type="text"
        placeholder="描述（選填）"
        class="w-full rounded-xl bg-transparent px-3 py-2 text-sm text-slate-600 placeholder:text-slate-400 focus:outline-none dark:text-slate-300 dark:placeholder:text-slate-500 sm:w-52 sm:border-l sm:border-slate-900/[0.07] sm:pl-4 dark:sm:border-white/10"
      />
      <div class="flex shrink-0 gap-1.5 p-1 sm:p-0 sm:pr-1">
        <BaseButton type="submit" variant="primary">
          {{ editingTask ? '儲存' : '新增' }}
        </BaseButton>
        <BaseButton v-if="editingTask" variant="ghost" @click="emit('cancel')">
          取消
        </BaseButton>
      </div>
    </div>
  </form>
</template>
