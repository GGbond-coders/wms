<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import Modal from '../components/Modal.vue'
import Pager from '../components/Pager.vue'
import { apiLogsCreate, apiLogsList } from '../api/wms'
import { userRef } from '../auth'

const loading = ref(false)
const errorMsg = ref('')

const query = reactive({
  username: '',
  type: ''
})

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const rows = ref([])

const currentUser = userRef()
const isAdmin = computed(() => String(currentUser.value?.role || '').toUpperCase() === 'ADMIN')

const createOpen = ref(false)
const form = reactive({
  username: 'admin',
  operation: '',
  content: ''
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await apiLogsList({
      username: query.username.trim(),
      type: query.type.trim(),
      page: page.value,
      pageSize: pageSize.value
    })
    rows.value = (data && data.rows) || []
    total.value = Number((data && data.total) || 0)
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'load failed'
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  load()
}

function resetSearch() {
  query.username = ''
  query.type = ''
  search()
}

function openCreate() {
  if (!isAdmin.value) return
  createOpen.value = true
  form.operation = ''
  form.content = ''
}

async function save() {
  errorMsg.value = ''
  try {
    const username = String(form.username || '').trim()
    const operation = String(form.operation || '').trim()
    const content = String(form.content || '').trim()
    if (!username) throw new Error('请输入用户名')
    if (!operation) throw new Error('请输入操作类型')
    if (!content) throw new Error('请输入内容')
    await apiLogsCreate({ username, operation, content })
    createOpen.value = false
    await load()
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'save failed'
  }
}

onMounted(load)
</script>

<template>
  <div class="panel">
    <div class="panelHeader">
      <div class="panelTitle">操作日志</div>
      <div class="row">
        <button class="ghostBtn" :disabled="loading" @click="load">刷新</button>
        <button class="primaryBtn" :disabled="!isAdmin" @click="openCreate">新增日志</button>
      </div>
    </div>
    <div class="panelBody">
      <div v-if="errorMsg" class="error">{{ errorMsg }}</div>

      <div class="row" style="margin-bottom: 10px">
        <div class="field">
          <div class="label">用户名</div>
          <input class="input" v-model="query.username" placeholder="精确匹配" @keydown.enter="search" />
        </div>
        <div class="field">
          <div class="label">操作类型</div>
          <input class="input" v-model="query.type" placeholder="匹配 operation 字段" @keydown.enter="search" />
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
            <th style="width: 140px">用户名</th>
            <th style="width: 140px">操作</th>
            <th>内容</th>
            <th style="width: 180px">时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="rows.length === 0">
            <td colspan="5" class="muted">暂无数据</td>
          </tr>
          <tr v-for="r in rows" :key="r.id">
            <td>{{ r.id }}</td>
            <td>{{ r.username }}</td>
            <td><span class="pill">{{ r.operation }}</span></td>
            <td style="white-space: normal">{{ r.content }}</td>
            <td class="muted">{{ r.createTime }}</td>
          </tr>
        </tbody>
      </table>

      <Pager v-model:page="page" v-model:pageSize="pageSize" :total="total" @update:page="load" @update:pageSize="load" />
    </div>
  </div>

  <Modal :open="createOpen" title="新增操作日志" @close="createOpen = false">
    <div class="grid">
      <div class="field">
        <div class="label">用户名</div>
        <input class="input" v-model="form.username" />
      </div>
      <div class="field">
        <div class="label">操作类型</div>
        <input class="input" v-model="form.operation" placeholder="如：LOGIN/UPDATE_STOCK" />
      </div>
      <div class="field" style="grid-column: 1 / -1">
        <div class="label">内容</div>
        <textarea class="input" rows="4" style="height: auto; padding: 8px 10px" v-model="form.content" />
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
