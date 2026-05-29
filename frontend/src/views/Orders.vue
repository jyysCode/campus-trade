<template>
  <div class="orders-container">
    <div class="orders-tabs">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="我购买的" name="buyer" />
        <el-tab-pane label="我卖出的" name="seller" />
      </el-tabs>
    </div>
    <div class="orders-content">
      <div v-if="activeTab === 'buyer'" class="orders-list">
        <el-card
          v-for="order in buyerOrders"
          :key="order.id"
          class="order-card"
        >
          <div class="order-header">
            <span class="order-id">订单号：{{ order.id }}</span>
            <span :class="['order-status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</span>
          </div>
          <div class="order-content">
            <img :src="order.productImage" :alt="order.productName" class="order-product-image" />
            <div class="order-product-info">
              <h3 class="order-product-name">{{ order.productName }}</h3>
              <p class="order-product-price">¥{{ order.price }}</p>
              <p class="order-quantity">数量：{{ order.quantity }}</p>
            </div>
          </div>
          <div class="order-footer">
            <span class="order-total">合计：¥{{ order.totalAmount }}</span>
            <div class="order-actions">
              <el-button v-if="order.status === 1" type="primary" @click="confirmOrder(order.id)">确认收货</el-button>
            </div>
          </div>
        </el-card>
        <div v-if="buyerOrders.length === 0" class="empty-state">
          <el-icon size="48" color="#ccc"><ShoppingCart /></el-icon>
          <p>暂无订单</p>
        </div>
      </div>
      <div v-else class="orders-list">
        <el-card
          v-for="order in sellerOrders"
          :key="order.id"
          class="order-card"
        >
          <div class="order-header">
            <span class="order-id">订单号：{{ order.id }}</span>
            <span :class="['order-status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</span>
          </div>
          <div class="order-content">
            <img :src="order.productImage" :alt="order.productName" class="order-product-image" />
            <div class="order-product-info">
              <h3 class="order-product-name">{{ order.productName }}</h3>
              <p class="order-product-price">¥{{ order.price }}</p>
              <p class="order-quantity">数量：{{ order.quantity }}</p>
            </div>
          </div>
          <div class="order-footer">
            <span class="order-total">合计：¥{{ order.totalAmount }}</span>
            <div class="order-actions">
              <el-button v-if="order.status === 0" type="primary" @click="shipOrder(order.id)">发货</el-button>
            </div>
          </div>
        </el-card>
        <div v-if="sellerOrders.length === 0" class="empty-state">
          <el-icon size="48" color="#ccc"><Box /></el-icon>
          <p>暂无订单</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ShoppingCart, Box } from '@element-plus/icons-vue'
import { useOrderStore } from '../stores/order'
import { ElMessage } from 'element-plus'

const orderStore = useOrderStore()

const activeTab = ref('buyer')
const buyerOrders = ref([])
const sellerOrders = ref([])

const getStatusText = (status) => {
  const statusMap = {
    0: '待发货',
    1: '待收货',
    2: '已完成',
    3: '已取消'
  }
  return statusMap[status] || '未知状态'
}

const getStatusClass = (status) => {
  const classMap = {
    0: 'status-pending',
    1: 'status-shipped',
    2: 'status-completed',
    3: 'status-canceled'
  }
  return classMap[status] || ''
}

const handleTabChange = async () => {
  if (activeTab.value === 'buyer') {
    await loadBuyerOrders()
  } else {
    await loadSellerOrders()
  }
}

const loadBuyerOrders = async () => {
  try {
    const data = await orderStore.buyerOrders()
    buyerOrders.value = data.records || data
  } catch (e) {
    console.error('加载购买订单失败', e)
  }
}

const loadSellerOrders = async () => {
  try {
    const data = await orderStore.sellerOrders()
    sellerOrders.value = data.records || data
  } catch (e) {
    console.error('加载销售订单失败', e)
  }
}

const confirmOrder = async (id) => {
  try {
    await orderStore.updateStatus(id, 2)
    ElMessage.success('确认收货成功')
    await loadBuyerOrders()
  } catch (e) {
    ElMessage.error(e.message || '确认收货失败')
  }
}

const shipOrder = async (id) => {
  try {
    await orderStore.updateStatus(id, 1)
    ElMessage.success('发货成功')
    await loadSellerOrders()
  } catch (e) {
    ElMessage.error(e.message || '发货失败')
  }
}

onMounted(async () => {
  if (activeTab.value === 'buyer') {
    await loadBuyerOrders()
  } else {
    await loadSellerOrders()
  }
})
</script>

<style scoped>
.orders-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.orders-tabs {
  max-width: 1200px;
  margin: 0 auto 20px;
}

.orders-content {
  max-width: 1200px;
  margin: 0 auto;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.order-id {
  font-size: 14px;
  color: #999;
}

.order-status {
  font-size: 14px;
  font-weight: bold;
}

.status-pending {
  color: #e6a23c;
}

.status-shipped {
  color: #409eff;
}

.status-completed {
  color: #67c23a;
}

.status-canceled {
  color: #909399;
}

.order-content {
  display: flex;
  gap: 20px;
  padding: 20px;
}

.order-product-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.order-product-info {
  flex: 1;
}

.order-product-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.order-product-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 4px;
}

.order-quantity {
  font-size: 14px;
  color: #999;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}

.order-total {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.empty-state p {
  margin-top: 16px;
}
</style>