<template>
  <div class="product-card" @click="$router.push(`/product/${product.id}`)">
    <div class="card-img">
      <img :src="product.image || 'https://via.placeholder.com/300x300?text=No+Image'" :alt="product.name" />
      <span v-if="product.originalPrice && product.originalPrice > product.price" class="discount-tag">
        {{ Math.round((1 - product.price / product.originalPrice) * 100) }}%OFF
      </span>
    </div>
    <div class="card-info">
      <h3 class="card-title">{{ product.name }}</h3>
      <div class="card-price">
        <span class="price-now">¥{{ product.price }}</span>
        <span v-if="product.originalPrice" class="price-old">¥{{ product.originalPrice }}</span>
      </div>
      <div class="card-meta">
        <span class="card-sales" v-if="product.sales">已售{{ product.sales }}</span>
        <span class="card-seller">{{ product.sellerName }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ product: { type: Object, required: true } })
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}
.card-img {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
}
.card-img img {
  position: absolute;
  top: 0; left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.product-card:hover .card-img img { transform: scale(1.05); }
.discount-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #ff4757;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.card-info { padding: 12px; }
.card-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8px;
}
.card-price { display: flex; align-items: baseline; gap: 6px; }
.price-now { color: #ff4757; font-size: 18px; font-weight: bold; }
.price-old { color: #999; font-size: 12px; text-decoration: line-through; }
.card-meta {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}
</style>
