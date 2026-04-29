<script setup>
import { computed, ref, watchEffect } from 'vue'
import { clearToken, clearUser, tokenRef, userRef } from './auth'
import { push, routePath } from './router'
import Modal from './components/Modal.vue'

import LoginPage from './pages/LoginPage.vue'
import DashboardPage from './pages/DashboardPage.vue'
import GoodsPage from './pages/GoodsPage.vue'
import StockPage from './pages/StockPage.vue'
import InboundPage from './pages/InboundPage.vue'
import OutboundPage from './pages/OutboundPage.vue'
import WarningPage from './pages/WarningPage.vue'
import LogsPage from './pages/LogsPage.vue'

const nav = [
  { path: '/dashboard', label: '首页' },
  { path: '/goods', label: '商品' },
  { path: '/stock', label: '库存' },
  { path: '/inbound', label: '入库' },
  { path: '/outbound', label: '出库' },
  { path: '/warning', label: '预警' },
  { path: '/logs', label: '日志' }
]

const title = 'WMS'
const sidebarOpen = ref(true)
const profileOpen = ref(false)

const currentPath = routePath()
const token = tokenRef()
const user = userRef()
const isAuthed = computed(() => !!token.value)
const isLogin = computed(() => currentPath.value === '/login')

const currentView = computed(() => {
  const p = currentPath.value
  if (p === '/login') return LoginPage
  if (p === '/goods') return GoodsPage
  if (p === '/stock') return StockPage
  if (p === '/inbound') return InboundPage
  if (p === '/outbound') return OutboundPage
  if (p === '/warning') return WarningPage
  if (p === '/logs') return LogsPage
  return DashboardPage
})

watchEffect(() => {
  const p = currentPath.value
  if (p === '/' || p === '') push('/dashboard')
  if (!isAuthed.value && p !== '/login') push('/login')
  if (isAuthed.value && p === '/login') push('/dashboard')
})

function logout() {
  clearToken()
  clearUser()
  push('/login')
}
</script>

<template>
  <div class="app">
    <div v-if="!isLogin" class="topbar">
      <button class="iconBtn" :aria-label="sidebarOpen ? '收起侧边栏' : '展开侧边栏'" @click="sidebarOpen = !sidebarOpen">
        <span class="icon" aria-hidden="true">{{ sidebarOpen ? '⟨' : '⟩' }}</span>
      </button>
      <div class="topTitle">{{ title }}</div>
      <div class="spacer" />
      <button class="btn" @click="profileOpen = true">用户</button>
      <button class="btn" @click="logout">退出</button>
    </div>

    <div class="body">
      <aside v-if="!isLogin" class="sidebar" :class="{ closed: !sidebarOpen }">
        <nav class="nav">
          <a
            v-for="item in nav"
            :key="item.path"
            class="navItem"
            :class="{ active: currentPath === item.path }"
            :href="`#${item.path}`"
            @click.prevent="push(item.path)"
          >
            <span class="dot" aria-hidden="true" />
            <span class="label">{{ item.label }}</span>
          </a>
        </nav>
      </aside>

      <main class="main" :class="{ wide: isLogin }">
        <component :is="currentView" />
      </main>
    </div>
  </div>

  <Modal :open="profileOpen" title="用户信息" @close="profileOpen = false">
    <div v-if="user" class="field">
      <div class="label">用户名</div>
      <div class="pill">{{ user.username }}</div>
    </div>
    <div v-if="user" class="field" style="margin-top: 10px">
      <div class="label">角色</div>
      <div class="pill">{{ user.role }}</div>
    </div>
    <div v-if="!user" class="muted">未获取到用户信息</div>
    <template #footer>
      <button class="ghostBtn" @click="profileOpen = false">关闭</button>
    </template>
  </Modal>
</template>

<style scoped>
.app {
  min-height: 100vh;
  background: #f6f7fb;
  color: #0f172a;
}

.topbar {
  height: 52px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
}

.topTitle {
  font-weight: 650;
  letter-spacing: 0;
}

.spacer {
  flex: 1;
}

.iconBtn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  cursor: pointer;
}

.icon {
  font-size: 14px;
}

.btn {
  height: 32px;
  padding: 0 12px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  cursor: pointer;
}

.body {
  display: flex;
  min-height: calc(100vh - 52px);
}

.sidebar {
  width: 210px;
  border-right: 1px solid #e5e7eb;
  background: #ffffff;
  padding: 10px;
  transition: width 180ms ease;
}

.sidebar.closed {
  width: 54px;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.navItem {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 10px;
  border-radius: 6px;
  color: inherit;
  text-decoration: none;
}

.navItem:hover {
  background: #f3f4f6;
}

.navItem.active {
  background: #eef2ff;
  border: 1px solid #e0e7ff;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #94a3b8;
  flex: 0 0 auto;
}

.navItem.active .dot {
  background: #4f46e5;
}

.sidebar.closed .label {
  display: none;
}

.main {
  flex: 1;
  padding: 14px;
}

.main.wide {
  padding: 0;
}
</style>

