<script setup lang="ts">
// [AI assisted 003]
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
    'bg-slate-900 text-white shadow-[0_1px_2px_rgb(0_0_0/0.2),inset_0_1px_0_rgb(255_255_255/0.08)] hover:bg-slate-800 active:scale-[0.98] focus-visible:outline-slate-900 dark:bg-white dark:text-slate-900 dark:hover:bg-slate-200 dark:focus-visible:outline-white',
  secondary:
    'bg-white/70 text-slate-600 ring-1 ring-inset ring-slate-900/10 shadow-[0_1px_2px_rgb(0_0_0/0.04)] hover:bg-white hover:text-slate-900 hover:ring-slate-900/15 active:scale-[0.98] focus-visible:outline-slate-400 dark:bg-white/[0.06] dark:text-slate-300 dark:ring-white/10 dark:hover:bg-white/10 dark:hover:text-white',
  danger:
    'bg-white/70 text-slate-500 ring-1 ring-inset ring-slate-900/10 shadow-[0_1px_2px_rgb(0_0_0/0.04)] hover:bg-red-50 hover:text-red-600 hover:ring-red-600/20 active:scale-[0.98] focus-visible:outline-red-500 dark:bg-white/[0.06] dark:text-slate-400 dark:ring-white/10 dark:hover:bg-red-500/10 dark:hover:text-red-400 dark:hover:ring-red-500/30',
  ghost:
    'text-slate-500 hover:bg-slate-900/5 hover:text-slate-900 active:scale-[0.98] focus-visible:outline-slate-400 dark:text-slate-400 dark:hover:bg-white/5 dark:hover:text-white',
}

const classes = computed(() => [
  'inline-flex items-center justify-center gap-1.5 rounded-lg px-3 py-1.5 text-[13px]/5 font-medium',
  'transition-all duration-150',
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
