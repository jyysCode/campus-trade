import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../components/NavBar.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/Home.vue') },
      { path: 'search', name: 'Search', component: () => import('../views/Search.vue') },
      { path: 'product/:id', name: 'ProductDetail', component: () => import('../views/ProductDetail.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue') },
      { path: 'orders', name: 'Orders', component: () => import('../views/Orders.vue') },
      { path: 'favorites', name: 'Favorites', component: () => import('../views/Favorites.vue') },
      { path: 'cart', name: 'Cart', component: () => import('../views/Cart.vue') },
      { path: 'chat/:otherUserId', name: 'Chat', component: () => import('../views/Chat.vue') },
      { path: 'my-messages', name: 'MyMessages', component: () => import('../views/MyMessages.vue') },
      { path: 'my-info', name: 'MyInfo', component: () => import('../views/MyInfo.vue') },
      { path: 'publish', name: 'Publish', component: () => import('../views/Publish.vue') },
    ]
  },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  { path: '/forgot', name: 'Forgot', component: () => import('../views/Forgot.vue') },
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/AdminLogin.vue') },
  { path: '/admin', name: 'Admin', component: () => import('../views/Admin.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
