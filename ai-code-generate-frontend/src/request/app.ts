import type { ApiResponse, PageResponse } from '@/types'
import request from '.'
import type { UserVO } from '@/stores/user'

export type AppInfo = {
  id: string
  name: string
  cover: string | null
  initPrompt: string
  codeGenType: string
  deployKey: string | null
  deployTime: string | null
  priority: number
  userId: string
  createTime: string
  updateTime: string
  isDelete: number
  userVO: UserVO
}

export const PageUserApps: (
  current: number,
  pageSize: number,
) => Promise<ApiResponse<PageResponse<AppInfo>>> = async (current: number, pageSize: number) => {
  return await request.get('/app/page', {
    params: {
      current,
      pageSize,
    },
  })
}

export const GetAppById: (id: string) => Promise<ApiResponse<AppInfo>> = async (id: string) => {
  return await request.get(`/app/${id}`)
}
