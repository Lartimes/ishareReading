<template>
  <div class="user-profile">
    <div v-if="loading" class="loading-spinner">
      <div class="spinner"></div>
    </div>
    <div v-else class="profile-container">
      <div class="profile-header">
        <div class="avatar-section">
          <img :src="userInfo.avatar" alt="用户头像" class="avatar" />
          <input 
            type="file" 
            ref="fileInput"
            accept="image/*"
            @change="handleAvatarUpload"
            style="display: none"
          />
          <button class="edit-avatar-btn" @click="triggerFileInput">编辑头像</button>
        </div>
        <div class="user-info">
          <h1 class="username">{{ userInfo.name }}</h1>
          <p class="email">{{ userInfo.email }}</p>
          <p class="self-intro">{{ userInfo.selfIntro }}</p>
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.readingTime || 0 }}</span>
              <span class="stat-label">阅读时长</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.favoriteCount || 0 }}</span>
              <span class="stat-label">收藏书籍</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.noteCount || 0 }}</span>
              <span class="stat-label">笔记数量</span>
            </div>
          </div>
        </div>
      </div>

      <div class="profile-content">
        <div class="tabs">
          <button 
            v-for="tab in tabs" 
            :key="tab.id"
            :class="['tab-button', { active: currentTab === tab.id }]"
            @click="currentTab = tab.id"
          >
            {{ tab.label }}
          </button>
        </div>

        <div class="tab-content">
          <!-- 阅读历史 -->
          <div v-if="currentTab === 'history'" class="history-list">
            <div v-if="readingHistory.length === 0" class="empty-state">
              <p>暂无阅读历史</p>
            </div>
            <div v-else class="book-list">
              <div v-for="item in readingHistory" :key="item.id" class="book-item">
                <div class="book-cover">
                  <img :src="item.cover" :alt="item.title">
                </div>
                <div class="book-info">
                  <h3>{{ item.title }}</h3>
                  <p>{{ item.author }}</p>
                  <div class="book-meta">
                    <span class="reading-time">上次阅读：{{ item.lastReadTime }}</span>
                    <span class="progress-text">阅读进度：{{ item.progress }}%</span>
                  </div>
                  <div class="reading-progress">
                    <div class="progress-bar">
                      <div class="progress" :style="{ width: `${item.progress}%` }"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 收藏书籍 -->
          <div v-if="currentTab === 'favorites'" class="favorites-list">
            <div v-if="favoriteBooks.length === 0" class="empty-state">
              <p>暂无收藏书籍</p>
            </div>
            <div v-else class="book-list">
              <div v-for="book in favoriteBooks" :key="book.id" class="book-item">
                <div class="book-cover">
                  <img :src="book.cover" :alt="book.title">
                </div>
                <div class="book-info">
                  <h3>{{ book.title }}</h3>
                  <p>{{ book.author }}</p>
                  <div class="book-meta">
                    <span class="category">{{ book.category }}</span>
                    <span class="word-count">{{ book.wordCount }}字</span>
                  </div>
                  <p class="book-description">{{ book.description }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 笔记 -->
          <div v-if="currentTab === 'notes'" class="notes-list">
            <div v-if="notes.length === 0" class="empty-state">
              <p>暂无笔记</p>
            </div>
            <div v-else class="note-items">
              <div v-for="note in notes" :key="note.id" class="note-item">
                <div class="note-header">
                  <h3>{{ note.title }}</h3>
                  <span class="note-date">{{ note.createTime }}</span>
                </div>
                <div class="note-content">
                  <p>{{ note.content }}</p>
                </div>
                <div class="note-meta">
                  <span>{{ note.bookTitle }}</span>
                  <span>第{{ note.chapter }}章</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 我的帖子 -->
          <div v-if="currentTab === 'posts'" class="posts-list">
            <div v-if="posts.length === 0" class="empty-state">
              <p>暂无帖子</p>
            </div>
            <div v-else class="list-items">
              <div v-for="post in posts" :key="post.id" class="list-item">
                <div class="item-header">
                  <h3>{{ post.title }}</h3>
                  <span class="item-date">{{ post.createTime }}</span>
                </div>
                <div class="item-content">
                  <p>{{ post.content }}</p>
                </div>
                <div class="item-meta">
                  <span class="meta-item">
                    <i class="icon-like">👍</i>
                    {{ post.likes }}
                  </span>
                  <span class="meta-item">
                    <i class="icon-comment">💬</i>
                    {{ post.comments }}
                  </span>
                  <span class="meta-item">
                    <i class="icon-view">👁️</i>
                    {{ post.views }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- 我的粉丝 -->
          <div v-if="currentTab === 'followers'" class="followers-list">
            <div v-if="followers.length === 0" class="empty-state">
              <p>暂无粉丝</p>
            </div>
            <div v-else class="list-items">
              <div v-for="follower in followers" :key="follower.id" class="list-item">
                <div class="user-avatar">
                  <img :src="follower.avatar" :alt="follower.name" />
                </div>
                <div class="user-info">
                  <h3>{{ follower.name }}</h3>
                  <p>{{ follower.bio }}</p>
                </div>
                <div class="user-stats">
                  <span class="stat-item">
                    <i class="icon-post">📝</i>
                    {{ follower.posts }}
                  </span>
                  <span class="stat-item">
                    <i class="icon-follower">👥</i>
                    {{ follower.followers }}
                  </span>
                  <span class="stat-item">
                    <i class="icon-following">👤</i>
                    {{ follower.following }}
                  </span>
                </div>
                <button 
                  class="follow-button"
                  :class="{
                    'mutual': follower.followStatus === 'mutual',
                    'following': follower.followStatus === 'following',
                    'none': follower.followStatus === 'none'
                  }"
                  @click="handleFollow(follower.id, follower.followStatus)"
                >
                  <span v-if="follower.followStatus === 'mutual'">互相关注</span>
                  <span v-else-if="follower.followStatus === 'following'">已关注</span>
                  <span v-else>关注</span>
                </button>
              </div>
            </div>
          </div>

          <!-- 我的关注 -->
          <div v-if="currentTab === 'following'" class="following-list">
            <div v-if="following.length === 0" class="empty-state">
              <p>暂无关注</p>
            </div>
            <div v-else class="list-items">
              <div v-for="user in following" :key="user.id" class="list-item">
                <div class="user-avatar">
                  <img :src="user.avatar" :alt="user.name" />
                </div>
                <div class="user-info">
                  <h3>{{ user.name }}</h3>
                  <p>{{ user.bio }}</p>
                </div>
                <div class="user-stats">
                  <span class="stat-item">
                    <i class="icon-post">📝</i>
                    {{ user.posts }}
                  </span>
                  <span class="stat-item">
                    <i class="icon-follower">👥</i>
                    {{ user.followers }}
                  </span>
                  <span class="stat-item">
                    <i class="icon-following">👤</i>
                    {{ user.following }}
                  </span>
                </div>
                <button 
                  class="follow-button"
                  :class="{
                    'mutual': user.followStatus === 'mutual',
                    'following': user.followStatus === 'following',
                    'none': user.followStatus === 'none'
                  }"
                  @click="handleFollow(user.id, user.followStatus)"
                >
                  <span v-if="user.followStatus === 'mutual'">互相关注</span>
                  <span v-else-if="user.followStatus === 'following'">已关注</span>
                  <span v-else>关注</span>
                </button>
              </div>
            </div>
          </div>

          <!-- 导入数据 -->
          <div v-if="currentTab === 'import'" class="import-section">
            <div class="import-header">
              <h3>导入书籍数据</h3>
              <p>支持导入TXT、EPUB、PDF等格式的书籍文件</p>
            </div>
            <div class="import-content">
              <div class="import-form">
                <div class="form-group">
                  <label>书籍名称</label>
                  <input v-model="importForm.name" type="text" placeholder="请输入书籍名称">
                </div>
                <div class="form-group">
                  <label>作者</label>
                  <input v-model="importForm.author" type="text" placeholder="请输入作者">
                </div>
                <div class="form-group">
                  <label>出版年份</label>
                  <input v-model="importForm.publicationYear" type="text" placeholder="请输入出版年份">
                </div>
                <div class="form-group">
                  <label>出版社</label>
                  <input v-model="importForm.publisher" type="text" placeholder="请输入出版社">
                </div>
                <div class="form-group">
                  <label>ISBN</label>
                  <input v-model="importForm.isbn" type="text" placeholder="请输入ISBN">
                </div>
                <div class="form-group">
                  <label>分类</label>
                  <div class="multi-select">
                    <div class="selected-tags">
                      <span v-for="(tag, index) in importForm.genre" :key="index" class="tag">
                        {{ tag }}
                        <button @click="removeGenre(index)" class="remove-tag">×</button>
                      </span>
                    </div>
                    <div class="select-input">
                      <input 
                        type="text" 
                        v-model="genreInput"
                        @keydown.enter="addGenre"
                        @keydown.delete="handleDelete"
                        placeholder="选择或输入分类，按回车确认"
                        @focus="showOptions = true"
                      >
                      <div v-if="showOptions" class="options-list">
                        <div 
                          v-for="type in filteredTypes" 
                          :key="type.id" 
                          class="option"
                          @click="selectGenre(type.typeName)"
                        >
                          {{ type.typeName }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="form-group">
                  <label>简介</label>
                  <textarea v-model="importForm.description" placeholder="请输入书籍简介"></textarea>
                </div>
                <div class="form-group">
                  <label>页数</label>
                  <input v-model="importForm.totalPages" type="text" placeholder="请输入页数">
                </div>
                <div class="form-group">
                  <label>语言</label>
                  <input v-model="importForm.language" type="text" placeholder="请输入语言">
                </div>
                <div class="form-group">
                  <label>目录</label>
                  <div class="directory-tree">
                    <div v-for="(item, index) in importForm.bookMarks" :key="index" class="tree-item">
                      <div class="tree-node">
                        <span class="tree-title">{{ item.title }}</span>
                        <span class="tree-page">第{{ item.page }}页</span>
                      </div>
                      <div v-if="item.children && item.children.length > 0" class="tree-children">
                        <div v-for="(child, childIndex) in item.children" :key="childIndex" class="tree-item">
                          <div class="tree-node">
                            <span class="tree-title">{{ child.title }}</span>
                            <span class="tree-page">第{{ child.page }}页</span>
                          </div>
                          <div v-if="child.children && child.children.length > 0" class="tree-children">
                            <div v-for="(grandChild, grandChildIndex) in child.children" :key="grandChildIndex" class="tree-item">
                              <div class="tree-node">
                                <span class="tree-title">{{ grandChild.title }}</span>
                                <span class="tree-page">第{{ grandChild.page }}页</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="import-upload">
                <div class="upload-area" @click="triggerFileUpload">
                  <input 
                    type="file" 
                    ref="fileInput"
                    @change="handleFileUpload"
                    accept=".txt,.epub,.pdf"
                    style="display: none"
                  >
                  <div class="upload-icon">📥</div>
                  <div class="upload-text">点击或拖拽文件到此处</div>
                  <div class="upload-hint">支持TXT、EPUB、PDF格式</div>
                </div>
                <div v-if="selectedFile" class="selected-file">
                  <span>{{ selectedFile.name }}</span>
                  <button @click="removeFile">删除</button>
                </div>
              </div>
            </div>
            <div class="import-actions">
              <button class="cancel-btn" @click="cancelImport">取消</button>
              <button class="update-btn" @click="updateBookData" :disabled="!isImportValid">上传云端</button>
              <button class="submit-btn" @click="submitImport" :disabled="!isImportValid">上传书籍</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { API } from '@/config/api'

const route = useRoute()
const router = useRouter()
const userInfo = ref(null)
const currentTab = ref('history')
const readingHistory = ref([])
const favoriteBooks = ref([])
const notes = ref([])
const posts = ref([])
const followers = ref([])
const following = ref([])
const loading = ref(true)
const fileInput = ref(null)
const bookTypes = ref([])
const importForm = ref({
  name: '',
  author: '',
  publicationYear: '',
  publisher: '',
  isbn: '',
  genre: [],
  description: '',
  totalPages: '',
  language: '',
  bookMarks: [],
  fileId: '',
  coverImageId: ''
})
const selectedFile = ref(null)
const genreInput = ref('')
const showOptions = ref(false)
const filteredTypes = computed(() => {
  if (!genreInput.value) return bookTypes.value
  return bookTypes.value.filter(type => 
    type.typeName.toLowerCase().includes(genreInput.value.toLowerCase())
  )
})

const tabs = [
  { id: 'history', label: '阅读历史' },
  { id: 'favorites', label: '收藏书籍' },
  { id: 'notes', label: '笔记' },
  { id: 'posts', label: '我的帖子' },
  { id: 'import', label: '导入数据' }
]

// 更新假数据中的图片路径
const mockReadingHistory = [
  {
    id: 1,
    title: '三体',
    author: '刘慈欣',
    cover: '/src/assets/book-covers/精品小说.jpg',
    progress: 75,
    lastReadTime: '2024-03-15 14:30'
  },
  {
    id: 2,
    title: '活着',
    author: '余华',
    cover: '/src/assets/book-covers/文学.jpg',
    progress: 100,
    lastReadTime: '2024-03-10 09:15'
  },
  {
    id: 3,
    title: '围城',
    author: '钱钟书',
    cover: '/src/assets/book-covers/精品小说.jpg',
    progress: 45,
    lastReadTime: '2024-03-08 16:20'
  }
]

const mockFavoriteBooks = [
  {
    id: 1,
    title: '三体',
    author: '刘慈欣',
    cover: '/src/assets/book-covers/精品小说.jpg',
    category: '科幻',
    wordCount: '300000',
    description: '中国科幻文学的里程碑之作，讲述了地球文明与三体文明的碰撞。'
  },
  {
    id: 2,
    title: '活着',
    author: '余华',
    cover: '/src/assets/book-covers/文学.jpg',
    category: '文学',
    wordCount: '150000',
    description: '一部关于生命与苦难的史诗，讲述了一个普通人的一生。'
  },
  {
    id: 3,
    title: '围城',
    author: '钱钟书',
    cover: '/src/assets/book-covers/精品小说.jpg',
    category: '文学',
    wordCount: '200000',
    description: '一部描写知识分子生活的经典小说。'
  }
]

const mockNotes = [
  {
    id: 1,
    title: '三体中的黑暗森林法则',
    content: '黑暗森林法则揭示了宇宙文明之间的残酷竞争，每个文明都是带枪的猎人...',
    createTime: '2024-03-15 14:30',
    bookTitle: '三体',
    chapter: '第15章'
  },
  {
    id: 2,
    title: '活着中的生命意义',
    content: '福贵的一生虽然充满苦难，但他始终保持着对生活的热爱，这让我深受感动...',
    createTime: '2024-03-10 09:15',
    bookTitle: '活着',
    chapter: '第8章'
  },
  {
    id: 3,
    title: '围城中的婚姻观',
    content: '婚姻就像一座围城，城外的人想进去，城里的人想出来...',
    createTime: '2024-03-08 16:20',
    bookTitle: '围城',
    chapter: '第12章'
  }
]

const mockPosts = [
  {
    id: 1,
    title: '《三体》读后感：宇宙的黑暗森林法则',
    content: '读完《三体》后，我对宇宙的认知有了全新的理解。黑暗森林法则揭示了宇宙文明之间的残酷竞争...',
    createTime: '2024-03-15 14:30',
    likes: 128,
    comments: 45,
    views: 1024
  },
  {
    id: 2,
    title: '《活着》读书笔记：生命的坚韧与希望',
    content: '余华的《活着》让我深刻体会到生命的可贵。福贵的一生虽然充满苦难，但他始终保持着对生活的热爱...',
    createTime: '2024-03-10 09:15',
    likes: 256,
    comments: 78,
    views: 2048
  },
  {
    id: 3,
    title: '《围城》中的婚姻观与现代社会的思考',
    content: '重读《围城》，对钱钟书笔下的婚姻观有了更深的理解。在当今社会，这种围城现象依然存在...',
    createTime: '2024-03-08 16:20',
    likes: 192,
    comments: 64,
    views: 1536
  }
]

const mockFollowers = [
  {
    id: 1,
    name: '书虫小张',
    avatar: '/src/assets/avatars/demo-image.jpg',
    bio: '热爱阅读，喜欢分享读书心得',
    posts: 32,
    followers: 128,
    following: 64,
    followStatus: 'mutual'
  },
  {
    id: 2,
    name: '文学爱好者',
    avatar: '/src/assets/avatars/demo-image.jpg',
    bio: '专注于文学创作和阅读',
    posts: 45,
    followers: 256,
    following: 128,
    followStatus: 'following'
  },
  {
    id: 3,
    name: '科幻迷',
    avatar: '/src/assets/avatars/demo-image.jpg',
    bio: '科幻小说爱好者，喜欢探索未知世界',
    posts: 28,
    followers: 96,
    following: 48,
    followStatus: 'none'
  }
]

const mockFollowing = [
  {
    id: 1,
    name: '读书笔记',
    avatar: '/src/assets/avatars/demo-image.jpg',
    bio: '分享优质读书笔记和书评',
    posts: 128,
    followers: 1024,
    following: 256,
    followStatus: 'mutual'
  },
  {
    id: 2,
    name: '文学社',
    avatar: '/src/assets/avatars/demo-image.jpg',
    bio: '文学爱好者聚集地',
    posts: 256,
    followers: 2048,
    following: 512,
    followStatus: 'following'
  },
  {
    id: 3,
    name: '科幻世界',
    avatar: '/src/assets/avatars/demo-image.jpg',
    bio: '探索科幻文学的魅力',
    posts: 96,
    followers: 768,
    following: 192,
    followStatus: 'mutual'
  }
]

// 将 base64 转换为 blob URL
const base64ToBlobUrl = (base64String) => {
  if (!base64String) return null
  try {
    // 移除 base64 头部信息
    const base64Data = base64String.split(',')[1] || base64String
    // 将 base64 转换为 blob
    const byteCharacters = atob(base64Data)
    const byteArrays = []
    for (let i = 0; i < byteCharacters.length; i++) {
      byteArrays.push(byteCharacters.charCodeAt(i))
    }
    const byteArray = new Uint8Array(byteArrays)
    const blob = new Blob([byteArray], { type: 'image/jpeg' })
    return URL.createObjectURL(blob)
  } catch (error) {
    console.error('Error converting base64 to blob:', error)
    return null
  }
}

// 检查 token 并处理未登录状态
const checkAuth = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    // 清除用户信息
    localStorage.removeItem('userInfo')
    // 跳转到首页
    router.push('/')
    return false
  }
  return true
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    loading.value = true
    if (!checkAuth()) return

    const token = localStorage.getItem('token')
    const response = await axios.get(API.USER_INFO(route.params.id), {
      headers: {
        'token': token
      }
    })

    if (response.data.code === 200) {
      const userData = response.data.data
      const avatarUrl = userData.avatarBase64 ? base64ToBlobUrl(userData.avatarBase64) : null
      
      userInfo.value = {
        id: userData.id,
        name: userData.userName,
        email: userData.email,
        avatar: avatarUrl || '/src/assets/avatars/demo-image.jpg',
        selfIntro: userData.selfIntro || '这个人很懒，什么都没写~',
        sex: userData.sex || '未知',
        readingPreference: userData.readingPreference || '暂无偏好',
        lastLoginTime: userData.lastLoginTime ? new Date(userData.lastLoginTime).toLocaleString() : '从未登录'
      }
    } else {
      console.error('Failed to fetch user info:', response.data.msg)
      if (response.data.code === 401) {
        checkAuth()
      }
    }
  } catch (error) {
    console.error('Error fetching user info:', error)
    if (error.response?.status === 401) {
      checkAuth()
    }
  } finally {
    loading.value = false
  }
}

// 获取用户统计数据
const fetchUserStats = async () => {
  try {
    if (!checkAuth()) return

    const token = localStorage.getItem('token')
    const userId = route.params.id
    const [historyRes, favoritesRes, notesRes, postsRes, followersRes, followingRes] = await Promise.all([
      axios.get(`${API.USER_HISTORY}?userId=${userId}`, {
        headers: { 'token': token }
      }),
      axios.get(`${API.USER_FAVORITES}?userId=${userId}`, {
        headers: { 'token': token }
      }),
      axios.get(`${API.USER_NOTES}?userId=${userId}`, {
        headers: { 'token': token }
      }),
      axios.get(`${API.USER_POSTS}?userId=${userId}`, {
        headers: { 'token': token }
      }),
      axios.get(`${API.USER_FOLLOWERS}?userId=${userId}`, {
        headers: { 'token': token }
      }),
      axios.get(`${API.USER_FOLLOWING}?userId=${userId}`, {
        headers: { 'token': token }
      })
    ])

    if (historyRes.data.code === 200) readingHistory.value = historyRes.data.data
    if (favoritesRes.data.code === 200) favoriteBooks.value = favoritesRes.data.data
    if (notesRes.data.code === 200) notes.value = notesRes.data.data
    if (postsRes.data.code === 200) posts.value = postsRes.data.data
    if (followersRes.data.code === 200) followers.value = followersRes.data.data
    if (followingRes.data.code === 200) following.value = followingRes.data.data
  } catch (error) {
    console.error('获取用户统计数据失败:', error)
    if (error.response?.status === 401) {
      checkAuth()
    }
  }
}

// 处理头像上传
const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }

  // 检查文件大小（限制为5MB）
  if (file.size > 5 * 1024 * 1024) {
    alert('图片大小不能超过5MB')
    return
  }

  try {
    loading.value = true
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', route.params.id)

    const token = localStorage.getItem('token')
    const response = await axios.post(API.UPLOAD_AVATAR, formData, {
      headers: {
        'token': token,
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response.data.code === 200) {
      // 更新头像后重新获取用户信息
      await fetchUserInfo()
      alert('头像更新成功')
    } else {
      alert(response.data.msg || '头像上传失败')
    }
  } catch (error) {
    console.error('上传头像失败:', error)
    alert('上传失败，请重试')
  } finally {
    loading.value = false
  }
}

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value.click()
}

// 处理关注/取关
const handleFollow = async (userId, currentStatus) => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      router.push('/login')
      return
    }

    if (currentStatus === 'none') {
      // 关注
      await axios.post(`${API.FOLLOW_USER}?userId=${userId}`, null, {
        headers: { 'token': token }
      })
    } else {
      // 取关
      await axios.delete(`${API.UNFOLLOW_USER}?userId=${userId}`, {
        headers: { 'token': token }
      })
    }

    // 更新状态
    const updateStatus = (list, id) => {
      const item = list.find(item => item.id === id)
      if (item) {
        if (currentStatus === 'none') {
          item.followStatus = 'following'
        } else {
          item.followStatus = 'none'
        }
      }
    }

    updateStatus(followers.value, userId)
    updateStatus(following.value, userId)
  } catch (error) {
    console.error('操作失败:', error)
    alert('操作失败，请重试')
  }
}

// 获取书籍类型
const fetchBookTypes = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get(API.GET_BOOK_TYPES, {
      headers: { 'token': token }
    })
    if (response.data.code === 200) {
      bookTypes.value = response.data.data
    }
  } catch (error) {
    console.error('获取书籍类型失败:', error)
  }
}

// 触发文件上传
const triggerFileUpload = () => {
  fileInput.value.click()
}

// 解析目录树字符串
const parseDirectoryTree = (str) => {
  if (!str) return []
  
  const lines = str.split('\n')
  const result = []
  const stack = [{ children: result, level: -1 }]

  lines.forEach(line => {
    // 匹配 "标题 (Page 数字)" 格式
    const match = line.match(/^( *)(.*?) \(Page (\d+)\)$/)
    if (!match) return

    const [, spaces, title, page] = match
    const level = spaces.length / 4  // 每4个空格表示一级缩进
    const node = {
      title: title.trim(),
      page: parseInt(page),
      children: []
    }

    // 找到父节点
    while (stack[stack.length - 1].level >= level) {
      stack.pop()
    }

    // 添加到父节点的children中
    stack[stack.length - 1].children.push(node)
    // 压入栈
    stack.push({ ...node, level })
  })

  return result
}

// 处理文件上传
const handleFileUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    loading.value = true
    const formData = new FormData()
    formData.append('file', file)

    const token = localStorage.getItem('token')
    const response = await axios.post(API.GET_BOOK_METADATA, formData, {
      headers: {
        'token': token,
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response.data.code === 200) {
      const { metaData, imgId, bookId } = response.data.data
      
      // 解析目录树字符串
      const bookMarks = parseDirectoryTree(metaData.bookMarks)
      
      // 填充表单数据
      importForm.value = {
        name: metaData.title || '',
        author: metaData.author || '',
        publicationYear: metaData.publicationYear || '',
        publisher: metaData.publisher || '',
        isbn: metaData.isbn || '',
        genre: [], // 需要用户手动选择
        description: metaData.description || '',
        totalPages: metaData.pages || '',
        language: metaData.language || '',
        bookMarks,
        fileId: bookId,
        coverImageId: imgId
      }
      selectedFile.value = file
    } else {
      console.error('获取元数据失败:', response.data.msg)
      alert('获取元数据失败，请重试')
    }
  } catch (error) {
    console.error('获取元数据失败:', error)
    alert('获取元数据失败，请重试')
  } finally {
    loading.value = false
  }
}

// 删除已选文件
const removeFile = () => {
  selectedFile.value = null
  fileInput.value.value = ''
}

// 检查导入表单是否有效
const isImportValid = computed(() => {
  return importForm.value.name && 
         importForm.value.author && 
         importForm.value.genre.length > 0 && 
         importForm.value.description && 
         selectedFile.value
})

// 提交导入
const submitImport = async () => {
  if (!isImportValid.value) return
  
  try {
    loading.value = true
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    
    // 准备书籍数据
    const bookData = {
      name: importForm.value.name,
      author: importForm.value.author,
      publicationYear: parseInt(importForm.value.publicationYear) || null,
      publisher: importForm.value.publisher,
      isbn: importForm.value.isbn,
      genre: importForm.value.genre.join(','), // 将数组转换为逗号分隔的字符串
      description: importForm.value.description,
      totalPages: parseInt(importForm.value.totalPages) || null,
      language: importForm.value.language,
      structure: JSON.stringify(importForm.value.bookMarks), // 将目录树转换为JSON字符串
      userId: route.params.id, // 添加用户ID
      uploadTime: new Date().toISOString(), // 添加上传时间
      fileId: importForm.value.fileId,
      coverImageId: importForm.value.coverImageId
    }

    // 将bookData添加到FormData中
    Object.entries(bookData).forEach(([key, value]) => {
      if (value !== null && value !== undefined) {
        formData.append(key, value)
      }
    })

    const token = localStorage.getItem('token')
    const response = await axios.post(API.IMPORT_BOOK, formData, {
      headers: {
        'token': token,
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response.data.code === 200) {
      alert('书籍上传成功')
      cancelImport()
    } else {
      console.error('上传失败:', response.data.msg)
      alert(response.data.msg || '上传失败，请重试')
    }
  } catch (error) {
    console.error('上传失败:', error)
    alert('上传失败，请重试')
  } finally {
    loading.value = false
  }
}

// 取消导入
const cancelImport = () => {
  importForm.value = {
    name: '',
    author: '',
    publicationYear: '',
    publisher: '',
    isbn: '',
    genre: [],
    description: '',
    totalPages: '',
    language: '',
    bookMarks: [],
    fileId: '',
    coverImageId: ''
  }
  selectedFile.value = null
  fileInput.value.value = ''
}

// 添加分类
const addGenre = () => {
  if (!genreInput.value) return
  const value = genreInput.value.trim()
  if (value && !importForm.value.genre.includes(value)) {
    importForm.value.genre.push(value)
  }
  genreInput.value = ''
  showOptions.value = false
}

// 选择分类
const selectGenre = (value) => {
  if (!importForm.value.genre.includes(value)) {
    importForm.value.genre.push(value)
  }
  genreInput.value = ''
  showOptions.value = false
}

// 删除分类
const removeGenre = (index) => {
  importForm.value.genre.splice(index, 1)
}

// 处理删除键
const handleDelete = (e) => {
  if (e.key === 'Backspace' && !genreInput.value && importForm.value.genre.length > 0) {
    importForm.value.genre.pop()
  }
}

// 点击外部关闭选项列表
const handleClickOutside = (e) => {
  if (!e.target.closest('.multi-select')) {
    showOptions.value = false
  }
}

// 更新书籍数据
const updateBookData = async () => {
  if (!isImportValid.value) return
  
  try {
    loading.value = true
    // 准备书籍数据
    const bookData = {
      name: importForm.value.name,
      author: importForm.value.author,
      publicationYear: parseInt(importForm.value.publicationYear) || null,
      publisher: importForm.value.publisher,
      isbn: importForm.value.isbn,
      genre: importForm.value.genre.join(','),
      description: importForm.value.description,
      totalPages: parseInt(importForm.value.totalPages) || null,
      language: importForm.value.language,
      structure: JSON.stringify(importForm.value.bookMarks),
      userId: route.params.id,
      uploadTime: new Date().toISOString(),
      fileId: importForm.value.fileId,
      coverImageId: importForm.value.coverImageId
    }

    const token = localStorage.getItem('token')
    const response = await axios.post('http://localhost:8080/ishareReading/book/uploadUpdateBook', bookData, {
      headers: {
        'token': token,
        'Content-Type': 'application/json'
      }
    })

    if (response.data.code === 200) {
      alert('书籍数据已成功上传至云端')
      // 刷新用户数据
      await Promise.all([
        fetchUserInfo(),
        fetchUserStats()
      ])
    } else {
      console.error('上传失败:', response.data.msg)
      alert(response.data.msg || '上传失败，请重试')
    }
  } catch (error) {
    console.error('上传失败:', error)
    alert('上传失败，请重试')
  } finally {
    loading.value = false
  }
}

// 在组件挂载时获取书籍类型
onMounted(() => {
  fetchUserInfo()
  fetchBookTypes()
  // 设置假数据
  readingHistory.value = mockReadingHistory
  favoriteBooks.value = mockFavoriteBooks
  notes.value = mockNotes
  posts.value = mockPosts
  followers.value = mockFollowers
  following.value = mockFollowing
  
  // 监听导航栏的导入书籍事件
  window.addEventListener('show-import-dialog', () => {
    currentTab.value = 'import'
    // 滚动到导入区域
    const importSection = document.querySelector('.import-section')
    if (importSection) {
      importSection.scrollIntoView({ behavior: 'smooth' })
    }
  })
  
  document.addEventListener('click', handleClickOutside)
})

// 组件卸载时清理 blob URL
onUnmounted(() => {
  if (userInfo.value?.avatar && userInfo.value.avatar.startsWith('blob:')) {
    URL.revokeObjectURL(userInfo.value.avatar)
  }
  window.removeEventListener('show-import-dialog', () => {
    currentTab.value = 'import'
  })
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* 更新颜色方案 */
:root {
  --primary-color: #4f46e5;
  --primary-hover: #4338ca;
  --secondary-color: #10b981;
  --text-primary: #1f2937;
  --text-secondary: #4b5563;
  --border-color: #e5e7eb;
  --success-color: #10b981;
  --warning-color: #f59e0b;
  --error-color: #ef4444;
  --card-bg: #0f172a;
  --hover-bg: #1e293b;
  --header-bg: #0f172a;
  --header-text: #ffffff;
  --header-text-secondary: #e2e8f0;
  --header-text-highlight: #fef08a;
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.user-profile {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  background-color: #f3f4f6;
  min-height: 100vh;
}

.profile-header {
  background: linear-gradient(135deg, var(--header-bg) 0%, #020617 100%);
  border-radius: 16px;
  padding: 3rem;
  color: var(--header-text);
  margin: 2rem 0;
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-lg);
}

.avatar-section {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.avatar {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(254, 240, 138, 0.5);
  box-shadow: 0 0 20px rgba(254, 240, 138, 0.3);
  transition: transform 0.3s ease;
}

.avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 0 30px rgba(254, 240, 138, 0.5);
}

.edit-avatar-btn {
  padding: 0.75rem 1.5rem;
  background-color: #fef08a;
  color: #0f172a;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 700;
  font-size: 1.1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.edit-avatar-btn:hover {
  background-color: #fcd34d;
  transform: translateY(-2px);
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.4);
}

.user-info {
  text-align: center;
}

.username {
  font-size: 2.5rem;
  font-weight: 800;
  margin: 0 0 0.75rem 0;
  color: var(--header-text-highlight);
  text-shadow: 0 4px 6px rgba(0, 0, 0, 0.4);
  letter-spacing: 1px;
}

.email {
  opacity: 1;
  margin: 0 0 1.25rem 0;
  color: var(--header-text);
  font-weight: 600;
  font-size: 1.25rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.self-intro {
  max-width: 600px;
  margin: 0 auto 2rem;
  line-height: 1.8;
  color: var(--header-text);
  opacity: 1;
  font-size: 1.2rem;
  font-weight: 500;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.user-stats {
  display: flex;
  justify-content: center;
  gap: 3rem;
  background: rgba(0, 0, 0, 0.3);
  padding: 2rem;
  border-radius: 16px;
  border: 1px solid rgba(254, 240, 138, 0.2);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
}

.stat-value {
  font-size: 2.25rem;
  font-weight: 800;
  color: var(--header-text-highlight);
  text-shadow: 0 4px 6px rgba(0, 0, 0, 0.4);
}

.stat-label {
  font-size: 1.1rem;
  opacity: 1;
  color: var(--header-text);
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: 1px;
}

.tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  border-bottom: 1px solid rgba(254, 240, 138, 0.2);
  padding-bottom: 1rem;
  overflow-x: auto;
  scrollbar-width: none;
}

.tabs::-webkit-scrollbar {
  display: none;
}

.tab-button {
  padding: 0.75rem 1.5rem;
  border: none;
  background: none;
  color: var(--header-text);
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 8px;
  white-space: nowrap;
  font-weight: 500;
}

.tab-button:hover {
  color: var(--header-text-highlight);
  background: rgba(254, 240, 138, 0.1);
}

.tab-button.active {
  color: var(--header-text-highlight);
  background: rgba(254, 240, 138, 0.1);
  font-weight: 600;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 0;
  color: var(--header-text);
  background: rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  box-shadow: var(--shadow-lg);
  border: 1px solid rgba(254, 240, 138, 0.1);
}

.empty-state p {
  font-size: 1.1rem;
  margin: 0;
  font-weight: 500;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f4f6;
  border-top-color: #2563eb;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.history-item,
.favorite-item,
.note-item,
.list-item {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 1rem;
  box-shadow: var(--shadow-lg);
  border: 1px solid rgba(254, 240, 138, 0.1);
  color: var(--header-text);
}

.book-cover {
  width: 100px;
  height: 140px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(254, 240, 138, 0.2);
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.book-item:hover .book-cover img {
  transform: scale(1.05);
}

.book-info {
  flex: 1;
}

.book-info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--header-text-highlight);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.book-info p {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: var(--header-text);
  opacity: 0.9;
}

.book-meta {
  display: flex;
  gap: 1.5rem;
  color: var(--header-text);
  font-size: 0.9rem;
  opacity: 0.9;
  margin-bottom: 1rem;
}

.reading-time,
.progress-text {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.reading-time::before {
  content: "🕒";
  font-size: 1rem;
}

.progress-text::before {
  content: "📖";
  font-size: 1rem;
}

.reading-progress {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 0.5rem;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: var(--header-text-highlight);
  border-radius: 3px;
  transition: width 0.3s;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.note-header h3 {
  color: var(--header-text-highlight);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.note-date {
  color: var(--header-text);
  font-size: 0.9rem;
  opacity: 0.9;
}

.note-content {
  margin-bottom: 0.5rem;
  color: var(--header-text);
  line-height: 1.6;
}

.note-meta {
  display: flex;
  gap: 1rem;
  color: var(--header-text);
  font-size: 0.9rem;
  opacity: 0.9;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.item-header h3 {
  color: var(--header-text-highlight);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.item-date {
  color: var(--header-text);
  font-size: 0.9rem;
  opacity: 0.9;
}

.item-content {
  margin-bottom: 0.5rem;
  color: var(--header-text);
  line-height: 1.6;
}

.item-meta {
  display: flex;
  gap: 1rem;
  color: var(--header-text);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--header-text);
}

.icon-like,
.icon-comment,
.icon-view,
.icon-post,
.icon-follower,
.icon-following {
  font-size: 1rem;
  color: var(--header-text-highlight);
}

/* 收藏书籍样式 */
.favorites-list {
  padding: 1rem 0;
}

.book-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  padding: 0 1rem;
}

.book-item {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-color);
  display: flex;
  gap: 1.5rem;
  transition: all 0.2s ease;
  margin-bottom: 1rem;
}

.book-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
  background: var(--hover-bg);
}

.book-cover {
  width: 100px;
  height: 140px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(254, 240, 138, 0.2);
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.book-item:hover .book-cover img {
  transform: scale(1.05);
}

.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.book-info h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 0.5rem 0;
}

.book-info p {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin: 0 0 0.5rem 0;
}

.book-meta {
  display: flex;
  gap: 1.5rem;
  color: var(--header-text);
  font-size: 0.9rem;
  opacity: 0.9;
  margin-bottom: 1rem;
}

.category {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0.75rem;
  background: rgba(254, 240, 138, 0.1);
  border: 1px solid rgba(254, 240, 138, 0.2);
  border-radius: 20px;
  color: var(--header-text-highlight);
}

.word-count {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.word-count::before {
  content: "📚";
  font-size: 1rem;
}

.book-description {
  font-size: 0.875rem;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .book-item,
  .note-item,
  .list-item {
    flex-direction: column;
    gap: 1rem;
  }

  .book-cover {
    width: 80px;
    height: 120px;
  }

  .user-stats {
    flex-direction: column;
    gap: 1rem;
  }
}

.follow-button {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid var(--header-text-highlight);
  background: transparent;
  color: var(--header-text-highlight);
  min-width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.follow-button.mutual {
  background: var(--header-text-highlight);
  color: #0f172a;
  border-color: var(--header-text-highlight);
}

.follow-button.following {
  background: rgba(254, 240, 138, 0.2);
  color: var(--header-text-highlight);
  border-color: var(--header-text-highlight);
}

.follow-button.none {
  background: transparent;
  color: var(--header-text-highlight);
  border-color: var(--header-text-highlight);
}

.follow-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 响应式布局 */
@media (max-width: 768px) {
  .list-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .follow-button {
    width: 100%;
  }
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 1rem;
  border: 2px solid rgba(254, 240, 138, 0.3);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.user-avatar:hover img {
  transform: scale(1.1);
}

/* 响应式布局 */
@media (max-width: 768px) {
  .book-cover {
    width: 80px;
    height: 120px;
  }

  .user-avatar {
    width: 50px;
    height: 50px;
  }
}

/* 导入数据样式 */
.import-section {
  padding: 2rem;
}

.import-header {
  margin-bottom: 2rem;
}

.import-header h3 {
  font-size: 1.5rem;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.import-header p {
  color: var(--text-secondary);
}

.import-content {
  display: flex;
  gap: 2rem;
}

.import-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  color: var(--text-primary);
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--card-bg);
  color: var(--text-primary);
}

.form-group textarea {
  min-height: 120px;
  resize: vertical;
}

.import-upload {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-area {
  width: 100%;
  height: 200px;
  border: 2px dashed var(--border-color);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: var(--primary-color);
  background: rgba(37, 99, 235, 0.05);
}

.upload-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.upload-text {
  font-size: 1.1rem;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.upload-hint {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.selected-file {
  margin-top: 1rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.75rem;
  background: var(--card-bg);
  border-radius: 8px;
  width: 100%;
}

.selected-file span {
  flex: 1;
  color: var(--text-primary);
}

.selected-file button {
  padding: 0.5rem 1rem;
  background: var(--danger-color);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.import-actions {
  margin-top: 2rem;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.cancel-btn,
.update-btn,
.submit-btn {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
}

.cancel-btn {
  background: var(--card-bg);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.update-btn {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  background: #3b82f6; /* 蓝色背景 */
  color: white;
  border: none;
  margin-right: 1rem;
}

.update-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.update-btn:not(:disabled):hover {
  background: #2563eb; /* 深蓝色悬停效果 */
}

.submit-btn {
  background: var(--primary-color);
  color: white;
  border: none;
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.directory-tree {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 1rem;
  max-height: 300px;
  overflow-y: auto;
}

.tree-item {
  margin-bottom: 0.5rem;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

.tree-title {
  color: var(--text-primary);
  font-weight: 500;
}

.tree-page {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.tree-children {
  margin-left: 1.5rem;
  margin-top: 0.5rem;
}

.tree-children .tree-node {
  background: rgba(0, 0, 0, 0.05);
}

.multi-select {
  position: relative;
  width: 100%;
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.tag {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.75rem;
  background: #1e40af; /* 深蓝色背景 */
  color: white;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.remove-tag {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 1rem;
  padding: 0;
  line-height: 1;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.remove-tag:hover {
  opacity: 1;
}

.select-input {
  position: relative;
}

.select-input input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: #1e293b; /* 深色背景 */
  color: white;
  font-size: 0.875rem;
}

.select-input input:focus {
  outline: none;
  border-color: #3b82f6; /* 蓝色边框 */
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.options-list {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  max-height: 200px;
  overflow-y: auto;
  background: #1e293b; /* 深色背景 */
  border: 1px solid #334155;
  border-radius: 8px;
  margin-top: 0.25rem;
  z-index: 1000; /* 提高层级 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
}

.option {
  padding: 0.75rem 1rem;
  cursor: pointer;
  color: #e2e8f0;
  transition: all 0.2s;
}

.option:hover {
  background: #334155;
  color: white;
}

.option.selected {
  background: #3b82f6; /* 蓝色选中状态 */
  color: white;
  font-weight: 500;
}

/* 滚动条样式 */
.options-list::-webkit-scrollbar {
  width: 6px;
}

.options-list::-webkit-scrollbar-track {
  background: #1e293b;
}

.options-list::-webkit-scrollbar-thumb {
  background: #475569;
  border-radius: 3px;
}

.options-list::-webkit-scrollbar-thumb:hover {
  background: #64748b;
}
</style> 