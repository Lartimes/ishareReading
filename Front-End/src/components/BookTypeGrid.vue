<template>
  <div class="book-type-grid">
    <div class="grid-header">
      <h2>图书分类</h2>
      <router-link to="/ishare/search" class="view-all">查看全部</router-link>
    </div>
    <div class="grid-content">
      <div v-if="loading" class="loading-spinner">
        <div class="spinner"></div>
      </div>
      <div v-else class="type-grid">
        <div 
          v-for="type in displayedTypes" 
          :key="type.id" 
          class="type-card"
          @click="handleTypeClick(type.typeName)"
        >
          <div class="type-image">
            <img :src="getRandomImage(type.typeName)" :alt="type.typeName">
          </div>
          <div class="type-info">
            <h3>{{ type.typeName }}</h3>
            <p>{{ type.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { API } from '@/config/api'

const router = useRouter()
const types = ref([])
const loading = ref(true)

// 本地图片列表
const localImages = [
  '/src/assets/book-covers/心理.jpg',
  '/src/assets/book-covers/计算机.jpg',
  '/src/assets/book-covers/哲学宗教.jpg',
  '/src/assets/book-covers/艺术.jpg',
  '/src/assets/book-covers/文学.jpg',
  '/src/assets/book-covers/历史.jpg',
  '/src/assets/book-covers/精品小说.jpg',
  '/src/assets/book-covers/人物传记.jpg'
]

// 获取随机图片
const getRandomImage = (typeName) => {
  // 如果是计算机分类，固定使用计算机图片
  if (typeName === '计算机') {
    return '/src/assets/book-covers/计算机.jpg'
  }
  // 随机选择一个图片
  const randomIndex = Math.floor(Math.random() * localImages.length)
  return localImages[randomIndex]
}

// 获取书籍类型
const fetchBookTypes = async () => {
  try {
    loading.value = true
    const token = localStorage.getItem('token')
    const response = await axios.get(API.GET_BOOK_TYPES, {
      headers: {
        'token': token
      }
    })

    if (response.data.code === 200) {
      types.value = response.data.data
    } else {
      console.error('获取书籍类型失败:', response.data.msg)
    }
  } catch (error) {
    console.error('获取书籍类型失败:', error)
  } finally {
    loading.value = false
  }
}

// 计算要显示的类型
const displayedTypes = computed(() => {
  if (!types.value) return []
  
  // 找到计算机分类
  const computerType = types.value.find(type => type.typeName === '计算机')
  
  // 过滤掉计算机分类，然后随机打乱其他分类
  const otherTypes = types.value
    .filter(type => type.typeName !== '计算机')
    .sort(() => Math.random() - 0.5)
    .slice(0, 7)
  
  // 如果存在计算机分类，放在第一位
  return computerType 
    ? [computerType, ...otherTypes]
    : otherTypes
})

// 总类型数量
const totalTypes = computed(() => types.value.length)

// 处理类型点击
const handleTypeClick = (typeName) => {
  router.push({
    path: '/ishare/search',
    query: { type: typeName }
  })
}

// 初始化
onMounted(() => {
  fetchBookTypes()
})
</script>

<style scoped>
.book-type-grid {
  margin-top: 48px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 18px;
  box-shadow: var(--shadow-lg);
  border: 1px solid rgba(254, 240, 138, 0.1);
  padding: 32px 32px 24px 32px;
  max-width: 100%;
}

.grid-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.grid-header h2 {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--header-text-highlight);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.view-all {
  font-size: 0.98rem;
  color: var(--header-text);
  text-decoration: none;
  transition: all 0.2s;
}

.view-all:hover {
  color: var(--header-text-highlight);
}

.loading-spinner {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(254, 240, 138, 0.2);
  border-top-color: var(--header-text-highlight);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 28px;
}

.type-card {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 14px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 18px 12px 12px 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s;
  cursor: pointer;
  border: 1px solid rgba(254, 240, 138, 0.1);
}

.type-card:hover {
  transform: translateY(-2px);
  background: rgba(0, 0, 0, 0.4);
  border-color: var(--header-text-highlight);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.type-image {
  width: 54px;
  height: 74px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(254, 240, 138, 0.2);
}

.type-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.type-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.type-info h3 {
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--header-text-highlight);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.type-info p {
  font-size: 0.92rem;
  color: var(--header-text);
  opacity: 0.9;
}

@media (max-width: 1200px) {
  .type-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 700px) {
  .type-grid {
    grid-template-columns: 1fr;
  }
}
</style> 