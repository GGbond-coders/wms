import { ref } from 'vue'

const TOKEN_KEY = 'wms_token'
const USER_KEY = 'wms_user'

function readTokenFromStorage() {
  try {
    return window.localStorage.getItem(TOKEN_KEY) || ''
  } catch {
    return ''
  }
}

const tokenState = ref(readTokenFromStorage())
const userState = ref(readUserFromStorage())

function readUserFromStorage() {
  try {
    const raw = window.localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function tokenRef() {
  return tokenState
}

export function userRef() {
  return userState
}

export function getToken() {
  return tokenState.value || ''
}

export function setToken(token) {
  const next = token || ''
  tokenState.value = next
  try {
    window.localStorage.setItem(TOKEN_KEY, next)
  } catch {
    // ignore
  }
}

export function setUser(user) {
  userState.value = user || null
  try {
    window.localStorage.setItem(USER_KEY, JSON.stringify(userState.value))
  } catch {
    // ignore
  }
}

export function clearToken() {
  tokenState.value = ''
  try {
    window.localStorage.removeItem(TOKEN_KEY)
  } catch {
    // ignore
  }
}

export function clearUser() {
  userState.value = null
  try {
    window.localStorage.removeItem(USER_KEY)
  } catch {
    // ignore
  }
}
