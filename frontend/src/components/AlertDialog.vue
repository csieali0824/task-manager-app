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
    <Transition
      enter-active-class="transition duration-150 ease-out"
      enter-from-class="opacity-0"
      leave-active-class="transition duration-100 ease-in"
      leave-to-class="opacity-0"
    >
      <div
        v-if="message"
        class="fixed inset-0 z-50 flex items-start justify-center bg-stone-900/25 px-6 pt-36"
        @click.self="emit('close')"
        @keydown.esc="emit('close')"
      >
        <Transition
          appear
          enter-active-class="transition duration-150 ease-out"
          enter-from-class="scale-95 opacity-0"
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
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>
