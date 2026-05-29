<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px" class="admin-aside">
        <div class="admin-logo">🔧 管理后台</div>
        <el-menu :default-active="currentRoute" router>
          <el-menu-item index="/admin"><el-icon><DataAnalysis /></el-icon>数据概览</el-menu-item>
          <el-menu-item index="/admin/products"><el-icon><Goods /></el-icon>商品管理</el-menu-item>
          <el-menu-item index="/admin/orders"><el-icon><List /></el-icon>订单管理</el-menu-item>
          <el-menu-item index="/admin/users"><el-icon><User /></el-icon>用户管理</el-menu-item>
        </el-menu>
        <div class="admin-logout">
          <el-button @click="handleLogout" type="info" plain size="small">退出登录</el-button>
        </div>
      </el-aside>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { DataAnalysis, Goods, List, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const currentRoute = computed(() => route.path)

const handleLogout = () => {
  userStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout { min-height: 100vh; }
.admin-aside { background: #304156; }
.admin-logo { color: #fff; text-align: center; padding: 20px; font-size: 18px; font-weight: bold; }
.admin-logout { padding: 20px; text-align: center; }
.admin-main { background: #f0f2f5; padding: 20px; }
.el-menu { border-right: none; background: #304156; }
:deep(.el-menu-item) { color: #bfcbd9; }
:deep(.el-menu-item:hover), :deep(.el-menu-item.is-active) { background: #263445; color: #409eff; }
</style>
