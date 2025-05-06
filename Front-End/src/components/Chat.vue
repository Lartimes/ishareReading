<template>
  <div class="chat-container">
    <div class="chat-messages" ref="messagesContainer">
      <!-- 欢迎信息 -->
      <div v-if="!messages.length" class="welcome-message">
        <div class="welcome-content">
          <div class="welcome-icon">🤖</div>
          <h2 class="welcome-title">IshareReading AI 聊天解说小助手</h2>
          <p class="welcome-text">你好！我是你的阅读助手，可以帮你分析文本内容，提供多维度见解。</p>
          <p class="welcome-text">请选择聊天模式或多维度鉴赏模式开始对话。</p>
        </div>
      </div>
      <!-- 消息列表 -->
      <div v-else class="message-list">
        <div 
          v-for="(message, index) in messages" 
          :key="index" 
          class="message" 
          :class="[message.role, message.agentType]"
        >
          <div class="avatar" :class="message.role">
            <svg v-if="message.role === 'assistant'" viewBox="0 0 24 24" width="24" height="24">
              <path v-if="message.agentType === 'appreciator'" fill="currentColor" d="M21 5c-1.11-.35-2.33-.5-3.5-.5-1.95 0-4.05.4-5.5 1.5-1.45-1.1-3.55-1.5-5.5-1.5S2.45 4.9 1 6v14.65c0 .25.25.5.5.5.1 0 .15-.05.25-.05C3.1 20.45 5.05 20 6.5 20c1.95 0 4.05.4 5.5 1.5 1.35-.85 3.8-1.5 5.5-1.5 1.65 0 3.35.3 4.75 1.05.1.05.15.05.25.05.25 0 .5-.25.5-.5V6c-.6-.45-1.25-.75-2-1zm0 13.5c-1.1-.35-2.3-.5-3.5-.5-1.7 0-4.15.65-5.5 1.5V8c1.35-.85 3.8-1.5 5.5-1.5 1.2 0 2.4.15 3.5.5v11.5z"/>
              <path v-else-if="message.agentType === 'neutralist'" fill="currentColor" d="M13 7h-2v2h2V7zm0 4h-2v6h2v-6zm4-9.99L7 1c-1.1 0-2 .9-2 2v18c0 1.1.9 2 2 2h10c1.1 0 2-.9 2-2V3c0-1.1-.9-1.99-2-1.99zM17 19H7V5h10v14z"/>
              <path v-else-if="message.agentType === 'dialectician'" fill="currentColor" d="M21 6h-2v9H6v2c0 .55.45 1 1 1h11l4 4V7c0-.55-.45-1-1-1zm-4 6V3c0-.55-.45-1-1-1H3c-.55 0-1 .45-1 1v14l4-4h10c.55 0 1-.45 1-1z"/>
              <!-- 默认AI助手图标 - 机器人 -->
              <path v-else fill="currentColor" d="M20 2H4c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM7.76 16.24l-1.41 1.41C4.78 16.1 4 14.05 4 12c0-2.05.78-4.1 2.34-5.66l1.41 1.41C6.59 8.93 6 10.46 6 12s.59 3.07 1.76 4.24zM12 16c-2.21 0-4-1.79-4-4s1.79-4 4-4 4 1.79 4 4-1.79 4-4 4zm5.66 1.66l-1.41-1.41C17.41 15.07 18 13.54 18 12s-.59-3.07-1.76-4.24l1.41-1.41C19.22 7.9 20 9.95 20 12c0 2.05-.78 4.1-2.34 5.66zM12 10c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
            </svg>
            <span v-else>You</span>
          </div>
          <div class="message-content">
            <div v-if="message.role === 'assistant'" class="agent-info">
              <span class="agent-name">{{ getAgentName(message.agentType) }}</span>
            </div>
            <div v-if="message.role === 'assistant'" class="markdown-body" v-html="renderMarkdown(message.content)"></div>
            <div v-else>{{ message.content }}</div>
          </div>
        </div>
        <div v-if="isStreaming" class="message assistant">
          <div class="avatar">
            <svg viewBox="0 0 24 24" width="24" height="24">
              <!-- 智能机器人图标 -->
              <path fill="currentColor" d="M20 2H4c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM7.76 16.24l-1.41 1.41C4.78 16.1 4 14.05 4 12c0-2.05.78-4.1 2.34-5.66l1.41 1.41C6.59 8.93 6 10.46 6 12s.59 3.07 1.76 4.24zM12 16c-2.21 0-4-1.79-4-4s1.79-4 4-4 4 1.79 4 4-1.79 4-4 4zm5.66 1.66l-1.41-1.41C17.41 15.07 18 13.54 18 12s-.59-3.07-1.76-4.24l1.41-1.41C19.22 7.9 20 9.95 20 12c0 2.05-.78 4.1-2.34 5.66zM12 10c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
            </svg>
          </div>
          <div class="message-content">
            <div class="markdown-body" v-html="renderMarkdown(streamContent)"></div>
            <div class="cursor"></div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="input-section">
      <div class="input-container">
        <div class="input-wrapper">
          <div class="mode-selector">
            <button 
              v-for="mode in modes" 
              :key="mode.value"
              :class="['mode-button', { active: selectedMode === mode.value }]"
              @click="selectedMode = mode.value"
            >
              <svg v-if="mode.value === 'chat'" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>
              </svg>
              <svg v-else-if="mode.value === 'image'" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/>
              </svg>
              <svg v-else-if="mode.value === 'video'" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M17 10.5V7c0-.55-.45-1-1-1H4c-.55 0-1 .45-1 1v10c0 .55.45 1 1 1h12c.55 0 1-.45 1-1v-3.5l4 4v-11l-4 4z"/>
              </svg>
              <svg v-else-if="mode.value === 'appreciation'" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M12 2L4.5 20.29l.71.71L12 18l6.79 3 .71-.71z"/>
              </svg>
              {{ mode.label }}
            </button>
            <!-- 添加导入知识库按钮 -->
            <button 
              class="mode-button import-knowledge-btn"
              @click="showImportDialog = true"
            >
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
              </svg>
              导入知识库
            </button>
          </div>
          <!-- 添加文件上传按钮 -->
          <div v-if="selectedMode === 'image' || selectedMode === 'video'" class="file-upload">
            <input
              type="file"
              ref="fileInput"
              :accept="selectedMode === 'image' ? 'image/*' : 'video/*'"
              style="display: none"
              @change="handleFileUpload"
            >
            <button class="upload-button" @click="triggerFileUpload">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
              </svg>
              {{ selectedMode === 'image' ? '上传图片' : '上传视频' }}
            </button>
          </div>
          <textarea
            v-model="userInput"
            @keydown.enter.prevent="sendMessage"
            :placeholder="selectedMode === 'image' ? '描述图片内容...' : selectedMode === 'video' ? '描述视频内容...' : 'Send a message...'"
            rows="1"
            ref="textarea"
          ></textarea>
          <button @click="sendMessage" :disabled="isStreaming || !userInput.trim()" class="send-button">
            <svg viewBox="0 0 24 24" class="send-icon">
              <path fill="currentColor" d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 导入知识库对话框 -->
    <div v-if="showImportDialog" class="import-dialog-overlay" @click="showImportDialog = false">
      <div class="import-dialog" @click.stop>
        <div class="import-dialog-header">
          <h3>导入知识库</h3>
          <button class="close-button" @click="showImportDialog = false">×</button>
        </div>
        <div class="import-dialog-content">
          <div class="import-upload">
            <div class="upload-area" @click="triggerKnowledgeUpload">
              <input 
                type="file" 
                ref="knowledgeFileInput"
                accept=".txt,.pdf,.doc,.docx"
                style="display: none"
                @change="handleKnowledgeUpload"
              >
              <div class="upload-icon">📚</div>
              <div class="upload-text">点击或拖拽文件到此处</div>
              <div class="upload-hint">支持 TXT、PDF、DOC、DOCX 格式</div>
            </div>
            <div v-if="selectedKnowledgeFile" class="selected-file">
              <span>{{ selectedKnowledgeFile.name }}</span>
              <button @click="removeKnowledgeFile">删除</button>
            </div>
          </div>
          <div class="import-actions">
            <button class="cancel-btn" @click="showImportDialog = false">取消</button>
            <button class="submit-btn" @click="submitKnowledgeImport" :disabled="!selectedKnowledgeFile">导入</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'
import { handleStreamResponse } from '@/utils/request'
import { API } from '@/config/api'

const modes = [
  { label: '聊天', value: 'chat' },
  { label: '图片理解', value: 'image' },
  { label: '视频理解', value: 'video' },
  { label: '多维度鉴赏', value: 'appreciation' }
]

const selectedMode = ref('chat')
const md = new MarkdownIt({
  html: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch (__) {}
    }
    return ''
  }
})

const messages = ref([])
const streamContent = ref('')
const isStreaming = ref(false)
const userInput = ref('')
const messagesContainer = ref(null)
const textarea = ref(null)
const abortController = ref(null)
const fileInput = ref(null)
const selectedFile = ref(null)
const showImportDialog = ref(false)
const knowledgeFileInput = ref(null)
const selectedKnowledgeFile = ref(null)

const renderMarkdown = (content) => {
  return md.render(content)
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const mockStreamResponse = async (text) => {
  const words = text.split(' ')
  isStreaming.value = true
  streamContent.value = ''
  
  for (const word of words) {
    streamContent.value += word + ' '
    await new Promise(resolve => setTimeout(resolve, 100))
    await scrollToBottom()
  }
  
  messages.value.push({
    role: 'assistant',
    content: streamContent.value.trim()
  })
  
  streamContent.value = ''
  isStreaming.value = false
}

// 触发文件选择
const triggerFileUpload = () => {
  fileInput.value.click()
}

// 处理文件上传
const handleFileUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  if (selectedMode.value === 'image') {
    // 检查图片文件类型
    if (!file.type.startsWith('image/')) {
      alert('请选择图片文件')
      return
    }
    // 检查图片文件大小（限制为10MB）
    if (file.size > 10 * 1024 * 1024) {
      alert('图片大小不能超过10MB')
      return
    }
    selectedFile.value = file
    userInput.value = '这些是什么？' // 默认提示词
  } else if (selectedMode.value === 'video') {
    // 检查视频文件类型
    if (!file.type.startsWith('video/')) {
      alert('请选择视频文件')
      return
    }
    // 检查视频文件大小（限制为100MB）
    if (file.size > 100 * 1024 * 1024) {
      alert('视频大小不能超过100MB')
      return
    }
    selectedFile.value = file
    userInput.value = '这是一组从视频中提取的图片帧，请描述此视频中的内容。' // 默认提示词
  }
}

// 发送视频识别请求
const sendVideoRequest = async () => {
  if (!selectedFile.value) return

  try {
    isStreaming.value = true
    streamContent.value = ''

    const formData = new FormData()
    formData.append('prompt', userInput.value)
    formData.append('file', selectedFile.value)

    const response = await fetch('http://localhost:8080/ishareReading/multiMode/stream/video', {
      method: 'POST',
      headers: {
        'token': localStorage.getItem('token')
      },
      body: formData
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
                streamContent.value += data.results[0].output.text
                scrollToBottom()
              }
            }
          } catch (e) {
            console.error('解析响应数据失败:', e)
          }
        }
      }
    }

    messages.value.push({
      role: 'assistant',
      content: streamContent.value.trim()
    })
  } catch (error) {
    console.error('视频识别失败:', error)
    messages.value.push({
      role: 'assistant',
      content: '抱歉，视频识别失败，请重试。'
    })
  } finally {
    streamContent.value = ''
    isStreaming.value = false
    selectedFile.value = null
    userInput.value = ''
  }
}

const sendMessage = async () => {
  if (!userInput.value.trim() || isStreaming.value) return
  
  // 取消之前的请求
  if (abortController.value) {
    abortController.value.abort()
  }
  
  const message = userInput.value.trim()
  messages.value.push({
    role: 'user',
    content: message
  })
  
  userInput.value = ''
  await scrollToBottom()
  
  if (selectedMode.value === 'chat') {
    try {
      isStreaming.value = true
      streamContent.value = ''
      
      abortController.value = new AbortController()
      
      await handleStreamResponse(
        API.CHAT.CHAT_TEST(message, 1),
        null,
        (chunk) => {
          streamContent.value += chunk
          scrollToBottom()
        },
        abortController.value
      )
      
      messages.value.push({
        role: 'assistant',
        content: streamContent.value.trim()
      })
    } catch (error) {
      if (error.name !== 'AbortError') {
        console.error('发送消息失败:', error)
        messages.value.push({
          role: 'assistant',
          content: '抱歉，发送消息失败，请重试。'
        })
      }
    } finally {
      streamContent.value = ''
      isStreaming.value = false
      abortController.value = null
    }
  } else if (selectedMode.value === 'appreciation') {
    try {
      isStreaming.value = true
      const agents = [
        { name: 'appreciator', title: '赏析者' },
        { name: 'neutralist', title: '中立者' },
        { name: 'dialectician', title: '辩证者' }
      ]
      
      for (const agent of agents) {
        streamContent.value = ''
        abortController.value = new AbortController()
        
        const formData = new FormData()
        formData.append('sessionId', '1')
        formData.append('agentName', agent.name)
        formData.append('userInput', message)
        
        const response = await fetch('http://localhost:8080/ishareReading/agents/readingByName', {
          method: 'POST',
          headers: {
            'token': localStorage.getItem('token')
          },
          body: formData
        })
        
        if (!response.ok) {
          throw new Error(`请求失败: ${response.status}`)
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
                  if (data.results?.[0]?.output?.text) {
                    streamContent.value += data.results[0].output.text
                    scrollToBottom()
                  }
                }
              } catch (e) {
                console.error('解析数据失败:', e)
              }
            }
          }
        }
        
        messages.value.push({
          role: 'assistant',
          content: streamContent.value.trim(),
          agentType: agent.name
        })
      }
    } catch (error) {
      if (error.name !== 'AbortError') {
        console.error('发送消息失败:', error)
        messages.value.push({
          role: 'assistant',
          content: '抱歉，发送消息失败，请重试。'
        })
      }
    } finally {
      streamContent.value = ''
      isStreaming.value = false
      abortController.value = null
    }
  } else if (selectedMode.value === 'image') {
    if (selectedFile.value) {
      await sendImageRequest()
    } else {
      messages.value.push({
        role: 'assistant',
        content: '请先上传图片'
      })
    }
  } else if (selectedMode.value === 'video') {
    if (selectedFile.value) {
      await sendVideoRequest()
    } else {
      messages.value.push({
        role: 'assistant',
        content: '请先上传视频'
      })
    }
  } else {
    // 其他模式暂时显示提示信息
    messages.value.push({
      role: 'assistant',
      content: '该功能正在开发中，敬请期待...'
    })
  }
}

// Auto-resize textarea
const adjustTextarea = () => {
  if (!textarea.value) return
  textarea.value.style.height = 'auto'
  textarea.value.style.height = textarea.value.scrollHeight + 'px'
}

onMounted(() => {
  if (textarea.value) {
    textarea.value.addEventListener('input', adjustTextarea)
  }
})

// 触发知识库文件选择
const triggerKnowledgeUpload = () => {
  knowledgeFileInput.value.click()
}

// 处理知识库文件上传
const handleKnowledgeUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 检查文件类型
  const allowedTypes = ['.txt', '.pdf', '.doc', '.docx']
  const fileExtension = file.name.toLowerCase().substring(file.name.lastIndexOf('.'))
  if (!allowedTypes.includes(fileExtension)) {
    alert('请选择支持的文件格式：TXT、PDF、DOC、DOCX')
    return
  }

  // 检查文件大小（限制为50MB）
  if (file.size > 50 * 1024 * 1024) {
    alert('文件大小不能超过50MB')
    return
  }

  selectedKnowledgeFile.value = file
}

// 删除已选文件
const removeKnowledgeFile = () => {
  selectedKnowledgeFile.value = null
  knowledgeFileInput.value.value = ''
}

// 提交知识库导入
const submitKnowledgeImport = async () => {
  if (!selectedKnowledgeFile.value) return

  try {
    const formData = new FormData()
    formData.append('file', selectedKnowledgeFile.value)

    const response = await fetch(API.KNOWLEDGE.IMPORT_RESOURCES, {
      method: 'POST',
      headers: {
        'token': localStorage.getItem('token')
      },
      body: formData
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const result = await response.json()
    if (result.code === 200) {
      alert('知识库导入成功')
      showImportDialog.value = false
      selectedKnowledgeFile.value = null
    } else {
      throw new Error(result.msg || '导入失败')
    }
  } catch (error) {
    console.error('知识库导入失败:', error)
    alert('知识库导入失败，请重试')
  }
}

// 添加获取AI助手信息的函数
const fetchAgentInfo = async (name) => {
  try {
    const response = await fetch(API.AGENTS.GET_AGENTS(name), {
      headers: {
        'token': localStorage.getItem('token')
      }
    })
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const result = await response.json()
    if (result.code === 200 && result.data) {
      return result.data.welcomeMessage
    }
    return null
  } catch (error) {
    console.error('获取AI助手信息失败:', error)
    return null
  }
}

// 添加处理多维度赏析模式的函数
const handleAppreciationMode = async () => {
  const agentNames = ['appreciator', 'neutralist', 'dialectician']
  messages.value = [] // 清空现有消息
  
  for (const name of agentNames) {
    const welcomeMessage = await fetchAgentInfo(name)
    if (welcomeMessage) {
      messages.value.push({
        role: 'assistant',
        content: welcomeMessage,
        agentType: name // 添加agentType属性
      })
    }
  }
}

// 监听模式变化
watch(selectedMode, (newMode) => {
  if (newMode === 'appreciation') {
    handleAppreciationMode()
  } else {
    // 切换到其他模式时清空消息
    messages.value = []
    streamContent.value = ''
    isStreaming.value = false
    userInput.value = ''
    if (abortController.value) {
      abortController.value.abort()
      abortController.value = null
    }
  }
})

// 添加获取 agent 中文名字的函数
const getAgentName = (agentType) => {
  const agentNames = {
    'appreciator': '赏析者',
    'neutralist': '中立者',
    'dialectician': '辩证者'
  }
  return agentNames[agentType] || 'AI助手'
}
</script>

<style scoped>
.chat-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  color: #333333;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  background: #ffffff;
}

.welcome-message {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 2rem;
}

.welcome-content {
  text-align: center;
  max-width: 500px;
  padding: 2rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
}

.welcome-icon {
  font-size: 48px;
  margin-bottom: 1rem;
}

.welcome-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333333;
  margin-bottom: 1rem;
}

.welcome-text {
  font-size: 1rem;
  color: #666666;
  line-height: 1.5;
  margin-bottom: 0.5rem;
}

.message {
  margin-bottom: 1.5rem;
  display: flex;
  gap: 1rem;
  padding: 0.75rem;
  border-radius: 0.5rem;
  transition: background-color 0.2s;
}

.message:hover {
  background-color: #f9fafb;
}

.message-content {
  flex: 1;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  line-height: 1.5;
}

.message.user {
  flex-direction: row-reverse;
}

.message.user .message-content {
  background: #f3f4f6;
  color: #333333;
  border: 1px solid #e5e7eb;
}

.message.assistant .message-content {
  background: #ffffff;
  color: #333333;
  border: 1px solid #e5e7eb;
}

.avatar {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%; /* 改为圆形 */
  background-color: #2563eb;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
  font-weight: 500;
  padding: 6px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 添加阴影 */
  transition: all 0.3s ease; /* 添加过渡效果 */
}

.avatar:hover {
  transform: scale(1.05); /* 悬停时放大效果 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.avatar.appreciator {
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%); /* 紫色渐变背景 */
}

.avatar.neutralist {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%); /* 绿色渐变背景 */
}

.avatar.dialectician {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); /* 金色渐变背景 */
}

.avatar.assistant {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%); /* 蓝色渐变背景 */
}

.avatar.user {
  background: linear-gradient(135deg, #4b5563 0%, #374151 100%); /* 渐变背景 */
}

.avatar svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1)); /* SVG阴影 */
}

.input-section {
  padding: 1.5rem;
  background: #ffffff;
  border-top: 1px solid #e5e7eb;
}

.input-container {
  max-width: 100%;
  margin: 0 auto;
}

.input-wrapper {
  position: relative;
}

.mode-selector {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
  padding: 0.5rem;
  background: #f3f4f6;
  border-radius: 0.5rem;
  flex-wrap: wrap;
}

.mode-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  background-color: #ffffff;
  color: #666666;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.mode-button:hover {
  background-color: #f3f4f6;
  border-color: #2563eb;
  color: #2563eb;
}

.mode-button.active {
  background-color: #2563eb;
  border-color: #2563eb;
  color: #ffffff;
}

.mode-button svg {
  flex-shrink: 0;
}

.mode-button.import-knowledge-btn {
  background-color: #ffffff;
  color: #666666;
  border: 1px solid #e5e7eb;
}

.mode-button.import-knowledge-btn:hover {
  background-color: #f3f4f6;
  border-color: #2563eb;
  color: #2563eb;
}

textarea {
  width: 100%;
  max-height: 200px;
  padding: 0.75rem 3rem 0.75rem 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  background-color: #ffffff;
  color: #333333;
  resize: none;
  outline: none;
  font-family: inherit;
  font-size: 1rem;
  line-height: 1.5;
}

textarea:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
}

.send-button {
  position: absolute;
  right: 0.5rem;
  bottom: 0.5rem;
  padding: 0.5rem;
  border: none;
  border-radius: 0.25rem;
  background: none;
  color: #2563eb;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-button:disabled {
  color: #9ca3af;
  cursor: not-allowed;
}

.send-icon {
  width: 1.25rem;
  height: 1.25rem;
}

.cursor {
  display: inline-block;
  width: 0.5rem;
  height: 1.2rem;
  background-color: #2563eb;
  margin-left: 0.25rem;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* Markdown Styles */
:deep(.markdown-body) {
  color: #333333;
  font-size: 1rem;
}

:deep(.markdown-body pre) {
  background-color: #f8f9fa !important;
  border-radius: 0.5rem;
  padding: 1rem;
  margin: 1rem 0;
  border: 1px solid #e5e7eb;
}

:deep(.markdown-body code) {
  background-color: #f8f9fa;
  padding: 0.2rem 0.4rem;
  border-radius: 0.25rem;
  font-size: 0.875em;
  border: 1px solid #e5e7eb;
  color: #333333;
}

:deep(.markdown-body pre code) {
  padding: 0;
  background-color: transparent;
  border: none;
  color: #333333;
}

:deep(.markdown-body p) {
  margin: 0.5rem 0;
  color: #333333;
}

:deep(.markdown-body ul),
:deep(.markdown-body ol) {
  padding-left: 1.5rem;
  color: #333333;
}

:deep(.markdown-body li) {
  margin: 0.25rem 0;
  color: #333333;
}

/* Scrollbar Styles */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.drawer-toggle {
  position: fixed;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333333;
  transition: all 0.2s;
  z-index: 1000;
}

.drawer-toggle:hover {
  background: #f3f4f6;
  border-color: #2563eb;
  color: #2563eb;
}

.drawer-toggle span {
  font-size: 1.25rem;
  transition: transform 0.2s;
}

.drawer-toggle.active span {
  transform: rotate(180deg);
}

.session-list {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 280px;
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  z-index: 999;
  transform: translateX(-100%);
  transition: transform 0.3s ease;
}

.session-list.active {
  transform: translateX(0);
}

.new-session {
  width: 100%;
  padding: 0.75rem 1rem;
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.new-session:hover {
  background-color: #1d4ed8;
}

.sessions {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.session-item {
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  background-color: #ffffff;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.2s;
}

.session-item:hover {
  background-color: #f3f4f6;
  border-color: #2563eb;
}

.session-item.active {
  background-color: #f3f4f6;
  border-color: #2563eb;
}

.session-item .title {
  font-size: 0.875rem;
  color: #333333;
  margin-bottom: 0.25rem;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-item .date {
  font-size: 0.75rem;
  color: #666666;
}

.sessions::-webkit-scrollbar {
  width: 6px;
}

.sessions::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.sessions::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.sessions::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.file-upload {
  margin-bottom: 1rem;
}

.upload-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.upload-button:hover {
  background-color: #1d4ed8;
}

.upload-button svg {
  width: 1rem;
  height: 1rem;
}

.import-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.import-dialog {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.import-dialog-header {
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.import-dialog-header h3 {
  margin: 0;
  font-size: 1.25rem;
  color: #333333;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #666666;
  cursor: pointer;
  padding: 0.25rem;
}

.import-dialog-content {
  padding: 1.5rem;
}

.import-upload {
  margin-bottom: 1.5rem;
}

.upload-area {
  border: 2px dashed #e5e7eb;
  border-radius: 8px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-area:hover {
  border-color: #2563eb;
  background-color: #f8fafc;
}

.upload-icon {
  font-size: 2rem;
  margin-bottom: 1rem;
}

.upload-text {
  font-size: 1rem;
  color: #333333;
  margin-bottom: 0.5rem;
}

.upload-hint {
  font-size: 0.875rem;
  color: #666666;
}

.selected-file {
  margin-top: 1rem;
  padding: 0.75rem;
  background-color: #f3f4f6;
  border-radius: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-file span {
  font-size: 0.875rem;
  color: #333333;
}

.selected-file button {
  background: none;
  border: none;
  color: #ef4444;
  cursor: pointer;
  font-size: 0.875rem;
}

.import-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.cancel-btn,
.submit-btn {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn {
  background-color: #f3f4f6;
  border: 1px solid #e5e7eb;
  color: #666666;
}

.cancel-btn:hover {
  background-color: #e5e7eb;
}

.submit-btn {
  background-color: #2563eb;
  border: none;
  color: white;
}

.submit-btn:hover {
  background-color: #1d4ed8;
}

.submit-btn:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.agent-info {
  margin-bottom: 0.5rem;
}

.agent-name {
  font-size: 0.875rem;
  font-weight: 500;
  color: #666666;
}

.appreciator .agent-name {
  color: #8b5cf6;
}

.neutralist .agent-name {
  color: #10b981;
}

.dialectician .agent-name {
  color: #f59e0b;
}
</style> 