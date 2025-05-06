<template>
  <div class="carousel-section">
    <swiper
      :modules="[Navigation, Autoplay, Pagination]"
      :slides-per-view="1"
      :autoplay="{ delay: 5000, disableOnInteraction: false }"
      :pagination="{ clickable: true }"
      navigation
      class="carousel"
    >
      <swiper-slide v-for="(slide, idx) in slides" :key="idx">
        <img :src="slide.img" :alt="slide.title" class="carousel-img" />
        <div class="carousel-caption">
          <h2>{{ slide.title }}</h2>
          <p>{{ slide.desc }}</p>
          <button class="carousel-btn">了解更多</button>
        </div>
      </swiper-slide>
    </swiper>
  </div>
</template>

<script setup>
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Navigation, Autoplay, Pagination } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import 'swiper/css/autoplay';
import img1 from '@/assets/book-covers/文学.jpg';
import img2 from '@/assets/book-covers/历史.jpg';

const slides = [
  {
    img: img1,
    title: '探索知识的海洋',
    desc: '在这里，每一本书都是一次新的冒险',
  },
  {
    img: img2,
    title: '发现阅读的乐趣',
    desc: '让阅读成为生活的一部分',
  }
];
</script>

<style scoped>
.carousel-section {
  flex: 1 1 0;
  min-width: 0;
  max-width: 820px;
  background: #fcfcfd;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(60,60,60,0.06);
  border: 1px solid #f0f1f3;
  position: relative;
  height: 500px;
  transition: box-shadow 0.2s, transform 0.2s;
}
.carousel-section:hover {
  box-shadow: 0 8px 32px rgba(37,99,235,0.10);
  transform: translateY(-2px) scale(1.01);
}
.carousel {
  width: 100%;
  height: 100%;
}
.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.carousel-caption {
  position: absolute;
  left: 48px;
  bottom: 48px;
  background: rgba(0, 0, 0, 0.75);
  padding: 32px;
  border-radius: 16px;
  max-width: 460px;
}
.carousel-caption h2 {
  color: #fff;
  font-size: 2.5rem;
  margin: 0 0 16px 0;
}
.carousel-caption p {
  color: #e2e8f0;
  font-size: 1.1rem;
  margin: 0 0 24px 0;
}
.carousel-btn {
  background: #2563eb;
  color: #fff;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
}
.carousel-btn:hover {
  background: #1d4ed8;
  transform: translateY(-2px);
}
</style> 