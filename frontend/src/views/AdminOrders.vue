<template>
  <div>
    <h2>订单管理</h2>
    <el-table :data="orders" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="productName" label="商品" />
      <el-table-column prop="buyerName" label="买家" width="100" />
      <el-table-column prop="sellerName" label="卖家" width="100" />
      <el-table-column prop="totalAmount" label="金额" width="100">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="{ 0: 'warning', 1: 'primary', 2: '', 3: 'success', 4: 'danger', 5: 'info' }[row.status]" size="small">
            {{ { 0: '待付款', 1: '已付款', 2: '已发货', 3: '已完成', 4: '退款中', 5: '已取消' }[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" text @click="handleEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-if="total > 0" v-model:current-page="page" :page-size="10" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top:16px" />

    <!-- 编辑订单对话框 -->
    <el-dialog v-model="dialogVisible" title="编辑订单状态" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="订单号">
          <el-input v-model="form.orderNo" disabled />
        </el-form-item>
        <el-form-item label="商品">
          <el-input v-model="form.productName" disabled />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="form.status">
            <el-option label="待付款" :value="0" />
            <el-option label="已付款" :value="1" />
            <el-option label="已发货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="退款中" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveOrder">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllOrders, adminUpdateOrder } from '../api'
import { ElMessage } from 'element-plus'

const orders = ref([])
const page = ref(1)
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({
  id: null,
  orderNo: '',
  productName: '',
  status: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAllOrders({ page: page.value, size: 10 })
    orders.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } catch (e) {
    console.error('加载订单列表失败', e)
    ElMessage.error('加载订单列表失败')
  }
  loading.value = false
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const saveOrder = async () => {
  try {
    await adminUpdateOrder(form.value.id, { status: form.value.status })
    ElMessage.success('更新成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error('更新失败', e)
    ElMessage.error('更新失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
h2 { margin-bottom: 16px; }
</style>
