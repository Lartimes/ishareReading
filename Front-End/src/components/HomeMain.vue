<template>
  <div class="main-container">
    <div class="brand-welcome">
      <span class="brand-logo">iloveshare</span>
      <span class="brand-slogan">—— 分享让世界更美好！</span>
    </div>
    <div class="main-row">
      <!-- 左侧：幻灯片 -->
      <CarouselSection />
      <!-- 右侧：排行榜 -->
      <TrendingSection />
    </div>
    <div class="categories-section">
      <h2 class="section-title">热门分类</h2>
      <div class="category-grid">
        <div v-for="category in categories" :key="category.name" class="category-card" @click="goCategory(category)">
          <div class="category-icon">{{ category.icon }}</div>
          <h3>{{ category.name }}</h3>
          <p>{{ category.bookCount }} 本</p>
        </div>
      </div>
    </div>
    <BookHomePageList :books="bookList" />
    <BookTypeGrid :types="typeList" />
    <div class="divider-gradient"></div>
    <div class="about-section">
      <h3>关于 iloveshare 团队</h3>
      <p>iloveshare 致力于打造开放、友好、创新的知识与书籍分享社区。我们相信：分享让世界更美好！</p>
    </div>
  </div>
</template>

<script setup>
import CarouselSection from './CarouselSection.vue';
import TrendingSection from './TrendingSection.vue';
import BookHomePageList from './BookHomePageList.vue';
import BookTypeGrid from './BookTypeGrid.vue'
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { API } from '@/config/api'
import axios from 'axios'

const router = useRouter()
const bookList = ref([])
const loading = ref(true)
const bookDetails = ref([]) // 存储完整的书籍数据

const categories = [
  { name: '小说', icon: '📚', bookCount: 2500 },
  { name: '科学', icon: '🧬', bookCount: 1800 },
  { name: '历史', icon: '🌏', bookCount: 1200 },
  { name: '商业', icon: '💡', bookCount: 1500 }
];

const typeList = [
  {
    id: 1,
    name: '精品小说',
    count: 25324,
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33492087.jpg'
  },
  {
    id: 2,
    name: '历史',
    count: 29024,
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33953718.jpg'
  },
  {
    id: 3,
    name: '文学',
    count: 43199,
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809795.jpg'
  },
  {
    id: 4,
    name: '艺术',
    count: 9266,
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809796.jpg'
  },
  {
    id: 5,
    name: '人物传记',
    count: 6526,
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809797.jpg'
  },
  {
    id: 6,
    name: '哲学宗教',
    count: 7886,
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809798.jpg'
  },
  {
    id: 7,
    name: '计算机',
    count: 12023,
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809799.jpg'
  },
  {
    id: 8,
    name: '心理',
    count: 4481,
    cover: 'https://img1.doubanio.com/view/subject/l/public/s33809800.jpg'
  }
]

// 获取精品好书数据
const fetchFeaturedBooks = async () => {
  try {
    loading.value = true
    const token = localStorage.getItem('token')
    const response = await axios.get(API.GET_FEATURED_BOOKS, {
      headers: {
        'token': token
      }
    })

    if (response.data.code === 200) {
      // 保存完整数据
      bookDetails.value = response.data.data
      // 处理显示数据，限制为6个
      bookList.value = response.data.data.slice(0, 6).map(item => {
        if (!item || !item.books) {
          console.error('无效的书籍数据:', item)
          return null
        }
        return {
          id: String(item.books.id),
          title: item.books.name,
          author: item.books.author,
          cover: item.coverageBase64 ? `data:image/jpeg;base64,${item.coverageBase64}` : ''
        }
      }).filter(Boolean) // 过滤掉无效数据
    } else {
      console.error('获取精品好书失败:', response.data.msg)
    }
  } catch (error) {
    console.error('获取精品好书失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchFeaturedBooks()
})

function goCategory(cat) {
  console.log(cat)
  console.log('跳转')
  router.push({ path: '/ishare/search', query: { type: cat.name } })
  
}

// 跳转到书籍详情页
const goToBookDetail = (bookId) => {
  if (!bookDetails.value || !bookDetails.value.length) {
    console.error('书籍详情数据未加载')
    return
  }
  
  const bookDetail = bookDetails.value.find(item => item && item.books && String(item.books.id) === String(bookId))
  if (bookDetail) {
    router.push({
      path: '/ishare/book/detail',
      query: {
        id: String(bookDetail.books.id),
        api: 'getBooksHomePageById'
      }
    })
  } else {
    console.error('未找到对应的书籍详情:', bookId)
  }
}
</script>

<style scoped>
:root {
  --ilove-main: #2563eb;
  --ilove-orange: #ff9800;
  --ilove-bg: #f6f7fa;
}
.main-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 48px 24px 64px 24px;
  background: var(--ilove-bg);
}
.brand-welcome {
  font-size: 1.5rem;
  color: #222;
  margin-bottom: 32px;
  text-align: center;
  letter-spacing: 1px;
  animation: fadeInDown 1.2s;
}
.brand-logo {
  color: var(--ilove-main);
  font-weight: bold;
  font-size: 2rem;
  letter-spacing: 2px;
  margin-right: 12px;
}
.brand-slogan {
  color: #888;
  font-size: 1.1rem;
}
.main-row {
  display: flex;
  gap: 32px;
  width: 100%;
  margin-bottom: 40px;
}
.categories-section {
  margin-top: 40px;
}
.section-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 24px;
  color: var(--ilove-main);
  letter-spacing: 1px;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 24px;
  margin-top: 24px;
}
.category-card {
  background: #fcfcfd;
  padding: 24px;
  border-radius: 18px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(60,60,60,0.06);
  border: 1px solid #f0f1f3;
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
}
.category-card:hover {
  box-shadow: 0 8px 32px rgba(37,99,235,0.10);
  transform: translateY(-4px) scale(1.04);
  border: 1.5px solid var(--ilove-main);
}
.category-icon {
  font-size: 2.2rem;
  margin-bottom: 10px;
  background: linear-gradient(120deg, #2563eb 30%, #ff9800 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.category-card h3 {
  font-size: 1.1rem;
  font-weight: bold;
  margin: 0 0 8px 0;
  color: var(--ilove-orange);
}
.category-card p {
  font-size: 0.9rem;
  color: #6b7280;
}
.divider-gradient {
  margin: 48px auto 0 auto;
  width: 60%;
  height: 5px;
  border-radius: 3px;
  background: linear-gradient(90deg, #2563eb 0%, #ff9800 100%);
  opacity: 0.7;
}
.about-section {
  margin-top: 48px;
  padding: 32px 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  text-align: center;
  color: #444;
}
.about-section h3 {
  color: var(--ilove-main);
  margin-bottom: 12px;
  font-size: 1.2rem;
}
.about-section p {
  font-size: 1rem;
  color: #666;
  margin: 0;
}
@media (max-width: 900px) {
  .main-row {
    flex-direction: column;
    gap: 20px;
  }
  .categories-section {
    margin-top: 20px;
  }
  .about-section {
    margin-top: 24px;
    padding: 20px 8px;
  }
}
@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-30px);}
  to { opacity: 1; transform: translateY(0);}
}
</style>
