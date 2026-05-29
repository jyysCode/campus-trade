<template>
  <div class="search-page">
    <div class="search-header">
      <el-input v-model="keyword" placeholder="搜索商品..." size="large" @keyup.enter="doSearch">
        <template #append>
          <el-button type="primary" @click="doSearch"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>
    <div class="filter-bar">
      <div class="filter-categories" v-if="categories.length">
        <el-tag v-for="cat in categories" :key="cat" :type="selectedCategories.includes(cat) ? 'primary' : 'info'" class="filter-tag" @click="toggleCategory(cat)" effect="plain">{{ cat }}</el-tag>
      </div>
      <div class="filter-sort">
        <el-radio-group v-model="sortBy" size="small" @change="doSearch">
          <el-radio-button value="createTime">最新</el-radio-button>
          <el-radio-button value="price_asc">价格↑</el-radio-button>
          <el-radio-button value="price_desc">价格↓</el-radio-button>
          <el-radio-button value="sales">销量</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    <div class="product-grid" v-loading="loading">
      <ProductCard v-for="p in products" :key="p.id" :product="p" />
    </div>
    <el-empty v-if="!loading && products.length === 0" description="未找到相关商品" />
    <el-pagination v-if="total > 0" v-model:current-page="page" :page-size="12" :total="total" layout="prev, pager, next" @current-change="doSearch" style="margin-top:20px;text-align:center" />
    <FooterBar />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { searchProducts, getCategories } from '../api'
import ProductCard from '../components/ProductCard.vue'
import FooterBar from '../components/FooterBar.vue'
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const keyword = ref(route.query.name || '')
const products = ref([])
const categories = ref([])
const selectedCategories = ref([])
const sortBy = ref('createTime')
const page = ref(1)
const total = ref(0)
const loading = ref(false)

const loadCategories = async () => {
  const res = await getCategories()
  categories.value = res.data.data || []
}

const toggleCategory = (cat) => {
  const idx = selectedCategories.value.indexOf(cat)
  if (idx > -1) selectedCategories.value.splice(idx, 1)
  else selectedCategories.value.push(cat)
  page.value = 1
  doSearch()
}

const doSearch = async () => {
  loading.value = true
  try {
    let sortField = sortBy.value
    let sortOrder = 'desc'
    if (sortField === 'price_asc') { sortField = 'price'; sortOrder = 'asc' }
    else if (sortField === 'price_desc') { sortField = 'price'; sortOrder = 'desc' }

    const params = { name: keyword.value || undefined, categories: selectedCategories.value.length ? selectedCategories.value.join(',') : undefined, sortBy: sortField, sortOrder, page: page.value, size: 12 }
    const res = await searchProducts(params)
    products.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

onMounted(() => { loadCategories(); doSearch() })
</script>

<style scoped>
.search-page { max-width: 1200px; margin: 0 auto; padding: 20px; }
.search-header { max-width: 600px; margin: 0 auto 20px; }
.filter-bar { margin-bottom: 20px; }
.filter-categories { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 12px; }
.filter-tag { cursor: pointer; }
.filter-sort { display: flex; justify-content: flex-end; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
</style>
