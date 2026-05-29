<template>
  <div class="detail-page" v-loading="loading">
    <template v-if="product">
      <div class="detail-main">
        <div class="detail-img">
          <img :src="product.image || 'https://via.placeholder.com/400x400'" />
        </div>
        <div class="detail-info">
          <h1>{{ product.name }}</h1>
          <div class="price-row">
            <span class="price-now">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="price-old">原价 ¥{{ product.originalPrice }}</span>
          </div>
          <div class="info-row"><span>分类：</span>{{ product.category }}</div>
          <div class="info-row"><span>成色：</span>{{ product.condition || '未标注' }}</div>
          <div class="info-row"><span>库存：</span>{{ product.stock }}件</div>
          <div class="info-row"><span>销量：</span>{{ product.sales || 0 }}件</div>
          <div class="info-row"><span>卖家：</span>{{ product.sellerName }}</div>
          <div class="info-row"><span>发布时间：</span>{{ product.createTime }}</div>
          <el-divider />
          <div class="action-row">
            <el-input-number v-model="quantity" :min="1" :max="product.stock" size="large" />
            <el-button type="primary" size="large" @click="handleBuy" :disabled="product.sellerId === currentUserId">立即购买</el-button>
            <el-button type="warning" size="large" @click="handleAddCart" :disabled="product.sellerId === currentUserId">
              <el-icon><ShoppingCart /></el-icon> 加入购物车
            </el-button>
            <el-button size="large" @click="handleFavorite" :type="isFavorited ? 'danger' : 'default'">
              <el-icon><Star /></el-icon> {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
            <el-button size="large" @click="handleChat" :disabled="product.sellerId === currentUserId">
              <el-icon><ChatDotRound /></el-icon> 私信卖家
            </el-button>
          </div>
        </div>
      </div>
      <div class="detail-desc">
        <h2>商品描述</h2>
        <p>{{ product.description }}</p>
      </div>
      <div class="detail-reviews">
        <h2>买家评价 ({{ reviews.length }})</h2>
        <div v-if="reviews.length">
          <div v-for="r in reviews" :key="r.id" class="review-item">
            <div class="review-header">
              <span class="review-user">{{ r.username }}</span>
              <el-rate v-model="r.rating" disabled />
              <span class="review-time">{{ r.createTime }}</span>
            </div>
            <p>{{ r.content }}</p>
          </div>
        </div>
        <el-empty v-else description="暂无评价" />
      </div>
    </template>
    <FooterBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getProductDetail, addToCart, addFavorite, removeFavorite, checkFavorite, getProductReviews, createOrder } from '../api'
import FooterBar from '../components/FooterBar.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, Star, ChatDotRound } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const product = ref(null)
const reviews = ref([])
const isFavorited = ref(false)
const quantity = ref(1)
const loading = ref(true)
const currentUserId = computed(() => userStore.userInfo?.id)

const loadData = async () => {
  try {
    const [pRes, rRes] = await Promise.all([
      getProductDetail(route.params.id),
      getProductReviews(route.params.id, { page: 1, size: 20 })
    ])
    product.value = pRes.data.data
    reviews.value = rRes.data.data?.records || []
    if (userStore.isLoggedIn()) {
      try {
        const fRes = await checkFavorite(route.params.id)
        isFavorited.value = fRes.data.data
      } catch (e) {}
    }
  } catch (e) {}
  loading.value = false
}

const handleBuy = async () => {
  if (!userStore.isLoggedIn()) { router.push('/login'); return }
  try {
    await ElMessageBox.prompt('请输入收货地址', '确认购买', { confirmButtonText: '确认下单', cancelButtonText: '取消', inputPattern: /.+/, inputErrorMessage: '请输入收货地址' })
      .then(async ({ value }) => {
        await createOrder({ productId: product.value.id, quantity: quantity.value, address: value })
        ElMessage.success('下单成功！')
        router.push('/orders')
      })
  } catch (e) { /* cancelled */ }
}

const handleAddCart = async () => {
  if (!userStore.isLoggedIn()) { router.push('/login'); return }
  try {
    await addToCart(product.value.id)
    ElMessage.success('已加入购物车')
  } catch (e) {}
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn()) { router.push('/login'); return }
  try {
    if (isFavorited.value) {
      await removeFavorite(product.value.id)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(product.value.id)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e) {}
}

const handleChat = () => {
  if (!userStore.isLoggedIn()) { router.push('/login'); return }
  router.push(`/chat/${product.value.sellerId}`)
}

onMounted(loadData)
</script>

<style scoped>
.detail-page { max-width: 1200px; margin: 0 auto; padding: 20px; }
.detail-main { display: flex; gap: 30px; background: #fff; padding: 24px; border-radius: 8px; margin-bottom: 20px; }
.detail-img { width: 400px; flex-shrink: 0; }
.detail-img img { width: 100%; border-radius: 8px; }
.detail-info { flex: 1; }
.detail-info h1 { font-size: 22px; margin-bottom: 16px; }
.price-row { margin-bottom: 16px; }
.price-now { font-size: 28px; color: #ff4757; font-weight: bold; }
.price-old { font-size: 14px; color: #999; text-decoration: line-through; margin-left: 12px; }
.info-row { font-size: 14px; color: #666; margin-bottom: 8px; }
.info-row span { color: #999; }
.action-row { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; margin-top: 16px; }
.detail-desc, .detail-reviews { background: #fff; padding: 24px; border-radius: 8px; margin-bottom: 20px; }
.detail-desc h2, .detail-reviews h2 { font-size: 18px; margin-bottom: 16px; }
.detail-desc p { color: #666; line-height: 1.8; white-space: pre-wrap; }
.review-item { padding: 16px 0; border-bottom: 1px solid #f0f0f0; }
.review-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.review-user { font-weight: 500; }
.review-time { color: #999; font-size: 12px; }
</style>
