import { API } from '@/config/api'
import request from '@/utils/request'

// 创建新会话
export function createSession(data) {
  return request({
    url: API.CHAT.CREATE_SESSION,
    method: 'post',
    data
  })
}

// 获取会话列表
export function getSessions() {
  return request({
    url: API.CHAT.GET_SESSIONS,
    method: 'get'
  })
}

// 获取会话消息历史
export function getMessages(sessionId) {
  return request({
    url: API.CHAT.GET_MESSAGES(sessionId),
    method: 'get'
  })
}

// 发送消息
export function sendMessage(data) {
  return request({
    url: API.CHAT.SEND_MESSAGE,
    method: 'post',
    data
  })
}

// 删除会话
export function deleteSession(sessionId) {
  return request({
    url: API.CHAT.DELETE_SESSION(sessionId),
    method: 'delete'
  })
}

// 更新会话标题
export function updateSessionTitle(data) {
  return request({
    url: API.CHAT.UPDATE_SESSION_TITLE,
    method: 'put',
    data
  })
}

// 获取流式响应
export function getStreamResponse(data) {
  return request({
    url: API.CHAT.STREAM_RESPONSE,
    method: 'post',
    data,
    responseType: 'stream'
  })
} 