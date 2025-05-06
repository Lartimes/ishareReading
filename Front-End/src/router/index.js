import { createRouter, createWebHistory } from 'vue-router'
import UserProfile from '@/components/UserProfile.vue'
import BookTypeList from '../components/BookTypeList.vue'

const routes = [
  {
    path: '/ishare',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/components/HomeMain.vue') },
      { path: 'search', name: 'Search', component: () => import('@/components/BookListPage.vue') },
      { path: 'book/:id', name: 'BookDetail', component: () => import('@/components/BookDetailPage.vue') },
      { path: 'read/:id', name: 'Read', component: () => import('@/components/ReadingLayout.vue') },
      { path: 'user/:id', name: 'user-profile', component: UserProfile }
    ]
  },
  {
    path: '/',
    redirect: '/ishare'
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/ishare'
  },
  {
    path: '/book-types',
    name: 'BookTypes',
    component: BookTypeList
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router