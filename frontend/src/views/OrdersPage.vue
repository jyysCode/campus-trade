<template>
  <div class="orders-page">
    <h1>📋 我的订单</h1>
    <el-tabs v-model="activeTab" @tab-change="loadOrders">
      <el-tab-pane label="我买到的" name="buyer" />
      <el-tab-pane label="我卖出的" name="seller" />
    </el-tabs>
    <div v-loading="loading">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span>订单号：{{ order.orderNo || order.id }}</span>
          <el-tag :type="statusType(order.status)" size="small">{{ statusText(order.status) }}</el-tag>
          <span class="order-time">{{ order.createTime }}</span>
        </div>
        <div class="order-body" @click="$router.push(`/product/${order.productId}`)">
          <img :src="order.productImage || 'https://via.placeholder.com/80x80'" class="order-img" />
          <div class="order-info">
            <h3>{{ order.productName }}</h3>
            <p>数量：{{ order.quantity }}　单价：¥{{ order.price }}　合计：<b>¥{{ order.totalAmount }}</b></p>
          </div>
        </div>
        <div class="order-footer">
          <el-button v-if="activeTab === 'buyer' && order.status === 0" type="primary" size="small" @click="updateStatus(order.id, 1)">付款</el-button>
          <el-button v-if="activeTab === 'buyer' && order.status === 2" type="success" size="small" @click="updateStatus(order.id, 3)">确认收货</el-button>
          <el-button v-if="activeTab === 'buyer' && order.status === 0" type="info" size="small" @click="updateStatus(order.id, 5)">取消订单</el-button>
          <el-button v-if="activeTab === 'seller' && order.status === 1" type="primary" size="small" @click="updateStatus(order.id, 2)">发货</el-button>
        </div>
      </div>
      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
    </div>
    <el-pagination v-if="total > 0" v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadOrders" style="margin-top:20px;text-align:center" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getBuyerOrders, getSellerOrders, updateOrderStatus } from '../api'
import { ElMessage } from 'element-plus'

const activeTab = ref('buyer')
const orders = ref([])
const page = ref(1)
const total = ref(0)
const loading = ref(false)

const statusText = (s) => ({ 0: '待付款', 1: '已付款', 2: '已发货', 3: '已完成', 4: '退款中', 5: '已取消' }[s] || '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'primary', 2: '', 3: 'success', 4: 'danger', 5: 'info' }[s] || 'info')

const loadOrders = async () => {
  loading.value = true
  try {
    const fn = activeTab.value === 'buyer' ? getBuyerOrders : getSellerOrders
    const res = await fn({ page: page.value, size: 10 })
    orders.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } catch (e) {}
  loading.value = false
}

const doUpdateStatus = async (id, status) => {
  try {
    await updateOrderStatus(id, status)
    ElMessage.success('操作成功')
    loadOrders()
  } catch (e) {}
}

onMounted(loadOrders)
</script>

<style scoped>
.orders-page { max-width: 900px; margin: 0 auto; padding: 20px; }
.orders-page h1 { margin-bottom: 20px; }
.order-card { background: #fff; border-radius: 8px; margin-bottom: 12px; overflow: hidden; }
.order-header { display: flex; align-items: center; gap: 12px; padding: 12px 16px; background: #fafafa; font-size: 13px; color: #999; }
.order-time { margin-left: auto; }
.order-body { display: flex; gap: 12px; padding: 16px; cursor: pointer; }
.order-body:hover { background: #f9f9f9; }
.order-img { width: 80px; height: 80px; border-radius: 8px; object-fit: cover; }
.order-info { flex: 1; }
.order-info h3 { font-size: 15px; margin-bottom: 6px; }
.order-info p { font-size: 13px; color: #666; }
.order-info b { color: #ff4757; }
.order-footer { padding: 12px 16px; text-align: right; border-top: 1px solid #f0f0f0; }
</style>
