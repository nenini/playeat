import type { UserItem, UserItemDetail, UserItemListResponse } from '../../types/item'
import { apiRequest } from './client'

export const itemApi = {
  async getMyItems(): Promise<UserItem[]> {
    const response = await apiRequest<UserItemListResponse>('/v1/items/me')
    return response.items
  },
  getMyItem(userItemId: number): Promise<UserItemDetail> {
    return apiRequest(`/v1/items/me/${userItemId}`)
  }
}
