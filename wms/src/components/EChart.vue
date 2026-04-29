<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  option: { type: Object, default: () => ({}) },
  height: { type: String, default: '320px' }
})

const elRef = ref(null)
let chart = null
let ro = null

function render() {
  if (!chart) return
  chart.setOption(props.option || {}, true)
}

onMounted(() => {
  if (!elRef.value) return
  chart = echarts.init(elRef.value)
  render()
  ro = new ResizeObserver(() => {
    chart && chart.resize()
  })
  ro.observe(elRef.value)
})

watch(
  () => props.option,
  () => render(),
  { deep: true }
)

onBeforeUnmount(() => {
  try {
    ro && ro.disconnect()
  } catch {
    // ignore
  }
  try {
    chart && chart.dispose()
  } catch {
    // ignore
  }
  chart = null
})
</script>

<template>
  <div ref="elRef" :style="{ width: '100%', height }" />
</template>

