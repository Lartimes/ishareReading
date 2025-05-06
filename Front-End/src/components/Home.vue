<template>
  <div class="home">
    <div class="book-types-section">
      <h2 class="section-title">热门书籍</h2>
      <div v-if="loading" class="loading-spinner">
        <div class="spinner"></div>
      </div>
      <div v-else class="book-grid">
        <div 
          v-for="book in books" 
          :key="book.id" 
          class="book-item"
          @click="selectBook(book)"
        >
          <div class="book-cover">
            <img :src="book.cover" :alt="book.title">
          </div>
          <div class="book-info">
            <h3 class="book-title">{{ book.title }}</h3>
            <p class="book-author">{{ book.author }}</p>
            <div class="book-meta">
              <span class="category">{{ book.category }}</span>
              <span class="word-count">{{ book.wordCount }}字</span>
            </div>
          </div>
        </div>
      </div>
      <router-link to="/books" class="view-all">
        查看全部书籍
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const books = ref([])
const loading = ref(true)

// 获取随机书籍
const fetchRandomBooks = async () => {
  try {
    loading.value = true
    const token = localStorage.getItem('token')
    const response = await axios.get('http://localhost:8080/ishareReading/book/random?count=6', {
      headers: {
        'token': token
      }
    })

    if (response.data.code === 200) {
      books.value = response.data.data
    } else {
      console.error('获取随机书籍失败:', response.data.msg)
    }
  } catch (error) {
    console.error('获取随机书籍失败:', error)
  } finally {
    loading.value = false
  }
}

// 选择书籍
const selectBook = (book) => {
  router.push({
    path: `/book/${book.id}`
  })
}

// 初始化
onMounted(() => {
  fetchRandomBooks()
})
</script>

<style scoped>
.book-types-section {
  margin-bottom: 2rem;
  padding: 2rem;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 16px;
  box-shadow: var(--shadow-lg);
  border: 1px solid rgba(254, 240, 138, 0.1);
}

.section-title {
  color: var(--header-text-highlight);
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
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

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.book-item {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(254, 240, 138, 0.1);
  border-radius: 12px;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.book-item:hover {
  transform: translateY(-2px);
  background: rgba(0, 0, 0, 0.4);
  border-color: var(--header-text-highlight);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
}

.book-cover {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
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
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.book-title {
  color: var(--header-text-highlight);
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.book-author {
  color: var(--header-text);
  font-size: 0.9rem;
  opacity: 0.9;
  margin: 0;
}

.book-meta {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
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
  font-size: 0.8rem;
}

.word-count {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--header-text);
  font-size: 0.8rem;
  opacity: 0.9;
}

.view-all {
  display: block;
  text-align: center;
  color: var(--header-text-highlight);
  font-size: 1.1rem;
  font-weight: 500;
  text-decoration: none;
  padding: 0.75rem;
  border: 1px solid rgba(254, 240, 138, 0.2);
  border-radius: 8px;
  transition: all 0.2s;
}

.view-all:hover {
  background: rgba(254, 240, 138, 0.1);
  border-color: var(--header-text-highlight);
}

@media (max-width: 768px) {
  .book-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
  
  .book-cover {
    height: 160px;
  }
}
</style> 