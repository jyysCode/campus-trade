<template>
  <div class="cart-page">
    <div class="cart-content">
      <h1 class="page-title">🛒 我的购物车</h1>
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>
      <div v-else-if="cartItems.length > 0" class="cart-list">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <div class="item-check">
            <el-checkbox v-model="item.selected" @change="updateTotal" />
          </div>
          <img :src="getProductImage(item)" :alt="item.productName" class="item-image" @click="goDetail(item.productId)" />
          <div class="item-info">
            <h3 class="item-name" @click="goDetail(item.productId)">{{ item.productName }}</h3>
            <p class="item-seller">卖家：{{ item.sellerName }}</p>
            <div class="item-price-row">
              <span class="item-price">¥{{ formatPrice(item.productPrice) }}</span>
              <div class="item-quantity">
                <el-button size="small" :disabled="item.quantity <= 1" @click="decreaseQuantity(item)">-</el-button>
                <span class="quantity-num">{{ item.quantity }}</span>
                <el-button size="small" @click="increaseQuantity(item)">+</el-button>
              </div>
            </div>
          </div>
          <div class="item-actions">
            <el-button type="danger" size="small" @click="removeItem(item.productId)" plain>删除</el-button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <span>🛒</span>
        <p>购物车是空的</p>
        <button class="go-btn" @click="goHome">去看看商品</button>
      </div>
      <div v-if="cartItems.length > 0" class="cart-footer">
        <div class="select-all">
          <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
        </div>
        <div class="total-info">
          <span class="total-label">合计：</span>
          <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <el-button type="primary" class="checkout-btn" @click="checkout" :disabled="selectedCount === 0">
          结算 ({{ selectedCount }})
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductImage } from '../utils/image'

const router = useRouter()
const cartItems = ref([])
const selectAll = ref(false)
const loading = ref(false)

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => {
      const price = item.productPrice ? Number(item.productPrice) : 0
      return sum + price * item.quantity
    }, 0)
})

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length
})

const updateTotal = () => {
  selectAll.value = cartItems.value.length > 0 && cartItems.value.every(item => item.selected)
}

const handleSelectAll = (val) => {
  cartItems.value.forEach(item => { item.selected = val })
}

const goDetail = (id) => router.push(`/product/${id}`)
const goHome = () => router.push('/')

const loadCart = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await fetch('/api/cart/list', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      cartItems.value = (data.data || []).map(item => ({ ...item, selected: false }))
    } else {
      ElMessage.error(data.message || '加载购物车失败')
    }
  } catch (e) {
    console.error('加载购物车失败', e)
    ElMessage.error('加载购物车失败')
  } finally {
    loading.value = false
  }
}

const decreaseQuantity = async (item) => {
  if (item.quantity <= 1) return
  await updateQuantity(item.productId, item.quantity - 1)
  item.quantity -= 1
}

const increaseQuantity = async (item) => {
  await updateQuantity(item.productId, item.quantity + 1)
  item.quantity += 1
}

const updateQuantity = async (productId, quantity) => {
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`/api/cart/update/${productId}?quantity=${quantity}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code !== 200) {
      ElMessage.error(data.message || '更新失败')
    }
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

const removeItem = async (productId) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
    const token = localStorage.getItem('token')
    const res = await fetch(`/api/cart/remove/${productId}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      cartItems.value = cartItems.value.filter(item => item.productId !== productId)
      ElMessage.success('删除成功')
    } else {
      ElMessage.error(data.message || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const checkout = async () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  try {
    const token = localStorage.getItem('token')
    const res = await fetch('/api/order/batch-create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify({ items: selectedItems.map(item => ({ productId: item.productId, quantity: item.quantity })) })
    })
    const data = await res.json()
    if (data.code === 200) {
      ElMessage.success('下单成功')
      cartItems.value = cartItems.value.filter(item => !item.selected)
      router.push('/orders')
    } else {
      ElMessage.error(data.message || '下单失败')
    }
  } catch (e) {
    ElMessage.error('下单失败')
  }
}

onMounted(() => { loadCart() })
</script>

<style scoped>
.cart-page { min-height: 100vh; background: #F5F5F5; }
.cart-content { max-width: 1200px; margin: 0 auto; padding: 24px 16px; }
.page-title { font-size: 22px; font-weight: 700; color: #333; margin-bottom: 24px; }
.loading-state { background: #fff; border-radius: 12px; padding: 40px; }
.cart-list { display: flex; flex-direction: column; gap: 16px; margin-bottom: 80px; }
.cart-item {
  display: flex; align-items: center; gap: 16px;
  background: #FFFFFF; border-radius: 12px; padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.item-check { flex-shrink: 0; }
.item-image { width: 100px; height: 100px; border-radius: 8px; object-fit: cover; cursor: pointer; flex-shrink: 0; }
.item-info { flex: 1; min-width: 0; }
.item-name { font-size: 16px; font-weight: 600; color: #333; margin-bottom: 8px; cursor: pointer; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.item-name:hover { color: #FF6A00; }
.item-seller { font-size: 13px; color: #999; margin-bottom: 12px; }
.item-price-row { display: flex; justify-content: space-between; align-items: center; }
.item-price { font-size: 18px; font-weight: 700; color: #FF6A00; }
.item-quantity { display: flex; align-items: center; gap: 8px; }
.quantity-num { min-width: 30px; text-align: center; font-size: 14px; }
.item-actions { flex-shrink: 0; }
.empty-state { text-align: center; padding: 80px 0; color: #999; }
.empty-state span { font-size: 48px; display: block; margin-bottom: 12px; }
.go-btn { margin-top: 16px; padding: 10px 32px; background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%); color: #FFFFFF; border: none; border-radius: 24px; font-size: 14px; cursor: pointer; }
.cart-footer {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #FFFFFF; border-top: 1px solid #EEEEEE;
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 24px; box-shadow: 0 -4px 12px rgba(0,0,0,0.08);
  z-index: 100;
}
.select-all { flex-shrink: 0; }
.total-info { flex: 1; text-align: right; padding-right: 24px; }
.total-label { font-size: 14px; color: #666; }
.total-price { font-size: 24px; font-weight: 700; color: #FF6A00; }
.checkout-btn {
  min-width: 120px; height: 44px;
  background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%) !important;
  border: none !important; font-size: 16px;
}
.checkout-btn:disabled { opacity: 0.5; }
</style>
