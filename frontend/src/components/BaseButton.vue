<script setup lang="ts">
// [AI assisted 003, 004]
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
    type?: 'button' | 'submit'
    disabled?: boolean
  }>(),
  {
    variant: 'secondary',
    type: 'button',
    disabled: false,
  },
)

const variantClasses: Record<NonNullable<typeof props.variant>, string> = {
  primary:
    'bg-stone-900 text-white hover:bg-stone-700 focus-visible:outline-stone-900',
  secondary:
    'bg-white text-stone-600 border border-stone-300 hover:border-stone-400 hover:text-stone-900 focus-visible:outline-stone-500',
  danger:
    'bg-white text-stone-600 border border-stone-300 hover:border-red-300 hover:bg-red-50 hover:text-red-700 focus-visible:outline-red-500',
  ghost:
    'text-stone-500 hover:bg-stone-200/60 hover:text-stone-800 focus-visible:outline-stone-500',
}

const classes = computed(() => [
  'inline-flex items-center justify-center gap-1.5 rounded-md px-3 py-1.5 text-[13px]/5 font-medium',
  'transition-colors duration-100',
  'focus-visible:outline-2 focus-visible:outline-offset-2',
  'disabled:pointer-events-none disabled:opacity-40',
  variantClasses[props.variant],
])
</script>

<template>
  <button :type="type" :disabled="disabled" :class="classes">
    <slot />
  </button>
</template>
