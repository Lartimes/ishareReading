<template>
  <div class="trending-section">
    <div class="rank-header">
      <div class="rank-title">
        <span class="rank-icon">📈</span>
        <span>书籍</span>
      </div>
      <div class="rank-tabs">
        <button 
          v-for="tab in ['热门书籍', '最新发布']" 
          :key="tab"
          :class="['rank-tab', { active: selectedTab === tab }]"
          @click="selectedTab = tab"
        >
          {{ tab }}
        </button>
      </div>
    </div>
    <div v-if="loading" class="loading-spinner">
      <div class="spinner"></div>
    </div>
    <ul v-else class="rank-list">
      <li 
        v-for="(item, idx) in rankList" 
        :key="item.id" 
        class="rank-item"
        @click="handleBookClick(item)"
      >
        <span class="rank-num" :class="`rank-${idx + 1}`">{{ idx + 1 }}</span>
        <div class="rank-content">
          <span class="rank-title-text">{{ item.title }}</span>
          <div class="rank-meta">
            <span class="rank-author">{{ item.author }}</span>
            <span class="rank-count">{{ formatCount(item.count) }} 阅读</span>
          </div>
        </div>
        <div class="rank-trend" :class="item.trend">
          {{ item.trend === 'up' ? '↑' : item.trend === 'down' ? '↓' : '=' }}
        </div>
      </li>
    </ul>
    <button class="rank-btn" @click="viewFullRanking">
      <span>查看完整榜单</span>
      <span class="arrow">→</span>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { API } from '@/config/api';
import axios from 'axios';
import { useRouter } from 'vue-router';

const selectedTab = ref('热门书籍');
const rankList = ref([]);
const loading = ref(true);
const router = useRouter();

const formatCount = (count) => {
  return new Intl.NumberFormat('zh-CN', { notation: 'compact' }).format(count);
};

const handleBookClick = (book) => {
  router.push({
    path: '/ishare/book/detail',
    query: {
      id: book.id
    }
  })
};

const viewFullRanking = () => {
  console.log('Viewing full rankings');
};

// 获取热门书籍数据
const fetchHotBooks = async () => {
  try {
    loading.value = true;
    const token = localStorage.getItem('token');
    const response = await axios.get(API.GET_HOT_BOOKS, {
      headers: {
        'token': token
      }
    });

    if (response.data.code === 200) {
      // 限制最多显示5个
      rankList.value = response.data.data.slice(0, 5).map((book, index) => ({
        title: book.name,
        author: book.author,
        count: book.hot,
        trend: 'up',
        id: book.id,
        coverImageId: book.coverImageId,
        genre: book.genre,
        averageRating: book.averageRating
      }));
    } else {
      console.error('获取热门书籍失败:', response.data.msg);
    }
  } catch (error) {
    console.error('获取热门书籍失败:', error);
  } finally {
    loading.value = false;
  }
};

// 获取最新发布数据
const fetchLatestBooks = async () => {
  try {
    loading.value = true;
    const token = localStorage.getItem('token');
    const response = await axios.get(API.GET_LATEST_BOOKS, {
      headers: {
        'token': token
      }
    });

    if (response.data.code === 200) {
      // 限制最多显示5个
      rankList.value = response.data.data.slice(0, 5).map((book, index) => ({
        title: book.name,
        author: book.author,
        count: book.hot,
        trend: 'up',
        id: book.id,
        coverImageId: book.coverImageId,
        genre: book.genre,
        averageRating: book.averageRating
      }));
    } else {
      console.error('获取最新发布失败:', response.data.msg);
    }
  } catch (error) {
    console.error('获取最新发布失败:', error);
  } finally {
    loading.value = false;
  }
};

// 监听标签切换
watch(selectedTab, (newTab) => {
  if (newTab === '热门书籍') {
    fetchHotBooks();
  } else if (newTab === '最新发布') {
    fetchLatestBooks();
  }
});

onMounted(() => {
  fetchHotBooks();
});
</script>

<style scoped>
.trending-section {
  width: 360px;
  flex: 0 0 360px;
  min-width: 280px;
  background: #fcfcfd;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(60,60,60,0.06);
  border: 1px solid #f0f1f3;
  margin-left: 0;
  height: 500px;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s, transform 0.2s;
}
.trending-section:hover {
  box-shadow: 0 8px 32px rgba(37,99,235,0.10);
  transform: translateY(-2px) scale(1.01);
}
.rank-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.rank-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 1.1rem;
  font-weight: 600;
}
.rank-icon {
  font-size: 1.1rem;
}
.rank-tabs {
  display: flex;
  gap: 8px;
  background: #f3f4f6;
  padding: 4px;
  border-radius: 8px;
  width: fit-content;
}
.rank-tab {
  background: #2563eb;
  color: #fff;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.8rem;
  min-width: 70px;
  text-align: center;
  font-weight: 500;
}
.rank-tab:not(.active) {
  background: transparent;
  color: #6b7280;
  font-weight: normal;
}
.rank-tab:hover:not(.active) {
  background: rgba(37, 99, 235, 0.1);
  color: #2563eb;
}
.rank-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.rank-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
}
.rank-item:last-child {
  border-bottom: none;
}
.rank-num {
  font-weight: 600;
  margin-right: 10px;
  font-size: 0.85rem;
  color: #4b5563;
}
.rank-content {
  flex: 1;
}
.rank-title-text {
  font-size: 0.9rem;
  font-weight: 600;
  line-height: 1.3;
  color: #1f2937;
}
.rank-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 3px;
}
.rank-author {
  font-size: 0.75rem;
  color: #6b7280;
}
.rank-count {
  font-size: 0.75rem;
  color: #6b7280;
}
.rank-trend {
  font-size: 0.85rem;
  font-weight: 600;
}
.rank-trend.up {
  color: #15803d;
}
.rank-trend.down {
  color: #b91c1c;
}
.rank-trend.same {
  color: #6b7280;
}
.rank-btn {
  background: #2563eb;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 16px;
  width: 100%;
}
.rank-btn:hover {
  background: #1d4ed8;
  transform: translateY(-2px);
}
.arrow {
  margin-left: 8px;
}
.loading-spinner {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}
.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f4f6;
  border-top-color: #2563eb;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
</style> 