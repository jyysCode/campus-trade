<template>
  <div class="home-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchKeyword" placeholder="搜索你想要的宝贝..." size="large" @keyup.enter="doSearch">
        <template #append>
          <el-button type="primary" @click="doSearch"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>

    <!-- 分类导航 -->
    <div class="category-section" v-if="categories.length">
      <div class="section-title">热门分类</div>
      <div class="category-list">
        <el-tag v-for="cat in categories" :key="cat" class="category-tag" @click="searchByCategory(cat)" effect="plain">{{ cat }}</el-tag>
      </div>
    </div>

    <!-- 热门商品 -->
    <div class="section">
      <div class="section-title">🔥 热门推荐</div>
      <div class="product-grid" v-loading="loading">
        <ProductCard v-for="p in hotProducts" :key="p.id" :product="p" />
      </div>
      <el-empty v-if="!loading && hotProducts.length === 0" description="暂无商品" />
    </div>

    <FooterBar />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotProducts, getCategories } from '../api'
import ProductCard from '../components/ProductCard.vue'
import FooterBar from '../components/FooterBar.vue'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const searchKeyword = ref('')
const hotProducts = ref([])
const categories = ref([])
const loading = ref(true)

const loadData = async () => {
  try {
    const [hotRes, catRes] = await Promise.all([getHotProducts(), getCategories()])
    hotProducts.value = hotRes.data.data || []
    categories.value = catRes.data.data || []
  } catch (e) { /* ignore */ }
  loading.value = false
}

const doSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { name: searchKeyword.value.trim() } })
  }
}

const searchByCategory = (cat) => {
  router.push({ path: '/search', query: { categories: cat } })
}

onMounted(loadData)
</script>

<style scoped>
.home-page { max-width: 1200px; margin: 0 auto; padding: 20px; }
.search-bar { max-width: 600px; margin: 20px auto; }
.category-section { margin: 20px 0; }
.section-title { font-size: 18px; font-weight: 600; margin-bottom: 16px; color: #333; }
.category-list { display: flex; flex-wrap: wrap; gap: 8px; }
.category-tag { cursor: pointer; font-size: 14px; }
.category-tag:hover { color: #409eff; border-color: #409eff; }
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
</style>
