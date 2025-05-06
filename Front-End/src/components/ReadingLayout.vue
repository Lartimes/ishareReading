<template>
  <div class="reading-layout">
    <!-- 阅读/标注区 -->
    <div class="reading-area" ref="readingAreaRef">
      <button class="fullscreen-btn" @click="toggleFullscreen">
        <svg v-if="!isFullscreen" width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="7" height="7" rx="2" stroke="#2563eb" stroke-width="2"/><rect x="14" y="3" width="7" height="7" rx="2" stroke="#2563eb" stroke-width="2"/><rect x="14" y="14" width="7" height="7" rx="2" stroke="#2563eb" stroke-width="2"/><rect x="3" y="14" width="7" height="7" rx="2" stroke="#2563eb" stroke-width="2"/></svg>
        <svg v-else width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="6" y="6" width="12" height="12" rx="2" stroke="#ff9800" stroke-width="2"/></svg>
      </button>
      <BookImage />
    </div>
    <!-- AI对话区 -->
    <div class="chat-area">
      <div class="chat-toggle" @click="toggleChat">
        <svg v-if="showChat" viewBox="0 0 24 24" width="24" height="24">
          <path fill="currentColor" d="M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" width="24" height="24">
          <path fill="currentColor" d="M8.59 16.59L10 18l6-6-6-6-1.41 1.41L13.17 12z"/>
        </svg>
      </div>
      <div class="chat-flex-wrapper" v-if="showChat">
        <div class="chat-main-wrapper">
          <Chat />
          <div class="session-list-drawer" :class="{ open: showSessionList }">
            <button class="drawer-toggle" @click="showSessionList = !showSessionList">
              <svg v-if="!showSessionList" width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M4 20V6a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H7l-3 3z" stroke="#2563eb" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <svg v-else width="22" height="22" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="#ff9800" stroke-width="2"/><path d="M9 12h6M12 9v6" stroke="#ff9800" stroke-width="2" stroke-linecap="round"/></svg>
            </button>
            <div class="drawer-content" v-show="showSessionList">
              <SessionList />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import BookImage from './BookImage.vue'
import Chat from './Chat.vue'
import SessionList from './SessionList.vue'
import axios from 'axios'
import { API } from '@/config/api'

const route = useRoute()
const showChat = ref(true) // 控制聊天区域显示/隐藏
const showSessionList = ref(true) // 控制会话列表显示/隐藏
const readingAreaRef = ref(null)
const isFullscreen = ref(false)

// 获取阅读内容
const fetchReadingContent = async () => {
  try {
    const token = localStorage.getItem('token')
    const bookId = route.params.id
    const pageNum = 1

    const response = await axios.post(API.GET_BOOK_READING_DETAILS, {
      bookId,
      pageNum
    }, {
      headers: {
        'token': token,
        'Content-Type': 'application/json'
      }
    })

    if (response.data.code === 200) {
      // 处理阅读内容
      console.log('获取阅读内容成功:', response.data)
    } else {
      console.error('获取阅读内容失败:', response.data.msg)
    }
  } catch (error) {
    console.error('获取阅读内容失败:', error)
  }
}

onMounted(() => {
  fetchReadingContent()
})

function toggleChat() {
  showChat.value = !showChat.value
}

function toggleFullscreen() {
  const el = readingAreaRef.value
  if (!el) return
  if (!isFullscreen.value) {
    if (el.requestFullscreen) {
      el.requestFullscreen()
    } else if (el.webkitRequestFullscreen) {
      el.webkitRequestFullscreen()
    } else if (el.mozRequestFullScreen) {
      el.mozRequestFullScreen()
    } else if (el.msRequestFullscreen) {
      el.msRequestFullscreen()
    }
    isFullscreen.value = true
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen()
    } else if (document.mozCancelFullScreen) {
      document.mozCancelFullScreen()
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen()
    }
    isFullscreen.value = false
  }
}

// 监听全屏变化，自动同步状态
if (typeof window !== 'undefined') {
  document.addEventListener('fullscreenchange', () => {
    isFullscreen.value = !!document.fullscreenElement
  })
}
</script>

<style scoped>
.reading-layout {
  display: flex;
  width: 100%;
  height: 100vh;
  background: none;
}

.reading-area {
  background: #fff;
  min-width: 320px;
  max-width: 100vw;
  box-shadow: 2px 0 12px rgba(60,60,60,0.06);
  z-index: 1;
  transition: width 0.2s;
  overflow: auto;
  position: relative;
  flex: 1 1 0;
}

.chat-area {
  display: flex;
  flex-direction: column;
  background: #f6f7fa;
  min-width: 0;
  height: 100vh;
  position: relative;
  transition: width 0.2s;
  overflow: hidden;
  flex: 1 1 0;
}

.chat-toggle {
  position: absolute;
  left: 0;
  top: 12px;
  z-index: 10;
  background: #fff;
  border-radius: 0 12px 12px 0;
  box-shadow: 0 2px 8px rgba(60,60,60,0.08);
  cursor: pointer;
  padding: 4px 6px;
  display: flex;
  align-items: center;
  transition: background 0.2s;
}

.chat-toggle:hover {
  background: rgba(37, 99, 235, 0.1);
}

.chat-toggle svg {
  color: currentColor;
}

.chat-flex-wrapper {
  display: flex;
  height: 100%;
}

.chat-main-wrapper {
  flex: 1;
  background: #f6f7fa;
  height: 100%;
  overflow: auto;
  display: flex;
  flex-direction: column;
  position: relative;
}

.session-list-drawer {
  position: absolute;
  top: 0;
  right: 0;
  height: 100%;
  width: 0;
  background: var(--ilove-card, #fff);
  color: var(--ilove-text, #222);
  box-shadow: -2px 0 18px rgba(60,60,60,0.10);
  z-index: 10;
  transition: width 0.32s cubic-bezier(.4,1.3,.6,1), box-shadow 0.2s;
  overflow: visible;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  border-radius: 18px 0 0 18px;
}

.session-list-drawer.open {
  width: 320px;
  min-width: 320px;
  max-width: 320px;
  box-shadow: -4px 0 32px rgba(37,99,235,0.10);
}

.drawer-toggle {
  position: absolute;
  left: -48px;
  top: 40px;
  background: #fff;
  color: #2563eb;
  border: none;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  cursor: pointer;
  font-size: 1.3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(37,99,235,0.10);
  z-index: 11;
  transition: background 0.2s, box-shadow 0.2s, left 0.32s cubic-bezier(.4,1.3,.6,1);
  outline: none;
  border: 2px solid #e3eaff;
}

.drawer-toggle:hover {
  background: #e3eaff;
  box-shadow: 0 4px 16px rgba(37,99,235,0.16);
}

.drawer-content {
  width: 100%;
  height: 100%;
  overflow: auto;
  border-radius: 18px 0 0 18px;
  background: var(--ilove-card, #fff);
}

.session-list-drawer:not(.open) .drawer-toggle {
  left: -48px;
  top: 50%;
  transform: translateY(-50%);
  border-radius: 50%;
  background: #fff;
}

.session-list-drawer:not(.open) {
  box-shadow: none;
}

.fullscreen-btn {
  position: absolute;
  top: 18px;
  right: 18px;
  z-index: 20;
  background: #fff;
  border: 2px solid #e3eaff;
  border-radius: 50%;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(37,99,235,0.10);
  cursor: pointer;
  transition: background 0.2s, box-shadow 0.2s;
  outline: none;
}

.fullscreen-btn:hover {
  background: #e3eaff;
  box-shadow: 0 4px 16px rgba(37,99,235,0.16);
}

@media (max-width: 900px) {
  .reading-layout {
    flex-direction: column;
  }
  .reading-area {
    width: 100vw !important;
    min-width: 0;
    max-width: 100vw;
  }
  .chat-area {
    width: 100vw !important;
    min-width: 0;
  }
  .chat-flex-wrapper {
    flex-direction: column;
  }
  .session-list-drawer {
    width: 100vw;
    min-width: 0;
    max-width: 100vw;
    height: 180px;
  }
}
</style> 