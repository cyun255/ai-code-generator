import { reactive } from 'vue'
import { defineStore } from 'pinia'
import request from '@/request'
import type { ApiResponse } from '@/types'

type UserInfo = {
  id?: number | undefined
  username?: string | undefined
  name?: string | undefined
  avatar?: string | undefined
  profile?: string | undefined
  role?: number | undefined
}

export type LoginInfo = {
  username: string
  password: string
}

export type RegisterInfo = LoginInfo

export const useUserStore = defineStore('user', () => {
  const userInfo = reactive<UserInfo>({})

  const login = async (data: LoginInfo) => {
    const response: ApiResponse<UserInfo> = await request.post('/user/login', data)
    if (response.code === 0 && response.data) {
      userInfo.id = response.data.id
      userInfo.username = response.data.username
      userInfo.name = response.data.name
      userInfo.avatar = response.data.avatar
      userInfo.profile = response.data.profile
      userInfo.role = response.data.role
    } else {
      throw new Error(`${response.message}`)
    }
  }

  const getInfo = async () => {
    const response: ApiResponse<UserInfo> = await request.get(`/user`)
    if (response.code === 0 && response.data) {
      userInfo.id = response.data.id
      userInfo.username = response.data.username
      userInfo.name = response.data.name
      userInfo.avatar = response.data.avatar
      userInfo.profile = response.data.profile
      userInfo.role = response.data.role
    } else {
      throw new Error(`${response.message}`)
    }
  }

  const logout = async () => {
    const response: ApiResponse<void> = await request.post('/user/logout')
    if (response.code !== 0) {
      throw new Error(`${response.message}`)
    }
    userInfo.id = undefined
    userInfo.username = undefined
    userInfo.name = undefined
    userInfo.avatar = undefined
    userInfo.profile = undefined
    userInfo.role = undefined
  }

  const register = async (data: RegisterInfo) => {
    const response: ApiResponse<void> = await request.post('/user/register', data)
    if (response.code !== 0) {
      throw new Error(`${response.message}`)
    }
  }

  return { userInfo, login, getInfo, logout, register }
})
