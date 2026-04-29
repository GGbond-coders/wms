import { computed, reactive } from 'vue'

function normalizePath(p) {
  if (!p) return '/'
  if (!p.startsWith('/')) return `/${p}`
  return p
}

function getHashPath() {
  const raw = window.location.hash || '#/'
  const p = raw.startsWith('#') ? raw.slice(1) : raw
  return normalizePath(p)
}

const state = reactive({
  path: '/'
})

export function initRouter() {
  state.path = getHashPath()
  window.addEventListener('hashchange', () => {
    state.path = getHashPath()
  })
}

export function routePath() {
  return computed(() => state.path)
}

export function push(path) {
  const next = normalizePath(path)
  if (state.path === next) return
  window.location.hash = `#${next}`
}

