<script setup>
import { computed, onMounted, ref } from 'vue'
import EChart from '../components/EChart.vue'
import { apiLogsList, apiStatsCategoryShare, apiStatsIO, apiStatsStock, apiStatsTrend30d, apiUserInfo, apiWarningLowStock } from '../api/wms'

const loading = ref(false)
const errorMsg = ref('')
const stockStats = ref({ totalGoods: 0, totalStock: 0 })
const ioStats = ref({ inboundTotal: 0, outboundTotal: 0 })
const user = ref(null)
const categoryShare = ref([])
const trend = ref({ inbound: [], outbound: [] })
const warnings = ref([])
const logs = ref([])

const inbound = computed(() => Number((ioStats.value && ioStats.value.inboundTotal) || 0))
const outbound = computed(() => Number((ioStats.value && ioStats.value.outboundTotal) || 0))
const maxIO = computed(() => Math.max(1, inbound.value, outbound.value))

const pieOption = computed(() => {
  const items = categoryShare.value || []
  return {
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom' },
    series: [
      {
        type: 'pie',
        radius: ['35%', '65%'],
        data: items.map((it) => ({ name: it.category || '未分类', value: Number(it.quantity || 0) }))
      }
    ]
  }
})

const trendOption = computed(() => {
  const inboundRows = (trend.value && trend.value.inbound) || []
  const outboundRows = (trend.value && trend.value.outbound) || []
  const days = Array.from(new Set([...inboundRows.map((r) => r.day), ...outboundRows.map((r) => r.day)])).sort()
  const inMap = new Map(inboundRows.map((r) => [r.day, Number(r.quantity || 0)]))
  const outMap = new Map(outboundRows.map((r) => [r.day, Number(r.quantity || 0)]))
  return {
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 40, right: 20, top: 30, bottom: 40 },
    xAxis: { type: 'category', data: days, axisLabel: { rotate: 30 } },
    yAxis: { type: 'value' },
    series: [
      { name: '入库', type: 'line', smooth: true, data: days.map((d) => inMap.get(d) || 0) },
      { name: '出库', type: 'line', smooth: true, data: days.map((d) => outMap.get(d) || 0) }
    ]
  }
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const [s, io, u, cat, t, w, l] = await Promise.all([
      apiStatsStock(),
      apiStatsIO(),
      apiUserInfo().catch(() => null),
      apiStatsCategoryShare().catch(() => []),
      apiStatsTrend30d().catch(() => ({ inbound: [], outbound: [] })),
      apiWarningLowStock({ page: 1, pageSize: 6 }).catch(() => ({ rows: [], total: 0 })),
      apiLogsList({ page: 1, pageSize: 6 }).catch(() => ({ rows: [], total: 0 }))
    ])
    stockStats.value = s || { totalGoods: 0, totalStock: 0 }
    ioStats.value = io || { inboundTotal: 0, outboundTotal: 0 }
    user.value = u
    categoryShare.value = Array.isArray(cat) ? cat : []
    trend.value = t || { inbound: [], outbound: [] }
    warnings.value = (w && w.rows) || []
    logs.value = (l && l.rows) || []
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'load failed'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="row" style="gap: 12px; align-items: stretch">
    <div class="panel" style="flex: 1; min-width: 220px">
      <div class="panelHeader">
        <div class="panelTitle">库存概览</div>
        <button class="ghostBtn" :disabled="loading" @click="load">刷新</button>
      </div>
      <div class="panelBody">
        <div v-if="errorMsg" class="error">{{ errorMsg }}</div>
        <div class="cards">
          <div class="stat">
            <div class="k">商品数</div>
            <div class="v">{{ stockStats.totalGoods ?? 0 }}</div>
          </div>
          <div class="stat">
            <div class="k">库存总量</div>
            <div class="v">{{ stockStats.totalStock ?? 0 }}</div>
          </div>
          <div class="stat" v-if="user">
            <div class="k">当前用户</div>
            <div class="v">{{ user.username }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="panel" style="flex: 1; min-width: 260px">
      <div class="panelHeader">
        <div class="panelTitle">出入库统计</div>
      </div>
      <div class="panelBody">
        <div class="bars">
          <div class="barRow">
            <div class="name">入库</div>
            <div class="bar">
              <div class="fill inbound" :style="{ width: `${(inbound / maxIO) * 100}%` }" />
            </div>
            <div class="num">{{ inbound }}</div>
          </div>
          <div class="barRow">
            <div class="name">出库</div>
            <div class="bar">
              <div class="fill outbound" :style="{ width: `${(outbound / maxIO) * 100}%` }" />
            </div>
            <div class="num">{{ outbound }}</div>
          </div>
        </div>
        <div class="muted" style="margin-top: 8px; font-size: 12px">数据来自 `/statistics/stock` 与 `/statistics/io`</div>
      </div>
    </div>
  </div>

  <div class="row" style="gap: 12px; margin-top: 12px; align-items: stretch">
    <div class="panel" style="flex: 1; min-width: 320px">
      <div class="panelHeader">
        <div class="panelTitle">分类占比</div>
      </div>
      <div class="panelBody">
        <EChart :option="pieOption" height="320px" />
      </div>
    </div>
    <div class="panel" style="flex: 2; min-width: 420px">
      <div class="panelHeader">
        <div class="panelTitle">趋势分析（近30天）</div>
      </div>
      <div class="panelBody">
        <EChart :option="trendOption" height="320px" />
      </div>
    </div>
  </div>

  <div class="row" style="gap: 12px; margin-top: 12px; align-items: stretch">
    <div class="panel" style="flex: 1; min-width: 420px">
      <div class="panelHeader">
        <div class="panelTitle">库存预警</div>
      </div>
      <div class="panelBody">
        <table class="table">
          <thead>
            <tr>
              <th style="width: 90px">商品ID</th>
              <th>商品</th>
              <th style="width: 110px">当前</th>
              <th style="width: 110px">安全</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="warnings.length === 0">
              <td colspan="4" class="muted">暂无预警</td>
            </tr>
            <tr v-for="r in warnings" :key="r.goodsId">
              <td>{{ r.goodsId }}</td>
              <td>{{ r.goodsName }}</td>
              <td><span class="pill" style="border-color:#fecaca;background:#fef2f2;color:#991b1b">{{ r.currentStock }}</span></td>
              <td>{{ r.safeStock }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="panel" style="flex: 1; min-width: 420px">
      <div class="panelHeader">
        <div class="panelTitle">最近日志</div>
      </div>
      <div class="panelBody">
        <table class="table">
          <thead>
            <tr>
              <th style="width: 120px">用户</th>
              <th style="width: 140px">操作</th>
              <th>内容</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="logs.length === 0">
              <td colspan="3" class="muted">暂无日志</td>
            </tr>
            <tr v-for="r in logs" :key="r.id">
              <td>{{ r.username }}</td>
              <td><span class="pill">{{ r.operation }}</span></td>
              <td style="white-space: normal">{{ r.content }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cards {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.stat {
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 10px;
  background: #ffffff;
}

.k {
  font-size: 12px;
  color: #475569;
}

.v {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 700;
}

.bars {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.barRow {
  display: grid;
  grid-template-columns: 46px 1fr 60px;
  gap: 10px;
  align-items: center;
}

.name {
  font-size: 12px;
  color: #475569;
}

.bar {
  height: 10px;
  border-radius: 999px;
  background: #f1f5f9;
  overflow: hidden;
}

.fill {
  height: 100%;
  border-radius: 999px;
}

.fill.inbound {
  background: #10b981;
}

.fill.outbound {
  background: #ef4444;
}

.num {
  text-align: right;
  font-variant-numeric: tabular-nums;
}

.error {
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid #fecaca;
  background: #fef2f2;
  color: #991b1b;
  font-size: 13px;
  margin-bottom: 10px;
}

@media (max-width: 980px) {
  .cards {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
