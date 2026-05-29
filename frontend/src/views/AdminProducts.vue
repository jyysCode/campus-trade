<template>
  <div>
    <h2>商品管理</h2>
    <div style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center;">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部商品" name="all" />
        <el-tab-pane label="待审核" name="pending" />
      </el-tabs>
      <el-button type="primary" @click="handleAdd">+ 添加商品</el-button>
    </div>
    <el-table :data="products" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="70" />
      <el-table-column prop="sellerName" label="卖家" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="{ 0: 'warning', 1: 'success', 2: 'danger' }[row.status]">
            {{ { 0: '待审核', 1: '已通过', 2: '已拒绝' }[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" text @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 0" type="success" size="small" @click="handleApprove(row.id)">通过</el-button>
          <el-button v-if="row.status === 0" type="danger" size="small" @click="handleReject(row.id)">拒绝</el-button>
          <el-button type="danger" size="small" text @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-if="total > 0" v-model:current-page="page" :page-size="10" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top:16px" />

    <!-- 添加/编辑商品对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '添加商品'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" :precision="0" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="商品图片">
          <el-input v-model="form.image" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="状态" v-if="isEdit">
          <el-select v-model="form.status">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProductList, getPendingProducts, approveProduct, rejectProduct, deleteProduct, addProduct, updateProduct, getCategories } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('all')
const products = ref([])
const page = ref(1)
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  name: '',
  category: '',
  price: 0,
  originalPrice: 0,
  stock: 1,
  description: '',
  image: '',
  status: 1
})
const categories = ref(['电子产品', '图书教材', '生活用品', '服饰鞋包', '运动器材', '美妆护肤', '数码配件', '其他'])

const loadData = async () => {
  loading.value = true
  try {
    const fn = activeTab.value === 'pending' ? getPendingProducts : getProductList
    const res = await fn({ page: page.value, size: 10 })
    products.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } catch (e) {
    console.error('加载商品列表失败', e)
    ElMessage.error('加载商品列表失败')
  }
  loading.value = false
}

const handleTabChange = () => {
  page.value = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    name: '',
    category: '',
    price: 0,
    originalPrice: 0,
    stock: 1,
    description: '',
    image: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const saveProduct = async () => {
  try {
    if (isEdit.value) {
      await updateProduct(form.value)
      ElMessage.success('更新成功')
    } else {
      await addProduct(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error('保存失败', e)
    ElMessage.error('保存失败')
  }
}

const handleApprove = async (id) => {
  try {
    await approveProduct(id)
    ElMessage.success('已通过')
    loadData()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async (id) => {
  try {
    await rejectProduct(id)
    ElMessage.success('已拒绝')
    loadData()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该商品？', '提示')
    await deleteProduct(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败', e)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
h2 { margin-bottom: 16px; }
</style>
