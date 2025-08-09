export type ApiResponse<T> = {
  code: number // 状态码
  message: string // 响应消息
  data?: T // 响应数据
}

export type PageResponse<T> = {
  records: T[] // 数据记录
  pageNumber: number // 当前页码
  pageSize: number // 每页条数
  totalPage: number // 总页数
  totalRow: number // 总记录数
}
