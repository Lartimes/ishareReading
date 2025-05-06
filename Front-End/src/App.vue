<template>
  <div class="bg-glow-left"></div>
  <div class="bg-glow-right"></div>
  <div class="app-content">
    <router-view />
    
  </div>
</template>

<script setup>
import Navbar from '@/components/Navbar.vue'
import HomeMain from '@/components/HomeMain.vue'
</script>

<style>
:root {
  --page-bg: linear-gradient(120deg, #f0f4ff 0%, #f6f7fa 60%, #ffe6c7 100%);
  --glow-left: radial-gradient(circle at 30% 40%, #2563eb 0%, transparent 80%);
  --glow-right: radial-gradient(circle at 70% 60%, #ff9800 0%, transparent 80%);
}
[data-theme='dark'] {
  --page-bg: linear-gradient(120deg, #23262f 0%, #181a20 100%);
  --glow-left: radial-gradient(circle at 30% 40%, #5fa8ff 0%, transparent 80%);
  --glow-right: radial-gradient(circle at 70% 60%, #ffd580 0%, transparent 80%);
}
body, #app {
  background: var(--page-bg) !important;
  transition: background 0.4s;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
}
.bg-glow-left, .bg-glow-right {
  position: fixed;
  top: 0;
  width: 600px;
  height: 100vh;
  z-index: 0;
  pointer-events: none;
  filter: blur(90px);
  opacity: 0.38;
  transition: opacity 0.4s, background 0.4s;
}
.bg-glow-left {
  left: -220px;
  background: var(--glow-left);
}
.bg-glow-right {
  right: -220px;
  background: var(--glow-right);
}
.app-content {
  position: relative;
  z-index: 1;
}
</style>
