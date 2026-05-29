<template>
  <div class="favorites-page">
    <div class="favorites-content">
      <h1 class="page-title">❤️ 我的收藏</h1>
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>
      <div class="product-grid" v-else-if="favorites.length > 0">
        <div v-for="item in favorites" :key="item.id" class="product-card" @click="goDetail(item.id)">
          <div class="card-image">
            <img :src="getProductImage(item)" :alt="item.name" />
          </div>
          <div class="card-content">
            <h3 class="product-name">{{ item.name }}</h3>
            <div class="product-price-row">
              <span class="price-current">¥{{ item.price }}</span>
              <span v-if="item.originalPrice" class="price-original">¥{{ item.originalPrice }}</span>
            </div>
            <div class="product-meta">
              <span class="product-category">{{ item.category }}</span>
              <span class="product-seller">{{ item.sellerName }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <span>❤️</span>
        <p>暂无收藏</p>
        <button class="go-btn" @click="goHome">去看看商品</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductImage } from '../utils/image'

const router = useRouter()
const favorites = ref([])
const loading = ref(false)

const goHome = () => router.push('/')
const goDetail = (id) => router.push(`/product/${id}`)

const loadFavorites = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await fetch('/api/favorite/list', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      // 后端返回的是分页对象，需要取records
      const pageData = data.data || {}
      const items = pageData.records || []
      const products = []
      for (let item of items) {
        try {
          const productRes = await fetch(`/api/product/detail/${item.productId}`)
          const productData = await productRes.json()
          if (productData.code === 200) {
            products.push(productData.data)
          }
        } catch (e) {
          console.error('获取商品详情失败', e)
        }
      }
      favorites.value = products
    } else {
      ElMessage.error(data.message || '加载收藏失败')
    }
  } catch (e) {
    console.error('加载收藏失败', e)
    ElMessage.error('加载收藏失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-page { min-height: 100vh; background: #F5F5F5; }
.favorites-content { max-width: 1200px; margin: 0 auto; padding: 24px 16px; }
.page-title { font-size: 22px; font-weight: 700; color: #333; margin-bottom: 24px; }
.loading-state { background: #fff; border-radius: 12px; padding: 40px; }
.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}
.card-image {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
}
.card-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.product-card:hover .card-image img {
  transform: scale(1.05);
}
.card-content {
  padding: 16px;
}
.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 12px;
}
.price-current {
  font-size: 20px;
  font-weight: bold;
  color: #FF6A00;
}
.price-original {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}
.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
.product-category {
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
}
.empty-state { text-align: center; padding: 80px 0; color: #999; }
.empty-state span { font-size: 48px; display: block; margin-bottom: 12px; }
.go-btn { margin-top: 16px; padding: 10px 32px; background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%); color: #FFFFFF; border: none; border-radius: 24px; font-size: 14px; cursor: pointer; }

@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}
</style>
