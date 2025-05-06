<template>
  <div class="book-detail-page">
    <!-- 左侧主区 -->
    <div class="main-area">
      <div class="book-info-card">
        <img :src="book.cover" class="book-cover" />
        <div class="book-meta">
          <h2 class="book-title">{{ book.title }}</h2>
          <div class="book-author">作者：{{ book.author }}</div>
          <div class="book-stats">
            <span>👍 {{ book.likes }} </span>
            <span>⭐ {{ book.favorites }} </span>
          </div>
          <button class="read-btn" @click="startReading">开始阅读</button>
        </div>
      </div>
      <div class="book-catalog-card" v-if="route.query.api !== 'getBooksHomePageById'">
        <h3>目录</h3>
        <ul>
          <li v-for="(chapter, idx) in book.catalog" :key="idx">{{ chapter }}</li>
        </ul>
      </div>
      <div class="book-desc-card">
        <h3>书籍简介</h3>
        <div class="book-desc">{{ book.desc }}</div>
      </div>
      <div class="highlight-comment-card">
        <h3>精彩评论</h3>
        <div v-for="comment in highlightComments" :key="comment.id" class="highlight-comment-item">
          <div class="comment-user">{{ comment.user }}</div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-actions">
            <span class="like">👍 {{ comment.likes }}</span>
            <span class="fav">⭐ {{ comment.favorites }}</span>
          </div>
        </div>
      </div>
    </div>
    <!-- 右侧推荐区 -->
    <div class="side-area">
      <div class="recommend-list-card">
        <h3 class="recommend-title">相似书籍推荐</h3>
        <div v-for="sim in similarBooks" :key="sim.id" class="recommend-item book">
          <img :src="sim.cover" class="rec-cover" />
          <div class="rec-info">
            <div class="rec-post-title">{{ sim.title }}</div>
            <div class="rec-post-meta">by {{ sim.author }}</div>
            <div class="rec-post-meta">by {{ sim.viewcount }}</div>
          </div>
        </div>
        <h3 class="recommend-title" style="margin-top:32px;">推荐帖子</h3>
        <div v-for="post in recommendPosts" :key="post.id" class="recommend-item post" @click="goToPostDetail(post)">
          <div class="rec-post-title">{{ post.title }}</div>
          <div class="rec-post-meta">by {{ post.user }} · {{ post.date }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted } from 'vue'
import { API } from '@/config/api'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const book = ref({})
const loading = ref(true)
const highlightComments = ref([]) // Placeholder for highlight comments
const similarBooks = ref([]) // Placeholder for similar books
const recommendPosts = ref([
  { id: 1, title: '这是一篇关于阅读的精彩讨论', user: '读者A', date: '2023-10-26' },
  { id: 2, title: '如何高效阅读技术书籍', user: '极客B', date: '2023-10-25' },
  { id: 3, title: '分享我最近读的一本好书', user: '书虫C', date: '2023-10-24' },
  { id: 4, title: '读书笔记：思考的乐趣', user: '哲学家D', date: '2023-10-23' },
  { id: 5, title: '我的编程书单推荐', user: '码农E', date: '2023-10-22' },
]) // 推荐帖子假数据

// 获取书籍详情
const fetchBookDetail = async () => {
  try {
    loading.value = true
    const token = localStorage.getItem('token')
    const bookId = route.query.id
    
    // 根据来源使用不同的API
    const apiEndpoint = route.query.api === 'getBooksHomePageById' 
      ? API.GET_BOOK_HOMEPAGE_DETAIL(bookId)
      : `${API.GET_BOOK_DETAIL}?id=${bookId}`

    const response = await axios.get(apiEndpoint, {
      headers: {
        'token': token
      }
    })

    if (response.data.code === 200) {
      const bookData = response.data.data
      // 如果是从精选好书页面进入，数据结构不同
      const bookInfo = route.query.api === 'getBooksHomePageById' ? bookData.books : bookData
      
      book.value = {
        id: bookInfo.id,
        title: bookInfo.name,
        author: bookInfo.author,
        cover: bookData.coverageBase64 ? `data:image/jpeg;base64,${bookData.coverageBase64}` : '',
        desc: bookInfo.description || '暂无简介',
        likes: bookInfo.likes || 0,
        favorites: bookInfo.favorites || 0,
        catalog: bookInfo.catalog || []
      }
    } else {
      console.error('获取书籍详情失败:', response.data.msg)
    }
  } catch (error) {
    console.error('获取书籍详情失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchSimilarBooks = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get(API.PUSH_BOOKS, {
      headers: { 'token': token }
    })
    if (response.data.code === 200 && Array.isArray(response.data.data)) {
      console.log(response.data.data)
      const books = response.data.data.map(item => ({
        
        id: item.books.id,
        title: item.books.name || item.books.title,
        author: item.books.author,
        cover: item.coverageBase64 ? `data:image/jpeg;base64,${item.coverageBase64}` : (item.cover || ''),
      }))
      // 随机截取5个数据，如果不足则全部显示
      similarBooks.value = books.length > 5 ? books.sort(() => 0.5 - Math.random()).slice(0, 5) : books
    } else {
      similarBooks.value = []
    }
  } catch (error) {
    console.error('获取推荐书籍失败:', error)
    similarBooks.value = []
  }
}

onMounted(() => {
  fetchBookDetail()
  fetchSimilarBooks()
})

const startReading = () => {
  router.push(`/ishare/read/${book.value.id}`)
}

function goToPostDetail(post) {
  console.log('点击了推荐帖子:', post.id)
  // TODO: Implement actual navigation to post detail page
}
</script>

<style scoped>
.book-detail-page {
  display: flex;
  gap: 48px;
  max-width: 1440px;
  margin: 0 auto;
  padding: 64px 32px 80px 32px;
  background: none;
}
.main-area {
  flex: 2.2;
  display: flex;
  flex-direction: column;
  gap: 40px;
}
.side-area {
  flex: 1.1;
  display: flex;
  flex-direction: column;
  gap: 32px;
}
.book-info-card {
  display: flex;
  gap: 40px;
  background: var(--ilove-card, #fff);
  border-radius: 28px;
  box-shadow: 0 6px 32px rgba(60,60,60,0.10);
  padding: 48px 44px;
  align-items: flex-start;
}
.book-cover {
  width: 160px;
  height: 220px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(60,60,60,0.13);
}
.book-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 22px;
}
.book-title {
  font-size: 2.1rem;
  font-weight: bold;
  color: #222;
  letter-spacing: 1px;
}
.book-author {
  font-size: 1.18rem;
  color: #666;
}
.book-stats {
  font-size: 1.13rem;
  color: #888;
  display: flex;
  gap: 32px;
}
.book-catalog-card, .book-desc-card, .highlight-comment-card {
  background: var(--ilove-card, #fff);
  border-radius: 22px;
  box-shadow: 0 4px 18px rgba(60,60,60,0.08);
  padding: 36px 38px;
}
.book-catalog-card h3, .book-desc-card h3, .highlight-comment-card h3 {
  font-size: 1.35rem;
  margin-bottom: 18px;
  color: var(--ilove-main, #2563eb);
  font-weight: bold;
}
.book-catalog-card ul {
  padding-left: 24px;
  margin: 0;
}
.book-catalog-card li {
  font-size: 1.13rem;
  color: #555;
  margin-bottom: 8px;
  line-height: 1.7;
}
.book-desc {
  font-size: 1.18rem;
  color: #444;
  line-height: 1.85;
}
.highlight-comment-card {
  margin-top: 16px;
}
.highlight-comment-item {
  margin-bottom: 22px;
  padding-bottom: 14px;
  border-bottom: 1.5px solid #f0f1f3;
}
.comment-user {
  font-weight: bold;
  color: #2563eb;
  margin-bottom: 6px;
  font-size: 1.08rem;
}
.comment-content {
  color: #444;
  font-size: 1.13rem;
  line-height: 1.7;
}
.comment-actions {
  margin-top: 6px;
  font-size: 1.01rem;
  color: #888;
  display: flex;
  gap: 22px;
}
.like, .fav {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.recommend-list-card {
  background: var(--ilove-card, #fff);
  border-radius: 22px;
  box-shadow: 0 4px 18px rgba(60,60,60,0.08);
  padding: 36px 38px;
}
.recommend-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #222;
  margin-bottom: 20px;
}
.recommend-item {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px;
  border-radius: 12px;
  background: #f9f9f9;
  transition: background 0.3s;
}
.recommend-item:hover {
  background: #f0f0f0;
}
.rec-cover {
  width: 80px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(60,60,60,0.1);
}
.rec-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.rec-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: #222;
}
.rec-author {
  font-size: 0.9rem;
  color: #666;
}
.recommend-item.post {
  margin-bottom: 22px;
  padding-bottom: 12px;
  border-bottom: 1.5px solid #f0f1f3;
}
.recommend-item.post:hover {
  background-color: #f0f0f0;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(60,60,60,0.08);
  transform: translateY(-1px);
}
.rec-post-title {
  font-size: 1.13rem;
  font-weight: 600;
  color: #222;
  margin-bottom: 4px;
}
.rec-post-meta {
  font-size: 1.01rem;
  color: #888;
}
.read-btn {
  margin-top: 18px;
  padding: 12px 38px;
  background: linear-gradient(90deg, #2563eb 0%, #ff9800 100%);
  color: #fff;
  font-size: 1.15rem;
  font-weight: bold;
  border: none;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(37,99,235,0.10);
  cursor: pointer;
  transition: background 0.2s, box-shadow 0.2s, transform 0.2s;
}
.read-btn:hover {
  background: linear-gradient(90deg, #ff9800 0%, #2563eb 100%);
  box-shadow: 0 4px 16px rgba(37,99,235,0.16);
  transform: translateY(-2px) scale(1.04);
}
@media (max-width: 1100px) {
  .book-detail-page {
    flex-direction: column;
    gap: 24px;
    padding: 32px 4vw 48px 4vw;
  }
  .main-area, .side-area {
    flex: unset;
    width: 100%;
  }
}
</style> 