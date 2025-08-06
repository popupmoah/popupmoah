import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PopupStoresView from '../views/PopupStoresView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/popupstores',
      name: 'popupstores',
      component: PopupStoresView,
    },
    {
      path: '/popupstores/:id',
      name: 'popupstore-detail',
      component: () => import('../views/PopupStoreDetailView.vue'),
    },
    {
      path: '/popupstores/create',
      name: 'popupstore-create',
      component: () => import('../views/PopupStoreFormView.vue'),
    },
    {
      path: '/popupstores/:id/edit',
      name: 'popupstore-edit',
      component: () => import('../views/PopupStoreFormView.vue'),
    },
    {
      path: '/categories',
      name: 'categories',
      component: () => import('../views/CategoriesView.vue'),
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
