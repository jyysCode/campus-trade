<template>
  <div class="my-info-page">
    <div class="info-container">
      <h2 class="page-title">我的信息</h2>
      
      <!-- 用户信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <img :src="getUserAvatar(userStore.userInfo?.id, userStore.userInfo?.username)" class="user-avatar" />
          <div class="user-basic">
            <h3>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h3>
            <p class="user-type">{{ userTypeText }}</p>
          </div>
        </div>
      </div>

      <!-- 统计信息 -->
      <div class="stats-grid">
        <div class="stat-item" @click="$router.push('/profile')">
          <span class="stat-num">{{ stats.products }}</span>
          <span class="stat-label">我的商品</span>
        </div>
        <div class="stat-item" @click="$router.push('/orders')">
          <span class="stat-num">{{ stats.orders }}</span>
          <span class="stat-label">我的订单</span>
        </div>
        <div class="stat-item" @click="$router.push('/favorites')">
          <span class="stat-num">{{ stats.favorites }}</span>
          <span class="stat-label">我的收藏</span>
        </div>
        <div class="stat-item" @click="$router.push('/cart')">
          <span class="stat-num">{{ stats.cart }}</span>
          <span class="stat-label">购物车</span>
        </div>
      </div>

      <!-- 功能菜单 -->
      <div class="menu-list">
        <div class="menu-item" @click="$router.push('/profile')">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/orders')">
          <el-icon><Document /></el-icon>
          <span>我的订单</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/favorites')">
          <el-icon><Star /></el-icon>
          <span>我的收藏</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/cart')">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>

      <!-- 设置菜单 -->
      <div class="menu-list">
        <div class="menu-item" @click="editProfile">
          <el-icon><Edit /></el-icon>
          <span>编辑资料</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="changePassword">
          <el-icon><Lock /></el-icon>
          <span>修改密码</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item logout" @click="logout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Document, Star, ShoppingCart, Edit, Lock, SwitchButton, ArrowRight } from '@element-plus/icons-vue'
import { getUserAvatar } from '../utils/image'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  products: 0,
  orders: 0,
  favorites: 0,
  cart: 0
})

const userTypeText = computed(() => {
  if (!userStore.userInfo) return ''
  return userStore.userInfo.userType === 1 ? '管理员' : '普通用户'
})

const loadStats = async () => {
  try {
    // 获取购物车数量
    const token = localStorage.getItem('token')
    const cartRes = await fetch('/api/cart/list', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const cartData = await cartRes.json()
    if (cartData.code === 200 && Array.isArray(cartData.data)) {
      stats.value.cart = cartData.data.length
    }

    // 获取收藏数量
    const favRes = await fetch('/api/favorite/list?page=1&size=1', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const favData = await favRes.json()
    if (favData.code === 200 && favData.data) {
      stats.value.favorites = favData.data.total || 0
    }

    // 获取我的商品数量
    const productRes = await fetch('/api/product/my?page=1&size=1', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const productData = await productRes.json()
    if (productData.code === 200 && productData.data) {
      stats.value.products = productData.data.total || 0
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

const editProfile = () => {
  router.push('/profile')
}

const changePassword = () => {
  ElMessage.info('修改密码功能开发中')
}

const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }).catch(() => {})
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.my-info-page {
  min-height: calc(100vh - 60px);
  background: #f5f5f5;
  padding: 20px;
}

.info-container {
  max-width: 800px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
}

.user-basic h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.user-type {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.stat-item {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-num {
  display: block;
  font-size: 24px;
  font-weight: 600;
  color: #FF6A00;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.menu-list {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #f5f5f5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: #f9f9f9;
}

.menu-item .el-icon {
  font-size: 20px;
  color: #666;
  margin-right: 12px;
}

.menu-item span {
  flex: 1;
  font-size: 15px;
  color: #333;
}

.menu-item .arrow {
  color: #ccc;
  font-size: 16px;
}

.menu-item.logout {
  color: #f56c6c;
}

.menu-item.logout .el-icon,
.menu-item.logout span {
  color: #f56c6c;
}

@media (max-width: 600px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
