<script setup>
import { onMounted, ref } from 'vue'
import Pager from '../components/Pager.vue'
import { apiWarningLowStock } from '../api/wms'

const loading = ref(false)
const errorMsg = ref('')

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const rows = ref([])

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await apiWarningLowStock({ page: page.value, pageSize: pageSize.value })
    rows.value = (data && data.rows) || []
    total.value = Number((data && data.total) || 0)
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'load failed'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="panel">
    <div class="panelHeader">
      <div class="panelTitle">库存预警</div>
      <div class="row">
        <button class="ghostBtn" :disabled="loading" @click="load">刷新</button>
      </div>
    </div>
    <div class="panelBody">
      <div v-if="errorMsg" class="error">{{ errorMsg }}</div>

      <table class="table">
        <thead>
          <tr>
            <th style="width: 120px">商品ID</th>
            <th>商品名称</th>
            <th style="width: 160px">分类</th>
            <th style="width: 120px">当前库存</th>
            <th style="width: 120px">安全库存</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="rows.length === 0">
            <td colspan="5" class="muted">暂无预警</td>
          </tr>
          <tr v-for="r in rows" :key="r.goodsId">
            <td>{{ r.goodsId }}</td>
            <td>{{ r.goodsName }}</td>
            <td>{{ r.category }}</td>
            <td>
              <span class="pill" style="border-color:#fecaca;background:#fef2f2;color:#991b1b">{{ r.currentStock }}</span>
            </td>
            <td>{{ r.safeStock }}</td>
          </tr>
        </tbody>
      </table>

      <Pager v-model:page="page" v-model:pageSize="pageSize" :total="total" @update:page="load" @update:pageSize="load" />
    </div>
  </div>
</template>

<style scoped>
.error {
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid #fecaca;
  background: #fef2f2;
  color: #991b1b;
  font-size: 13px;
  margin-bottom: 10px;
}
</style>

