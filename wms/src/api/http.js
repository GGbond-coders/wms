import { getToken, clearToken } from '../auth'
import { push } from '../router'

const baseURL = (import.meta.env && import.meta.env.VITE_API_BASE) || 'http://localhost:8080'

function joinUrl(path) {
  if (!path) return baseURL
  if (path.startsWith('http://') || path.startsWith('https://')) return path
  if (path.startsWith('/')) return `${baseURL}${path}`
  return `${baseURL}/${path}`
}

async function parseJsonSafe(res) {
  const text = await res.text()
  if (!text) return null
  try {
    return JSON.parse(text)
  } catch {
    return { code: res.status, msg: text, data: null }
  }
}

export async function request(path, { method = 'GET', params, body, headers } = {}) {
  const url = new URL(joinUrl(path))
  if (params && typeof params === 'object') {
    Object.entries(params).forEach(([k, v]) => {
      if (v === undefined || v === null || v === '') return
      url.searchParams.set(k, String(v))
    })
  }

  const token = getToken()
  const h = new Headers(headers || {})
  if (token) h.set('Authorization', `Bearer ${token}`)

  const init = {
    method,
    headers: h
  }

  if (body !== undefined) {
    init.body = body
  }

  const res = await fetch(url.toString(), init)
  if (res.status === 401) {
    clearToken()
    push('/login')
    const payload = await parseJsonSafe(res)
    throw new Error((payload && payload.msg) || 'Unauthorized')
  }

  const payload = await parseJsonSafe(res)
  if (payload && typeof payload.code !== 'undefined' && payload.code !== 1) {
    throw new Error(payload.msg || 'Request failed')
  }
  return payload ? payload.data : null
}

export function formBody(obj) {
  const p = new URLSearchParams()
  Object.entries(obj || {}).forEach(([k, v]) => {
    if (v === undefined || v === null) return
    p.set(k, String(v))
  })
  return p
}

export function jsonBody(obj) {
  return JSON.stringify(obj || {})
}

