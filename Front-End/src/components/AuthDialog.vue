<template>
  <div class="auth-dialog-overlay" v-if="show" @click="close">
    <div class="auth-dialog" @click.stop>
      <div class="auth-dialog-content">
        <!-- 左侧团队信息 -->
        <div class="team-info">
          <div class="team-logo">
            <img src="@/assets/ishare.png" alt="ishare logo" />
          </div>
          <h2 class="team-title">ishare 团队</h2>
          <p class="team-desc">致力于打造最智能的阅读助手</p>
          <div class="team-features">
            <div class="feature-item">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M12 2v20M5 17l7 7 7-7" stroke="#1976d2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <span>智能阅读助手</span>
            </div>
            <div class="feature-item">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M12 2v20M5 17l7 7 7-7" stroke="#1976d2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <span>知识库管理</span>
            </div>
            <div class="feature-item">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M12 2v20M5 17l7 7 7-7" stroke="#1976d2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              <span>多维度分析</span>
            </div>
          </div>
        </div>

        <!-- 右侧表单 -->
        <div class="auth-form">
          <div class="form-header">
            <h2>{{ isLogin ? '登录' : '注册' }}</h2>
            <button class="close-button" @click="close">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
              </svg>
            </button>
          </div>

          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <label for="email">邮箱</label>
              <div class="email-input-group">
                <input
                  type="email"
                  id="email"
                  v-model="form.email"
                  placeholder="请输入邮箱"
                  required
                >
                <button 
                  v-if="!isLogin"
                  type="button" 
                  class="email-captcha-button"
                  :disabled="emailCountdown > 0 || !isEmailValid"
                  @click="sendVerificationCode"
                >
                  {{ emailCountdown > 0 ? `${emailCountdown}秒后重试` : '获取验证码' }}
                </button>
              </div>
            </div>

            <div class="form-group" v-if="!isLogin">
              <label for="userName">用户名</label>
              <input
                type="text"
                id="userName"
                v-model="form.userName"
                placeholder="请输入用户名"
                required
              >
            </div>

            <div class="form-group">
              <label for="password">密码</label>
              <input
                type="password"
                id="password"
                v-model="form.password"
                placeholder="请输入密码"
                required
              >
            </div>

            <template v-if="!isLogin">
              <div class="form-group">
                <label for="captcha">图形验证码</label>
                <div class="captcha-input-group">
                  <input
                    type="text"
                    id="captcha"
                    v-model="form.captcha"
                    placeholder="请输入验证码"
                    required
                  >
                  <div class="captcha-image-wrapper">
                    <img 
                      :src="captchaImage" 
                      alt="验证码" 
                      class="captcha-image"
                      @click="refreshCaptcha"
                    />
                  </div>
                </div>
              </div>

              <div class="form-group">
                <label for="emailCaptcha">邮箱验证码</label>
                <input
                  type="text"
                  id="emailCaptcha"
                  v-model="form.emailCaptcha"
                  placeholder="请输入邮箱验证码"
                  required
                >
              </div>
            </template>

            <button type="submit" class="submit-button" :disabled="loading">
              {{ isLogin ? '登录' : '注册' }}
            </button>
          </form>

          <div class="form-footer">
            <span>{{ isLogin ? '还没有账号？' : '已有账号？' }}</span>
            <button class="switch-button" @click="toggleMode">
              {{ isLogin ? '立即注册' : '立即登录' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- 消息提示 -->
  <div class="message-box" v-if="message" :class="{ 'show': message }">
    <div class="message-content">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { generateUUID } from '@/utils/uuid'
import { API } from '@/config/api'
import axios from 'axios'
import { useRouter } from 'vue-router'

const props = defineProps({
  show: {
    type: Boolean,
    required: true
  },
  isLogin: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:show', 'update:isLogin', 'submit', 'sendEmailCaptcha', 'login-success'])

const form = reactive({
  email: '',
  password: '',
  captcha: '',
  emailCaptcha: '',
  userName: ''
})

const emailCountdown = ref(0)
const captchaUUID = ref(generateUUID())
const captchaImage = ref('')

// 邮箱验证正则
const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/

const isEmailValid = ref(false)

// 监听邮箱输入
watch(() => form.email, (newEmail) => {
  isEmailValid.value = emailRegex.test(newEmail)
})

// 监听模式切换，清空表单
watch(() => props.isLogin, () => {
  if (props.isLogin) {
    // 切换到登录模式时，保留邮箱和密码
    form.captcha = ''
    form.emailCaptcha = ''
  } else {
    // 切换到注册模式时，清空所有字段
    form.email = ''
    form.password = ''
    form.captcha = ''
    form.emailCaptcha = ''
    form.userName = ''
    refreshCaptcha()
  }
})

const close = () => {
  emit('update:show', false)
  // 清空表单
  form.email = ''
  form.password = ''
  form.captcha = ''
  form.emailCaptcha = ''
  form.userName = ''
}

const toggleMode = () => {
  emit('update:isLogin', !props.isLogin)
}

const refreshCaptcha = () => {
  captchaUUID.value = generateUUID()
  // 添加时间戳避免缓存
  captchaImage.value = `${API.CAPTCHA(captchaUUID.value)}?t=${Date.now()}`
  showMessage('验证码已刷新')
}

onMounted(() => {
  refreshCaptcha()
})

const message = ref('')
const showMessage = (msg) => {
  message.value = msg
  setTimeout(() => {
    message.value = ''
  }, 2000)
}

const loading = ref(false)

// 发送验证码
const sendVerificationCode = async () => {
  if (!form.email) {
    showMessage('请输入邮箱')
    return
  }
  if (!isEmailValid.value) {
    showMessage('请输入正确的邮箱格式')
    return
  }
  if (!form.captcha) {
    showMessage('请输入图形验证码')
    return
  }
  try {
    const response = await axios.post(API.SEND_EMAIL_CODE, {
      code: form.captcha,
      uuid: captchaUUID.value,
      email: form.email
    })
    if (response.data.code === 200) {
      showMessage('验证码已发送')
      // 开始倒计时
      emailCountdown.value = 60
      const timer = setInterval(() => {
        emailCountdown.value--
        if (emailCountdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    } else {
      showMessage(response.data.msg || '发送失败')
      // 刷新图形验证码
      refreshCaptcha()
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    showMessage('发送失败，请重试')
    // 刷新图形验证码
    refreshCaptcha()
  }
}

// 处理表单提交
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }
  
  try {
    loading.value = true
    if (props.isLogin) {
      // 登录逻辑
      const response = await axios.post(API.LOGIN, {
        email: form.email,
        password: form.password
      })
      if (response.data.code === 200) {
        const userData = response.data.data
        // 保存用户信息和token
        localStorage.setItem('userInfo', JSON.stringify({
          name: userData.name,
          user: {
            id: userData.user.id,
            email: userData.user.email
          }
        }))
        localStorage.setItem('token', userData.token)
        // 设置axios默认headers
        axios.defaults.headers.common['token'] = userData.token
        showMessage('登录成功')
        // 延迟1.5秒后关闭对话框并触发登录成功事件
        setTimeout(() => {
          emit('login-success', {
            name: userData.name,
            user: {
              id: userData.user.id,
              email: userData.user.email
            }
          })
          emit('update:show', false)
        }, 1500)
      } else {
        showMessage(response.data.msg || '登录失败')
      }
    } else {
      // 注册逻辑
      const response = await axios.post(API.REGISTER, {
        emailCode: form.emailCaptcha,
        user: {
          email: form.email,
          userName: form.userName,
          password: form.password
        }
      })
      if (response.data.code === 200) {
        showMessage('注册成功，请登录')
        emit('update:isLogin', true)
        // 清空表单
        form.email = ''
        form.password = ''
        form.captcha = ''
        form.emailCaptcha = ''
        form.userName = ''
      } else {
        showMessage(response.data.msg || '注册失败')
      }
    }
  } catch (error) {
    console.error(props.isLogin ? '登录失败:' : '注册失败:', error)
    showMessage(props.isLogin ? '登录失败，请重试' : '注册失败，请重试')
  } finally {
    loading.value = false
  }
}

// 表单验证
const validateForm = () => {
  if (!form.email) {
    showMessage('请输入邮箱')
    return false
  }
  if (!isEmailValid.value) {
    showMessage('请输入正确的邮箱格式')
    return false
  }
  if (!form.password) {
    showMessage('请输入密码')
    return false
  }
  if (!props.isLogin) {
    if (!form.userName) {
      showMessage('请输入用户名')
      return false
    }
    if (!form.captcha) {
      showMessage('请输入图形验证码')
      return false
    }
    if (!form.emailCaptcha) {
      showMessage('请输入邮箱验证码')
      return false
    }
  }
  return true
}
</script>

<style scoped>
.auth-dialog-overlay {
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

.auth-dialog {
  background-color: #ffffff;
  border-radius: 12px;
  width: 1600px;
  max-width: 90vw;
  max-height: 90vh;
  overflow: hidden;
}

.auth-dialog-content {
  display: flex;
  height: 1000px;
}

.team-info {
  flex: 1;
  background-color: #f3f4f6;
  padding: 3rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.team-logo {
  width: 180px;
  height: 180px;
  margin-bottom: 2.5rem;
}

.team-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.team-title {
  font-size: 2rem;
  font-weight: 600;
  color: #333333;
  margin-bottom: 2rem;
}

.team-desc {
  font-size: 1.2rem;
  color: #666666;
  line-height: 1.6;
  margin-bottom: 3rem;
}

.team-features {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  background-color: #ffffff;
  border-radius: 8px;
  color: #333333;
}

.feature-item svg {
  color: #2563eb;
}

.auth-form {
  flex: 1;
  padding: 3rem;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.form-header h2 {
  font-size: 2rem;
  font-weight: 600;
  color: #333333;
}

.close-button {
  background: none;
  border: none;
  cursor: pointer;
  color: #666666;
  padding: 0.5rem;
}

.close-button:hover {
  color: #333333;
}

form {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  min-height: 0;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  flex-shrink: 0;
}

.form-group label {
  font-size: 0.875rem;
  color: #666666;
}

.form-group input {
  padding: 1.2rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 1.2rem;
  transition: all 0.2s;
}

.form-group input:focus {
  border-color: #2563eb;
  outline: none;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
}

.submit-button {
  margin-top: auto;
  padding: 1.2rem;
  background-color: #2563eb;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 1.2rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}

.submit-button:hover {
  background-color: #1d4ed8;
}

.form-footer {
  margin-top: 1.5rem;
  text-align: center;
  color: #666666;
}

.switch-button {
  background: none;
  border: none;
  color: #2563eb;
  cursor: pointer;
  font-weight: 500;
  padding: 0.25rem;
  transition: all 0.2s;
}

.switch-button:hover {
  color: #1d4ed8;
  text-decoration: underline;
}

.captcha-input-group {
  display: flex;
  gap: 0.75rem;
}

.captcha-input-group input {
  flex: 1;
}

.captcha-image-wrapper {
  position: relative;
  width: 160px;
  height: 60px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.captcha-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  transition: opacity 0.2s;
}

.captcha-image:hover {
  opacity: 0.9;
}

.email-input-group {
  display: flex;
  gap: 0.75rem;
}

.email-input-group input {
  flex: 1;
}

.email-captcha-button {
  padding: 0.75rem 1rem;
  background-color: #f3f4f6;
  color: #666666;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.email-captcha-button:hover:not(:disabled) {
  background-color: #e5e7eb;
  color: #333333;
}

.email-captcha-button:disabled {
  cursor: not-allowed;
  opacity: 0.7;
  background-color: #f3f4f6;
  color: #999;
}

.message-box {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  pointer-events: none;
}

.message-content {
  background-color: #2563eb;
  color: #fff;
  padding: 16px 32px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  min-width: 200px;
  text-align: center;
  opacity: 0;
  transform: scale(0.9);
  transition: all 0.3s ease;
}

.message-box.show .message-content {
  opacity: 1;
  transform: scale(1);
}
</style> 