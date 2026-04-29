<script setup>
import { computed } from 'vue'

const props = defineProps({
  page: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 },
  total: { type: Number, default: 0 }
})

const emit = defineEmits(['update:page', 'update:pageSize'])

const totalPages = computed(() => Math.max(1, Math.ceil((props.total || 0) / (props.pageSize || 10))))

function prev() {
  emit('update:page', Math.max(1, props.page - 1))
}

function next() {
  emit('update:page', Math.min(totalPages.value, props.page + 1))
}
</script>

<template>
  <div class="row" style="justify-content: space-between; margin-top: 10px">
    <div class="muted">共 {{ total }} 条</div>
    <div class="row">
      <button class="ghostBtn" :disabled="page <= 1" @click="prev">上一页</button>
      <div class="pill">第 {{ page }} / {{ totalPages }} 页</div>
      <button class="ghostBtn" :disabled="page >= totalPages" @click="next">下一页</button>
      <select class="select" :value="pageSize" @change="$emit('update:pageSize', Number($event.target.value))">
        <option :value="10">10</option>
        <option :value="20">20</option>
        <option :value="50">50</option>
      </select>
    </div>
  </div>
</template>

