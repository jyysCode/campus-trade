<template>
  <div class="navbar">
    <div class="navbar-content">
      <div class="logo" @click="handleLogoClick">
        <span class="logo-icon">📦</span>
        <span class="logo-text">校园二手</span>
      </div>
      
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" class="search-btn" @click="handleSearch">
          搜索
        </el-button>
      </div>
      
      <div class="nav-actions">
        <template v-if="userStore.isLoggedIn()">
          <el-button type="primary" class="publish-btn" @click="$router.push('/publish')">
            <el-icon><Plus /></el-icon>
            发布
          </el-button>
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
            <el-button text class="action-btn" @click="$router.push('/cart')">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </el-button>
          </el-badge>
          <el-button text class="action-btn" @click="$router.push('/favorites')">
            <el-icon :size="20"><Star /></el-icon>
          </el-button>
          <el-badge :value="messageCount" :hidden="messageCount === 0" class="message-badge">
            <el-button text class="action-btn" @click="$router.push('/my-messages')">
              <el-icon :size="20"><ChatDotRound /></el-icon>
            </el-button>
          </el-badge>
          <el-button text class="action-btn" @click="$router.push('/my-info')">
            <el-icon :size="20"><User /></el-icon>
          </el-button>
          <el-dropdown trigger="click">
            <div class="user-avatar-wrapper">
              <img :src="getUserAvatar(userStore.userInfo?.id, userStore.userInfo?.username)" class="user-avatar" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/orders')">
                  <el-icon><Document /></el-icon>我的订单
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/favorites')">
                  <el-icon><Star /></el-icon>我的收藏
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/cart')">
                  <el-icon><ShoppingCart /></el-icon>购物车
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" class="login-btn" @click="$router.push('/login')">登录</el-button>
          <el-button class="register-btn" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
    
    <div class="category-bar">
      <div class="category-content">
        <span 
          v-for="cat in categories" 
          :key="cat.value"
          :class="['category-item', { active: currentCategory === cat.value }]"
          @click="selectCategory(cat.value)"
        >
          {{ cat.label }}
        </span>
      </div>
    </div>

    <div class="navbar-body">
      <router-view />
    </div>

    <div class="footer">
      <div class="footer-content">
        <div class="footer-links">
          <span class="footer-link">关于我们</span>
          <span class="footer-divider">|</span>
          <span class="footer-link">联系方式</span>
          <span class="footer-divider">|</span>
          <span class="footer-link">帮助中心</span>
          <span class="footer-divider">|</span>
          <span class="footer-link">用户协议</span>
        </div>
        <p class="copyright">© 2024 校园二手交易平台 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { Search, Plus, ShoppingCart, Star, User, Document, SwitchButton, ChatDotRound } from '@element-plus/icons-vue'
import { getUserAvatar } from '../utils/image'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const searchKeyword = ref('')
const cartCount = ref(0)
const messageCount = ref(0)
const currentCategory = ref('')

const categories = [
  { label: '首页', value: '' },
  { label: '电子产品', value: '电子产品' },
  { label: '图书教材', value: '图书教材' },
  { label: '生活用品', value: '生活用品' },
  { label: '服饰鞋包', value: '服饰鞋包' },
  { label: '运动器材', value: '运动器材' },
  { label: '美妆护肤', value: '美妆护肤' },
  { label: '其他', value: '其他' },
]

const handleLogoClick = () => {
  currentCategory.value = ''
  searchKeyword.value = ''
  router.push('/')
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/search?keyword=${encodeURIComponent(searchKeyword.value.trim())}`)
  }
}

const selectCategory = (value) => {
  currentCategory.value = value
  if (value) {
    router.push(`/search?category=${encodeURIComponent(value)}`)
  } else {
    router.push('/')
  }
}

const logout = () => {
  userStore.logout()
  router.push('/login')
}

const loadCartCount = async () => {
  if (!userStore.isLoggedIn()) return
  try {
    const token = localStorage.getItem('token')
    const res = await fetch('/api/cart/count', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      cartCount.value = data.data || 0
    }
  } catch (e) {}
}

const loadMessageCount = async () => {
  if (!userStore.isLoggedIn()) return
  try {
    const token = localStorage.getItem('token')
    const res = await fetch('/api/message/unread-count', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      messageCount.value = data.data || 0
    }
  } catch (e) {}
}

watch(() => route.query, (query) => {
  if (query.category) {
    currentCategory.value = query.category
  } else if (!query.keyword) {
    currentCategory.value = ''
  }
}, { immediate: true })

onMounted(() => {
  // 如果已登录但没有用户信息，重新获取
  if (userStore.isLoggedIn() && !userStore.userInfo) {
    userStore.fetchUserInfo().catch(() => {})
  }
  loadCartCount()
  loadMessageCount()
  setInterval(() => {
    loadCartCount()
    loadMessageCount()
  }, 30000)
})
</script>

<style scoped>
.navbar {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 40px;
  background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%);
  box-shadow: 0 2px 10px rgba(255, 106, 0, 0.3);
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px 0 0 20px;
  box-shadow: none;
  background: rgba(255, 255, 255, 0.95);
}

.search-input :deep(.el-input__inner) {
  height: 40px;
}

.search-btn {
  border-radius: 0 20px 20px 0;
  height: 40px;
  background: #333 !important;
  border-color: #333 !important;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.publish-btn {
  background: #333 !important;
  border-color: #333 !important;
  border-radius: 20px;
  padding: 8px 16px;
}

.action-btn {
  color: #fff !important;
  padding: 8px;
}

.cart-badge :deep(.el-badge__content) {
  background: #ff4757;
  border: none;
}

.login-btn {
  background: #fff !important;
  color: #FF6A00 !important;
  border: none;
  border-radius: 20px;
}

.register-btn {
  background: transparent !important;
  color: #fff !important;
  border: 1px solid #fff;
  border-radius: 20px;
}

.user-avatar-wrapper {
  cursor: pointer;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid #fff;
  object-fit: cover;
}

.category-bar {
  background: #fff;
  border-bottom: 1px solid #eee;
  padding: 0 40px;
}

.category-content {
  display: flex;
  gap: 32px;
  height: 44px;
  align-items: center;
}

.category-item {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.category-item:hover {
  color: #FF6A00;
}

.category-item.active {
  color: #FF6A00;
  font-weight: 600;
}

.navbar-body {
  flex: 1;
}

.footer {
  background: #333;
  padding: 30px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.footer-links {
  margin-bottom: 16px;
}

.footer-link {
  color: #999;
  font-size: 14px;
  cursor: default;
}

.footer-divider {
  color: #555;
  margin: 0 16px;
}

.copyright {
  color: #666;
  font-size: 12px;
}
</style>
