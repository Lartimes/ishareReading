<template>
  <div class="session-list">
    <button class="new-session">开启新对话</button>
    <div class="sessions">
      <div v-for="item in sessionItems" :key="item.id" class="session-item" :class="{active: item.id === activeId}">
        <div class="title">{{ item.title }}</div>
        <div class="date">{{ item.date }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const sessionItems = ref([
  { id: 1, title: 'Java代码优化与缓存机制增强', date: '昨天' },
  { id: 2, title: '使用Spring AI Tika文档读取', date: '昨天' },
  { id: 3, title: 'JdbcChatMemory优化与改进', date: '7天前' },
  { id: 4, title: '你好吗，什么是Java Agent', date: '7天前' },
  { id: 5, title: '国内AI模型工具调研', date: '7天前' },
])
const activeId = ref(1)
</script>

<style scoped>
.session-list {
  width: 100%;
  background: var(--ilove-card, #fff);
  color: var(--ilove-text, #222);
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-sizing: border-box;
  border-radius: 0 0 0 0;
  box-shadow: none;
}
.new-session {
  margin: 1.5rem 1.5rem 1.5rem 1.5rem;
  padding: 0.75rem 1rem;
  background: linear-gradient(90deg, #2563eb 0%, #ff9800 100%);
  color: #fff;
  border: none;
  border-radius: 0.5rem;
  font-size: 1rem;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s;
}
.new-session:hover {
  background: linear-gradient(90deg, #ff9800 0%, #2563eb 100%);
}
.sessions {
  flex: 1;
  overflow-y: auto;
  padding: 0 0.5rem;
}
.session-item {
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  margin-bottom: 0.5rem;
  cursor: pointer;
  background: transparent;
  transition: background 0.2s;
  display: flex;
  flex-direction: column;
}
.session-item.active, .session-item:hover {
  background: #e3eaff;
}
.title {
  font-size: 1rem;
  font-weight: 500;
  color: var(--ilove-main, #2563eb);
  margin-bottom: 0.25rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.date {
  font-size: 0.85rem;
  color: #8a8fa3;
}
</style> 