// [AI assisted 001]
import type { Task, TaskInput } from '@/types/task'

const BASE_URL = '/api/tasks'

async function handle<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const body = await response.json().catch(() => null)
    throw new Error(body?.message ?? `Request failed with status ${response.status}`)
  }
  if (response.status === 204) {
    return undefined as T
  }
  return response.json() as Promise<T>
}

export function listTasks(): Promise<Task[]> {
  return fetch(BASE_URL).then((res) => handle<Task[]>(res))
}

export function createTask(input: TaskInput): Promise<Task> {
  return fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input),
  }).then((res) => handle<Task>(res))
}

/** PUT replaces the whole task, so every field of TaskInput must be sent. */
export function updateTask(id: number, input: TaskInput): Promise<Task> {
  return fetch(`${BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input),
  }).then((res) => handle<Task>(res))
}

export function deleteTask(id: number): Promise<void> {
  return fetch(`${BASE_URL}/${id}`, { method: 'DELETE' }).then((res) => handle<void>(res))
}
