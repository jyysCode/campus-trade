<template>
  <div class="detail-container" v-if="product">
    <div class="detail-content">
      <div class="detail-image">
        <img :src="getProductImage(product)" :alt="product.name" />
      </div>
      <div class="detail-info">
        <h1 class="product-title">{{ product.name }}</h1>
        <p class="product-price">¥{{ product.price }}</p>
        <div class="product-meta">
          <span class="meta-item">分类：{{ product.category }}</span>
          <span class="meta-item">发布时间：{{ formatTime(product.createTime) }}</span>
        </div>
        <p class="product-desc">{{ product.description }}</p>
        <div class="seller-info">
          <span class="seller-label">卖家：</span>
          <span class="seller-name">{{ product.sellerName }}</span>
          <span class="seller-phone">{{ product.sellerPhone }}</span>
        </div>
        <div class="action-buttons">
          <el-button type="primary" class="buy-btn" @click="buy">立即购买</el-button>
          <el-button 
            class="cart-btn" 
            :class="{ 'in-cart': isInCart }"
            @click="toggleCart"
          >
            <el-icon><ShoppingCart /></el-icon>
            {{ isInCart ? '已在购物车' : '加入购物车' }}
          </el-button>
          <el-button 
            class="favorite-btn" 
            :class="{ 'favorited': isFavorited }"
            @click="toggleFavorite"
          >
            <el-icon><Star /></el-icon>
            {{ isFavorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button class="chat-btn" @click="chat">联系卖家</el-button>
        </div>
      </div>
    </div>
    
    <!-- 功能开发中提示弹窗 -->
    <el-dialog 
      v-model="showFeatureDialog"
      title="提示" 
      width="350px"
      :show-close="false"
    >
      <div class="feature-content">
        <div class="feature-icon">🛠️</div>
        <p class="feature-text">立即购买功能正在开发中</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="showFeatureDialog = false">确认</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="showOrderDialog" title="确认订单" width="400px">
        <el-form :model="orderForm" class="order-form">
          <el-form-item label="商品名称">
            <span>{{ product.name }}</span>
          </el-form-item>
          <el-form-item label="商品价格">
            <span class="order-price">¥{{ product.price }}</span>
          </el-form-item>
          <el-form-item label="购买数量">
            <el-input-number v-model="orderForm.quantity" :min="1" :max="product.stock" />
          </el-form-item>
          <el-form-item label="收货地址">
            <el-input v-model="orderForm.address" placeholder="请输入收货地址" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="orderForm.remark" type="textarea" placeholder="请输入备注" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showOrderDialog = false">取消</el-button>
          <el-button type="primary" @click="submitOrder">确认下单</el-button>
        </template>
      </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '../stores/product'
import { useOrderStore } from '../stores/order'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Star } from '@element-plus/icons-vue'
import { getProductImage } from '../utils/image'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const orderStore = useOrderStore()
const userStore = useUserStore()

const product = ref(null)
const showOrderDialog = ref(false)
const showFeatureDialog = ref(false)
const isFavorited = ref(false)
const isInCart = ref(false)

const orderForm = reactive({
  quantity: 1,
  address: '',
  remark: ''
})

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const buy = () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (product.value.sellerId === userStore.userInfo?.id) {
    ElMessage.warning('不能购买自己的商品')
    return
  }
  showOrderDialog.value = true
}

const chat = () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (product.value.sellerId === userStore.userInfo?.id) {
    ElMessage.warning('不能联系自己')
    return
  }
  router.push(`/chat/${product.value.sellerId}?productId=${product.value.id}`)
}

// 加入/移出购物车 切换
const toggleCart = async () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // 不能添加自己的商品到购物车
  if (product.value.sellerId === userStore.userInfo?.id) {
    ElMessage.warning('不能购买自己的商品')
    return
  }
  try {
    const token = localStorage.getItem('token')
    if (isInCart.value) {
      const res = await fetch(`/api/cart/remove/${product.value.id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const data = await res.json()
      if (data.code === 200) {
        isInCart.value = false
        ElMessage.success('已移出购物车')
      } else {
        ElMessage.error(data.message || '操作失败')
      }
    } else {
      const res = await fetch(`/api/cart/add/${product.value.id}`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const data = await res.json()
      if (data.code === 200) {
        isInCart.value = true
        ElMessage.success('已加入购物车')
      } else {
        ElMessage.error(data.message || '添加失败')
      }
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 收藏/取消收藏 切换
const toggleFavorite = async () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const token = localStorage.getItem('token')
    if (isFavorited.value) {
      // 取消收藏
      const res = await fetch(`/api/favorite/remove/${product.value.id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const data = await res.json()
      if (data.code === 200) {
        isFavorited.value = false
        ElMessage.success('已取消收藏')
      } else {
        ElMessage.error(data.message || '操作失败')
      }
    } else {
      // 添加收藏
      const res = await fetch(`/api/favorite/add/${product.value.id}`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const data = await res.json()
      if (data.code === 200) {
        isFavorited.value = true
        ElMessage.success('已收藏')
      } else {
        ElMessage.error(data.message || '收藏失败')
      }
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const submitOrder = async () => {
  try {
    await orderStore.create({
      productId: product.value.id,
      quantity: orderForm.quantity,
      address: orderForm.address,
      remark: orderForm.remark
    })
    ElMessage.success('下单成功')
    showOrderDialog.value = false
    router.push('/orders')
  } catch (e) {
    ElMessage.error(e.message || '下单失败')
  }
}

onMounted(async () => {
  const id = route.params.id
  if (id) {
    try {
      product.value = await productStore.getDetail(id)
    } catch (e) {
      console.error('获取商品详情失败', e)
      ElMessage.error('获取商品详情失败')
    }
  }
  // 确保获取用户信息（用于判断是否是自己的商品）
  if (userStore.isLoggedIn() && !userStore.userInfo) {
    try { await userStore.fetchUserInfo() } catch (e) { /* ignore */ }
  }
  // 检查是否已收藏
  if (userStore.isLoggedIn() && id) {
    try {
      const token = localStorage.getItem('token')
      // 检查收藏状态
      const favRes = await fetch(`/api/favorite/check/${id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const favData = await favRes.json()
      if (favData.code === 200) {
        isFavorited.value = !!favData.data
      }
      // 检查购物车状态
      const cartRes = await fetch('/api/cart/list', {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const cartData = await cartRes.json()
      if (cartData.code === 200 && Array.isArray(cartData.data)) {
        isInCart.value = cartData.data.some(item => item.productId === parseInt(id))
      }
    } catch (e) { /* ignore */ }
  }
})
</script>

<style scoped>
.detail-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.detail-content {
  max-width: 1000px;
  margin: 0 auto;
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  display: flex;
  gap: 40px;
}

.detail-image {
  flex-shrink: 0;
}

.detail-image img {
  width: 400px;
  height: 400px;
  object-fit: cover;
  border-radius: 12px;
}

.detail-info {
  flex: 1;
}

.product-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.product-price {
  font-size: 36px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 20px;
}

.product-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.meta-item {
  font-size: 14px;
  color: #999;
}

.product-desc {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 20px;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 20px;
}

.seller-label {
  font-size: 14px;
  color: #999;
}

.seller-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.seller-phone {
  font-size: 14px;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 8px;
}

.buy-btn {
  min-width: 120px;
  height: 44px;
  font-size: 16px;
  border-radius: 22px;
}

.cart-btn, .favorite-btn, .chat-btn {
  height: 44px;
  font-size: 14px;
  border-radius: 22px;
}

.cart-btn {
  color: #FF6A00;
  border-color: #FF6A00;
}

.cart-btn:hover {
  background: #FF6A00;
  color: #fff;
}

.cart-btn.in-cart {
  background: #FFF3E8;
  color: #FF6A00;
  border-color: #FFB380;
}

.favorite-btn {
  color: #999;
  border-color: #DCDFE6;
}

.favorite-btn:hover {
  color: #FF4D4F;
  border-color: #FF4D4F;
}

.favorite-btn.favorited {
  background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%);
  color: #fff;
  border-color: transparent;
}

.favorite-btn.favorited:hover {
  opacity: 0.85;
}

.chat-btn {
  color: #666;
  border-color: #DCDFE6;
  border-radius: 22px;
}

.chat-btn:hover {
  color: #FF6A00;
  border-color: #FF6A00;
}

.order-form {
  padding: 20px 0;
}

.order-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}
</style>
