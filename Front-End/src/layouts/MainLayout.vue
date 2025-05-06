<template>
  <div>
    <Navbar />
    <router-view />
  </div>
</template>

<script setup>
// 可在此引入全局导航栏等
import Navbar from '@/components/Navbar.vue'
</script>

<style scoped>
/* 可添加全局布局样式 */
</style>