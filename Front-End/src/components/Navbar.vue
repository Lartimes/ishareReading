<template>
  <nav class="navbar">
    <div class="navbar-inner">
      <div class="navbar-logo">
        <router-link to="/">
          <img src="@/assets/ishare.png" alt="ishare logo" class="logo-img" />
        </router-link>
      </div>
      <div class="navbar-menu">
        <router-link to="/" class="navbar-link"><span class="icon">🏠</span> 首页</router-link>
        <router-link to="/categories" class="navbar-link"><span class="icon">📚</span> 分类</router-link>
        <router-link to="/ranking" class="navbar-link"><span class="icon">📈</span> 排行榜</router-link>
        <router-link to="/booklists" class="navbar-link"><span class="icon">🔖</span> 书单</router-link>
        <router-link to="/user" class="navbar-link" @click="showImportDialog"><span class="icon">📥</span> 导入书籍</router-link>
      </div>
      <div class="navbar-search">
        <input 
          type="text" 
          v-model="searchQuery"
          @keyup.enter="handleSearch"
          placeholder="搜索书籍或作者…" 
        />
        <button class="search-btn" @click="handleSearch">🔍</button>
      </div>
      <div 
        class="navbar-user"
        ref="userDropdown"
        @click="toggleDropdown"
      >
        <span class="user-icon">👤</span>
        <span v-if="isLoggedIn" @click.stop="goToUserProfile" class="user-name">{{ userInfo.name }}</span>
        <span v-else>用户中心</span>
        <transition name="fade">
          <div class="user-dropdown" v-if="showDropdown">
            <template v-if="isLoggedIn">
              <button class="dropdown-item" @click="goToUserProfile">个人中心</button>
              <button class="dropdown-item" @click="handleLogout">退出登录</button>
            </template>
            <template v-else>
              <button class="dropdown-item" @click="showAuthDialog('login')">登录</button>
              <button class="dropdown-item" @click="showAuthDialog('register')">注册</button>
              <button class="dropdown-item" @click="handleForgotPassword">找回密码</button>
            </template>
          </div>
        </transition>
      </div>
      <button class="theme-btn" @click="toggleTheme">
        <span v-if="theme === 'light'">🌙</span>
        <span v-else>☀️</span>
      </button>
    </div>
  </nav>
  <AuthDialog 
    v-model:show="showAuth"
    v-model:isLogin="isLogin"
    @submit="handleAuthSubmit"
    @login-success="handleLoginSuccess"
  />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AuthDialog from './AuthDialog.vue'

const router = useRouter()
const showDropdown = ref(false)
const searchQuery = ref('')
const theme = ref('light')
const showAuth = ref(false)
const isLogin = ref(true)

// 添加用户信息状态
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
const isLoggedIn = computed(() => !!userInfo.value)

// 跳转到用户详情页
const goToUserProfile = () => {
  console.log('Current user info:', userInfo.value)
  if (userInfo.value?.user?.id) {
    console.log('Navigating to user profile with ID:', userInfo.value.user.id)
    router.push({ name: 'user-profile', params: { id: userInfo.value.user.id } })
      .then(() => {
        console.log('Navigation successful')
      })
      .catch(err => {
        console.error('Navigation failed:', err)
      })
    showDropdown.value = false
  } else {
    console.error('No user ID found')
  }
}

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const handleClickOutside = (event) => {
  if (event.target.closest('.navbar-user')) return
  showDropdown.value = false
}

const handleSearch = () => {
  if (!searchQuery.value.trim()) return
  router.push({
    path: '/ishare/search',
    query: { q: searchQuery.value.trim() }
  })
  searchQuery.value = ''
}

const setTheme = (t) => {
  document.documentElement.setAttribute('data-theme', t)
  theme.value = t
  localStorage.setItem('ilove-theme', t)
}

const toggleTheme = () => {
  setTheme(theme.value === 'light' ? 'dark' : 'light')
}

const showAuthDialog = (type) => {
  isLogin.value = type === 'login'
  showAuth.value = true
  showDropdown.value = false
}

const handleAuthSubmit = (formData) => {
  console.log('Auth form submitted:', formData)
  // 处理登录/注册逻辑
}

const handleForgotPassword = () => {
  // 处理找回密码逻辑
  showDropdown.value = false
}

// 处理登录成功事件
const handleLoginSuccess = (userData) => {
  console.log('Login success, user data:', userData)
  userInfo.value = userData
  localStorage.setItem('userInfo', JSON.stringify(userData))
  showDropdown.value = false
}

// 处理退出登录
const handleLogout = () => {
  localStorage.removeItem('userInfo')
  localStorage.removeItem('token')
  userInfo.value = null
  showDropdown.value = false
}

const showImportDialog = () => {
  // 这里会通过事件总线触发导入对话框
  window.dispatchEvent(new CustomEvent('show-import-dialog'))
}

// 在组件挂载时检查用户信息
onMounted(() => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)
      console.log('Loaded user info from storage:', userInfo.value)
    } catch (error) {
      console.error('Failed to parse user info:', error)
    }
  }
})

// 监听点击外部事件
document.addEventListener('click', handleClickOutside)
</script>

<style scoped>
:root {
  --navbar-bg: #2563eb;
  --navbar-text: #fff;
  --navbar-hover: #ff9800;
  --search-bg: rgba(255,255,255,0.85);
  --search-border: #c7d7fa;
  --search-text: #222;
  --search-focus: #2563eb;
  --search-bg-focus: #e3eaff;
  --search-shadow: 0 2px 8px rgba(37,99,235,0.06);
}
[data-theme='dark'] {
  --navbar-bg: #181a20;
  --navbar-text: #f3f4f6;
  --navbar-hover: #3b82f6;
  --search-bg: rgba(35,38,47,0.85);
  --search-border: #3b82f6;
  --search-text: #f3f4f6;
  --search-focus: #3b82f6;
  --search-bg-focus: #23262f;
  --search-shadow: 0 2px 8px rgba(59,130,246,0.10);
}

.navbar {
  background: var(--navbar-bg);
  color: var(--navbar-text);
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 48px;
  z-index: 1000;
  box-sizing: border-box;
  display: flex;
  align-items: center;
}

.navbar-inner {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
  width: 100%;
  display: flex;
  align-items: center;
}

.navbar-logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  margin-right: 32px;
  flex-shrink: 0;
}

.logo-img {
  height: 32px;
  width: auto;
  margin-right: 6px;
  filter: drop-shadow(0 2px 8px rgba(37,99,235,0.10));
}

.navbar-menu {
  display: flex;
  align-items: center;
  flex: 1 1 auto;
  min-width: 0;
}

.navbar-link {
  color: var(--navbar-text);
  text-decoration: none;
  margin-right: 24px;
  font-size: 16px;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}

.navbar-link:hover,
.navbar-link.router-link-active {
  color: var(--navbar-hover);
}

.icon {
  margin-right: 4px;
  font-size: 16px;
}

.navbar-search {
  background: var(--search-bg);
  border: 1.5px solid var(--search-border);
  border-radius: 10px;
  padding: 4px 8px;
  display: flex;
  align-items: center;
  margin-right: 24px;
  box-shadow: var(--search-shadow);
  height: 36px;
  transition: border 0.2s, box-shadow 0.2s, background 0.2s;
  backdrop-filter: blur(2px);
}

.navbar-search:focus-within {
  border-color: var(--search-focus);
  background: var(--search-bg-focus);
  box-shadow: 0 4px 16px rgba(37,99,235,0.10);
}

.navbar-search input {
  background: transparent;
  border: none;
  outline: none;
  padding: 0 8px;
  font-size: 15px;
  color: var(--search-text);
  height: 28px;
  line-height: 28px;
}

.search-btn {
  background: linear-gradient(90deg, var(--navbar-hover) 0%, var(--navbar-bg) 100%);
  border: none;
  color: #fff;
  padding: 4px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  margin-left: 8px;
  transition: background 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 4px rgba(37,99,235,0.08);
  display: flex;
  align-items: center;
}

.search-btn:hover {
  background: linear-gradient(90deg, var(--navbar-bg) 0%, var(--navbar-hover) 100%);
  box-shadow: 0 2px 8px rgba(37,99,235,0.16);
}

.navbar-user {
  color: var(--navbar-text);
  display: flex;
  align-items: center;
  font-size: 15px;
  position: relative;
  cursor: pointer;
  user-select: none;
  flex-shrink: 0;
  margin-left: 8px;
  transition: color 0.2s;
}

.navbar-user:hover {
  color: var(--navbar-hover);
}

.user-icon {
  margin-right: 4px;
  font-size: 18px;
}

.user-dropdown {
  position: absolute;
  top: 110%;
  right: 0;
  background: #fff;
  color: #393e43;
  min-width: 120px;
  max-width: calc(100vw - 16px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  border-radius: 4px;
  overflow: hidden;
  z-index: 2000;
  display: flex;
  flex-direction: column;
}

.dropdown-item {
  padding: 10px 18px;
  color: #393e43;
  text-decoration: none;
  font-size: 15px;
  transition: background 0.2s, color 0.2s;
  white-space: nowrap;
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  cursor: pointer;
}

.dropdown-item:hover {
  background: #1976d2;
  color: #fff;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
}

.theme-btn {
  background: transparent;
  border: none;
  margin-left: 16px;
  font-size: 1.3rem;
  cursor: pointer;
  color: var(--navbar-hover);
  transition: color 0.3s, background 0.3s;
  display: flex;
  align-items: center;
  padding: 4px 8px;
  border-radius: 50%;
}
.theme-btn:hover {
  background: #fff2;
  color: var(--navbar-text);
}

.user-name {
  cursor: pointer;
  transition: color 0.2s;
}

.user-name:hover {
  color: var(--navbar-hover);
}
</style>

<style>
:root {
  --ilove-main: #2563eb;
  --ilove-orange: #ff9800;
  --ilove-bg: #f6f7fa;
  --ilove-card: #fcfcfd;
  --ilove-text: #222;
  --ilove-border: #f0f1f3;
  --ilove-shadow: 0 2px 12px rgba(60,60,60,0.06);
  --ilove-gradient: linear-gradient(120deg, #f6f7fa 60%, #e3eaff 100%);
}
[data-theme='dark'] {
  --ilove-main: #3b82f6;
  --ilove-orange: #ffb74d;
  --ilove-bg: #181a20;
  --ilove-card: #23262f;
  --ilove-text: #f3f4f6;
  --ilove-border: #23262f;
  --ilove-shadow: 0 2px 16px rgba(0,0,0,0.18);
  --ilove-gradient: linear-gradient(120deg, #23262f 60%, #181a20 100%);
}
html, body, #app {
  background: var(--ilove-bg);
  color: var(--ilove-text);
  transition: background 0.4s, color 0.4s;
}
body {
  background: var(--ilove-gradient);
  color: var(--ilove-text);
  transition: background 0.4s, color 0.4s;
}
</style>
