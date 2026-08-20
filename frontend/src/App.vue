<script setup lang="ts">
// [AI assisted 001, 003]
import { computed, onMounted, ref } from 'vue'
import TaskForm from '@/components/TaskForm.vue'
import TaskList from '@/components/TaskList.vue'
import { createTask, deleteTask, listTasks, setTaskCompleted, updateTask } from '@/api/tasks'
import type { Task, TaskInput } from '@/types/task'

const tasks = ref<Task[]>([])
const editingTask = ref<Task | null>(null)
const errorMessage = ref('')

const completedCount = computed(() => tasks.value.filter((t) => t.completed).length)
const progress = computed(() =>
  tasks.value.length === 0 ? 0 : Math.round((completedCount.value / tasks.value.length) * 100),
)

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
  if (!task.completed) {
    window.alert('請先勾選完成此任務，才能進行編輯。')
    return
  }
  editingTask.value = task
}

function onCancelEdit() {
  editingTask.value = null
}

async function onRemove(task: Task) {
  if (!task.completed) {
    window.alert('請先勾選完成此任務，才能進行刪除。')
    return
  }
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
  <div class="bg-page-glow min-h-screen">
    <main class="mx-auto w-full max-w-xl px-5 py-14 sm:py-20">
      <header class="mb-10">
        <div class="flex items-center gap-3">
          <div
            class="flex size-9 items-center justify-center rounded-xl bg-gradient-to-b from-indigo-500 to-indigo-600 shadow-[0_2px_8px_rgb(99_102_241/0.4),inset_0_1px_0_rgb(255_255_255/0.25)]"
          >
            <svg viewBox="0 0 20 20" fill="none" class="size-4.5 text-white" aria-hidden="true">
              <path
                d="M4 10.5 8 14.5 16 5.5"
                stroke="currentColor"
                stroke-width="2.2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <h1 class="text-2xl font-semibold tracking-[-0.02em] text-slate-900 dark:text-white">
            Task Manager
          </h1>
        </div>

        <div v-if="tasks.length > 0" class="mt-6">
          <div class="flex items-baseline justify-between text-[13px]">
            <span class="font-medium text-slate-500 dark:text-slate-400">
              {{ completedCount }} / {{ tasks.length }} 已完成
            </span>
            <span class="tabular-nums font-medium text-slate-400 dark:text-slate-500">
              {{ progress }}%
            </span>
          </div>
          <div
            class="mt-2 h-1 overflow-hidden rounded-full bg-slate-900/[0.06] dark:bg-white/[0.08]"
          >
            <div
              class="h-full rounded-full bg-gradient-to-r from-indigo-500 to-violet-500 transition-[width] duration-500 ease-out"
              :style="{ width: `${progress}%` }"
            />
          </div>
        </div>
      </header>

      <Transition
        enter-active-class="transition duration-200 ease-out"
        enter-from-class="-translate-y-1 opacity-0"
        leave-active-class="transition duration-150 ease-in"
        leave-to-class="opacity-0"
      >
        <p
          v-if="errorMessage"
          class="mb-6 rounded-xl bg-red-500/[0.07] px-4 py-3 text-[13px] font-medium text-red-600 ring-1 ring-inset ring-red-500/20 dark:text-red-400"
        >
          {{ errorMessage }}
        </p>
      </Transition>

      <TaskForm :editing-task="editingTask" @submit="onSubmit" @cancel="onCancelEdit" />
      <TaskList :tasks="tasks" @toggle="onToggle" @edit="onEdit" @remove="onRemove" />
    </main>
  </div>
</template>
