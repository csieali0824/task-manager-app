<script setup lang="ts">
// [AI assisted 001, 002, 003]
import { computed, onMounted, ref } from 'vue'
import AlertDialog from '@/components/AlertDialog.vue'
import TaskForm from '@/components/TaskForm.vue'
import TaskList from '@/components/TaskList.vue'
import { createTask, deleteTask, listTasks, updateTask } from '@/api/tasks'
import type { Task, TaskInput } from '@/types/task'

const tasks = ref<Task[]>([])
const editingTask = ref<Task | null>(null)
const errorMessage = ref('')
const pendingDelete = ref<Task | null>(null)

const completedCount = computed(() => tasks.value.filter((t) => t.completed).length)

const deleteMessage = computed(() =>
  pendingDelete.value ? `確定要刪除「${pendingDelete.value.title}」嗎？` : '',
)

async function refresh() {
  try {
    tasks.value = await listTasks()

    // 編輯中的任務可能已經不存在(刪除動作)
    const editingId = editingTask.value?.id
    if (editingId !== undefined && !tasks.value.some((t) => t.id === editingId)) {
      editingTask.value = null
    }
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
    // PUT replaces the whole task. The list row already holds every field, so send it back
    // unchanged apart from the flag.
    await updateTask(task.id, {
      title: task.title,
      description: task.description,
      completed: !task.completed,
    })
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

function onRemove(task: Task) {
  // 只記下要刪的是哪一筆，實際刪除等使用者在確認視窗按下確定。
  pendingDelete.value = task
}

async function confirmDelete() {
  const task = pendingDelete.value
  pendingDelete.value = null
  if (!task) return

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
  <main class="mx-auto w-full max-w-2xl px-6 py-16">
    <header class="mb-8 flex items-baseline justify-between border-b border-stone-300 pb-4">
      <h1 class="text-xl font-semibold tracking-tight text-stone-900">Task Manager</h1>
      <p v-if="tasks.length > 0" class="text-[13px] text-stone-500">
        共 {{ tasks.length }} 項任務，{{ completedCount }} 項已完成
      </p>
    </header>

    <p
      v-if="errorMessage"
      class="mb-6 border-l-2 border-red-600 bg-red-50 px-4 py-2.5 text-[13px] text-red-800"
    >
      {{ errorMessage }}
    </p>

    <TaskForm :editing-task="editingTask" @submit="onSubmit" @cancel="onCancelEdit" />
    <TaskList :tasks="tasks" @toggle="onToggle" @edit="onEdit" @remove="onRemove" />

    <AlertDialog
      mode="confirm"
      :message="deleteMessage"
      @close="pendingDelete = null"
      @confirm="confirmDelete"
    />
  </main>
</template>
