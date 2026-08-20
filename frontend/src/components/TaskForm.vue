<script setup lang="ts">
// [AI assisted 001]
import { reactive, watch } from 'vue'
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
  <form class="task-form" @submit.prevent="onSubmit">
    <input v-model="form.title" type="text" placeholder="Task title" required />
    <input v-model="form.description" type="text" placeholder="Description (optional)" />
    <div class="task-form__actions">
      <button type="submit">{{ editingTask ? 'Save' : 'Add task' }}</button>
      <button v-if="editingTask" type="button" @click="emit('cancel')">Cancel</button>
    </div>
  </form>
</template>

<style scoped>
.task-form {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1.5rem;
}

.task-form input {
  flex: 1;
  min-width: 160px;
  padding: 0.5rem;
}

.task-form__actions {
  display: flex;
  gap: 0.5rem;
}
</style>
