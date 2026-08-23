<script setup lang="ts">
// [AI assisted 003]
import BaseButton from '@/components/BaseButton.vue'

defineProps<{
  message: string
}>()

const emit = defineEmits<{
  close: []
}>()
</script>

<template>
  <Teleport to="body">
    <div
      v-if="message"
      class="fixed inset-0 z-50 flex items-start justify-center bg-stone-900/25 px-6 pt-36"
      @click.self="emit('close')"
      @keydown.esc="emit('close')"
    >
      <div
        role="alertdialog"
        aria-modal="true"
        class="w-full max-w-xs rounded-lg border border-stone-200 bg-white p-5 shadow-xl"
      >
        <p class="text-sm/6 text-stone-800">{{ message }}</p>
        <div class="mt-4 flex justify-end">
          <BaseButton variant="primary" autofocus @click="emit('close')">確定</BaseButton>
        </div>
      </div>
    </div>
  </Teleport>
</template>
