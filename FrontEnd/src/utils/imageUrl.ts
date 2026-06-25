const API_BASE_URL = String(import.meta.env.VITE_API_BASE_URL || '/api').trim().replace(/\/+$/, '')

export function resolveImageUrl(value: unknown) {
  if (typeof value !== 'string') return ''
  const path = value.trim()
  if (!path || path === 'null' || path === 'undefined') return ''
  if (/^https?:\/\//i.test(path) || path.startsWith('blob:')) return path
  if (!path.startsWith('/uploads/') && !path.startsWith('/api/uploads/')) return ''

  return resolveApiAssetUrl(path)
}

export function resolveApiAssetUrl(value: unknown) {
  if (typeof value !== 'string') return ''
  const path = value.trim()
  if (!path || path === 'null' || path === 'undefined') return ''
  if (/^https?:\/\//i.test(path) || path.startsWith('blob:')) return path
  if (!path.startsWith('/')) return ''

  try {
    if (/^https?:\/\//i.test(API_BASE_URL)) {
      const base = new URL(API_BASE_URL)
      const contextPath = base.pathname.replace(/\/+$/, '')
      const assetPath = path.startsWith(`${contextPath}/`) ? path : `${contextPath}${path}`
      return `${base.origin}${assetPath}`
    }

    const basePath = API_BASE_URL.startsWith('/') ? API_BASE_URL : `/${API_BASE_URL}`
    return path.startsWith(`${basePath}/`) ? path : `${basePath}${path}`
  } catch {
    return ''
  }
}
