<template>
  <div class="book-list-card">
    <div class="book-list-header">
      <span class="book-list-icon">{{ icon }}</span>
      <span class="book-list-title">{{ title }}</span>
      <span class="book-list-sub">{{ subtitle }}</span>
    </div>
    <div class="book-list-books">
      <div v-for="(book, idx) in books" :key="book.id" class="book-item" @click="goToBookDetail(book.id)">
        <div class="book-rank" :class="'rank-' + (idx+1)">{{ idx + 1 }}</div>
        <img :src="book.cover" :alt="book.title" class="book-cover" />
        <div class="book-info">
          <div class="book-title">{{ book.title }}</div>
          <div class="book-author">{{ book.author }}</div>
          <div class="book-meta">
            <span v-if="book.score" class="book-score">推荐值 {{ book.score }}%</span>
            <span v-if="book.tag" class="book-tag">{{ book.tag }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="book-list-footer">
      <a href="#" class="book-list-more" @click.prevent="goAllBooks">查看全部 &gt;</a>
    </div>
  </div>
</template>

<script setup>
import BookTypeGrid from './BookTypeGrid.vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  books: {
    type: Array,
    required: true
  },
  title: {
    type: String,
    default: '精选好书'
  },
  subtitle: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: '🏆'
  }
});

const router = useRouter()

function goToBookDetail(bookId) {
  router.push({
    path: '/ishare/book/detail',
    query: {
      id: bookId,
      api: 'getBooksHomePageById'
    }
  })
}

function goAllBooks() {
  router.push('/ishare/search')
}
</script>

<style scoped>
.book-list-card {
  background: var(--ilove-card, #fff);
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(60,60,60,0.06);
  padding: 22px 16px 12px 16px;
  width: 100%;
  max-width: none;
  min-width: 0;
  display: flex;
  flex-direction: column;
  margin-bottom: 18px;
  box-sizing: border-box;
}
.book-list-header {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 12px;
}
.book-list-icon {
  font-size: 1.2rem;
}
.book-list-title {
  font-size: 1rem;
}
.book-list-sub {
  font-size: 0.85rem;
}
.book-list-books {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, auto);
  gap: 10px;
  margin-bottom: 6px;
}
.book-item {
  background: var(--ilove-bg, #f6f7fa);
  border-radius: 8px;
  padding: 6px 2px;
  box-shadow: 0 1px 4px rgba(60,60,60,0.04);
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
  min-width: 0;
}
.book-item:hover {
  box-shadow: 0 4px 16px rgba(37,99,235,0.10);
  transform: translateY(-2px) scale(1.03);
}
.book-rank {
  font-size: 0.95rem;
  margin-bottom: 3px;
}
.rank-1 { color: #ff9800; }
.rank-2 { color: #ffb300; }
.rank-3 { color: #ffca28; }
.book-cover {
  width: 38px;
  height: 52px;
  border-radius: 5px;
  margin-bottom: 4px;
}
.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.book-title {
  font-size: 0.85rem;
  margin-bottom: 1px;
}
.book-author {
  font-size: 0.78rem;
}
.book-meta {
  gap: 3px;
  font-size: 0.75rem;
}
.book-score, .book-tag {
  font-size: 0.75rem;
  padding: 0 4px;
}
.book-list-footer {
  margin-top: 6px;
  text-align: right;
}
.book-list-more {
  font-size: 0.85rem;
}
@media (max-width: 1200px) {
  .book-list-card {
    max-width: 100%;
  }
}
@media (max-width: 900px) {
  .book-list-card {
    padding: 8px 2px 4px 2px;
  }
  .book-list-books {
    grid-template-columns: 1fr;
    grid-template-rows: none;
    gap: 8px;
  }
}
</style> 