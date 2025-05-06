import axios from 'axios'
import { API } from '@/config/api'

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/ishareReading',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['token'] = token
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response
  },
  error => {
    console.error('Response error:', error)
    return Promise.reject(error)
  }
)

// 处理流式响应
export const handleStreamResponse = async (url, data, onData, abortController) => {
  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'token': localStorage.getItem('token')
      },
      signal: abortController.signal
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        if (line.startsWith('data:')) {
          try {
            const jsonStr = line.slice(5).trim()
            if (jsonStr) {
              const data = JSON.parse(jsonStr)
              if (data.results && data.results[0]?.output?.text) {
                onData(data.results[0].output.text)
              }
            }
          } catch (e) {
            console.error('解析响应数据失败:', e)
          }
        }
      }
    }
  } catch (error) {
    if (error.name === 'AbortError') {
      console.log('请求被取消')
    } else {
      console.error('Stream error:', error)
      throw error
    }
  }
}

export default service 