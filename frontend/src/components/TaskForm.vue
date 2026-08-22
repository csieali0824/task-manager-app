<script setup lang="ts">
// [AI assisted 001, 002, 003]
import { reactive, ref, watch } from 'vue'
import BaseButton from '@/components/BaseButton.vue'
import type { Task, TaskInput } from '@/types/task'
import { Check, Plus, X } from 'lucide-vue-next'

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

const titleError = ref('')

watch(
  () => props.editingTask,
  (task) => {
    form.title = task?.title ?? ''
    form.description = task?.description ?? ''
    form.completed = task?.completed ?? false
    titleError.value = ''
  },
  { immediate: true },
)

function onSubmit() {
  if (!form.title.trim()) {
    titleError.value = '請輸入任務名稱。'
    return
  }
  titleError.value = ''
  emit('submit', { ...form })
  if (!props.editingTask) {
    form.title = ''
    form.description = ''
    form.completed = false
  }
}
</script>

<template>
  <form class="mb-10" novalidate @submit.prevent="onSubmit">
    <p v-if="editingTask" class="mb-2 text-[13px] text-stone-500">
      正在編輯「<span class="font-medium text-stone-800">{{ editingTask.title }}</span
      >」
    </p>
    <div class="flex flex-col gap-2 sm:flex-row sm:items-end">
      <label class="relative flex-1">
        <span class="mb-1 block text-xs font-medium text-stone-500">任務名稱</span>
        <input
          v-model="form.title"
          type="text"
          :aria-invalid="!!titleError"
          class="w-full border-b bg-transparent px-0.5 py-1.5 text-sm text-stone-900 placeholder:text-stone-400 focus:outline-none"
          :class="
            titleError
              ? 'border-red-500 focus:border-red-600'
              : 'border-stone-300 focus:border-stone-800'
          "
          @input="titleError = ''"
        />
        <span v-if="titleError" class="absolute top-full mt-1 block text-xs text-red-600">
          {{ titleError }}
        </span>
      </label>
      <label class="flex-1">
        <span class="mb-1 block text-xs font-medium text-stone-500">描述（選填）</span>
        <input
          v-model="form.description"
          type="text"
          class="w-full border-b border-stone-300 bg-transparent px-0.5 py-1.5 text-sm text-stone-700 placeholder:text-stone-400 focus:border-stone-800 focus:outline-none"
        />
      </label>
      <div class="flex shrink-0 gap-2 pt-2 sm:pt-0">
        <BaseButton type="submit" variant="primary">
<!--          <Check v-if="editingTask" :size="16" />-->
<!--          <Plus v-else :size="16" />-->
          {{ editingTask ? '儲存' : '新增任務' }}
        </BaseButton>
<!--        <BaseButton v-if="editingTask" variant="ghost" @click="emit('cancel')"><X :size="16"/></BaseButton>-->
        <BaseButton v-if="editingTask" variant="ghost" @click="emit('cancel')">取消</BaseButton>
      </div>
    </div>
  </form>
</template>
