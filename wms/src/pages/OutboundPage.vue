<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import Modal from '../components/Modal.vue'
import Pager from '../components/Pager.vue'
import { apiOutboundCreate, apiOutboundList, apiUsers } from '../api/wms'
import { ensureGoodsLoaded, goodsCache, goodsNameById } from '../shared/goodsCache'
import { userRef } from '../auth'

const loading = ref(false)
const errorMsg = ref('')

const query = reactive({
  goodsName: ''
})

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const rows = ref([])

const createOpen = ref(false)
const form = reactive({
  goodsId: null,
  quantity: 1,
  operatorId: null,
  receiver: ''
})

const goodsItems = computed(() => goodsCache().value.items || [])
const users = ref([])
const currentUser = userRef()
const isAdmin = computed(() => String(currentUser.value?.role || '').toUpperCase() === 'ADMIN')
const userMap = computed(() => {
  const m = new Map()
  users.value.forEach((u) => m.set(Number(u.id), u))
  return m
})

function usernameById(id) {
  return userMap.value.get(Number(id))?.username || `#${id}`
}

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    await ensureGoodsLoaded()
    const data = await apiOutboundList({ goodsName: query.goodsName.trim(), page: page.value, pageSize: pageSize.value })
    rows.value = (data && data.rows) || []
    total.value = Number((data && data.total) || 0)
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'load failed'
  } finally {
    loading.value = false
  }
}

async function loadUsers() {
  try {
    const list = await apiUsers()
    users.value = Array.isArray(list) ? list : []
    if (!form.operatorId && currentUser.value?.id) form.operatorId = currentUser.value.id
    if (!form.operatorId && users.value.length) form.operatorId = users.value[0].id
  } catch {
    users.value = []
  }
}

function openCreate() {
  if (!isAdmin.value) return
  createOpen.value = true
  if (!form.goodsId && goodsItems.value.length) form.goodsId = goodsItems.value[0].id
  loadUsers()
}

async function save() {
  errorMsg.value = ''
  try {
    const goodsId = Number(form.goodsId)
    const quantity = Number(form.quantity || 0)
    const operatorId = Number(form.operatorId)
    const receiver = String(form.receiver || '').trim()
    if (!goodsId) throw new Error('请选择商品')
    if (!quantity || quantity <= 0) throw new Error('数量必须大于 0')
    if (!operatorId) throw new Error('请选择办理人')
    if (!receiver) throw new Error('请输入领用/收货人')
    await apiOutboundCreate({ goodsId, quantity, operatorId, receiver })
    createOpen.value = false
    await load()
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'save failed'
  }
}

function search() {
  page.value = 1
  load()
}

function resetSearch() {
  query.goodsName = ''
  search()
}

onMounted(async () => {
  await loadUsers()
  await load()
})
</script>

<template>
  <div class="panel">
      <div class="panelHeader">
        <div class="panelTitle">出库管理</div>
        <div class="row">
          <button class="ghostBtn" :disabled="loading" @click="load">刷新</button>
          <button class="primaryBtn" :disabled="!isAdmin" @click="openCreate">新增出库</button>
        </div>
      </div>
    <div class="panelBody">
      <div v-if="errorMsg" class="error">{{ errorMsg }}</div>

      <div class="row" style="margin-bottom: 10px">
        <div class="field">
          <div class="label">商品名称</div>
          <input class="input" v-model="query.goodsName" placeholder="支持模糊查询" @keydown.enter="search" />
        </div>
        <div class="row" style="align-self: flex-end">
          <button class="ghostBtn" @click="search">查询</button>
          <button class="ghostBtn" @click="resetSearch">重置</button>
        </div>
      </div>

      <table class="table">
        <thead>
          <tr>
            <th style="width: 70px">ID</th>
            <th style="width: 110px">商品</th>
            <th>商品名称</th>
            <th style="width: 120px">数量</th>
            <th style="width: 140px">办理人</th>
            <th style="width: 160px">收货人</th>
            <th style="width: 180px">时间</th>
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
            <td>{{ r.quantity }}</td>
            <td>{{ usernameById(r.operatorId) }}</td>
            <td>{{ r.receiver }}</td>
            <td class="muted">{{ r.createTime }}</td>
          </tr>
        </tbody>
      </table>

      <Pager v-model:page="page" v-model:pageSize="pageSize" :total="total" @update:page="load" @update:pageSize="load" />
    </div>
  </div>

  <Modal :open="createOpen" title="新增出库" @close="createOpen = false">
    <div class="grid">
      <div class="field">
        <div class="label">商品</div>
        <select class="select" v-model="form.goodsId">
          <option v-for="g in goodsItems" :key="g.id" :value="g.id">{{ g.name }} (#{{ g.id }})</option>
        </select>
      </div>
      <div class="field">
        <div class="label">数量</div>
        <input class="input" type="number" min="1" step="1" v-model="form.quantity" />
      </div>
      <div class="field" style="grid-column: 1 / -1">
        <div class="label">办理人</div>
        <select class="select" v-model="form.operatorId">
          <option v-for="u in users" :key="u.id" :value="u.id">{{ u.username }} ({{ u.role }})</option>
        </select>
      </div>
      <div class="field" style="grid-column: 1 / -1">
        <div class="label">领用/收货人</div>
        <input class="input" v-model="form.receiver" />
      </div>
    </div>
    <template #footer>
      <button class="ghostBtn" @click="createOpen = false">取消</button>
      <button class="primaryBtn" @click="save">保存</button>
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

.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

@media (max-width: 720px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
