<template>
  <div class="fav-page">
    <h1>⭐ 我的收藏</h1>
    <div class="product-grid" v-loading="loading">
      <ProductCard v-for="item in favorites" :key="item.id" :product="item" />
    </div>
    <el-empty v-if="!loading && favorites.length === 0" description="暂无收藏">
      <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
    </el-empty>
    <el-pagination v-if="total > 0" v-model:current-page="page" :page-size="12" :total="total" layout="prev, pager, next" @current-change="loadFavorites" style="margin-top:20px;text-align:center" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFavoriteList } from '../api'
import ProductCard from '../components/ProductCard.vue'

const favorites = ref([])
const page = ref(1)
const total = ref(0)
const loading = ref(false)

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavoriteList({ page: page.value, size: 12 })
    favorites.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } catch (e) {}
  loading.value = false
}

onMounted(loadFavorites)
</script>

<style scoped>
.fav-page { max-width: 1200px; margin: 0 auto; padding: 20px; }
.fav-page h1 { margin-bottom: 20px; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
</style>
