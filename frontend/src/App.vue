<script setup lang="ts">
// [AI assisted 001]
import { onMounted, ref } from 'vue'
import TaskForm from '@/components/TaskForm.vue'
import TaskList from '@/components/TaskList.vue'
import { createTask, deleteTask, listTasks, setTaskCompleted, updateTask } from '@/api/tasks'
import type { Task, TaskInput } from '@/types/task'

const tasks = ref<Task[]>([])
const editingTask = ref<Task | null>(null)
const errorMessage = ref('')

async function refresh() {
  try {
    tasks.value = await listTasks()
  } catch (err) {
    errorMessage.value = (err as Error).message
  }
}

async function onSubmit(input: TaskInput) {
  try {
    if (editingTask.value) {
      await updateTask(editingTask.value.id, input)
      editingTask.value = null
    } else {
      await createTask(input)
    }
    await refresh()
  } catch (err) {
    errorMessage.value = (err as Error).message
  }
}

async function onToggle(task: Task) {
  try {
    await setTaskCompleted(task.id, !task.completed)
    await refresh()
  } catch (err) {
    errorMessage.value = (err as Error).message
  }
}

function onEdit(task: Task) {
  editingTask.value = task
}

function onCancelEdit() {
  editingTask.value = null
}

async function onRemove(task: Task) {
  try {
    await deleteTask(task.id)
    await refresh()
  } catch (err) {
    errorMessage.value = (err as Error).message
  }
}

onMounted(refresh)
</script>

<template>
  <main class="app">
    <h1>Task Manager</h1>
    <p v-if="errorMessage" class="app__error">{{ errorMessage }}</p>
    <TaskForm :editing-task="editingTask" @submit="onSubmit" @cancel="onCancelEdit" />
    <TaskList :tasks="tasks" @toggle="onToggle" @edit="onEdit" @remove="onRemove" />
  </main>
</template>

<style scoped>
.app {
  max-width: 640px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.app__error {
  color: #c0392b;
  background: #fdecea;
  padding: 0.5rem;
  border-radius: 4px;
}
</style>