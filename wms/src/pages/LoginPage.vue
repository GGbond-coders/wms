<script setup>
import { ref } from 'vue'
import { apiLogin } from '../api/wms'
import { setToken, setUser } from '../auth'
import { push } from '../router'

const username = ref('admin')
const password = ref('123456')
const loading = ref(false)
const errorMsg = ref('')

async function submit() {
  errorMsg.value = ''
  loading.value = true
  try {
    const data = await apiLogin(username.value.trim(), password.value)
    const token = data && data.token
    const user = data && data.user
    if (!token) throw new Error('login failed: missing token')
    setToken(token)
    if (user) setUser(user)
    push('/dashboard')
  } catch (e) {
    errorMsg.value = e && e.message ? e.message : 'login failed'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="loginWrap">
    <div class="loginCard panel">
      <div class="panelHeader">
        <div class="panelTitle">WMS 登录</div>
      </div>
      <div class="panelBody">
        <div class="field">
          <div class="label">用户名</div>
          <input class="input" v-model="username" autocomplete="username" />
        </div>
        <div class="field" style="margin-top: 10px">
          <div class="label">密码</div>
          <input class="input" type="password" v-model="password" autocomplete="current-password" @keydown.enter="submit" />
        </div>
        <div v-if="errorMsg" class="error" style="margin-top: 10px">{{ errorMsg }}</div>
        <div class="row" style="justify-content: flex-end; margin-top: 12px">
          <button class="primaryBtn" :disabled="loading" @click="submit">{{ loading ? '登录中...' : '登录' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.loginWrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0b1220;
}

.loginCard {
  width: min(420px, 94vw);
}

.error {
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid #fecaca;
  background: #fef2f2;
  color: #991b1b;
  font-size: 13px;
}
</style>
