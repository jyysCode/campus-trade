<template>
  <div class="navbar">
    <div class="navbar-inner">
      <div class="navbar-left">
        <router-link to="/" class="logo">📚 校园二手交易</router-link>
      </div>
      <div class="navbar-center">
        <router-link to="/" class="nav-link">首页</router-link>
        <span class="nav-sep">|</span>
        <router-link to="/search" class="nav-link">搜索</router-link>
      </div>
      <div class="navbar-right">
        <template v-if="userStore.isLoggedIn()">
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
            <router-link to="/cart" class="nav-icon" title="购物车">
              <el-icon :size="22"><ShoppingCart /></el-icon>
            </router-link>
          </el-badge>
          <router-link to="/favorites" class="nav-icon" title="收藏">
            <el-icon :size="22"><Star /></el-icon>
          </router-link>
          <router-link to="/orders" class="nav-icon" title="我的订单">
            <el-icon :size="22"><List /></el-icon>
          </router-link>
          <router-link to="/publish" class="nav-btn publish-btn">
            <el-icon><Plus /></el-icon> 发布商品
          </router-link>
          <el-dropdown @command="handleCommand">
            <span class="nav-user">
              {{ userStore.userInfo?.username || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                <el-dropdown-item command="publish">发布商品</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-btn login-btn">登录</router-link>
          <router-link to="/register" class="nav-btn register-btn">注册</router-link>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getCartCount } from '../api'
import { ShoppingCart, Star, List, Plus, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const cartCount = ref(0)

const loadCartCount = async () => {
  if (userStore.isLoggedIn()) {
    try {
      const res = await getCartCount()
      cartCount.value = res.data.data || 0
    } catch (e) { /* ignore */ }
  }
}

const handleCommand = (cmd) => {
  const map = { profile: '/profile', orders: '/orders', favorites: '/favorites', publish: '/publish' }
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/')
    cartCount.value = 0
  } else if (map[cmd]) {
    router.push(map[cmd])
  }
}

onMounted(loadCartCount)
</script>

<style scoped>
.navbar {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}
.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 20px;
}
.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  white-space: nowrap;
}
.navbar-center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}
.nav-link {
  color: #666;
  font-size: 15px;
  transition: color 0.2s;
}
.nav-link:hover { color: #409eff; }
.nav-link.router-link-active { color: #409eff; font-weight: 600; }
.nav-sep { color: #ddd; }
.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.nav-icon {
  color: #666;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}
.nav-icon:hover { color: #409eff; }
.cart-badge { margin-right: 4px; }
.nav-btn {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.2s;
  white-space: nowrap;
}
.login-btn {
  border: 1px solid #409eff;
  color: #409eff;
}
.login-btn:hover { background: #ecf5ff; }
.register-btn {
  background: #409eff;
  color: #fff;
}
.register-btn:hover { background: #66b1ff; }
.publish-btn {
  background: #ff6b35;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 4px;
}
.publish-btn:hover { background: #ff8c5a; }
.nav-user {
  cursor: pointer;
  font-size: 14px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
