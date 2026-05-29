<template>
  <div class="cart-page">
    <h1>🛒 我的购物车</h1>
    <div v-if="cartItems.length" class="cart-list">
      <div v-for="item in cartItems" :key="item.id" class="cart-item">
        <img :src="item.productImage || 'https://via.placeholder.com/100x100'" class="cart-item-img" @click="$router.push(`/product/${item.productId}`)" />
        <div class="cart-item-info">
          <h3 @click="$router.push(`/product/${item.productId}`)">{{ item.productName || '商品' + item.productId }}</h3>
          <p class="cart-item-price">¥{{ item.productPrice || '0.00' }}</p>
        </div>
        <div class="cart-item-actions">
          <el-input-number v-model="item.quantity" :min="1" :max="99" size="small" @change="updateQty(item)" />
          <el-button type="danger" size="small" text @click="handleRemove(item.productId)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
      <div class="cart-footer">
        <el-button @click="handleClear" type="info" plain>清空购物车</el-button>
        <div class="cart-total">
          <span>共 {{ cartItems.length }} 件商品</span>
          <el-button type="primary" size="large" @click="handleBuyAll">全部购买</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="购物车是空的">
      <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, updateCartQuantity, removeFromCart, clearCart } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const router = useRouter()
const cartItems = ref([])

const loadCart = async () => {
  const res = await getCartList()
  cartItems.value = res.data.data || []
}

const updateQty = async (item) => {
  try { await updateCartQuantity(item.productId, item.quantity) } catch (e) {}
}

const handleRemove = async (productId) => {
  await removeFromCart(productId)
  cartItems.value = cartItems.value.filter(i => i.productId !== productId)
  ElMessage.success('已移除')
}

const handleClear = async () => {
  await ElMessageBox.confirm('确定清空购物车？', '提示')
  await clearCart()
  cartItems.value = []
  ElMessage.success('购物车已清空')
}

const handleBuyAll = () => {
  ElMessage.info('请前往商品详情页逐个下单购买')
}

onMounted(loadCart)
</script>

<style scoped>
.cart-page { max-width: 900px; margin: 0 auto; padding: 20px; }
.cart-page h1 { margin-bottom: 20px; }
.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 12px;
}
.cart-item-img { width: 80px; height: 80px; border-radius: 8px; object-fit: cover; cursor: pointer; }
.cart-item-info { flex: 1; }
.cart-item-info h3 { font-size: 15px; cursor: pointer; margin-bottom: 4px; }
.cart-item-info h3:hover { color: #409eff; }
.cart-item-price { color: #ff4757; font-size: 16px; font-weight: bold; }
.cart-item-actions { display: flex; align-items: center; gap: 12px; }
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
}
.cart-total { display: flex; align-items: center; gap: 16px; }
</style>
