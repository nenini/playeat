import { characterApi } from './characterApi'
import { userApi } from './userApi'

export const myPageApi = {
  async getMyPageOverview() {
    const [user, healthProfile, character] = await Promise.all([
      userApi.getMe(),
      userApi.getHealthProfile(),
      characterApi.getMe()
    ])

    return { user, healthProfile, character }
  }
}
