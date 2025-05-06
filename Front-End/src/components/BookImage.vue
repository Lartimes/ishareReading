<template>
  <div class="book-container" :class="{ 'fullscreen': isFullscreen }">
    <!-- 顶部工具栏 -->
    <div class="toolbar" v-if="isReading">
      <button class="toolbar-btn" @click="toggleFullscreen">
        <svg viewBox="0 0 24 24" width="20" height="20">
          <path fill="currentColor" d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"/>
        </svg>
      </button>
      <button class="toolbar-btn" @click="toggleInterface">
        <svg viewBox="0 0 24 24" width="20" height="20">
          <path fill="currentColor" d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
        </svg>
      </button>
    </div>

    <!-- 顶部书籍信息 -->
    <div class="book-header" v-if="isReading">
      <div class="book-info">
        <h1 class="book-title">{{ book.title }}</h1>
        <div class="book-meta">
          <span class="author">作者：{{ book.author }}</span>
          <span class="chapter">当前章节：{{ getCurrentChapter }}</span>
        </div>
      </div>
    </div>

    <!-- 标注/笔记按钮放到书籍头信息下方 -->
    <div v-if="isReading" class="annotate-controls annotate-note-row annotate-header-row">
      <button 
        class="main-action-btn"
        :class="{ active: enableAnnotate }"
        @click="toggleAnnotate"
      >
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path fill="currentColor" d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
        </svg>
        标注
      </button>
      <button class="main-action-btn" @click="showNotePanel = !showNotePanel">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none"><path d="M4 4h16v16H4V4zm2 2v12h12V6H6zm2 2h8v2H8V8zm0 4h5v2H8v-2z" fill="#2563eb"/></svg>
        笔记
      </button>
    </div>

    <div class="book-content">
      <!-- 左侧目录抽屉 -->
      <div class="book-sidebar-drawer" :class="{ open: showSidebarDrawer }" :style="showSidebarDrawer ? { width: sidebarWidth + 'px' } : { width: '0' }">
        <transition name="fade-slide">
          <div v-if="showSidebarDrawer" class="drawer-inner">
            <div class="toc-header">
              <h3>目录</h3>
            </div>
            <div class="toc-content">
              <div 
                v-for="(chapter, index) in chapters" 
                :key="index"
                class="toc-item"
                :class="{ active: currentPage >= chapter.startPage && currentPage <= chapter.endPage }"
                @click="jumpToPage(chapter.startPage)"
              >
                <span class="chapter-title">{{ chapter.title }}</span>
                <span class="page-range">第{{ chapter.startPage }}-{{ chapter.endPage }}页</span>
              </div>
            </div>
            <div class="page-navigation">
              <div class="nav-buttons">
                <button class="nav-button" :disabled="currentPage <= 1" @click="prevPage">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"/>
                  </svg>
                  <span>上一页</span>
                </button>
                <div class="page-input">
                  <input 
                    v-model.number="currentPage" 
                    type="number" 
                    min="1" 
                    :max="maxPage"
                    @change="handlePageChange"
                    @keydown.enter="handlePageChange"
                  />
                  <span>/{{ maxPage }}</span>
                </div>
                <button class="nav-button" :disabled="currentPage >= maxPage" @click="nextPage">
                  <span>下一页</span>
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </transition>
        <button class="sidebar-drawer-toggle" :class="{ collapsed: !showSidebarDrawer }" @click="showSidebarDrawer = !showSidebarDrawer">
          <svg v-if="!showSidebarDrawer" width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M4 20V6a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H7l-3 3z" stroke="#2563eb" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          <svg v-else width="22" height="22" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="#ff9800" stroke-width="2"/><path d="M9 12h6M12 9v6" stroke="#ff9800" stroke-width="2" stroke-linecap="round"/></svg>
        </button>
      </div>
      <div class="book-main" :class="{ 'full-width': !showSidebarDrawer }" :style="showSidebarDrawer ? { width: 'calc(100% - ' + sidebarWidth + 'px)' } : { width: '100%' }">
        <div class="book-image-container">
          <div class="img-annotate-wrap">
            <img
              :src="imageSrc"
              class="book-image"
              ref="imgRef"
              draggable="false"
              v-if="imageSrc"
            />
            <!-- 标注层 -->
            <div 
              class="annotate-layer"
              ref="annotateLayer"
              @mousedown="startDraw"
              @mousemove="drawRect"
              @mouseup="endDraw"
              @mouseleave="endDraw"
              @click="handleLayerClick"
              :style="{ cursor: enableAnnotate ? 'crosshair' : 'default' }"
            >
              <!-- 已绘制的矩阵 -->
              <div
                v-for="(rect, idx) in rects"
                :key="idx"
                class="annotate-rect"
                :class="{ 'has-annotation': rect.views && rect.views.length > 0 }"
                :style="getRectStyle(rect)"
              >
                <!-- 矩阵底部的可点击区域 -->
                <div 
                  v-if="rect.views && rect.views.length > 0"
                  class="bottom-area"
                  :style="getBottomAreaStyle(rect)"
                  @click.stop="showPopover(idx)"
                >
                  <div class="bottom-line"></div>
                  <span class="area-text">标注 {{ idx + 1 }}</span>
                </div>
              </div>
              <!-- 正在绘制的临时矩阵 -->
              <div v-if="drawing" class="annotate-rect temp" :style="getRectStyle(tempRect)"></div>
            </div>
            <!-- 批注输入浮窗 -->
            <div v-if="showInput" class="note-input-float" :style="inputPos" @click.stop>
              <input v-model="newNote" placeholder="请输入你的见解..." @keydown.enter="submitNote" />
              <button @click="submitNote">添加批注</button>
            </div>
            <!-- 批注意见弹窗 -->
            <div v-if="popoverIdx !== null" class="note-popover" :style="popoverPos">
              <div class="note-list">
                <div v-for="(view, vIdx) in rects[popoverIdx].views" :key="vIdx" class="note-view">{{ view }}</div>
              </div>
              <div class="note-input">
                <input v-model="newView" placeholder="发表见解..." @keydown.enter="addView(popoverIdx)" />
                <button @click="addView(popoverIdx)">发表</button>
              </div>
            </div>
          </div>
          <!-- OCR文本识别区域 -->
          <div class="ocr-text-area" v-if="ocrText">
            <div class="ocr-header">
              <span>识别文本</span>
              <button class="copy-btn" @click="copyOcrText">
                <svg viewBox="0 0 24 24" width="16" height="16">
                  <path fill="currentColor" d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
                </svg>
                复制
              </button>
            </div>
            <div class="ocr-content">{{ ocrText }}</div>
          </div>
        </div>
        <!-- 笔记抽屉面板 -->
        <transition name="fade-slide">
          <div v-if="showNotePanel" class="note-panel">
            <div class="note-panel-header">
              <span>我的笔记</span>
              <button class="note-close-btn" @click="showNotePanel = false">×</button>
            </div>
            <div class="note-panel-body">
              <textarea v-model="noteContent" placeholder="支持Markdown语法..." class="note-textarea"></textarea>
              <button class="main-action-btn note-add-btn">添加</button>
              <div class="note-preview" v-html="renderedNote"></div>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, watch, onMounted, onUnmounted, computed } from 'vue'
import MarkdownIt from 'markdown-it'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { createWorker } from 'tesseract.js'

const props = defineProps({
  imgSrc: String,
  annotations: { type: Array, default: () => [] },
  readonly: Boolean
})
const emit = defineEmits(['add-annotation'])

const route = useRoute()
const book = ref({})
const loading = ref(true)

const imageSrc = ref('')
const imgRef = ref(null)
const annotateLayer = ref(null)
const rects = ref([])
const drawing = ref(false)
const tempRect = reactive({ x: 0, y: 0, w: 0, h: 0 })
const startPoint = reactive({ x: 0, y: 0 })
const showInput = ref(false)
const inputPos = reactive({ left: '0px', top: '0px' })
const newNote = ref('')
const popoverIdx = ref(null)
const popoverPos = reactive({ left: '0px', top: '0px' })
const newView = ref('')
const enableAnnotate = ref(false)
const isReading = ref(true)
const showSidebar = ref(true)
const currentPage = ref(1)
const isFullscreen = ref(false)
const showInterface = ref(true)
const sidebarWidth = ref(280)
const showSidebarDrawer = ref(true)
const showNotePanel = ref(false)
const noteContent = ref('')
const md = new MarkdownIt({ html: true })
const renderedNote = computed(() => md.render(noteContent.value))
const ocrText = ref('')
const isOcrLoading = ref(false)
let dragging = false

// 章节数据
const chapters = ref([
  { title: '第一回 甄士隐梦幻识通灵 贾雨村风尘怀闺秀', startPage: 1, endPage: 20 },
  { title: '第二回 贾夫人仙逝扬州城 冷子兴演说荣国府', startPage: 21, endPage: 40 },
  { title: '第三回 贾雨村夤缘复旧职 林黛玉抛父进京都', startPage: 41, endPage: 60 },
  { title: '第四回 薄命女偏逢薄命郎 葫芦僧乱判葫芦案', startPage: 61, endPage: 80 },
  { title: '第五回 游幻境指迷十二钗 饮仙醪曲演红楼梦', startPage: 81, endPage: 100 }
])

// 添加页面标注存储
const pageAnnotations = ref(new Map()) // 存储每一页的标注数据
const pageStyles = ref(new Map()) // 存储每一页的标注样式

// 同步外部批注
watch(() => props.annotations, (val) => {
  rects.value = JSON.parse(JSON.stringify(val || []))
}, { immediate: true })

// 监听图片选择，当选择图片时进入阅读模式
watch(() => imageSrc.value, (newVal) => {
  isReading.value = !!newVal
})

// 添加OCR识别函数
async function performOcr(imageUrl) {
  try {
    isOcrLoading.value = true
    const worker = await createWorker('chi_sim+eng')
    const { data: { text } } = await worker.recognize(imageUrl)
    await worker.terminate()
    ocrText.value = text
  } catch (error) {
    console.error('OCR识别失败:', error)
    ocrText.value = '识别失败，请重试'
  } finally {
    isOcrLoading.value = false
  }
}

// 复制OCR文本
function copyOcrText() {
  if (ocrText.value) {
    navigator.clipboard.writeText(ocrText.value)
      .then(() => {
        alert('文本已复制到剪贴板')
      })
      .catch(err => {
        console.error('复制失败:', err)
      })
  }
}

// 获取书籍图片和信息
const fetchBookData = async (pageNum) => {
  try {
    loading.value = true
    const token = localStorage.getItem('token')
    const bookId = route.params.id
    
    const response = await axios.post('http://localhost:8080/ishareReading/book/getBooksImgByPage', {
      bookId: bookId,
      pageNum: pageNum
    }, {
      headers: {
        'token': token,
        'Content-Type': 'application/json'
      }
    })

    if (response.data.code === 200) {
      const data = response.data.data
      // 更新图片
      if (data.imgBase64) {
        // 将base64转换为blob
        const byteString = atob(data.imgBase64.split(',')[1] || data.imgBase64);
        const mimeString = 'image/jpeg';
        const ab = new ArrayBuffer(byteString.length);
        const ia = new Uint8Array(ab);
        for (let i = 0; i < byteString.length; i++) {
          ia[i] = byteString.charCodeAt(i);
        }
        const blob = new Blob([ab], { type: mimeString });
        const imageUrl = URL.createObjectURL(blob);
        imageSrc.value = imageUrl
        
        // 执行OCR识别
        await performOcr(imageUrl)
      }
      
      // 更新书籍信息
      if (data.books) {
        book.value = {
          id: data.books.id,
          title: data.books.name,
          author: data.books.author,
          publicationYear: data.books.publicationYear,
          publisher: data.books.publisher,
          isbn: data.books.isbn,
          genre: data.books.genre,
          description: data.books.description,
          totalPages: data.books.totalPages,
          language: data.books.language,
          averageRating: data.books.averageRating,
          ratingCount: data.books.ratingCount,
          viewCount: data.books.viewCount,
          downloadCount: data.books.downloadCount
        }
        
        // 更新章节数据
        if (data.books.catalog && data.books.catalog.length > 0) {
          chapters.value = data.books.catalog.map((chapter, index) => ({
            title: chapter,
            startPage: index * 20 + 1,
            endPage: (index + 1) * 20
          }))
        }
      }

      // 恢复当前页的标注数据
      if (pageAnnotations.value.has(pageNum)) {
        const savedAnnotations = pageAnnotations.value.get(pageNum)
        rects.value = savedAnnotations.map(annotation => ({
          ...annotation,
          style: annotation.style || {
            border: '2px solid #2563eb',
            background: 'rgba(37,99,235,0.08)',
            boxShadow: '0 0 8px #2563eb55',
            borderRadius: '6px'
          }
        }))
      } else {
        rects.value = []
      }
    } else {
      console.error('获取书籍数据失败:', response.data.msg)
    }
  } catch (error) {
    console.error('获取书籍数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听页码变化
watch(currentPage, (newPage, oldPage) => {
  // 保存当前页的标注数据
  if (rects.value.length > 0) {
    pageAnnotations.value.set(oldPage, [...rects.value])
  }
  // 清空当前页的标注
  rects.value = []
  // 获取新页面的数据
  fetchBookData(newPage)
})

onMounted(() => {
  fetchBookData(1) // 初始加载第一页
})

function getRectStyle(rect) {
  const baseStyle = {
    position: 'absolute',
    left: rect.x * 100 + '%',
    top: rect.y * 100 + '%',
    width: rect.w * 100 + '%',
    height: rect.h * 100 + '%',
    zIndex: 10,
    display: 'flex',
    alignItems: 'flex-end',
    justifyContent: 'center',
    pointerEvents: 'auto',
  }

  // 如果是正在绘制的临时矩阵，添加蓝色边框样式
  if (drawing.value) {
    return {
      ...baseStyle,
      border: '2px solid #2563eb',
      background: 'rgba(37,99,235,0.08)',
      boxShadow: '0 0 8px #2563eb55',
      borderRadius: '6px',
    }
  }

  // 标注完成后，只显示底部标注线
  return {
    ...baseStyle,
    border: 'none',
    background: 'none',
    boxShadow: 'none',
    borderRadius: '0',
  }
}

function getBottomAreaStyle(rect) {
  return {
    position: 'absolute',
    bottom: '0',
    left: '0',
    width: '100%',
    height: '20px', // 可点击区域的高度
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'rgba(37,99,235,0.1)',
  }
}

function startDraw(e) {
  if (!enableAnnotate.value || !annotateLayer.value) return
  drawing.value = true
  const rect = annotateLayer.value.getBoundingClientRect()
  const x = (e.clientX - rect.left) / rect.width
  const y = (e.clientY - rect.top) / rect.height
  startPoint.x = x
  startPoint.y = y
  tempRect.x = x
  tempRect.y = y
  tempRect.w = 0
  tempRect.h = 0
}

function drawRect(e) {
  if (!drawing.value || !annotateLayer.value) return
  const rect = annotateLayer.value.getBoundingClientRect()
  let x = (e.clientX - rect.left) / rect.width
  let y = (e.clientY - rect.top) / rect.height
  
  // 限制在容器范围内
  x = Math.max(0, Math.min(1, x))
  y = Math.max(0, Math.min(1, y))
  
  // 支持SHIFT直线
  if (e.shiftKey) {
    if (Math.abs(x - startPoint.x) > Math.abs(y - startPoint.y)) {
      y = startPoint.y // 水平
    } else {
      x = startPoint.x // 垂直
    }
  }
  
  tempRect.x = Math.min(startPoint.x, x)
  tempRect.y = Math.min(startPoint.y, y)
  tempRect.w = Math.abs(x - startPoint.x)
  tempRect.h = Math.abs(y - startPoint.y)
}

function endDraw(e) {
  if (!drawing.value || !annotateLayer.value) return
  drawing.value = false
  if (tempRect.w < 0.01 && tempRect.h < 0.01) return // 忽略太小的框
  
  // 弹出输入框，输入意见
  nextTick(() => {
    const rect = annotateLayer.value.getBoundingClientRect()
    inputPos.left = `${tempRect.x * rect.width + tempRect.w * rect.width / 2 - 60}px`
    inputPos.top = `${tempRect.y * rect.height - 40}px`
    showInput.value = true
    newNote.value = ''
  })
}

function submitNote() {
  if (!newNote.value.trim()) return
  const newAnn = { 
    x: tempRect.x, 
    y: tempRect.y, 
    w: tempRect.w, 
    h: tempRect.h, 
    views: [newNote.value],
    style: {
      border: 'none',
      background: 'none',
      boxShadow: 'none',
      borderRadius: '0'
    }
  }
  rects.value.push(newAnn)
  // 保存当前页的标注数据
  pageAnnotations.value.set(currentPage.value, [...rects.value])
  emit('add-annotation', newAnn)
  showInput.value = false
  newNote.value = ''
  enableAnnotate.value = false
}

function showPopover(idx) {
  popoverIdx.value = idx
  nextTick(() => {
    if (!annotateLayer.value) return
    const rect = annotateLayer.value.getBoundingClientRect()
    const r = rects.value[idx]
    popoverPos.left = `${r.x * rect.width + r.w * rect.width / 2}px`
    popoverPos.top = `${r.y * rect.height + r.h * rect.height + 8}px`
  })
}

function hidePopover() {
  popoverIdx.value = null
}

function addView(idx) {
  if (!newView.value.trim()) return
  rects.value[idx].views.push(newView.value)
  // 更新当前页的标注数据
  pageAnnotations.value.set(currentPage.value, [...rects.value])
  newView.value = ''
}

function handleLayerClick(e) {
  // 如果点击的是标注层本身（而不是矩阵或链接），则关闭弹窗和输入框
  if (e.target === annotateLayer.value) {
    hidePopover()
    showInput.value = false
    drawing.value = false
  }
}

// 添加切换标注模式的函数
function toggleAnnotate() {
  enableAnnotate.value = !enableAnnotate.value
  if (!enableAnnotate.value) {
    drawing.value = false
    showInput.value = false
  }
}

function jumpToPage(page) {
  currentPage.value = page
}

// 添加最大页数计算
const maxPage = computed(() => {
  return chapters.value.length > 0 ? chapters.value[chapters.value.length - 1].endPage : 1
})

// 添加页面导航方法
function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

function nextPage() {
  if (currentPage.value < maxPage.value) {
    currentPage.value++
  }
}

function handlePageChange() {
  let page = parseInt(currentPage.value)
  if (isNaN(page)) {
    page = 1
  }
  page = Math.max(1, Math.min(page, maxPage.value))
  currentPage.value = page
}

// 添加全屏切换函数
function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value
  if (isFullscreen.value) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 添加界面切换函数
function toggleInterface() {
  showInterface.value = !showInterface.value
}

// 添加键盘快捷键监听
function handleKeyDown(e) {
  // Ctrl + I 切换界面
  if (e.ctrlKey && e.key === 'i') {
    e.preventDefault()
    toggleInterface()
  }
  // Esc 退出全屏
  if (e.key === 'Escape' && isFullscreen.value) {
    toggleFullscreen()
  }
}

function startDrag(e) {
  dragging = true
  document.body.style.cursor = 'col-resize'
}

function onDrag(e) {
  if (!dragging) return
  // 计算新宽度，限制最小/最大
  sidebarWidth.value = Math.max(180, Math.min(e.clientX, 500))
}

function stopDrag() {
  dragging = false
  document.body.style.cursor = ''
}

onMounted(() => {
  window.addEventListener('keydown', handleKeyDown)
  window.addEventListener('mousemove', onDrag)
  window.addEventListener('mouseup', stopDrag)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
  window.removeEventListener('mousemove', onDrag)
  window.removeEventListener('mouseup', stopDrag)
})

// 监听全屏变化
onMounted(() => {
  document.addEventListener('fullscreenchange', () => {
    isFullscreen.value = !!document.fullscreenElement
  })
})

// 添加计算属性获取当前章节
const getCurrentChapter = computed(() => {
  const chapter = chapters.value.find(ch => 
    currentPage.value >= ch.startPage && currentPage.value <= ch.endPage
  )
  return chapter ? chapter.title : '未知章节'
})
</script>

<style scoped>
.book-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  background: linear-gradient(120deg, #f6f8fc 0%, #e3eaff 100%);
  color: #333333;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.book-container.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  background: #f6f8fc;
}

.toolbar {
  position: fixed;
  top: 1.5rem;
  right: 2.5rem;
  display: flex;
  gap: 0.75rem;
  z-index: 10000;
  background: rgba(255, 255, 255, 0.85);
  padding: 0.75rem 1.25rem;
  border-radius: 1.5rem;
  box-shadow: 0 4px 24px rgba(37,99,235,0.10);
  backdrop-filter: blur(12px);
}

.toolbar-btn {
  padding: 0.5rem 0.7rem;
  background: none;
  border: none;
  border-radius: 0.75rem;
  color: #2563eb;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}
.toolbar-btn:hover {
  background: linear-gradient(90deg, #e3eaff 0%, #c7d2fe 100%);
  color: #ff9800;
}

.book-header, .book-sidebar {
  transition: opacity 0.3s ease;
}

.book-header.hidden, .book-sidebar.hidden {
  opacity: 0;
  pointer-events: none;
}

.book-header {
  background: rgba(255, 255, 255, 0.95);
  padding: 1rem 2rem 0.8rem 2rem;
  border-bottom: 1px solid rgba(37, 99, 235, 0.08);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 12px rgba(37,99,235,0.04);
  border-radius: 0 0 1.5rem 1.5rem;
  margin-bottom: 0.8rem;
}
.book-info {
  text-align: center;
}
.book-title {
  font-size: 1.6rem;
  font-weight: 700;
  color: #222;
  margin-bottom: 0.3rem;
  letter-spacing: 1px;
}
.book-meta {
  display: flex;
  gap: 1.5rem;
  color: #666666;
  font-size: 0.9rem;
  justify-content: center;
}
.book-meta .chapter {
  background: #e3eaff;
  color: #2563eb;
  border-radius: 0.5rem;
  padding: 0.2rem 0.8rem;
  font-size: 0.95rem;
  margin-left: 1rem;
}

.book-content {
  display: flex;
  flex: 1;
  overflow: hidden;
  gap: 0.5rem;
}

.book-sidebar-drawer {
  position: relative;
  height: 100%;
  background: #fff;
  border-right: 1px solid #f0f1f3;
  display: flex;
  flex-direction: column;
  transition: width 0.32s cubic-bezier(.4,1.3,.6,1), box-shadow 0.2s;
  overflow: visible;
  z-index: 10;
  box-shadow: 0 2px 12px rgba(60,60,60,0.06);
  border-radius: 14px 0 0 14px;
}
.book-sidebar-drawer:not(.open) {
  width: 0 !important;
  min-width: 0 !important;
  box-shadow: none;
}
.drawer-inner {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 14px 0 0 14px;
  box-shadow: none;
  padding: 18px 0 0 0;
}
.sidebar-drawer-toggle {
  position: absolute;
  right: -48px;
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
  transition: background 0.2s, box-shadow 0.2s, right 0.32s cubic-bezier(.4,1.3,.6,1);
  outline: none;
  border: 2px solid #e3eaff;
}
.sidebar-drawer-toggle.collapsed {
  right: -24px;
  top: 50%;
  transform: translateY(-50%);
  background: #fff;
  border-radius: 50%;
}
.sidebar-drawer-toggle:hover {
  background: #e3eaff;
  box-shadow: 0 4px 16px rgba(37,99,235,0.16);
}
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: opacity 0.32s, transform 0.32s cubic-bezier(.4,1.3,.6,1);
}
.fade-slide-enter-from, .fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-40px);
}
.toc-header {
  padding: 0 24px 10px 24px;
  border-bottom: 1px solid #f0f1f3;
  background: #fff;
  font-weight: 600;
  font-size: 1.08rem;
  color: #2563eb;
  letter-spacing: 1px;
}
.toc-content {
  flex: 1;
  overflow-y: auto;
  padding: 0.5rem 18px 1.2rem 18px;
}
.toc-item {
  padding: 0.7rem 1rem;
  margin: 0.18rem 0;
  border-radius: 8px;
  cursor: pointer;
  color: #444;
  font-size: 0.98rem;
  font-weight: 500;
  transition: all 0.18s cubic-bezier(.4,1.3,.6,1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(60,60,60,0.04);
  border: 1px solid transparent;
}
.toc-item:hover {
  background: #e3eaff;
  color: #2563eb;
  border: 1px solid #2563eb33;
  box-shadow: 0 2px 8px rgba(37,99,235,0.07);
}
.toc-item.active {
  background: linear-gradient(90deg, #e3eaff 0%, #c7d2fe 100%);
  color: #2563eb;
  font-weight: 700;
  border: 1px solid #2563eb;
  box-shadow: 0 3px 12px rgba(37,99,235,0.10);
}
.chapter-title {
  flex: 1;
  margin-right: 0.8rem;
  font-size: 0.98rem;
}
.page-range {
  color: #999;
  font-size: 0.92rem;
}
.page-navigation {
  padding: 0.7rem 1.2rem 0.7rem 1.2rem;
  border-top: 1px solid #f0f1f3;
  background: #fff;
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: center;
  border-radius: 0 0 12px 12px;
  box-shadow: 0 1px 4px rgba(60,60,60,0.04);
  margin-top: 0.2rem;
}
.nav-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.7rem;
  background: #fcfcfd;
  padding: 0.3rem 0.9rem;
  border-radius: 10px;
  box-shadow: 0 1px 4px rgba(60,60,60,0.04);
}
.nav-button {
  padding: 0.4rem 1rem;
  background: linear-gradient(90deg, #2563eb 0%, #ff9800 100%);
  border: none;
  border-radius: 8px;
  color: #fff;
  cursor: pointer;
  transition: all 0.18s;
  font-size: 0.98rem;
  font-weight: 600;
  min-width: 60px;
  box-shadow: 0 1px 4px rgba(60,60,60,0.06);
  display: flex;
  align-items: center;
  gap: 0.3rem;
}
.nav-button:hover:not(:disabled) {
  background: linear-gradient(90deg, #ff9800 0%, #2563eb 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(37,99,235,0.10);
}
.nav-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #e3eaff;
  color: #aaa;
}
.page-input {
  display: flex;
  align-items: center;
  gap: 0.2rem;
  font-size: 0.98rem;
  color: #666;
  background: #fff;
  border: 1.5px solid #e3eaff;
  border-radius: 8px;
  padding: 0.2rem 0.7rem;
  min-width: 60px;
  justify-content: center;
  box-shadow: 0 1px 2px rgba(60,60,60,0.02);
}
.page-input input {
  width: 24px;
  border: none;
  outline: none;
  text-align: center;
  font-size: 0.98rem;
  color: #333;
  padding: 0;
  background: transparent;
  border-radius: 6px;
}
.page-input span {
  color: #666;
  font-size: 0.98rem;
}

.book-main {
  flex: 1;
  overflow-y: auto;
  padding: 1rem 1.5rem 1rem 1rem;
  transition: all 0.3s;
  background: #fff;
  border-radius: 1.5rem;
  margin: 0.8rem 1.5rem 0.8rem 0;
  box-shadow: 0 4px 32px rgba(37,99,235,0.06);
  min-width: 0;
}
.book-main.full-width {
  margin-left: 0;
  width: 100%;
}
.book-image-container {
  position: relative;
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  background: #f6f8fc;
  border-radius: 1.2rem;
  box-shadow: 0 2px 12px rgba(37,99,235,0.04);
  padding: 1rem 1rem 1rem 1rem;
}
.img-annotate-wrap {
  position: relative;
  width: 100%;
  height: 100%;
}
.book-image {
  max-width: 100%;
  height: auto;
  display: block;
  border-radius: 1rem;
  box-shadow: 0 2px 12px rgba(37,99,235,0.08);
  background: #fff;
}
.annotate-controls.annotate-note-row {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  position: absolute;
  top: 0.5rem;
  right: 1rem;
  z-index: 10;
}
.main-action-btn {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.4rem 0.8rem;
  background: linear-gradient(90deg, #2563eb 0%, #ff9800 100%);
  border: none;
  border-radius: 0.75rem;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(37,99,235,0.10);
  font-size: 0.9rem;
  font-weight: 600;
}
.main-action-btn:hover {
  background: linear-gradient(90deg, #ff9800 0%, #2563eb 100%);
  color: #fff;
}
.main-action-btn.active {
  background: #2563eb;
  color: white;
  border-color: #2563eb;
}

.annotate-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 5;
}

.note-input-float {
  position: absolute;
  background: white;
  padding: 10px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 15;
  display: flex;
  gap: 8px;
  pointer-events: auto;
}

.note-input-float input {
  padding: 6px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  width: 200px;
  background: white;
  color: #333333;
}

.note-input-float button {
  padding: 6px 12px;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.note-popover {
  position: absolute;
  background: white;
  padding: 12px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 20;
  min-width: 200px;
  max-width: 300px;
}

.note-list {
  margin-bottom: 10px;
  max-height: 200px;
  overflow-y: auto;
}

.note-view {
  padding: 8px;
  border-bottom: 1px solid #e5e7eb;
  font-size: 14px;
  line-height: 1.4;
  color: #333333;
}

.note-view:last-child {
  border-bottom: none;
}

.note-input {
  display: flex;
  gap: 8px;
}

.note-input input {
  flex: 1;
  padding: 6px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  background: white;
  color: #333333;
}

.note-input button {
  padding: 6px 12px;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.annotate-rect {
  position: absolute;
  pointer-events: auto;
}

.annotate-rect.has-annotation {
  border: none;
  background: none;
  box-shadow: none;
}

.bottom-area {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(37,99,235,0.1);
  transition: all 0.3s;
}

.bottom-area:hover {
  background: rgba(37,99,235,0.2);
}

.bottom-line {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: #2563eb;
}

.area-text {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.9);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  color: #2563eb;
  white-space: nowrap;
  opacity: 0;
  transition: opacity 0.3s;
}

.bottom-area:hover .area-text {
  opacity: 1;
}

.note-panel {
  position: fixed;
  top: 64px;
  right: 0;
  width: 320px;
  height: calc(100vh - 64px);
  background: linear-gradient(120deg, #f6f8fc 0%, #e3eaff 100%);
  box-shadow: -8px 0 32px rgba(37,99,235,0.10);
  z-index: 20000;
  display: flex;
  flex-direction: column;
  border-radius: 1.2rem 0 0 1.2rem;
  animation: slideInRight 0.32s cubic-bezier(.4,1.3,.6,1);
}
@keyframes slideInRight {
  from { right: -500px; opacity: 0; }
  to { right: 0; opacity: 1; }
}
.note-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.8rem 1rem 0.6rem 1rem;
  font-size: 1rem;
  font-weight: 700;
  color: #2563eb;
  border-bottom: 1.5px solid #e3eaff;
  position: relative;
}
.note-close-btn {
  background: none;
  border: none;
  font-size: 2.4rem;
  color: #ff9800;
  cursor: pointer;
  border-radius: 0.7rem;
  transition: background 0.2s, color 0.2s;
  padding: 0.2rem 1rem;
  margin-left: 1rem;
  position: absolute;
  right: 0.5rem;
  top: 0.2rem;
  width: 2.6rem;
  height: 2.6rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
.note-close-btn:hover {
  background: #e3eaff;
  color: #2563eb;
}
.note-panel-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0.8rem 1rem;
  gap: 0.8rem;
  overflow-y: auto;
}
.note-textarea {
  width: 100%;
  min-height: 100px;
  max-height: 180px;
  border: 1.5px solid #e3eaff;
  border-radius: 0.7rem;
  padding: 0.8rem 1rem;
  font-size: 0.95rem;
  background: #fff;
  color: #222;
  resize: vertical;
  margin-bottom: 0.3rem;
  box-shadow: 0 2px 8px rgba(37,99,235,0.04);
}
.note-add-btn {
  margin-bottom: 0.5rem;
  width: 100%;
  justify-content: center;
}
.note-preview {
  flex: 1;
  background: #fff;
  border-radius: 0.7rem;
  padding: 0.8rem 1rem;
  box-shadow: 0 2px 8px rgba(37,99,235,0.04);
  font-size: 0.95rem;
  color: #222;
  overflow-y: auto;
}

@media (max-width: 900px) {
  .book-content {
    flex-direction: column;
    gap: 0;
  }
  .book-main {
    margin: 0.5rem 0.2rem;
    padding: 0.8rem 0.2rem;
    border-radius: 0.7rem;
    max-width: 100vw;
  }
  .book-image-container {
    padding: 0.5rem 0.2rem;
    border-radius: 0.7rem;
  }
  .book-image {
    max-height: 300px;
    border-radius: 0.7rem;
    margin-bottom: 1rem;
  }
}

.annotate-header-row {
  position: static !important;
  margin: 0.3rem 0 0.8rem 0;
  display: flex;
  flex-direction: row;
  gap: 1rem;
  justify-content: center;
  align-items: center;
  z-index: 2;
}

.import-knowledge-btn-row {
  display: flex;
  justify-content: center;
  margin: 2.2rem 0 0.5rem 0;
}
.import-knowledge-btn {
  padding: 0.7rem 2.2rem;
  font-size: 1.08rem;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(90deg, #2563eb 0%, #ff9800 100%);
  color: #fff;
  border: none;
  box-shadow: 0 2px 8px rgba(37,99,235,0.10);
  display: flex;
  align-items: center;
  gap: 0.6rem;
  cursor: pointer;
  transition: background 0.2s, box-shadow 0.2s;
}
.import-knowledge-btn:hover {
  background: linear-gradient(90deg, #ff9800 0%, #2563eb 100%);
  box-shadow: 0 4px 16px rgba(37,99,235,0.16);
}

.ocr-text-area {
  margin-top: 1rem;
  background: #fff;
  border-radius: 0.8rem;
  box-shadow: 0 2px 8px rgba(37,99,235,0.04);
  overflow: hidden;
}

.ocr-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 1rem;
  background: #f6f8fc;
  border-bottom: 1px solid #e3eaff;
}

.ocr-header span {
  font-size: 0.95rem;
  font-weight: 600;
  color: #2563eb;
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.4rem 0.8rem;
  background: #e3eaff;
  border: none;
  border-radius: 0.5rem;
  color: #2563eb;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.copy-btn:hover {
  background: #c7d2fe;
}

.ocr-content {
  padding: 1rem;
  font-size: 0.95rem;
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}
</style>