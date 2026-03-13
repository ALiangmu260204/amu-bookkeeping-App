import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', component: () => import('@/views/login/LoginPage.vue') },
    {
      path: '/',
      component: () => import('@/views/layout/LayoutContainer.vue'),
      redirect: '/data/board',
      children: [
        { path: '/data/board', component: () => import('@/views/business/DataBoard.vue') },
        { path: '/data/analysis', component: () => import('@/views/business/DataAnalysis.vue') },
        { path: '/data/category', component: () => import('@/views/business/CategoryManage.vue') },
        { path: '/data/user', component: () => import('@/views/business/UserManagement.vue') },
        { path: '/profile', component: () => import('@/views/user/UserProfile.vue') },
      ]
    }
  ],
})

router.beforeEach((to) => {
  if (!useUserStore().token && to.path !== '/login') return '/login'

  if (useUserStore().token && to.path === '/login') return '/'

  return true
})

export default router
