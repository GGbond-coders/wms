import { ref } from 'vue'
import { apiGoodsList } from '../api/wms'

const cache = ref({
  loaded: false,
  loading: false,
  items: [],
  map: new Map()
})

export function goodsCache() {
  return cache
}

export async function ensureGoodsLoaded() {
  if (cache.value.loaded || cache.value.loading) return
  cache.value.loading = true
  try {
    const page = await apiGoodsList({ page: 1, pageSize: 1000 })
    const items = (page && page.rows) || []
    const map = new Map()
    items.forEach((g) => {
      if (g && g.id != null) map.set(Number(g.id), g)
    })
    cache.value.items = items
    cache.value.map = map
    cache.value.loaded = true
  } finally {
    cache.value.loading = false
  }
}

export function goodsNameById(id) {
  const n = Number(id)
  return cache.value.map.get(n)?.name || `#${id}`
}

