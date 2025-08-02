export type ApiResponse<T> = {
  code: number // 状态码
  message: string // 响应消息
  data?: T // 响应数据
}
