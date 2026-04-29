<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import Modal from '../components/Modal.vue'
import EChart from '../components/EChart.vue'
import { apiStockList, apiStockSetSafe } from '../api/wms'
import { ensureGoodsLoaded, goodsNameById } from '../shared/goodsCache'
import { userRef } from '../auth'

const loading = ref(false)
const errorMsg = ref('')

const query = reactive({
  goodsName: '',
  lowStock: false
})

const rows = ref([])
const viewMode = ref('table') // table|chart

const safeOpen = ref(false)
const currentGoodsId = ref(null)
const safeStock = ref(10)

const currentUser = userRef()
const isAdmin = computed(() => String(currentUser.value?.role || '').toUpperCase() === 'ADMIN')

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    await ensureGoodsLoaded()
    const data = await apiStockList({ goodsName: query.goodsName.trim(), lowStock: query.lowStock })
    rows.value = Array.isArray(data) ? data : []
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'load failed'
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  query.goodsName = ''
  query.lowStock = false
  load()
}

function openSafe(row) {
  if (!isAdmin.value) return
  currentGoodsId.value = row.goodsId
  safeStock.value = Number(row.safeStock || 0)
  safeOpen.value = true
}

async function saveSafe() {
  errorMsg.value = ''
  try {
    await apiStockSetSafe({ goodsId: currentGoodsId.value, safeStock: Number(safeStock.value || 0) })
    safeOpen.value = false
    await load()
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'save failed'
  }
}

onMounted(load)

const chartOption = computed(() => {
  const items = rows.value || []
  const names = items.map((r) => goodsNameById(r.goodsId))
  const qty = items.map((r) => Number(r.quantity || 0))
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 20, bottom: 40 },
    xAxis: { type: 'category', data: names, axisLabel: { rotate: 30 } },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: qty, itemStyle: { color: '#4f46e5' } }]
  }
})
</script>

<template>
  <div class="panel">
    <div class="panelHeader">
      <div class="panelTitle">库存管理</div>
      <div class="row">
        <button class="ghostBtn" :disabled="loading" @click="load">刷新</button>
      </div>
    </div>
    <div class="panelBody">
      <div v-if="errorMsg" class="error">{{ errorMsg }}</div>

      <div class="row" style="margin-bottom: 10px">
        <div class="field">
          <div class="label">商品名称</div>
          <input class="input" v-model="query.goodsName" placeholder="支持模糊查询" @keydown.enter="load" />
        </div>
        <label class="row" style="gap: 8px; align-self: flex-end">
          <input type="checkbox" v-model="query.lowStock" />
          <span class="muted">仅低于安全库存</span>
        </label>
        <div class="row" style="align-self: flex-end">
          <button class="ghostBtn" @click="load">查询</button>
          <button class="ghostBtn" @click="resetSearch">重置</button>
        </div>
      </div>

      <div class="row" style="justify-content: space-between; margin-bottom: 10px">
        <div class="muted">视图</div>
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="table">表格</el-radio-button>
          <el-radio-button label="chart">柱状图</el-radio-button>
        </el-radio-group>
      </div>

      <div v-if="viewMode === 'chart'" class="panel" style="padding: 10px">
        <EChart :option="chartOption" height="360px" />
      </div>

      <table v-else class="table">
        <thead>
          <tr>
            <th style="width: 70px">ID</th>
            <th style="width: 100px">商品ID</th>
            <th>商品名称</th>
            <th style="width: 120px">库存数量</th>
            <th style="width: 120px">安全库存</th>
            <th style="width: 210px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="rows.length === 0">
            <td colspan="6" class="muted">暂无数据</td>
          </tr>
          <tr v-for="r in rows" :key="r.id">
            <td>{{ r.id }}</td>
            <td>{{ r.goodsId }}</td>
            <td>{{ goodsNameById(r.goodsId) }}</td>
            <td>
              <span class="pill" :style="r.quantity < r.safeStock ? 'border-color:#fecaca;background:#fef2f2;color:#991b1b' : ''">
                {{ r.quantity }}
              </span>
            </td>
            <td>{{ r.safeStock }}</td>
            <td>
              <div class="row" style="gap: 8px">
                <button class="ghostBtn" :disabled="!isAdmin" @click="openSafe(r)">安全库存</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <Modal :open="safeOpen" title="设置安全库存" @close="safeOpen = false">
    <div class="field">
      <div class="label">商品</div>
      <div class="pill">{{ goodsNameById(currentGoodsId) }}</div>
    </div>
    <div class="field" style="margin-top: 10px">
      <div class="label">安全库存</div>
      <input class="input" type="number" min="0" step="1" v-model="safeStock" />
    </div>
    <template #footer>
      <button class="ghostBtn" @click="safeOpen = false">取消</button>
      <button class="primaryBtn" @click="saveSafe">保存</button>
    </template>
  </Modal>
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
