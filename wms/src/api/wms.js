import { formBody, jsonBody, request } from './http'

export async function apiLogin(username, password) {
  return request('/login', {
    method: 'POST',
    body: formBody({ username, password }),
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export async function apiUserInfo() {
  return request('/user/info')
}

export async function apiUsers() {
  return request('/users')
}

export async function apiGoodsList({ name, category, page, pageSize }) {
  return request('/goods', { params: { name, category, page, pageSize } })
}

export async function apiGoodsGet(id) {
  return request(`/goods/${id}`)
}

export async function apiGoodsCreate(dto) {
  return request('/goods', { method: 'POST', body: jsonBody(dto), headers: { 'Content-Type': 'application/json' } })
}

export async function apiGoodsUpdate(dto) {
  return request('/goods', { method: 'PUT', body: jsonBody(dto), headers: { 'Content-Type': 'application/json' } })
}

export async function apiGoodsDelete(id) {
  return request(`/goods/${id}`, { method: 'DELETE' })
}

export async function apiStockList({ goodsName, lowStock }) {
  return request('/stock', { params: { goodsName, lowStock } })
}

export async function apiStockSetSafe({ goodsId, safeStock }) {
  return request('/stock/safe', {
    method: 'PUT',
    body: formBody({ goodsId, safeStock }),
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export async function apiInboundList({ goodsName, page, pageSize }) {
  return request('/inbound', { params: { goodsName, page, pageSize } })
}

export async function apiInboundCreate({ goodsId, quantity, operatorId }) {
  return request('/inbound', {
    method: 'POST',
    body: formBody({ goodsId, quantity, operatorId }),
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export async function apiOutboundList({ goodsName, page, pageSize }) {
  return request('/outbound', { params: { goodsName, page, pageSize } })
}

export async function apiOutboundCreate({ goodsId, quantity, operatorId, receiver }) {
  return request('/outbound', {
    method: 'POST',
    body: formBody({ goodsId, quantity, operatorId, receiver }),
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export async function apiWarningLowStock({ page, pageSize }) {
  return request('/warning/low-stock', { params: { page, pageSize } })
}

export async function apiLogsList({ username, type, page, pageSize }) {
  return request('/logs', { params: { username, type, page, pageSize } })
}

export async function apiLogsCreate({ username, operation, content }) {
  return request('/logs', {
    method: 'POST',
    body: formBody({ username, operation, content }),
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export async function apiStatsStock() {
  return request('/statistics/stock')
}

export async function apiStatsIO() {
  return request('/statistics/io')
}

export async function apiStatsCategoryShare() {
  return request('/statistics/category-share')
}

export async function apiStatsTrend30d() {
  return request('/statistics/trend-30d')
}
