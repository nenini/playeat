import type { PageId } from '../services/mock/nyamnyamMock'

export function pageFromPath(path: string): PageId {
  if (path === '/' || path === '/home') return 'home'
  if (path === '/meals') return 'meals'
  if (path === '/analyze') return 'analyze'
  if (path === '/boss') return 'boss'
  if (path === '/guild') return 'guild'
  if (path === '/shop') return 'shop'
  if (path === '/mypage') return 'mypage'
  return 'home'
}

export function pathFromPage(page: PageId) {
  return page === 'home' ? '/' : `/${page}`
}
