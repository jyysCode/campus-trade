<template>
  <div class="search-page">
    <!-- 筛选栏 - 滚动到顶部时固定 -->
    <div class="filter-bar" ref="filterBar">
      <div class="filter-content">
        <div class="filter-row">
          <span class="filter-label">分类：</span>
          <el-select v-model="filter.category" placeholder="全部分类" clearable @change="handleSearch" style="width: 150px">
            <el-option label="电子产品" value="电子产品" />
            <el-option label="图书教材" value="图书教材" />
            <el-option label="生活用品" value="生活用品" />
            <el-option label="服饰鞋包" value="服饰鞋包" />
            <el-option label="运动器材" value="运动器材" />
            <el-option label="美妆护肤" value="美妆护肤" />
            <el-option label="数码配件" value="数码配件" />
            <el-option label="其他" value="其他" />
          </el-select>
        </div>
        <div class="filter-row">
          <span class="filter-label">价格：</span>
          <el-input-number v-model="filter.minPrice" :min="0" placeholder="最低价" :controls="false" style="width: 100px" @change="handleSearch" />
          <span class="price-separator">-</span>
          <el-input-number v-model="filter.maxPrice" :min="0" placeholder="最高价" :controls="false" style="width: 100px" @change="handleSearch" />
        </div>
        <div class="filter-row">
          <span class="filter-label">排序：</span>
          <el-radio-group v-model="filter.sort" @change="handleSearch">
            <el-radio-button label="newest">最新发布</el-radio-button>
            <el-radio-button label="price_asc">价格从低到高</el-radio-button>
            <el-radio-button label="price_desc">价格从高到低</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-content">
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>
      <div v-else-if="products.length === 0" class="empty-state">
        <span class="empty-icon">🔍</span>
        <p>暂无相关商品</p>
        <el-button type="primary" @click="resetFilter">清除筛选</el-button>
      </div>
      <div v-else class="product-grid">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click="goDetail(product.id)"
        >
          <div class="card-image">
            <img :src="getProductImage(product)" :alt="product.name" />
            <span v-if="product.originalPrice" class="discount-badge">
              {{ Math.round((1 - product.price / product.originalPrice) * 100) }}折
            </span>
            <!-- 悬停操作按钮 -->
            <div class="card-actions">
              <el-tooltip content="加入购物车" placement="top">
                <el-button circle class="action-btn" @click.stop="addToCart(product)">
                  <el-icon><ShoppingCart /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="收藏" placement="top">
                <el-button circle class="action-btn" @click.stop="addToFavorites(product)">
                  <el-icon><Star /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </div>
          <div class="card-content">
            <h3 class="product-name">{{ product.name }}</h3>
            <div class="product-price-row">
              <span class="price-current">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="price-original">¥{{ product.originalPrice }}</span>
            </div>
            <div class="product-meta">
              <span class="product-category">{{ product.category }}</span>
              <span class="product-seller">{{ product.sellerName }}</span>
            </div>
            <p class="product-desc">{{ product.description }}</p>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="products.length > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Star, Search } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { getProductImage } from '../utils/image'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const filterBar = ref(null)

const loading = ref(false)
const products = ref([])

const filter = reactive({
  keyword: '',
  category: '',
  minPrice: null,
  maxPrice: null,
  sort: 'newest'
})

const pagination = reactive({
  page: 1,
  size: 12,
  total: 0
})

const goDetail = (id) => router.push(`/product/${id}`)

const resetFilter = () => {
  filter.category = ''
  filter.minPrice = null
  filter.maxPrice = null
  filter.sort = 'newest'
  pagination.page = 1
  handleSearch()
}

const handleSearch = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams()
    if (filter.keyword) params.append('name', filter.keyword)
    if (filter.category) params.append('categories', filter.category)
    if (filter.minPrice !== null) params.append('minPrice', filter.minPrice)
    if (filter.maxPrice !== null) params.append('maxPrice', filter.maxPrice)
    
    // 排序参数转换
    let sortBy = 'createTime'
    let sortOrder = 'desc'
    if (filter.sort === 'price_asc') {
      sortBy = 'price'
      sortOrder = 'asc'
    } else if (filter.sort === 'price_desc') {
      sortBy = 'price'
      sortOrder = 'desc'
    }
    params.append('sortBy', sortBy)
    params.append('sortOrder', sortOrder)
    params.append('page', pagination.page)
    params.append('size', pagination.size)

    const res = await fetch(`/api/product/search?${params.toString()}`)
    const data = await res.json()
    if (data.code === 200) {
      products.value = data.data.records || []
      pagination.total = data.data.total || 0
    }
  } catch (e) {
    console.error('搜索失败', e)
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 添加到购物车
const addToCart = async (product) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // 不能添加自己的商品到购物车
  if (product.sellerId === userStore.userInfo?.id) {
    ElMessage.warning('不能购买自己的商品')
    return
  }
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`/api/cart/add/${product.id}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    const data = await res.json()
    if (data.code === 200) {
      ElMessage.success('已加入购物车')
    } else {
      ElMessage.error(data.message || '添加失败')
    }
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

// 添加到收藏
const addToFavorites = async (product) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`/api/favorite/add/${product.id}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    const data = await res.json()
    if (data.code === 200) {
      ElMessage.success('已收藏')
    } else {
      ElMessage.error(data.message || '收藏失败')
    }
  } catch (e) {
    ElMessage.error('收藏失败')
  }
}

watch(() => route.query, (query) => {
  filter.keyword = query.keyword || ''
  filter.category = query.category || ''
  pagination.page = 1
  handleSearch()
}, { immediate: true })

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.search-page {
  background: #f5f5f5;
  min-height: calc(100vh - 60px);
}

.filter-bar {
  background: #fff;
  border-bottom: 1px solid #eee;
  padding: 12px 40px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 搜索结果区域 - 确保不被固定的筛选栏遮挡 */
.search-content {
  padding-top: 24px;
}

.filter-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 32px;
  flex-wrap: wrap;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
  font-weight: 500;
}

.price-separator {
  color: #999;
  margin: 0 4px;
}

.search-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 20px;
}

.loading-state {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  background: #fff;
  border-radius: 12px;
}

.empty-icon {
  font-size: 64px;
  display: block;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  color: #999;
  margin-bottom: 24px;
}

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

.discount-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #ff4757;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

/* 悬停操作按钮 */
.card-actions {
  position: absolute;
  bottom: 8px;
  right: 8px;
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.product-card:hover .card-actions {
  opacity: 1;
}

.action-btn {
  background: rgba(255, 255, 255, 0.95) !important;
  border: none !important;
  color: #FF6A00 !important;
  width: 36px;
  height: 36px;
}

.action-btn:hover {
  background: #FF6A00 !important;
  color: #fff !important;
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
  margin-bottom: 8px;
}

.product-category {
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
}

.product-desc {
  font-size: 12px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
}

.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .filter-bar {
    padding: 12px 20px;
    top: 60px; /* 移动端只有导航栏 */
  }

  .filter-content {
    gap: 16px;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}
</style>
