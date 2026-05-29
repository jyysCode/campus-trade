<template>
  <div>
    <h2>数据概览</h2>
    <el-row :gutter="20">
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="用户总数" :value="stats.users" /></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="商品总数" :value="stats.products" /></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="订单总数" :value="stats.orders" /></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="待审核商品" :value="stats.pending" /></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProductList, getUserList, getAllOrders, getPendingProducts } from '../api'

const stats = ref({ users: 0, products: 0, orders: 0, pending: 0 })

const loadStats = async () => {
  try {
    const [pRes, uRes, oRes, pendingRes] = await Promise.all([
      getProductList({ page: 1, size: 1 }),
      getUserList(),
      getAllOrders({ page: 1, size: 1 }),
      getPendingProducts({ page: 1, size: 1 })
    ])
    stats.value.products = pRes.data.data?.total || 0
    stats.value.users = (uRes.data.data || []).length
    stats.value.orders = oRes.data.data?.total || 0
    stats.value.pending = pendingRes.data.data?.total || 0
  } catch (e) {}
}

onMounted(loadStats)
</script>

<style scoped>
h2 { margin-bottom: 20px; }
.el-col { margin-bottom: 16px; }
</style>
