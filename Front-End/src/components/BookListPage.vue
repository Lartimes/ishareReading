<template>
  <div class="book-list-page">
    <aside class="sidebar">
      <div class="sidebar-title">分类</div>
      <ul class="category-list">
        <li :class="{active: selectedCategory === ''}" @click="selectCategory('')">全部</li>
        <li v-for="cat in categories" :key="cat.id" :class="{active: cat.typeName === selectedCategory}" @click="selectCategory(cat.typeName)">
          {{ cat.typeName }}
        </li>
      </ul>
    </aside>
    <main class="main-content">
      <div class="search-bar">
        <input v-model="search" @keyup.enter="onSearch" placeholder="搜索书名/作者/简介..." />
        <button @click="onSearch">搜索</button>
      </div>
      <div class="book-list">
        <div v-for="book in visibleBooks" :key="book.id" class="book-item" @click="goToBookDetail(book.id)">
          <img :src="book.cover" :alt="book.title" class="book-cover" />
          <div class="book-info">
            <div class="book-title">{{ book.title }}</div>
            <div class="book-author">{{ book.author }}</div>
            <div class="book-meta">
              <span>{{ book.readers }}人今日阅读</span>
              <span>推荐值 {{ book.score }}%</span>
            </div>
            <div class="book-desc">{{ book.desc }}</div>
          </div>
        </div>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-if="!hasMore &&!loading" class="no-more">没有更多了</div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { API } from '@/config/api'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const selectedCategory = ref('')
const search = ref('')
const page = ref(1)
const pageSize = 5
const loading = ref(false)
const hasMore = ref(true)
const visibleBooks = ref([])

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
      categories.value = response.data.data
      // 检查URL参数，如果有type参数则选中对应的分类
      if (route.query.type) {
        selectedCategory.value = route.query.type
      }
    } else {
      console.error('获取书籍类型失败:', response.data.msg)
    }
  } catch (error) {
    console.error('获取书籍类型失败:', error)
  } finally {
    loading.value = false
  }
}

const allBooks = ref([
  {
    id: 1,
    title: '红楼梦',
    author: '曹雪芹',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33492087.jpg',
    readers: 6810,
    score: 91.7,
    desc: '《红楼梦》，中国古代小说巅峰之作。',
    category: 3
  },
  {
    id: 2,
    title: '一句顶一万句（2022新版）',
    author: '刘震云',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809795.jpg',
    readers: 6239,
    score: 91.3,
    desc: '长篇小说，获得茅盾文学奖。',
    category: 3
  },
  {
    id: 3,
    title: '我与地坛',
    author: '史铁生',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809799.jpg',
    readers: 5491,
    score: 91.5,
    desc: '散文集，关于生命与思考。',
    category: 3
  },
  {
    id: 4,
    title: '社会心理学',
    author: '戴维·迈尔斯',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809800.jpg',
    readers: 3210,
    score: 89.2,
    desc: '心理学入门经典。',
    category: 8
  },
  {
    id: 5,
    title: '人类简史',
    author: '尤瓦尔·赫拉利',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33953718.jpg',
    readers: 4120,
    score: 92.1,
    desc: '全球畅销历史著作。',
    category: 2
  },
  {
    id: 6,
    title: '计算机程序的构造和解释',
    author: 'Harold Abelson',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809798.jpg',
    readers: 2100,
    score: 95.0,
    desc: 'SICP，计算机科学圣经。',
    category: 7
  },
  {
    id: 7,
    title: '艺术的故事',
    author: '贡布里希',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809796.jpg',
    readers: 1800,
    score: 90.2,
    desc: '艺术史入门读物。',
    category: 4
  },
  {
    id: 8,
    title: '自卑与超越',
    author: '阿德勒',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809797.jpg',
    readers: 2600,
    score: 88.8,
    desc: '心理成长经典。',
    category: 10
  },
  {
    id: 9,
    title: '三体',
    author: '刘慈欣',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s2768378.jpg',
    readers: 8900,
    score: 93.5,
    desc: '中国科幻文学代表作。',
    category: 5
  },
  {
    id: 10,
    title: '活着',
    author: '余华',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s29053580.jpg',
    readers: 7500,
    score: 94.2,
    desc: '余华代表作，讲述生命的意义。',
    category: 3
  },
  {
    id: 11,
    title: '百年孤独',
    author: '加西亚·马尔克斯',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s6384944.jpg',
    readers: 6800,
    score: 92.8,
    desc: '魔幻现实主义文学经典。',
    category: 3
  },
  {
    id: 12,
    title: '时间简史',
    author: '史蒂芬·霍金',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s1914861.jpg',
    readers: 4500,
    score: 89.5,
    desc: '探索宇宙奥秘的科普经典。',
    category: 2
  },
  {
    id: 13,
    title: '围城',
    author: '钱钟书',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s1070222.jpg',
    readers: 7200,
    score: 91.9,
    desc: '中国现代文学经典。',
    category: 3
  },
  {
    id: 14,
    title: '算法导论',
    author: 'Thomas H. Cormen',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s29107378.jpg',
    readers: 2800,
    score: 94.0,
    desc: '计算机科学经典教材。',
    category: 7
  },
  {
    id: 15,
    title: '平凡的世界',
    author: '路遥',
    cover: 'https://img1.doubanio.com/view/subject/l/public/s27279654.jpg',
    readers: 8300,
    score: 93.1,
    desc: '中国当代文学经典。',
    category: 3
  }
])

function filterBooks() {
  let filtered = allBooks.value
  if (selectedCategory.value) {
    filtered = filtered.filter(b => b.category === selectedCategory.value)
  }
  if (search.value.trim()) {
    const s = search.value.trim().toLowerCase()
    filtered = filtered.filter(b => 
      b.title.toLowerCase().includes(s) || 
      b.author.toLowerCase().includes(s) || 
      b.desc.toLowerCase().includes(s) ||
      b.category.toString().includes(s)
    )
  }
  return filtered
}

function loadMore() {
  if (loading.value ||!hasMore.value) return
  loading.value = true
  setTimeout(() => {
    const filtered = filterBooks()
    const next = filtered.slice((page.value - 1) * pageSize, page.value * pageSize)
    if (next.length > 0) {
      visibleBooks.value.push(...next)
      page.value++
    } else {
      hasMore.value = false
    }
    loading.value = false
  }, 500)
}

function onScroll() {
  const el = document.documentElement
  if (el.scrollTop + window.innerHeight + 100 >= el.scrollHeight) {
    loadMore()
  }
}

function selectCategory(typeName) {
  selectedCategory.value = typeName
  resetAndLoad()
}

function onSearch() {
  if (search.value.trim()) {
    resetAndLoad()
  }
}

function resetAndLoad() {
  page.value = 1
  visibleBooks.value = []
  hasMore.value = true
  loadMore()
}

function goToBookDetail(bookId) {
  router.push({
    path: '/ishare/book/detail',
    query: {
      bookId: bookId // 修正这里的属性值
    }
  })
}

// 添加防抖函数
function debounce(fn, delay) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// 使用防抖处理搜索
const debouncedSearch = debounce(() => {
  onSearch()
}, 300)

// 监听搜索输入
watch(search, () => {
  debouncedSearch()
})

onMounted(() => {
  fetchBookTypes()
  resetAndLoad()
  window.addEventListener('scroll', onScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})
</script>

<style scoped>
.book-list-page {
  display: flex;
  min-height: 80vh;
  background: var(--ilove-bg, #f6f7fa);
}
.sidebar {
  width: 180px;
  background: #fff;
  border-right: 1px solid #f0f1f3;
  padding: 32px 0 0 0;
  min-height: 100vh;
}
.sidebar-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: #222;
  margin-left: 32px;
  margin-bottom: 18px;
}
.category-list {
  list-style: none;
  padding: 0 0 0 32px;
  margin: 0;
}
.category-list li {
  padding: 8px 0 8px 0;
  cursor: pointer;
  color: #444;
  border-radius: 6px 0 0 6px;
  transition: background 0.2s, color 0.2s;
}
.category-list li.active, .category-list li:hover {
  background: #e8f0fe;
  color: var(--ilove-main, #2563eb);
  font-weight: bold;
}
.main-content {
  flex: 1;
  padding: 32px 40px 32px 40px;
}
.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}
.search-bar input {
  flex: 1;
  padding: 8px 12px;
  border: 1.5px solid #e0e3e8;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: border 0.2s;
}
.search-bar input:focus {
  border: 1.5px solid var(--ilove-main, #2563eb);
}
.search-bar button {
  padding: 8px 18px;
  background: var(--ilove-main, #2563eb);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}
.search-bar button:hover {
  background: #1746a2;
}
.book-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.book-item {
  display: flex;
  gap: 22px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(60,60,60,0.06);
  padding: 18px 22px;
  align-items: flex-start;
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
}
.book-item:hover {
  box-shadow: 0 8px 32px rgba(37,99,235,0.10);
  transform: translateY(-2px) scale(1.02);
}
.book-cover {
  width: 90px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 1px 6px rgba(60,60,60,0.10);
}
.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.book-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: #222;
}
.book-author {
  font-size: 0.98rem;
  color: #666;
}
.book-meta {
  font-size: 0.92rem;
  color: #888;
  margin-bottom: 4px;
  display: flex;
  gap: 18px;
}
.book-desc {
  font-size: 0.98rem;
  color: #444;
  line-height: 1.5;
  margin-top: 2px;
}
.loading, .no-more {
  text-align: center;
  color: #888;
  margin: 18px 0;
}
@media (max-width: 900px) {
 .book-list-page {
    flex-direction: column;
  }
 .sidebar {
    width: 100%;
    min-height: unset;
    border-right: none;
    border-bottom: 1px solid #f0f1f3;
    padding: 12px 0 0 0;
  }
 .main-content {
    padding: 18px 4vw 18px 4vw;
  }
}
</style>