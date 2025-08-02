import axios from 'axios'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 10000, // 请求超时时间
  withCredentials: true, // 是否携带凭证
})

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 处理响应数据
    if (response.status === 200) {
      return response.data
    } else {
      // 处理非200响应
      return Promise.reject(new Error(response.statusText))
    }
  },
  (error) => {
    // 处理请求错误
    return Promise.reject(error)
  },
)

export default request
