<script setup>
defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: '' }
})

const emit = defineEmits(['close'])

function onMaskClick(e) {
  if (e.target === e.currentTarget) emit('close')
}
</script>

<template>
  <div v-if="open" class="modalMask" @click="onMaskClick">
    <div class="modal" role="dialog" aria-modal="true">
      <div class="modalHeader">
        <div class="modalTitle">{{ title }}</div>
        <button class="ghostBtn" @click="$emit('close')">关闭</button>
      </div>
      <div class="modalBody">
        <slot />
      </div>
      <div v-if="$slots.footer" class="modalFooter">
        <slot name="footer" />
      </div>
    </div>
  </div>
</template>

