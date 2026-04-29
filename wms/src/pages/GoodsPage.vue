<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import Modal from '../components/Modal.vue'
import Pager from '../components/Pager.vue'
import { apiGoodsCreate, apiGoodsDelete, apiGoodsList, apiGoodsUpdate } from '../api/wms'
import { userRef } from '../auth'

const loading = ref(false)
const errorMsg = ref('')

const query = reactive({
  name: '',
  category: ''
})

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const rows = ref([])

const currentUser = userRef()
const isAdmin = computed(() => String(currentUser.value?.role || '').toUpperCase() === 'ADMIN')

const editorOpen = ref(false)
const editorMode = ref('create') // create|edit
const form = reactive({
  id: null,
  name: '',
  category: '',
  brand: '',
  price: 0,
  unit: ''
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await apiGoodsList({
      name: query.name.trim(),
      category: query.category.trim(),
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

function reset() {
  query.name = ''
  query.category = ''
  page.value = 1
  load()
}

function openCreate() {
  if (!isAdmin.value) return
  editorMode.value = 'create'
  Object.assign(form, { id: null, name: '', category: '', brand: '', price: 0, unit: '' })
  editorOpen.value = true
}

function openEdit(row) {
  if (!isAdmin.value) return
  editorMode.value = 'edit'
  Object.assign(form, {
    id: row.id ?? null,
    name: row.name ?? '',
    category: row.category ?? '',
    brand: row.brand ?? '',
    price: row.price ?? 0,
    unit: row.unit ?? ''
  })
  editorOpen.value = true
}

async function save() {
  errorMsg.value = ''
  try {
    const dto = {
      id: form.id,
      name: form.name.trim(),
      category: form.category.trim(),
      brand: form.brand.trim(),
      price: Number(form.price || 0),
      unit: form.unit.trim()
    }
    if (!dto.name) throw new Error('请输入商品名称')
    if (editorMode.value === 'create') await apiGoodsCreate(dto)
    else await apiGoodsUpdate(dto)
    editorOpen.value = false
    await load()
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'save failed'
  }
}

async function remove(row) {
  if (!isAdmin.value) return
  if (!row || row.id == null) return
  const ok = window.confirm(`确认删除商品 #${row.id}？`)
  if (!ok) return
  errorMsg.value = ''
  try {
    await apiGoodsDelete(row.id)
    await load()
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'delete failed'
  }
}

onMounted(load)
</script>

<template>
  <div class="panel">
      <div class="panelHeader">
        <div class="panelTitle">商品管理</div>
        <div class="row">
          <button class="ghostBtn" :disabled="loading" @click="load">刷新</button>
          <button class="primaryBtn" :disabled="!isAdmin" @click="openCreate">新增</button>
        </div>
      </div>
    <div class="panelBody">
      <div v-if="errorMsg" class="error">{{ errorMsg }}</div>

      <div class="row" style="margin-bottom: 10px">
        <div class="field">
          <div class="label">名称</div>
          <input class="input" v-model="query.name" placeholder="支持模糊查询" @keydown.enter="load" />
        </div>
        <div class="field">
          <div class="label">分类</div>
          <input class="input" v-model="query.category" placeholder="如：食品/耗材" @keydown.enter="load" />
        </div>
        <div class="row" style="align-self: flex-end">
          <button class="ghostBtn" @click="search">查询</button>
          <button class="ghostBtn" @click="reset">重置</button>
        </div>
      </div>

      <table class="table">
        <thead>
          <tr>
            <th style="width: 70px">ID</th>
            <th>名称</th>
            <th>分类</th>
            <th>品牌</th>
            <th style="width: 110px">价格</th>
            <th style="width: 90px">单位</th>
            <th style="width: 160px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="rows.length === 0">
            <td colspan="7" class="muted">暂无数据</td>
          </tr>
          <tr v-for="r in rows" :key="r.id">
            <td>{{ r.id }}</td>
            <td>{{ r.name }}</td>
            <td>{{ r.category }}</td>
            <td>{{ r.brand }}</td>
            <td>{{ r.price }}</td>
            <td>{{ r.unit }}</td>
            <td>
              <div class="row" style="gap: 8px">
                <button class="ghostBtn" :disabled="!isAdmin" @click="openEdit(r)">编辑</button>
                <button class="dangerBtn" :disabled="!isAdmin" @click="remove(r)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <Pager v-model:page="page" v-model:pageSize="pageSize" :total="total" @update:page="load" @update:pageSize="load" />
    </div>
  </div>

  <Modal :open="editorOpen" :title="editorMode === 'create' ? '新增商品' : '编辑商品'" @close="editorOpen = false">
    <div class="grid">
      <div class="field">
        <div class="label">名称</div>
        <input class="input" v-model="form.name" />
      </div>
      <div class="field">
        <div class="label">分类</div>
        <input class="input" v-model="form.category" />
      </div>
      <div class="field">
        <div class="label">品牌</div>
        <input class="input" v-model="form.brand" />
      </div>
      <div class="field">
        <div class="label">价格</div>
        <input class="input" type="number" min="0" step="0.01" v-model="form.price" />
      </div>
      <div class="field">
        <div class="label">单位</div>
        <input class="input" v-model="form.unit" placeholder="如：件/箱/个" />
      </div>
    </div>
    <template #footer>
      <button class="ghostBtn" @click="editorOpen = false">取消</button>
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
