<template>
  <div class="book-type-list">
    <div class="type-header">
      <h2>书籍分类</h2>
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索分类..." 
          @input="filterTypes"
        >
      </div>
    </div>
    <div class="type-grid">
      <div 
        v-for="type in filteredTypes" 
        :key="type.id" 
        class="type-item"
        @click="selectType(type)"
      >
        <div class="type-icon">📚</div>
        <div class="type-name">{{ type.typeName }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const bookTypes = ref([])
const searchQuery = ref('')

// 获取书籍类型
const fetchBookTypes = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get('http://localhost:8080/ishareReading/type/listByType?type=books', {
      headers: {
        'token': token
      }
    })

    if (response.data.code === 200) {
      bookTypes.value = response.data.data
    } else {
      console.error('获取书籍类型失败:', response.data.msg)
    }
  } catch (error) {
    console.error('获取书籍类型失败:', error)
  }
}

// 过滤类型
const filteredTypes = computed(() => {
  if (!searchQuery.value) return bookTypes.value
  return bookTypes.value.filter(type => 
    type.typeName.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// 选择类型
const selectType = (type) => {
  router.push({
    path: '/books',
    query: { type: type.typeName }
  })
}

// 初始化
onMounted(() => {
  fetchBookTypes()
})
</script>

<style scoped>
.book-type-list {
  padding: 2rem;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 16px;
  box-shadow: var(--shadow-lg);
  border: 1px solid rgba(254, 240, 138, 0.1);
}

.type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.type-header h2 {
  color: var(--header-text-highlight);
  font-size: 1.5rem;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.search-box {
  position: relative;
  width: 300px;
}

.search-box input {
  width: 100%;
  padding: 0.75rem 1rem;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(254, 240, 138, 0.2);
  border-radius: 8px;
  color: var(--header-text);
  font-size: 1rem;
  transition: all 0.2s;
}

.search-box input:focus {
  outline: none;
  border-color: var(--header-text-highlight);
  box-shadow: 0 0 0 2px rgba(254, 240, 138, 0.2);
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

.type-item {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(254, 240, 138, 0.1);
  border-radius: 12px;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.type-item:hover {
  transform: translateY(-2px);
  background: rgba(0, 0, 0, 0.4);
  border-color: var(--header-text-highlight);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
}

.type-icon {
  font-size: 2rem;
  color: var(--header-text-highlight);
}

.type-name {
  color: var(--header-text);
  font-size: 1.1rem;
  font-weight: 500;
  text-align: center;
}

@media (max-width: 768px) {
  .type-header {
    flex-direction: column;
    gap: 1rem;
  }

  .search-box {
    width: 100%;
  }

  .type-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style> 