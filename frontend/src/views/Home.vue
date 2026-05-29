<template>
  <div class="home-container">
    <!-- 橙色Banner -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1>校园二手交易，安全便捷有保障</h1>
        <div class="stats">
          <div class="stat-item">
            <span class="stat-num">1.2万</span>
            <span class="stat-label">注册用户</span>
          </div>
          <div class="stat-item">
            <span class="stat-num">5千+</span>
            <span class="stat-label">在售商品</span>
          </div>
          <div class="stat-item">
            <span class="stat-num">8千+</span>
            <span class="stat-label">成功订单</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门推荐 -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">🔥 热门推荐</h2>
        <a class="view-more" @click="router.push('/search')">查看更多 ></a>
      </div>
      <div class="product-grid">
        <div
          v-for="product in hotProducts"
          :key="product.id"
          class="product-card"
          @click="goDetail(product.id)"
        >
          <img :src="product.image || getDefaultImage(product.id)" :alt="product.name" class="product-image" @error="handleImgError" />
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-price">¥{{ product.price }}</p>
            <p class="product-seller">{{ product.sellerName }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 最新发布 -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">✨ 最新发布</h2>
        <a class="view-more" @click="router.push('/search')">查看更多 ></a>
      </div>
      <div class="product-grid">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click="goDetail(product.id)"
        >
          <img :src="product.image || getDefaultImage(product.id)" :alt="product.name" class="product-image" @error="handleImgError" />
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-price">¥{{ product.price }}</p>
            <p class="product-seller">{{ product.sellerName }}</p>
          </div>
        </div>
      </div>
      <el-pagination
        v-if="total > pageSize"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
        class="pagination"
        background
        layout="prev, pager, next"
      />
    </div>

    <!-- 分类浏览 -->
    <div class="section">
      <h2 class="section-title">📦 分类浏览</h2>
      <div class="category-grid">
        <div
          v-for="cat in categories"
          :key="cat.value"
          class="category-card"
          @click="router.push(`/search?category=${encodeURIComponent(cat.value)}`)"
        >
          <span class="category-icon">{{ cat.icon }}</span>
          <span class="category-name">{{ cat.label }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '../stores/product'

const router = useRouter()
const productStore = useProductStore()

const hotProducts = ref([])
const products = ref([])
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

const categories = [
  { label: '电子产品', value: '电子产品', icon: '📱' },
  { label: '图书教材', value: '图书教材', icon: '📚' },
  { label: '生活用品', value: '生活用品', icon: '🏠' },
  { label: '服饰鞋包', value: '服饰鞋包', icon: '👕' },
  { label: '运动器材', value: '运动器材', icon: '⚽' },
  { label: '美妆护肤', value: '美妆护肤', icon: '💄' },
  { label: '其他', value: '其他', icon: '📦' },
]

const goDetail = (id) => {
  router.push(`/product/${id}`)
}

const getDefaultImage = (id) => {
  return `https://picsum.photos/seed/product${id || Math.random().toString(36).substr(2, 6)}/300/300`
}

const handleImgError = (e) => {
  e.target.src = 'https://picsum.photos/seed/' + Math.random().toString(36).substr(2, 6) + '/300/300'
}

const handlePageChange = async (page) => {
  currentPage.value = page
  await loadProducts()
}

const loadProducts = async () => {
  try {
    const data = await productStore.list({
      page: currentPage.value,
      size: pageSize.value,
    })
    products.value = data.records || data || []
    total.value = data.total || 0
  } catch (e) {
    console.error('加载商品失败', e)
  }
}

const loadHotProducts = async () => {
  try {
    hotProducts.value = await productStore.hot()
  } catch (e) {
    console.error('加载热门商品失败', e)
  }
}

onMounted(async () => {
  await loadHotProducts()
  await loadProducts()
})
</script>

<style scoped>
.home-container {
  background: #f5f5f5;
}

/* Hero Banner */
.hero-banner {
  background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%);
  padding: 50px 20px;
  text-align: center;
}

.hero-content h1 {
  font-size: 36px;
  color: #fff;
  margin-bottom: 30px;
  font-weight: bold;
}

.stats {
  display: flex;
  justify-content: center;
  gap: 60px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-num {
  font-size: 32px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 4px;
}

/* Sections */
.section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 22px;
  font-weight: bold;
  color: #333;
}

.view-more {
  color: #FF6A00;
  font-size: 14px;
  cursor: pointer;
}

.view-more:hover {
  text-decoration: underline;
}

/* Product Grid */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 20px;
  color: #FF6A00;
  font-weight: bold;
  margin-bottom: 4px;
}

.product-seller {
  font-size: 12px;
  color: #999;
}

/* Category Grid */
.category-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 16px;
}

.category-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 10px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.category-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.category-icon {
  font-size: 32px;
  display: block;
  margin-bottom: 8px;
}

.category-name {
  font-size: 14px;
  color: #333;
}

/* Pagination */
.pagination {
  margin-top: 24px;
  text-align: center;
}

/* Responsive */
@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .category-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .stats {
    gap: 30px;
  }
  .hero-content h1 {
    font-size: 24px;
  }
  .stat-num {
    font-size: 24px;
  }
}
</style>
